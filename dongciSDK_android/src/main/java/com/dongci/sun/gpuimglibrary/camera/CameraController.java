package com.dongci.sun.gpuimglibrary.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.api.apiTest.KLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 相机的管理类 主要是Camera的一些设置
 * 包括预览和录制尺寸、闪光灯、曝光、聚焦、摄像头切换等
 */

public class CameraController implements ICamera {
    /**
     * 相机的宽高及比例配置
     */
    private ICamera.Config mConfig;
    /**
     * 相机实体
     */
    private Camera mCamera;
    /**
     * 预览的尺寸
     */
    private Camera.Size preSize;
    /**
     * 实际的尺寸
     */
    private Camera.Size picSize;

    private Point mPreSize;
    private Point mPicSize;

    private int cameraId;

    public CameraController() {
        /**初始化一个默认的格式大小*/
        mConfig = new ICamera.Config();
        mConfig.minPreviewWidth = 640;
        mConfig.minPictureWidth = 640;
        mConfig.rate = 1.333f;
        mConfig.autoFocus = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
        mConfig.flashModel = false;//闪光灯
        mConfig.fps = 24;
    }

    public void open(int cameraId) {
        try {
            if (mCamera == null) {
                mCamera = Camera.open(cameraId);
            }
            this.cameraId = cameraId;

            if (mCamera != null) {
                /**选择当前设备允许的预览尺寸*/
                Camera.Parameters param = mCamera.getParameters();
                preSize = getPropPreviewSize(param.getSupportedPreviewSizes(), mConfig.rate,
                        mConfig.minPictureWidth);
                KLog.i("getPropPreviewSize-result2>"+preSize.width+"height:>"+preSize.height);
                picSize = getPropPictureSize(param.getSupportedPictureSizes(), mConfig.rate,
                        mConfig.minPictureWidth);
                param.setPictureSize(picSize.width, picSize.height);
                param.setPreviewSize(preSize.width, preSize.height);
                param.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
                if (cameraId == 0) {//拍照模式，自动对焦
                    param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {//打开闪光灯,后置摄像头，
                    param.setFlashMode(mConfig.flashModel ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);
                }
//                param.setPreviewFpsRange(24000,24000);
//                int[] range = new int[]{};
//                param.getPreviewFpsRange(range);
//                Log.i("Fps0range-->","range-->"+range);
                mCamera.setParameters(param);
                mCamera.setDisplayOrientation(90);

                Camera.Size pre = param.getPreviewSize();
                Camera.Size pic = param.getPictureSize();
                mPicSize = new Point(pic.height, pic.width);
                mPreSize = new Point(pre.height, pre.width);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置自动对焦
     *
     * @param autoFocus
     */
    public void setAutoFocus(String autoFocus) {
        mConfig.autoFocus = autoFocus;
        if (mCamera != null) {
            Camera.Parameters param = mCamera.getParameters();
            param.setFocusMode(autoFocus);
            //mCamera.setParameters(param);
        }
    }

    /**
     * 打开闪光灯
     *
     * @param flashModel
     */
    public void setFlashModel(boolean flashModel) {
        mConfig.flashModel = flashModel;
        if (mCamera != null) {
            Camera.Parameters param = mCamera.getParameters();
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {//打开闪光灯,后置摄像头，
                param.setFlashMode(mConfig.flashModel ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
            }
            mCamera.setParameters(param);
        }
    }

    public boolean getFlashModel() {
        return mConfig.flashModel;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                Log.e("hero", "----setPreviewTexture");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setConfig(Config config) {
        this.mConfig = config;
    }

    public Config getmConfig() {
        return mConfig;
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreSize.x, mPreSize.y);
                }
            });
        }
    }


    @Override
    public void preview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    @Override
    public Point getPreviewSize() {
        return mPreSize;
    }

    @Override
    public Point getPictureSize() {
        return mPicSize;
    }

    @Override
    public boolean close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return false;
    }

    /**
     * 手动聚焦
     *
     * @param point 触屏坐标 必须传入转换后的坐标
     */
    public void onFocus(Point point, Camera.AutoFocusCallback callback) {
        Camera.Parameters parameters = mCamera.getParameters();
        boolean supportFocus = true;
        boolean supportMetering = true;
        //不支持设置自定义聚焦，则使用自动聚焦，返回
        if (parameters.getMaxNumFocusAreas() <= 0) {
            supportFocus = false;
        }
        if (parameters.getMaxNumMeteringAreas() <= 0) {
            supportMetering = false;
        }
        List<Camera.Area> areas = new ArrayList<Camera.Area>();
        List<Camera.Area> areas1 = new ArrayList<Camera.Area>();
        //再次进行转换
        point.x = (int) (((float) point.x) / GpuConfig.screenWidth * 2000 - 1000);
        point.y = (int) (((float) point.y) / GpuConfig.screenHeight * 2000 - 1000);

        int left = point.x - 300;
        int top = point.y - 300;
        int right = point.x + 300;
        int bottom = point.y + 300;
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
        areas1.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
        if (supportFocus) {
            parameters.setFocusAreas(areas);
        }
        if (supportMetering) {
            parameters.setMeteringAreas(areas1);
        }

        try {
            mCamera.setParameters(parameters);// 部分手机 会出Exception（红米）
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);
        float minRate = 100;
        int sencondResult = 0 ;
        int i = 0;
        for (Camera.Size s : list) {
            KLog.i("getPropPreviewSize->"+s.width+"height:>"+s.height);
            if ((s.height >= minWidth) && equalRate(s, th)) {//符合要求
                break;
            } else if((s.height >= minWidth)){
                float r = (float) (s.width) / (float) (s.height);
                float chazhi = Math.abs(r - th);
                if(chazhi<minRate){
                    minRate = chazhi;
                    sencondResult = i;
                }
            }
            i++;
        }
        if (i == list.size()) {//如果都不符合
            if(sencondResult>0){
                i = sencondResult;
            }else {
                i = 0;
            }

        }
        KLog.i("getPropPreviewSize-result>"+list.get(i).width+"height:>"+list.get(i).height);
        return list.get(i);
    }

    private static boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    private Comparator<Camera.Size> sizeComparator = new Comparator<Camera.Size>() {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return -1;
            } else {
                return 1;
            }
        }
    };
}
