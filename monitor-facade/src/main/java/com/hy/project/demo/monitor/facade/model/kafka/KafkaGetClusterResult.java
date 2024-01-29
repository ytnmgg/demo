package com.hy.project.demo.monitor.facade.model.kafka;

import java.util.List;

import com.hy.project.demo.common.model.BaseResult;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class KafkaGetClusterResult extends BaseResult {
    private static final long serialVersionUID = 3080479576064147032L;

    private List<KafkaNode> nodes;
    private KafkaNode controller;
    private String clusterId;

    public List<KafkaNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<KafkaNode> nodes) {
        this.nodes = nodes;
    }

    public KafkaNode getController() {
        return controller;
    }

    public void setController(KafkaNode controller) {
        this.controller = controller;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

}
