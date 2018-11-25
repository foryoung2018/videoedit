package com.dongci.sun.gpuimglibrary.player.script;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class JsonTool {
    public static JSONObject loadJSONFromFile(String filename) {
        JSONObject result = null;
        try {
            FileInputStream inputStream = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String resultStr = builder.toString();
            JSONTokener tokener = new JSONTokener(resultStr);
            result = new JSONObject(tokener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key)) {
            return null;
        }
        return jsonObject.optJSONObject(key);
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key)) {
            return null;
        }
        return jsonObject.getJSONArray(key);
    }

    public static String getString(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key)) {
            return null;
        }
        return jsonObject.getString(key);
    }

    public static int getInt(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key)) {
            return -1;
        }
        return jsonObject.getInt(key);
    }

    public static long getLong(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key)) {
            return -1;
        }
        return jsonObject.getLong(key);
    }

    public static double getDouble(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key)) {
            return 0;
        }
        return jsonObject.getDouble(key);
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key)) {
            return false;
        }
        return jsonObject.getBoolean(key);
    }
}
