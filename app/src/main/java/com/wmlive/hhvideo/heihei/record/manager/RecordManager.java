package com.wmlive.hhvideo.heihei.record.manager;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.text.TextUtils;

import com.danikula.videocache.file.TotalSizeLruDiskUsage;
import com.dongci.sun.gpuimglibrary.player.script.DCScriptManager;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ProductExtendEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//import com.rd.vecore.MusicPlayer;
//import com.rd.vecore.RdVECore;
//import com.rd.vecore.models.VideoConfig;

/**
 * Created by lsq on 8/25/2017.
 * 视频录制管理类
 */

public class RecordManager {
    private static final String KEY_USE_FRONT_CAMERA = "key_use_front_camera";
    private static final String KEY_USE_BEAUTY = "key_use_beauty";

    public static final String PREFIX_VIDEO_DIR = "video_";//视频目录
    public static final String PREFIX_BASE_DIR = "record_";//作品目录
    public static final String PREFIX_REVERSE_FILE = "reverse_";//倒序视频
    public static final String PREFIX_EDITING_FILE = "editing_";//生成的单个视频
    public static final String PREFIX_COMBINE_FILE = "combine_";//合成的视频
    public static final String PREFIX_SPLIT_FILE = "split_";//合成的视频
    public static final String PREFIX_WEBP_FILE = "video_webp_";//webp图片
    public static final String PREFIX_COVER_FILE = "video_cover_";//封面图片
    public static final String PREFIX_CREATIVE_AUDIO_FILE = "creative_audio_";//创意视频地址前缀
    public static final String PREFIX_CREATIVE_VIDEO_FILE = "creative_video_";//创意视频地址前缀

    public static final String SUFFIX_VIDEO_FILE = ".mp4";//录制的视频文件
    public static final String SUFFIX_AUDIO_FILE = ".wav";//录制的音频文件
    public static final String SUFFIX_JSON_FILE = ".json";//录制的音频文件
    public static final long MAX_FILE_SIZE = 500 << 20;

    private Context appContext;
    private boolean isInitialized = false;
    private boolean isDebug;
    private RecordSetting recordSetting;
    private ProductEntity productEntity;
    //    private MusicPlayer musicPlayer;  //配乐预览播放器
    private TotalSizeLruDiskUsage totalSizeLruDiskUsage;

    private static final class Holder {
        static final RecordManager INSTANCE = new RecordManager();
    }

