package com.dongci.sun.gpuimglibrary.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.dongci.sun.gpuimglibrary.api.listener.DCCameraListener;
import com.dongci.sun.gpuimglibrary.gles.CameraDrawer;
import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.common.SLVideoTool;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "CameraView";
    private Activity mContext;

    private CameraDrawer mCameraDrawer;
    private CameraController mCamera;

    private int dataWidth = 0, dataHeight = 0;

    private boolean isSetParm = false;

    private static int cameraId = 1;

    DCCameraListener dcCameraListener;

    private boolean isRecording;

    public CameraView(Activity context) {
        this(context, null);
        mContext = context;
        GpuConfig.context = context;
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    private void init() {
        /**初始化OpenGL的相关信息*/
        setEGLContextClientVersion(2);//设置版本
        setRenderer(this);//设置Renderer
        setRenderMode(RENDERMODE_WHEN_DIRTY);//主动调用渲染
        setPreserveEGLContextOnPause(true);//保存Context当pause时
        setCameraDistance(100);//相机距离

        /**初始化Camera的绘制类*/
        mCameraDrawer = new CameraDrawer(getResources());
        Log.d(TAG, "init: getResources()==" + getResources());
        /**初始化相机的管理类*/
        mCamera = new CameraController();

    }

    public void configCameraId(int cameraId) {
        this.cameraId = cameraId;
        Log.d(TAG, this + "config--->: " + cameraId);
    }

    public void setAutoFocus(boolean focus) {
        mCamera.setAutoFocus(focus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO : Camera.Parameters.FOCUS_MODE_FIXED);
    }

    public void setFlashMode(boolean autoFocus) {
        mCamera.setFlashModel(autoFocus);
    }

    public void setVideoSize(int width, int height) {
        mCameraDrawer.setVideoSize(width, height);
    }

    public void setRecordBitrate(int birate) {
        mCameraDrawer.setBitrate(birate);
    }

    public void setFps(int fps) {
        ICamera.Config config = mCamera.getmConfig();
        config.fps = fps;
        mCamera.setConfig(config);
    }

    public boolean getFlashMode() {
        return mCamera.getFlashModel();
    }

    private void open(int cameraId) {

        Log.d(TAG, this + "open: " + cameraId);

        if (mCamera == null)
            return;
        mCamera.close();

        try {
            mCamera.open(cameraId);
        } catch (Exception e) {//打开摄像头错误
            e.printStackTrace();
            if (dcCameraListener != null)
                dcCameraListener.onCamera(DCCameraConfig.ERROR_CAMERA_OPEN_FAILED, e.toString());
        }

        mCameraDrawer.setCameraId(cameraId);
        final Point previewSize = mCamera.getPreviewSize();
        if (previewSize != null) {
            dataWidth = previewSize.x;
            dataHeight = previewSize.y;
        }
        CameraConstentValues.previewWidth = dataWidth;
        CameraConstentValues.previewHeight = dataHeight;
        Log.d(TAG, "open: " + "dataWidth==" + dataWidth + " dataHeight==" + dataHeight);
        SurfaceTexture texture = mCameraDrawer.getTexture();
        if(texture==null){
            Log.e(TAG, "mCameraDrawer is null " );
            return;
        }

        texture.setOnFrameAvailableListener(this);
        mCamera.setPreviewTexture(texture);
        mCamera.preview();

        setMirror(false);
        Log.d(TAG, "open:-success ");
    }

    public void switchCamera(int cameraId) {
        this.cameraId = cameraId;
        Log.d(TAG, "switchCamera: " + cameraId);
        mCameraDrawer.switchCamera();
        open(cameraId);
    }

    public boolean isFaceFront() {
        return cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated: =" + dataWidth + "height:" + dataHeight);
        mCameraDrawer.onSurfaceCreated(gl, config);
        if (!isSetParm) {
            open(cameraId);
            stickerInit();
        }
        mCameraDrawer.setPreviewSize(dataWidth, dataHeight);
        //准备好了
        if (dcCameraListener != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dcCameraListener.onPrepared(DCCameraConfig.SUCCESS, "");
                }
            });

        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        Log.d(TAG, "onDrawFrame: issetParm=" + isSetParm);
        if (isSetParm) {
            mCameraDrawer.onDrawFrame(gl);
        }
    }

    public void onPrepare(DCCameraListener dcCameraListener) {
        this.dcCameraListener = dcCameraListener;
        mCameraDrawer.setDcCameraListener(dcCameraListener);
        if (isSetParm) {
            open(cameraId);
        }
    }

    /**
     * 每次Activity onResume时被调用,第一次不会打开相机
     */
    @Override
    public void onResume() {
        super.onResume();
        if (isSetParm) {
            open(cameraId);
        }
    }

    public void onDestroy() {
        if (mCamera != null) {
            mCamera.close();
            mCamera = null;
            GpuConfig.context = null;
        }
    }

    /**
     * 摄像头聚焦
     */
    public void onFocus(Point point, Camera.AutoFocusCallback callback) {
        mCamera.onFocus(point, callback);
    }

    public int getCameraId() {
        return cameraId;
    }

    public void changeBeautyLevel(final int level) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.changeBeautyLevel(level);
            }
        });
    }

    public void startRecord() {
        SLVideoTool.startRecordTime = 0;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                isRecording = true;
                SLVideoTool.videoPts = 0;
                SLVideoTool.isStartedToRecordAudio = false;
                mCameraDrawer.startRecord();
            }
        });
    }

    public void stopRecord() {
        Log.d("gpuimg", "stopRecord-->00");
        queueEvent(new Runnable() {
            @Override
            public void run() {
                isRecording = false;
                SLVideoTool.videoPts = 0;
                mCameraDrawer.stopRecord();
            }
        });
    }

    /**
     * 是否在录制
     *
     * @return
     */
    public boolean isRecording() {
        return mCameraDrawer.isRecording();
    }

    /**
     * 0~4  0: 没有美颜效果
     *
     * @param level
     */
    public void setBeautyLevel(int level) {
        mCameraDrawer.changeBeautyLevel(level);
    }

    public int getBeautyLevel() {
        return mCameraDrawer.getBeautyLevel();
    }

    public void setSavePath(String path) {
        mCameraDrawer.setSavePath(path);
    }

    public void resume(final boolean auto) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.onResume(auto);
            }
        });
    }

    public void pause(final boolean auto) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.onPause(auto);
            }
        });
    }

    public void switchFilter(final GPUImageFilter filter) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (filter != null) {
                    mCameraDrawer.setFilter(filter);
                }
            }
        });
    }

    public void addWaterMarker(int x, int y, int width, int height, int imgRes) {
        mCameraDrawer.addWaterFilter(x, y, width, height, imgRes);
    }

    public void clearWaterMarker() {
        mCameraDrawer.clearWater();
    }

    public void setMirror(boolean mirror) {
        mCameraDrawer.setMirror(mirror);
    }

    public void onTouch(final MotionEvent event) {

    }

    public void stickerInit() {
        if (!isSetParm && dataWidth > 0 && dataHeight > 0) {
            isSetParm = true;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (isRecording) {
            if (SLVideoTool.isStartedToRecordAudio) {
                SLVideoTool.videoPts = System.nanoTime();
            }
        }
        this.requestRender();
    }

    /**
     * 回收cameraview ，等待再次加载
     */
    public void recycleCameraView() {
        mCamera.close();
    }


}
