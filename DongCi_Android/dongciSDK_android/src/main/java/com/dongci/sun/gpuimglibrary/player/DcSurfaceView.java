package com.dongci.sun.gpuimglibrary.player;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.gles.EventHandler;
import com.dongci.sun.gpuimglibrary.camera.CameraCaptureView;
import com.dongci.sun.gpuimglibrary.gles.CameraEGLRender;
import com.dongci.sun.gpuimglibrary.gles.EGLUtils;
import com.dongci.sun.gpuimglibrary.common.Constant;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class DcSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final String TAG = "ggq";

    public static final int NV21 = ImageFormat.NV21;

    private static final int DEFAULT_PREVIEW_WIDTH = 720;
    private static final int DEFAULT_PREVIEW_HEIGHT = 960;
    private static final int DEFAULT_PREVIEW_FPS = 24;
    private final Context mContext;

    private CameraEGLRender mCameraEGLRender;
    public SurfaceTexture mSurfaceTexture;
    private int mOESTextureId = EGLUtils.NO_TEXTURE;

    private int mPreviewWidth = DEFAULT_PREVIEW_WIDTH;
    private int mPreviewHeight = DEFAULT_PREVIEW_HEIGHT;
    private volatile boolean mIsCapturing = false;
    private boolean mIsTurnOnTheTorch = false;
    private float[] mProjectionMatrix = new float[16];
    private float[] mSurfaceMatrix = new float[16];
    private float[] mTransformMatrix = new float[16];
    private int mPreviewFormat = NV21;
    private int mPreviewFps = DEFAULT_PREVIEW_FPS;
    private boolean mIsPreviewing = false;
    private CameraCaptureView.OnGetRGBAFrameCallback mOnGetRGBAFrameCallback;
    private EventHandler mEventHandler;

    private volatile boolean mIsWaitingForStartPreviewing = false;

    public DcSurfaceView(Context context) {
        this(context, null);
    }

    public DcSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setEventHandler(EventHandler eventHandler) {
        mEventHandler = eventHandler;
    }

    private void init() {
        setKeepScreenOn(true);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public interface SurfaceListener{
        public void onCreate(SurfaceTexture surfaceTexture);
    }
    SurfaceListener surfaceListener;
    public void setSurfaceCratedListener(SurfaceListener surfaceListener){
        this.surfaceListener = surfaceListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated"+mCameraEGLRender);

        if (mCameraEGLRender == null) {
            mOESTextureId = EGLUtils.getExternalOESTextureID();
            mSurfaceTexture = new SurfaceTexture(mOESTextureId);

            mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    Log.d(TAG, "onSurfaceCreated---==onFrameAvailable");
                    requestRender();

                }
            });

            synchronized (this) {
                mCameraEGLRender = new CameraEGLRender(mContext, mEventHandler, mOESTextureId);
                if (mIsWaitingForStartPreviewing) {
                    mIsWaitingForStartPreviewing = false;
//                    startPreview();
                }
            }
            if(surfaceListener!=null){
                surfaceListener.onCreate(mSurfaceTexture);
            }

        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged width=" + width + ",height=" + height);
        mCameraEGLRender.onPictureSizeChanged(width, height);

        if (width > height) {
            Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        } else {
            Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        }

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Log.d("sun","drawFrame");
        mSurfaceTexture.updateTexImage();

        mSurfaceTexture.getTransformMatrix(mSurfaceMatrix);
        Matrix.multiplyMM(mTransformMatrix, 0, mSurfaceMatrix, 0, mProjectionMatrix, 0);
        mCameraEGLRender.drawFrame(mTransformMatrix);

        if (mIsCapturing) {
            mOnGetRGBAFrameCallback.onGetRGBAFrame(mCameraEGLRender.getGLFboBuffer().array());
        }
    }



    /**
     * start preview.
     *
     * @returnx
     */
    public void startPreview() {
        if (mIsPreviewing) {
            Log.d(TAG, "is previewing");
            return;
        }

        synchronized (this) {
            if (mCameraEGLRender == null) {
                mIsWaitingForStartPreviewing = true;
                return;
            }
        }
//        int cameraPerm = PermissionChecker.checkSelfPermission(mContext, Manifest.permission.CAMERA);
//        if (cameraPerm != PackageManager.PERMISSION_GRANTED) {
//            if (mEventHandler.isHandleError()) {
//                Message msg = Message.obtain();
//                msg.what = Constant.STREAM_PUBLISHER_CAMERA_PERMISSION_DENIED;
//                mEventHandler.sendMessage(msg);
//            }
//            return;
//        }

//        boolean cameraPerm2 = PermissionUtils.isHasCameraPermission();
//        if (!cameraPerm2) {
//            if (mEventHandler.isHandleError()) {
//                Message msg = Message.obtain();
//                msg.what = Constant.STREAM_PUBLISHER_CAMERA_PERMISSION_DENIED;
//                mEventHandler.sendMessage(msg);
//            }
//            return;
//        }


//        if (cameraIsNull()) {
//            mCamera = openCamera();
//            Log.e(TAG,"open-camera--result>"+mCamera);
//            if (cameraIsNull()) {
//                if (mEventHandler.isHandleError()) {
//                    Message msg = Message.obtain();
//                    msg.what = Constant.CAMERA_CAPTURE_VIEW_OPEN_CAMERA_FAILED;
//                    mEventHandler.sendMessage(msg);
//                }
//                return;
//            }
//        }
        if (!mCameraEGLRender.isInitialized()) {
            if (mEventHandler.isHandleError()) {
                Message msg = Message.obtain();
                msg.what = Constant.CAMERA_CAPTURE_VIEW_CAMERA_EGL_RENDER_UNAVAILABLE;
                mEventHandler.sendMessage(msg);
            }
            return;
        }
//        try {
//            mCamera.setPreviewTexture(mSurfaceTexture);
//        } catch (IOException e) {
//            Log.d(TAG, "e=" + e);
//            if (mEventHandler.isHandleError()) {
//                Message msg = Message.obtain();
//                msg.what = Constant.CAMERA_CAPTURE_VIEW_SET_PREVIEW_TEXTURE_FAILED;
//                mEventHandler.sendMessage(msg);
//            }
//            return;
//        }

//        Camera.Parameters params = mCamera.getParameters();

        Log.d(TAG, "before adaptPreviewResolution mPreviewWidth=" + mPreviewWidth + ",mPreviewHeight=" + mPreviewHeight);

//        Camera.Size rs = adaptPreviewResolution(mCamera.new Size(mPreviewWidth, mPreviewHeight));
//        if (rs != null) {
//            mPreviewWidth = rs.width;
//            mPreviewHeight = rs.height;
//        }
        Log.d(TAG, "after adaptPreviewResolution mPreviewWidth=" + mPreviewWidth + ",mPreviewHeight=" + mPreviewHeight);

//        params.setPictureSize(mPreviewWidth, mPreviewHeight);
//        params.setPreviewSize(mPreviewWidth, mPreviewHeight);
        post(new Runnable() {
            @Override
            public void run() {
                getHolder().setFixedSize(mPreviewWidth, mPreviewHeight);
            }
        });

//        int[] range = adaptFpsRange(mPreviewFps, params.getSupportedPreviewFpsRange());
//        params.setPreviewFpsRange(range[0], range[1]);
////        params.setPreviewFormat(mPreviewFormat);
////        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
////        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
////        params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
//
//        List<String> supportedFocusModes = params.getSupportedFocusModes();
//        if (supportedFocusModes != null && !supportedFocusModes.isEmpty()) {
//            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                mCamera.autoFocus(null);
//            } else {
//                params.setFocusMode(supportedFocusModes.get(0));
//            }
//        }
//        mCamera.setDisplayOrientation(90);
//        mCamera.setParameters(params);
//        mCamera.startPreview();
        mIsPreviewing = true;
    }

    /**
     * stop preview.
     */
    public void stopPreview() {
        if (!mIsPreviewing) {
            Log.d(TAG, "is not previewing");
            return;
        }
//        mCamera.stopPreview();
        mIsPreviewing = false;
    }

    /**
     * is previewing.
     *
     * @return
     */
    public boolean isPreviewing() {
        return mIsPreviewing;
    }

    /**
     * set callback for getting rgba data.
     *
     * @param cb
     */
    public void setOnGetRGBAFrameCallback(CameraCaptureView.OnGetRGBAFrameCallback cb) {
        mOnGetRGBAFrameCallback = cb;
    }



    /**
     * turn on the torch.
     *
     * @return
     */
    public void turnOnTheTorch() {
        if (mIsTurnOnTheTorch)
            return;
        if (!mIsPreviewing) return;
//        Camera.Parameters params = mCamera.getParameters();
//        List<String> supportedFlashModes = params.getSupportedFlashModes();
//        if (supportedFlashModes != null && !supportedFlashModes.isEmpty()) {
//            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
//                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                mCamera.setParameters(params);
//                mIsTurnOnTheTorch = true;
//            }
//        }
    }

