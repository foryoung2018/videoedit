package com.dongci.sun.gpuimglibrary.player;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import com.dongci.sun.gpuimglibrary.gles.EasyGlUtils;
import com.dongci.sun.gpuimglibrary.gles.filter.FilterUtils;
import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImageMaskFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.DCGPUImageEmptyFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImageBeautifyFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.diyfilter.GPUImagePolkaDotFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageCrosshatchFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFalseColorFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFilterGroup;
import com.dongci.sun.gpuimglibrary.player.math.DCMatrix4;
import com.dongci.sun.gpuimglibrary.player.math.DCMatrixStack;
import com.dongci.sun.gpuimglibrary.player.script.DCActor;
import com.dongci.sun.gpuimglibrary.player.script.DCScriptManager;
import com.dongci.sun.gpuimglibrary.player.renderObject.*;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEvent;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEventManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import static com.dongci.sun.gpuimglibrary.player.script.DCActor.ACTOR_TYPE_BILLBOARD;
import static com.dongci.sun.gpuimglibrary.player.script.DCActor.ACTOR_TYPE_DECORATION;
import static com.dongci.sun.gpuimglibrary.player.script.DCActor.ACTOR_TYPE_VIDEO;

/**
 * Created by ZXGoto on 16/12/2016.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class DCGLRenderer implements SurfaceTexture.OnFrameAvailableListener {
    interface OnVideoFrameReadyListener {
        void OnVideoFrameReady(long timestamp);
    }

    private static String TAG = "GLRenderer";
    private SurfaceTexture mSurfaceTexture;
    private DCPlayer mPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;

    private int mViewWidth;
    private int mViewHeight;

    private EGL10 mEgl;
    private EGLDisplay mEglDisplay = EGL10.EGL_NO_DISPLAY;
    private EGLContext mEglContext = EGL10.EGL_NO_CONTEXT;
    private EGLSurface mEglSurface = EGL10.EGL_NO_SURFACE;


    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final int EGL_OPENGL_ES2_BIT = 4;

    private float[] mMVPMatrix = new float[16];

    private List<DCRenderObject> mRenderObjects = new ArrayList<>();


    private volatile int mAvailableFrameCount = 0;

    private OnVideoFrameReadyListener mOnVideoFrameReadyListener;
    private boolean mIsForExporting;
    private boolean mHasReleased;

    private final Object mRenderReleasedObject = new Object();
    private DCMatrixStack mMatrixStack = new DCMatrixStack();
    private DCMatrix4 mOrthoMat4;

    private Map<String, GPUImageFilterGroup> mFilters = new HashMap<>();

    private int mBackgroundColor = 0;


    DCGLRenderer(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        mIsForExporting = false;
        init();
    }

    DCGLRenderer(int width, int height) {
        mIsForExporting = true;
        setVideoSize(width, height);
        init();
    }

    private void init() {
        initGL();
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    private void initGL() {
        mEgl = (EGL10) EGLContext.getEGL();

        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetdisplay failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int[] configAttribs = {
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_SURFACE_TYPE, mIsForExporting ? EGL10.EGL_PBUFFER_BIT : EGL10.EGL_WINDOW_BIT,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_NONE
        };

        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, configAttribs, configs, 1, numConfigs)) {
            throw new RuntimeException("eglChooseConfig failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int[] contextAttribs = {
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], EGL10.EGL_NO_CONTEXT, contextAttribs);
        if (mIsForExporting) {
            int[] surfaceAttribs = {
                    EGL10.EGL_WIDTH, mVideoWidth,
                    EGL10.EGL_HEIGHT, mVideoHeight,
                    EGL10.EGL_NONE
            };
            mEglSurface = mEgl.eglCreatePbufferSurface(mEglDisplay, configs[0], surfaceAttribs);
        } else {
            mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], mSurfaceTexture, null);
        }
        if (mEglSurface == EGL10.EGL_NO_SURFACE || mEglContext == EGL10.EGL_NO_CONTEXT) {
            int error = mEgl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                throw new RuntimeException("eglCreateWindowSurface returned  EGL_BAD_NATIVE_WINDOW. ");
            }
            throw new RuntimeException("eglCreateWindowSurface failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }





    }

    void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        mOrthoMat4 = DCMatrix4.ortho(-mVideoWidth / 2.0f, mVideoWidth / 2.0f, -mVideoHeight / 2.0f, mVideoHeight / 2.0f, 0, 10000);
    }

    void setViewSize(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
    }

    void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    void setPlayer(DCPlayer player) {
        mPlayer = player;
        mOnVideoFrameReadyListener = mPlayer;
    }



    void prepare() {
        synchronized (mRenderReleasedObject) {
            if (mPlayer != null && mPlayer.getAssetWrappers() != null) {
                if (mRenderObjects != null) {
                    mRenderObjects.clear();
                }
                List<DCAssetInfo> infos = new ArrayList<>();
                for (DCAssetWrapper assetWrapper : mPlayer.getAssetWrappers()) {
                    if (assetWrapper.getAsset().type == DCAsset.DCAssetTypeImage) {
                        DCAssetInfo info = DCAssetInfo.createAssetInfo(assetWrapper, mVideoWidth, mVideoHeight);
                        DCRenderImage renderImage = new DCRenderImage(info);
                        mRenderObjects.add(renderImage);
                        infos.add(info);
                    } else if (assetWrapper.getAsset().type == DCAsset.DCAssetTypeImages) {
                        if (assetWrapper.getAsset().decorationName != null && assetWrapper.getAsset().decorationName.length() > 0) {
                            if (DCScriptManager.scriptManager().checkDecoration(assetWrapper.getAsset().decorationName)) {
                                DCAssetInfo info = DCAssetInfo.createAssetInfo(assetWrapper, mVideoWidth, mVideoHeight);
                                DCRenderImages renderImages = new DCRenderImages(info);
                                mRenderObjects.add(renderImages);
                                infos.add(info);
                            }
                        } else {
                            DCAssetInfo info = DCAssetInfo.createAssetInfo(assetWrapper, mVideoWidth, mVideoHeight);
                            DCRenderImages renderImages = new DCRenderImages(info);
                            mRenderObjects.add(renderImages);
                            infos.add(info);
                        }
                    } else if (assetWrapper.getAsset().type == DCAsset.DCAssetTypeVideo) {
                        DCAssetInfo info = DCAssetInfo.createAssetInfo(assetWrapper, mVideoWidth, mVideoHeight);
                        DCRenderVideo renderVideo = new DCRenderVideo(info);
                        //renderVideo.surface.setOnFrameAvailableListener(this);
                        renderVideo.filterSurface.setOnFrameAvailableListener(this);
                        Surface s = new Surface(renderVideo.filterSurface);
                        assetWrapper.setSurface(s);
                        mRenderObjects.add(renderVideo);
                        infos.add(info);
                    }
                }


                DCScriptManager.scriptManager().initDefaultScript(infos);
            }
        }
    }

    void release() {
        synchronized (mRenderReleasedObject) {
            mHasReleased = true;
            if (mEgl != null) {
                if (mEgl.eglGetCurrentContext().equals(mEglContext)) {
                    mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                            EGL10.EGL_NO_CONTEXT);
                }
                mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
                mEgl.eglDestroyContext(mEglDisplay, mEglContext);
            }
            mEglContext = EGL10.EGL_NO_CONTEXT;
            mEglSurface = EGL10.EGL_NO_SURFACE;
            mSurfaceTexture = null;
            mPlayer = null;
            if (mRenderObjects != null) {
                for (DCRenderObject renderObject : mRenderObjects) {
                    renderObject.release();
                }
                mRenderObjects.clear();
            }
            mOnVideoFrameReadyListener = null;

            if (mFilters != null) {
                for (GPUImageFilter filter : mFilters.values()) {
                    if (filter != null) {
                        filter.destroy();
                    }
                }
                mFilters.clear();
            }
        }
    }


    private boolean AllFirstFrameDone() {
        for (DCRenderObject renderObject : mRenderObjects) {
//            if (!renderObject.isShowFirstFrame && mediaSurface.assetWrapper.getAsset().type == DCAsset.DCAssetTypeVideo) {
//                return false;
//            }
//            if (renderObject.assetInfo.assetWrapper.getAsset().type == DCAsset.DCAssetTypeVideo) {
//                return false;
//            }

            if (renderObject.getClass().equals(DCRenderVideo.class) && !renderObject.firstRended) {
                return  false;
            }
        }
        return  true;
    }

    synchronized
    public void onFrameAvailable(SurfaceTexture surface) {
        synchronized (mRenderReleasedObject) {
            if (mHasReleased) {
                return;
            }
            DCRenderObject renderObject = null;
            for (DCRenderObject obj : mRenderObjects) {
                if (obj.getClass().equals(DCRenderVideo.class)) {
                    DCRenderVideo renderVideo = (DCRenderVideo)obj;
                    if (renderVideo.filterSurface != null && renderVideo.filterSurface.equals(surface)) {
                        renderObject = obj;
                        break;
                    }
                }
            }
            if (renderObject == null) {
                return;
            }
            try {
                surface.updateTexImage();
                // TODO: 2018/8/13 ZXGoto
                //surface.getTransformMatrix(mediaSurface.uvMatrix);


//                if(!AllFirstFrameDone()) {
//                    mediaSurface.isShowFirstFrame = true;
//                }

                if(!AllFirstFrameDone()) {
                    renderObject.firstRended = true;
                }
                if (mIsForExporting && AllFirstFrameDone()) {
                    renderObject.assetInfo.assetWrapper.onVideoFrameAvailable();
                } else {
                    mAvailableFrameCount++;
                    int activatedCount = mPlayer.getSceneWrapper().getActivatedVideoCount();
                    if (activatedCount > 0 && mAvailableFrameCount >= activatedCount && AllFirstFrameDone()) {
                        mAvailableFrameCount = 0;
                        drawFrame(getTimestamp());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // ignore
            }
        }
    }

    void drawFrame(long timestamp) {
        if(mIsForExporting && !AllFirstFrameDone() ) {
            return;
        }
        int viewportWidth = mViewWidth > 0? mViewWidth : mVideoWidth;
        int viewportHeight = mViewHeight > 0 ? mViewHeight : mVideoHeight;

        float red = ((mBackgroundColor & 0x00ff0000) >> 16) / 255.0f;
        float green = ((mBackgroundColor & 0x0000ff00) >> 8) / 255.0f;
        float blue = (mBackgroundColor & 0x000000ff) / 255.0f;
        float alpha = ((mBackgroundColor & 0xff000000) >> 24) / 255.0f;
        GLES20.glClearColor(red, green, blue, alpha);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glViewport(0, 0, viewportWidth, viewportHeight);

        mMatrixStack.gl_flushMatrix();
        mMatrixStack.gl_pushMatrix();
        {
            mMatrixStack.gl_multiplyMatrix(mOrthoMat4);

            List<DCActor> actors = DCScriptManager.scriptManager().updateActors(timestamp);
            if (actors != null) {
                for (DCActor actor : actors) {
                    if (actor.actorId == 0 && DCScriptManager.scriptManager().currentScript.isFullscreen &&
                            DCScriptManager.scriptManager().currentScript.timeRange.containsTime(timestamp)) {
                        continue;
                    }
                    actor.hasRendered = false;
                    if (actor.transparency <= 0 || (actor.trackId < 0 && !DCScriptManager.scriptManager().currentScript.isAnimated)) {
                        continue;
                    }

                    // decoration
                    if (actor.type.equals(ACTOR_TYPE_DECORATION)) {
                        boolean needToRender = false;
                        for (DCActor a : actors) {
                            if (a.decorationActorId >= 0 && a.decorationActorId == actor.actorId) {
                                if (a.hasRendered) {
                                    needToRender = true;
                                    break;
                                }
                            }
                        }
                        if (!needToRender) {
                            continue;
                        }
                    }

                    DCTimeEvent timeEvent = null;
                    if (DCScriptManager.scriptManager().currentScript != DCScriptManager.scriptManager().defaultScript && actor.actorId > 0) {
                        timeEvent = DCTimeEventManager.timeEventManager().getTimeEvent(timestamp, actor.trackId, DCScriptManager.scriptManager().currentScript.timeRange);
                        if (timeEvent != null) {
                            actor.assetId = timeEvent.assetId;
                        } else {
                            actor.assetId = -1;
                        }
                        boolean exists = false;
                        int assetId = -1;
                        for (DCRenderObject obj : mRenderObjects) {
                            if (assetId < 0 && actor.type.equals(ACTOR_TYPE_VIDEO) &&
                                    !obj.assetInfo.assetWrapper.getAsset().isBillboard && obj.assetInfo.assetWrapper.getAsset().assetId > 0 &&
                                    (obj.assetInfo.assetWrapper.getAsset().decorationName == null || obj.assetInfo.assetWrapper.getAsset().decorationName.length() == 0)) {
                                assetId = obj.assetInfo.assetWrapper.getAsset().assetId;
                            }
                            if (actor.assetId == obj.assetInfo.assetWrapper.getAsset().assetId) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists && assetId > 0) {
                            actor.assetId = assetId;
                        }
                    }

                    String maskFilePath = null;
                    DCRenderObject renderObject = null;
                    for (DCRenderObject obj : mRenderObjects) {
                        if (actor.type.equals(ACTOR_TYPE_DECORATION)) {
                            if (actor.decorationName != null && actor.decorationName.length() > 0) {
                                if (obj.assetInfo.assetWrapper.getAsset().decorationName != null) {
                                    if (obj.assetInfo.assetWrapper.getAsset().decorationName.equals(actor.decorationName)) {
                                        renderObject = obj;
                                        break;
                                    }
                                }
                            }
                        } else if (actor.type.equals(ACTOR_TYPE_BILLBOARD)) {
                            if (obj.assetInfo.assetWrapper.getAsset().isBillboard) {
                                renderObject = obj;
                                break;
                            }
                        } else if (obj.assetInfo.assetWrapper.getAsset().assetId == actor.assetId) {
                            renderObject = obj;
                            // get mask
                            if (actor.decorationName != null && actor.decorationName.length() > 0) {
                                for (DCRenderObject o : mRenderObjects) {
                                    if (o.assetInfo.assetWrapper.getAsset().decorationName != null && o.assetInfo.assetWrapper.getAsset().decorationName.equals(actor.decorationName)) {
                                        maskFilePath = o.assetInfo.assetWrapper.getAsset().decorationMaskPath;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    if (renderObject != null) {
                        DCAsset.TimeRange assetTimeRange = renderObject.assetInfo.assetWrapper.getAsset().getTimeRange();
                        DCAsset.TimeRange timeRange = new DCAsset.TimeRange(renderObject.assetInfo.assetWrapper.getAsset().startTimeInScene, assetTimeRange.duration);
                        if (DCScriptManager.scriptManager().currentScript.timeRange.duration != 0) {
                            if (!DCScriptManager.scriptManager().currentScript.timeRange.containsTime(timestamp) && actor.actorId > 0) {
                                continue;
                            }
                        }
                        if (DCScriptManager.scriptManager().currentScript == DCScriptManager.scriptManager().defaultScript) {
                            if (!((assetTimeRange.duration > 0 && timeRange.containsTime(timestamp)) || timeRange.startTime < timestamp)){
                                continue;
                            }
                        }

                        if (DCScriptManager.scriptManager().currentScript.timeRange.containsTime(timestamp) || timeRange.startTime <= timestamp) {
                            GPUImageFilterGroup filter = null;
                            if (!actor.type.equals(ACTOR_TYPE_DECORATION)) {
                                if (actor.decorationName != null && actor.decorationName.length() > 0) {
                                    if (mFilters.containsKey(actor.decorationName)) {
                                        filter = mFilters.get(actor.decorationName);
                                    } else {
                                        GPUImageFilter realFilter;
                                        if (maskFilePath != null) {
                                            realFilter = FilterUtils.maskFilterWithPath(maskFilePath);
                                        } else {
                                            realFilter = FilterUtils.maskFilterWithName(actor.decorationName);
                                        }

                                        if (realFilter != null) {
                                            int frameWidth = mVideoWidth;
                                            int frameHeight = mVideoHeight;
                                            filter = new GPUImageFilterGroup();
                                            filter.addFilter(realFilter);
                                            filter.init();
                                            filter.onInputSizeChanged(frameWidth, frameHeight);
                                            filter.onDisplaySizeChanged(frameWidth, frameHeight);
                                            mFilters.put(actor.decorationName, filter);
                                            ((GPUImageMaskFilter)realFilter).setStrokeColor(mBackgroundColor);
                                        }
                                    }
                                }
                            }

                            renderObject.addFilter(renderObject.assetInfo.assetWrapper.getAsset().filter,mVideoWidth,mVideoHeight);

                            renderObject.draw(mMatrixStack, timestamp, actor, timeEvent, filter,renderObject.filterGroup, viewportWidth, viewportHeight);
                            actor.hasRendered = true;
                        }
                    }
                }
            }
        }
        mMatrixStack.gl_popMatrix();

        GLES20.glFinish();

        if (!mIsForExporting) {
            mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
        }

        if (mOnVideoFrameReadyListener != null) {
            mOnVideoFrameReadyListener.OnVideoFrameReady(timestamp);
        }
    }

    private long getTimestamp() {
        long min = Long.MAX_VALUE;
        if (mIsForExporting) {
            for (DCRenderObject renderObject : mRenderObjects) {
                if (renderObject.getClass().equals(DCRenderVideo.class)) {
                    DCRenderVideo renderVideo = (DCRenderVideo)renderObject;
                    if (renderVideo.filterSurface != null) {
                        long timestamp = renderVideo.filterSurface.getTimestamp();
                        if (timestamp < min && min - timestamp < 1000000) {
                            min = timestamp;
                        }
                    }
                }
            }
        } else {
            for (DCAssetWrapper wrapper : mPlayer.getSceneWrapper().getAssetWrappers()) {
                DCAsset asset = wrapper.getAsset();
                if (asset.type == DCAsset.DCAssetTypeVideo) {
                    DCAssetVideoWrapper videoWrapper = (DCAssetVideoWrapper) wrapper;
                    long time = asset.startTimeInScene + videoWrapper.getPlayerTimestamp() - asset.getTimeRange().startTime;
                    if (time >= 0 && time < min) {
                        min = time;
                    }
                }
            }
        }
        if (min == Long.MAX_VALUE) {
            min = 0;
        }
        return min;
    }
}
