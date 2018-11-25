package com.wmlive.hhvideo.heihei.record.engine.model;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;

import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MAsset extends MediaObject{

    DCAsset dcAsset;


    @Override
    public DCAsset getAsset() {
        if(dcAsset==null)
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
        dcAsset.weights = weights;
        dcAsset.imagePaths=imagePaths;
        dcAsset.frameInterval=frameInterval;
        dcAsset.decorationName=decorationName;
        dcAsset.decorationMaskPath=decorationMaskPath;
        dcAsset.isBillboard = isBillboard;
        return dcAsset;
    }

    public MAsset(String framePath) {
        super.MediaObject(framePath);
    }

    public MAsset() {
    }

    public MAsset(String framePath, int type) {
        super.MediaObject(framePath,type);
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
        if(timeRange==null)
            timeRange = new DCAsset.TimeRange((long )start*1000,(long)duration*1000);
        else{
            timeRange.startTime = (long )start*1000;
            timeRange.duration  = (long)duration*1000;
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
}
