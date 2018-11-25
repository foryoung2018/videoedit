package com.wmlive.hhvideo.heihei.record.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.listener.WebpJoinListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.WebpUtil;
import com.wmlive.hhvideo.utils.download.DownloadUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_BASE_DIR;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_COMBINE_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_VIDEO_DIR;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_AUDIO_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_VIDEO_FILE;

/**
 * Created by lsq on 8/25/2017.
 */

public class RecordFileUtil {

    private static final String INDIVIDUAL_DIR_NAME = "video_record_cache";

    private static String rootPath;
    private static String tempDir = "temp";
    private static String logDir = "log";
    private static String draftDir = "draft";
    private static String assetDir = "asset";
    private static String videoDir = "video";
    private static String materialDir = "material";
    private static String sysGalleryDir;

    public static boolean initAllDir(File rootDir) {
        rootPath = rootDir.getAbsolutePath();
        sysGalleryDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera/").getAbsolutePath();
        tempDir = rootPath + File.separator + tempDir;
        logDir = rootPath + File.separator + logDir;
        draftDir = rootPath + File.separator + draftDir;
        assetDir = rootPath + File.separator + assetDir;
        videoDir = rootPath + File.separator + videoDir;
        materialDir = tempDir + File.separator + materialDir;
        return createDir(new File(tempDir))
                && createDir(new File(draftDir))
                && createDir(new File(assetDir))
                && createDir(new File(videoDir))
                && createDir(new File(logDir))
                && createDir(new File(materialDir));
    }

    public static String getRootPath() {
        return rootPath;
    }

    public static String getTempDir() {
        return tempDir;
    }

    public static String getLogDir() {
        return logDir;
    }

    public static String getDraftDir() {
        return draftDir;
    }

    public static String getAssetDir() {
        return assetDir;
    }

    public static String getVideoDir() {
        return videoDir;
    }

    public static String getSysGalleryDir() {
        return sysGalleryDir;
    }

    public static String getMaterialDir() {
        return materialDir;
    }
    /**
     * 创建一个文件夹
     *
     * @param dir
     * @return
     */
    public static boolean createDir(File dir) {
        if (dir == null) {
            return false;
        }
        return dir.exists() ? (dir.isDirectory() || dir.mkdirs()) : dir.mkdirs();
    }

