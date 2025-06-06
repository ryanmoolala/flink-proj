package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.concurrent.Future;

import org.json.JSONObject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

//submits data to the kafka topic
public class Reader {
    public static void run() {
        // List all CSV files in the directory
        File folder = new File("match_shot_log");
        File[] csvFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (csvFiles == null || csvFiles.length == 0) {
            System.out.println("No CSV files found.");
            return;
        }

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        //System.out.println(csvFiles.length + " CSV files found in the directory.");
        for (File file : csvFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Preserve trailing empty strings and print the array
                    String values = convertToJsonString(line.split(",", -1));

                    ProducerRecord<String, String> record = new ProducerRecord<>("football-input", values);
                    
                    producer.send(record);
                    // flush data - synchronous
                    producer.flush();

                    Thread.sleep(100);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }   
        System.out.println("All records sent successfully.");
        producer.close();    
    }

    public static String convertToJsonString(String[] values) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("minute", values[0]);
        jsonObject.put("player", values[1]);
        jsonObject.put("team", values[2]);
        jsonObject.put("outcome", values[3]);
        jsonObject.put("distance", values[4]);
        jsonObject.put("sca1", values[5]);
        jsonObject.put("sca2", values[6]);
        return jsonObject.toString();
    }

    // New serializer to convert String[] to JSON formatted string
    public static class JsonSerializer implements org.apache.kafka.common.serialization.Serializer<String[]> {
        @Override
        public byte[] serialize(String topic, String[] data) {
            if (data == null) {
                return "".getBytes();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < data.length; i++) {
                sb.append("\"").append(data[i]).append("\"");
                if (i != data.length - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            return sb.toString().getBytes();
        }
    }
}
