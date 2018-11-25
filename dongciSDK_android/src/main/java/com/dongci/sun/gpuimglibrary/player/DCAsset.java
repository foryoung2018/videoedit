package com.dongci.sun.gpuimglibrary.player;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by zhangxiao on 2018/5/31.
 *
 */

public class DCAsset implements Parcelable{

    // asset type
    public static final int DCAssetTypeVideo = 0;
    public static final int DCAssetTypeAudio = 1;
    public static final int DCAssetTypeImage = 2;
    public static final int DCAssetTypeImages = 3;

    protected DCAsset(Parcel in) {
        filePath = in.readString();
        type = in.readInt();
        fillType = in.readInt();
        cropRect = in.readParcelable(RectF.class.getClassLoader());
        rectInVideo = in.readParcelable(RectF.class.getClassLoader());
        startTimeInScene = in.readLong();
        volume = in.readFloat();
        weights = in.readFloat();
        index = in.readInt();
        scalenorm = in.readFloat();
        inputscale = in.readFloat();
        imagePaths = in.createStringArrayList();
        frameInterval = in.readLong();
        assetId = in.readInt();
        decorationName = in.readString();
        decorationMaskPath = in.readString();
        isBillboard = in.readByte() != 0;
    }

    public static final Creator<DCAsset> CREATOR = new Creator<DCAsset>() {
        @Override
        public DCAsset createFromParcel(Parcel in) {
            return new DCAsset(in);
        }

        @Override
        public DCAsset[] newArray(int size) {
            return new DCAsset[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeInt(type);
        dest.writeInt(fillType);
        dest.writeParcelable(cropRect, flags);
        dest.writeParcelable(rectInVideo, flags);
        dest.writeLong(startTimeInScene);
        dest.writeFloat(volume);
        dest.writeFloat(weights);
        dest.writeInt(index);
        dest.writeFloat(scalenorm);
        dest.writeFloat(inputscale);
        dest.writeStringList(imagePaths);
        dest.writeLong(frameInterval);
        dest.writeInt(assetId);
        dest.writeString(decorationName);
        dest.writeString(decorationMaskPath);
        dest.writeByte((byte) (isBillboard ? 1 : 0));
    }

    @IntDef({DCAssetTypeVideo, DCAssetTypeAudio, DCAssetTypeImage, DCAssetTypeImages})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCAssetType {
    }

    // asset fill type
    public static final int DCAssetFillTypeScaleToFit = 0;
    public static final int DCAssetFillTypeAspectFill = 1;
    @IntDef({DCAssetFillTypeScaleToFit, DCAssetFillTypeAspectFill})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCAssetFillType {
    }

    public final static class TimeRange implements Parcelable{
        // us
        public long startTime;
        public long duration;

        public TimeRange(long startTime, long duration) {
            this.startTime = startTime;
            this.duration = duration;
        }

        protected TimeRange(Parcel in) {
            startTime = in.readLong();
            duration = in.readLong();
        }

        public static final Creator<TimeRange> CREATOR = new Creator<TimeRange>() {
            @Override
            public TimeRange createFromParcel(Parcel in) {
                return new TimeRange(in);
            }

            @Override
            public TimeRange[] newArray(int size) {
                return new TimeRange[size];
            }
        };

        public boolean containsTime(long time) {
            return time >= startTime && time <= startTime + duration;
        }

        public long endTime() {
            return startTime + duration;
        }

        public String toString(){
            return "TimeRange:-start:"+startTime+"duration->"+duration;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(startTime);
            dest.writeLong(duration);
        }
    }

    public String filePath;
    public @DCAssetType int type;
    public @DCAssetFillType int fillType;
    public RectF cropRect;
    public RectF rectInVideo;

    // us
    public long startTimeInScene;
    private TimeRange timeRange;

    private @FloatRange(from=0.0f, to=1.0f) float volume = 1.0f;

    private DCAssetWrapper assetWrapper;

    public float  weights;
    public int    index;
    float  scalenorm;
    float  inputscale;

    // animation
    public List<String> imagePaths;
    public long frameInterval;
    public int assetId;
    public String decorationName;
    public String decorationMaskPath;
    public boolean isBillboard;

    // rotate angle(0/+-90/+-180/+-270)
    public int zRotation;

    public GPUImageFilter filter;
    public int  filterType;

    public DCAsset() {
        this.type = DCAssetTypeVideo;
        this.fillType = DCAssetFillTypeAspectFill;
        this.volume  = 1.0f;
        this.index = 0;
        this.scalenorm = 0;
        this.inputscale = 0;
        this.weights = 1.0f;
        this.zRotation = 0;
        this.filter = null;
    }

    public TimeRange getTimeRange(){
        if(timeRange==null)
            timeRange = new TimeRange(0,120000000L);
        return timeRange;
    }

    public void setFilter(GPUImageFilter filter) {
        this.filter = filter;
    }

    public TimeRange setTimeRange(TimeRange timeRange){
        return this.timeRange = timeRange;
    }

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(@FloatRange(from=0.0f, to=1.0f) float volume) {
        this.volume = volume;
        if (assetWrapper != null) {
            assetWrapper.setVolume(this.volume);
        }
    }

    void setAssetWrapper(DCAssetWrapper assetWrapper) {
        this.assetWrapper = assetWrapper;
    }
}
