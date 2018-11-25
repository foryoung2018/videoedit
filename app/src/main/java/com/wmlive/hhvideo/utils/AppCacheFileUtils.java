package com.wmlive.hhvideo.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * app 缓存目录管理
 * Created by kangzhen on 2017/6/5.
 */

public class AppCacheFileUtils {
    //cache
    private static String sPathInternalCacheRoot = "";  //应用的内部cache根目录
    private static final String PATH_TEMP = "temp";                  //临时文件目录


    //files
    private static String sPathInternalFilesRoot = "";  //应用的内部File根目录
    private static final String PATH_IMAGES = "images";              //图片目录
    private static final String PATH_LOG = "log";                    //log目录
    private static final String PATH_DOWNLOAD = "download";          //下载目录
    private static final String PATH_DB = "db";                      //数据库目录
    private static final String PATH_MUSIC_CACHE = "music_cache";   //音乐缓存目录
    private static final String PATH_VIDEO_RECORD_CACHE = "video_record_cache";   //视频录制的缓存目录（第三方
    private static final String PATH_HTTP_CACHE = "http_cache";      //网络缓存目录
    private static final String PATH_VIDEO_CACHE = "video-cache";   //视频的缓存目录
    public static final String PATH_GIFTS_CACHE = "gift_cache";//礼物资源缓存目录
    private static final String PATH_FRAMES_IMAGE = "frame";//画框图片目录
    private static final String PATH_IM_CACHE = "im_cache/";//im资源缓存目录
    private static final String PATH_AUDIO_CACHE = "audio_cache/";//录音的缓存目录

    private static final String PATH_IMAGES_WATERMARKS = "images/watermark";//图片目录-水印目录

    private static final String CREATIVE = "creative/";//创意视频脚本存储目录
    private static final String CREATIVEASSETS = "creative/assets/";//创意视频脚本存储目录
    private static String sPathExternalRoot = "";  //应用的外部根目录
    private static final String PATH_EXTERNAL_ROOT = "DongCi";    //sd卡根目录下的文件夹 //  4/5/2017 这个目录最好自定义

