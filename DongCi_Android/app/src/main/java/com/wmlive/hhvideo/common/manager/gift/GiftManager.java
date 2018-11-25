package com.wmlive.hhvideo.common.manager.gift;

import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftEntity;
import com.wmlive.hhvideo.heihei.beans.main.SplashResourceEntity;
import com.wmlive.hhvideo.service.GiftService;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 1/8/2018.4:27 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftManager {
    private static final String PATH_SPLASH_RESOURCE = "dc_splash";//Splash页面的图片资源文件夹
    private static final String SUFFIX_ZIP = ".zip";
    private static final String SUFFIX_PNG = ".png";
    private static final String SUFFIX_FLY_WEBP = "_icon.webp";//飞翔小图
    private static final String SUFFIX_BIG_WEBP = "_img.webp";//礼物大图
    private static final String SUFFIX_FLY_MUSIC = "_icon_music.mp3";//点击音效
    private static final String SUFFIX_GIFT_MUSIC = "_bg_music.mp3";//背景音乐
    private static final String SUFFIX_BURST_MUSIC = "_burst_music.mp3";//暴击音效

    private MediaPlayer mediaPlayer;

    private List<GiftEntity> giftList;

    private static final class Holder {
        static final GiftManager INSTANCE = new GiftManager();
    }

    public static GiftManager get() {
        return GiftManager.Holder.INSTANCE;
    }

    public void startGiftService() {
        GiftService.sendCommand(GiftService.CMD_START_SERVICE);
    }

    public void stopGiftService() {
        GiftService.sendCommand(GiftService.CMD_STOP_SERVICE);
    }

    /**
     * 拉取服务器礼物列表
     */
    @Deprecated
    public void pullGiftList() {
//        GiftService.sendCommand(GiftService.CMD_GET_GIFT_LIST);
    }

    /**
     * 设置用于显示到礼物面板的列表
     *
     * @param giftList
     */
    public void setGiftList(List<GiftEntity> giftList) {
        this.giftList = giftList;
    }

    /**
     * 已上线的礼物列表
     *
     * @return
     */
    public List<GiftEntity> getGiftList() {
        if (CollectionUtil.isEmpty(giftList)) {
            return new ArrayList<>(1);
        } else {
            return CommonUtils.cloneList(giftList);
        }
    }

    /**
     * 礼物zip文件的下载目录
     *
     * @param giftId
     * @return
     */
    public static String getRootGiftDirPath(String giftId) {
        return AppCacheFileUtils.getAppGiftCachePath() + File.separator + giftId;
    }

    /**
     * 礼物zip文件的全路径
     *
     * @param giftId
     * @return
     */
    public static String getZipFilePath(String giftId) {
        return getRootGiftDirPath(giftId) + File.separator + giftId + SUFFIX_ZIP;
    }

    /**
     * zip文件已解压的目录，gift_cache+gift_id+gift_id
     *
     * @param giftId
     * @return
     */
    public static String getUnzipGiftDirPath(String giftId, String prefix) {
        return getRootGiftDirPath(giftId) + File.separator + prefix;
    }


    public static String getFlyIcon(String giftId, String prefix) {
        return getLocalFile(giftId, prefix, SUFFIX_FLY_WEBP);
    }

    /**
     * 获取礼物的长音效
     *
     * @param giftId
     * @param prefix
     * @return
     */
    public static String getGiftMusic(String giftId, String prefix) {
        return getLocalFile(giftId, prefix, SUFFIX_GIFT_MUSIC);
    }

    /**
     * 获取礼物的音符飞翔的音效
     *
     * @param giftId
     * @param prefix
     * @return
     */
    public static String getFlyMusic(String giftId, String prefix) {
        return getLocalFile(giftId, prefix, SUFFIX_FLY_MUSIC);
    }

    /**
     * 暴击的音效
     *
     * @param giftId
     * @param prefix
     * @return
     */
    public static String getBurstMusic(String giftId, String prefix) {
        return getLocalFile(giftId, prefix, SUFFIX_BURST_MUSIC);
    }

    /**
     * 获取礼物的本地图片
     *
     * @param giftId
     * @param prefix
     * @return
     */
    public static String getGiftImage(String giftId, String prefix) {
        return getLocalFile(giftId, prefix, SUFFIX_BIG_WEBP);
    }

    public static String getLocalFile(String giftId, String prefix, String suffix) {
        String dirPath = getUnzipGiftDirPath(giftId, prefix);
        if (!TextUtils.isEmpty(dirPath)) {
            File dir = new File(dirPath);
            if (dir.exists() && dir.isDirectory()) {
                dirPath += (File.separator + prefix + suffix);
                File file = new File(dirPath);
                KLog.i("=====需要加载的本地文件路径：" + dirPath);
                if (file.exists() && file.isFile()) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    private MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }

    private String currentPlayPath;

    public void playMusic(String musicPath) {
        if (!TextUtils.isEmpty(musicPath)) {
            getMediaPlayer();
            if (musicPath.equals(currentPlayPath)) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                KLog.i("====继续播放音乐");
            } else {
                KLog.i("====重新播放音乐");
                currentPlayPath = musicPath;
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(DCApplication.getDCApp(), Uri.parse(musicPath));
                    mediaPlayer.setLooping(false);
                    if (musicPath.startsWith("http://")) {
                        mediaPlayer.prepareAsync();
                    } else {
                        mediaPlayer.prepare();
                    }
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pauseMusic() {
        KLog.i("====暂停播放音乐");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    public static String getSplashResourcePath() {
        String giftDirPath = FileUtil.getAppFilesDirectory(DCApplication.getDCApp()) + File.separator + AppCacheFileUtils.PATH_GIFTS_CACHE;
        return giftDirPath + File.separator + PATH_SPLASH_RESOURCE;
    }

    public void resetCurrentPlayPath() {
        currentPlayPath = null;
    }

    public void checkSplashResource(List<SplashResourceEntity> entityList) {
        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                               @Override
                               public void accept(Integer integer) throws Exception {
                                   SPUtils.putString(DCApplication.getDCApp(), SPUtils.SPLASH_RESOURCE_DATA, JSON.toJSONString(entityList));
                                   long current = System.currentTimeMillis() / 1000;
                                   String path = getSplashResourcePath();
                                   File dir = new File(path);
                                   boolean dirOk = true;
                                   if (dir.exists() && dir.isDirectory()) {
                                   } else {
                                       dirOk = dir.mkdirs();
                                   }
                                   String md5Str;
                                   String filePath;
                                   if (dirOk) {
                                       KLog.i("====创建splash文件夹成功");
                                       Map<String, String> allFileMd5 = getAllFileMd5(dir);
                                       List<BaseDownloadTask> downloadTaskList = new ArrayList<>(4);
                                       List<String> md5List = new ArrayList<>(4);
                                       for (SplashResourceEntity entity : entityList) {
                                           if (entity != null) {
                                               md5List.add(entity.file_md5);
                                               if (!TextUtils.isEmpty(entity.cover) && entity.end_time > current) {
                                                   md5Str = entity.file_md5;
                                                   if (!allFileMd5.containsKey(md5Str)) {
                                                       md5Str = md5Str.toUpperCase();
                                                   }
                                                   if (allFileMd5.containsKey(md5Str)) {
                                                       KLog.i("===文件：" + entity.cover + " 存在，不需要下载");
                                                       continue;
                                                   }
                                                   downloadTaskList.add(FileDownloader.getImpl()
                                                           .create(entity.cover)
                                                           .setTag(entity.cover)
                                                           .setPath(path + File.separator + entity.getFileName()));
                                               }

                                           }
                                       }

                                       //删除不需要的文件
                                       Iterator<Map.Entry<String, String>> iterator = allFileMd5.entrySet().iterator();
                                       Map.Entry<String, String> entry;
                                       while (iterator.hasNext()) {
                                           entry = iterator.next();
                                           if (entry != null) {
                                               filePath = path + File.separator + entry.getValue();
                                               md5Str = entry.getKey();
                                               if (!md5List.contains(md5Str)) {
                                                   //这里转成小写
                                                   md5Str = md5Str.toLowerCase();
                                               }
                                               if (!md5List.contains(md5Str)) {
                                                   KLog.i("======删除文件：" + filePath);
                                                   FileUtil.deleteFile(filePath);
                                               }
                                           }
                                       }

                                       KLog.i("=====需要下载的文件数：" + downloadTaskList.size());
                                       if (downloadTaskList.size() > 0) {
                                           final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);
                                           queueSet.setAutoRetryTimes(2)
                                                   .downloadTogether(downloadTaskList)
                                                   .start();
                                           KLog.i("=====狂飙吧，splash下载器!!!");
                                       } else {
                                           KLog.i("====没有需要下载的文件");
                                       }
                                   } else {
                                       KLog.i("====创建splash文件夹失败");
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                KLog.i("========checkSplashResource Exception:" + throwable.getMessage());
                            }
                        });

    }

    public Observable<SplashResourceEntity> getAdvertResource() {
        return Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, SplashResourceEntity>() {
                    @Override
                    public SplashResourceEntity apply(Integer integer) throws Exception {
                        String result = SPUtils.getString(DCApplication.getDCApp(), SPUtils.SPLASH_RESOURCE_DATA, "");
                        List<SplashResourceEntity> resourceEntities = null;
                        if (!TextUtils.isEmpty(result)) {
                            resourceEntities = JSON.parseArray(result, SplashResourceEntity.class);
                        }
                        if (!CollectionUtil.isEmpty(resourceEntities)) {
                            long nowTime = System.currentTimeMillis() / 1000;
                            KLog.i("=======nowTime:" + nowTime);
                            String path = getSplashResourcePath();
                            File dir = new File(path);
                            String filePath;
                            if (dir.exists() && dir.isDirectory()) {
                                Map<String, String> allFileMd5 = getAllFileMd5(dir);
                                String md5Str;
                                if (allFileMd5.size() > 0) {
                                    List<SplashResourceEntity> filterEntities = new ArrayList<>();
                                    for (SplashResourceEntity entity : resourceEntities) {
                                        if (entity != null) {
                                            if (entity.end_time <= nowTime) {
                                                //删除文件
                                                md5Str = entity.file_md5;
                                                if (!allFileMd5.containsKey(md5Str)) {
                                                    md5Str = md5Str.toUpperCase();
                                                }
                                                if (allFileMd5.containsKey(md5Str)) {
                                                    filePath = path + File.separator + allFileMd5.get(entity.file_md5);
                                                    KLog.i("======删除过时的文件：" + filePath);
                                                    FileUtil.deleteFile(filePath);
                                                }
                                            } else {
                                                md5Str = entity.file_md5;
                                                if (!allFileMd5.containsKey(md5Str)) {
                                                    md5Str = md5Str.toUpperCase();
                                                }
                                                if (allFileMd5.containsKey(md5Str) && entity.start_time < nowTime) {
                                                    filePath = path + File.separator + allFileMd5.get(entity.file_md5);
                                                    entity.localPath = filePath;
                                                    KLog.i("======需要使用的文件：" + filePath);
                                                    filterEntities.add(entity);
                                                }
                                            }
                                        }
                                    }
                                    //从过滤后的集合中,随机选取一个实体,启动页随机加载不同图片
                                    int pos = new Random().nextInt(filterEntities.size());
                                    return filterEntities.get(pos);
                                }
                            }
                        }
                        return new SplashResourceEntity();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Map<String, String> getAllFileMd5(File dir) {
        List<File> files = FileUtil.listAllFiles(dir);
        Map<String, String> fileMd5Map = new HashMap<>(4);
        if (!CollectionUtil.isEmpty(files)) {
            String fileMd5;
            for (File file : files) {
                if (file != null && file.exists() && file.isFile()) {
                    try {
                        fileMd5 = BinaryUtil.calculateMd5Str(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        fileMd5 = null;
                    }
                    if (!TextUtils.isEmpty(fileMd5)) {
                        //这里全部是大写
                        fileMd5Map.put(fileMd5.toUpperCase(), file.getName());
                    }
                }
            }
        }
        KLog.i("=====获取到所有文件的md5\n" + CommonUtils.printMap(fileMd5Map));
        return fileMd5Map;
    }

    private FileDownloadListener downloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            KLog.e("pending url:" + task.getUrl() + "\n tag" + task.getTag() + " ，soFarBytes: " + soFarBytes + " ,totalBytes:" + totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            KLog.e("progress url:" + task.getUrl() + "\ntag" + task.getTag() + " ，soFarBytes: " + soFarBytes + " ,totalBytes:" + totalBytes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            KLog.e("completed tag:" + task.getTag() + " ，url:" + task.getUrl());
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            KLog.e("paused url:" + task.getUrl() + "\ntag" + task.getTag() + " ，soFarBytes: " + soFarBytes + " ,totalBytes:" + totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            KLog.e("error tag:" + task.getTag() + " ，url:" + task.getUrl() + ",error message:" + e.getMessage());
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            KLog.e("warn  tag:" + task.getTag() + " ，url:" + task.getUrl());
        }
    };


}
