package org.example;


import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Flink_one {
    public void test_flink_one() throws Exception {

        //This is a simple Flink program that reads from a Kafka topic and performs a word count.
        // Create the execution environmentSystem.out.println("flink program starts");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("flink_one")
                .setGroupId("test")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new org.apache.flink.api.common.serialization.SimpleStringSchema())
                .build();

        DataStreamSource<String> data = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        KeyedStream<Tuple2<String, Integer>, String> keyedStream = data
        .flatMap(new Tokeniser())
        .name("Tokeniser")
        .keyBy(value -> value.f0);
        
        DataStreamSink<Tuple2<String, Integer>> wordCounts = keyedStream
        .sum(1)
        .name("Word Count").print();

        env.execute();
    }

    public class Tokeniser implements org.apache.flink.api.common.functions.FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            String[] words = value.split("\\s+");
            for (String word : words) {
                out.collect(new Tuple2<>(word, 1));
            }
        }

    }




}
