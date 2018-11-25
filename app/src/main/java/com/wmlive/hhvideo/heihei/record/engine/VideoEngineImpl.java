package com.wmlive.hhvideo.heihei.record.engine;

import com.dongci.sun.gpuimglibrary.api.listener.DCCutListener;
import com.dongci.sun.gpuimglibrary.common.CutEntity;
import com.wmlive.hhvideo.heihei.record.engine.config.VideoConfigImpl;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;

import java.util.ArrayList;
import java.util.List;

public interface VideoEngineImpl {
    /**
     * 合成视频
     */
    public void compose(final ArrayList videoList, final String outPath, VideoListener listener);

    /**
     * 连接音频，
     * @param videoList
     * @param outPath
     * @param listener
     */
    public void composeAudio(final ArrayList videoList, final String outPath, VideoListener listener);

    /**
     * 混合音频
     * @param videoList
     * @param outPath
     * @param listener
     */
    public void mixAudio(final ArrayList videoList, final String outPath, VideoListener listener);

    public void muxAudioVideo(final ArrayList videoList,final ArrayList audioList, final ArrayList outPaths, VideoListener listener);

    /**
     * 压缩视频
     */
    public void compress(String path, String outPath,final VideoListener listener);

    /**
     * 剪切视频
     */
    public void cutVideo(final String path, final long start, final long duration,final VideoListener listener);


    public void cutVideos(List<CutEntity> list, DCCutListener dcCutListener);

    public void cutAudioVideos(List<CutEntity> list, VideoListener dcCutListener);


    /**
     * 导出视频
     */
    void export(List <Scene> list, VideoConfigImpl videoConfig, ExportListener dcPlayerListener);

    void export(List<Scene> list, VideoConfigImpl videoConfig,String color, ExportListener dcPlayerListener);


    /**分离音视频*/
    public void splitVideoAudio(List<CutEntity> list,VideoListener videoListener);

    public void splitVideoAudio(String videos,String outVideos,String outAudios,VideoListener videoListener);

    public void setVolume(List<CutEntity> list, VideoListener videoListener);

}
