package com.wmlive.networklib.retrofit;

import com.alibaba.fastjson.JSON;
import com.wmlive.networklib.util.NetLog;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;

public class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Type type;

    public FastJsonResponseBodyConverter(Type type) {
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        BufferedSource bufferedSource = Okio.buffer(value.source());
        String tempStr = bufferedSource.readUtf8();
        bufferedSource.close();
        return parseJson(tempStr, type);
    }

    /**
     * 字符串转对象
     *
     * @param jsonString
     * @param type
     * @param <T>
     * @return
     */
    private <T> T parseJson(String jsonString, Type type) {
        try {
            NetLog.json("retrofit response json", jsonString);
            return JSON.parseObject(jsonString, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
