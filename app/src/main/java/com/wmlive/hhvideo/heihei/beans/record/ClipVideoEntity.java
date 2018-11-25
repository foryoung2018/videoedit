package com.wmlive.hhvideo.heihei.beans.record;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.models.VideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordSpeed;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by lsq on 8/30/2017.
 * 视频片段实体类
 */

public class ClipVideoEntity extends CloneableEntity implements Parcelable {
    public String videoPath;



    public String audioPath;
    public int speedIndex = RecordSpeed.NORMAL.ordinal();
    private float during;
    public int videoWidth;
    public int videoHeight;
    public boolean supportFastReverse; // 是否支持快速倒序
    public ExtendEntity extendInfo;

    public boolean isValid() {
        return during > 0 && !TextUtils.isEmpty(videoPath) /*&& new File(videoPath).exists()*/;
    }

    public float getDuring() {
        return isValid() ? during : 0;
    }

    public void setSpeedIndex(int speedIndex) {
        if (speedIndex >= RecordSpeed.SLOWEST.ordinal() && speedIndex <= RecordSpeed.FASTEST.ordinal()) {
            this.speedIndex = speedIndex;
        }
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public void setVideoPath(String path) {
        videoPath = path;
        MVideoConfig videoConfig = new MVideoConfig();
//        during = VirtualVideo.getMediaInfo(videoPath, videoConfig);
        during = VideoUtils.getVideoLength(videoPath)*1.0f / (1000000.0f);
        KLog.e("videoplay======VirtualVideo视频获取到的时间是：" + during + " ,videoPath:" + (new File(videoPath).exists()) + videoPath);
        during = during > 0 ? during : 0;
        videoWidth = videoConfig.getVideoWidth();
        videoHeight = videoConfig.getVideoHeight();
    }

//    private long getVideoDuration(String url){
//            KLog.e("videoplay======VirtualVideo视频获取到的时间-media--pre>" + (new File(url).exists())+url);
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            mediaMetadataRetriever.setDataSource(url);
//            String result = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//
////            mediaMetadataRetriever.getFrameAtTime()
//            // 单位毫秒
//            KLog.i("videoplay======VirtualVideo视频获取到的时间-media--retri>" + result);
//            return (long)Math.floor(Double.parseDouble(result)/1000);
////            DCMediaInfoExtractor.MediaInfo mediaInfo = DCMediaInfoExtractor.extract(url);
////            return mediaInfo.durationUs;
//    }

    public boolean delete() {
        if (!TextUtils.isEmpty(videoPath)) {
            File file = new File(videoPath);
            if (file.exists() && !file.isDirectory()) {
                if (!TextUtils.isEmpty(audioPath)) {
                    File audio = new File(audioPath);
                    if (audio.exists() && !audio.isDirectory()) {
                        audio.delete();
                    }
                }
                boolean isOk = file.delete();
                if (isOk) {
                    videoPath = null;
                    audioPath = null;
                }
                return isOk;
            }
            videoPath = null;
            audioPath = null;
            return true;
        }
        return true;
    }

    public boolean hasVideo() {
        return !TextUtils.isEmpty(videoPath) && new File(videoPath).exists();
    }

    @Override
    public String toString() {
        return "ClipVideoEntity{" +
                "videoPath='" + videoPath + '\'' +
                ", audioPath='" + audioPath + '\'' +
                ", speedIndex=" + speedIndex +
                ", during=" + during +
                ", videoWidth=" + videoWidth +
                ", videoHeight=" + videoHeight +
                ", supportFastReverse=" + supportFastReverse +
                ", extendInfo=" + (extendInfo == null ? "null" : extendInfo.toString()) +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ClipVideoEntity videoEntity = null;
        try {
            videoEntity = (ClipVideoEntity) super.clone();
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
        dest.writeString(this.videoPath);
        dest.writeString(this.audioPath);
        dest.writeInt(this.speedIndex);
        dest.writeFloat(this.during);
        dest.writeInt(this.videoWidth);
        dest.writeInt(this.videoHeight);
        dest.writeByte(this.supportFastReverse ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.extendInfo, flags);
    }

    public ClipVideoEntity() {
    }

    protected ClipVideoEntity(Parcel in) {
        this.videoPath = in.readString();
        this.audioPath = in.readString();
        this.speedIndex = in.readInt();
        this.during = in.readFloat();
        this.videoWidth = in.readInt();
        this.videoHeight = in.readInt();
        this.supportFastReverse = in.readByte() != 0;
        this.extendInfo = in.readParcelable(ExtendEntity.class.getClassLoader());
    }

    public static final Creator<ClipVideoEntity> CREATOR = new Creator<ClipVideoEntity>() {
        @Override
        public ClipVideoEntity createFromParcel(Parcel source) {
            return new ClipVideoEntity(source);
        }

        @Override
        public ClipVideoEntity[] newArray(int size) {
            return new ClipVideoEntity[size];
        }
    };
}
