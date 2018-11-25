package com.wmlive.hhvideo.heihei.record.engine;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.dongci.sun.gpuimglibrary.api.DCPlayerManager;
import com.dongci.sun.gpuimglibrary.api.listener.DCPlayerListener;
import com.dongci.sun.gpuimglibrary.api.listener.DcPlayerCreateListener;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.DCAssetWrapper;
import com.example.loopback.DCLatencytestTool;
import com.example.loopback.DCLoopbackTool;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MScene;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.model.TranslateModel;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * sdk 接入的 对接
 */
public class PlayerEngine implements PlayerEngineImpl, PlayerContentImpl {

    DCPlayerManager dcPlayerManager;

    /**
     *
     */
    public PlayerEngine() {
        dcPlayerManager = getDcPlayerManager();
    }

    public DCPlayerManager getDcPlayerManager() {
        if (dcPlayerManager == null)
            dcPlayerManager = new DCPlayerManager();
        return dcPlayerManager;
    }

    public void build(final TextureView mPlayerView, final int w, final int h, final int fps, final boolean autoPlay, PlayerCreateListener playerCreateListener) {
        getDcPlayerManager().build(mPlayerView, w, h, fps, autoPlay, new DcPlayerCreateListener() {
            @Override
            public void playCreated() {
                playerCreateListener.playCreated();
            }
        });
    }

    public void build(final TextureView mPlayerView, final int w, final int h, final int fps, final boolean autoPlay) {
        getDcPlayerManager().build(mPlayerView, w, h, fps, autoPlay, null);
    }

    /**
     * 在trimActivity 使用了，
     *
     * @param mPlayerView
     */
    public void build(final TextureView mPlayerView, PlayerCreateListener playerCreateListener) {
        int width = mPlayerView.getWidth() == 0 ? 720 : mPlayerView.getWidth();
        int heigth = mPlayerView.getHeight() == 0 ? 960 : mPlayerView.getHeight();
        getDcPlayerManager().build(mPlayerView, width, heigth, 24, false, new DcPlayerCreateListener() {
            @Override
            public void playCreated() {
                playerCreateListener.playCreated();
            }
        });
    }

    /**
     * 录制结束后，暂停播放
     */
    @Override
    public void pause() {
        getDcPlayerManager().pause();
    }

    @Override
    public void release() {
        getDcPlayerManager().release();
    }

    @Override
    public int getWidth() {
        //获取视频的宽度
        return getDcPlayerManager().getWidth();
    }

    @Override
    public int getHeight() {
        //获取视频的高度
        return getDcPlayerManager().getHeight();
    }

    @Override
    public void setVisibility(int visibility) {
        getDcPlayerManager().getPlayerView().setVisibility(visibility);
    }


    @Override
    public void setLayoutParams(ViewGroup.MarginLayoutParams params) {
        if (getDcPlayerManager() != null && getDcPlayerManager().getPlayerView() != null)
            getDcPlayerManager().getPlayerView().setLayoutParams(params);
    }

    @Override
    public void start() {
        getDcPlayerManager().start();
    }

    @Override
    public void seekTo(float sec) {
        getDcPlayerManager().seekTo(sec);
    }

    /**
     * 微秒
     *
     * @param sec
     * @param autoPlay
     */
    public void seekToPlay(long sec, boolean autoPlay) {
        getDcPlayerManager().seekAndPlay(sec, autoPlay);
    }

    /**
     * <<<<<<< Updated upstream
     * 微秒
     *
     * @param sec
     * @param autoPlay
     */
    public void seekToPlay(int index, long sec, boolean autoPlay) {
        getDcPlayerManager().seekAndPlay(sec, autoPlay);
    }


    /**
     * =======
     * >>>>>>> Stashed changes
     * 获取当前播放的位置
     *
     * @return
     */
    @Override
    public long getCurrentPosition() {
        return getDcPlayerManager().getCurrentPosition();
    }

    /**
     * view 是否为空
     */
    public boolean isNull() {
        return getDcPlayerManager().isNull();
    }

    /**
     * 重置状态，以作再次播放
     */
    public void reset() {
        getDcPlayerManager().reset();
    }

    public void destroy() {
        getDcPlayerManager().destroy();
    }

    @Override
    public boolean isPlaying() {
        return getDcPlayerManager().isPlaying();
    }

    @Override
    public void setPreviewAspectRatio(float ratio) {

    }

    @Override
    public void setAspectRatioFitMode(int mode) {

    }

    @Override
    public void setBackgroundColor(int color) {
        getDcPlayerManager().setBackgroundColor(color);
    }

    @Override
    public void setOnPlaybackListener(PlayerListener playerListener) {
        getDcPlayerManager().setPlayListener(new DCPlayerListener() {
            @Override
            public void onPrepared() {
                playerListener.onPlayerPrepared();
            }

            @Override
            public void onProgress(float progress) {
                playerListener.onGetCurrentPosition(progress);
            }

            @Override
            public void onComplete() {
                playerListener.onPlayerCompletion();
            }

            @Override
            public boolean onPlayerError(int var1, int var2) {
                playerListener.onPlayerError(var1, var2);
                return false;
            }
        });
    }

