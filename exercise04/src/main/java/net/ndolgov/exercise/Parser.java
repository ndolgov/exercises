package net.ndolgov.exercise;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.function.Function;

/**
 * Parse JSON into a field map. Notice that a JSON integer value is parsed into a java.lang.Long instance.
 */
final class Parser implements Function<String, JSONObject> {
    private final JSONParser parser = new JSONParser();

    @Override
    public JSONObject apply(String json) {
        try {
            return (JSONObject) parser.parse(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON: " + json, e);
        }
    }
}
