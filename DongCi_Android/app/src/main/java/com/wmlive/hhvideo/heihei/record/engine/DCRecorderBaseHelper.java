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
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.record.config.RecordSettingSDK;
import com.wmlive.hhvideo.heihei.record.listener.RecordListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.uird.ResultConstants;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.widget.RecordMenuView;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;
import java.io.File;
import static com.wmlive.hhvideo.utils.ToastUtil.showToast;

/**
 * 录制帮助，提供录制的相关方法，
 * 去掉其中的 业务逻辑部分
 */
public class DCRecorderBaseHelper {
    public final String TAG = "DCRecorderHelper";
    public static final String KEY_USE_FRONT_CAMERA = "key_use_front_camera";
    public static final String KEY_USE_BEAUTY = "key_use_beauty";
    public static final String PREFIX_VIDEO_DIR = "video_";//视频目录
    public static final String PREFIX_BASE_DIR = "record_";//作品目录
    public static final String PREFIX_COMBINE_FILE = "combine_";//合成的视频
    public static final String PREFIX_WEBP_FILE = "video_webp_";//webp图片
    public static final String PREFIX_COVER_FILE = "video_cover_";//封面图片
    public static final String SUFFIX_VIDEO_FILE = ".mp4";//录制的视频文件
    public static final String SUFFIX_AUDIO_FILE = ".wav";//录制的音频文件
    public static final long MAX_FILE_SIZE = 500 << 20;
    public String tempVideoPath;
    private RecordMenuView recordMenu;
    private String tempAudioPath;
    RecordSettingSDK recordSetting;
    BaseCompatActivity context;
    DCRecorderCore dcRecorderCore;


    /**
     * 选中某一个小框，进行录制切换到当前页面
     */
    public void selectCamera(RelativeLayout relativeLayout) {
        previewCamera(relativeLayout);
        setCurrentCameraConfig();
    }

