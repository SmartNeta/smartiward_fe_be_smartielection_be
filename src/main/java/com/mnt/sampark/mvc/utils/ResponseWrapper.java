package com.mnt.sampark.mvc.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseWrapper extends HashMap<String, Object> {

    public static String MSG_SAVED_SUCCESSFULLY = "Saved successfully";
    public static String MSG_DELETED_SUCCESSFULLY = "Deleted successfully";

    public ResponseWrapper asData(Object data) {
        put("data", data);
        return this;
    }

    public ResponseWrapper asCode(String data) {
        put("code", data);
        return this;
    }

    public ResponseWrapper asError() {
        put("error", true);
        return this;
    }

    public ResponseWrapper asMessage(String data) {
        put("Message", data);
        return this;
    }

    public ResponseWrapper addAsVoilation(String field, String message) {
        Map<String, String> voilationMap = (Map<String, String>) get("voilation");
        if (voilationMap == null) {
            voilationMap = new HashMap<String, String>();
        }
        voilationMap.put(field, message);
        put("voilation", voilationMap);
        return this;
    }

    public HashMap<String, Object> get() {
        return this;
    }
}