    public static RecordManager get() {
        return Holder.INSTANCE;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void init(Context context, boolean isDebug) {
        if (context == null) {
            KLog.i("=====初始化相机失败", "视频录制管理类未初始化");
            ToastUtil.showToast("视频录制管理类未初始化");
            return;
        }
        this.appContext = context.getApplicationContext();
        this.isDebug = isDebug;
        this.recordSetting = RecordSetting.defaultSetting();
        this.totalSizeLruDiskUsage = new TotalSizeLruDiskUsage(MAX_FILE_SIZE);
        initCore(AppCacheFileUtils.getAppVideoRecordCachePathFile(context), isDebug);
//        if (isDebug) {
//            OSSLog.enableLog();
//        } else {
//            OSSLog.disableLog();
//        }
    }

    private boolean initCore(File dir, boolean isDebug) {
        try {
            if (dir != null) {

            }
        } catch (Exception e) {
            KLog.d("ggq", "e==" + e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 初始化录制的SDK
     * 注意：在调用这个方法之前必须已经获取到运行时读写权限
     *
     * @param context
     * @throws IllegalAccessException
     */
    public boolean initRecordCore(Context context) {
        return initRecordCore(AppCacheFileUtils.getAppVideoRecordCachePathFile(context));
    }

    /**
     * 初始化录制的SDK
     * 注意：在调用这个方法之前必须已经获取到运行时读写权限
     *
     * @param rootPath 视频文件的存储目录
     * @throws IllegalAccessException
     */
    private boolean initRecordCore(File rootPath) {
        if (!isInitialized) {
            File dir = null;
            try {
                dir = initRecordDir(appContext, rootPath);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (dir != null) {
                isInitialized = initCore(dir, isDebug);
            } else {
                isInitialized = false;
            }
        }
        return isInitialized;
    }

    private File initRecordDir(Context context, File rootDir) throws IllegalAccessException {
        if (RecordFileUtil.hasWritePermission(context)) {
            if (rootDir == null) {
                rootDir = RecordFileUtil.getIndividualCacheDirectory(context);
            }
            boolean isOk = (rootDir != null);
            if (rootDir != null && (!rootDir.exists() || !rootDir.isDirectory())) {
                isOk = rootDir.mkdirs();
            }
            KLog.i("=====创建视频根目录" + (isOk ? "成功" : "失败"));
            if (isOk) {
                isOk = RecordFileUtil.initAllDir(rootDir);
                KLog.i("=====创建视频临时目录" + (isOk ? "成功" : "失败"));
            }
            return isOk ? rootDir : null;
        }
        KLog.i("===initRecordDir没有读写权限");
        return null;
    }

    public void initRecorderConfig() {

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

    public RecordSetting getSetting() {
        return recordSetting == null ? RecordSetting.defaultSetting() : recordSetting;
    }

    public void initSetting() {
        this.recordSetting = RecordSetting.initSetting();
    }

    public boolean canCutMusic() {
        return hasMusic() && !getProductEntity().hasVideo();
    }

    public boolean hasMusic() {
        return productEntity != null
                && productEntity.musicInfo != null
                && !TextUtils.isEmpty(productEntity.musicInfo.getMusicPath())
                && new File(productEntity.musicInfo.getMusicPath()).exists();
    }

    /**
     * 调用此方法初始化一个作品
     *
     * @param frameInfo
     */
    public void newProductEntity(FrameInfo frameInfo) {
        productEntity = ProductEntity.createProductEntity(frameInfo);
        productEntity.userId = AccountUtil.getUserId();
    }

    /**
     * 调用此方法初始化一个作品
     * 一般是从数据库中查询出来的ProductEntity
     *
     * @param entity
     */
    public void setProductEntity(ProductEntity entity) {
        productEntity = entity;
        productEntity.userId = AccountUtil.getUserId();
    }

    public void setFrameInfo(FrameInfo frameInfo) {
        getProductEntity().frameInfo = frameInfo;
        if (getProductEntity().shortVideoList == null) {
            getProductEntity().shortVideoList = new ArrayList<>();
        }
        getProductEntity().shortVideoList.clear();
        if (frameInfo != null && frameInfo.getLayout() != null) {
            for (int i = 0, n = frameInfo.getLayout().size(); i < n; i++) {
                productEntity.shortVideoList.add(new ShortVideoEntity());
            }
        }
    }


    public FrameInfo getFrameInfo() {
//        ProductEntity productEntity = getProductEntity();
        if (productEntity == null) {
            return null;
        }
        return getProductEntity().frameInfo;
    }

    public boolean isFrameInfoValid() {
        return getProductEntity() != null
                && getProductEntity().frameInfo != null
                && getProductEntity().frameInfo.getLayout() != null
                && getProductEntity().frameInfo.getLayout().size() > 0;
    }

    /**
     * 调用这个方法前必须保证已经调用过{@link #newProductEntity(FrameInfo)}方法
     *
     * @return
     */
    public ProductEntity getProductEntity() {
        if (productEntity == null) {
            if (RecordManager.get().getFrameInfo() != null)
                RecordManager.get().newProductEntity(RecordManager.get().getFrameInfo());
            else {
                return null;
            }
        }
        return productEntity;
    }

    public ProductEntity getProductEntityMv(FrameInfo frameInfo) {
        if (productEntity == null) {
            if (frameInfo != null)
                RecordManager.get().newProductEntity(frameInfo);
            else {
                return null;
            }
        }
        return productEntity;
    }

    public ShortVideoEntity getShortVideoEntity(int index) {
        if (getProductEntity() == null || CollectionUtil.isEmpty(getProductEntity().shortVideoList)) {
            return new ShortVideoEntity();
        } else {
            int size = getProductEntity().shortVideoList.size();
            index = index < 0 ? 0 : index;
            if (index > size) {
                return new ShortVideoEntity();
            }

            return getProductEntity().shortVideoList.get(index);
        }
    }

    public void setShortVideoEntity(int index, ShortVideoEntity entity) {
        if (getProductEntity() == null || getProductEntity().shortVideoList == null) {
            getProductEntity().shortVideoList = new ArrayList<ShortVideoEntity>();
            getProductEntity().shortVideoList.add(entity);
        } else if (index < getProductEntity().shortVideoList.size()) {
            getProductEntity().shortVideoList.set(index, entity);
        } else {
            getProductEntity().shortVideoList.add(index, entity);
        }
    }

    public ProductExtendEntity getProductExtend() {
        if (getProductEntity().extendInfo == null) {
            getProductEntity().extendInfo = new ProductExtendEntity();
        }
        return getProductEntity().extendInfo;
    }

    public float getVideoDuring(int index) {
        return getProductEntity().shortVideoList.get(index).getDuring();
    }

    public void setMusicInfo(MusicInfoEntity musicInfo) {
        getProductEntity().musicInfo = musicInfo;
    }

    public void setTopicInfo(TopicInfoEntity topicInfo) {
        getProductEntity().topicInfo = topicInfo;
    }

//    public MusicPlayer getMusicPlayer() {
//        if (musicPlayer == null) {
//            musicPlayer = new MusicPlayer(appContext);
//        }
//        return musicPlayer;
//    }

    /**
     * @param musicPath
     * @param recordSpeed
     * @param musicDuring
     * @param start       单位是秒
     * @param end         单位是秒
     */
    public void playMusic(String musicPath, double recordSpeed, float musicDuring, float start, float end) {
        playMusic(musicPath, recordSpeed, musicDuring, start, end, 1.0f, false);
    }

    /**
     * @param musicPath
     * @param recordSpeed
     * @param musicDuring
     * @param start       单位是秒
     * @param end         单位是秒
     * @param autoRepeat
     */
    public void playMusic(String musicPath, double recordSpeed, float musicDuring, float start, float end, boolean autoRepeat) {
        playMusic(musicPath, recordSpeed, musicDuring, start, end, 1.0f, autoRepeat);
    }

    /**
     * 初始化播放音乐
     *
     * @param musicPath
     * @param recordSpeed
     * @param musicDuring
     * @param start
     * @param end
     * @param volume      音量大小 0 - 1f
     * @param autoRepeat
     */
    public void playMusic(String musicPath, double recordSpeed, float musicDuring, float start, float end, @FloatRange(from = 0f, to = 1.0f) float volume, boolean autoRepeat) {
//        getMusicPlayer().reset();
//        getMusicPlayer().setDataSource(musicPath, musicDuring);
//        getMusicPlayer().setSpeed((float) (1 / (recordSpeed == 0 ? 1.0f : recordSpeed)));
//        getMusicPlayer().setTimeRange(start, end);
//        getMusicPlayer().setAutoRepeat(autoRepeat);
//        getMusicPlayer().setVolume(volume);
//        getMusicPlayer().prepare();
//        getMusicPlayer().start();
    }

    /**
     * 播放音乐
     */
    public void startMusic() {
//        if (musicPlayer != null) {
//            musicPlayer.start();
//        }
    }

    /**
     * 暂停音乐
     */
    public void pauseMusic() {
//        if (musicPlayer != null) {
//            musicPlayer.pause();
//        }
    }

    /**
     * 停止音乐
     */
    public void stopMusic() {
//        if (musicPlayer != null) {
//            if (musicPlayer.isPlaying()) {
//                musicPlayer.stop();
//            }
//            musicPlayer.reset();
//        }
    }

    public void releaseMusicPlayer() {
//        if (musicPlayer != null) {
//            musicPlayer.reset();
//            musicPlayer.release();
//            musicPlayer = null;
//        }
    }

    /**
     * 设置配乐混音比例
     *
     * @param musicMixFactor 比例(0-100)
     */
    public void setMusicMixFactor(int musicMixFactor) {
        getProductEntity().musicMixFactor = musicMixFactor;
    }

    /**
     * 设置原音混音比例
     *
     * @param originalMixFactor 比例(0-100)
     */
    public void setOriginalMixFactor(int originalMixFactor) {
        getProductEntity().originalMixFactor = originalMixFactor;
    }

//    public VideoConfig getVideoConfig() {
//        VideoConfig videoConfig = new VideoConfig();
//        videoConfig.setVideoSize(RecordManager.get().getSetting().videoWidth, RecordManager.get().getSetting().videoHeight);
//        videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoPublishBitrate);
//        videoConfig.setAspectRatio(RecordManager.get().getSetting().getVideoRatio());
//        if (RecordSetting.SET_AUDIO_RATE) {
//            videoConfig.setAudioEncodingParameters(1, RecordSetting.AUDIO_SAMPLING_RATE, RecordSetting.AUDIO_ENCODING_BITRATE);
//        }
//        videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//        videoConfig.enableHWDecoder(false);
//        videoConfig.enableHWEncoder(true);
//        return videoConfig;
//    }

    public boolean hasPublishingProduct() {
        return GlobalParams.StaticVariable.sPublishingProductId > 0;
    }

    public void setPublishingProductId(long id) {
        GlobalParams.StaticVariable.sPublishingProductId = id;
    }

    public long getPublishingProductId() {
        return GlobalParams.StaticVariable.sPublishingProductId;
    }

    /**
     * 这个方法只能是在对当前Product进行了编辑才能调用
     */
    public void updateProduct() {
        if (getProductEntity() != null) {
            getProductEntity().productType = ProductEntity.TYPE_EDITING;
            getProductEntity().userId = AccountUtil.getUserId();
            RecordUtil.insertOrUpdateProductToDb(getProductEntity());
        }
    }

    public void trimFile(File dir) {
        if (dir != null && dir.exists()) {
            try {
                totalSizeLruDiskUsage.touch(dir);
            } catch (IOException e) {
                KLog.i("====修剪文件失败：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 发布成功或者存入草稿作品后一定记得清楚这个单例中的所有成员变量
     */
    public void clearAll() {
        KLog.i("====清除作品对象");
        productEntity = null;
        DCScriptManager.scriptManager().clearScripts();//防止没有清除脚本，
    }

    public static ProductEntity createLocalVideoEntity() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.extendInfo = new ProductExtendEntity();
        productEntity.extendInfo.isLocalUploadVideo = true;
        RecordManager.get().productEntity = productEntity;
        return productEntity;
    }

    public void setExceptWH(int width, int height) {
        if (productEntity == null) {
            createLocalVideoEntity();
        }
        getProductExtend().videoWidth = width;
        getProductExtend().videoHeight = height;

    }
}
