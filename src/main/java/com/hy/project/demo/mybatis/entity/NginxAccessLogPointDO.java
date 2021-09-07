package com.hy.project.demo.mybatis.entity;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxAccessLogPointDO {

    private String time;
    private Integer count;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
