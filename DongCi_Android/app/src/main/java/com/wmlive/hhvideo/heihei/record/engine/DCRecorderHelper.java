package com.wmlive.hhvideo.heihei.record.engine;

import android.app.Activity;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.dongci.sun.gpuimglibrary.api.DCRecorderConfig;
import com.dongci.sun.gpuimglibrary.api.DCRecorderCore;
import com.dongci.sun.gpuimglibrary.api.listener.DCCameraListener;
import com.dongci.sun.gpuimglibrary.coder.TextureMovieEncoder;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.record.activity.RecordActivitySdk;
import com.wmlive.hhvideo.heihei.record.config.RecordSettingSDK;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.uird.ResultConstants;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.widget.ExtBtnRecord;
import com.wmlive.hhvideo.heihei.record.widget.FullRecordView;
import com.wmlive.hhvideo.heihei.record.widget.RecordMenuView;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;

import java.io.File;

import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.utils.ToastUtil.showToast;

/**
 * 录制帮助，提供录制的相关方法，
 *
 *
 */
public class DCRecorderHelper {

    private final String TAG = "DCRecorderHelper";

    private static final String KEY_USE_FRONT_CAMERA = "key_use_front_camera";
    private static final String KEY_USE_BEAUTY = "key_use_beauty";

    public static final String PREFIX_VIDEO_DIR = "video_";//视频目录
    public static final String PREFIX_BASE_DIR = "record_";//作品目录
    public static final String PREFIX_REVERSE_FILE = "reverse_";//倒序视频
    public static final String PREFIX_EDITING_FILE = "editing_";//生成的单个视频
    public static final String PREFIX_COMBINE_FILE = "combine_";//合成的视频
    public static final String PREFIX_WEBP_FILE = "video_webp_";//webp图片
    public static final String PREFIX_COVER_FILE = "video_cover_";//封面图片

    public static final String SUFFIX_VIDEO_FILE = ".mp4";//录制的视频文件
    public static final String SUFFIX_AUDIO_FILE = ".wav";//录制的音频文件
    public static final long MAX_FILE_SIZE = 500 << 20;

    public boolean isFullRecord;

    RecordSettingSDK recordSetting;
    private RecordMenuView recordMenu;
    private FullRecordView flFullRecord;
    RecordActivitySdk context;
    public String tempVideoPath;

    public String getTempAudioPath() {
        if(tempVideoPath==null)
            return null;
        if(tempAudioPath==null)
            tempAudioPath = tempVideoPath+SUFFIX_AUDIO_FILE;//.substring(0,tempVideoPath.length()-4)
        return tempAudioPath;
    }

    public void setTempAudioPath(String path){
        tempAudioPath = path;
    }

    private String tempAudioPath;

    DCRecorderCore dcRecorderCore;

    public DCRecorderHelper(RecordActivitySdk context) {
        this.context = context;
        recordSetting = RecordSettingSDK.defaultSetting();
        getDcRecorderCore();
    }

    public DCRecorderCore getDcRecorderCore() {
        if (dcRecorderCore == null) {
            dcRecorderCore = DCRecorderCore.getDcRecorderCore();
            dcRecorderCore.init(context);
        }
        return dcRecorderCore;
    }

