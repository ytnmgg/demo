package com.hy.project.demo.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxLogListRequest extends ToString {

    private static final long serialVersionUID = -3075477661553818065L;

    @NotBlank(message = "开始时间不能为空")
    String gmtBegin;

    @NotBlank(message = "结束时间不能为空")
    String gmtEnd;

    @Min(10)
    @Max(100)
    int pageSize;

    @Min(1)
    int pageIndex;

    public String getGmtBegin() {
        return gmtBegin;
    }

    public void setGmtBegin(String gmtBegin) {
        this.gmtBegin = gmtBegin;
    }

    public String getGmtEnd() {
        return gmtEnd;
    }

    public void setGmtEnd(String gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

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
