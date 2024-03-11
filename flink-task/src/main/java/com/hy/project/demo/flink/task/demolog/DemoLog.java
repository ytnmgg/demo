package com.hy.project.demo.flink.task.demolog;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DemoLog {

    public static void main(String[] args) throws Throwable {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("ecs01:9092,ecs02:9092,ecs03:9092")
                .setTopics("DEMO_LOG")
                .setGroupId("FlinkConsumer")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> ds = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Demo Log Source");

        ds.print();

        env.execute();
    }
}
