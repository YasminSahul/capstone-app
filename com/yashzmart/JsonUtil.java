package com.yashzmart;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(
            String json,
            Class<T> type) {

        return gson.fromJson(json, type);
    }
}