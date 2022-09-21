package com.hy.project.demo.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class PageRequest extends ToString {
    private static final long serialVersionUID = -3325526594945168592L;

    @Min(2)
    @Max(100)
    int pageSize;

    @Min(1)
    int pageIndex;

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
}
