package org.example;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {
    public static void produce() {
        System.out.println("Kafka producer");

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        String topic = "flink_one";
        String key = "key"; //decides which parition record will go to
        //decides the value of the record

        String[] words = {"Kafka"};

//                {
//                "The", "boy", "walked", "to", "the", "river", ".",
//                "The", "boy", "saw", "a", "bird", "by", "the", "river", ".",
//                "The", "bird", "looked", "at", "the", "boy", ",",
//                "and", "the", "boy", "smiled", "."
//        };

        for (String s : words) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, s);
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("Message sent successfully to " + metadata.topic() +
                            ", partition: " + metadata.partition() +
                            ", offset: " + metadata.offset());
                } else {
                    System.err.println("Error sending message: " + exception.getMessage());
                }
            });
        }
        producer.close();
    }
}
