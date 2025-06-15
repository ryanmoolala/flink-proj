package org.example;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.json.JSONObject;

public class Operator {
    private static HashMap<String, Integer> team_score = new HashMap<String, Integer>();
    private KafkaSink<String> sink;
    public Operator(KafkaSink<String> sink) {
        this.sink = sink;
    }

    public SingleOutputStreamOperator<String> processStream(DataStreamSource<String> inputStream) {
        return inputStream
            .map(value -> {
                JSONObject json_object = new JSONObject(value);
                return json_object;
            }).map(new UpdateTeamScore()).map(new ProduceHighlight());
    }

    class TeamFilterFunction implements FilterFunction<JSONObject> {
        @Override
        public boolean filter(JSONObject value) throws Exception {
            return value.has("player");
        }
    }

    class UpdateTeamScore implements MapFunction<JSONObject, JSONObject> {
        @Override
        public JSONObject map(JSONObject value) throws Exception {
            if (value.has("team_a") && value.has("team_b")) {
                team_score.putIfAbsent(value.getString("team_a"), 0);
                team_score.putIfAbsent(value.getString("team_b"), 0);

                return value;
            }
            String team = value.getString("team");
            String action = value.has("outcome") ? value.getString("outcome") : "none"; 
            if (action.equals("Goal")) {
                team_score.put(team, team_score.getOrDefault(team, 0) + 1);
            } 
            return value;
        }
    }

    class ProduceHighlight implements MapFunction<JSONObject, String> { // sent to timeline
        @Override
        public String map(JSONObject value) throws Exception {

            if (!value.has("player") || !value.has("team") || !value.has("minute") || 
                !value.has("outcome") || !value.has("foot") || !value.has("score")) {
                return value.toString(); // skip if any required field is missing
            }

            String player = value.getString("player");
            String team = value.getString("team");
            String minute = value.getString("minute");
            String outcome = value.getString("outcome");
            String foot = value.getString("foot");
            String distance = value.getString("score");
            String highlight = "";
            String team_score_highlight = "";
            
            if (outcome.equals("Goal")) {
                highlight = String.format("Goal by %s of %s at minute %s with %s from %s meters away!", 
                                                  player, team, minute, foot, distance);
                team_score_highlight = team_score.toString();
            } else {
                java.util.Random random = new java.util.Random();
                int style = random.nextInt(4);
                switch (style) {
                    case 0:
                        highlight = String.format("In the %sth minute, %s of %s fired a %s strike from %s meters... but it was %s!",
                                                  minute, player, team, foot, distance, outcome.toLowerCase());
                        break;
                    case 1:
                        highlight = String.format("%s (%s) – %s foot, %sm out, %sth min – %s!",
                                                  player, team, foot, distance, minute, outcome.toLowerCase());
                        break;
                    case 2:
                        highlight = String.format("%s attempts a %s-footed shot from %s meters for %s in minute %s — result: %s.",
                                                  player, foot, distance, team, minute, outcome.toLowerCase());
                        break;
                    case 3:
                        highlight = String.format("Minute %s: %s unleashes a %s-footed shot from %s meters for %s... %s!",
                                                  minute, player, foot, distance, team, outcome.toLowerCase());
                        break;
                }                
            }
            
            //create the json object to be sent to the timeline
            JSONObject highlightJson = new JSONObject();
            highlightJson.put("player", player);
            highlightJson.put("team", team);
            highlightJson.put("minute", minute);
            highlightJson.put("outcome", outcome);
            highlightJson.put("foot", foot);
            highlightJson.put("distance", distance);
            highlightJson.put("highlight", highlight);
            //System.out.println(highlight)
            if (team_score_highlight.length() != 0) {
                highlightJson.put("team_score", team_score);
            } 
            return highlightJson.toString();
        }
    }
}
