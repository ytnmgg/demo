package com.hy.project.demo.web.model;

import javax.validation.constraints.NotBlank;

import com.hy.project.demo.common.model.PageRequest;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxLogListRequest extends PageRequest {
    private static final long serialVersionUID = -3075477661553818065L;

    @NotBlank(message = "开始时间不能为空")
    String gmtBegin;

    @NotBlank(message = "结束时间不能为空")
    String gmtEnd;

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
}
