package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Flink_one {
    public void test_flink_one() throws Exception {
        //This is a simple Flink program that reads from a Kafka topic and performs a word count.
        // Create the execution environmentSystem.out.println("flink program starts");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("flink_one")
                .setGroupId("test")
                .setValueOnlyDeserializer(new org.apache.flink.api.common.serialization.SimpleStringSchema())
                .build();

        DataStreamSource<String> data = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
        data.print();
        env.execute();
    }
}
