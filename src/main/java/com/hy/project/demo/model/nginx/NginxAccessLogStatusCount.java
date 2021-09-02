package com.hy.project.demo.model.nginx;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxAccessLogStatusCount extends ToString {

    private static final long serialVersionUID = -677438758449607617L;

    private NginxAccessLogStatusCountModel today = new NginxAccessLogStatusCountModel();

    private NginxAccessLogStatusCountModel yesterday = new NginxAccessLogStatusCountModel();

    public NginxAccessLogStatusCountModel getToday() {
        return today;
    }

    public void setToday(NginxAccessLogStatusCountModel today) {
        this.today = today;
    }

    public NginxAccessLogStatusCountModel getYesterday() {
        return yesterday;
    }

    public void setYesterday(NginxAccessLogStatusCountModel yesterday) {
        this.yesterday = yesterday;
    }
}
