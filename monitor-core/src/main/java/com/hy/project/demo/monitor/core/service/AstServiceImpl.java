package com.hy.project.demo.monitor.core.service;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.util.JdbcConstants;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.hy.project.demo.monitor.core.service.es.EsAstVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.monitor.core.service.es.EsAstVisitor.ES_QUERY;

/**
 * https://github.com/alibaba/druid/wiki/Druid_SQL_AST
 *
 * @author rick.wl
 * @date 2023/03/10
 */
@Service
public class AstServiceImpl implements AstService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AstServiceImpl.class);

    @Autowired
    EsAstVisitor esAstVisitor;

    @Override
    public Query buildEsQuery(String sql) {
        SQLExpr expr = SQLUtils.toSQLExpr(sql, JdbcConstants.MYSQL);
        expr.accept(esAstVisitor);
        Query result = (Query)expr.getAttribute(ES_QUERY);
        LOGGER.info("query from ast: {}", result.toString());
        return result;
    }
}