    public static boolean createFile(File file, boolean createNoMedia) {
        boolean isOk = true;
        if (file == null) {
            return false;
        }
        if (!file.exists() || file.isDirectory()) {
            try {
                isOk = file.createNewFile();
                File noMediaFile = new File(file.getParent(), ".nomedia");
                if (createNoMedia) {
                    if (!noMediaFile.exists() || noMediaFile.isDirectory()) {
                        noMediaFile.createNewFile();
                    }
                } else {
                    if (noMediaFile.exists()) {
                        noMediaFile.delete();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                isOk = false;
            }
        }
        return isOk;
    }

    /**
     * 创建视频文件
     *
     * @param parentPath 所在文件夹
     * @return 创建失败返回null
     */
    public static String createVideoFile(String parentPath) {
        return createTimestampFile(parentPath, PREFIX_BASE_DIR, SUFFIX_VIDEO_FILE, true);
    }


    /**
     * 创建视频文件
     *
     * @param parentPath
     * @param tag        tag标识
     * @return 创建失败返回null
     */
    public static String createVideoFile(String parentPath, String tag) {
        return createTimestampFile(parentPath, PREFIX_BASE_DIR + tag + "_", SUFFIX_VIDEO_FILE, true);
    }

    /**
     * 创建视频文件
     * 根据传入的参数进行创建文件
     *
     * @param parentPath
     * @param tag        tag标识
     * @return 创建失败返回null
     */
    public static String createVideoFileByFilePath(String parentPath, String tag) {
        if (parentPath == null)
            return null;
        File file = new File(parentPath);
        KLog.i("rotate-->path" + file.getParent());
        return createTimestampFile(file.getParent(), PREFIX_BASE_DIR + tag + "_", SUFFIX_VIDEO_FILE, true);
    }

    /**
     * 获取导出视频的路径
     *
     * @return
     */
    public static String createExportFile() {


        if (RecordManager.get().getProductEntity() == null)
//            return "/storage/emulated/0/DCIM/Camera/11.mp4";
            return null;
        if (TextUtils.isEmpty(RecordManager.get().getProductEntity().baseDir)) {
            prepareDir(true);
        }
        final String exportPath = RecordManager.get().getProductEntity().baseDir
                + File.separator
                + PREFIX_COMBINE_FILE
                + RecordFileUtil.getTimestampString()
                + SUFFIX_VIDEO_FILE;
        File f = new File(exportPath);
        KLog.i("====yang exportPath");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return exportPath;

    }

    private static boolean prepareDir(boolean createAllVideoDir) {
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
        }
        return true;
    }

//    public static boolean prepareDir(String basePath) {
//        if (TextUtils.isEmpty(basePath)) {
//            String productPath = RecordFileUtil.createTimestampDir(RecordFileUtil.getTempDir(), "");
//            if (TextUtils.isEmpty(productPath)) {
//                KLog.i("====创建productDir文件夹失败");
//                return false;
//            }
//            RecordManager.get().getProductEntity().baseDir = productPath;
//        }
//
//        if (!RecordFileUtil.createDir(new File(RecordManager.get().getProductEntity().baseDir))) {
//            KLog.i("====创建productDir文件夹失败");
//            return false;
//        }
//
//            String shortVideoDir = RecordFileUtil.createTimestampDir(basePath, RecordManager.PREFIX_VIDEO_DIR);
//            if (TextUtils.isEmpty(shortVideoDir)) {
//                KLog.i("====创建shortVideoDir文件夹失败");
//                return false;
//            }
//            currentVideoEntity.baseDir = shortVideoDir;
//
//
//        if (!RecordFileUtil.createDir(new File(currentVideoEntity.baseDir))) {
//            KLog.i("====创建shortVideoDir文件夹失败");
//            return false;
//        }
//        return true;
//    }

    /**
     * 创建文件夹
     *
     * @param index
     * @param createAllVideoDir
     * @return
     */
    public static boolean prepareDirIndex(int index, boolean createAllVideoDir) {
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
                    KLog.i("====创建文件夹" + shortVideoDir);
                    videoEntity.baseDir = shortVideoDir;
                }

                if (!RecordFileUtil.createDir(new File(videoEntity.baseDir))) {
                    KLog.i("====创建shortVideoDir文件夹失败");
                    return false;
                }
            }
        } else {
            ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
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
        return true;
    }


    /**
     * 创建音频文件
     *
     * @param parentPath 所在文件夹
     * @return 创建失败返回null
     */
    public static String createAudioFile(String parentPath) {
        return createTimestampFile(parentPath, PREFIX_BASE_DIR, SUFFIX_AUDIO_FILE, true);
    }

    /**
     * 创建一个以时间戳为名字的文件
     *
     * @param dirPath   所在文件夹
     * @param prefix    文件前缀
     * @param extension 文件扩展名，请自带.
     * @return 创建失败返回null
     */
    public static String createTimestampFile(String dirPath, String prefix, String extension, boolean createNoMedia) {
        if (!TextUtils.isEmpty(dirPath)) {
            String filePath = dirPath
                    + File.separator
                    + prefix
                    + getTimestampString()
                    + extension;
            return createFile(new File(filePath), createNoMedia) ? filePath : null;
        }
        return null;
    }

    /**
     * 创建一个以时间戳为名字的文件夹
     *
     * @param parentPath 所在文件夹
     * @param prefix     文件前缀
     * @return 创建失败返回null
     */
    public static String createTimestampDir(String parentPath, String prefix) {
        if (!TextUtils.isEmpty(parentPath)) {
            String filePath = parentPath
                    + File.separator
                    + prefix
                    + getTimestampString();
            return createDir(new File(filePath)) ? filePath : null;
        }
        return null;
    }

    public static void deleteFiles(String... filePaths) {
        for (String file : filePaths) {
            KLog.i("deleteFiles->" + file);
            if (!TextUtils.isEmpty(file)) {
                File f = new File(file);
                if (f.exists() && !f.isDirectory()) {
                    f.delete();
                }
            }
        }
    }


    /**
     * 删除指定目录下的特定前缀的所有文件
     *
     * @param dirPath   指定的目录
     * @param strPrefix 文件铅锤
     */
    public static void cleanFilesByPrefix(String dirPath, final String strPrefix) {
        if (!TextUtils.isEmpty(dirPath)) {
            File tempDir = new File(dirPath);
            if (tempDir.exists() && tempDir.isDirectory()) {
                File[] files = tempDir.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String filename) {
                        return TextUtils.isEmpty(strPrefix) || filename.startsWith(strPrefix);
                    }
                });
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.exists() && !file.isDirectory()) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    public static File getIndividualCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context, true);
        return new File(cacheDir, INDIVIDUAL_DIR_NAME);
    }

    private static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens
            externalStorageState = "";
        }
        //获取外部存储区  /Android/data/包名/cache目录
        if (preferExternal && Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
            appCacheDir = getExternalCacheDir(context);
        }
        //获取内部存储区   /data/data/包名/cache目录
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            KLog.e("Can't define system cache directory! '" + cacheDirPath + "%s' will be used.");
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * 拿到的是外置存储器的cache目录，路径：/Android/data/包名/cache，
     * 通过调用 context.getExternalCacheDir()方法会自动生成文件夹，无需手动创建
     *
     * @param context
     * @return
     */
    private static File getExternalCacheDir(Context context) {
        File dataDir = context.getExternalCacheDir();
//        通过调用 context.getExternalCacheDir()方法会自动生成文件夹，无需手动创建
        if (dataDir == null || !dataDir.exists()) {
            dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
            File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
            if (!appCacheDir.exists()) {
                try {
                    if (!appCacheDir.mkdirs()) {  //6.0以上会有运行时权限
                        KLog.e("Unable to create external cache directory");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return appCacheDir.exists() ? appCacheDir : null;
                }
            }
        }
        return dataDir;
    }

    public static boolean hasWritePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static String getTimestampString() {
        return (new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(new Date())) + getFileEndNum();
    }

    static int endNum;

    private static int getFileEndNum() {
        endNum++;
        if (endNum == 10)
            endNum = 0;
        return endNum;
    }

    public static String getTimestampVideoFileName() {
        return PREFIX_VIDEO_DIR + getTimestampString();
    }

    public static String renameToDir(String srcPath, String destDir) {
        if (!TextUtils.isEmpty(destDir)
                && !TextUtils.isEmpty(srcPath)
                && srcPath.contains("/")) {
            return destDir + File.separator + srcPath.substring(srcPath.lastIndexOf("/") + 1);
        }
        return srcPath;
    }

    /**
     * 根据视频的 显示位置，等比例的缩小，放大原视频
     *
     * @param videoWidth  视频原始宽
     * @param videoHeight 视频原始高
     * @param viewWidth   视频显示位置的宽
     * @param viewHeight
     */
    public static RectF getRectInVideo(int videoWidth, int videoHeight, int viewWidth,int viewHeight) {
        RectF rectF = new RectF();
        if(viewHeight==0){//适配1080 1920，
            rectF.set(0,0,0.74f,1);
            return rectF;
        }
        float videoRate = videoWidth*1.0f/videoHeight*1.0f;
        float viewRate = viewWidth*1.0f/viewHeight*1.0f;
        if(videoRate>viewRate){//视频更宽
            rectF.set(0,0,1,videoWidth*1.0f/viewWidth*1.0f);
        }else if(videoRate<viewRate){//视频更高
            rectF.set(0,0,videoRate*1.0f/viewRate*1.0f,1);
        }else {
            rectF.set(0,0,1,1);
        }

        return rectF;
    }

    /**
     * 根据视频的 显示位置，等比例的裁剪出需要保留的视频区域（相对于原始视频的尺寸）
     *
     * @param videoWidth  视频原始宽
     * @param videoHeight 视频原始高
     * @param clipRatio   视频显示位置的宽高比
     * @param offset      上下、左右偏移(-1.0f<---->1.0f)
     */
    public static RectF getClipSrc(int videoWidth, int videoHeight, float clipRatio, float offset) {
        RectF rectF = new RectF();
        KLog.i("======裁剪视频宽高width:" + videoWidth + "_height:" + videoHeight);
        KLog.i("======裁剪比例Ratio:" + clipRatio);
        getClipRect(videoWidth, videoHeight, clipRatio, rectF);
        KLog.i("======裁剪区域 Rect:" + rectF.toString());
        int dx = (int) (offset * ((videoWidth - rectF.width()) / 2.0f));
        int dy = (int) (offset * ((videoHeight - rectF.height()) / 2.0f));
        rectF.offset(dx, dy);
        KLog.i("======裁剪区域 偏移dx:" + dx + "==dy:" + dy);
        KLog.i(clipRatio + "====最终裁剪区域  getClipSrc: " + rectF.toShortString());
        return rectF;
    }

    /**
     * 根据视频的 显示位置，等比例的裁剪出需要保留的视频区域（相对于原始视频的尺寸）
     *
     * @param videoWidth  视频原始宽
     * @param videoHeight 视频原始高
     * @param clipRatio   视频显示位置的宽高比
     * @param rectF       裁剪后的Rect
     */
    public static void getClipRect(int videoWidth, int videoHeight, float clipRatio, RectF rectF) {
        float videoRatio = videoWidth * 1.0f / videoHeight;
        float needWidth = clipRatio >= videoRatio ? videoWidth : (videoHeight * 1.0f * clipRatio);
        float needHeight = clipRatio <= videoRatio ? videoHeight : (videoWidth * 1.0f / clipRatio);
        rectF.left = (videoWidth - needWidth) / 2.0f;
        rectF.right = rectF.left + needWidth;
        rectF.top = (videoHeight - needHeight) / 2.0f;
        rectF.bottom = rectF.top + needHeight;
    }

//    int localHeight = 960;
//    int localWidth = 720;

    private static int[] getTargetWidthNew(int videoWidth,int videoHeight,int viewWidth,int viewHeight){
        float targetWidth = videoWidth;
        float targetHeight = videoHeight;
        float rate = videoHeight*1.0f / videoWidth *1.0f;
        if(videoHeight>viewHeight){
            if(videoWidth>viewWidth){
                targetHeight = viewHeight;
                targetWidth = targetHeight*1.0f / rate;
            }else if(videoWidth<viewWidth){
                targetWidth = viewWidth;
                targetHeight = targetWidth * rate;
            }else {//
                targetHeight = viewHeight;
                targetWidth = targetHeight*1.0f / rate;
            }

        }else {
            if(videoWidth>viewWidth){
                targetWidth = viewWidth;
                targetHeight = targetWidth* rate;
            }else if(videoWidth<viewWidth){//太小了不用变

            }else {

            }
        }
        if(((int)targetHeight)%2!=0){
            targetHeight = targetHeight+1;
        }
        if(((int)targetWidth)%2!=0){
            targetWidth = targetWidth+1;
        }

        return new int[]{(int)targetWidth,(int)targetHeight};
    }

    /**
     * 获取视频的封面图
     *
     * @param videoPath
     * @param savePath
     * @param frameTime
     * @return
     */
    public static boolean getVideoCover(String videoPath, String savePath, float frameTime, int width, int height) {
        return getSnapShot(videoPath, savePath, frameTime, width, height, false);
    }

    /**
     * 获取视频的缩略图
     *
     * @param videoPath
     * @param savePath
     * @param timeSecond
     * @return
     */
    public static boolean getVideoThumb(String videoPath, String savePath, float timeSecond) {
        return getSnapShot(videoPath, savePath, timeSecond, 300, 400, false);
    }

    /**
     * 获取视频缩略图
     *
     * @param mediaPath 视频路径
     * @param savePath  缩略图保存路径
     * @param frameTime 抽取帧时间点
     * @param width     宽度
     * @param height    高度
     * @return
     */
    public static boolean getSnapShot(String mediaPath, String savePath, float frameTime, int width, int height, boolean isKey) {
        if (!TextUtils.isEmpty(mediaPath) && !TextUtils.isEmpty(savePath)) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(mediaPath);
            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime((long) frameTime);

            if (bitmap == null) {
                mediaMetadataRetriever.release();
                return false;
            }
            File file = null;
            FileOutputStream out = null;
            try {
                file = new File(savePath);
                file.createNewFile();
                out = new FileOutputStream(file.getPath());
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            }
        } else {
            KLog.i("=====getSnapShot,path error,mediaPath:" + mediaPath + " , savePath:" + savePath);
            return false;
        }
    }

    public static Bitmap loadBitmapFromViewBySystem(View v) {
        if (v == null) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        return bitmap;
    }

    public static boolean getVideoCover(View v, String savePath) {
        Bitmap bitmap = loadBitmapFromViewBySystem(v);
//        Bitmap bitmap = ScreenShot.getCacheBitmapFromView(v);

        if (bitmap != null) {
            File file = null;
            FileOutputStream out = null;
            try {
                file = new File(savePath);
                file.createNewFile();
                out = new FileOutputStream(file.getPath());
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }
        return false;
    }


//    public static boolean getSnapShot(VirtualVideo virtualVideo, int width, int height, float timeSecond, String savePath) {
//        if (virtualVideo != null) {
//            KLog.i("=======getSnapShot virtualVideo:" + virtualVideo + " ，width:" + width + " ,height:" + height + " ,timeSecond:" + timeSecond + " ,savePath:" + savePath);
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            try {
//                if (virtualVideo.getSnapshot(DCApplication.getDCApp(), timeSecond, bitmap, true)) {
//                    File fileSave = new File(savePath);
//                    if (!fileSave.exists()) {
//                        fileSave.getParentFile().mkdirs();
//                        fileSave.createNewFile();
//                    }
//                    FileOutputStream fileOutputStream = new FileOutputStream(fileSave);
//                    String var8 = getFileSuffix(savePath);
//                    if ("jpg".equals(var8)) {
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//                    } else {
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//                    }
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
//                    return true;
//                } else {
//                    return false;
//                }
//            } catch (Exception e) {
//                KLog.i("======getSnapShot exception:" + e.getMessage());
//                return false;
//            }
//        } else {
//            KLog.i("=====virtualVideo is null");
//            return false;
//        }
//    }

    /**
     * 获取文件的后缀名
     *
     * @param filePath
     * @return
     */
    public static String getFileSuffix(String filePath) {
        int dotIndex = filePath.lastIndexOf(".");
        return dotIndex >= 0 ? filePath.substring(dotIndex) : "";
    }

    public static void createWebp(final String videoPath, final String savePath, WebpJoinListener listener) {
        createWebp(videoPath, savePath, 300, 400, 5, listener);
    }

    /**
     * 生成webp图片
     *
     * @param videoPath 视频的路径
     * @param savePath  webp保存路径
     * @param width     webp宽度
     * @param height    webp高度
     * @param maxFrame  抽取最多帧数
     * @param listener  监听
     */
    public static void createWebp(final String videoPath, final String savePath, final int width,
                                  final int height, final int maxFrame, final WebpJoinListener listener) {
        final int max = maxFrame <= 1 ? 2 : maxFrame;
        final String[] tempImages = new String[max];
        final byte[][] container = new byte[max * 2][];
        final String dirPath;
        final String tempSuffix = "_temp.png";
        KLog.i("====开始生成webp , videoPath：" + videoPath + " , save path:" + savePath + " , 最多" + maxFrame + "帧");
        if (!TextUtils.isEmpty(savePath)
                && !TextUtils.isEmpty(videoPath)
                && new File(videoPath).exists()) {
            dirPath = new File(savePath).getParent();
            File dir = new File(dirPath);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            if (listener != null) {
                listener.onStartJoin();
            }
            //单位秒
            final float during = VideoUtils.getVideoLength(videoPath) / 1000000;
            if (during > 1f) {
                for (int index = 0; index < max; index++) {
                    float frameTime = 1.0f + 0.2f * index;//单位：秒
                    KLog.i("=====get index" + index + " bitmap，frameTime：" + frameTime + " , during:" + during);
                    if (frameTime > during) {
                        container[index] = container[index - 1];
                    } else {
                        tempImages[index] = dirPath + File.separator + index + tempSuffix;
                        if (getSnapShot(videoPath, tempImages[index], frameTime, width, height, false)) {
                            Bitmap b = BitmapFactory.decodeFile(tempImages[index]);
                            byte[] bytePng = WebpUtil.getBGRA(b);
                            if (bytePng != null) {
                                byte[] byteWebp = WebpUtil.getWebpByte(bytePng, b.getWidth(), b.getHeight(), b.getWidth() * 4, 80);
                                if (byteWebp != null) {
                                    container[index] = byteWebp;
                                    KLog.i("====createWebp=get index：" + index + " success");
                                } else {
                                    KLog.i("====createWebp=get index：" + index + " byteWebp is null");
                                }
                            } else {
                                KLog.i("====createWebp=get index：" + index + " bytePng is null");
                            }
                        } else {
                            KLog.i("====createWebp=get index：" + index + " getSnapShot error");
                        }
                    }
                    container[container.length - index - 1] = container[index];
                    if (listener != null) {
                        listener.onJoining(index * 1f / max);
                    }
                }
                KLog.i("====开始生成webp");
                List<byte[]> list = new ArrayList<>();
                for (byte[] b : container) {
                    if (b != null) {
                        list.add(b);
                    }
                }
                boolean isOk = list.size() > 0;
                KLog.i("=====有效的size:" + list.size());
                if (isOk) {
                    isOk = WebpUtil.saveWebpMuxByByes(list.toArray(new byte[list.size()][]), 150, 0, savePath);
                }
                if (!GlobalParams.Config.IS_DEBUG) {
                    deleteFiles(tempImages);
                }
                KLog.i("====生成webp" + (isOk ? "成功" : "失败"));
                if (listener != null) {
                    listener.onJoinEnd(isOk, savePath);
                }
            } else {
                KLog.i("======webp ，the video is too short,during is:" + during);
                if (listener != null) {
                    listener.onJoinEnd(false, "the video is too short,during is:" + during);
                }
            }
        } else {
            KLog.i("========webp,video path error");
            if (listener != null) {
                listener.onJoinEnd(false, "video path error");
            }
        }
    }

    public static void insertToGallery(Context context, String videoPath, boolean copyFile) {
        insertToGallery(context, videoPath, null, copyFile);
    }


    /**
     * 将视频信息存入相册数据库
     *
     * @param videoPath 视频路径
     */
    public static void insertToGallery(Context context, String videoPath, String saveName, boolean copyFile) {
        if (!TextUtils.isEmpty(videoPath)) {
            File videoFile = new File(videoPath);
            if (videoFile.exists() && !videoFile.isDirectory()) {
                String fileName = !TextUtils.isEmpty(saveName) ? saveName : videoFile.getName().toUpperCase();
                String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera";
                boolean copyOk = true;
                MediaMetadataRetriever retriever = null;
                try {
                    if (copyFile) {
                        if (FileUtil.createDirectory(destPath)) {
                            FileUtil.copy(videoPath, destPath, fileName);
                            KLog.i("====insertToGallery=copy file to destPath:" + destPath + "/" + fileName);
                        } else {
                            copyOk = false;
                        }
                    }
                    retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(videoPath);
                    int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    ContentValues videoValues = new ContentValues();

                    videoValues.put(MediaStore.Video.Media.TITLE, fileName.replaceFirst(".MP4", ""));
                    videoValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                    videoValues.put(MediaStore.Video.Media.DATA, copyFile && copyOk ? (destPath + File.separator + fileName) : videoPath);
                    videoValues.put(MediaStore.Video.Media.ARTIST, "");
                    videoValues.put(MediaStore.Video.Media.DATE_TAKEN, String.valueOf(System.currentTimeMillis()));
                    videoValues.put(MediaStore.Video.Media.DESCRIPTION, "");
                    videoValues.put(MediaStore.Video.Media.DURATION, duration);
                    videoValues.put(MediaStore.Video.Media.WIDTH, width);
                    videoValues.put(MediaStore.Video.Media.HEIGHT, height);
                    KLog.i("====insertToGallery=插入到相册的数据：" + videoValues.toString());
                    context.getApplicationContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoValues);
                    KLog.i("====insertToGallery=插入视频到相册成功");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    KLog.i("====insertToGallery=插入数据库出错");
                } finally {
                    if (retriever != null) {
                        retriever.release();
                    }
                }
            }
        }
    }

    public static void openGallery(Activity context, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
        context.startActivityForResult(intent, requestCode);
    }

    public static String getGalleryFileUri(Context context, Uri uri) {
        String filePath = null;
        String[] column = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, column, null, null, null);
        if (null != cursor) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(column[0]);
            filePath = cursor.getString(columnIndex);//视频路径
            cursor.close();
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }

    public static String getFrameImagePath(String urlImage) {
        if (DownloadUtil.isUrl(urlImage)) {
            String frameName = urlImage.substring(urlImage.lastIndexOf("/") + 1, urlImage.length());
            String frameImagePath = AppCacheFileUtils.getAppFramesImagePath() + File.separator + frameName;
            File file = new File(frameImagePath);
            if (file.exists()) {
                return frameImagePath;
            }
        }
        return null;
    }


    public static String copyFile(String path) {
        final String exportPath = RecordManager.get().getProductEntity().baseDir
                + File.separator
                + PREFIX_COMBINE_FILE
                + RecordFileUtil.getTimestampString()
                + SUFFIX_VIDEO_FILE;
        boolean copy = FileUtil.copy(path, exportPath);
        if (copy) {
            return exportPath;
        } else {
            return path;
        }

    }

}
