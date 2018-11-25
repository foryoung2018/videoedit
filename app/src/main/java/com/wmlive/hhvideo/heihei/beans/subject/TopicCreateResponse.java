package com.wmlive.hhvideo.heihei.beans.subject;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * 添加话题
 * Created by kangzhen on 2017/6/1.
 */

public class TopicCreateResponse extends BaseResponse {
    private TopicCreateBean topic;

    public TopicCreateResponse() {
    }

    public TopicCreateBean getTopic() {
        return topic;
    }

    public void setTopic(TopicCreateBean topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "TopicCreateResponse{" +
                "topic=" + topic +
                '}';
    }
}
