/*
 * Copyright 2013 Google Inc. All rights reserved.
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

package com.dongci.sun.gpuimglibrary.coder;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.camera.GpuConfig;
import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.dongci.sun.gpuimglibrary.api.listener.DCCameraListener;
import com.dongci.sun.gpuimglibrary.coder.videoCodec.VideoEncoderCore;
import com.dongci.sun.gpuimglibrary.gles.WindowSurface;
import com.dongci.sun.gpuimglibrary.gles.filter.AFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.NoFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.MagicCameraInputFilter;
import com.dongci.sun.gpuimglibrary.gles.EglCore;
import com.dongci.sun.gpuimglibrary.common.SLVideoTool;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;


/**
 * Encode a movie from frames rendered from an external texture image.
 * <p>
 * The object wraps an encoder running on a dedicated thread.  The various control messages
 * may be sent from arbitrary threads (typically the app UI thread).  The encoder thread
 * manages both sides of the encoder (feeding and draining); the only external input is
 * the GL texture.
 * <p>
 * The design is complicated slightly by the need to create an EGL context that shares state
 * with a view that gets restarted if (say) the device orientation changes.  When the view
 * in question is a GLSurfaceView, we don't have full control over the EGL context creation
 * on that side, so we have to bend a bit backwards here.
 * <p>
 * To use:
 * <ul>
 * <li>create TextureMovieEncoder object
 * <li>create an EncoderConfig
 * <li>call TextureMovieEncoder#startRecording() with the config
 * <li>call TextureMovieEncoder#setTextureId() with the texture object that receives frames
 * <li>for each frame, after latching it with SurfaceTexture#updateTexImage(),
 * call TextureMovieEncoder#frameAvailable().
 * </ul>
 * <p>
 * TODO: tweak the API (esp. textureId) so it's less awkward for simple use cases.
 */
public class TextureMovieEncoder implements Runnable {
    private static final String TAG = "TextureMovieEncoder";
    private static final boolean VERBOSE = false;

    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_FRAME_AVAILABLE = 2;
    private static final int MSG_SET_TEXTURE_ID = 3;
    private static final int MSG_UPDATE_SHARED_CONTEXT = 4;
    private static final int MSG_QUIT = 5;
    private static final int MSG_PAUSE = 6;
    private static final int MSG_RESUME = 7;

    // ----- accessed exclusively by encoder thread -----
    private WindowSurface mInputWindowSurface;
    private EglCore mEglCore;
    private MagicCameraInputFilter mInput;
    private int mTextureId;

    private VideoEncoderCore mVideoEncoder;

    // ----- accessed by multiple threads -----
    private volatile EncoderHandler mHandler;
    private volatile HandlerStart handlerStart;

    private Object mReadyFence = new Object();      // guards ready/running
    private boolean mReady;
    private boolean mRunning;
    private GPUImageFilter filter;
    private FloatBuffer gLCubeBuffer;
    private FloatBuffer gLTextureBuffer;
    public static volatile long baseTimeStamp = 0;//第一帧的时间戳

    private boolean prepareEncoder = false;

    public TextureMovieEncoder() {

    }

    /**
     * Encoder configuration.
     * <p>
     * Object is immutable, which means we can safely pass it between threads without
     * explicit synchronization (and don't need to worry about it getting tweaked out from
     * under us).
     * <p>
     * TODO: make frame rate and iframe interval configurable?  Maybe use builder pattern
     * with reasonable defaults for those and bit rate.
     */
    public static class EncoderConfig {
        final String path;
        final int mWidth;
        final int mHeight;
        final int mBitRate;
        final EGLContext mEglContext;

        public EncoderConfig(String path, int width, int height, int bitRate,
                             EGLContext sharedEglContext, Camera.CameraInfo info) {
            this.path = path;
            mWidth = width;
            mHeight = height;
            mBitRate = bitRate;
            mEglContext = sharedEglContext;
        }

        @Override
        public String toString() {
            return "EncoderConfig: " + mWidth + "x" + mHeight + " @" + mBitRate +
                    " to '" + path + "' ctxt=" + mEglContext;
        }
    }

    EncoderConfig config;

