package com.wmlive.hhvideo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by lsq on 2/7/2018.4:05 PM
 *
 * @author lsq
 * @describe FastJson的解析类
 */

public class JsonUtils {
    public static <T> List<T> parseArray(String jsonString, Class<T> clazz) {
        try {
            return JSON.parseArray(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject parseObject(String jsonString) {
        try {
            return JSON.parseObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T parseObject(byte[] jsonString, Class<T> clazz) {
        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
