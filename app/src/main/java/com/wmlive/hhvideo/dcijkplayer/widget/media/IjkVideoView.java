/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wmlive.hhvideo.dcijkplayer.widget.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.wmlive.hhvideo.dcijkplayer.L;
import com.wmlive.hhvideo.dcijkplayer.application.Settings;
import com.wmlive.hhvideo.utils.KLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;

public class IjkVideoView extends FrameLayout implements MediaController.MediaPlayerControl {
    private String TAG = "IjkVideoView";
    // settable by the client
    protected Uri mUri;
    protected Map<String, String> mHeaders;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;


    //    private int mCurrentAspectRatio = IRenderView.AR_ADJUST_MATCH_WIDTH;
    private int mCurrentAspectRatio = IRenderView.AR_ASPECT_FILL_PARENT;
    // All the stuff we need for playing and showing a video
    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    protected IMediaPlayer mMediaPlayer = null;
    // private int         mAudioSession;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private IMediaController mMediaController;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;
    protected boolean mJustCache = false;
    protected static boolean sMtkCpu = false;
    protected Context mAppContext;
    protected IRenderView mRenderView;
    private int mVideoSarNum;
    private int mVideoSarDen;

    public static final int RENDER_NONE = 0;
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;

    private static final int MAX_TRY_COUNT = 6;
    private byte tryCount = MAX_TRY_COUNT;

    private List<Integer> mAllRenders = new ArrayList<Integer>();
    private int mCurrentRenderIndex = 0;
    private int mCurrentRender = RENDER_NONE;

    static {
        sMtkCpu = isMtkCpu();
    }

    /**
     * 这个构造方法只用来创建一个缓存视频的Player，不做播放
     *
     * @param context
     * @param justCache
     */
    public IjkVideoView(Context context, boolean justCache) {
        super(context);
        mAppContext = context.getApplicationContext();
        mJustCache = justCache;
    }

    public IjkVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }


    // REMOVED: onMeasure
    // REMOVED: onInitializeAccessibilityEvent
    // REMOVED: onInitializeAccessibilityNodeInfo
    // REMOVED: resolveAdjustedSize

    private void initVideoView(Context context) {
        mAppContext = context.getApplicationContext();
        initRenders();
        mVideoWidth = 0;
        mVideoHeight = 0;
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    private void initRenders() {
        mAllRenders.add(RENDER_TEXTURE_VIEW);//默认使用RENDER_SURFACE_VIEW
        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
    }

    public void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }
            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            removeView(renderUIView);
        }

        if (renderView == null)
            return;

        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = mRenderView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);

        L.i("======addRenderCallback");
        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);
    }

    public void setRender(int render) {
        L.i("========setRender:" + render);
        Log.d(TAG, "setRender: render=="+render);
        switch (render) {
            case RENDER_NONE:
                setRenderView(null);
                break;
            case RENDER_TEXTURE_VIEW: {
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                    renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(mCurrentAspectRatio);
                }
                setRenderView(renderView);
                break;
            }
            case RENDER_SURFACE_VIEW: {
                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
                setRenderView(renderView);
                break;
            }
            default:
                Log.e(TAG, String.format(Locale.getDefault(), "invalid render %d\n", render));
                break;
        }
    }

    public IRenderView getRenderView() {
        return mRenderView;
    }


    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    private void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        tryCount = MAX_TRY_COUNT;
        removeCallbacks(tryRunnable);
        openVideo();
        requestLayout();
        invalidate();
    }

    /**
     * 停止播放
     */
    public void stopPlayback() {
        if (mMediaPlayer != null) {
            if (isPlaying()) {
                mMediaPlayer.stop();
            }
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }
            mMediaPlayer = null;
        }
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }


    private Runnable tryRunnable = new Runnable() {
        @Override
        public void run() {
            KLog.i("======mSurfaceHolder is null,do try count:" + (MAX_TRY_COUNT - tryCount));
            openVideo();
            tryCount--;
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    private void openVideo() {

        Log.d("缓存时间测试", "openVideo11111: " + System.currentTimeMillis());
        L.i("========mSurfaceHolder:" + mSurfaceHolder + " ,mUri:" + mUri);
        if (mUri == null || mSurfaceHolder == null) {
//        if (mUri == null) {
            // not ready for playback just yet, will try again later
            KLog.i("=======openVideo===mSurfaceHolder:" + mSurfaceHolder + " \nmUri:" + mUri + " \n remain tryCount:" + tryCount);
            if (tryCount > 0) {
                postDelayed(tryRunnable, 200);
            }
            return;
        }
        removeCallbacks(tryRunnable);
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        L.i("======openVideo1");
        stopPlayback();
        L.i("======openVideo2");
        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        L.i("======openVideo3");
        try {
            L.i("======openVideo4");
            Log.d("缓存时间测试", "openVideo  mMediaPlayer == null : " + System.currentTimeMillis());
            if (mMediaPlayer == null) {
                //MTK的CPU使用安卓原生播放器，会造成起播变慢1s左右，但是画质会正常点
                mMediaPlayer = createPlayer(Settings.PV_PLAYER__IjkMediaPlayer);
                Log.d("缓存时间测试", "openVideo  mMediaPlayer == null : " + System.currentTimeMillis());
            }
            L.i("======openVideo5");
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            L.i("======openVideo6");
            mCurrentBufferPercentage = 0;
            String scheme = mUri.getScheme();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
//                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
//                mMediaPlayer.setDataSource(dataSource);
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
//            } else {
            mMediaPlayer.setDataSource(mUri.toString());
//            }
            L.i("======openVideo7");
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            L.i("======openVideo8");
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            L.i("======openVideo9");

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
            L.i("======openVideo11");
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalArgumentException ex) {
            L.e("Unable to open content: " + mUri + "\n" + ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } finally {
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }

    private IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        if (mRenderView != null) {
                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        }
                        // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                        requestLayout();
                    }
                }
            };

    private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            mCurrentState = STATE_PREPARED;

            if (mJustCache) {
                return;
            }
            Log.d("缓存时间测试", "onPrepared : " + System.currentTimeMillis());
            // Get the capabilities of the player for this stream
            // REMOVED: Metadata

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            L.i("=======onPrepared");
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
//                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
//                        // We didn't actually change the size (it was already at the size
//                        // we need), so we won't get a "surface changed" callback, so
//                        // start the video here instead of in the callback.
//                        if (mTargetState == STATE_PLAYING) {
////                            start();
//                            if (mMediaController != null) {
//                                mMediaController.show();
//                            }
//                        } else if (!isPlaying() &&
//                                (seekToPosition != 0 || getCurrentPosition() > 0)) {
//                            if (mMediaController != null) {
//                                // Show the media controls when we're paused into a video and make 'em stick.
//                                mMediaController.show(0);
//                            }
//                        }
//                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
//                if (mTargetState == STATE_PLAYING) {
////                    start();
//                }
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                public void onCompletion(IMediaPlayer mp) {
                    long currentPosition = mp.getCurrentPosition();
                    KLog.d("播放器", "onCompletion: currentPosition==" + currentPosition);

                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }

                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                            mVideoRotationDegree = arg2;
                            L.i("MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                            if (mRenderView != null)
                                mRenderView.setVideoRotation(arg2);
                            break;
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    L.i("IjkPlayer Error: " + framework_err + "," + impl_err);
                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null && framework_err != 1) {
                        mCurrentState = STATE_ERROR;
                        mTargetState = STATE_ERROR;
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }
                    return true;
                }
            };


    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    // REMOVED: mSHCallback
    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    private IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                L.e("onSurfaceChanged: unmatched render callback\n");
                return;
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
//            boolean isValidState = (mTargetState == STATE_PLAYING);
//            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
//            if (mMediaPlayer != null && isValidState && hasValidSize) {
//                if (mSeekWhenPrepared != 0) {
//                    seekTo(mSeekWhenPrepared);
//                }
//                start();
//            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                L.e("onSurfaceCreated: unmatched render callback\n");
                return;
            }
            L.e("onSurfaceCreated: mSurfaceHolder:" + mSurfaceHolder + " ,from onSurfaceCreated holder:" + holder);
            mSurfaceHolder = holder;
            if (mMediaPlayer != null)
                bindSurfaceHolder(mMediaPlayer, holder);
            else {
//                openVideo();
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                L.e("onSurfaceDestroyed: unmatched render callback\n");
                return;
            }

            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hide();
            // REMOVED: release(true);
