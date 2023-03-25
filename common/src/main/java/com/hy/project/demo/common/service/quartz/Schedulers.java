package com.hy.project.demo.common.service.quartz;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rick.wl
 * @date 2022/11/23
 */
public enum Schedulers {
    /**
     * 默认scheduler
     */
    DEFAULT_SCHEDULER("DEFAULT_SCHEDULER"),

    ;

    private String name;

    public static List<String> getNames() {
        List<String> result = new ArrayList<>();
        for (Schedulers scheduler : values()) {
            result.add(scheduler.getName());
        }
        return result;
    }

    Schedulers(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