//    /**
//     * turn off the torch.
//     */
//    public void turnOffTheTorch() {
//        if (!mIsTurnOnTheTorch)
//            return;
//        if (!mIsPreviewing) return;
//        Camera.Parameters params = mCamera.getParameters();
//        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//        mCamera.setParameters(params);
//        mIsTurnOnTheTorch = false;
//    }

    /**
     * is turn on the torch.
     *
     * @return
     */
    public boolean isTurnOnTheTorch() {
        return mIsTurnOnTheTorch;
    }

    /**
     * release the camera resources.
     */
    public void release() {
//        if (!cameraIsNull()) {
            if (mIsPreviewing) {
                stopPreview();
            }
//            mCamera.release();
//            mCamera = null;
//        }
        mCameraEGLRender.release();
        mSurfaceTexture.release();
        EGLUtils.deleteTexture(mOESTextureId);
    }





    /**
     * get the best best preview resolution.(the ratio of the two is closest)
     *
     * @param resolution
     * @return
     */
    private Camera.Size adaptPreviewResolution(Camera.Size resolution) {

        float diff = 100f;
        float xdy = (float) resolution.width / (float) resolution.height;
        Camera.Size best = null;
//        for (Camera.Size size : mCamera.getParameters().getSupportedPreviewSizes()) {
//            Log.d(TAG, "supportPreviewSize=" + size.width + "*" + size.height);
//            if (size.equals(resolution)) {
//                return size;
//            }
//            float tmp = Math.abs(((float) size.width / (float) size.height) - xdy);
//            if (tmp < diff) {
//                diff = tmp;
//                best = size;
//            }
//        }
        return best;
    }

    /**
     * get the best fps range.
     *
     * @param expectedFps
     * @param fpsRanges
     * @return
     */
    private int[] adaptFpsRange(int expectedFps, List<int[]> fpsRanges) {
        expectedFps *= 1000;
        int[] closestRange = fpsRanges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : fpsRanges) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        return closestRange;
    }

    /**
     * judge camera is null.
     *
     * @return
     */
