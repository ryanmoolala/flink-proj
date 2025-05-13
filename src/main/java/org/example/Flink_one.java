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
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class Flink_one {

    Hashtable<String, Integer> wordCountMap = new Hashtable<String, Integer>();

    public Tuple2<String, Integer> wordCount(String word) {
        if (wordCountMap.containsKey(word)) {
            wordCountMap.put(word, wordCountMap.get(word) + 1);
        } else {
            wordCountMap.put(word, 1);
        }
        return new Tuple2<String, Integer>(word, wordCountMap.get(word));
    }

    public void test_flink_one() {
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

        //perform stream operations on data_stream
        data_stream.print();

        //perform sink operations
//        results.print();
        try {
            stream_env.execute("Flink Word Concatenation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //env.execute() ->
        // trigger execution of above program, wait for the job to finish and
        // then return a JobExecutionResult, this contains execution times and accumulator results.
    }
}
