package com.wmlive.hhvideo.heihei.beans.record;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

//import com.rd.vecore.VirtualVideo;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;

/**
 * Created by lsq on 8/30/2017.
 * 作品的配乐信息
 */

public class MusicInfoEntity extends CloneableEntity implements Parcelable {
    public long musicId;
    public float trimStart;
    public float trimEnd;
    private float during;
    private String musicPath;
    public String musicIconUrl;
    public String title;
    public String author;
    public ExtendEntity extendInfo;

    public float getDuring() {
        if (isLocalFile(musicPath)) {
            during = VideoUtils.getVideoLength(musicPath)/1000;

//            during = VirtualVideo.getMediaInfo(musicPath, null);
            during = during > 0 ? during : 0;
        }
        return during;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String path) {
        musicPath = path;
    }

    public boolean isLocalFile(String path) {
        return !TextUtils.isEmpty(path)
                && (path.startsWith("/") || path.startsWith("/assets/"));
    }

    /**
     * 设置截取音乐的时间
     *
     * @param trimStart
     * @param trimEnd
     */
    public void setTrimRange(float trimStart, float trimEnd) {
        if (trimStart >= 0 && trimEnd >= 0 && (trimEnd > trimStart)) {
            this.trimStart = Math.min(trimStart, during);
            this.trimEnd = Math.min(RecordManager.get().getSetting().maxVideoDuration + this.trimStart, trimEnd);
        }
    }


    @Override
    public String toString() {
        return "MusicInfoEntity{" +
                "musicId=" + musicId +
                ", trimStart=" + trimStart +
                ", trimEnd=" + trimEnd +
                ", during=" + during +
                ", musicPath='" + musicPath + '\'' +
                ", musicIconUrl='" + musicIconUrl + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", extendInfo=" + (extendInfo == null ? "null" : extendInfo.toString()) +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MusicInfoEntity infoEntity = null;
        try {
            infoEntity = (MusicInfoEntity) super.clone();
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
        dest.writeLong(this.musicId);
        dest.writeFloat(this.trimStart);
        dest.writeFloat(this.trimEnd);
        dest.writeFloat(this.during);
        dest.writeString(this.musicPath);
        dest.writeString(this.musicIconUrl);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeParcelable(this.extendInfo, flags);
    }

    public MusicInfoEntity() {
    }

    protected MusicInfoEntity(Parcel in) {
        this.musicId = in.readLong();
        this.trimStart = in.readFloat();
        this.trimEnd = in.readFloat();
        this.during = in.readFloat();
        this.musicPath = in.readString();
        this.musicIconUrl = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.extendInfo = in.readParcelable(ExtendEntity.class.getClassLoader());
        setTrimRange(trimStart, trimEnd);
    }

    public static final Creator<MusicInfoEntity> CREATOR = new Creator<MusicInfoEntity>() {
        @Override
        public MusicInfoEntity createFromParcel(Parcel source) {
            return new MusicInfoEntity(source);
        }

        @Override
        public MusicInfoEntity[] newArray(int size) {
            return new MusicInfoEntity[size];
        }
    };
}
