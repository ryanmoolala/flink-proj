package org.example;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;

public class Operations {
    // This class can be used to define operations or methods that can be reused across different parts of the application.
    // Currently, it is empty, but you can add methods or fields as needed for your application logic.
    public Operations() {
        // Constructor can be used to initialize any resources or configurations if needed.
    }

    public SingleOutputStreamOperator<Tuple2<String, Integer>> processStream(DataStreamSource<String> inputStream) {
        // Example method to process the input stream and return a transformed output stream.
        KeyedStream<Tuple2<String, Integer>, String> keyedStream = inputStream
        .flatMap(new Tokeniser())
        .name("Tokeniser")
        .keyBy(value -> value.f0);
        
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCounts = keyedStream
        .sum(1)
        .name("Word Count");

        return wordCounts;
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
