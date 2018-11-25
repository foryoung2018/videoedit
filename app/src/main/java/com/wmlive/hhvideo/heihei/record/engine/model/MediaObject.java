package com.wmlive.hhvideo.heihei.record.engine.model;

import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.content.ExportContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;


public abstract class MediaObject<T> implements MediaObjectImpl, Parcelable {

    public void MediaObject(String framePath) {
        filePath = framePath;
        initDefault(MediaObjectTypeVideo);
        initPath(filePath);
    }

    /**
     * 指定类型
     *
     * @param framePath
     * @param type
     */
    public void MediaObject(String framePath, int type) {
        filePath = framePath;
        initDefault(type);
//        initPath(filePath);
    }

    public void MediaObject(int type) {

    }


    public void MediaObject() {

    }

    public T getAsset() {
        return null;
    }

    public void setShowRectF(RectF rectF) {
        cropRect = rectF;
    }

    @Override
    public void setAspectRatioFitMode(int type) {
        fillType = type;
    }

    /**
     * 设置播放速度
     *
     * @param speed
     */
    public void setSpeed(float speed) {

    }

    /**
     * 是否静音
     *
     * @param mute
     */
    public void setAudioMute(boolean mute) {
        if (mute)
            volume = 0f;
        else
            volume = 0.5f;
    }

    public void setMixFactor(int originalMix) {

    }

    public long getDuration() {
        if (timeRange != null)
            return timeRange.duration;
        return 0;
    }

    @Override
    public void setRectInVideo(RectF rectf) {
        rectInVideo = rectf;
    }

    @Override
    public void setStartTimeInScene(long mStartTimeInScene) {
        startTimeInScene = mStartTimeInScene;
    }

    @Override
    public void setTimeRange(long start, long duration) {
        if (timeRange == null)
            timeRange = new DCAsset.TimeRange(start, duration);
        else {
            timeRange.startTime = start;
            timeRange.duration = duration;
        }
    }


    @Override
    public void setVolume(float mVolume) {
        volume = mVolume;
    }

