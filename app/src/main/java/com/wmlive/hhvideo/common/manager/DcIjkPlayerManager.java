package com.wmlive.hhvideo.common.manager;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.wmlive.hhvideo.common.VideoProxy;
import com.wmlive.hhvideo.dcijkplayer.AbsIjkPlayListener;
import com.wmlive.hhvideo.dcijkplayer.DcIjkPlayer;
import com.wmlive.hhvideo.dcijkplayer.IjkPlayListener;
import com.wmlive.hhvideo.dcijkplayer.L;
import com.wmlive.hhvideo.dcijkplayer.widget.media.TextureRenderView;
import com.wmlive.hhvideo.DCApplication;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.main.UserBehavior;

import com.wmlive.hhvideo.heihei.beans.opus.VideoPlayEntity;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.networklib.util.EventHelper;

import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by lsq on 7/5/2017.
 * 单例的IjkPlayer
 */

public class DcIjkPlayerManager {
    private DcIjkPlayer dcIjkPlayer;
    private Context context;
    private volatile int pageId = 0;
    private volatile long videoId = 0;
    private SparseArray<IjkPlayListener> listenerArray;
    private boolean isDebug;
    public static final int START_PLAY_DELAY = DeviceUtils.isMtkCpu() ? 140 : 70;


    private static DcIjkPlayerManager manager = new DcIjkPlayerManager();

    private DcIjkPlayerManager() {
        init(DCApplication.getDCApp(), GlobalParams.Config.IS_DEBUG);
    }

    public static DcIjkPlayerManager get() {
        return manager;
    }