    /**
     * Tells the video recorder to start recording.  (Call from non-encoder thread.)
     * <p>
     * Creates a new thread, which will create an encoder using the provided configuration.
     * <p>
     * Returns after the recorder thread has started and is ready to accept Messages.  The
     * encoder may not yet be fully configured.
     */
    public void startRecording(EncoderConfig config) {
        Log.d(TAG, "Encoder: startRecording()");
        synchronized (mReadyFence) {
            if (mRunning) {
                Log.w(TAG, "Encoder thread already running");
                return;
            }
            temp = true;
            mRunning = true;
            new Thread(this, "TextureMovieEncoder").start();
            while (!mReady) {
                try {
                    mReadyFence.wait();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_START_RECORDING, config));
    }

    boolean temp = true;

    /**
     * Tells the video recorder to stop recording.  (Call from non-encoder thread.)
     * <p>
     * Returns immediately; the encoder/muxer may not yet be finished creating the movie.
     * <p>
     * TODO: have the encoder thread invoke a callback on the UI thread just before it shuts down
     * so we can provide reasonable status UI (and let the caller know that movie encoding
     * has completed).
     */
    public void stopRecording() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
        mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
        // We don't know when these will actually finish (or even start).  We don't want to
        // delay the UI thread though, so we return immediately.
    }

    public void pauseRecording() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_PAUSE));
    }

    public void resumeRecording() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_RESUME));
    }

    /**
     * Returns true if recording has been started.
     */
    public boolean isRecording() {
        synchronized (mReadyFence) {
            return mRunning;
        }
    }

    /**
     * Tells the video recorder to refresh its EGL surface.  (Call from non-encoder thread.)
     */
    public void updateSharedContext(EGLContext sharedContext) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SHARED_CONTEXT, sharedContext));
    }

    /**
     * Tells the video recorder that a new frame is available.  (Call from non-encoder thread.)
     * <p>
     * This function sends a message and returns immediately.  This isn't sufficient -- we
     * don't want the caller to latch a new frame until we're done with this one -- but we
     * can get away with it so long as the input frame rate is reasonable and the encoder
     * thread doesn't stall.
     * <p>
     * TODO: either block here until the texture has been rendered onto the encoder surface,
     * or have a separate "block if still busy" method that the caller can execute immediately
     * before it calls updateTexImage().  The latter is preferred because we don't want to
     * stall the caller while this thread does work.
     */
    public void frameAvailable(SurfaceTexture st) {

        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }

        float[] transform = new float[16];      // TODO - avoid alloc every frame

        st.getTransformMatrix(transform);

        Message msg = mHandler.obtainMessage(MSG_FRAME_AVAILABLE, transform);

        mHandler.sendMessage(msg);

        frameAvaliabe = true;

    }

    private boolean frameAvaliabe = false;

    /**
     * Tells the video recorder what texture name to use.  This is the external texture that
     * we're receiving camera previews in.  (Call from non-encoder thread.)
     * <p>
     * TODO: do something less clumsy
     */
    public void setTextureId(int id) {
        Log.i("hero", "-setTextureId--FramAvailable->" + mReadyFence + "readY" + mReady);
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TEXTURE_ID, id, 0, null));
    }

    /**
     * Encoder thread entry point.  Establishes Looper/Handler and waits for messages.
     * <p>
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        // Establish a Looper for this thread, and define a Handler for it.
        Looper.prepare();
        synchronized (mReadyFence) {
            mHandler = new EncoderHandler(this);
            handlerStart = new HandlerStart();
            mReady = true;
            mReadyFence.notify();
        }
        Looper.loop();

        Log.d(TAG, "Encoder thread exiting");
        synchronized (mReadyFence) {
            mReady = mRunning = false;
            mHandler = null;
            handlerStart = null;
        }
    }

    class HandlerStart extends Handler {
        @Override
        public void handleMessage(Message msg) {
            handleFrameAvailable(null);
        }
    }

    /**
     * Handles encoder state change requests.  The handler is created on the encoder thread.
     */
    private static class EncoderHandler extends Handler {
        private WeakReference<TextureMovieEncoder> mWeakEncoder;

        public EncoderHandler(TextureMovieEncoder encoder) {
            mWeakEncoder = new WeakReference<TextureMovieEncoder>(encoder);
        }

        @SuppressLint("NewApi")
        @Override  // runs on encoder thread
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;

            TextureMovieEncoder encoder = mWeakEncoder.get();
            if (encoder == null) {
                Log.w(TAG, "EncoderHandler.handleMessage: encoder is null");
                return;
            }

            switch (what) {
                case MSG_START_RECORDING:
                    encoder.handleStartRecording((EncoderConfig) obj);
                    break;
                case MSG_STOP_RECORDING:
                    encoder.handleStopRecording();
                    break;
                case MSG_FRAME_AVAILABLE:

                    encoder.handleFrameAvailable((float[]) obj);

                    break;
                case MSG_SET_TEXTURE_ID:
                    encoder.handleSetTexture(inputMessage.arg1);
                    break;
                case MSG_UPDATE_SHARED_CONTEXT:
                    encoder.handleUpdateSharedContext((EGLContext) inputMessage.obj);
                    break;
                case MSG_QUIT:
                    Looper.myLooper().quit();
                    break;
                case MSG_PAUSE:
                    encoder.handlePauseRecording();
                    baseTimeStamp = 0;//暂停
//                    VideoEncoderCore.mWriteStop = false;
                    break;
                case MSG_RESUME:
                    encoder.handleResumeRecording();
                    break;
                default:
                    throw new RuntimeException("Unhandled msg what=" + what);
            }
        }
    }

    /**
     * Starts recording.
     */
    private void handleStartRecording(EncoderConfig config) {
        prepareEncoder(config.mEglContext, config.mWidth, config.mHeight, config.mBitRate,
                config.path);
    }

    public static volatile boolean playLock = false;

    /**
     * Handles notification of an available frame.
     * <p>
     * The texture is rendered onto the encoder's input surface, along with a moving
     * box (just because we can).
     * <p>
     *
     * @param transform The texture transform, from SurfaceTexture.
     */
    private void handleFrameAvailable(float[] transform) {

//        Log.e("RecordActivitySdk----", "System.nanoTime():" + System.nanoTime() + " SLVideoTool.isStartRecord:" + SLVideoTool.isStartRecord + " SLVideoTool.moreFrame:" + SLVideoTool.moreFrame + " SLVideoTool.isStartRecord:" + SLVideoTool.isStartRecord + " SLVideoTool.playerStartPlayTime:" + SLVideoTool.playerStartPlayTime);

        if (null == mVideoEncoder)//偶然发生 ，需要解决
            return;

        mVideoEncoder.drainEncoder(false);

        mShowFilter.setTextureId(mTextureId);

        mShowFilter.draw();

        if (baseTimeStamp == 0) {
            baseTimeStamp = System.nanoTime();
            mVideoEncoder.startRecord();
        }

        long time = System.nanoTime() - baseTimeStamp - pauseDelayTime;

//        if (SLVideoTool.moreFrame == true && SLVideoTool.isStartRecord == true && SLVideoTool.playerStartPlayTime == 0){// 多格录制的情况下，点击开始录制，尚未播放
//
//        }else{
//
//        }

//        Log.e("RecordActivitySdk----", "System.nanoTime():" + System.nanoTime() + " baseTimeStamp:" + baseTimeStamp + " pauseDelayTime:" + pauseDelayTime + " video_time:" + time);

        mInputWindowSurface.setPresentationTime(SLVideoTool.videoPts);

        mInputWindowSurface.swapBuffers();

    }

    public void startRecordThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
