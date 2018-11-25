package com.wmlive.hhvideo.dcijkplayer;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpUrlSource;
import com.example.crclibrary.Crc64;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.log.VideoDownLoad;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.common.VideoProxy;
import com.wmlive.hhvideo.dcijkplayer.widget.media.IjkVideoView;
import com.wmlive.networklib.util.EventHelper;

import java.io.File;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.wmlive.hhvideo.utils.FileUtil.getBytes;

/**
 * Created by yangjiangang on 2018/7/27.
 */

public class DcIjkPlayer extends IjkVideoView implements
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnInfoListener,
        View.OnTouchListener {

    private boolean isDebug;
    private boolean canCache = true;//是否需要缓存
    private boolean canLoop = true;//是否循环播放
    private boolean clickPause = true;//是否点击播放器暂停
    private String currentUrl;//当前的url
    public long currentId;//当前的视频id
    private IjkPlayListener ijkPlayListener;
    private static final int MSG_CURRENT_POSITION = 10;
    private static final int MSG_NET_ERROR = 30;//网络出错
    private static final int MSG_SINGLE_CLICK = 40;//单击事件
    private int refreshInterval = 1000;//获取当前播放位置的频率
    private boolean isStarted;//是否已经开始播放
    public static final int STATE_NONE = 0;//未初始化播放
    public static final int STATE_STARTED = 1;//已经初始化播放
    public static final int STATE_ERROR = 2;//出错
    private int playState = STATE_NONE;

    private boolean onceCompleted;//播放完了一次


    private long playtime;//开始缓存的时间
    private int bufferTimes;//缓冲的次数
    private long bufferBeginTime;//缓冲开始的时间点
    private int bufferToatalTime;//缓存的总时间
    private boolean notAllCache;//是否缓存完成
    private long alllenth;//下载文件总长度
    private String finalURl;//资源的真实地址
    private long allLoadTime;//全部下载完成所用时间
    private String lastUrl;
    private boolean first;
    private long beginLenth;
    private long lastLenth;


    public static void initPlayer() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            L.i("====DcijkPlayer loadLibraries:" + e.getLocalizedMessage());
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CURRENT_POSITION:
                    if (null != ijkPlayListener) {
                        ijkPlayListener.onPlayingPosition(getCurrentPosition());
                    }
                    handler.sendEmptyMessageDelayed(MSG_CURRENT_POSITION, refreshInterval);
                    break;
                case MSG_SINGLE_CLICK:

                    if (null != ijkPlayListener) {
                        ijkPlayListener.onClickPause();
                    }
                    break;
                case MSG_NET_ERROR:
                    int code = getNetworkState(mAppContext);
                    if (ijkPlayListener != null) {
                        if (code == 2) {//无网络时
                            ijkPlayListener.onFileError(2, "网络不给力哦～");
                        } else {//默认为文件已删除
                            ijkPlayListener.onFileError(code, "播放视频出错");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 这个构造方法只用来创建一个缓存视频的Player，不做播放
     *
     * @param context
     * @param justCache
     */
    public DcIjkPlayer(Context context, boolean justCache) {
        super(context, justCache);
    }

    public DcIjkPlayer(Context context) {
        super(context);
        init(context);
    }

    public DcIjkPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DcIjkPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DcIjkPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    private void init(Context context) {
        this.setOnTouchListener(this);
        this.setOnInfoListener(this);
        this.setOnCompletionListener(this);
        this.setOnPreparedListener(this);
        this.setOnErrorListener(this);
        setBackgroundColor(Color.BLACK);
    }

    /**
     * 设置刷新当前播放位置的时间间隔，默认是1s
     *
     * @param refreshInterval
     */
    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     * 设置是否可缓存
     *
     * @param canCache
     */
    public void setCacheable(boolean canCache) {
        this.canCache = canCache;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    /**
     * 点击播放器暂停，再次点击播放
     *
     * @param clickPause
     */
    public void setClickPause(boolean clickPause) {
        this.clickPause = clickPause;
    }

    public void setPlayListener(IjkPlayListener ijkPlayListener) {
        this.ijkPlayListener = ijkPlayListener;
    }

    /**
     * 设置视频的url
     *
     * @param url
     */
    public void setVideoUrl(long videoId, String url) {
        L.i("preparing url:" + url);
        bufferTimes = 0;
        bufferToatalTime = 0;
        playtime = System.currentTimeMillis();
        notAllCache = false;
        alllenth = 0;
        allLoadTime = 0;
        finalURl = "";
        lastUrl = url;
        beginLenth = 0;
        lastLenth = 0;


        handler.removeMessages(MSG_CURRENT_POSITION);
        handler.removeMessages(MSG_NET_ERROR);
        if (!TextUtils.isEmpty(currentUrl)) {
            if (!currentUrl.equals(url) && getCurrentPosition() > 0) {
                if (ijkPlayListener != null) {//这是上一个视频的播放位置
                    ijkPlayListener.onPlayTimeCompleted(currentId, currentUrl, getCurrentPosition());
                }
            }
        }
        if (isPlaying()) {
            pause();
        }
        currentId = videoId;
        currentUrl = url;

        if (null != ijkPlayListener) {
            ijkPlayListener.onPlayPreparing();
        }
        first = true;
        if (canCache) {
            if (!TextUtils.isEmpty(url)) {
                if ((url.startsWith("http") && url.contains("127.0.0.1"))//这些协议地址不可使用缓存
                        || url.startsWith("rtmp")
                        || url.startsWith("rtsp")) {
                } else {
                    L.i("======set Proxy 111");
                    if (VideoProxy.get().getProxy() != null) {
                        VideoProxy.get().getProxy().registerCacheListener(new CacheListener() {
                            @Override
                            public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
                                //表示未全部缓存成功
                                if (percentsAvailable < 100) {
                                    notAllCache = true;
                                }
                                if (first) {
                                    beginLenth = cacheFile.length();
                                    first = false;
                                }
                                lastLenth = cacheFile.length();

                                Log.d("ggqcache", "onCacheAvailable: url==" + url);
                                Log.d("ggqcache", "onCacheAvailable: percentsAvailable==" + percentsAvailable + "  cacheFile==" + cacheFile.length());
                                HttpUrlSource httpSource= VideoProxy.get().getProxy().getHttpSource(url);
                                if (httpSource != null) {
                                    alllenth = httpSource.getTotalLenth() / 1000;
                                    finalURl = httpSource.getFinalURl();
                                    Log.d("alllenth", "sendCacheDate: alllenth==" + alllenth + finalURl);
                                }
                                if (percentsAvailable == 100) {
                                    allLoadTime = System.currentTimeMillis() - playtime;
                                }
                            }
                        }, url);
                        Log.d("缓存时间测试", "setVideoUrl11111: " + System.currentTimeMillis());
                        url = VideoProxy.get().getProxy().getProxyUrl(url);
                        Log.d("缓存时间测试", "setVideoUrl22222: " + System.currentTimeMillis());

                    }
                }
            }
        }
        onceCompleted = false;
        if (!TextUtils.isEmpty(currentUrl)) {
            setVideoPath(url);
        }
    }


    /**
     * 缓存上报
     */
    public void sendCacheDate() {
        Log.d("alllenth", "sendCacheDate: alllenth==" + alllenth);
        if (notAllCache && lastUrl != null) {
            if (lastLenth - beginLenth > 0) {
                EventHelper.post(GlobalParams.EventType.TYPE_PLAY_DOWNLOAD, new VideoDownLoad(
                        TextUtils.isEmpty(finalURl) ? lastUrl : finalURl,
                        currentId + "",
                        "" + alllenth,
                        (lastLenth - beginLenth) / 1000 + "",
                        "" + (allLoadTime == 0 ? (System.currentTimeMillis() - playtime) / 1000 : allLoadTime),
                        "" + (lastLenth - beginLenth) / (System.currentTimeMillis() - playtime),
                        "" + bufferTimes,
                        "" + bufferToatalTime / 1000
                ));
            }
        }


    }

    /**
     * 获取当前播放的url
     *
     * @return
     */
    public String getUrl() {
        return currentUrl;
    }

    public void resetUrl() {
        currentUrl = null;
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        handler.removeMessages(MSG_CURRENT_POSITION);
        if (!isPlaying()) {
            start();
        }
        handler.sendEmptyMessage(MSG_CURRENT_POSITION);
        L.i("===video start2");
        if (null != ijkPlayListener) {
            ijkPlayListener.onPlayStart();
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        handler.removeMessages(MSG_CURRENT_POSITION);
        pause();
        L.i("video pause");
        if (null != ijkPlayListener) {
            ijkPlayListener.onPlayPause();
        }
    }

    /**
     * 停止播放，此方法会释放掉IMediaPlayer
     */
    public void stopPlay() {
        handler.removeMessages(MSG_CURRENT_POSITION);
        stopPlayback();
        if (null != ijkPlayListener) {
            ijkPlayListener.onPlayStop();
        }
        playState = STATE_NONE;
    }

    /**
     * 释放播放器
     *
     * @param cleartargetstate
     */
    public void releasePlayer(boolean cleartargetstate) {
        playState = STATE_NONE;
        release(cleartargetstate);
        this.currentUrl = null;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        L.i("=======onCompletion");
        if (null != ijkPlayListener) {
            ijkPlayListener.onPlayCompleted();
        }
        if (!onceCompleted) {//首次播放完成
            if (ijkPlayListener != null) {
                ijkPlayListener.onPlayTimeCompleted(currentId, currentUrl, getDuration());
            }
        }
        onceCompleted = true;
        if (canLoop) {
            handler.removeMessages(MSG_CURRENT_POSITION);
            start();
            handler.sendEmptyMessageDelayed(MSG_CURRENT_POSITION, refreshInterval);
            L.i("video loop start");
            if (null != ijkPlayListener) {
                ijkPlayListener.onLoopStart();
            }
        }
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        L.i("onPlayPrepared");
        handler.removeMessages(MSG_NET_ERROR);
        if (null != ijkPlayListener) {
            ijkPlayListener.onPlayPrepared();
        }
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int errorCode, int i1) {
        L.i("onPlayError errorCode: " + errorCode + " i1:" + i1);
        handler.removeMessages(MSG_CURRENT_POSITION);
        if (null != ijkPlayListener) {
            switch (errorCode) {
                case IMediaPlayer.MEDIA_INFO_UNKNOWN:
                    return true;
                case -10000://文件不存在，没有网络也是这个错误码
                    playState = STATE_ERROR;
                    handler.sendEmptyMessageDelayed(MSG_NET_ERROR, 3000);
                    return true;
                default:
                    playState = STATE_ERROR;
                    ijkPlayListener.onPlayError(errorCode);
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int infoCode, int i1) {
        L.i("onInfo infoCode: " + infoCode + " i1:" + i1);
        if (null != ijkPlayListener) {
            switch (infoCode) {
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    L.i("onInfo: 缓冲开始");
                    ijkPlayListener.onPlayBufferStart();
                    bufferTimes++;
                    bufferBeginTime = System.currentTimeMillis();

                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    L.i("onInfo: 缓冲结束");
                    ijkPlayListener.onPlayBufferEnd();
                    bufferToatalTime += System.currentTimeMillis() - bufferBeginTime;
                    break;
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    L.i("onInfo: 开始渲染音频");
                    ijkPlayListener.onAudioRenderingStart();
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    L.i("onInfo: 开始渲染视频");
                    onceCompleted = false;
                    ijkPlayListener.onVideoRenderingStart();
                    playState = STATE_STARTED;
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    L.i("onInfo: 视频方向改变");
                    ijkPlayListener.onVideoRotationChanged(i1);
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private float downX;
    private float downY;

    private long lastTime = 0;


    /**
     * 挂起播放器，此方法会释放掉IMediaPlayer，但是不会清除播放状态
     */
    public void suspendPlay() {
        suspend();
    }

    private int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        L.i("Action:" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                L.i("====xxxDOWN=X:" + downX + " ===Y:" + downY);
                break;
            case MotionEvent.ACTION_MOVE:
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                long nowTime = System.currentTimeMillis();
                L.i("===UP==xxxX:" + event.getRawX() + " ===Y:" + event.getRawY());
                if (nowTime - lastTime > 30) {//需要连击时，改成300
                    if (Math.abs(downX - event.getRawX()) < touchSlop
                            && Math.abs(downY - event.getRawY()) < touchSlop) {
                        L.i("====xxxsend click nowTime " + nowTime + "  lastTime " + lastTime);
//                        handler.sendEmptyMessageDelayed(MSG_SINGLE_CLICK, 300);//需要连击时，发送单击事件
                        if (ijkPlayListener != null) {
                            ijkPlayListener.onClickPause();
                        }
                    }
                } else {//连续点击
                    handler.removeMessages(MSG_SINGLE_CLICK);
                    L.i("======xxxPlayer连续点击X:" + downX + "====Y:" + downY);
                    if (ijkPlayListener != null) {
                        ijkPlayListener.onDoubleClick(downX, downY);
                    }
                }
                lastTime = nowTime;
                break;
            default:
                break;
        }
        return clickPause;
    }


    public void resumePlay() {
        handler.removeMessages(MSG_CURRENT_POSITION);
        start();
        L.i("==========on video resume");
        playState = STATE_STARTED;
        handler.sendEmptyMessageDelayed(MSG_CURRENT_POSITION, refreshInterval);
        if (null != ijkPlayListener) {
            ijkPlayListener.onPlayResume();
        }
    }

    /**
     * 获取当前网络类型，
     *
     * @param context
     * @return 0：wifi    1:移动网络   2:无网络
     */
    private int getNetworkState(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //获取WIFI连接的信息
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //获取移动数据连接的信息
        NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        int result = 0;
        if ((wifiNetworkInfo == null || !wifiNetworkInfo.isConnected())) {
            if (dataNetworkInfo != null && dataNetworkInfo.isConnected()) {
//                ToastUtil.showToast("当前正在使用流量");
                result = 1;
            } else {
//                ToastUtil.showToast("当前无网络");
                result = 2;
            }
        }
        return result;
    }

    private boolean needPause = true;

    public void setNeedPause(boolean needPause) {
        this.needPause = needPause;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
        KLog.i("=====onDetachedFromWindow=needPause:" + needPause);
        if (needPause) {
            pausePlay();
        }
        playState = STATE_NONE;
        super.onDetachedFromWindow();
    }

    /**
     * 设置视频宽高比
     *
     * @param aspectRatio
     */
    public void setAspectRatio(int aspectRatio) {
        if (mRenderView != null) {
            mRenderView.setAspectRatio(aspectRatio);
        }
    }

}
