package com.wmlive.networklib.util;


import com.wmlive.networklib.entity.EventEntity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lsq on 8/21/2017.
 * EventBus的简单帮助类
 */


public class EventHelper {
    /**
     * 注册EventBus
     *
     * @param object
     */
    public static void register(Object object) {
        EventBus.getDefault().register(object);
    }

    /**
     * 解除注册EventBus
     *
     * @param object
     */
    public static void unregister(Object object) {
        EventBus.getDefault().unregister(object);
    }

    /**
     * 发送一个消息
     *
     * @param code 用于区分消息的消息code
     */
    public static void post(int code) {
        EventBus.getDefault().post(new EventEntity(code));
    }

    /**
     * 尽量使用这个方法发送消息
     * 发送一个消息
     * 在接收事件的注册类中使用以下方式处理事件
     *
     * @param code   用于区分消息的消息code
     * @param object 消息的数据
     */
    public static void post(int code, Object object) {
        EventBus.getDefault().post(new EventEntity(code, object));
    }

}
