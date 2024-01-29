package com.hy.project.demo.monitor.facade.model.kafka;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2024/01/29
 */
public class KafkaNode extends ToString {
    private static final long serialVersionUID = 6967989547087266100L;

    private String id;
    private String host;
    private String port;

    public KafkaNode(String id, String host, String port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
