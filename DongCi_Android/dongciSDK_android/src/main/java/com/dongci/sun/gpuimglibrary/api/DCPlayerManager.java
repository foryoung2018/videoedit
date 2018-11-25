package com.dongci.sun.gpuimglibrary.api;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.dongci.sun.gpuimglibrary.api.listener.DCPlayerListener;
import com.dongci.sun.gpuimglibrary.api.listener.DcPlayerCreateListener;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.DCAssetWrapper;
import com.dongci.sun.gpuimglibrary.player.DCPlayer;
import com.dongci.sun.gpuimglibrary.player.DCScene;
import com.dongci.sun.gpuimglibrary.common.SLVideoTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DCPlayerManager {

    private final String TAG = "DCPlayerManager";
    DCPlayer mPlayer;
    //    private List<DCScene> list;
    private TextureView playerView;
    private MediaMetadataRetriever mediaMetadataRetriever = null;
    private volatile boolean mIsSetScenes = false;
    /**
     * 播放状态的 回调
     */
    DCPlayerListener dcPlayerListener;

    /**
     * 视频导出初始化
     *
     * @param mPlayerView 展示视频的view
     */
    public void build(final TextureView mPlayerView, final int w, final int h, final int fps, final boolean autoPlay, final DcPlayerCreateListener dcPlayerCreateListener) {
        playerView = mPlayerView;
        mPlayerView.setVisibility(View.VISIBLE);
        Log.d(TAG, "build-Tv->pre" + mPlayerView);
        mPlayerView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @SuppressLint("NewApi")
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.i(TAG, "播放器大小: 没有视频!" + w + "heigth:" + h);

                mPlayer = new DCPlayer(surface, w, h, width, height, fps, autoPlay);
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (dcPlayerCreateListener != null)//去设置播放数据
                    dcPlayerCreateListener.playCreated();

                if (autoPlay)
                    start();
                Log.d(TAG, "build-Tv->onSurfaceTextureAvailable->" + autoPlay + "Visiable:>" + (mPlayerView.getVisibility() == View.VISIBLE));
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (mPlayer != null) {
                    Log.d(TAG, "build-Tv->onSurfaceTextureSizeChanged" + width + "heigth:" + height + mPlayer);
                    mPlayer.resize(surface, width, height);
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.d(TAG, "build-Tv->onSurfaceTextureDestroyed" + mPlayerView);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    /**
     * 获取当前播放器的 播放控件
     *
     * @return
     */
    public TextureView getPlayerView() {
        return playerView;
    }

    public void setAutoRepeate(boolean autoRepeate) {
        if (mPlayer != null)
            mPlayer.setRepeat(autoRepeate);
    }

    /**
     * 录制结束后，暂停播放
     */
    public void pause() {
        mPlayer.pause();
    }

    public void start() {
        startThread = false;
        startThread1 = false;
        progressTemp = 0;
        Log.e("sun", "AA==---playTime--FramAvailable->，start()--play-->" + startThread);
        mPlayer.play();
    }

    public void destroy() {
        if (mPlayer != null) {
            mIsSetScenes = false;
            mPlayer.release();
        }

        mPlayer = null;
        playerView = null;
    }

    public void seekTo(float sec) {
        if (mPlayer != null)
            mPlayer.seekTo((long) (sec));
    }

    /**
     * 微秒
     *
     * @param time
     */
    public void seekAndPlay(long time, boolean autoPlay) {
        startThread = false;
        startThread1 = false;
        progressTemp = 0;
        if (mPlayer != null)
            mPlayer.seekTo(time, autoPlay);
    }

    /**
     * view 是否为空
     */
    public boolean isNull() {
        return playerView == null || mPlayer == null;// ||mPlayer.getSceneWrapper()==null
    }


    public int getWidth() {
        if (mPlayer == null)
            return 0;
        return mPlayer.getVideoWidth();
    }


    public int getHeight() {
        if (mPlayer == null)
            return 0;
        return mPlayer.getVideoHeight();
    }

    /**
     * 重置状态，以作再次播放
     */
    public void reset() {
        if (mPlayer != null) {
            mIsSetScenes = false;
            mPlayer.reset();
        }

    }

    public void release() {
        if (mPlayer != null) {
            mIsSetScenes = false;
            mPlayer.release();
        }

        if (mediaMetadataRetriever != null) {
            mediaMetadataRetriever.release();
            mediaMetadataRetriever = null;
        }
    }

    public long getDuration() {
        Log.i("DcPlayerManager", "getDuration--player is " + mPlayer);
        if (mPlayer != null)
            return mPlayer.getDuration();
        return 0;
    }

    public boolean isPlaying() {
        if (mPlayer != null)
            return mPlayer.isPlaying();
        return false;
    }

    public void setPlayListener(final DCPlayerListener dcPlayerListener) {
        if (mPlayer == null)
            return;
        mPlayer.setOnPreparedListener(new DCPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(DCPlayer player) {
                Log.d(TAG, "setPlayListener--onPlayerPrepared" + player);
                dcPlayerListener.onPrepared();
            }
        });
        mPlayer.setOnPositionUpdateListener(new DCPlayer.OnPositionUpdateListener() {
            @Override
            public void onPositionUpdate(DCPlayer player, float progress, long timeStamp) {
//                Log.d(TAG, "progress: " + progress);
                dcPlayerListener.onProgress(progress);
            }
        });
        mPlayer.setOnCompletionListener(new DCPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(DCPlayer player) {
                Log.d(TAG, "setOnCompletionListener: ");
                dcPlayerListener.onComplete();
            }
        });
    }

    /**
     * 设置 数据框 准备播放器
     */
    public void setScenceAndPrepare(List<DCScene> list) {
        if (mPlayer == null)
            return;
        if (!mIsSetScenes) {
            mPlayer.setScenes(list);
            mIsSetScenes = true;
        } else {
            mPlayer.setTimeRange(list);
        }

        if (mediaMetadataRetriever != null) {
            DCAsset dcAsset = list.get(0).assets.get(0);
            if (dcAsset.type != DCAsset.DCAssetTypeImage&&dcAsset.type != DCAsset.DCAssetTypeImages)
                mediaMetadataRetriever.setDataSource(dcAsset.filePath);
        }
        try {
            mPlayer.prepare();
            Log.d("DCplayerManager", "setScenceAndPrepare===onPlayerPrepared====preparse" + list.get(0).assets.get(0).filePath);
            mPlayer.setOnPositionUpdateListener(new DCPlayer.OnPositionUpdateListener() {
                @Override
                public void onPositionUpdate(DCPlayer player, float progress, long timeStamp) {
                    Log.d("DCplayerManager", "onPositionUpdate" + progress);
                    if (timeStamp > 0) {
                        getPlayTime(player, progress, timeStamp);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public List<DCAssetWrapper> getAsset() {
        if (mPlayer != null) {
            return mPlayer.getAssetWrappers();
        }
        return null;
    }

    /**
     * 当前进度
     */
    private float progressTemp;
    private boolean startThread = false;
    private boolean startThread1 = false;
    private long timeStemp = 0;
    //播放时，获取到的 已经播放时间戳
    long playTimeStamp = 0;
    long playCurrentTime = 0;

    public long startCutTime = 0;


    private void getPlayTime(DCPlayer player, float progress, long timeStamp) {

        if (SLVideoTool.startRecordTime > 0 && SLVideoTool.playerStartPlayTime > 0) {

            long currentTime = System.nanoTime();
            long t0 = currentTime - SLVideoTool.startRecordTime;
            long t1 = currentTime - SLVideoTool.lastRecordTime;
            startCutTime = t0 - t1;

            Log.e("recordActivitySdk", "currentTime = " + startCutTime + ", t0 = " + t0 + ", t1 = " + t1 + ", timestamp = " + timeStamp * 1000);

            SLVideoTool.startRecordTime = 0;
            SLVideoTool.playerStartPlayTime = 0;
        }

    }

    public List<DCScene> createScenes(List<DCAsset> assets) {
        DCScene scene = new DCScene();
        scene.assets = assets;
        List<DCScene> scenes = Arrays.asList(scene);
        return scenes;
    }

    /**
     * 去掉 各种滤镜，效果
     */
    public void clearEffects() {

    }

    /**
     * 获取当前播放的进度
     *
     * @return
     */
    public long getCurrentPosition() {
        if (mPlayer != null)
            return mPlayer.getCurrentTime();
        else
            return 0;
    }

    /**
     * 设置视频资源文件地址
     *
     * @param filePath
     */
    public void setSnapSource(String filePath) {
        if (mediaMetadataRetriever == null) {
            mediaMetadataRetriever = new MediaMetadataRetriever();
        }
        mediaMetadataRetriever.setDataSource(filePath);
    }

    public boolean getSnapShot(long time, String coverPath) {

        if (mediaMetadataRetriever == null) {
            Log.e(TAG, "mediaMetadataRetriever is null");
            return false;
        }
        //Bitmap  bitmap = mVideoRetriever.getFrameAtTime(time);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(time);
        if (bitmap == null) {
            return false;
        }
        File file = null;

        FileOutputStream out = null;
        try {
            file = new File(coverPath);
            file.createNewFile();
            out = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    public Bitmap getSnapShot(long time) {
        if (mediaMetadataRetriever == null) {
            Log.e(TAG, "mediaMetadataRetriever is null");
            return null;
        }
        return mediaMetadataRetriever.getFrameAtTime(time);
    }

    public boolean getSnapShot(long time, Bitmap bitmap) {

        return false;
    }


}
