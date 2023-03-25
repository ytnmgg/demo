package com.hy.project.demo.monitor.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLDoubleExpr;
import com.alibaba.druid.sql.ast.expr.SQLFloatExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.json.JsonData;
import com.hy.project.demo.common.exception.DemoException;
import com.hy.project.demo.common.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.UNEXPECTED;

/**
 * @author rick.wl
 * @date 2023/03/15
 */
@Service
public class EsAstVisitor extends SQLASTVisitorAdapter {
    public static final String ES_QUERY = "es.query";
    public static final String ES_VALUE = "es.value";

    @Override
    public boolean visit(SQLBinaryOpExpr expr) {
        SQLExpr left = expr.getLeft();
        SQLExpr right = expr.getRight();

        left.accept(this);
        right.accept(this);

        Query query;

        switch (expr.getOperator()) {
            case Equality:
                query = doEquality(left, right);
                break;
            case NotEqual:
                query = doNotEqual(left, right);
                break;
            case GreaterThan:
            case GreaterThanOrEqual:
            case LessThan:
            case LessThanOrEqual:
                query = doRange(left, right, expr.getOperator());
                break;
            case BooleanAnd:
            case BooleanOr:
                query = doLogic(left, right, expr.getOperator());
                break;
            default:
                throw new DemoException(INVALID_PARAM_EXCEPTION, "不符合要求的操作符");
        }

        expr.putAttribute(ES_QUERY, query);
        return false;
    }

    private Query doEquality(SQLExpr left, SQLExpr right) {
        Object leftValue = left.getAttribute(ES_VALUE);
        Object rightValue = right.getAttribute(ES_VALUE);
        AssertUtil.isTrue(leftValue instanceof FieldValue, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(left));
        AssertUtil.isTrue(rightValue instanceof FieldValue, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(right));

        String filed = ((FieldValue)leftValue).stringValue();
        FieldValue value = (FieldValue)rightValue;
        return TermQuery.of(b -> b.field(filed).value(value))._toQuery();
    }

    private Query doNotEqual(SQLExpr left, SQLExpr right) {
        Object leftValue = left.getAttribute(ES_VALUE);
        Object rightValue = right.getAttribute(ES_VALUE);
        AssertUtil.isTrue(leftValue instanceof FieldValue, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(left));
        AssertUtil.isTrue(rightValue instanceof FieldValue, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(right));

        String filed = ((FieldValue)leftValue).stringValue();
        FieldValue value = (FieldValue)rightValue;
        Query term = TermQuery.of(b -> b.field(filed).value(value))._toQuery();
        return BoolQuery.of(b -> b.mustNot(term))._toQuery();
    }

    private Query doRange(SQLExpr left, SQLExpr right, SQLBinaryOperator rangeOp) {
        Object leftValue = left.getAttribute(ES_VALUE);
        Object rightValue = right.getAttribute(ES_VALUE);
        AssertUtil.isTrue(leftValue instanceof FieldValue, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(left));
        AssertUtil.isTrue(rightValue instanceof FieldValue, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(right));

        String filed = ((FieldValue)leftValue).stringValue();

        FieldValue rightField = (FieldValue)rightValue;
        JsonData value;
        if (rightField.isString()) {
            value = JsonData.of(rightField.stringValue());
        } else if (rightField.isDouble()) {
            value = JsonData.of(rightField.doubleValue());
        } else if (rightField.isLong()) {
            value = JsonData.of(rightField.longValue());
        } else {
            throw new DemoException(UNEXPECTED, "不符合要求的类型: " + rightField);
        }

        switch (rangeOp) {
            case GreaterThan:
                return RangeQuery.of(b -> b.field(filed).gt(value))._toQuery();
            case GreaterThanOrEqual:
                return RangeQuery.of(b -> b.field(filed).gte(value))._toQuery();
            case LessThan:
                return RangeQuery.of(b -> b.field(filed).lt(value))._toQuery();
            case LessThanOrEqual:
                return RangeQuery.of(b -> b.field(filed).lte(value))._toQuery();
            default:
                throw new DemoException(UNEXPECTED, "不符合要求的操作符: " + rangeOp.getName());
        }
    }

    private Query doLogic(SQLExpr left, SQLExpr right, SQLBinaryOperator logicOp) {
        Object leftValue = left.getAttribute(ES_QUERY);
        Object rightValue = right.getAttributes().get(ES_QUERY);

        AssertUtil.isTrue(leftValue instanceof Query, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(left));
        AssertUtil.isTrue(rightValue instanceof Query, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(right));

        List<Query> queries = new ArrayList<>();
        queries.add((Query)leftValue);
        queries.add((Query)rightValue);

        switch (logicOp) {
            case BooleanAnd:
                return BoolQuery.of(b -> b.filter(queries))._toQuery();
            case BooleanOr:
                return BoolQuery.of(b -> b.should(queries).minimumShouldMatch("1"))._toQuery();
            default:
                throw new DemoException(UNEXPECTED, "不符合要求的操作符: " + logicOp.getName());
        }
    }

    @Override
    public boolean visit(SQLUnaryExpr expr) {
        if (expr.getOperator() != SQLUnaryOperator.Not) {
            throw new DemoException(UNEXPECTED, "不符合要求的操作符: " + expr.getOperator());
        }

        SQLExpr exp = expr.getExpr();
        exp.accept(this);

        Object expQuery = exp.getAttribute(ES_QUERY);
        AssertUtil.isTrue(expQuery instanceof Query, UNEXPECTED, "值类型异常: %s", SQLUtils.toSQLString(exp));

        Query query = BoolQuery.of(b -> b.mustNot((Query)expQuery))._toQuery();

        expr.putAttribute(ES_QUERY, query);
        return false;
    }

    @Override
    public boolean visit(SQLIdentifierExpr expr) {
        String exprStr = expr.toString();
        exprStr = StringUtils.strip(exprStr, "'");
        exprStr = StringUtils.strip(exprStr, "\"");
        expr.putAttribute(ES_VALUE, FieldValue.of(exprStr));
        return false;
    }

    @Override
    public boolean visit(SQLPropertyExpr expr) {
        String exprStr = expr.toString();
        exprStr = StringUtils.strip(exprStr, "'");
        exprStr = StringUtils.strip(exprStr, "\"");
        expr.putAttribute(ES_VALUE, FieldValue.of(exprStr));
        return false;
    }

    @Override
    public boolean visit(SQLCharExpr expr) {
        String exprStr = expr.toString();
        exprStr = StringUtils.strip(exprStr, "'");
        exprStr = StringUtils.strip(exprStr, "\"");
        expr.putAttribute(ES_VALUE, FieldValue.of(exprStr));
        return false;
    }

    @Override
    public boolean visit(SQLIntegerExpr expr) {
        expr.putAttribute(ES_VALUE, FieldValue.of(expr.getNumber().intValue()));
        return false;
    }

    @Override
    public boolean visit(SQLNumberExpr expr) {
        expr.putAttribute(ES_VALUE, FieldValue.of(expr.getNumber().longValue()));
        return false;
    }

    @Override
    public boolean visit(SQLDoubleExpr expr) {
        expr.putAttribute(ES_VALUE, FieldValue.of(expr.getNumber()));
        return false;
    }

    @Override
    public boolean visit(SQLFloatExpr expr) {
        expr.putAttribute(ES_VALUE, FieldValue.of(expr.getNumber()));
        return false;
    }

    @Override
    public boolean visit(SQLBooleanExpr expr) {
        expr.putAttribute(ES_VALUE, FieldValue.of(expr.getBooleanValue()));
        return false;
    }
}
