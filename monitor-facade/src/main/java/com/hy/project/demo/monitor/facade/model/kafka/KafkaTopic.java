package com.hy.project.demo.monitor.facade.model.kafka;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2024/01/29
 */
public class KafkaTopic extends ToString {
    private static final long serialVersionUID = -6613892785671751645L;

    private String name;
    private String uuid;

    public KafkaTopic(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
