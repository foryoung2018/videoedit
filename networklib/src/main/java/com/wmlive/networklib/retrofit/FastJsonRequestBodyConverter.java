package com.wmlive.networklib.retrofit;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;


public class FastJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MediaType.parse("application/octet-stream"), JSON.toJSONBytes(value));
    }
}