    /**
     * 初始化IjkPlayer播放器
     *
     * @param context
     */
    public void init(Context context, boolean isDebug) {
        this.context = context.getApplicationContext();
        this.isDebug = isDebug;
        listenerArray = new SparseArray<>();
        L.i("====初始化DcIjkPlayer");
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);//日志级别
        } catch (Throwable e) {
            L.i("====初始化DcIjkPlayer loadLibraries:" + e.getLocalizedMessage());
        }
        initPlayer();
    }

    private void initPlayer() {
        if (context != null) {
            dcIjkPlayer = new DcIjkPlayer(context.getApplicationContext());
            dcIjkPlayer.setDebug(isDebug);
            dcIjkPlayer.setPlayListener(listener);
            dcIjkPlayer.setCanLoop(true);
        }
    }

    public DcIjkPlayer getPlayer() {
        if (dcIjkPlayer == null) {
            initPlayer();
        }
        return dcIjkPlayer;
    }


    public void setPlayerAlpha(float alpha) {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.setAlpha(alpha);
//            dcIjkPlayer.clearAnimation();
//            dcIjkPlayer.startAnimation(alphaAnim);
        }
    }

    public int getPageId() {
        return pageId;
    }

    /**
     * 设置刷新当前播放位置的时间间隔，默认是1s
     *
     * @param refreshInterval
     */
    public void setRefreshInterval(int refreshInterval) {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.setRefreshInterval(refreshInterval);
        }
    }

    /**
     * 设置是否可缓存
     *
     * @param canCache
     */
    public void setCacheable(boolean canCache) {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.setCacheable(canCache);
        }
    }

    public void setCanLoop(boolean canLoop) {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.setCanLoop(canLoop);
        }
    }

    /**
     * 点击播放器暂停，再次点击播放
     *
     * @param clickPause
     */
    public void setClickPause(boolean clickPause) {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.setClickPause(clickPause);
        }
    }


    public void setNeedPause(boolean needPause) {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.setNeedPause(needPause);
        }
    }

    public void attachPlayer(ViewGroup container, OnPlayerDetachListener listener) {
        attachPlayer(container, true, listener);
    }

    public void attachPlayer(ViewGroup container, boolean needPause, OnPlayerDetachListener listener) {
        if (dcIjkPlayer == null) {
            initPlayer();
        } else {
            if (needPause) {
                dcIjkPlayer.pausePlay();
                dcIjkPlayer.setAlpha(0f);
            }
        }
        if (dcIjkPlayer != null) {
            ViewGroup viewGroup = (ViewGroup) dcIjkPlayer.getParent();
            dcIjkPlayer.setNeedPause(needPause);
            if (viewGroup != null) {
                viewGroup.removeView(dcIjkPlayer);
                if (listener != null) {
                    listener.onDetach();
                }
            }
            container.removeAllViews();
            container.addView(dcIjkPlayer);
            if (!needPause) {
                dcIjkPlayer.setAlpha(1f);
            }
            KLog.d("ggqIJK", "container.width==" + container.getWidth() + "  container.height==" + container.getHeight());
            KLog.d("ggqIJK", "dcIjkPlayer.getWidth()==" + dcIjkPlayer.getWidth() + "  dcIjkPlayer.getHeight()==" + dcIjkPlayer.getHeight());
            KLog.d("ggqIJK", "屏幕宽" + ScreenUtil.getWidth(context) + "  屏幕高" + ScreenUtil.getHeight(context));
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return dcIjkPlayer != null && dcIjkPlayer.isPlaying();
    }

    /**
     * 获取当前播放的url
     *
     * @return
     */
    public String getUrl() {
        if (dcIjkPlayer != null) {
            return dcIjkPlayer.getUrl();
        }
        return null;
    }


    /**
     * 开始播放
     */
    public void startPlay() {
        if (dcIjkPlayer == null) {
            initPlayer();
        }
        if (dcIjkPlayer != null) {
            dcIjkPlayer.startPlay();
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.pausePlay();
            KLog.i("====ijkmanager==pausePlay");
        }
    }

    public void resetUrl() {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.resetUrl();
        }
    }

    /**
     * 设置视频的url
     *
     * @param pageId  随机值，用于区分是哪个页面
     * @param videoId 视频的id
     * @param url     视频Url
     */
    public int videoID;

    public void setVideoUrl(int pageId, long videoId, String url, IjkPlayListener listener) {
        getPreVideoPlayingDuring(false);
        this.pageId = pageId;
        this.videoID = videoID;
        setPlayListener(videoId, pageId, listener);
        KLog.d("======setVideoUrl,pageId:" + pageId + " url:" + url + " videoId==" + videoId);
        if (dcIjkPlayer == null) {
            initPlayer();
        }
        if (dcIjkPlayer != null) {
            dcIjkPlayer.setVideoUrl(videoId, url);
        }
    }

    /**
     * 恢复播放
     *
     * @param pageId
     */
    public void resumePlay(long videoId, int pageId, IjkPlayListener listener) {
        setPlayListener(videoId, pageId, listener);
        this.pageId = pageId;
        this.videoId = videoId;
        KLog.i("======resumePlay,pageId:" + pageId);
        if (dcIjkPlayer == null) {
            initPlayer();
        }
        if (dcIjkPlayer != null) {
            dcIjkPlayer.resumePlay();
        }
    }

    public void setPlayListener(long videoId, int randomId, IjkPlayListener ijkPlayListener) {
        pageId = randomId;
        this.videoId = videoId;
        if (listenerArray != null) {
            listenerArray.put(randomId, ijkPlayListener);
        }
    }

    /**
     * 停止播放，此方法会释放掉IMediaPlayer
     */
    public void stopPlay() {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.stopPlay();
            KLog.d("观看行为数据", " stopPlay  videoID==" + videoID + "  dcIjkPlayer.getCurrentPosition()" + dcIjkPlayer.getCurrentPosition());
            EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(videoID,
                    dcIjkPlayer.getCurrentPosition(), 0, 0));
        }
    }

    /**
     * 释放播放器
     *
     * @param cleartargetstate
     */
    public void releasePlayer(final boolean cleartargetstate) {
        if (dcIjkPlayer != null && dcIjkPlayer.getParent() != null) {
            KLog.d("观看行为数据", " releasePlayer  videoID==" + videoID + "  dcIjkPlayer.getCurrentPosition()" + dcIjkPlayer.getCurrentPosition());
            EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(videoID,
                    dcIjkPlayer.getCurrentPosition(), 0, 0));
            ViewGroup viewGroup = (ViewGroup) dcIjkPlayer.getParent();
            if (viewGroup != null) {
                viewGroup.removeAllViews();
                L.i("====从父布局移除Player：" + viewGroup);
            }
        }
        removeListener(pageId);
        L.i("====释放videoType：" + pageId);
        pageId = 0;
        if (dcIjkPlayer != null) {
            dcIjkPlayer.resetUrl();
            dcIjkPlayer.releasePlayer(cleartargetstate);
            dcIjkPlayer = null;

        }
        VideoProxy.get().stopCacheFile();
    }

    public void setRotation(int rotation) {
        if (dcIjkPlayer != null && dcIjkPlayer.getRenderView() != null) {
            if (dcIjkPlayer.getRenderView() instanceof TextureRenderView) {
                TextureRenderView renderView = (TextureRenderView) dcIjkPlayer.getRenderView();

                renderView.setVideoRotation(rotation);
            }
        }

    }

    public void removeListener(int pageId) {
        if (listenerArray != null) {
            listenerArray.put(pageId, null);
            listenerArray.remove(pageId);
            listenerArray.size();
        }
    }

    /**
     * 挂起播放器，此方法会释放掉IMediaPlayer，但是不会清除播放状态
     */
    public void suspendPlay() {
        if (dcIjkPlayer != null) {
            dcIjkPlayer.suspendPlay();
        }
    }


    /**
     * 获取视频的播放信息
     *
     * @return
     */
    public VideoPlayEntity getPreVideoPlayingDuring(boolean isComplete) {
        VideoPlayEntity entity = new VideoPlayEntity();
        if (dcIjkPlayer != null) {
            dcIjkPlayer.sendCacheDate();
            entity.during = dcIjkPlayer.getDuration();
            entity.current = isComplete ? entity.during : dcIjkPlayer.getCurrentPosition();
            entity.videoId = dcIjkPlayer.currentId;
            entity.url = dcIjkPlayer.getUrl();
            if (dcIjkPlayer.getCurrentPosition() > 500 && dcIjkPlayer.currentId > 0) {
                KLog.d("观看行为数据", "getPreVideoPlayingDuring");
                EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(dcIjkPlayer.currentId,
                        dcIjkPlayer.getCurrentPosition(), 0, 0));
            }
        }
        KLog.i("========preVideoPlayingDuring setVideoUrl:" + entity);
        return entity;
    }

    public void sendUserBehavior() {
        if (dcIjkPlayer == null) {
            return;
        }
        KLog.d("观看行为数据", "sendUserBehavior");
        if (dcIjkPlayer.getCurrentPosition() > 500 && dcIjkPlayer.currentId > 0) {
            EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(dcIjkPlayer.currentId,
                    dcIjkPlayer.getCurrentPosition(), 0, 0));
        }
    }

    private AbsIjkPlayListener listener = new AbsIjkPlayListener() {
        @Override
        public void onPlayStart() {
            super.onPlayStart();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayStart();
            }
        }

        @Override
        public void onPlayStop() {
            super.onPlayStop();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayStop();
            }
        }

        @Override
        public void onPlayPause() {
            super.onPlayPause();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayPause();
            }
        }

        @Override
        public void onPlayResume() {
            super.onPlayResume();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayResume();
            }
        }

        @Override
        public void onPlayError(int errorCode) {
            super.onPlayError(errorCode);
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayError(errorCode);
            }
        }

        @Override
        public void onPlayCompleted() {
            super.onPlayCompleted();
            getPreVideoPlayingDuring(true);
            KLog.d("观看行为数据", "onPlayCompleted");
            EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(dcIjkPlayer.currentId,
                    dcIjkPlayer.getDuration(), 0, 0));
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayCompleted();
            }
        }

        @Override
        public void onPlayBufferStart() {
            super.onPlayBufferStart();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayBufferStart();
            }
        }

        @Override
        public void onPlayBufferEnd() {
            super.onPlayBufferEnd();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayBufferEnd();
            }
        }

        @Override
        public void onPlayPreparing() {
            super.onPlayPreparing();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayPreparing();
            }
        }

        @Override
        public void onPlayPrepared() {
            super.onPlayPrepared();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayPrepared();
            }
        }

        @Override
        public void onAudioRenderingStart() {
            super.onAudioRenderingStart();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onAudioRenderingStart();
            }
        }

        @Override
        public void onVideoRenderingStart() {
            super.onVideoRenderingStart();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onVideoRenderingStart();
            }
        }

        @Override
        public void onVideoRotationChanged(int rotate) {
            super.onVideoRotationChanged(rotate);
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onVideoRotationChanged(rotate);
            }
        }

        @Override
        public void onPlayingPosition(long position) {
            super.onPlayingPosition(position);
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayingPosition(position);
            }
        }

        @Override
        public void onFileError(int code, String errorMessage) {
            super.onFileError(code, errorMessage);
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onFileError(code, errorMessage);
            }
        }

        @Override
        public void onLoopStart() {
            super.onLoopStart();
            KLog.i("======dcIjkPlayer alpha:" + dcIjkPlayer.getAlpha());
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onLoopStart();
            }
        }

        @Override
        public void onClickPause() {
            super.onClickPause();
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onClickPause();
            }
        }

        @Override
        public void onPlayTimeCompleted(long videoId, String url, int videoDuring) {
            super.onPlayTimeCompleted(videoId, url, videoDuring);
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onPlayTimeCompleted(videoId, url, videoDuring);
            }
        }

        @Override
        public void onDoubleClick(float x, float y) {
            super.onDoubleClick(x, y);
            if (listenerArray != null && listenerArray.get(pageId) != null) {
                listenerArray.get(pageId).onDoubleClick(x, y);
            }
        }
    };

    public interface OnPlayerDetachListener {
        void onDetach();
    }
}