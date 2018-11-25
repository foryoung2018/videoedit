package com.wmlive.hhvideo.heihei.beans.record;

import android.os.Parcel;
import android.os.Parcelable;

import com.dc.platform.voicebeating.VoiceAnalysisInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsq on 9/25/2017.
 * 扩展用
 */

public class ExtendEntity extends CloneableEntity implements Parcelable {
    public String extendName = "2";

    public List<String> videoImgs = new ArrayList <String>();
    public boolean hasImg;//是否生成图片

    //下载素材时,存储下载信息
    public int downloadId;
    public String videopath;
    public int materialIndex;
    public int state;


    public VoiceAnalysisInfo analysisInfo = null;

    public ExtendEntity() {
    }

    @Override
    public String toString() {
        return "ExtendEntity{" +
                "extendName='" + extendName + '\'' +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ExtendEntity extendEntity = null;
        try {
            extendEntity = (ExtendEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return extendEntity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.extendName);
        dest.writeStringList(this.videoImgs);
        dest.writeInt(this.downloadId);
        dest.writeString(this.videopath);
        dest.writeInt(this.materialIndex);
        dest.writeInt(this.state);
        dest.writeParcelable(this.analysisInfo, flags);
    }

    protected ExtendEntity(Parcel in) {
        this.extendName = in.readString();
        this.videoImgs = in.createStringArrayList();
        this.downloadId = in.readInt();
        this.videopath = in.readString();
        this.materialIndex = in.readInt();
        this.state = in.readInt();
        this.analysisInfo = in.readParcelable(VoiceAnalysisInfo.class.getClassLoader());
    }

    public static final Creator <ExtendEntity> CREATOR = new Creator <ExtendEntity>() {
        @Override
        public ExtendEntity createFromParcel(Parcel source) {
            return new ExtendEntity(source);
        }

        @Override
        public ExtendEntity[] newArray(int size) {
            return new ExtendEntity[size];
        }
    };
}
