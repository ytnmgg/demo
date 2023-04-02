package com.hy.project.demo.common.service.quartz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author rick.wl
 * @date 2022/11/23
 */
public enum Schedulers {
    ///**
    // * 默认scheduler
    // */
    //DEFAULT_SCHEDULER("DEFAULT_SCHEDULER"),

    /**
     * auth core scheduler
     */
    AUTH_CORE_SCHEDULER("AUTH_CORE_SCHEDULER"),

    ;

    private String name;

    public static List<String> getNames() {
        List<String> result = new ArrayList<>();
        for (Schedulers scheduler : values()) {
            result.add(scheduler.getName());
        }
        return result;
    }

    public static Schedulers getByName(String name) {
        for (Schedulers item : values()) {
            if (StringUtils.equals(item.toString(), name)) {
                return item;
            }
        }
        return null;
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
