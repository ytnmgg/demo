package com.hy.project.demo.web.controller.monitor;

import java.util.List;

import javax.validation.Valid;

import com.hy.project.demo.common.model.AjaxResult;
import com.hy.project.demo.common.model.BaseRequest;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaGetClusterResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopic;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicCreateRequest;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicDescribeResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicRequest;
import com.hy.project.demo.monitor.facade.service.KafkaService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hy.project.demo.web.util.WebUtil.fromRpcResult;

/**
 * @author rick.wl
 * @date 2021/11/30
 */
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @DubboReference
    KafkaService kafkaService;

    @GetMapping("/get_cluster.json")
    public AjaxResult getCluster(@Valid BaseRequest request) throws Throwable {
        RpcResult<KafkaGetClusterResult> result = kafkaService.getCluster(RpcRequest.of(null));
        return fromRpcResult(result);
    }

    @PostMapping("/create_topic.json")
    public AjaxResult createTopic(@RequestBody @Valid KafkaTopicCreateRequest request) throws Throwable {
        RpcResult<Void> result = kafkaService.createTopic(RpcRequest.of(request));
        return fromRpcResult(result);
    }

    @GetMapping("/get_topic.json")
    public AjaxResult getTopic(@Valid KafkaTopicRequest request) throws Throwable {
        RpcResult<KafkaTopicDescribeResult> result = kafkaService.getTopic(RpcRequest.of(request));
        return fromRpcResult(result);
    }

    @GetMapping("/list_topic.json")
    public AjaxResult listTopic(@Valid PageRequest request) throws Throwable {
        RpcResult<PageResult<List<KafkaTopic>>> result = kafkaService.listTopic(RpcRequest.of(request));
        return fromRpcResult(result);
    }

    @PostMapping("/delete_topic.json")
    public AjaxResult createTopic(@RequestBody @Valid KafkaTopicRequest request) throws Throwable {
        RpcResult<Void> result = kafkaService.deleteTopic(RpcRequest.of(request));
        return fromRpcResult(result);
    }
}
