package com.wmlive.hhvideo.heihei.record.engine.config;

import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;


public class MVideoConfig extends aVideoConfig {

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    private  int rotate;
    public DCMediaInfoExtractor.MediaInfo getMediaInfo(){
        DCMediaInfoExtractor.MediaInfo mediaInfo = new DCMediaInfoExtractor.MediaInfo();
        mediaInfo.videoInfo = getVideoConfig();
        mediaInfo.audioInfo = getAudioConfig();
        mediaInfo.filePath = filePath;
        mediaInfo.durationUs = videoDuration;
        return mediaInfo;
    }

    private DCMediaInfoExtractor.VideoInfo getVideoConfig(){
        DCMediaInfoExtractor.VideoInfo videoInfo = new DCMediaInfoExtractor.VideoInfo();
        videoInfo.videoBitRate = videoEncodingBitRate;
//        videoInfo.totalFrames = 0;
//        videoInfo.rotation =rotate;
        videoInfo.degree = rotate; //旋转角度
        videoInfo.perFrameDurationUs = keyFrameTime;
        videoInfo.width = videoWidth;
        videoInfo.height = videoHeight;
        videoInfo.fps = fps;
        return videoInfo;
    }


    DCMediaInfoExtractor.AudioInfo audioInfo = null;

    public void setAudioInfo(DCMediaInfoExtractor.AudioInfo audioInf){
        this.audioInfo = audioInf;
    }

    public void setDefaultAudioInfo(){
        DCMediaInfoExtractor.AudioInfo audioInfo = new DCMediaInfoExtractor.AudioInfo();
        audioInfo.sampleRate = sampleRate;
        audioInfo.channelCount = channelCount;
        audioInfo.audioBitRate = audioBitRate;
        this.audioInfo = audioInfo;
    }

    private DCMediaInfoExtractor.AudioInfo getAudioConfig(){
//        DCMediaInfoExtractor.AudioInfo audioInfo = new DCMediaInfoExtractor.AudioInfo();
//        audioInfo.sampleRate = sampleRate;
//        audioInfo.channelCount = channelCount;
//        audioInfo.audioBitRate = audioBitRate;
        return audioInfo;
    }

}
