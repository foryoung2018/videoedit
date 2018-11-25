package com.wmlive.hhvideo.heihei.record.engine.model;

import android.content.Context;
import android.graphics.RectF;
import android.os.Parcel;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;

import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImagePolkaDotFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFalseColorFilter;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MAsset extends MediaObject {

    DCAsset dcAsset;


    @Override
    public DCAsset getAsset() {
        if (dcAsset == null)
            dcAsset = new DCAsset();
        dcAsset.assetId = assetId;
        dcAsset.filePath = filePath;
        dcAsset.fillType = fillType;
        dcAsset.rectInVideo = rectInVideo;
        dcAsset.cropRect = cropRect;
        dcAsset.index = index;
        dcAsset.startTimeInScene = startTimeInScene;
        DCAsset.TimeRange timeRange = getTimeRange();
        dcAsset.type = type;
        dcAsset.setTimeRange(timeRange);
        dcAsset.setVolume(volume);
        if (type == DCAsset.DCAssetTypeVideo) {
            if (gpuImageFilter != null)
                dcAsset.setFilter(gpuImageFilter);
        }
        dcAsset.weights = weights;
        dcAsset.imagePaths = imagePaths;
        dcAsset.frameInterval = frameInterval;
        dcAsset.decorationName = decorationName;
        dcAsset.decorationMaskPath = decorationMaskPath;
        dcAsset.isBillboard = isBillboard;
        dcAsset.zRotation = rotate;
        return dcAsset;
    }


    protected int rotate;

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public MAsset(String framePath) {
        super.MediaObject(framePath);
    }

    public MAsset() {
    }

    public MAsset(String framePath, int type) {
        super.MediaObject(framePath, type);
    }

    @Override
    public void setMixFactor() {

    }

//    public DCAsset.TimeRange getTimeRange(){
//        if(timeRange==null)
//            timeRange = new DCAsset.TimeRange(0,getDuration());
//        return timeRange;
//    }

    @Override
    public void setTimeRange(float start, float duration) {
        if (timeRange == null)
            timeRange = new DCAsset.TimeRange((long) start * 1000, (long) duration * 1000);
        else {
            timeRange.startTime = (long) start * 1000;
            timeRange.duration = (long) duration * 1000;
        }
    }

    @Override
    public void setVideoWidth(int width) {
        videoWidth = width;
    }

    @Override
    public void setVideoHeight(int height) {
        videoHeight = height;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.dcAsset, flags);
        dest.writeString(this.filePath);
        dest.writeParcelable(this.cropRect, flags);
        dest.writeParcelable(this.rectInVideo, flags);
        dest.writeLong(this.frameInterval);
        dest.writeStringList(this.imagePaths);
        dest.writeLong(this.startTimeInScene);
        dest.writeInt(this.videoWidth);
        dest.writeInt(this.videoHeight);
        dest.writeInt(this.type);
        dest.writeString(this.decorationName);
        dest.writeString(this.decorationMaskPath);
        dest.writeInt(this.fillType);
        dest.writeParcelable(this.timeRange, flags);
        dest.writeFloat(this.volume);
        dest.writeInt(this.assetId);
        dest.writeFloat(this.weights);
        dest.writeInt(this.index);
        dest.writeFloat(this.scalenorm);
        dest.writeFloat(this.inputscale);
        dest.writeByte(this.isBillboard ? (byte) 1 : (byte) 0);
    }

    protected MAsset(Parcel in) {
        this.dcAsset = in.readParcelable(DCAsset.class.getClassLoader());
        this.filePath = in.readString();
        this.cropRect = in.readParcelable(RectF.class.getClassLoader());
        this.rectInVideo = in.readParcelable(RectF.class.getClassLoader());
        this.frameInterval = in.readLong();
        this.imagePaths = in.createStringArrayList();
        this.startTimeInScene = in.readLong();
        this.videoWidth = in.readInt();
        this.videoHeight = in.readInt();
        this.type = in.readInt();
        this.decorationName = in.readString();
        this.decorationMaskPath = in.readString();
        this.fillType = in.readInt();
        this.timeRange = in.readParcelable(DCAsset.TimeRange.class.getClassLoader());
        this.volume = in.readFloat();
        this.assetId = in.readInt();
        this.weights = in.readFloat();
        this.index = in.readInt();
        this.scalenorm = in.readFloat();
        this.inputscale = in.readFloat();
        this.isBillboard = in.readByte() != 0;
    }

    public static final Creator<MAsset> CREATOR = new Creator<MAsset>() {
        @Override
        public MAsset createFromParcel(Parcel source) {
            return new MAsset(source);
        }

        @Override
        public MAsset[] newArray(int size) {
            return new MAsset[size];
        }
    };
}
