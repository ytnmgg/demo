package com.hy.project.demo.monitor.facade.model.kafka;

import javax.validation.constraints.NotBlank;

import com.hy.project.demo.common.model.BaseRequest;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class KafkaTopicRequest extends BaseRequest {
    private static final long serialVersionUID = 1064097662028798853L;

    @NotBlank
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

}
