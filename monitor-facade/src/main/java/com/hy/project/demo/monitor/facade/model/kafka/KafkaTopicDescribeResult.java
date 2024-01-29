package com.hy.project.demo.monitor.facade.model.kafka;

import java.util.ArrayList;
import java.util.List;

import com.hy.project.demo.common.model.BaseResult;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class KafkaTopicDescribeResult extends BaseResult {

    private static final long serialVersionUID = 3171564268840396155L;

    private String name;
    private String uuid;

    private List<KafkaTopicPartition> partitions = new ArrayList<>();

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

    public List<KafkaTopicPartition> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<KafkaTopicPartition> partitions) {
        this.partitions = partitions;
    }
}
