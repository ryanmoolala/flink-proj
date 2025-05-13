package org.example;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {
    public static void main(String[] args) {
        System.out.println("Kafka producer");

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        String topic = "word_count";
        String key = "key"; //decides which parition record will go to
        //decides the value of the record

        String[] words = {
                "The", "boy", "walked", "to", "the", "river", ".",
                "The", "boy", "saw", "a", "bird", "by", "the", "river", ".",
                "The", "bird", "looked", "at", "the", "boy", ",",
                "and", "the", "boy", "smiled", ".",
                "The", "river", "flowed", "quietly", ",",
                "as", "if", "it", "too", "was", "watching", "the", "boy", "and", "the", "bird", "."
        };


        for (String s : words) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, s);
            producer.send(record);
            try {
                Thread.sleep(10000); // Sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        producer.close();
    }
}
