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
import com.dongci.sun.gpuimglibrary.player.DCPlayer;
import com.dongci.sun.gpuimglibrary.player.DCScene;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * create by ggq at 2018/5/31
 * 视频播放
 */
public class VideoPlayAcctivity extends AppCompatActivity {
    static final String TAG = "VideoPlayAcctivity";

    TextureView mPlayerView;
    DCPlayer mPlayer;
    String path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_videoplay);
        path = getIntent().getStringExtra("path");
        initOk();
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
                mPlayer = createPlayer(surface, width, height);
                mPlayer.setOnPositionUpdateListener(new DCPlayer.OnPositionUpdateListener() {
                    @Override
                    public void onPositionUpdate(DCPlayer player, float progress, long timeStamp) {
                        Log.d(TAG, "progress: " + progress);
//                        seekBar.setProgress((int)(100 * progress));
                    }
                });
//                mPlayer.play();
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
    private DCPlayer createPlayer(SurfaceTexture surface, int viewWidth, int viewHeight) {
        DCPlayer player = new DCPlayer(surface, 720, 960, viewWidth, viewHeight, 24, false);
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
        asset0.setTimeRange(new DCAsset.TimeRange(0L, 120000000L));
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

        List<DCAsset> assets = Arrays.asList(asset0,asset1,asset2,asset3, asset4, asset5);//, asset1, asset2, asset3, asset4, asset5

        DCScene scene = new DCScene();
        scene.assets = assets;
        return Arrays.asList(scene);
    }




}
