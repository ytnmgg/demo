package com.hy.project.demo.mybatis.entity;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxAccessLogStatusCountDO {

    private String status;
    private Long count;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
