package com.hy.project.demo.monitor.facade.model.es;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2023/02/23
 */
public class EsSearchRequest extends ToString {
    private static final long serialVersionUID = -1147104810797534012L;

    @Min(2)
    @Max(100)
    int pageSize;

    @Min(1)
    int pageIndex;

    @NotBlank
    private String from;

    @NotBlank
    private String to;

    // TODO @RICK 先单个，后面支持多个
    private String wildcardField;
    private String wildcardValue;

    private String after;

    private String query;

    private String fieldKey;

    private String timestampKey;

    private String logType;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getWildcardField() {
        return wildcardField;
    }

    public void setWildcardField(String wildcardField) {
        this.wildcardField = wildcardField;
    }

    public String getWildcardValue() {
        return wildcardValue;
    }

    public void setWildcardValue(String wildcardValue) {
        this.wildcardValue = wildcardValue;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getTimestampKey() {
        return timestampKey;
    }

    public void setTimestampKey(String timestampKey) {
        this.timestampKey = timestampKey;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }
}
