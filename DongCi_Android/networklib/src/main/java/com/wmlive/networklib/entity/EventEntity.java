package com.wmlive.networklib.entity;

/**
 * Created by lsq on 8/21/2017.
 * EventBus的消息实体类
 */

public class EventEntity {
    public int code;
    public Object data=new Object();

    public EventEntity(int code) {
        this.code = code;
    }

    public EventEntity(int code, Object data) {
        this.code = code;
        this.data = data;
    }
}
