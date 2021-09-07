package com.hy.project.demo.controller.request;

import javax.validation.constraints.NotBlank;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/09/07
 */
public class NginxLogListPointsRequest extends ToString {
    private static final long serialVersionUID = -7932872765459036655L;

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
