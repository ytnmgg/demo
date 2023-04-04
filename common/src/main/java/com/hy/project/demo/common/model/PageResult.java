package com.hy.project.demo.common.model;

/**
 * @author rick.wl
 * @date 2021/09/02
 */
public class PageResult<T> extends BaseResult {

    private static final long serialVersionUID = -7860756718679386155L;

    private int pageIndex;
    private int pageSize;
    private long totalCount;
    private T data;

    public static <T> PageResult<T> of(T data) {
        return of(data, 0, 0, 0);
    }

    public static <T> PageResult<T> of(T data, int pageIndex, int pageSize, long totalCount) {
        PageResult<T> result = new PageResult<>();
        result.setData(data);
        result.setPageIndex(pageIndex);
        result.setPageSize(pageSize);
        result.setTotalCount(totalCount);
        return result;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
