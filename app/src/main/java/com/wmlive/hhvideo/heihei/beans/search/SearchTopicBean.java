package com.wmlive.hhvideo.heihei.beans.search;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * 搜索中话题bean
 * Created by kangzhen on 2017/6/2.
 */

public class SearchTopicBean extends BaseModel {
    private String description;
    private long creator_id;
    private int opus_count;
    private int visible;
    private String topic_tag;
    private long default_music_id;
    private long id;
    private String name;
    private String desc;//此处描述为搜索无结果是信息展示

    public SearchTopicBean() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(long creator_id) {
        this.creator_id = creator_id;
    }

    public long getDefault_music_id() {
        return default_music_id;
    }

    public void setDefault_music_id(long default_music_id) {
        this.default_music_id = default_music_id;
    }

    @Override
    public String toString() {
        return "{" +
                "description='" + description + '\'' +
                ", creator_id=" + creator_id +
                ", opus_count=" + opus_count +
                ", visible=" + visible +
                ", topic_tag='" + topic_tag + '\'' +
                ", default_music_id=" + default_music_id +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
