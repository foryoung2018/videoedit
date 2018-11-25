package com.dongci.sun.gpuimglibrary.api.apiTest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.dongci.sun.gpuimglibrary.R;


import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
import com.dongci.sun.gpuimglibrary.player.DCPlayer;
import com.dongci.sun.gpuimglibrary.player.DCScene;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 视频导出
 */
public class VideoExportAct extends AppCompatActivity {

    static final String TAG = "VideoPlayAcctivity";

    TextureView mPlayerView;
    DCPlayer mPlayer;
    String path = "";
    String outPath = "";

    static {
       // System.loadLibrary("dcffmpeg-native");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_videoplay);
        path = getIntent().getStringExtra("path");
        initOk();
        createPath();
        //doEncoderThread();
    }

    private void createPath(){
        String[] temp = path.split("\\.");
        outPath = temp[0]+"export.mp4";
        File file = new File(outPath);
        if(file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 可以行的通的
     */
    private void initOk(){
        mPlayerView = (TextureView) findViewById(R.id.act_videoplay_view);
        mPlayerView.setVisibility(View.VISIBLE);
        mPlayerView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @SuppressLint("NewApi")
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mPlayer = createPlayer(surface);
                mPlayer.setOnPositionUpdateListener(new DCPlayer.OnPositionUpdateListener() {
                    @Override
                    public void onPositionUpdate(DCPlayer player, float progress, long timeStamp) {
                        Log.d(TAG, "progress: " + progress);
//                        seekBar.setProgress((int)(100 * progress));
                    }
                });
//                mPlayer.play();
//                onExport(null);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Log.d(TAG, "progress:-update " );
            }
        });

    }

    // TODO: 2018/6/8
    // DCPlayer test code
    // zhangxiao
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private DCPlayer createPlayer(SurfaceTexture surface) {
        //DCPlayer player = new DCPlayer(surface, 720, 1280, 24, false);
        DCPlayer player = new DCPlayer(720,960);
        player.setScenes(createScene());
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return player;
    }

    private List<DCScene> createScene() {
        int width = 720;
        int height = 960;
        final String path0 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
        final String path1 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
        final String path2 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
        final String path3 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
        final String path4 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
        final String path5 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";

        //                 w
        //      ------ ---- ---- ------
        //     |      |    |    |      |
        //     |   0  |    |    |   4  |
        //   h  ------|  2 |  3 |------
        //     |      |    |    |      |
        //     |   1  |    |    |   5  |
        //      ------ ---- ---- ------




        Log.d("tag","path-->"+((new File(path0)).exists())+path0);
        DCAsset asset0 = new DCAsset();
        asset0.filePath = path0;
        asset0.type = DCAsset.DCAssetTypeVideo;
        asset0.fillType = DCAsset.DCAssetFillTypeScaleToFit;
        asset0.cropRect = new RectF(0, 0, width, height);
        asset0.rectInVideo = new RectF(0, 0.0f, 0.3f, 0.4f);
        asset0.setTimeRange(new DCAsset.TimeRange(0L, 120000000L));//图片显示在视频的什么位置，
        asset0.startTimeInScene = 0L;

        DCAsset asset1 = new DCAsset();
        asset1.filePath = path1;
        asset1.type = DCAsset.DCAssetTypeVideo;
        asset1.fillType = DCAsset.DCAssetFillTypeScaleToFit;
        asset1.cropRect = new RectF(0, 0, width, height);
        asset1.rectInVideo = new RectF(0.0f, 0.4f, 0.3f, 0.8f);
        asset1.setTimeRange(new DCAsset.TimeRange(0L, 120000000L));
        asset1.startTimeInScene = 0L;

        DCAsset asset2 = new DCAsset();
        asset2.filePath = path2;
        asset2.type = DCAsset.DCAssetTypeVideo;
        asset2.fillType = DCAsset.DCAssetFillTypeScaleToFit;
        asset2.cropRect = new RectF(0, 0, width, height);
        asset2.rectInVideo = new RectF(0.3f, 0.0f, 0.5f, 1.0f);
        asset2.setTimeRange(new DCAsset.TimeRange(0L, 120000000L));
        asset2.startTimeInScene = 0L;

        DCAsset asset3 = new DCAsset();
        asset3.filePath = path3;
        asset3.type = DCAsset.DCAssetTypeVideo;
        asset3.fillType = DCAsset.DCAssetFillTypeScaleToFit;
        asset3.cropRect = new RectF(0, 0, width, height);
        asset3.rectInVideo = new RectF(0.5f, 0, 0.7f, 1.0f);
        asset3.setTimeRange(new DCAsset.TimeRange(0L, 120000000L));
        asset3.startTimeInScene = 0L;

        DCAsset asset4 = new DCAsset();
        asset4.filePath = path4;
        asset4.type = DCAsset.DCAssetTypeVideo;
        asset4.fillType = DCAsset.DCAssetFillTypeScaleToFit;
        asset4.cropRect = new RectF(0, 0, width, height);
        asset4.rectInVideo = new RectF(0.7f, 0.0f, 1.0f, 0.5f);
        asset4.setTimeRange(new DCAsset.TimeRange(0L, 120000000L));
        asset4.startTimeInScene = 0L;

        DCAsset asset5 = new DCAsset();
        asset5.filePath = path5;
        asset5.type = DCAsset.DCAssetTypeVideo;
        asset5.fillType = DCAsset.DCAssetFillTypeScaleToFit;
        asset5.cropRect = new RectF(0, 0, width, 720);
        asset5.rectInVideo = new RectF(0.7f, 0.5f, 1.0f, 1.0f);
        asset5.setTimeRange(new DCAsset.TimeRange(0L, 120000000L));
        asset5.startTimeInScene = 0L;

        List<DCAsset> assets = Arrays.asList(asset0);//, asset1, asset2, asset3, asset4, asset5

        DCScene scene = new DCScene();
        scene.assets = assets;
        return Arrays.asList(scene);
    }

    public void onExport(View view){
//        final String path = Environment.getExternalStorageDirectory() + "/Movies/export.mp4";
        DCMediaInfoExtractor.MediaInfo outputFileInfo = new DCMediaInfoExtractor.MediaInfo();
        outputFileInfo.videoInfo.width = 720;
        outputFileInfo.videoInfo.height = 960;
        outputFileInfo.videoInfo.videoBitRate = (int)(2.5 * 1000 * 1000);//mediaInfo.videoBitRate;//(int)(1.5 * 1000 * 1000);
        outputFileInfo.videoInfo.fps = 24;//mediaInfo.frameRate;//24;
        outputFileInfo.audioInfo.audioBitRate = 256000;//mediaInfo.audioBitRate;//64000;
        outputFileInfo.audioInfo.sampleRate = 44100;//mediaInfo.sampleRate;//44100;
        outputFileInfo.audioInfo.channelCount = 1;//mediaInfo.channelCount;//1;
        outputFileInfo.filePath = outPath;
        mPlayer.export(outputFileInfo);
    }

    private void doThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

//    private FfmpegProcessor mProcessor = FfmpegProcessor.createFfmpegProcessor();

    public void testGetImageFromVideo() {
//        mProcessor.videoCopy("/sdcard/Codec/record/d2.mp4","/sdcard/Codec/record/outout.mp4");
    }


    private void doEncoderThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                testGetImageFromVideo();
            }
        }).start();
    }

}
