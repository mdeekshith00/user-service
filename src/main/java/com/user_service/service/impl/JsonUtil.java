package com.user_service.service.impl;

public class JsonUtil {
    private static final com.fasterxml.jackson.databind.ObjectMapper M = new com.fasterxml.jackson.databind.ObjectMapper();

    public static String toJson(Object o) {
        try { return M.writeValueAsString(o); } catch (Exception e) { throw new RuntimeException(e); }
    }

    public static com.fasterxml.jackson.databind.JsonNode jsonNodeFromString(String s) {
        try { return M.readTree(s); } catch (Exception e) { throw new RuntimeException(e); }
    }

}
