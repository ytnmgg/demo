package com.hy.project.demo.flink.task.demolog;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class DemoLog {

    public static void main(String[] args) throws Throwable {
//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        KafkaSource<String> source = KafkaSource.<String>builder()
//                .setBootstrapServers("ecs01:9092,ecs02:9092,ecs03:9092")
//                .setTopics("DEMO_LOG")
//                .setGroupId("FlinkConsumer")
//                .setStartingOffsets(OffsetsInitializer.earliest())
//                .setValueOnlyDeserializer(new SimpleStringSchema())
//                .build();
//
//        DataStreamSource<String> ds = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Demo Log Source");
//
//        ds.print();
//
//        env.execute();


        StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment env = StreamTableEnvironment.create(streamEnv);

//        EnvironmentSettings settings = EnvironmentSettings
//                .newInstance()
//                .inStreamingMode()
//                .build();
//
//        TableEnvironment env = TableEnvironment.create(settings);

        String createDDL = "CREATE TABLE clickTable (" +
                " user_name VARCHAR(30), " +
                " url VARCHAR(100), " +
                " ts BIGINT, " +
                " et AS TO_TIMESTAMP(FROM_UNIXTIME(ts/1000)), " +
                " WATERMARK FOR et AS et - INTERVAL '1' SECOND " +
                ") WITH (" +
                " 'connector'='filesystem', " +
                " 'path'='file:///Users/ytnmgg/work/projects/demo/flink-task/src/main/resources/test/clicks.txt', " +
                " 'format'='csv' " +
                " )";

        env.executeSql(createDDL);

        // 普通topN，查询当前访问前3的用户
        String topNSql = " SELECT user_name, cnt, row_num " +
                "FROM (" +
                "SELECT *, ROW_NUMBER() OVER (" +
                "   ORDER BY cnt DESC " +
                " ) AS row_num " +
                " FROM (SELECT user_name, COUNT(url) AS cnt FROM clickTable GROUP BY user_name)" +
                ") WHERE row_num <= 3";
        Table table = env.sqlQuery(topNSql);

        DataStream<Row> resultStream = env.toChangelogStream(table);
        resultStream.print();

        streamEnv.execute();


//
//        String createSinkDDL = "CREATE TABLE clickSinkTable (" +
//                " user_name VARCHAR(30), " +
//                " url VARCHAR(100), " +
//                " ts BIGINT " +
//                ") WITH (" +
//                " 'connector'='print' " +
////                " 'path'='file:///Users/ytnmgg/work/projects/demo/flink-task/src/main/resources/test', " +
////                " 'format'='csv' " +
//                " )";
//        env.executeSql(createSinkDDL);
//
//        table.executeInsert("clickSinkTable");


    }
}
