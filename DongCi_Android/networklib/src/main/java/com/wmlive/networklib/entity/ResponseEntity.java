package com.wmlive.networklib.entity;


/**
 * 服务器返回结果实体基类
 */
public class ResponseEntity<T> {
    //接口请求码
    public int reqCode;

    //请求成功或者失败的信息
    public String message;

    //服务器返回码
    public int code;

    //服务器返回的数据
    public T data;

    public ResponseEntity() {
    }

    public ResponseEntity(int reqCode, String message, int code, T data) {
        this.reqCode = reqCode;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "reqCode=" + reqCode +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", data=" + (data == null ? null : data.toString()) +
                '}';
    }
}
