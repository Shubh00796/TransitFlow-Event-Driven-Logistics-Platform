package com.transitflow.delivery.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class MetadataUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toJson(Map<String, Object> map) {
        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize metadata", e);
        }
    }

    public static Map<String, Object> fromJson(String json) {
        try {
            return json == null || json.trim().isEmpty()
                    ? Map.of()
                    : MAPPER.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize metadata", e);
        }
    }
}
