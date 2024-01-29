package com.hy.project.demo.monitor.facade.model.kafka;

import java.util.ArrayList;
import java.util.List;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2024/01/29
 */
public class KafkaTopicPartition extends ToString {
    private static final long serialVersionUID = -7851765808728703129L;

    private String partition;
    private KafkaNode leader;
    private List<KafkaNode> replicas = new ArrayList<>();
    private List<KafkaNode> isr = new ArrayList<>();

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public KafkaNode getLeader() {
        return leader;
    }

    public void setLeader(KafkaNode leader) {
        this.leader = leader;
    }

    public List<KafkaNode> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<KafkaNode> replicas) {
        this.replicas = replicas;
    }

    public List<KafkaNode> getIsr() {
        return isr;
    }

    public void setIsr(List<KafkaNode> isr) {
        this.isr = isr;
    }
}