    private DCCameraListener dcCameraListener = new DCCameraListener() {

        @Override
        public void onCamera(int var1, String var2) {
            if (var1 == ResultConstants.ERROR_CAMERA_OPEN_FAILED) {
//                //  打开相机失败处理
////                showToast("打开相机失败，请稍后再试");
                new PermissionDialog(context, 20).show();
            }
        }

        @Override
        public void onPrepared(int var1, String var2) {
            KLog.i("=======onPrepared result:" + var1 + " ,s:" + var2);
            if (var1 == ResultConstants.SUCCESS) {
                if (isFullRecord) {//
                    flFullRecord.getGlTouchView().onPrepared();
                } else {

                }
//                getDcRecorderCore().cameraAutoFocus(true);
            } else {
                KLog.i("=======onPrepared failed");
            }
        }

        @Override
        public void onPermissionFailed(int var1, String var2) {
            if (var1 == ResultConstants.PERMISSION_FAILED) {
//                // 权限被拒绝处理
            }
        }

        @Override
        public void onGetRecordStatus(final int position) {
            context.getWeakHandler().post(new Runnable() {
                @Override
                public void run() {
                    onGetRecordStatus1(position);
                }
            });
        }

        @Override
        public void onRecordBegin(int var1, String var2) {
            KLog.i("=====onRecordBegin:" + var1 + " ,s:" + var2);
            if (var1 == DCCameraConfig.SUCCESS) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playMusicAndVideo();
                    }
                });
            } else {
                showToast("录制初始化失败，请稍后再试");
//                stopMusicAndVideo();
            }
        }

        @Override
        public void onRecordFailed(int var1, String var2) {

        }

        @Override
        public void onRecordEnd(final int var1, String var2) {
            KLog.i("=====onRecordEnd:" + var1 + " ,s:" + var2);
            if(var1==DCCameraConfig.RECORD_PRE)
                return;
//            try {
//                Thread.sleep(100);
//            } catch (java.lang.InterruptedException e) {
//                e.printStackTrace();
//            }
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.onRecordEnd(var1);
                }
            });

        }
    };

    /**
     * 初始化配置
     */
    public void initRecorderConfig() {
        DCRecorderConfig dcRecorderConfig = new DCRecorderConfig();
        dcRecorderConfig.setVideoWidth(recordSetting.videoWidth);
        dcRecorderConfig.setVideoHeight(recordSetting.videoHeight);
        dcRecorderConfig.setEnableFront(isUseFrontCamera());
        dcRecorderConfig.setCanBeautity(isUseBeauty());
        dcRecorderConfig.setEnableFrontMirror(true);//前置 镜像
        dcRecorderConfig.setBitrate(recordSetting.videoRecordBitrate);
        dcRecorderConfig.setFps(recordSetting.videoFrameRate);
        dcRecorderConfig.setKeyFrameTime(0);
        dcRecorderConfig.setAutoFocus(true);
        dcRecorderConfig.setAutoFocusRecording(true);
        getDcRecorderCore().setEncoderConfig(dcRecorderConfig);
        // 回复本地配置信息
//        useFront();
    }


    /**
     * Camera 预览
     * @param relativeLayout
     */
    public void previewCamera(RelativeLayout relativeLayout) {
        getDcRecorderCore().recycleCameraView();
        getDcRecorderCore().init((Activity) relativeLayout.getContext());
        getDcRecorderCore().initConfig();// 使用默认的值
        Log.d("TAG", "previewCamera--->" + dcCameraListener);
        getDcRecorderCore().prepare(relativeLayout, dcCameraListener);
        getDcRecorderCore().onResume();
    }

    public boolean startRecord(ShortVideoEntity videoEntity, ExtBtnRecord btRecorder) {
        if (videoEntity.getDuringMS()
                >= RecordManager.get().getSetting().maxVideoDuration) {
            showToast(String.format(btRecorder.getContext().getString(R.string.over_max_record_time), RecordManager.get().getSetting().maxVideoDuration / 1000));
            stopRecord();
            return false;
        }

        if (!getDcRecorderCore().isRecording()) {
            if (!getDcRecorderCore().isPreparing()) {
                btRecorder.setImageResource(R.drawable.btn_recorder_end);
                if (prepareDir(false, videoEntity)) {
                    //临时测试使用
//                    tempVideoPath = Environment.getExternalStorageDirectory()+File.separator+"Dongci"+File.separator+System.currentTimeMillis()+".mp4";
                    setTempAudioPath(null);
                    tempVideoPath = RecordFileUtil.createVideoFile(videoEntity.baseDir);
//                    tempAudioPath = RecordFileUtil.createAudioFile(videoEntity.baseDir);
                    if (!TextUtils.isEmpty(tempVideoPath)) {
                        TextureMovieEncoder.baseTimeStamp = 0;
//                        VideoEncoderCore.mWriteStop = false;
                        startDelayRecord(tempVideoPath, 0);//视频速度
                        return true;
                    } else {
                        showToast("创建文件失败");
                        stopRecord();
                    }
                } else {
                    showToast("创建文件失败");
                    stopRecord();
                }
            } else {
                KLog.e(TAG, "====录制准备中");
            }
        } else {
            KLog.e(TAG, "====已经在录制中");
        }
        return false;
    }

    public boolean stopRecord() {
        Log.i("recordActivitySdk", "stopRecord_373：" + System.currentTimeMillis());
        if (getDcRecorderCore().isRecording() || getDcRecorderCore().isPreparing()) {
            getDcRecorderCore().stopRecord();
            return true;
        }
        return false;
    }














    /**
     * 初始化控件，将控件传递过来，进行相应的改变。
     */
    public void initRecordView(RecordMenuView recordMenu, FullRecordView flFullRecord, ExtBtnRecord btRecorder) {
        this.recordMenu = recordMenu;
        this.flFullRecord = flFullRecord;
//        this.btRecorder = btRecorder;
    }


    public void flashClick() {
        if (!getDcRecorderCore().isFaceFront()) {
            getDcRecorderCore().setFlashMode(!getDcRecorderCore().getFlashMode());
        }
        //view state
        recordMenu.setFlashEnable(!getDcRecorderCore().isFaceFront(), getDcRecorderCore().getFlashMode());
    }

    public void beautyClick() {
        if (getDcRecorderCore().isSuuportBeautify()) {
            getDcRecorderCore().enableBeautify(!getDcRecorderCore().isBeautifyEnabled());
            boolean beauty = getDcRecorderCore().isBeautifyEnabled();
            //local
            setUseBeauty(beauty);
            // 按钮状态
            recordMenu.setBeautyEnable(true, beauty);
            showToast((beauty ? "开启" : "关闭") + "美颜");
        } else {
            showToast("不支持美颜");
        }
    }

    /**
     * 切换滤镜
     * @param index
     */
    public void switchFilter(int index) {
        getDcRecorderCore().switchFilter(index);
    }

    public void switchClick() {
        getDcRecorderCore().switchCamera(getDcRecorderCore().isFaceFront() ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
        //local
        setUseFrontCamera(getDcRecorderCore().isFaceFront());
        // view state
        recordMenu.setFlashEnable(!getDcRecorderCore().isFaceFront(), getDcRecorderCore().getFlashMode());
    }

    public boolean isBeautifyEnabled() {
        return getDcRecorderCore().isBeautifyEnabled();
    }

    public boolean getFlashMode() {
        return getDcRecorderCore().getFlashMode();
    }


    /**
     * 选中某一个小框，进行录制切换到当前页面
     */
    public void selectSmallCamera(RelativeLayout relativeLayout) {
        previewCamera(relativeLayout);
        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(context.getCurrentPreviewIndex());
        context.selectedPositionChanged(videoEntity.getFilterId());
    }

    public void releaseCamera(){
        getDcRecorderCore().recycleCameraView();
    }

    /**
     * 重置摄像头，回收摄像头
     */
    public void resetPreItem() {
        getDcRecorderCore().recycleCameraView();
        getDcRecorderCore().onDestroy();
    }


    public void onDestroy() {
        Log.i("recordActivitySdk", "onDestroy_382：" + System.currentTimeMillis());
        resetPreItem();
        getDcRecorderCore().release();
    }

    public boolean isRecording() {
        return getDcRecorderCore().isRecording();
    }


    /**
     * 返回键，停止录像操作
     */
    public void onBackPressed() {

    }

    /**
     * 真正播放开始
     * @param path
     * @param speed
     */
    public void startDelayRecord(String path, double speed) {
        try {
            Log.i("recordActivitySdk", "开始录制：" + System.currentTimeMillis() + " path：" + path);
            getDcRecorderCore().startRecord(path, speed);
        } catch (Exception e) {
            e.printStackTrace();
            stopRecord();
        }
    }

    /**
     * 更新进度
     * @param position
     */
    public void onGetRecordStatus1(int position) {
        if (RecordManager.get().getProductEntity() == null) {
            context.toastFinish();
            return;
        }
        KLog.i("=======onGetRecordStatus position:" + position + " ,Max:" + RecordManager.get().getShortVideoEntity(context.getCurrentPreviewIndex()).getDuringMS());
        int nowP = context.recordActivitySdkView.recordUpdatePosition(position);
        if (nowP >= RecordManager.get().getSetting().maxVideoDuration+500) {
            ToastUtil.showToast("已经超过最长录制时间");
            onRecordAutoEnd();
        }
    }

    /**
     * 录制自动结束，到时间了
     */
    private void onRecordAutoEnd(){
        stopRecord();
        context.recordActivitySdkView.onRecordEnd(context.getCurrentPreviewIndex());
    }


    private void playMusicAndVideo() {
        KLog.i("======playMusicAndVideo1");
        context.playAllVideo(false);
    }

    public boolean isUseFrontCamera() {
        return SPUtils.getBoolean(DCApplication.getDCApp(), KEY_USE_FRONT_CAMERA, true);
    }

    public boolean isUseBeauty() {
        return SPUtils.getBoolean(DCApplication.getDCApp(), KEY_USE_BEAUTY, true);
    }

    public void setUseBeauty(boolean useBeauty) {
        SPUtils.putBoolean(DCApplication.getDCApp(), KEY_USE_BEAUTY, useBeauty);
    }

    public void setUseFrontCamera(boolean useFrontCamera) {
        SPUtils.putBoolean(DCApplication.getDCApp(), KEY_USE_FRONT_CAMERA, useFrontCamera);
    }


    /**
     * 创建各种路径
     *
     * @param createAllVideoDir
     * @param currentVideoEntity
     * @return
     */
    public boolean prepareDir(boolean createAllVideoDir, ShortVideoEntity currentVideoEntity) {
        if (TextUtils.isEmpty(RecordManager.get().getProductEntity().baseDir)) {
            String productPath = RecordFileUtil.createTimestampDir(RecordFileUtil.getTempDir(), "");
            if (TextUtils.isEmpty(productPath)) {
                KLog.i("====创建productDir文件夹失败");
                return false;
            }
            RecordManager.get().getProductEntity().baseDir = productPath;
        }

        if (!RecordFileUtil.createDir(new File(RecordManager.get().getProductEntity().baseDir))) {
            KLog.i("====创建productDir文件夹失败");
            return false;
        }

        if (createAllVideoDir) {
            int size = RecordManager.get().getProductEntity().shortVideoList.size();
            for (int i = 0; i < size; i++) {
                ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(i);
                if (TextUtils.isEmpty(videoEntity.baseDir)) {
                    String shortVideoDir = RecordFileUtil.createTimestampDir(RecordManager.get().getProductEntity().baseDir, RecordManager.PREFIX_VIDEO_DIR);
                    if (TextUtils.isEmpty(shortVideoDir)) {
                        KLog.i("====创建shortVideoDir文件夹失败");
                        return false;
                    }
                    videoEntity.baseDir = shortVideoDir;
                }

                if (!RecordFileUtil.createDir(new File(videoEntity.baseDir))) {
                    KLog.i("====创建shortVideoDir文件夹失败");
                    return false;
                }
            }
        } else {
//            ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
            if (TextUtils.isEmpty(currentVideoEntity.baseDir)) {
                String shortVideoDir = RecordFileUtil.createTimestampDir(RecordManager.get().getProductEntity().baseDir, RecordManager.PREFIX_VIDEO_DIR);
                if (TextUtils.isEmpty(shortVideoDir)) {
                    KLog.i("====创建shortVideoDir文件夹失败");
                    return false;
                }
                currentVideoEntity.baseDir = shortVideoDir;
            }

            if (!RecordFileUtil.createDir(new File(currentVideoEntity.baseDir))) {
                KLog.i("====创建shortVideoDir文件夹失败");
                return false;
            }
        }
        return true;
    }

    /**
     * 获取音频文件名称
     * @return
     */
    private String getAudioFileName(){
        if(tempVideoPath==null)
            return tempVideoPath;
        if(tempVideoPath.length()>4)
            return tempVideoPath.substring(0,tempVideoPath.length()-4)+SUFFIX_AUDIO_FILE;
        else
            return tempVideoPath+SUFFIX_AUDIO_FILE;
    }



}
