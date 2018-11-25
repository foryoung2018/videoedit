package com.wmlive.hhvideo.heihei.beans.subject;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 第三方话题类
 * Created by kangzhen on 2017/6/6.
 */

public class TopicInfo implements Parcelable {
    public static final String INTENT_EXTRA_KEY_NAME = "hot_topic";
    private String title;
    private String desc;
    private long topicId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public TopicInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeLong(this.topicId);
    }

    protected TopicInfo(Parcel in) {
        this.title = in.readString();
        this.desc = in.readString();
        this.topicId = in.readLong();
    }

    public static final Creator<TopicInfo> CREATOR = new Creator<TopicInfo>() {
        @Override
        public TopicInfo createFromParcel(Parcel source) {
            return new TopicInfo(source);
        }

        @Override
        public TopicInfo[] newArray(int size) {
            return new TopicInfo[size];
        }
    };

    @Override
    public String toString() {
        return "TopicInfo{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", topicId=" + topicId +
                '}';
    }
}