    @Override
    public void setOnClickListener(View.OnClickListener clickListener) {
        if (getDcPlayerManager() != null && getDcPlayerManager().getPlayerView() != null)
            getDcPlayerManager().getPlayerView().setOnClickListener(clickListener);
    }

    @Override
    public void setAutoRepeat(boolean autoRepeat) {
        getDcPlayerManager().setAutoRepeate(autoRepeat);
    }

    @Override
    public long getDuration() {
        return getDcPlayerManager().getDuration();
    }

    /**
     * 获取开始截取的 时间点
     *
     * @return
     */


    public long getCutTime(Context context) {
        KLog.i("RecordActivitySdk1","cutTime-->"+getDcPlayerManager().startCutTime+" DCLatencytestTool.cutTime:"+DCLatencytestTool.cutTime);

        return getDcPlayerManager().startCutTime + DCLoopbackTool.cutTime*1000;//
    }

    public void setCutTime(long time) {
        getDcPlayerManager().startCutTime = time;
    }

    /**
     * 添加背景
     *
     * @param path
     * @return
     */
    private MediaObject addbg(String path, long duration) {
        MediaObject mediaObjectBg = new MAsset(path);
        mediaObjectBg.setSourceType(DCAsset.DCAssetTypeImage);

        mediaObjectBg.setShowRectF(new RectF(0, 0, 100, 100));
        mediaObjectBg.setRectInVideo(new RectF(0, 0, 1, 1));
        mediaObjectBg.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObjectBg.setTimeRange(0L, duration);
        mediaObjectBg.setStartTimeInScene(0L);


        return mediaObjectBg;
    }


    @Override
    public List<Scene> createScenes(List<? extends MediaObject> assets) {
        if (assets == null)
            return null;
        Scene scene = new MScene();
        scene.assets = assets;
        List<Scene> scenes = Arrays.asList(scene);
        return scenes;
    }

    /**
     * @param list
     */
    public void setMediaAndPrepare(List<? extends MediaObject> list) {
        List<Scene> scenes = createScenes(list);
        if (scenes != null)
            setScenceAndPrepare(scenes);
    }

    @Override
    public void setScenceAndPrepare(List<Scene> scenes) {
        getDcPlayerManager().setScenceAndPrepare(TranslateModel.scenceToDC(scenes));
    }

    /**
     * 获取档期那的播放资源
     *
     * @return
     */
    public List<DCAsset> getScene() {
        List<DCAssetWrapper> dcAssetWrappers = getDcPlayerManager().getAsset();
        if (dcAssetWrappers == null)
            return null;
        List<DCAsset> dcAssets = new ArrayList<DCAsset>(dcAssetWrappers.size());
        for (DCAssetWrapper dcAssetWrapper : dcAssetWrappers) {
            dcAssets.add(dcAssetWrapper.getAsset());
        }
        return dcAssets;
    }

    public void setVolume(int index,float volume){
        List<DCAssetWrapper> dcAssetWrappers = getDcPlayerManager().getAsset();
        if(dcAssetWrappers==null)
            return ;
        if(index>=dcAssetWrappers.size() ||index<0)
            return;
        dcAssetWrappers.get(0).getAsset().setVolume(volume);

    }


    @Override
    public void addScene(Scene scene) {
        List<Scene> scenes = new ArrayList<Scene>();
        scenes.add(scene);
        setScenceAndPrepare(scenes);
    }

    @Override
    public Scene createScene() {
        return new Scene();
    }

    @Override
    public void clearEffects() {
        getDcPlayerManager().clearEffects();
    }

    /**
     * 滤镜
     *
     * @param filterType
     */
    @Override
    public void setFilterType(int filterType) {

    }

    @Override
    public void setOriginalMixFactor(int value) {

    }

    public void setSnapShotResource(String filePath) {
        getDcPlayerManager().setSnapSource(filePath);
    }


    @Override
    public boolean getSnapShot(long time, String coverPath) {
        return getDcPlayerManager().getSnapShot(time, coverPath);
    }

    @Override
    public Bitmap getSnapShot(long time) {
        return getDcPlayerManager().getSnapShot(time);
    }


    @Override
    public Bitmap getSnapShot(long time, int width, int height) {
        Bitmap temp = getSnapShot(time);
        if (temp == null)
            return null;
        Bitmap bitmap = Bitmap.createScaledBitmap(temp, width, height, false);
        bitmap.setDensity(100);

        temp.recycle();
        return bitmap;
    }

    public String saveBitmap(String savePath, Bitmap mBitmap) {
        File filePic;
        try {
            KLog.d("getSnapShot-->");
            filePic = new File(savePath + System.currentTimeMillis() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    public void onDestroy() {
        getDcPlayerManager().destroy();
        dcPlayerManager = null;
    }

}
