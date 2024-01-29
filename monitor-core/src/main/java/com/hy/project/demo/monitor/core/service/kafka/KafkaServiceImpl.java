package com.hy.project.demo.monitor.core.service.kafka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.hy.project.demo.common.exception.DemoException;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaGetClusterResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaNode;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopic;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicCreateRequest;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicDescribeResult;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicPartition;
import com.hy.project.demo.monitor.facade.model.kafka.KafkaTopicRequest;
import com.hy.project.demo.monitor.facade.service.KafkaService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.Uuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.REMOTE_BIZ_EXCEPTION;

/**
 * @author rick.wl
 * @date 2024/01/25
 */
@Service
@DubboService
public class KafkaServiceImpl implements KafkaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Override
    public RpcResult<KafkaGetClusterResult> getCluster(RpcRequest<Void> request) throws Throwable {

        KafkaGetClusterResult clusterResult = new KafkaGetClusterResult();

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (Admin admin = Admin.create(props)) {

            DescribeClusterResult result = admin.describeCluster();

            KafkaFuture<Collection<Node>> nodesFuture = result.nodes();
            KafkaFuture<Node> controllerFuture = result.controller();
            KafkaFuture<String> clusterIdFuture = result.clusterId();

            KafkaFuture.allOf(nodesFuture, controllerFuture, clusterIdFuture).get();

            Collection<Node> nodes = nodesFuture.get();
            Node controller = controllerFuture.get();
            String clusterId = clusterIdFuture.get();

            List<KafkaNode> nodeList = new ArrayList<>();
            for (Node node : nodes) {
                nodeList.add(convert(node));
            }
            clusterResult.setNodes(nodeList);
            clusterResult.setController(convert(controller));
            clusterResult.setClusterId(clusterId);
        }

        return RpcResult.success(clusterResult);
    }

    @Override
    public RpcResult<Void> createTopic(RpcRequest<KafkaTopicCreateRequest> request) throws Throwable {

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (Admin admin = Admin.create(props)) {
            KafkaTopicCreateRequest r = request.getData();
            NewTopic newTopic = new NewTopic(r.getTopicName(), r.getPartitions(), r.getReplicationFactor());
            CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));
            KafkaFuture<Uuid> uuidKafkaFuture = result.topicId(r.getTopicName());
            String s = uuidKafkaFuture.get().toString();
            LOGGER.info("create topic {} success", s);

        } catch (Throwable e) {
            LOGGER.error("create topic error", e);

            if (e instanceof ExecutionException) {
                if (e.getCause() instanceof KafkaException) {
                    KafkaException kafkaException = (KafkaException)e.getCause();
                    String msg = kafkaException.getMessage();
                    String code = e.getCause().getClass().getSimpleName();
                    throw new DemoException(REMOTE_BIZ_EXCEPTION, String.format("%s:%s", code, msg));
                }
            }

            throw e;
        }

        return RpcResult.success(null);
    }

    @Override
    public RpcResult<PageResult<List<KafkaTopic>>> listTopic(RpcRequest<PageRequest> request) throws Throwable {
        List<KafkaTopic> topics = null;
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (Admin admin = Admin.create(props)) {

            ListTopicsResult result = admin.listTopics();

            KafkaFuture<Collection<TopicListing>> future = result.listings();
            Collection<TopicListing> topicListings = future.get();

            topics = topicListings.stream().map(t -> new KafkaTopic(t.name(), t.topicId().toString())).sorted(
                Comparator.comparing(KafkaTopic::getName)).collect(Collectors.toList());
        }

        int total = topics.size();
        int pageIndex = request.getData().getPageIndex();
        int pageSize = request.getData().getPageSize();

        PageResult<List<KafkaTopic>> pageResult = new PageResult<>();
        pageResult.setTotalCount(total);
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);

        int startIndex = (pageIndex - 1) * pageSize;
        if (startIndex >= total) {
            return RpcResult.success(pageResult);
        }

        int endIndex = startIndex + pageSize;
        if (endIndex >= total) {
            endIndex = total;
        }

        pageResult.setData(topics.subList(startIndex, endIndex));

        return RpcResult.success(pageResult);
    }

    @Override
    public RpcResult<KafkaTopicDescribeResult> getTopic(RpcRequest<KafkaTopicRequest> request) throws Throwable {
        KafkaTopicDescribeResult describeResult = new KafkaTopicDescribeResult();

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (Admin admin = Admin.create(props)) {
            KafkaTopicRequest r = request.getData();
            DescribeTopicsResult result = admin.describeTopics(Collections.singleton(r.getTopicName()));

            KafkaFuture<TopicDescription> future = result.topicNameValues().get(r.getTopicName());
            TopicDescription topicDescription = future.get();

            describeResult.setName(topicDescription.name());
            describeResult.setUuid(topicDescription.topicId().toString());

            if (null != topicDescription.partitions()) {
                for (TopicPartitionInfo info : topicDescription.partitions()) {
                    KafkaTopicPartition p = new KafkaTopicPartition();
                    p.setPartition(String.valueOf(info.partition()));
                    p.setLeader(convert(info.leader()));
                    if (null != info.replicas()) {
                        for (Node n : info.replicas()) {
                            p.getReplicas().add(convert(n));
                        }
                    }
                    if (null != info.isr()) {
                        for (Node n : info.isr()) {
                            p.getIsr().add(convert(n));
                        }
                    }
                    describeResult.getPartitions().add(p);
                }
            }
        }

        return RpcResult.success(describeResult);
    }

    @Override
    public RpcResult<Void> deleteTopic(RpcRequest<KafkaTopicRequest> request) throws Throwable {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (Admin admin = Admin.create(props)) {
            KafkaTopicRequest r = request.getData();
            DeleteTopicsResult result = admin.deleteTopics(Collections.singleton(r.getTopicName()));
            result.all().get();

        } catch (Throwable e) {
            LOGGER.error("delete topic error", e);

            if (e instanceof ExecutionException) {
                if (e.getCause() instanceof KafkaException) {
                    KafkaException kafkaException = (KafkaException)e.getCause();
                    String msg = kafkaException.getMessage();
                    String code = e.getCause().getClass().getSimpleName();
                    throw new DemoException(REMOTE_BIZ_EXCEPTION, String.format("%s:%s", code, msg));
                }
            }

            throw e;
        }

        return RpcResult.success(null);
    }

    private KafkaNode convert(Node node) {
        return new KafkaNode(node.idString(), node.host(), String.valueOf(node.port()));
    }
}
