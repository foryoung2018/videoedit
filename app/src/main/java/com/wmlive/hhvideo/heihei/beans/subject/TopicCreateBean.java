package com.wmlive.hhvideo.heihei.beans.subject;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * 添加话题成功bean
 * Created by kangzhen on 2017/6/1.
 */

public class TopicCreateBean extends BaseModel {
    private String description;
    private String topic_type;
    private int opus_count;
    private int visible;
    private int creator_id;
    private int default_music_id;
    private String topic_tag;
    private long id;
    private String name;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic_type() {
        return topic_type;
    }

    public void setTopic_type(String topic_type) {
        this.topic_type = topic_type;
    }

    public int getOpus_count() {
        return opus_count;
    }

    public void setOpus_count(int opus_count) {
        this.opus_count = opus_count;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public int getDefault_music_id() {
        return default_music_id;
    }

    public void setDefault_music_id(int default_music_id) {
        this.default_music_id = default_music_id;
    }

    public String getTopic_tag() {
        return topic_tag;
    }

    public void setTopic_tag(String topic_tag) {
        this.topic_tag = topic_tag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TopicCreateBean{" +
                "description='" + description + '\'' +
                ", topic_type='" + topic_type + '\'' +
                ", opus_count=" + opus_count +
                ", visible=" + visible +
                ", creator_id=" + creator_id +
                ", default_music_id=" + default_music_id +
                ", topic_tag='" + topic_tag + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
