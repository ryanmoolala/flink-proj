package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import org.json.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
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
        // Process each CSV file
        for (File file : csvFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip lines that do not contain a comma (e.g., header metadata)
                    if (!line.contains(",")) {
                        continue;
                    }
                    // Preserve trailing empty strings
                    String[] values = line.split(",", -1);
                    String jsonString = convertToJsonString(values);
                    
                    ProducerRecord<String, String> record = new ProducerRecord<>("football-input", jsonString);
                    producer.send(record);
                    // Flush data synchronously
                    producer.flush();
                    Thread.sleep(1000); // Delay between messages
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
        // Ensure there are enough columns before mapping; adjust indices as needed
        if (values.length >= 11) {
            jsonObject.put("minute", values[0]);
            jsonObject.put("player", values[1]);
            jsonObject.put("team", values[2]);
            jsonObject.put("time", values[3]);         // e.g., probability or time value
            jsonObject.put("distance", values[4]);       // may be empty
            jsonObject.put("outcome", values[5]);
            jsonObject.put("score", values[6]);
            jsonObject.put("foot", values[7]);
            jsonObject.put("assist_player", values[9]);
            jsonObject.put("assist_type", values[10]);
        } else if (values.length == 2) {
            // Fallback: if there are not enough columns, map what is available using a simple key
            jsonObject.put("team_a", values[0]);
            jsonObject.put("team_b", values[1]);
        }
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
