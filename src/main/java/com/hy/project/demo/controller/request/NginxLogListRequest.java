package com.hy.project.demo.controller.request;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxLogListRequest extends ToString {

    private static final long serialVersionUID = -3075477661553818065L;

    String gmtBegin;

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
