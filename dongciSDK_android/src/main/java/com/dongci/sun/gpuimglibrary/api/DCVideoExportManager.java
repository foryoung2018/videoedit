package com.dongci.sun.gpuimglibrary.api;


import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.dongci.sun.gpuimglibrary.api.listener.DCPlayerListener;
import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
import com.dongci.sun.gpuimglibrary.player.DCPlayer;
import com.dongci.sun.gpuimglibrary.player.DCScene;
import com.dongci.sun.gpuimglibrary.api.apiTest.KLog;

import java.io.IOException;
import java.util.List;

public class DCVideoExportManager {
    private String TAG = "DCVideoExportManager";
    DCPlayer mPlayer;

    DCMediaInfoExtractor.MediaInfo outputFileInfo;

    DCPlayerListener dcPlayerListener;

    /**
     * 给导出视频添加背景色
     * @param list
     * @param outputFileInfo
     * @param color
     * @param dcPlayerListener
     */
    public void export(List<DCScene> list, final DCMediaInfoExtractor.MediaInfo outputFileInfo,String color, final DCPlayerListener dcPlayerListener) {
        this.dcPlayerListener = dcPlayerListener;
        mPlayer = createPlayer(list, outputFileInfo);
        if(color!=null)
            mPlayer.setBackgroundColor(Color.parseColor(color));
        dcPlayerListener.onPrepared();
        mPlayer.setOnPositionUpdateListener(new DCPlayer.OnPositionUpdateListener() {
            @Override
            public void onPositionUpdate(DCPlayer player, float progress, long timeStamp) {
                KLog.d("export---onPosition-->" + progress);
                Message msg = new Message();
                msg.what = 0;
                msg.arg1 = (int) (100 * progress);
                handler.sendMessage(msg);
            }
        });
        mPlayer.setOnCompletionListener(new DCPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(DCPlayer player) {
                release();
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
        mPlayer.export(outputFileInfo);
    }

    /**
     * 视频导出初始化
     *
     * @param list 每一块视频模块
     */
    public void export(List<DCScene> list, final DCMediaInfoExtractor.MediaInfo outputFileInfo, final DCPlayerListener dcPlayerListener) {
        this.dcPlayerListener = dcPlayerListener;
        mPlayer = createPlayer(list, outputFileInfo);
//        mPlayer.setBackgroundColor(Color.parseColor("#ffffff"));
        dcPlayerListener.onPrepared();
        mPlayer.setOnPositionUpdateListener(new DCPlayer.OnPositionUpdateListener() {
            @Override
            public void onPositionUpdate(DCPlayer player, float progress, long timeStamp) {
                KLog.d("export---onPosition-->" + progress);
                Message msg = new Message();
                msg.what = 0;
                msg.arg1 = (int) (100 * progress);
                handler.sendMessage(msg);
            }
        });
        mPlayer.setOnCompletionListener(new DCPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(DCPlayer player) {
                release();
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
        mPlayer.export(outputFileInfo);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                dcPlayerListener.onProgress(msg.arg1);
            } else {
                dcPlayerListener.onComplete();
            }

        }
    };

    /**
     * 释放 视频导出占用的资源
     */
    private void release() {
        if (mPlayer != null)
            mPlayer.release();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private DCPlayer createPlayer(List<DCScene> list, DCMediaInfoExtractor.MediaInfo mediaInfo) {
        DCPlayer player = new DCPlayer((int) mediaInfo.videoInfo.width, (int) mediaInfo.videoInfo.height);
        player.setIsExport(true);
        player.setScenes(list);
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return player;
    }

}
