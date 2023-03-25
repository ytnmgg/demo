package com.hy.project.demo.auth.core.mybatis.entity;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
public class SequenceDO {

    private String name;
    private Long currentValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
    }
}
