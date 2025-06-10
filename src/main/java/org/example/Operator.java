package org.example;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;
import org.json.JSONObject;

public class Operator {
    public static HashMap<String, Integer> team_score = new HashMap<String, Integer>();
    
    public Operator() {
    }

    public SingleOutputStreamOperator<String> processStream(DataStreamSource<String> inputStream) {
        return inputStream
            .map(value -> {
                JSONObject json_object = new JSONObject(value);
               // System.out.println(json_object.toString());
                return json_object;
            }).map(new UpdateTeamScore()).map(new ProduceHighlight());
    }

    class UpdateTeamScore implements MapFunction<JSONObject, JSONObject> {
        @Override
        public JSONObject map(JSONObject value) throws Exception {
            if (value.has("team_a") && value.has("team_b")) {
                team_score.putIfAbsent(value.getString("team_a"), 0);
                team_score.putIfAbsent(value.getString("team_b"), 0);
                // Initialize scores for both teams if not already present
                return value;
            }

            String team = value.getString("team");
            String action = value.has("outcome") ? value.getString("outcome") : "none"; 
            if (action.equals("Goal")) {
                team_score.put(team, team_score.getOrDefault(team, 0) + 1);
            } 

            System.out.println(team_score);
            return value;
        }
    }

    class ProduceHighlight implements MapFunction<JSONObject, String> {
        @Override
        public String map(JSONObject value) throws Exception {
            // Convert the JSONObject to a String for further processing
            return value.toString();
        }
    }
}
