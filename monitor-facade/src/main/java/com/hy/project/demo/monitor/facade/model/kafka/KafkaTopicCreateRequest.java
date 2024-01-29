package com.hy.project.demo.monitor.facade.model.kafka;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class KafkaTopicCreateRequest extends KafkaTopicRequest {
    private static final long serialVersionUID = -3443854710162320551L;

    private Integer partitions;

    private Short replicationFactor;

    public Integer getPartitions() {
        return partitions;
    }

    public void setPartitions(Integer partitions) {
        this.partitions = partitions;
    }

    public Short getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(Short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
}
