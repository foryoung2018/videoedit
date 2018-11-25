package com.wmlive.hhvideo.heihei.beans.record;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lsq on 9/13/2017.
 */

public class TopicInfoEntity extends CloneableEntity implements Parcelable {
    public String topicTitle;
    public String topicDesc;
    public long topicId;
    public ExtendEntity extendInfo;

    public TopicInfoEntity(long topicId, String topicTitle, String topicDesc) {
        this.topicTitle = topicTitle;
        this.topicDesc = topicDesc;
        this.topicId = topicId;
    }

    public TopicInfoEntity() {
    }

    @Override
    public String toString() {
        return "TopicInfoEntity{" +
                "topicTitle='" + topicTitle + '\'' +
                ", topicDesc='" + topicDesc + '\'' +
                ", topicId=" + topicId +
                ", extendInfo=" + (extendInfo == null ? "null" : extendInfo.toString()) +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        TopicInfoEntity infoEntity = null;
        try {
            infoEntity = (TopicInfoEntity) super.clone();
            if (infoEntity != null && extendInfo != null) {
                infoEntity.extendInfo = (ExtendEntity) extendInfo.clone();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return infoEntity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topicTitle);
        dest.writeString(this.topicDesc);
        dest.writeLong(this.topicId);
        dest.writeParcelable(this.extendInfo, flags);
    }

    protected TopicInfoEntity(Parcel in) {
        this.topicTitle = in.readString();
        this.topicDesc = in.readString();
        this.topicId = in.readLong();
        this.extendInfo = in.readParcelable(ExtendEntity.class.getClassLoader());
    }

    public static final Creator<TopicInfoEntity> CREATOR = new Creator<TopicInfoEntity>() {
        @Override
        public TopicInfoEntity createFromParcel(Parcel source) {
            return new TopicInfoEntity(source);
        }

        @Override
        public TopicInfoEntity[] newArray(int size) {
            return new TopicInfoEntity[size];
        }
    };
}
