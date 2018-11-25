package com.dongci.sun.gpuimglibrary.player;

import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by zhangxiao on 2018/5/31.
 *
 */

public class DCAsset {

    // asset type
    public static final int DCAssetTypeVideo = 0;
    public static final int DCAssetTypeAudio = 1;
    public static final int DCAssetTypeImage = 2;
    public static final int DCAssetTypeImages = 3;
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

    public final static class TimeRange {
        // us
        public long startTime;
        public long duration;

        public TimeRange(long startTime, long duration) {
            this.startTime = startTime;
            this.duration = duration;
        }

        public boolean containsTime(long time) {
            return time >= startTime && time <= startTime + duration;
        }

        public long endTime() {
            return startTime + duration;
        }

        public String toString(){
            return "TimeRange:-start:"+startTime+"duration->"+duration;
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

    public DCAsset() {
        this.type = DCAssetTypeVideo;
        this.fillType = DCAssetFillTypeAspectFill;
        this.volume  = 1.0f;
        this.index = 0;
        this.scalenorm = 0;
        this.inputscale = 0;
        this.weights = 1.0f;
    }

    public TimeRange getTimeRange(){
        if(timeRange==null)
            timeRange = new TimeRange(0,120000000L);
        return timeRange;
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
