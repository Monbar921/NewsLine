package com.newsline.responses;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseJSON {
    private final Map<String, Object> response;
    private final Map<String, Object> data;

    public ResponseJSON() {
        response = new LinkedHashMap<>();
        data = new LinkedHashMap<>();
        response.put("message", data);
        response.put("status", null);
        response.put("httpStatus",null);
    }

    public void putToMainBody(String key, Object object){
        response.put(key, object);
    }

    public void putToMessageBody(String key, Object object) {
        data.put(key, object);
    }

    public Map<String, Object> getResponse() {
        return response;
    }
}