    /**
     * App目录是否已经初始化
     *
     * @return
     */
    public static boolean isAppCacheDirectoryInit(Context context) {
        boolean isInternalCache = false;
        sPathInternalCacheRoot = FileUtil.getAppCacheDirectory(context);
        if (!TextUtils.isEmpty(sPathInternalCacheRoot)) {
            isInternalCache = (new File(sPathInternalCacheRoot + File.separator + PATH_TEMP).exists());
        }

        boolean isInternalFiles = false;
        sPathInternalFilesRoot = FileUtil.getAppFilesDirectory(context);
        if (!TextUtils.isEmpty(sPathInternalFilesRoot)) {
            isInternalFiles = (new File(sPathInternalFilesRoot + File.separator + PATH_IMAGES).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_LOG).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_DOWNLOAD).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_DB).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_MUSIC_CACHE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_VIDEO_RECORD_CACHE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_HTTP_CACHE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_VIDEO_CACHE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_GIFTS_CACHE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_FRAMES_IMAGE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_IM_CACHE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_AUDIO_CACHE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + CREATIVE).exists()
                    && new File(sPathInternalFilesRoot + File.separator + CREATIVEASSETS).exists()
                    && new File(sPathInternalFilesRoot + File.separator + PATH_IMAGES_WATERMARKS).exists()

            );
        }
        //外部存储卡
        boolean isExternalFiles = false;
        initAppExternalDirectory();
        isExternalFiles = isExternalDirectoryExist();

        return isInternalCache && isInternalFiles && isExternalFiles;
    }

    /**
     * 初始化App的外置目录
     */
    public static void initAppExternalDirectory() {
        sPathExternalRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + PATH_EXTERNAL_ROOT;
        FileUtil.createDirectory(sPathExternalRoot);
    }

    /**
     * 判断外置存储卡
     *
     * @return
     */
    public static boolean isExternalDirectoryExist() {
        sPathExternalRoot = getAppExternalTempPath();
        if (!TextUtils.isEmpty(sPathExternalRoot)) {
            return new File(sPathExternalRoot).exists();
        }
        return false;
    }

    /**
     * 初始化App的内部Cache目录
     *
     * @param context
     */
    public static void initAppInternalCacheDirectory(Context context) {
        sPathInternalCacheRoot = FileUtil.getAppCacheDirectory(context);
        if (!TextUtils.isEmpty(sPathInternalCacheRoot)) {
            FileUtil.createDirectory(sPathInternalCacheRoot + File.separator + PATH_TEMP);
        }
    }

    /**
     * 初始化App的内部File目录
     *
     * @param context
     */
    public static void initAppInternalFilesDirectory(Context context) {
        sPathInternalFilesRoot = FileUtil.getAppFilesDirectory(context);
        if (!TextUtils.isEmpty(sPathInternalFilesRoot)) {
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_IMAGES);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_DOWNLOAD);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_LOG);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_DB);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_MUSIC_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_VIDEO_RECORD_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_HTTP_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_VIDEO_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_GIFTS_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_IM_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_AUDIO_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + CREATIVE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + CREATIVEASSETS);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_IMAGES_WATERMARKS);

        }
    }


    /**
     * 清除制定内部缓存
     */
    public static void clearAppCachePath() {
        if (!TextUtils.isEmpty(sPathInternalFilesRoot)) {
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_TEMP));
        }
        if (!TextUtils.isEmpty(sPathInternalFilesRoot)) {
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_IMAGES));
//            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_LOG));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_DOWNLOAD));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_DB));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_HTTP_CACHE));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_VIDEO_CACHE));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_MUSIC_CACHE));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_IM_CACHE));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_AUDIO_CACHE));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + CREATIVE));
            FileUtil.deleteFiles(new File(sPathInternalFilesRoot + File.separator + PATH_IMAGES_WATERMARKS));

        }
    }

    /**
     * 清除视频的缓存
     */
    public static void clearVideoCache() {
        if (!TextUtils.isEmpty(sPathInternalCacheRoot)) {
            FileUtil.deleteFiles(new File(sPathInternalCacheRoot + File.separator + PATH_VIDEO_CACHE));
        }
    }

    /**
     * 初始化App的内部目录
     *
     * @param context
     */
    public static void initClearAppCachePath(Context context) {
        if (!TextUtils.isEmpty(sPathInternalFilesRoot)) {
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_TEMP);
        }
        if (!TextUtils.isEmpty(sPathInternalFilesRoot)) {
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_IMAGES);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_LOG);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_DOWNLOAD);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_DB);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_HTTP_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_VIDEO_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_GIFTS_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_FRAMES_IMAGE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_IM_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_AUDIO_CACHE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + CREATIVE);
            FileUtil.createDirectory(sPathInternalFilesRoot + File.separator + PATH_IMAGES_WATERMARKS);
        }

        if (!TextUtils.isEmpty(sPathInternalCacheRoot)) {
            FileUtil.createDirectory(sPathInternalCacheRoot + File.separator + PATH_VIDEO_CACHE);
        }
    }

    /**
     * 清除App的外置目录
     */
    public static void clearAppExternalDirectory() {
        if (FileUtil.isSDCardExist()) {
            FileUtil.deleteFiles(new File(sPathExternalRoot));
        }
    }

    /**
     * 获取图片目录
     *
     * @return
     */
    public static String getAppImagesPath() {
        return sPathInternalFilesRoot + File.separator + PATH_IMAGES;
    }

    /**
     * 获取log目录
     *
     * @return
     */
    public static String getAppLogPath() {
        return sPathInternalFilesRoot + File.separator + PATH_LOG;
    }

    /**
     * 获取database目录
     *
     * @return
     */
    public static String getAppDbPath() {
        return sPathInternalFilesRoot + File.separator + PATH_DB;
    }

    /**
     * 获取temp目录
     *
     * @return
     */
    public static String getAppTempPath() {
        return sPathInternalCacheRoot + File.separator + PATH_TEMP;
    }

    /**
     * 获取download目录
     *
     * @return
     */
    public static String getAppDownloadPath() {
        return sPathInternalFilesRoot + File.separator + PATH_DOWNLOAD;
    }

    /**
     * 获取video_cache 目录
     *
     * @return
     */
    public static String getAppVideoCachePath() {
        return sPathInternalCacheRoot + File.separator + PATH_VIDEO_CACHE;
    }

    /**
     * 获取应用礼物资源包
     *
     * @return
     */
    public static String getAppGiftCachePath() {
        return sPathInternalFilesRoot + File.separator + PATH_GIFTS_CACHE;
    }

    /**
     * 获取画框图片目录
     *
     * @return
     */
    public static String getAppFramesImagePath() {
        return sPathInternalFilesRoot + File.separator + PATH_FRAMES_IMAGE;
    }

    /**
     * 获取IM缓存目录
     *
     * @return
     */
    public static String getAppIMCachePath() {
        return sPathInternalFilesRoot + File.separator + PATH_IM_CACHE;
    }

    /**
     * 获取录制声音缓存目录
     *
     * @return
     */
    public static String getAppAudioCachePath() {
        return sPathInternalFilesRoot + File.separator + PATH_AUDIO_CACHE;
    }

    /**
     * 获取创意视频素材存储根目录
     *
     * @return
     */

    public static String getAppCreativePath() {
        return sPathInternalFilesRoot + File.separator + CREATIVE;
    }

    public static String getAppCreativeAssetsPath() {
        return sPathInternalFilesRoot + File.separator + CREATIVEASSETS;
    }

    public static String getAppWaterMarksPath() {
        return sPathInternalFilesRoot + File.separator + PATH_IMAGES_WATERMARKS;
    }

    /**
     * 获取video_cache 目录
     *
     * @return
     */
    public static File getAppVideoCachePathFile(Context context) {
        if (TextUtils.isEmpty(sPathInternalCacheRoot)) {
            sPathInternalCacheRoot = FileUtil.getAppCacheDirectory(context);
        }
        String strVideoCachePath = sPathInternalCacheRoot + File.separator + PATH_VIDEO_CACHE;
        return new File(strVideoCachePath);
    }

    /**
     * 获取HttpCache目录
     *
     * @return
     */
    public static String getAppHttpCachePath() {
        return sPathInternalFilesRoot + File.separator + PATH_HTTP_CACHE;
    }

    /**
     * 获取音乐缓存目录
     *
     * @return
     */
    public static String getAppMusicCachePath() {
        return sPathInternalFilesRoot + File.separator + PATH_MUSIC_CACHE;
    }


    /**
     * 获取音乐缓存文件
     *
     * @return
     */
    public static File getAppMusicCachePathFile() {
        if (!TextUtils.isEmpty(getAppMusicCachePath())) {
            File mMusixFile = new File(getAppMusicCachePath());
            return mMusixFile;
        } else {
            return null;
        }
    }

    /**
     * 获取第三方视频缓存模具
     *
     * @return
     */
    public static String getAppVideoRecordCachePath(Context context) {
        if (TextUtils.isEmpty(sPathInternalFilesRoot)) {
            sPathInternalFilesRoot = FileUtil.getAppFilesDirectory(context);
        }
        return sPathInternalFilesRoot + File.separator + PATH_VIDEO_RECORD_CACHE;
    }

    /**
     * 获取第三方文件缓存目录
     *
     * @return
     */
    public static File getAppVideoRecordCachePathFile(Context context) {
        if (!TextUtils.isEmpty(getAppVideoRecordCachePath(context))) {
            return new File(getAppVideoRecordCachePath(context));
        } else {
            return null;
        }
    }

    /**
     * 获取APP外置目录
     *
     * @return
     */
    public static String getAppExternalTempPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + PATH_EXTERNAL_ROOT;
    }

    /**
     * 获取视频截图的存放位置
     *
     * @param context
     * @return
     */
    public static String getShorVideoFirstClipImgPath(Context context) {
        if (TextUtils.isEmpty(sPathInternalFilesRoot)) {
            sPathInternalFilesRoot = FileUtil.getAppFilesDirectory(context);
        }
        return getAppImagesPath();
    }

}