    public void startRecord(ShortVideoEntity videoEntity) {
        if (!getDcRecorderCore().isRecording()) {
            if (!getDcRecorderCore().isPreparing()) {
                if (prepareDir(false, videoEntity)) {
//                    tempVideoPath = Environment.getExternalStorageDirectory()+File.separator+"Dongci"+File.separator+System.currentTimeMillis()+".mp4";
                    KLog.i(TAG, "====开始录制；；>" + videoEntity.baseDir);
                    setTempAudioPath(null);
                    tempVideoPath = RecordFileUtil.createVideoFile(videoEntity.baseDir);
                    KLog.i(TAG, "====开始录制-videoPath>" + tempVideoPath);
                    if (!TextUtils.isEmpty(tempVideoPath)) {
                        TextureMovieEncoder.baseTimeStamp = 0;
                        startDelayRecord(tempVideoPath, 0);//视频速度
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
    }

    public boolean stopRecord() {
        Log.i("recordActivitySdk", "stopRecord_373：" + System.currentTimeMillis());
        if (getDcRecorderCore().isRecording() || getDcRecorderCore().isPreparing()) {
            getDcRecorderCore().stopRecord();
            return true;
        }
        return false;
    }


    public String getTempAudioPath() {
        if (tempVideoPath == null)
            return null;
        if (tempAudioPath == null)
            tempAudioPath = tempVideoPath + SUFFIX_AUDIO_FILE;
        return tempAudioPath;
    }

    public void setTempAudioPath(String path) {
        tempAudioPath = path;
    }



    public DCRecorderBaseHelper(BaseCompatActivity context) {
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

    RecordListener recordListener;

    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    private DCCameraListener dcCameraListener = new DCCameraListener() {

        @Override
        public void onCamera(int var1, String var2) {
            if (var1 == ResultConstants.ERROR_CAMERA_OPEN_FAILED) {
//                打开相机失败处理
                new PermissionDialog(context, 20).show();
            }
        }

        @Override
        public void onPrepared(int var1, String var2) {
            KLog.i("=======onPrepared result:" + var1 + " ,s:" + var2);
            if (var1 == ResultConstants.SUCCESS) {


            } else {
                KLog.i("=======onPrepared failed");
            }
        }

        @Override
        public void onPermissionFailed(int var1, String var2) {
            if (var1 == ResultConstants.PERMISSION_FAILED) {
                // 权限被拒绝处理
            }
        }

        @Override
        public void onGetRecordStatus(final int position) {
            //正在录制刷新
            KLog.i("=====onGetRecordStatus:" + position);
            if (recordListener != null)
                recordListener.onProgress(position);
        }

        @Override
        public void onRecordBegin(int var1, String var2) {
            KLog.i("=====onRecordBegin:" + var1 + " ,s:" + var2);
            if (var1 == DCCameraConfig.SUCCESS) {//开始录制

            } else {
                showToast("录制初始化失败，请稍后再试");
            }
        }

        @Override
        public void onRecordFailed(int var1, String var2) {

        }

        @Override
        public void onRecordEnd(final int var1, String var2) {
            //录制结束
            if (recordListener != null)
                recordListener.onRecordEnd(var1, tempVideoPath, getTempAudioPath());
        }
    };

    /**
     * 初始化配置
     */
    public void initRecorderConfig(RecordMenuView menuView) {
        this.recordMenu = menuView;
        DCRecorderConfig dcRecorderConfig = new DCRecorderConfig();
        dcRecorderConfig.setVideoWidth(recordSetting.VIDEO_WIDTH_MV);
        dcRecorderConfig.setVideoHeight(recordSetting.VIDEO_HEIGHT_MV);
        dcRecorderConfig.setEnableFront(true);
        dcRecorderConfig.setCanBeautity(true);
        dcRecorderConfig.setEnableFrontMirror(true);//前置 镜像
        dcRecorderConfig.setBitrate(recordSetting.videoRecordBitrate);
        dcRecorderConfig.setFps(recordSetting.videoFrameRate);
        dcRecorderConfig.setKeyFrameTime(0);
        dcRecorderConfig.setAutoFocus(true);
        dcRecorderConfig.setAutoFocusRecording(true);
        getDcRecorderCore().setEncoderConfig(dcRecorderConfig);
        // 回复本地配置信息
        //useFront();
    }


    /**
     * Camera 预览
     *
     * @param relativeLayout
     */
    public void previewCamera(RelativeLayout relativeLayout) {
        getDcRecorderCore().recycleCameraView();
        getDcRecorderCore().init((Activity) relativeLayout.getContext());
        getDcRecorderCore().initConfig();// 使用默认的值
        Log.d("TAG", "previewCamera--->" + DCRecorderCore.config);
        getDcRecorderCore().prepare(relativeLayout, dcCameraListener);
        getDcRecorderCore().onResume();
        //设置滤镜
        getDcRecorderCore().switchFilter(0);
    }


    /**
     * 真正播放开始
     *
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


    public void flashClick() {
        if (!getDcRecorderCore().isFaceFront()) {
            getDcRecorderCore().setFlashMode(!getDcRecorderCore().getFlashMode());
        }
        recordMenu.setFlashEnable(!getDcRecorderCore().isFaceFront(), getDcRecorderCore().getFlashMode());
    }

    public void beautyClick() {
        if (getDcRecorderCore().isSuuportBeautify()) {
            getDcRecorderCore().enableBeautify(!getDcRecorderCore().isBeautifyEnabled());
            boolean beauty = getDcRecorderCore().isBeautifyEnabled();
            // 按钮状态
            recordMenu.setBeautyEnable(true, beauty);
            showToast((beauty ? "开启" : "关闭") + "美颜");
        } else {
            showToast("不支持美颜");
        }
    }

    /**
     * 切换滤镜
     *
     * @param index
     */
    public void switchFilter(int index) {
        getDcRecorderCore().switchFilter(index);
    }

    public void switchClick() {
        getDcRecorderCore().switchCamera(getDcRecorderCore().isFaceFront() ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
        recordMenu.setFlashEnable(!getDcRecorderCore().isFaceFront(), getDcRecorderCore().getFlashMode());
    }

    public boolean isBeautifyEnabled() {
        return getDcRecorderCore().isBeautifyEnabled();
    }

    public boolean getFlashMode() {
        return getDcRecorderCore().getFlashMode();
    }


    /**
     * 选择该摄像头后 ，设置相应的配置信息
     */
    private void setCurrentCameraConfig() {
        //选择相应的滤镜，前后摄像头，美颜

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
     * 录制自动结束，到时间了
     */
    private void onRecordAutoEnd() {
        stopRecord();
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
     *
     * @return
     */
    private String getAudioFileName() {
        if (tempVideoPath == null)
            return tempVideoPath;
        if (tempVideoPath.length() > 4)
            return tempVideoPath.substring(0, tempVideoPath.length() - 4) + SUFFIX_AUDIO_FILE;
        else
            return tempVideoPath + SUFFIX_AUDIO_FILE;
    }


}
