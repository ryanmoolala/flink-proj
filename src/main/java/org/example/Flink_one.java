package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.streaming.connectors.kafka.config.OffsetCommitMode;
import org.apache.flink.streaming.connectors.kafka.internals.AbstractFetcher;
import org.apache.flink.streaming.connectors.kafka.internals.AbstractPartitionDiscoverer;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicsDescriptor;
import org.apache.flink.util.SerializedValue;

import java.util.Collection;
import java.util.Map;

public class Flink_one {



    public static void main(String[] args) {
        //This is a simple Flink program that reads from a Kafka topic and performs a word count.

        //getExecutionEnvironment() --> creates the StreamExecutionEnvironment for the program
        StreamExecutionEnvironment stream_env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("word_count")
                .setGroupId("test")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> data_stream = stream_env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source");



        //perform sink operations
        //results.print();
        //env.execute() ->
        // trigger execution of above program, wait for the job to finish and
        // then return a JobExecutionResult, this contains execution times and accumulator results.
    }

}