//    private boolean cameraIsNull() {
//        if (mCamera == null) {
//            Log.w(TAG, "camera is null");
//            return true;
//        }
//        return false;
//    }

    /**********camera view build params setting functions***************/

    /**
     * set preview format.
     *
     * @param previewFormat
     */
    public void setPreviewFormat(int previewFormat) {
        if (mPreviewFormat == previewFormat)
            return;
        mPreviewFormat = previewFormat;

    }

    /**
     * get preview format.
     */
    public int getPreviewFormat() {
        return mPreviewFormat;
    }


    /**
     * set expected preview FPS.
     *
     * @param previewFps
     */
    public void setPreviewFps(int previewFps) {
        if (mPreviewFps == previewFps)
            return;
        mPreviewFps = previewFps;
    }

    /**
     * get preview FPS.
     */
    public int getPreviewFps() {
        return mPreviewFps;
    }

    /**
     * set expected preview resolution
     *
     * @param width
     * @param height
     */
    public void setPreviewResolution(int width, int height) {
        if (mPreviewWidth == width && mPreviewHeight == height)
            return;

        mPreviewWidth = width;
        mPreviewHeight = height;
    }

    /**
     * get preview resolution
     *
     * @return
     */
    public int[] getPreviewResolution() {
        return new int[]{mPreviewWidth, mPreviewHeight};
    }



    /**
     * rgba data callback interface.
     */
    public interface OnGetRGBAFrameCallback {
        void onGetRGBAFrame(byte[] data);
    }

}
