package com.hy.project.demo.model.nginx;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxAccessLogPointModel extends ToString {
    private static final long serialVersionUID = 688798532117809949L;

    private String time;
    private int count;
    private String status;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
