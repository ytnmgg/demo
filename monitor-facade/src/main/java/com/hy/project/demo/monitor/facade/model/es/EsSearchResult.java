package com.hy.project.demo.monitor.facade.model.es;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2023/02/25
 */
public class EsSearchResult<T> extends ToString {
    private static final long serialVersionUID = 1900850056762257920L;

    private long total;
    private T data;
    private String nextAfter;

    public static <T> EsSearchResult<T> of(long total, T data, String nextAfter) {
        EsSearchResult<T> result = new EsSearchResult<>();
        result.setTotal(total);
        result.setData(data);
        result.setNextAfter(nextAfter);
        return result;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getNextAfter() {
        return nextAfter;
    }

    public void setNextAfter(String nextAfter) {
        this.nextAfter = nextAfter;
    }
}
