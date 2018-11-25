package com.wmlive.hhvideo.heihei.record.config;

import android.Manifest;
import android.graphics.RectF;

import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.FilterInfoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;


/**
 * Created by lsq on 8/25/2017.
 */

public class RecordSettingSDK {

    public static final float MIN_VIDEO_DURATION = 6000; //最小录制时长(毫秒为单位)
    public static final float MAX_VIDEO_DURATION = 6*60000; //最大录制时长(毫秒为单位)
    public static final float MAX_UPLOAD_VIDEO_DURATION = 6 * 60 * 1000; //最大单个上传时长(毫秒为单位)
    public static final int VIDEO_DATA_WIDTH = 750;//视频的JSON数据宽度
    public static final int VIDEO_DATA_HEIGHT = 1000;//视频的JSON数据高度
    public static final int VIDEO_WIDTH = 480;//视频的宽度
    public static final int VIDEO_HEIGHT = 640;//视频的高度
    public static final int VIDEO_WIDTH_MV = 720;//视频的宽度
    public static final int VIDEO_HEIGHT_MV = 960;//视频的高度

    public static final int PRODUCT_WIDTH = 720;//作品的宽度
    public static final int PRODUCT_HEIGHT = 960;//作品的高度

    public static final int LOCAL_UPLOAD_VIDEO_MAX = 960;//本地单个上传发布的作品宽高最大值

    public static final int VIDEO_FRAME_RATE = 24;//视频的帧率
    public static final int VIDEO_RECORD_BITRATE = 2500 * 1000;//8000000;//录制时的比特率
    public static final int VIDEO_PUBLISH_BITRATE_HEIGHT = 2500*1000;//5000000;//发布时的比特率(720 * 960)
    public static final int VIDEO_PUBLISH_BITRATE_HEIGHT_MV = 5500*1000;//5000000;//发布时的比特率(720 * 960)
    public static final int VIDEO_PUBLISH_BITRATE_LOW = 1200*1000;//3000000;//发布时的比特率(480 * 640)
    public static final int AUDIO_ENCODING_BITRATE = 96000; //声音码率
    public static final int AUDIO_SAMPLING_RATE = 44100; //声音采样率
    public static final boolean SET_AUDIO_RATE = false; //设置声音

    public static final short STEP_RECORD = 10;
    public static final short STEP_PUBLISH = 20;

    public static final int MAX_VOLUME = 300;
    public static final int WATERMARK_HEIGHT = 142; // 水印高度
    public static final int WATERMARK_HEIGHT_ID = 50; // 水印高度

    public static final int VIDEO_QUALITY_LOW = 1;
    public static final int VIDEO_QUALITY_HIGH = 2;

    public static final float RECORD_MUSIC_DELAY = 0.29f; // 录制音乐延迟

    private static float recordOriginalAudioDelay = 0.11f; // 非第一个原声延迟
    private static int cpuLevel = 2;

    public final static String[] SPEED_TITLE = new String[]{"极慢", "慢", "标准", "快", "极快"};

    public static String[] RECORD_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA};

//    public final static List<FilterInfoEntity> FILTER_LIST = new ArrayList<>();

//    static {
//        FILTER_LIST.add(new FilterInfoEntity(0, R.drawable.recorder_filter_0, R.string.filter_1));
//        int index = 5;
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_1, R.string.filter_2));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_2, R.string.filter_3));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_3, R.string.filter_4));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_4, R.string.filter_5));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_5, R.string.filter_6));
//        index++;
//        FILTER_LIST.add(new FilterInfoEntity(1, R.drawable.recorder_filter_6, R.string.filter_7));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_7, R.string.filter_8));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_8, R.string.filter_9));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_9, R.string.filter_10));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_10, R.string.filter_11));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_11, R.string.filter_12));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_12, R.string.filter_13));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_13, R.string.filter_14));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_14, R.string.filter_15));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_15, R.string.filter_16));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_16, R.string.filter_17));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_17, R.string.filter_18));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_18, R.string.filter_19));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_19, R.string.filter_20));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_20, R.string.filter_21));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_21, R.string.filter_22));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_22, R.string.filter_23));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_23, R.string.filter_24));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_24, R.string.filter_25));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_25, R.string.filter_26));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_26, R.string.filter_27));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_27, R.string.filter_28));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_28, R.string.filter_29));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_29, R.string.filter_30));

