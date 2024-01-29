package com.hy.project.demo.monitor.facade.service;

import java.util.List;

import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaGetClusterResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopic;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicCreateRequest;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicDescribeResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicRequest;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
public interface KafkaService {

    /**
     * 查询metric
     *
     * @param request request
     * @return 结果
     */
    RpcResult<KafkaGetClusterResult> getCluster(RpcRequest<Void> request) throws Throwable;

    /**
     * 创建Topic
     *
     * @param request 请求
     * @return 结果
     * @throws Throwable 异常
     */
    RpcResult<Void> createTopic(RpcRequest<KafkaTopicCreateRequest> request) throws Throwable;

    /**
     * 分页列表 topic
     *
     * @param request 请求
     * @return 结果
     * @throws Throwable 异常
     */
    RpcResult<PageResult<List<KafkaTopic>>> listTopic(RpcRequest<PageRequest> request) throws Throwable;

    /**
     * 查询Topic详情
     *
     * @param request 请求
     * @return 结果
     * @throws Throwable 异常
     */
    RpcResult<KafkaTopicDescribeResult> getTopic(RpcRequest<KafkaTopicRequest> request) throws Throwable;

    /**
     * 删除Topic
     *
     * @param request 请求
     * @return 结果
     * @throws Throwable 异常
     */
    RpcResult<Void> deleteTopic(RpcRequest<KafkaTopicRequest> request) throws Throwable;
}