//                    Log.i("hero",baseTimeStamp+"+++++++++++playTime--FramAvailable->startRecordThread->while"+prepareEncoder+"draw:"+frameAvaliabe);
                    if (baseTimeStamp == 0) {
                        if (frameAvaliabe && prepareEncoder && handlerStart != null) {
                            handlerStart.sendEmptyMessage(0);
                            break;
                        }
                    } else if (baseTimeStamp > 1) {
                        break;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    long pauseDelayTime;
    long onceDelayTime;

    private void handlePauseRecording() {
        onceDelayTime = System.nanoTime();
        mVideoEncoder.pauseRecording();
    }

    private void handleResumeRecording() {
        onceDelayTime = System.nanoTime() - onceDelayTime;
        pauseDelayTime += onceDelayTime;
        mVideoEncoder.resumeRecording();
    }

    /**
     * Handles a request to stop encoding.
     */
    private void handleStopRecording() {
        if (dcCameraListener != null)
            dcCameraListener.onRecordEnd(DCCameraConfig.RECORD_PRE, "录制结束");
        Log.d(TAG, "handleStopRecording");
        mVideoEncoder.drainEncoder(true);
        mVideoEncoder.stopAudRecord();
        Log.d(TAG, "videoplay======VirtualVideo==drainEncoder( End00 )");
        releaseEncoder();
        Log.d(TAG, "videoplay======VirtualVideo==drainEncoder( End01 )"+dcCameraListener);
        if (dcCameraListener != null)
            dcCameraListener.onRecordEnd(DCCameraConfig.SUCCESS, "录制结束");
        Log.d(TAG, "videoplay======VirtualVideo==drainEncoder( End )");
    }

    /**
     * Sets the texture name that SurfaceTexture will use when frames are received.
     */
    private void handleSetTexture(int id) {
        //Log.d(TAG, "handleSetTexture " + id);
        mTextureId = id;
    }

    /**
     * Tears down the EGL surface and context we've been using to feed the MediaCodec input
     * surface, and replaces it with a new one that shares with the new context.
     * <p>
     * This is useful if the old context we were sharing with went away (maybe a GLSurfaceView
     * that got torn down) and we need to hook up with the new one.
     */
    private void handleUpdateSharedContext(EGLContext newSharedContext) {
        Log.d(TAG, "handleUpdatedSharedContext " + newSharedContext);

        // Release the EGLSurface and EGLContext.
        mInputWindowSurface.releaseEglSurface();
        mInput.destroy();
        mEglCore.release();

        // Create a new EGLContext and recreate the window surface.
        mEglCore = new EglCore(newSharedContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface.recreate(mEglCore);
        mInputWindowSurface.makeCurrent();

        // Create new programs and such for the new context.
        mInput = new MagicCameraInputFilter();
        mInput.init();
        filter = null;
        if (filter != null) {
            filter.init();
            filter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
            filter.onDisplaySizeChanged(mVideoWidth, mVideoHeight);
        }
    }

    private void prepareEncoder(EGLContext sharedContext, int width, int height, int bitRate,
                                String path) {
        try {
            mVideoEncoder = new VideoEncoderCore(width, height, bitRate, path, new VideoEncoderCore.WriteData() {
                @Override
                public void onWriteOk() {
                    //handler 主线程
                    if (prepareListener != null)
                        prepareListener.onPrepared();
                }
            });
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mVideoWidth = width;
        mVideoHeight = height;
        mEglCore = new EglCore(sharedContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface = new WindowSurface(mEglCore, mVideoEncoder.getInputSurface(), true);
        mInputWindowSurface.makeCurrent();

        mInput = new MagicCameraInputFilter();
        mInput.init();
        filter = null;
        if (filter != null) {
            filter.init();
            filter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
            filter.onDisplaySizeChanged(mVideoWidth, mVideoHeight);
        }
        mShowFilter.create();

        prepareEncoder = true;
//        mVideoEncoder.setWriteData(new VideoEncoderCore.WriteData() {
//            @Override
//            public void onWriteOk() {
//                //handler 主线程
//                if(prepareListener!=null)
//                    prepareListener.onPrepared();
//            }
//        });

        Log.i("hero", baseTimeStamp + "playTime--FramAvailable->startRecordThread->while--prepareEncoder");
    }

    private void releaseEncoder() {
        mVideoEncoder.release();
        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }
        if (mInput != null) {
            mInput.destroy();
            mInput = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        if (filter != null) {
            filter.destroy();
            filter = null;
//            type = MagicFilterType.NONE;
        }
        prepareEncoder = false;
    }

    //    private MagicFilterType type = MagicFilterType.NONE;
    private AFilter mShowFilter = new NoFilter(GpuConfig.context.getResources());
//    public void setFilter(MagicFilterType type) {
//        this.type = type;
//    }

    private int mPreviewWidth = -1;
    private int mPreviewHeight = -1;
    private int mVideoWidth = -1;
    private int mVideoHeight = -1;

    public void setPreviewSize(int width, int height) {
        mPreviewWidth = width;
        mPreviewHeight = height;
    }

    public void setTextureBuffer(FloatBuffer gLTextureBuffer) {
        this.gLTextureBuffer = gLTextureBuffer;
    }

    PrepareListener prepareListener;

    public void setPrepareListener(PrepareListener prepareListener) {
        this.prepareListener = prepareListener;
    }

    public interface PrepareListener {
        public void onPrepared();
    }

    private DCCameraListener dcCameraListener;

    public void setDcCameraListener(DCCameraListener dcCameraListener) {
        this.dcCameraListener = dcCameraListener;
    }

    public void setCubeBuffer(FloatBuffer gLCubeBuffer) {
        this.gLCubeBuffer = gLCubeBuffer;
    }
}