    @Override
    public void setWeights(float weights) {
        this.weights = weights;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void setScaleNorm(float scaleNorm) {
        this.scalenorm = scaleNorm;
    }

    @Override
    public void setInputScale(float inputScale) {
        this.inputscale = inputScale;
    }


    protected String filePath;

    public void setCropRect(RectF cropRect) {
        this.cropRect = cropRect;
    }

    @Override
    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    @Override
    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTimeRange(DCAsset.TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public void setScalenorm(float scalenorm) {
        this.scalenorm = scalenorm;
    }

    public void setInputscale(float inputscale) {
        this.inputscale = inputscale;
    }

    protected RectF cropRect;
    protected RectF rectInVideo;

    public void setFrameInterval(long frameInterval) {
        this.frameInterval = frameInterval;
    }

    protected long frameInterval;

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    protected List<String> imagePaths;
    // us
    protected long startTimeInScene;
    protected int videoWidth;
    protected int videoHeight;

    public void setGpuImageFilter(GPUImageFilter gpuImageFilter) {
        this.gpuImageFilter = gpuImageFilter;
    }

    protected GPUImageFilter gpuImageFilter;

    private final static class TimeRange {
        // us
        public long startTime;
        public long duration;

        public TimeRange(long startTime, long duration) {
            this.startTime = startTime;
            this.duration = duration;
        }
    }

    // asset type
    public static final int MediaObjectTypeVideo = 0;
    public static final int MediaObjectTypeAudio = 1;
    public static final int MediaObjectTypeImage = 2;

    @IntDef({MediaObjectTypeVideo, MediaObjectTypeAudio, MediaObjectTypeImage})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCAssetType {
    }

    // asset fill type
    public static final int FillTypeScaleToFit = 0;
    public static final int FillTypeAspectFill = 1;

    @IntDef({FillTypeScaleToFit, FillTypeAspectFill})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCAssetFillType {
    }

    protected @DCAsset.DCAssetType
    int type;

    public void setDecorationName(String decorationName) {
        this.decorationName = decorationName;
    }

    protected String decorationName;

    public void setDecorationMaskPath(String decorationMaskPath) {
        this.decorationMaskPath = decorationMaskPath;
    }

    protected String decorationMaskPath;

    public void setFillType(int fillType) {
        this.fillType = fillType;
    }

    protected @DCAsset.DCAssetFillType
    int fillType;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setSourceType(int mType) {
        type = mType;
    }

    public int getType() {
        return type;
    }

    public int getFillType() {
        return fillType;
    }

    public RectF getCropRect() {
        return cropRect;
    }

    public RectF getRectInVideo() {
        return rectInVideo;
    }

    public long getStartTimeInScene() {
        return startTimeInScene;
    }

    public DCAsset.TimeRange getTimeRange() {
        if (timeRange == null) {
//            if(type==DCAsset.DCAssetTypeVideo){
            timeRange = new DCAsset.TimeRange(0, VideoUtils.getVideoLength(filePath));
//            }else{
//                timeRange = new DCAsset.TimeRange(0,6000000);
//            }
        }
        return timeRange;
    }

    public float getVolume() {
        return volume;
    }

    public float getWeights() {
        return weights;
    }

    public int getIndex() {
        return index;
    }

    public float getScalenorm() {
        return scalenorm;
    }

    public float getInputscale() {
        return inputscale;
    }

    protected DCAsset.TimeRange timeRange;
    protected @FloatRange(from = 0.0f, to = 1.0f)
    float volume = 1.0f;
    /**
     * 1开始
     */
    public int assetId;

    protected float weights;
    protected int index;
    float scalenorm;
    float inputscale;
    //4.0
    public boolean isBillboard;

    public void initDefault(int type) {
        this.type = type;//MediaObjectTypeVideo;// 默认视频
        this.fillType = FillTypeScaleToFit;// 默认缩放适合
        this.volume = 1.0f;
        this.index = 0;
        this.scalenorm = 0;
        this.inputscale = 0;
        this.weights = 1.0f;
        videoHeight = 960;//1920;//640;
        videoWidth = 720;//1080;//480;
        rectInVideo = new RectF(0, 0, 1, 1);
        cropRect = new RectF(0, 0, videoWidth, videoHeight);
    }

    private void initPath(String path) {
        if (timeRange != null) {
            timeRange.duration = VideoUtils.getVideoLength(filePath);
        }
        aVideoConfig config = VideoUtils.getMediaInfor(filePath);
        cropRect = new RectF(0, 0, config.getVideoWidth(), config.getVideoHeight());
    }

    //兼容之前版本 写的方法
    public long getSpeed() {
        return 1;
    }

    public int getWidth() {
        if (filePath != null) {
            if (type == MediaObjectTypeImage) {
                BitmapFactory.Options options = ExportContentFactory.getImgSize(filePath);
                return options.outWidth;
            } else {
                aVideoConfig videoConfig = VideoUtils.getMediaInfor(filePath);
                return videoConfig.videoWidth;
            }

        }
        return videoWidth;
    }

    public int getHeight() {
        if (filePath != null) {
            if (type == MediaObjectTypeImage) {
                BitmapFactory.Options options = ExportContentFactory.getImgSize(filePath);
                return options.outHeight;
            } else {
                aVideoConfig videoConfig = VideoUtils.getMediaInfor(filePath);
                return videoConfig.videoHeight;
            }
        }
        return videoHeight;
    }

    @Override
    public String toString() {
        return "MediaObject{" +
                "filePath='" + filePath + '\'' +
                ", cropRect=" + cropRect +
                ", rectInVideo=" + rectInVideo +
                ", frameInterval=" + frameInterval +
                ", imagePaths=" + imagePaths +
                ", startTimeInScene=" + startTimeInScene +
                ", videoWidth=" + videoWidth +
                ", videoHeight=" + videoHeight +
                ", type=" + type +
                ", fillType=" + fillType +
                ", timeRange=" + timeRange +
                ", volume=" + volume +
                ", assetId=" + assetId +
                ", weights=" + weights +
                ", index=" + index +
                ", scalenorm=" + scalenorm +
                ", inputscale=" + inputscale +
                '}';
    }
}
