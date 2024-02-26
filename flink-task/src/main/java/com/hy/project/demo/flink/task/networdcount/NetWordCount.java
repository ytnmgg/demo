package com.hy.project.demo.flink.task.networdcount;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author rick.wl
 * @date 2024/02/23
 */
public class NetWordCount {
    public static void main(String[] args) throws Throwable {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> source = env.socketTextStream("127.0.0.1", 7878);
        SingleOutputStreamOperator<Tuple2<String, Long>> operator = source
            .flatMap((String line, Collector<Tuple2<String, Long>> out) -> {
                String[] words = line.split(" ");
                for (String word : words) {
                    out.collect(Tuple2.of(word, 1L));
                }
            }, Types.TUPLE(Types.STRING, Types.LONG))
            .keyBy(data -> data.f0)
            .sum(1);

        operator.print();

        env.execute("Streaming NetWordCount");
    }
}
