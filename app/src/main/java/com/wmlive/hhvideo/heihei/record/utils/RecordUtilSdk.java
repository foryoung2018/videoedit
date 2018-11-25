package com.wmlive.hhvideo.heihei.record.utils;

import android.graphics.RectF;
import android.text.TextUtils;

import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
import com.dongci.sun.gpuimglibrary.common.CutEntity;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.ClipVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.record.config.ExportConfig;
import com.wmlive.hhvideo.heihei.record.config.RecordSettingSDK;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.content.ExportContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideosListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MScene;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wmlive.hhvideo.heihei.record.config.RecordSettingSDK.WATERMARK_HEIGHT;
import static com.wmlive.hhvideo.heihei.record.config.RecordSettingSDK.WATERMARK_HEIGHT_ID;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_COMBINE_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_AUDIO_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_VIDEO_FILE;

public class RecordUtilSdk {


    private static final String TAG = "RecordUtilSdk";

    /**
     * fun: 合成视频
     * 取代:joinAdnReverse2
     *
     * @param entity
     */
    public static void compose(ShortVideoEntity entity, VideoListener videoListener) {
        //导出视频，逻辑，
        // 1.只有 一个视频，不需要导出，直接产生
        // 2 有多个clip 视频时，需要合成一个视频，然后进行后续操作
        //3.存在多格视频，需要将多格视频组合成一个视频进行播放？

        //如果当前视频已经合成过了，则不需要合成
        if (entity != null && entity.hasClipVideo()) {//多段视频

            if (entity.getClipList().size() > 0) {
                String outPath = RecordFileUtil.createTimestampFile(entity.baseDir,
                        PREFIX_COMBINE_FILE, SUFFIX_VIDEO_FILE, false);
                composeVideos(entity, outPath, videoListener);
                return;
            }
//            else if (entity.getClipList().size() == 1) {//将之前的视频数据复制到指定的目录，然后重命名
//                videoListener.onStart();
//                videoListener.onFinish(DCCameraConfig.SUCCESS, entity.getClipList().get(0).videoPath);
//            } else {//不存在
//
//            }
        } else if (entity != null && entity.importVideoPath != null) {//本地导入视频存在
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.SUCCESS, entity.importVideoPath);
        } else if (!TextUtils.isEmpty(entity.editingVideoPath)) {
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.SUCCESS, entity.editingVideoPath);
        } else {
            KLog.i(TAG + "====没有视频");
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.ERROR_VIDEO_NO, null);
        }
    }

    /**
     * 合并音频 连接 多段合成一段
     *
     * @param entity
     * @param videoListener
     */
    public static void composeAudio(ShortVideoEntity entity, VideoListener videoListener) {
        //导出视频，逻辑，
        // 1.只有 一个视频，不需要导出，直接产生
        // 2 有多个clip 视频时，需要合成一个视频，然后进行后续操作
        //3.存在多格视频，需要将多格视频组合成一个视频进行播放？

        //如果当前视频已经合成过了，则不需要合成
        if (entity != null && entity.hasClipVideo()) {//多段视频

            if (entity.getClipList().size() > 0) {
                String outPath = RecordFileUtil.createTimestampFile(entity.baseDir,
                        PREFIX_COMBINE_FILE, SUFFIX_AUDIO_FILE, false);
                composeAudios(entity, outPath, videoListener);//？？？？
                return;
            }
//            else if (entity.getClipList().size() == 1) {//将之前的视频数据复制到指定的目录，然后重命名
//                videoListener.onStart();
//                videoListener.onFinish(DCCameraConfig.SUCCESS, entity.getClipList().get(0).audioPath);
//            } else {//不存在
//
//            }
//            FileUtil.deleteFiles(new File(outPath));//删除已经创建的文件
        } else if (entity != null && entity.importVideoPath != null) {//本地导入视频存在
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.SUCCESS, entity.editingAudioPath);
        } else if (!TextUtils.isEmpty(entity.editingAudioPath)) {
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.SUCCESS, entity.editingAudioPath);
        } else {
            KLog.i(TAG + "composeAudio====没有视频");
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.ERROR_VIDEO_NO, null);
        }
    }

    /**
     * 同时拼接 音频视频
     */
    public static void composeVideoAudio(ShortVideoEntity entity, VideosListener videoListener){
        //导出视频，逻辑，
        // 1.只有 一个视频，不需要导出，直接产生
        // 2 有多个clip 视频时，需要合成一个视频，然后进行后续操作
        //3.存在多格视频，需要将多格视频组合成一个视频进行播放？

        //如果当前视频已经合成过了，则不需要合成
        if(entity!=null)
            KLog.i(entity.hasClipVideo()+"composeVideoAudio--importVideoPath>"+entity.importVideoPath);
        if (entity != null && entity.hasClipVideo()) {//多段视频
            if (entity.getClipList().size() > 0) {

                String outAudioPath = RecordFileUtil.createTimestampFile(entity.baseDir,
                        PREFIX_COMBINE_FILE, SUFFIX_AUDIO_FILE, false);
                String outVideoPath = RecordFileUtil.createTimestampFile(entity.baseDir,
                        PREFIX_COMBINE_FILE, SUFFIX_VIDEO_FILE, false);
                composeVideoAudios(entity, outVideoPath,outAudioPath, videoListener);//);
                return;
            }
        } else if (entity != null && entity.importVideoPath != null) {//本地导入视频存在
            KLog.i("composeVideoAudio--importVideoPath-本地导入视频>"+entity.importVideoPath);
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.SUCCESS, entity.editingVideoPath,entity.editingAudioPath);
            return;
        } else if (!TextUtils.isEmpty(entity.editingAudioPath)) {
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.SUCCESS, entity.editingVideoPath,entity.editingAudioPath);
        } else {
            KLog.i(TAG + "composeAudio====没有视频");
            videoListener.onStart();
            videoListener.onFinish(DCCameraConfig.ERROR_VIDEO_NO, null,null);
        }
    }

    /**
     * 组合音频
     * 多路变一路
     */
    public static void mixAudios(ProductEntity productEntity, VideoListener exportListener) {
        if (exportListener == null)
            return;
        KLog.d("TAG", "mixAudios-pre: outpath==");

        if (productEntity == null) {
            exportListener.onFinish(DCCameraConfig.ERROR_VIDEO_NO, null);
            return;
        }
        //如果不需要合成，
        ArrayList<String> audios = new ArrayList<String>();
        for (ShortVideoEntity shortVideoEntity : productEntity.shortVideoList) {
            KLog.d("mixAudios-pre" + "editingAudioPath--pre>>" + shortVideoEntity.editingAudioPath);
            if (!TextUtils.isEmpty(shortVideoEntity.editingAudioPath) && new File(shortVideoEntity.editingAudioPath).exists() && new File(shortVideoEntity.editingAudioPath).length()>200) {
                audios.add(shortVideoEntity.editingAudioPath);
                KLog.d("mixAudios-pre" + "editingAudioPath>>" + shortVideoEntity.editingAudioPath);
            }
        }
        KLog.d("mixAudios-pre" + "start" + audios.size());
        mixAudios(audios,exportListener);

    }

    /**
     *
     * @param audios
     * @param videoListener
     */
    public static void mixAudios(ArrayList<String> audios,VideoListener videoListener){

        videoListener.onStart();
        VideoEngine dcVideoManager = new VideoEngine();
        if (audios.size() == 0) {//没有数据，不需要合成
            videoListener.onFinish(DCCameraConfig.ERROR_VIDEO_NO, null);
        }else {//真正混合
            String outPath = RecordFileUtil.createTimestampFile(RecordManager.get().getProductEntity().baseDir,
                    PREFIX_COMBINE_FILE, SUFFIX_AUDIO_FILE, true);
            KLog.i("mixAudios--pre>" + outPath);
            dcVideoManager.mixAudio(audios, outPath, videoListener);
            KLog.i("mixAudios--end>" + new File(outPath).exists());
        }
    }

    private static List<CutEntity> initClipData(ProductEntity productEntity) {
        List<CutEntity> list = new ArrayList<CutEntity>();
        KLog.i("initClip-->start>" + "trimEnd" + productEntity.shortVideoList.size());
        for (ShortVideoEntity entity : productEntity.shortVideoList) {
//            if (entity.trimEnd == 0)
//                continue;
            CutEntity cutEntity = new CutEntity();
            if (TextUtils.isEmpty(entity.combineVideoAudio)) {//处理之前的草稿箱，
                if(TextUtils.isEmpty(entity.editingVideoPath)){//共同创作，为空进来
                    continue;
                }
                if (TextUtils.isEmpty(entity.editingAudioPath)) {//处理之前的草稿箱问题
                    entity.combineVideoAudio = entity.editingVideoPath;
                }

            }
            //视频分离，如果已经分离的视频，
            if(!TextUtils.isEmpty(entity.editingAudioPath) && !TextUtils.isEmpty(entity.editingVideoPath)){//同时不为空，则不需要分解
                continue;
            }
            cutEntity.path = entity.combineVideoAudio;
            cutEntity.start = (long) entity.trimStart;
            cutEntity.duration = (long) (entity.trimEnd - entity.trimStart);
            cutEntity.audioPath = entity.editingAudioPath;//音频数据
            //创建裁剪后的 视频地址，音频地址
            cutEntity.cutPath = RecordFileUtil.createVideoFile(entity.baseDir);
            cutEntity.cutAudioPath = RecordFileUtil.createAudioFile(entity.baseDir);

            KLog.i("initClip-->start>" + cutEntity.cutPath + "trimEnd" + cutEntity.cutAudioPath);
            list.add(cutEntity);
        }
        return list;
    }

    /**
     * 1.将所有视频进行分离
     *
     */
    public static void split(VideoListener videoListener) {
        List<CutEntity> list = initClipData(RecordManager.get().getProductEntity());
        KLog.d("TAG", "split-pre: outpath==" + list.size());
        new VideoEngine().splitVideoAudio(list, new VideoListener() {

            @Override
            public void onStart() {
                videoListener.onStart();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.d("TAG", "split-onFinish: outpath== code==  " + code);
                //分割后的
                videoListener.onFinish(code,outpath);
            }

            @Override
            public void onError() {
                videoListener.onError();
            }
        });
    }

    /**
     * 1.将所有视频进行分离
     * 2.多路合并成一路
     */
    public static void splitAndMuxAudio(VideoListener videoListener) {
        split(new VideoListener() {

            @Override
            public void onStart() {
                videoListener.onStart();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.d("TAG", "split-onFinish: outpath== code==  " + code);
                //分割后的
                mixAudios(RecordManager.get().getProductEntity(), videoListener);
            }

            @Override
            public void onError() {
                videoListener.onError();
            }
        });
    }

    /**
     * 导出所有的视频资源，用于临时播放
     */
    public static void muxAudioVideo(ProductEntity productEntity, ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
        final ArrayList<String>[] lists = initVideos(productEntity);
        //
        videoEngine.muxAudioVideo(lists[0], lists[1], lists[2], new VideoListener() {
            @Override
            public void onStart() {
                exportListener.onExportStart();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.e("muxAudioVideo-finish" + code + "size" + lists[2].size());
                for (int i = 0; i < productEntity.shortVideoList.size(); i++) {
                    for (int j = 0; j < lists[2].size(); j++) {
                        if (productEntity.shortVideoList.get(i).editingVideoPath != null && productEntity.shortVideoList.get(i).editingVideoPath.equals(lists[0].get(j))) {
                            String path = lists[2].get(j);
                            productEntity.shortVideoList.get(i).combineVideoAudio = path;
                            KLog.e(TAG, j + "素材本地地址::->" + lists[2].get(j) + VideoUtils.getVideoLength(lists[2].get(j)));
                        }
                    }
                }
                RecordManager.get().updateProduct();
                KLog.i("dialog---->muxAudioVideo-finish");
                exportListener.onExportEnd(code, outpath);
            }

            @Override
            public void onError() {
                exportListener.onExportEnd(-2, "");

            }
        });
    }

    /**
     * 合并大视频，大音频，
     *
     * @param productEntity
     * @param exportListener
     */
    public static void muxAudioVideoCombine(ProductEntity productEntity, ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
        ArrayList<String>[] lists = initVideosCombine(productEntity);
        //
        videoEngine.muxAudioVideo(lists[0], lists[1], lists[2], new VideoListener() {
            @Override
            public void onStart() {
                exportListener.onExportStart();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {

                if (lists[2].size() > 0) {
                    productEntity.combineVideoAudio = lists[2].get(0);
                }
                KLog.i("combineVideoAudio-上传大视频finish" + productEntity.combineVideoAudio);
                RecordManager.get().updateProduct();
                exportListener.onExportEnd(code, outpath);

            }

            @Override
            public void onError() {
                exportListener.onExportEnd(-2, "");

            }
        });
    }

    private static ArrayList<String>[] initVideos(ProductEntity productEntity) {
        ArrayList<String> videos = new ArrayList<String>();
        ArrayList<String> audios = new ArrayList<String>();
        ArrayList<String> outpaths = new ArrayList<String>();
        for (ShortVideoEntity shortVideoEntity : productEntity.shortVideoList) {
            KLog.i("doNext---initAudios-->>" + shortVideoEntity.editingAudioPath);
            KLog.i("doNext---initVideos-->>" + shortVideoEntity.editingVideoPath + VideoUtils.getVideoLength(shortVideoEntity.editingVideoPath));
            if (!TextUtils.isEmpty(shortVideoEntity.editingAudioPath) &&
                    !TextUtils.isEmpty(shortVideoEntity.editingVideoPath)) { //都不为空情况下，
                videos.add(shortVideoEntity.editingVideoPath);
                audios.add(shortVideoEntity.editingAudioPath);
                //创建一个导出视频
                String path = RecordFileUtil.createVideoFile(shortVideoEntity.baseDir, "combineTemp");
                KLog.i("doNext---创建素材本地地址-->>" + path);
                shortVideoEntity.combineVideoAudio = path;
                outpaths.add(path);
            }
        }
        return new ArrayList[]{videos, audios, outpaths};
    }

    /**
     * 大视频
     *
     * @param productEntity
     * @return
     */
    private static ArrayList<String>[] initVideosCombine(ProductEntity productEntity) {
        ArrayList<String> videos = new ArrayList<String>();
        ArrayList<String> audios = new ArrayList<String>();
        ArrayList<String> outpaths = new ArrayList<String>();
        videos.add(productEntity.combineVideo);
        audios.add(productEntity.combineAudio);
        KLog.i("CombineVideo-video>" + productEntity.combineVideo);
        KLog.i("CombineVideo-audio>" + productEntity.combineAudio);
        String path = RecordFileUtil.createVideoFile(productEntity.baseDir, "combineL");
        outpaths.add(path);

        return new ArrayList[]{videos, audios, outpaths};
    }



//    /**
//     * 导出视频
//     */
//    public static void exportVideo(final TextureView mPlayerView, List<MScene> list, String outPath, ExportConfig exportType, final ExportListener exportListener){
//        VideoEngine videoEngine = new VideoEngine();
//        DCMediaInfoExtractor.MediaInfo outputFileInfo =initExportConfig(outPath,exportType);
//        videoEngine.export(mPlayerView, list, outputFileInfo,exportListener);
//    }
//
//    public static void exportVideo(List<MScene> list,MVideoConfig videoConfig,ExportListener exportListener){
//        VideoEngine videoEngine = new VideoEngine();
//        videoEngine.export(list,videoConfig,exportListener);
//    }

    /**
     * 多格子合成要给视频
     * 如果没有设置，导出后的视频，与原视频宽高一致
     *
     * @param list
     * @param videoConfig
     * @param exportListener
     */
    public static void exportMvVideo(List<Scene> list, MVideoConfig videoConfig, String color,ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
        if(TextUtils.isEmpty(videoConfig.getFilePath())){
            videoConfig.setVideoPath(RecordFileUtil.createExportFile());
            videoConfig.setVideoSize(RecordSetting.VIDEO_EXPORT_WIDTH, RecordSetting.VIDEO_EXPORT_HEIGHT);
        }else {

        }
        videoConfig.setVideoSize(RecordSetting.VIDEO_EXPORT_WIDTH, RecordSetting.VIDEO_EXPORT_HEIGHT);
        videoConfig.setVideoEncodingBitRate(RecordSettingSDK.VIDEO_PUBLISH_BITRATE_HEIGHT_MV);
        videoEngine.export(list, videoConfig, color,exportListener);
    }


    /**
     * 多格子合成要给视频
     * 如果没有设置，导出后的视频，与原视频宽高一致
     *
     * @param list
     * @param videoConfig
     * @param exportListener
     */
    public static void _exportMvVideo(List<Scene> list, MVideoConfig videoConfig, String color,ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
        videoEngine.export(list, videoConfig, color,exportListener);
    }




    /**
     * 多格子合成要给视频
     *` 如果没有设置，导出后的视频，与原视频宽高一致
     *
     * @param list
     * @param videoConfig
     * @param exportListener
     */
    public static void exportCombineVideo(List<Scene> list, MVideoConfig videoConfig, ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
        if (videoConfig.getFilePath() == null) {//设置默认的值
            videoConfig.setVideoPath(RecordFileUtil.createExportFile());
            videoConfig.setVideoSize(RecordSetting.VIDEO_WIDTH, RecordSetting.VIDEO_HEIGHT);
            videoConfig.setVideoEncodingBitRate(RecordSettingSDK.VIDEO_PUBLISH_BITRATE_LOW);
        }
        videoEngine.export(list, videoConfig, exportListener);
    }


    /**
     * 导出单一视频，视频的大小宽高不变
     *
     * @param list
     * @param videoConfig
     * @param quality
     * @param exportListener
     */
    public static void exportSingleVideo(List<Scene> list, MVideoConfig videoConfig, int quality, ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
        if (videoConfig.getFilePath() == null) {//设置默认的值
            videoConfig.setVideoPath(RecordFileUtil.createExportFile());
        }
        if (quality == FrameInfo.VIDEO_QUALITY_HIGH) {
            videoConfig.setVideoSize(RecordSettingSDK.PRODUCT_WIDTH, RecordSettingSDK.PRODUCT_HEIGHT);
            videoConfig.setVideoEncodingBitRate(RecordSettingSDK.VIDEO_PUBLISH_BITRATE_HEIGHT);
        } else {
            videoConfig.setVideoSize(RecordSetting.VIDEO_WIDTH, RecordSetting.VIDEO_HEIGHT);
            videoConfig.setVideoEncodingBitRate(RecordSettingSDK.VIDEO_PUBLISH_BITRATE_LOW);
        }
        videoEngine.export(list, videoConfig, exportListener);
    }

    /**
     * 本地视频导出，码率为1.5，宽高不变
     *
     * @param list
     * @param videoConfig
     * @param exportListener
     */
    public static void exportLocalVideo(List<Scene> list, MVideoConfig videoConfig, ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
        if (videoConfig.getFilePath() == null) {//设置默认的值
            videoConfig.setVideoPath(RecordFileUtil.createExportFile());
            videoConfig.setVideoEncodingBitRate(RecordSettingSDK.VIDEO_PUBLISH_BITRATE_HEIGHT);
        }
        videoEngine.export(list, videoConfig, exportListener);
    }

    /**
     * 本地直接导入视频，上传
     * @param list
     * @param videoConfig
     * @param exportListener
     */
    public static void exportLocalUploadVideo(List<Scene> list, MVideoConfig videoConfig, ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
//        if (videoConfig.getFilePath() == null) {//设置默认的值
//            videoConfig.setVideoPath(RecordFileUtil.createExportFile());
//            videoConfig.setVideoEncodingBitRate(RecordSettingSDK.VIDEO_PUBLISH_BITRATE_HEIGHT);
//            videoConfig.setDefaultAudioInfo();
//            videoConfig.setVideoSize(540,960);
//        }
        KLog.i("video-width=5>"+videoConfig.getVideoWidth()+"height:>"+videoConfig.getVideoHeight());
        videoEngine.export(list, videoConfig, exportListener);
    }

    /**
     * 导出本地录制视频，
     * @param list
     * @param videoConfig
     * @param exportListener
     */
    public static void exportLocalRecordVideo(List<Scene> list, MVideoConfig videoConfig, ExportListener exportListener) {
        VideoEngine videoEngine = new VideoEngine();
        if (videoConfig.getFilePath() == null) {//设置默认的值
            videoConfig.setVideoPath(RecordFileUtil.createExportFile());
            videoConfig.setVideoEncodingBitRate(RecordSettingSDK.VIDEO_PUBLISH_BITRATE_HEIGHT);

        }
        videoEngine.export(list, videoConfig, exportListener);
    }

    /**
     * 导出单个视频
     */
    public static void exportSingleVideo(ShortVideoEntity shortVideoEntity, MVideoConfig videoConfig, ExportListener exportListener) {
        List<Scene> list = getSingleSence(shortVideoEntity);
        KLog.i("exportMaterial--videos" + list.size());
        exportSingleVideo(list, videoConfig, shortVideoEntity.quality, exportListener);
    }

    /**
     * 单个视频没有特效了,针对时间段 的特效
     *
     * @param shortVideoEntity
     */
    public static boolean loadSingleVideo(ShortVideoEntity shortVideoEntity) {
        if (shortVideoEntity != null
                && !TextUtils.isEmpty(shortVideoEntity.editingVideoPath)
                && new File(shortVideoEntity.editingVideoPath).exists()) {
            return true;
        }
        return false;
    }

    /**
     * 创建单视频显示 画框
     *
     * @param shortVideoEntity
     * @return
     */
    public static List<Scene> getSingleSence(ShortVideoEntity shortVideoEntity) {
        List<Scene> scenes = new ArrayList<Scene>();
        RectF rectF = new RectF(0, 0, 1, 1);
        MediaObject mediaObject = ExportContentFactory.createRecordMediaObject(shortVideoEntity, rectF, 1);
        if (mediaObject != null) {
            List<MediaObject> mediaObjects = new ArrayList<MediaObject>();
            mediaObjects.add(mediaObject);
            Scene scene = new Scene();
            scene.assets = mediaObjects;
            scenes.add(scene);
        }
        return scenes;
    }

    /**
     * 给资源添加水印
     */
    public static List<Scene> addWaterExport(List<Scene> scenes, String imgPath,int videoWidth,int videoHeight) {
        if (scenes.size() == 0) {
            return scenes;
        }
        bottomWatter = 0.9f;
        topId = 0.068f;
        KLog.i("water-export-->" + videoWidth+"VideoHeight:>"+videoHeight);
        if(videoHeight==544 && videoWidth == 960){//16:9
            left1 = 0.02f ;
            right1 = 0.185f;
            left2 = 0.795f ;
            right2= 0.96f;
            topId = 0.126f;
            bottomWatter = 0.84f;
            waterRateMv = 12.3f/17.0f;
            waterIdRateMv = 9.0f/17.0f;
        }else if(videoHeight==720 && videoWidth == 720) {//1:1
            left1 = 0.03f ;
            right1 = 0.185f;
            left2 = 0.795f ;
            right2= 0.95f;
            waterRateMv = 6.3f/17.0f;
            waterIdRateMv = 6.0f/17.0f;
        }else if(videoHeight==960 && videoWidth == 720) {//3：4
            left1 = 0.03f ;
            right1 = 0.225f;
            left2 = 0.755f ;
            right2= 0.95f;
            waterRateMv = 4.3f/17.0f;
            waterIdRateMv = 4.0f/17.0f;
        }else if(videoHeight==960 && videoWidth == 544) {//全屏
            left1 = 0.03f ;
            right1 = 0.265f;
            left2 = 0.715f ;
            right2= 0.95f;
            waterRateMv = 3.6f/17.0f;
            waterIdRateMv = 3.0f/17.0f;
        }
        Scene scene = scenes.get(0);

        MediaObject temp = getWaterTop();
        scene.assets.add(temp);
        MediaObject tempWater = getIdTop(imgPath);
        scene.assets.add(tempWater);
        MediaObject mediaBottomWater = getWaterBottom(scene.getDuration());
        scene.assets.add(mediaBottomWater);
        MediaObject mediaBottomId = getIdBottom(imgPath);
        scene.assets.add(mediaBottomId);
        KLog.i("assets--->"+scene.assets.size());
        scenes.set(0, scene);
        return scenes;
    }


    public static List<Scene> addWaterExportMv(List<Scene> scenes, String imgPath,int videoWidth,int videoHeight) {
        if (scenes.size() == 0) {
            return scenes;
        }
        KLog.i("water-export-->" + videoWidth+"VideoHeight:>"+videoHeight);
        Scene scene = scenes.get(0);

        left1 = 0.03f ;
        right1 = 0.245f;//0.245f
        left2 = 0.735f ;
        right2= 0.95f;
        topId = 0.07f;
        bottomWatter = 0.9f;
        waterRateMv = 3.3f/17.0f;
        waterIdRateMv = 3.0f/17.0f;


        MediaObject temp = getWaterTop();
        scene.assets.add(temp);
        MediaObject tempWater = getIdTop(imgPath);
        scene.assets.add(tempWater);
        MediaObject mediaBottomWater = getWaterBottom(scene.getDuration());
        scene.assets.add(mediaBottomWater);
        MediaObject mediaBottomId = getIdBottom(imgPath);
        scene.assets.add(mediaBottomId);
        KLog.i("assets--->"+scene.assets.size());
        scenes.set(0, scene);
        return scenes;
    }

    static float left1 = 0.03f ;
    static float right1 = 0.265f;
    static float left2 = 0.715f ;
    static float right2= 0.95f;
    static float topId = 0.08f;
    static float bottomWatter = 0.9f;

    private static MediaObject getWaterTop(){
        List<String> imagePaths = getWaterMarks();
        MediaObject mediaObject = new MAsset();
        mediaObject.setSourceType(DCAsset.DCAssetTypeImages);
        mediaObject.setFillType(DCAsset.DCAssetFillTypeScaleToFit);
        mediaObject.setCropRect(new RectF(0, 0, 158, 60));
        mediaObject.setImagePaths(imagePaths);
        mediaObject.setTimeRange(0, 5 * 1000 * 1000);
        mediaObject.setStartTimeInScene(0);
        float top = 0.02f;

        mediaObject.setRectInVideo(new RectF(left1, top, right1,getHeight(top,right1-left1)));
        mediaObject.setShowRectF(new RectF(0, 0, (int) (158 ), 60));
        mediaObject.setVolume(1.0f);
        mediaObject.assetId = 30;
//        mediaObject.setDecorationName(decorationsBean.getName());
//        mediaObject.setDecorationMaskPath(bg_path + File.separator + decorationsBean.getMask());
        mediaObject.setFrameInterval((long) (1000000 * 1.0f / (24 * 1.0f)));
        return  mediaObject;
    }



    /**
     * 底部水印
     * @return
     */
    private static MediaObject getWaterBottom(long duration){
        List<String> imagePaths = getWaterMarks();
        MediaObject mediaObject = new MAsset();
        mediaObject.setSourceType(DCAsset.DCAssetTypeImages);
        mediaObject.setFillType(DCAsset.DCAssetFillTypeScaleToFit);
        mediaObject.setCropRect(new RectF(0, 0, 132, 50));
        mediaObject.setImagePaths(imagePaths);
        KLog.i("timeRange------>"+duration);
        mediaObject.setStartTimeInScene(5 * 1000 * 1000);
        mediaObject.setTimeRange(0, RecordSetting.MAX_VIDEO_DURATION*1000);

        mediaObject.setRectInVideo(new RectF(left2, bottomWatter, right2,getHeight(bottomWatter,right2-left2)));
        mediaObject.setShowRectF(new RectF(0, 0, (int) (132 ), 50));
        mediaObject.setVolume(1.0f);
        mediaObject.assetId = 40;
        mediaObject.setFrameInterval((long) (1000000 * 1.0f / (24 * 1.0f)));
        return  mediaObject;
    }

    static float waterRateMv = 3.6f/17.0f;
    static float waterIdRateMv = 3.0f/17.0f;


    private static float getHeight(float top,float height){
        return top + height*waterRateMv;
    }

    private static float getIdHeight(float top,float height){
        return top + height*waterIdRateMv;
    }

    private static MediaObject getIdTop(String imgPath){
        MediaObject mediaObject = new MAsset(imgPath, DCAsset.DCAssetTypeImage);

        mediaObject.setTimeRange(0, 5 * 1000 * 1000);
        mediaObject.setStartTimeInScene(0);

        mediaObject.setRectInVideo(new RectF(left1, topId, right1,getIdHeight(topId,right1-left1)));
        mediaObject.setShowRectF(new RectF(0, 0, (int) (WATERMARK_HEIGHT), WATERMARK_HEIGHT_ID));
        mediaObject.setVolume(1.0f);
        mediaObject.assetId = 31;
        return mediaObject;
    }

    private static MediaObject getIdBottom(String imgPath){
        MediaObject mediaObject = new MAsset(imgPath, DCAsset.DCAssetTypeImage);
        mediaObject.setStartTimeInScene(5 * 1000 * 1000);
        mediaObject.setTimeRange(0, RecordSetting.MAX_VIDEO_DURATION*1000);
        float top = 0.95f;
        mediaObject.setRectInVideo(new RectF(left2, top, right2,getIdHeight(top,right2-left2)));
        mediaObject.setShowRectF(new RectF(0, 0, (int) (WATERMARK_HEIGHT ), WATERMARK_HEIGHT_ID));
        mediaObject.setVolume(1.0f);
        mediaObject.assetId = 41;
        return mediaObject;
    }


    private static List<String> getWaterMarks(){
        File file = new File(AppCacheFileUtils.getAppWaterMarksPath());
        String[] files = file.list();
        List<String> list = new ArrayList <String>(files.length);
        for(String s:files){
            list.add(AppCacheFileUtils.getAppWaterMarksPath()+File.separator+s);
        }
        Collections.sort(list);

        return list;
    }

//    public static void exportWaterMarkVideo(PlayerEngine playerEngine,ProductEntity productEntity,ExportListener exportListener) {
//        String watermarkPath = RecordManager.get().getSetting().getWatermarkPath();
//        exportWaterMarkVideo(playerEngine,productEntity,exportListener,watermarkPath);
//    }

    public static void exportWaterMarkVideo(List<Scene> list, MVideoConfig videoConfig, ExportListener exportListener) {
        String watermark = GraphicsHelper.createWatermarkNum(AccountUtil.getDcNum());
//        String watermark = GraphicsHelper.createWatermarkNum(AccountUtil.getDcNum());
        KLog.e(AccountUtil.getDcNum()+"water-export--imgPath-->>" + watermark);
        aVideoConfig config = VideoUtils.getMediaInfor(RecordManager.get().getProductEntity().combineVideoAudio);
        List<Scene> waterScence = addWaterExport(list, watermark,config.getVideoWidth(),config.getVideoHeight());
        videoConfig.setDefaultAudioInfo();
        videoConfig.setVideoSize(config.getVideoWidth(),config.getVideoHeight());
        exportCombineVideo(waterScence, videoConfig, exportListener);
    }

    public static void exportWaterMarkVideoMv(List<Scene> list, MVideoConfig videoConfig, ExportListener exportListener) {
        String watermark = GraphicsHelper.createWatermarkNum(AccountUtil.getDcNum());
//        String watermark = GraphicsHelper.createWatermarkNum(AccountUtil.getDcNum());
        KLog.e(AccountUtil.getDcNum()+"water-export--imgPath-->>" + watermark);
        aVideoConfig config = VideoUtils.getMediaInfor(RecordManager.get().getProductEntity().combineVideoAudio);
        List<Scene> waterScence = addWaterExportMv(list, watermark,config.getVideoWidth(),config.getVideoHeight());
        videoConfig.setDefaultAudioInfo();
        videoConfig.setVideoSize(config.getVideoWidth(),config.getVideoHeight());
        exportCombineVideo(waterScence, videoConfig, exportListener);
    }

    /**
     * 添加水印
     *
     * @param playerEngine
     * @param path
     * @return
     */
    private static PlayerEngine addWaterMark(PlayerEngine playerEngine, String path) {

        return playerEngine;
    }

    /**
     * 1.设置背景图
     * 2.将各个格子的视频加入到 Scence
     * 3.将Scence 添加到 Player 播放器
     *
     * @return 是否包含大于一个 格子视频,则返回true, false 不包含
     */
    private static boolean previewCombineVideo(PlayerEngine playerEngine, ProductEntity productEntity) {
        List<MediaObject> meidas = PlayerContentFactory.getPlayerMediaFromProduct(productEntity);
        if (meidas != null && meidas.size() > 0) {
            Scene scene = playerEngine.createScene();
            scene.assets = meidas;
            playerEngine.addScene(scene);
            return true;
        }
        return false;
    }

    /**
     * 该作品
     * @param productEntity
     * @return
     */
//    private static List<MediaObject> getMediaFromProduct(ProductEntity productEntity){
//        List<MediaObject> meidas = new ArrayList<MediaObject>();
//        if (productEntity == null || productEntity.shortVideoList == null) {
//            return null;
//        }
//        //添加画框
//        for(int i=0;i<productEntity.shortVideoList.size();i++){
//            ShortVideoEntity videoEntity = productEntity.shortVideoList.get(i);
//            MediaObject mediaObject = createMediaObject(videoEntity,productEntity.frameInfo.getLayoutRelativeRectF(i),productEntity.frameInfo.getLayoutAspectRatio(i));
//            if(mediaObject!=null)
//                meidas.add(mediaObject);
//        }
//        return meidas;
//    }

    /**
     * 创建单个 object
     *
     * @return
     */
//    private static MediaObject createMediaObject(ShortVideoEntity videoEntity, RectF frameRecf,float frameRatio){
//        if (videoEntity == null
//                || TextUtils.isEmpty(videoEntity.editingVideoPath)
//                || !new File(videoEntity.editingVideoPath).exists()) {
//            //当前 框无可以编辑的文件
//            return null;
//        }
//        MediaObject mediaObject = new MAsset(videoEntity.editingVideoPath);
////        RectF rectF = frameInfo.getLayoutRelativeRectF(i);
//        KLog.i("=======显示的区域1是：" + frameRecf);
//        mediaObject.setShowRectF(frameRecf);
//        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
//        mediaObject.setAudioMute(true);
////                        media.setMixFactor(videoEntity.getOriginalMixFactor());
//        float[] trimRange = videoEntity.getTrimRange();
//        float trimDuration = videoEntity.getDuring();
//        float voiceStartTime = 0;
//        //视频裁剪,
//        if ((trimRange[0] != 0 || trimRange[1] != 0)) {
//            mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
//            voiceStartTime = trimRange[0];
//            trimDuration = trimRange[1] - trimRange[0]; // 截取时长
//        }
//        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(),frameRatio, 0f);
//        KLog.i("=======显示的区域2是：" + clipRect + " ,ratio:" + frameRatio);
//        mediaObject.setShowRectF(clipRect);
//        return mediaObject;
//    }
    private static DCMediaInfoExtractor.MediaInfo initExportConfig(String outPath, ExportConfig exportType) {
        DCMediaInfoExtractor.MediaInfo outputFileInfo = new DCMediaInfoExtractor.MediaInfo();
        outputFileInfo.videoInfo.width = exportType.getWidth();
        outputFileInfo.videoInfo.height = exportType.getHeight();
        outputFileInfo.videoInfo.videoBitRate = exportType.BITRATE_VIDEO;//mediaInfo.videoBitRate;//(int)(1.5 * 1000 * 1000);
        outputFileInfo.videoInfo.fps = exportType.FPS;//mediaInfo.frameRate;//24;
        outputFileInfo.audioInfo.audioBitRate = exportType.BITRATE_AUDIO;//mediaInfo.audioBitRate;//64000;
        outputFileInfo.audioInfo.sampleRate = exportType.SAMPLE_RATE;//mediaInfo.sampleRate;//44100;
        outputFileInfo.audioInfo.channelCount = exportType.CHANNEL_COUNT;//mediaInfo.channelCount;//1;
        outputFileInfo.filePath = outPath;
        return outputFileInfo;
    }

    /**
     * 组合多个视频
     */
    private static void composeVideos(ShortVideoEntity entity, String outPath, VideoListener videoListener) {
        VideoEngine dcVideoManager = new VideoEngine();
        ArrayList<String> videos = new ArrayList<String>();
        for (ClipVideoEntity clipVideoEntity : entity.getClipList()) {
            if (!TextUtils.isEmpty(clipVideoEntity.videoPath) && new File(clipVideoEntity.videoPath).exists())
                videos.add(clipVideoEntity.videoPath);
        }
        dcVideoManager.compose(videos, outPath, videoListener);
    }

    /**
     * 连接多个音频，成一个
     * 时间变长
     *
     * @param entity
     * @param outPath
     * @param videoListener
     */
    public static void composeAudios(ShortVideoEntity entity, String outPath, VideoListener videoListener) {
        VideoEngine dcVideoManager = new VideoEngine();
        ArrayList<String> audios = new ArrayList<String>();
        for (ClipVideoEntity clipVideoEntity : entity.getClipList()) {
            if (!TextUtils.isEmpty(clipVideoEntity.audioPath) && new File(clipVideoEntity.audioPath).exists())
                audios.add(clipVideoEntity.audioPath);
        }
        dcVideoManager.composeAudio(audios, outPath, videoListener);
    }

    /**
     * 连接多个音频，视频
     * 时间变长
     *
     * @param entity
     * @param outVideoPath
     * @param videoListener
     */
    public static void composeVideoAudios(ShortVideoEntity entity, String outVideoPath,String outAudioPath, VideosListener videoListener) {
        VideoEngine dcVideoManager = new VideoEngine();
        ArrayList<String> audios = new ArrayList<String>();
        ArrayList<String> videos = new ArrayList<String>();
        for (ClipVideoEntity clipVideoEntity : entity.getClipList()) {
            if (!TextUtils.isEmpty(clipVideoEntity.audioPath) && new File(clipVideoEntity.audioPath).exists() &&
                    (!TextUtils.isEmpty(clipVideoEntity.videoPath) && new File(clipVideoEntity.videoPath).exists())){
                videos.add(clipVideoEntity.videoPath);
                audios.add(clipVideoEntity.audioPath);
                KLog.i("composeVideoAudios--audio"+clipVideoEntity.audioPath+new File(clipVideoEntity.audioPath).length());
                KLog.i("composeVideoAudios--video"+clipVideoEntity.videoPath+VideoUtils.getVideoLength(clipVideoEntity.videoPath));
            }
        }
        KLog.i("composeVideoAudios--audio-size>"+audios.size());
        KLog.i("composeVideoAudios--video-size>"+videos.size());
        dcVideoManager.composeVideoAndAudio(videos,audios, outVideoPath,outAudioPath, videoListener);
    }

}
