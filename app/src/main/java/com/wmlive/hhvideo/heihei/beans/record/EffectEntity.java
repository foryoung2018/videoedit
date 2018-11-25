package com.wmlive.hhvideo.heihei.beans.record;

import android.os.Parcel;
import android.os.Parcelable;

//import com.rd.vecore.models.EffectType;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;

/**
 * Created by lsq on 8/30/2017.
 * 特效实体类
 */

public class EffectEntity extends CloneableEntity implements Parcelable {
//    public EffectType effectType;
    private float startTime;
    private float endTime;
    public ExtendEntity extendInfo;

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime >= 0 ? startTime : 0;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        float max = RecordManager.get().getSetting().maxVideoDuration;
        this.endTime = endTime > max ? max : endTime;
    }

    public void setTimeRange(float startTime, float endTime) {
        setStartTime(startTime);
        setEndTime(endTime);
    }

    @Override
    public String toString() {
        return "EffectEntity{" +
//                "effectType=" + effectType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", extendInfo=" + (extendInfo == null ? "null" : extendInfo.toString()) +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EffectEntity videoEntity = null;
        try {
            videoEntity = (EffectEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return videoEntity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(this.effectType == null ? -1 : this.effectType.ordinal());
        dest.writeFloat(this.startTime);
        dest.writeFloat(this.endTime);
        dest.writeParcelable(this.extendInfo, flags);
    }

    public EffectEntity() {
    }

    protected EffectEntity(Parcel in) {
        int tmpEffectType = in.readInt();
//        this.effectType = tmpEffectType == -1 ? null : EffectType.values()[tmpEffectType];
        this.startTime = in.readFloat();
        this.endTime = in.readFloat();
        this.extendInfo = in.readParcelable(ExtendEntity.class.getClassLoader());
    }

    public static final Creator<EffectEntity> CREATOR = new Creator<EffectEntity>() {
        @Override
        public EffectEntity createFromParcel(Parcel source) {
            return new EffectEntity(source);
        }

        @Override
        public EffectEntity[] newArray(int size) {
            return new EffectEntity[size];
        }
    };
}
