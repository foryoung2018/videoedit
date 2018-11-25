package com.wmlive.hhvideo.heihei.discovery;

import android.media.MediaPlayer;
import android.text.TextUtils;

import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MediaPlayUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 6/12/2017.
 * 音乐播放的管理类
 */

public class AudioPlayerManager implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private MediaPlayUtil mediaPlayUtil;
    private Disposable disposable;

    private static final class Holder {
        static final AudioPlayerManager HOLDER = new AudioPlayerManager();
    }

    public static AudioPlayerManager get() {
        return Holder.HOLDER;
    }

    /**
     * 初始化AudioPlayerManager
     */
    public void init() {
        mediaPlayUtil = new MediaPlayUtil();
        mediaPlayUtil.setPlayOnBufferingUpdateListener(this);
        mediaPlayUtil.setPlayOnCompleteListener(this);
    }

    /**
     * 开始播放下一个音乐
     *
     * @param url
     */
    public void start(final String url) {
        Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        if (null == mediaPlayUtil) {
                            init();
                        }
                        if (mediaPlayUtil.isPlayingPlayer()) {
                            mediaPlayUtil.stopPlayer();
                            KLog.i("======停止音乐");
                        }
                        if (TextUtils.isEmpty(mediaPlayUtil.getSoundFilePath())) {//首次播放
                            mediaPlayUtil.initPlay(url);
                            KLog.i("======首次播放音乐");
                        } else if (!url.equalsIgnoreCase(mediaPlayUtil.getSoundFilePath())) {//如果不url相同，则重新播放
                            mediaPlayUtil.resetPlayer();//这个用来防止一直调用onBufferingUpdate
//                            init();
                            mediaPlayUtil.initPlay(url);
                            KLog.i("======切换下一首音乐");
                        } else {
                            mediaPlayUtil.setSoundFilePath(null);
                            KLog.i("======音乐url置空");
                        }
                        return 1;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 重置播放
     */
    public void reset() {
        if (null != mediaPlayUtil) {
            disposable = Observable.just(1)
                    .subscribeOn(Schedulers.computation())
                    .map(new Function<Integer, Integer>() {
                        @Override
                        public Integer apply(Integer integer) throws Exception {
                            if (null != mediaPlayUtil) {
                                if (mediaPlayUtil.isPlayingPlayer()) {
                                    mediaPlayUtil.stopPlayer();
                                }
                                if (mediaPlayUtil != null) {//这个地方我也很无奈
                                    mediaPlayUtil.resetPlayer();
                                    mediaPlayUtil.setSoundFilePath(null);
                                }
                                KLog.i("======重置音乐播放器");
                            }
                            return 1;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            if (null != statusChangeCallback) {
                                statusChangeCallback.onReset();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });

        }
    }

    /**
     * 释放
     */
    public void release() {
        statusChangeCallback = null;
        KLog.i("======开始释放音乐播放器");
        if (null != mediaPlayUtil) {
            disposable = Observable.just(1)
                    .subscribeOn(Schedulers.computation())
                    .map(new Function<Integer, Integer>() {
                        @Override
                        public Integer apply(Integer integer) throws Exception {
                            if (mediaPlayUtil != null) {
                                if (mediaPlayUtil.isPlayingPlayer()) {
                                    mediaPlayUtil.stopPlayer();
                                }
                                mediaPlayUtil.resetPlayer();
                                mediaPlayUtil.releasePlayer();
                                mediaPlayUtil.setSoundFilePath(null);
                                mediaPlayUtil = null;
                                KLog.i("======释放音乐播放器");
                            }
                            return 1;
                        }
                    })
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(@NonNull Integer integer) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
        }
//        if (null != disposable && !disposable.isDisposed()) {
//            disposable.dispose();
//        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        KLog.i("=====onBufferingUpdate:" + percent);
        if (null != statusChangeCallback) {
            statusChangeCallback.onBuffered();
            KLog.i("=====音乐缓冲完成");
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        reset();
        KLog.i("=====音乐播放完成");
    }


    private OnStatusChangeCallback statusChangeCallback;

    public void setStatusChangeCallback(OnStatusChangeCallback statusChangeCallback) {
        this.statusChangeCallback = statusChangeCallback;
    }

    public OnStatusChangeCallback getStatusChangeCallback() {
        return statusChangeCallback;
    }

    public interface OnStatusChangeCallback {
        void onBuffered();

        void onReset();

    }
}