//            releaseWithoutStop();
        }
    };

    public void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    /**
     * 只重置播放器
     */
    public void releaseJustReset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mCurrentState = STATE_IDLE;
        }
    }

    /*
     * release the media player in any state
     */
    public void release(boolean cleartargetstate) {
        L.i("========release1");
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
        L.i("========release2");
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayer.isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
        L.i("=====video start1");
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
        L.i("=======player=pause,mCurrentState:" + mCurrentState);
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }

        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    public IMediaPlayer createPlayer(int playerType) {
        IMediaPlayer mediaPlayer = null;
        switch (playerType) {
            case Settings.PV_PLAYER__IjkExoMediaPlayer: {
//                IjkExoMediaPlayer IjkExoMediaPlayer = new IjkExoMediaPlayer(mAppContext);
//                mediaPlayer = IjkExoMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__AndroidMediaPlayer: {
                AndroidMediaPlayer androidMediaPlayer = new AndroidMediaPlayer();
                mediaPlayer = androidMediaPlayer;
                MediaInfo mediaInfo = androidMediaPlayer.getMediaInfo();
            }
            break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
            default: {
                IjkMediaPlayer ijkMediaPlayer = null;
                if (mUri != null) {
                    ijkMediaPlayer = new IjkMediaPlayer();
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1); // 支持精确拖放

//                    3:设置播放前的最大探测时间
                    ijkMediaPlayer.setOption(1, "analyzemaxduration", 100L);
//                    ijkMediaPlayer.setOption(1, "probesize", 10240L);
//                    ijkMediaPlayer.setOption(1, "flush_packets", 1L);
                    ijkMediaPlayer.setOption(4, "packet-buffering", 0L);

//                    4:设置播放前的探测时间 1,达到首屏秒开效果
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"analyzeduration",1);
//                    5:播放前的探测Size，默认是1M, 改小一点会出画面更快
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"probesize",1024*10);
//                    6:每处理一个packet之后刷新io上下文
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"flush_packets",1L);
//                    7: 是否开启预缓冲，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"packet-buffering",isBufferCache?1:0);
//                    8:播放重连次数
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect",5);
//                    9:最大缓冲大小,单位kb
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"max-buffer-size",maxCacheSize);

                    //预读的 packet 超过 MIN_FRAMES 个，那么 ffplay 就会停止预读(取值2-50000)
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 2);





                }
                mediaPlayer = ijkMediaPlayer;
            }
            break;
        }

        return mediaPlayer;
    }

    public static boolean isMtkCpu() {
        String model = Build.HARDWARE;
        L.e("========isMtkCpu:" + model);
        if (!TextUtils.isEmpty(model)) {
            return model.contains("mt") || model.contains("MT");
        }
        return false;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (tryRunnable != null) {
            removeCallbacks(tryRunnable);
        }
    }
}
