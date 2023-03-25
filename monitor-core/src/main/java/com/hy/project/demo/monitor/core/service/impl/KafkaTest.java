package com.hy.project.demo.monitor.core.service.impl;

/**
 * @author rick.wl
 * @date 2022/12/08
 */
public class KafkaTest {
    //public static void main(String[] args) {
    //    String topic = "demo-test";
    //
    //    Properties props = new Properties();
    //    props.put("bootstrap.servers", "106.14.208.194:9092");
    //
    //    props.put("linger.ms", 1);
    //    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    //    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    //
    //    Producer<String, String> producer = new KafkaProducer<>(props);
    //    Future<RecordMetadata> future = producer.send(new ProducerRecord<String, String>(topic, "xixi", "哥哥"));
    //    try {
    //        RecordMetadata metadata = future.get();
    //        System.out.println(metadata);
    //    } catch (Throwable e) {
    //        System.out.println(e);
    //    }
    //
    //    producer.close();
    //
    //    Properties props = new Properties();
    //    props.setProperty("bootstrap.servers", "106.14.208.194:9092");
    //    props.setProperty("group.id", "test");
    //    props.setProperty("enable.auto.commit", "true");
    //    props.setProperty("auto.commit.interval.ms", "1000");
    //    props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    //    props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    //    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    //    consumer.subscribe(Arrays.asList(topic));
    //    while (true) {
    //        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    //        for (ConsumerRecord<String, String> record : records) {
    //            System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record
    //            .value());
    //        }
    //    }
    //
    //    Properties props = new Properties();
    //    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "106.14.208.194:9092");
    //
    //    try (Admin admin = Admin.create(props)) {
    //        int partitions = 12;
    //        short replicationFactor = 3;
    //        // Create a compacted topic
    //        CreateTopicsResult result = admin.createTopics(Collections.singleton(
    //            new NewTopic(topic, partitions, replicationFactor)
    //                .configs(Collections.singletonMap(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig
    //                .CLEANUP_POLICY_COMPACT))));
    //
    //        // Call values() to get the result for a specific topic
    //        KafkaFuture<Void> future = result.values().get(topic);
    //
    //        // Call get() to block until the topic creation is complete or has failed
    //        // if creation failed the ExecutionException wraps the underlying cause.
    //        future.get();
    //
    //
    //        DescribeTopicsResult describeTopicsResult = admin.describeTopics(Lists.newArrayList(topic));
    //        KafkaFuture<TopicDescription> future = describeTopicsResult.topicNameValues().get(topic);
    //        TopicDescription description = future.get();
    //        System.out.println(description);
    //
    //    } catch (Throwable e) {
    //        System.out.println(e);
    //    }
    //}
}