//    }

    public int videoWidth = VIDEO_WIDTH;
    public int videoHeight = VIDEO_HEIGHT;
    public int productWidth = PRODUCT_WIDTH;
    public int productHeight = PRODUCT_HEIGHT;
    public int videoRecordBitrate;//录制码率
    public int videoPublishBitrate;//发布码率
    public int videoFrameRate;//帧率
    public float minVideoDuration;//短视视最小持续时间(毫秒为单位)
    public float maxVideoDuration;//短视视最大持续时间(毫秒为单位)
    private int videoAspectRatioFitMode;
    private String watermarkPath;
    private RectF watermarkShowRectF;


    public RecordSettingSDK(int videoWidth, int videoHeight,
                            int videoRecordBitrate, int videoPublishBitrate,
                            int videoFrameRate, float minVideoDuration,
                            float maxVideoDuration) {
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoRecordBitrate = videoRecordBitrate;
        this.videoPublishBitrate = videoPublishBitrate;
        this.videoFrameRate = videoFrameRate;
        this.minVideoDuration = minVideoDuration;
        this.maxVideoDuration = maxVideoDuration;
        initVoiceDelay();
    }

    private void initVoiceDelay() {
        cpuLevel = DeviceUtils.getCPULevel();
        if (cpuLevel == 1) {
            recordOriginalAudioDelay = 0.10f;
        } else if (cpuLevel == 2) {
            recordOriginalAudioDelay = 0.11f;
        } else {
            recordOriginalAudioDelay = 0.12f;
        }
        KLog.i("**** * getCpuLevel " + cpuLevel);
    }

    public static float getVoiceDelay() {
        if (RecordUtil.hasHeadset()) {
            //使用耳机
            if (cpuLevel == 1) {
                recordOriginalAudioDelay = 0.10f;
            } else if (cpuLevel == 2) {
                recordOriginalAudioDelay = 0.11f;
            } else {
                recordOriginalAudioDelay = 0.12f;
            }
        } else {
            // 无耳机外放录制
            if (cpuLevel == 1) {
                recordOriginalAudioDelay = 0.05f;
            } else if (cpuLevel == 2) {
                recordOriginalAudioDelay = 0.06f;
            } else {
                recordOriginalAudioDelay = 0.07f;
            }
        }
        KLog.i("**** * getVoiceDelay " + recordOriginalAudioDelay);
        return recordOriginalAudioDelay;
    }

    /**
     * 默认配置
     *
     * @return
     */
    public static RecordSettingSDK defaultSetting() {
        return new RecordSettingSDK(
                VIDEO_WIDTH,
                VIDEO_HEIGHT,
                VIDEO_RECORD_BITRATE,
                VIDEO_PUBLISH_BITRATE_HEIGHT,
                VIDEO_FRAME_RATE,
                MIN_VIDEO_DURATION,
                MAX_VIDEO_DURATION);
    }

    /**
     * 初始化设置
     *
     * @return
     */
    public static RecordSettingSDK initSetting() {
        ProductEntity productEntity = RecordManager.get().getProductEntity();
        if (productEntity != null && productEntity.frameInfo != null
                && productEntity.frameInfo.video_quality == FrameInfo.VIDEO_QUALITY_HIGH) {
            return new RecordSettingSDK(
                    PRODUCT_WIDTH,
                    PRODUCT_HEIGHT,
                    VIDEO_RECORD_BITRATE,
                    VIDEO_PUBLISH_BITRATE_HEIGHT,
                    VIDEO_FRAME_RATE,
                    MIN_VIDEO_DURATION,
                    MAX_VIDEO_DURATION);
        }
        return RecordSettingSDK.defaultSetting();
    }

    public float getVideoRatio() {
//        return this.videoWidth * 1.0f / this.videoHeight;
        return this.VIDEO_DATA_WIDTH * 1.0f / this.VIDEO_DATA_HEIGHT;
    }

    public int getVideoAspectRatioFitMode() {
        return videoAspectRatioFitMode;
    }

//    public static int getFilterTitle(int index) {
//        return FILTER_LIST.get(getValidIndex(index)).titleId;
//    }
//
//    public static int getFilterDrawable(int index) {
//        return FILTER_LIST.get(getValidIndex(index)).drawableId;
//    }
//
//    public static int getFilterId(int index) {
//        return FILTER_LIST.get(getValidIndex(index)).filterId;
//    }
//
//    public static int getValidIndex(int index) {
//        return index < 0 ? 0 : (index >= FILTER_LIST.size() ? (FILTER_LIST.size() - 1) : index);
//    }

//    public static int getPreFilterId(int currentIndex) {
//        return getFilterId(currentIndex - 1);
//    }
//
//    public static int getNextFilterId(int currentIndex) {
//        return getFilterId(currentIndex + 1);
//    }
//
//    public static int getFilterIndex(int filterId) {
//        for (int i = 0; i < FILTER_LIST.size(); i++) {
//            if (FILTER_LIST.get(i) != null && FILTER_LIST.get(i).filterId == filterId) {
//                return i;
//            }
//        }
//        return FILTER_LIST.get(0).filterId;
//    }

    public String getWatermarkPath() {
        return watermarkPath;
    }

    public void setWatermarkPath(String watermarkPath) {
        this.watermarkPath = watermarkPath;
    }

    public RectF getWatermarkShowRectF() {
        return watermarkShowRectF;
    }

    public void setWatermarkShowRectF(RectF watermarkShowRectF) {
        this.watermarkShowRectF = watermarkShowRectF;
    }

    public float getMinDuration() {
        return minVideoDuration / 1000f;
    }

    public float getMaxDuration() {
        return maxVideoDuration / 1000f;
    }
}
