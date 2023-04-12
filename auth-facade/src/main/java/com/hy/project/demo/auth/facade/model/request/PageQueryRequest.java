package com.hy.project.demo.auth.facade.model.request;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2023/04/04
 */
public class PageQueryRequest extends ToString {
    private static final long serialVersionUID = -3630646877729155395L;

    private int pageIndex;
    private int pageSize;

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
}
