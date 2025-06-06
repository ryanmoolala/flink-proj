package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.shaded.zookeeper3.org.apache.zookeeper.Op;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class Flink_one {
    public Operator operator = new Operator(); 
    public void test_flink_one() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Load configuration from YAML file using try-with-resources
        Yaml yaml = new Yaml();
        Map<String, Object> config;

        try (InputStream input = new FileInputStream("src/main/java/org/resources/config.yaml")) {
            config = yaml.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from YAML file", e);
        }

        if (config == null || !config.containsKey("kafka")) {
            throw new RuntimeException("Configuration is missing or invalid");
        }

        //Extracting Kafka configuration from the loaded YAML
        Map<String, Object> kafkaConfig = (Map<String, Object>) config.get("kafka");
        String bootstrapServers = (String) kafkaConfig.get("bootstrapServers");
        String groupId = (String) kafkaConfig.get("groupId");
        Map<String, Object> sourceConfig = (Map<String, Object>) kafkaConfig.get("source");
        String sourceTopic = (String) sourceConfig.get("topic");
        Map<String, Object> sinkConfig = (Map<String, Object>) kafkaConfig.get("sink");
        String sinkTopic = (String) sinkConfig.get("topic");
        String deliveryGuaranteeStr = (String) sinkConfig.get("deliveryGuarantee");
        DeliveryGuarantee deliveryGuarantee = DeliveryGuarantee.valueOf(deliveryGuaranteeStr);

        //Source configuration using new JSON deserialization schema
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(sourceTopic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new org.apache.flink.api.common.serialization.SimpleStringSchema())
                .build();

        DataStreamSource<String> data = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
        
        //Transformation: Tokenize the input data and count the occurrences of each word
        operator.processStream(data);

        //Sink configuration using config.yaml values
        // KafkaSink<Tuple2<String, Integer>> sink = KafkaSink.<Tuple2<String, Integer>>builder()
        //         .setBootstrapServers(bootstrapServers)
        //         .setRecordSerializer(
        //             KafkaRecordSerializationSchema.builder()
        //                 .setTopic(sinkTopic)
        //                 .setValueSerializationSchema(new TupleSerializer())
        //                 .build()
        //                 )
        //         .setDeliveryGuarantee(deliveryGuarantee)
        //         .build();

        // // wordCounts.sinkTo(sink)
        // //         .name("Kafka Sink");
        
        //Execute the flink job
        env.execute();
    }

    public class TupleSerializer implements SerializationSchema<Tuple2<String, Integer>> {
        @Override
        public byte[] serialize(Tuple2<String, Integer> element) {
            return (element.f0 + "," + element.f1).getBytes();
        }
    }

    public class StringArrayDeserializer implements org.apache.flink.api.common.serialization.DeserializationSchema<String[]> {
        @Override
        public String[] deserialize(byte[] message) {
            if (message == null || message.length == 0) {
                return new String[0];
            }
            String str = new String(message);
            return str.split(",");
        }

        @Override
        public boolean isEndOfStream(String[] nextElement) {
            return false;
        }

        @Override
        public TypeInformation<String[]> getProducedType() {
            return TypeInformation.of(String[].class);
        }    
    }
}
