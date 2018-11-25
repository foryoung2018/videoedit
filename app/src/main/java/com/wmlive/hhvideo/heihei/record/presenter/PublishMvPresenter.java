package com.wmlive.hhvideo.heihei.record.presenter;

import android.text.TextUtils;

import com.dongci.sun.gpuimglibrary.player.script.DCScriptManager;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.opus.PublishResponseEntity;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.record.ProductExtendEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.content.ExportContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MScene;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.VIDEO_EXPORT_HEIGHT;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.VIDEO_EXPORT_WIDTH;
import static com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk.exportMvVideo;

public class PublishMvPresenter extends BasePresenter<AbsPublishView> {
    private final String TAG = "PublishMvPresenter";
    public static final String MODULE_OPUS = "opus";
    public static final String MODULE_MATERIAL = "material";

    public static final String EXTRA_FORMAT_MP4 = "mp4";
    public static final String EXTRA_FORMAT_JPG = "jpg";

    public static final int ERROR_ADD_TASK = 110;
    public static final int ERROR_EXPORT_VIDEO = 120;
    public static final int ERROR_UPLOAD_MATERIAL = 130;
    public static final int ERROR_UPLOAD_COMBINE = 140;
    public static final int ERROR_PUBLISH = 170;

    public static final int TYPE_EXPORT_VIDEO = 310;  //上传素材的任务index必须小于这个值
    public static final int TYPE_COVER = 330;
    public static final int TYPE_WEBP = 340;
    public static final int TYPE_UPLOAD_VIDEO = 350;
    public static final int TYPE_PUBLISH = 360;


    public static final int STEP_COMBINE = 10;
    public static final int STEP_COMBINE_MUX_AUDIO = 11;
    public static final int STEP_COMBINE_MUX_VA = 12;
    public static final int STEP_MATERIAL_MUX = 20;
    public static final int STEP_UPLOAD = 30;
    public static final int STEP_PUBLISH = 40;




    private List<UploadTask> taskList;
    private UploadTask coverTask;
    private UploadTask videoTask;
    private ProductEntity productEntity;

    private int remainMaterialTaskCount;
    private long allMaterialLength;
    private int allProgress = 0;
    private int nextIndex = 0;
    private int perMaterialTaskProgress;//每个素材上传的进度
    private int newStep = STEP_COMBINE;//新的每一步
    private boolean insertGallery;
    PublishResponseEntity publishResponseEntity;


    public PublishMvPresenter(AbsPublishView view) {
        super(view);
        taskList = new ArrayList<>(5);
    }

    public void retryPublish() {
        if(newStep<STEP_UPLOAD){
            return;
        }
        preparePublish(productEntity, insertGallery);
    }

    List<MediaObject> assets;

    public void preparePublish(ProductEntity entity, boolean insertGallery, List<MediaObject> assets) {
        this.assets = assets;
        if (entity != null) {
            productEntity = entity;
            productEntity.productType = ProductEntity.TYPE_DRAFT;

            this.insertGallery = insertGallery;
            KLog.i(TAG, "=======当前已经进入到第：" + newStep + " 步");
            stepControl(newStep);
        } else {
            if (viewCallback != null) {
                viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "no product error");
            }
            KLog.i(TAG, "=====没有作品");
        }
    }

    public void preparePublish(final ProductEntity entity, final boolean insertGallery) {
        if (entity != null) {
            productEntity = entity;
            productEntity.productType = ProductEntity.TYPE_DRAFT;

            this.insertGallery = insertGallery;
            KLog.i(TAG, "=======当前已经进入到第：" + newStep + " 步");
            stepControl(newStep);
        } else {
            if (viewCallback != null) {
                viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "no product error");
            }
            KLog.i(TAG, "=====没有作品");
        }
    }

    /**
     * 步骤管理，便于恢复之前状态
     */
    private void stepControl(int newStep) {
        if (RecordManager.get().getProductEntity() == null) {
            if (viewCallback != null) {
                viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "no product error");
            }
            return;
        }
        this.newStep = newStep;
        switch (newStep) {
            case STEP_COMBINE://导出视频,0 ,30
//                muxAudio(0,30);
                combiVideo(0,30);
                break;
            case STEP_COMBINE_MUX_AUDIO://合成音频
                muxAudio(30,35);
                break;
            case STEP_COMBINE_MUX_VA://合并音视频 video + audio 30 40
                muxVideoAudio(35,40);
                break;
            case STEP_MATERIAL_MUX://合并素材的 video + audio 40 50
                muxMaterial(40,50);
                break;
            case STEP_PUBLISH://最后网络请求 90 100
                publishProduct(null,90,100);
                break;
            case STEP_UPLOAD://上传素材
            default:// 50 90
                uploadMaterial(50,90);
                break;
        }
    }

    private void muxAudio(int start, int end) {
        ArrayList<String> audios = new ArrayList<String>();

        if(!TextUtils.isEmpty(productEntity.extendInfo.bgm_path))
            audios.add(productEntity.extendInfo.bgm_path);
        else {
            stepControl(STEP_COMBINE_MUX_VA);
            return;
        }

        if(!TextUtils.isEmpty(productEntity.combineAudio))
            audios.add(productEntity.combineAudio);

        RecordUtilSdk.mixAudios(audios, new VideoListener() {
            @Override
            public void onStart() {
                if(viewCallback!=null)
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO,start);
            }

            @Override
            public void onProgress(int progress) {
                KLog.i("mixAudio---onProgress>" + progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i("mixAudio---onFinish>" + code+outpath);
                //混合成功后
                if (code == SdkConstant.RESULT_SUCCESS) {
                    KLog.i("mixAudio--onFinish-deleteFile-combineAudio>" + RecordManager.get().getProductEntity().combineAudio);
                    RecordManager.get().getProductEntity().combineAudio = outpath;
                    if(viewCallback!=null)
                        viewCallback.onPublishing(TYPE_EXPORT_VIDEO,end);
                    stepControl(STEP_COMBINE_MUX_VA);
                }else {
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(TYPE_EXPORT_VIDEO, "声音混合失败");
                    }
                }
            }

            @Override
            public void onError() {
                if (viewCallback != null) {
                    viewCallback.onPublishFail(TYPE_EXPORT_VIDEO, "声音混合失败");
                }
            }
        });
    }

    private void combiVideo(int start,int end) {
        MVideoConfig mVideoConfig = new MVideoConfig();

        Scene scene = new MScene();
        String color = RecordManager.get().getProductEntity().extendInfo.bgColor;
        assets.remove(assets.size()-1);
        scene.assets = assets;
        List<Scene> scenes = new ArrayList<Scene>(1);
        scenes.add(scene);
        exportMvVideo(scenes, mVideoConfig,color, new ExportListener() {
            @Override
            public void onExportStart() {
                KLog.d("exportCombineVideo", "onExportStart: ");
                if(viewCallback!=null)
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO,start);
            }

            @Override
            public void onExporting(int progress, int max) {
                KLog.d("exportCombineVideo", "onExporting: progress==" + progress);
                if(viewCallback!=null)
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, start + RecordUtil.calculateProgress(progress, 100, end-start));
            }

            @Override
            public void onExportEnd(int var1, String path) {
                KLog.d("exportCombineVideo", "onExportEnd: path==" + path);
                if (RecordManager.get().getProductEntity() == null) {
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "no product error");
                    }
                    return;
                }
                allProgress = end;
                RecordManager.get().getProductEntity().combineVideo = path;
                stepControl(STEP_COMBINE_MUX_AUDIO);

                String coverPath = RecordManager.get().getProductEntity().baseDir + File.separator + RecordManager.PREFIX_COVER_FILE + RecordFileUtil.getTimestampString() + ".jpg";
                RecordFileUtil.getVideoCover(path, coverPath, RecordManager.get().getProductEntity().getExtendInfo().thumbnail_generate_time, VIDEO_EXPORT_WIDTH, VIDEO_EXPORT_HEIGHT);
                KLog.d(TAG, "onExportEnd: coverPath==" + coverPath);
                RecordManager.get().getProductEntity().coverPath = coverPath;


            }
        });
    }

    /**
     * 将所有的视频都进行合并
     * 小格子视频
     */
    private void muxMaterial(int start,int end) {
        RecordUtilSdk.muxAudioVideo(RecordManager.get().getProductEntity(), new ExportListener() {


            @Override
            public void onExportStart() {
                KLog.i(TAG, newStep + "muxMaterial-onExportStart=-->" + allProgress);
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, start);
                }
            }

            @Override
            public void onExporting(int progress, int max) {
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, start + RecordUtil.calculateProgress(progress, 100, end-start));
                }
            }

            @Override
            public void onExportEnd(int var1, String path) {
                KLog.i(TAG, newStep + "muxMaterial-onExportEnd=-->" + allProgress);
                if (var1 == SdkConstant.RESULT_SUCCESS) {
                    allProgress = end;
                    if (viewCallback != null) {
                        viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                    }
                    //去上传
                    stepControl(STEP_UPLOAD);
                } else {
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(TYPE_EXPORT_VIDEO, "素材导出失败");
                    }
                }
            }
        });
    }

    /**
     * 合并 video + audio
     * 大视频
     */
    private void muxVideoAudio(int start,int end) {
        RecordUtilSdk.muxAudioVideoCombine(RecordManager.get().getProductEntity(), new ExportListener() {

            @Override
            public void onExportStart() {
                viewCallback.onPublishing(TYPE_EXPORT_VIDEO, start);
            }

            @Override
            public void onExporting(int progress, int max) {
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, start + RecordUtil.calculateProgress(progress, 100, end-start));
                }
            }

            @Override
            public void onExportEnd(int var1, String path) {
                if (var1 == SdkConstant.RESULT_SUCCESS) {
                    allProgress = end;
                    if (viewCallback != null) {
                        viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                    }
                    //去 合并素材
                    stepControl(STEP_MATERIAL_MUX);
                } else {
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(TYPE_EXPORT_VIDEO, "素材导出失败");
                    }
                }
            }
        });
    }


    /**
     * 初始化
     * 导出视频信息
     */
    private MVideoConfig initVideoConfig() {
        //创建一个导出路径
        final String editingPath = RecordFileUtil.createExportFile();
        MVideoConfig videoConfig = new MVideoConfig();
        videoConfig.setVideoPath(editingPath);
        videoConfig.setKeyFrameTime(24);//1s 一个关键帧
        if (RecordManager.get().getProductEntity().frameInfo == null) {//左侧上传，需要配置宽高
            aVideoConfig video = VideoUtils.getMediaInfor(RecordManager.get().getShortVideoEntity(0).editingVideoPath);
            videoConfig.setVideoSize(video.getVideoWidth(), video.getVideoHeight());
        } else {
            videoConfig.setVideoSize(RecordManager.get().getProductEntity().frameInfo.opus_width, RecordManager.get().getProductEntity().frameInfo.opus_height);
        }
        return videoConfig;
    }

    /**
     * 播放器内容 Scence，，导出内容，
     * 是否需要build？
     * 需要内容 所有的视频,需要 PlayerExport
     */
    private List<Scene> initWaterVideoContent(ProductEntity entity) {
        List<MediaObject> assets = ExportContentFactory.getWaterMedias(entity);
        List<Scene> scenes = PlayerContentFactory.createScenes(assets);
        return scenes;
    }


    /**
     * 上传素材，总进度30%，当前需要进度20%
     *
     * @param start
     */
    private void uploadMaterial(int start,int end) {
        ProductEntity productEntity = RecordManager.get().getProductEntity();
        //导出水印视频
        if (insertGallery) {
            exportWatermarkVideo(productEntity);
        }
        if (productEntity != null) {
            UploadTask task;
            remainMaterialTaskCount = 0;
            nextIndex = 0;
            ShortVideoEntity videoEntity;
            taskList.clear();
            for (int index = 0, n = productEntity.shortVideoList.size(); index < n; index++) {
                videoEntity = productEntity.shortVideoList.get(index);
                KLog.i(TAG, "=====上传素材地址：" + videoEntity.combineVideoAudio + VideoUtils.getVideoLength(videoEntity.combineVideoAudio));
                if (videoEntity != null && videoEntity.hasEditingVideo()
                        && (videoEntity.hasEdited || videoEntity.originalId == 0)) {
                    task = new UploadTask(uploadTaskView, index, MODULE_MATERIAL, EXTRA_FORMAT_MP4,
                            videoEntity.combineVideoAudio);
                    task.videoLength = videoEntity.getEditingDuringMs();
                    task.originalId = videoEntity.originalId;
                    task.quality = videoEntity.quality == FrameInfo.VIDEO_QUALITY_HIGH ? "high" : "high";
                    taskList.add(task);
                    allMaterialLength += task.contentLength;
                    remainMaterialTaskCount++;
                    KLog.i(TAG, "====添加UploadTask：" + remainMaterialTaskCount + " ,detail:" + task.toString());
                }
            }
            KLog.i(TAG, "=====素材文件任务个数：" + remainMaterialTaskCount);
            if (remainMaterialTaskCount > 0) {
                KLog.i(TAG, "====开始上传素材，素材文件总大小：" + allMaterialLength);
                perMaterialTaskProgress = (int) (15 * 1f / remainMaterialTaskCount*1.0f);
                KLog.i(TAG, "====开始上传素材，素材上传进度：" + perMaterialTaskProgress);
                taskList.get(0).startUpload();
            } else {
                KLog.i(TAG, "=====没有任务,开始上传封面");
                getVideoCover(productEntity);
            }
        } else {
            if (uploadTaskView != null) {
                uploadTaskView.onUploadFail(ERROR_ADD_TASK, "no short video");
            }
        }
    }

    /**
     * 导出封面以及上传封面，总进度40%，当前需要进度10%
     *
     * @param productEntity
     */
    private void getVideoCover(final ProductEntity productEntity) {
//        allProgress = productEntity.isLocalUploadVideo() ? 50 : 50;
        allProgress = 70;
        if (productEntity != null) {
            if (!TextUtils.isEmpty(productEntity.combineVideoAudio)
                    && new File(productEntity.combineVideoAudio).exists()) {
                uploadCover(productEntity.coverPath);
            } else {
                KLog.i(TAG, "====作品视频不存在");
                if (uploadTaskView != null) {
                    uploadTaskView.onUploadFail(TYPE_COVER, "combine video not exist");
                }
            }
        } else {
            KLog.i(TAG, "====作品不存在");
            if (uploadTaskView != null) {
                uploadTaskView.onUploadFail(TYPE_COVER, "product not exist");
            }
        }
    }

    private void uploadCover(String coverPath) {
        if (coverTask == null) {
            coverTask = new UploadTask(uploadTaskView, TYPE_COVER, MODULE_MATERIAL, EXTRA_FORMAT_JPG, coverPath);
        }
        KLog.i(TAG, "====开始上传视频封面");
        coverTask.startUpload();
    }

    /**
     * 上传导出的作品，
     * 总进度70%，当前需要进度20%：
     * 如果是直接本地上传，总进度10%，当前需要进度80%
     */
    private void uploadCombineVideo() {
        String videoPath = RecordManager.get().getProductEntity().combineVideoAudio;
        allProgress = 75; //productEntity.isLocalUploadVideo() ? 60 : 60;
        if (videoTask == null) {
            videoTask = new UploadTask(uploadTaskView, TYPE_UPLOAD_VIDEO, MODULE_OPUS, EXTRA_FORMAT_MP4, videoPath);
        }
        KLog.i(TAG, "combineVideoAudio====开始上传合成的视频" + videoPath);
        videoTask.startUpload();
    }

    private void exportWatermarkVideo(ProductEntity entity) {
        DCScriptManager.scriptManager().clearScripts();
        RecordUtilSdk.exportWaterMarkVideoMv(initWaterVideoContent(entity), initVideoConfig(), new ExportListener() {
            @Override
            public void onExportStart() {
                KLog.i(TAG, "water-export-start");
            }

            @Override
            public void onExporting(int progress, int max) {
                KLog.i(TAG, "water-export-onExporting" + progress);
            }

            @Override
            public void onExportEnd(int var1, String path) {
                KLog.i(TAG, var1 + "water-export-onExportEnd" + path);
                if (var1 == SdkConstant.RESULT_SUCCESS) {
                    String saveName = "DC_ID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + RecordManager.SUFFIX_VIDEO_FILE;
                    RecordFileUtil.insertToGallery(DCApplication.getDCApp(), path, saveName, true);
                    KLog.i(TAG, var1 + "water-export-onExportEnd--insert>>" + saveName);
                    if (viewCallback != null) {
                        viewCallback.onExportLocal(0, publishResponseEntity);
                    }
                } else {
                    KLog.i(TAG, "=====水印视频导出失败");
                }
            }
        });
    }


    private AbsUploadTaskView uploadTaskView = new AbsUploadTaskView() {

        @Override
        public void onUploading(int taskIndex, long currentSize, long totalSize) {
            if (currentSize % 20480 == 0) {
                KLog.i("====正在上传index:" + taskIndex + ",currentSize:" + currentSize + ",totalSize:" + totalSize);
            }
            if (viewCallback != null) {
                switch (taskIndex) {
                    case TYPE_UPLOAD_VIDEO: //50 ,70
                        KLog.i("大视频上传中。。.》" + (allProgress + RecordUtil.calculateProgress(currentSize, totalSize,  15)));
                        viewCallback.onPublishing(taskIndex, allProgress + RecordUtil.calculateProgress(currentSize, totalSize,  15));
                        break;
                    case TYPE_WEBP://不上传了
//                        viewCallback.onPublishing(taskIndex, allProgress + RecordUtil.calculateProgress(currentSize, totalSize, 20));
                        break;
                    case TYPE_COVER://65 70
                        viewCallback.onPublishing(taskIndex, allProgress + RecordUtil.calculateProgress(currentSize, totalSize, 5));
                        break;
                    default://上传素材的任务 50 65
                        if (taskIndex < productEntity.shortVideoList.size()) {
                            int p = RecordUtil.calculateProgress(currentSize, totalSize, perMaterialTaskProgress);
                            allProgress = 50 + p + (taskList.size() - remainMaterialTaskCount) * perMaterialTaskProgress;
                            KLog.i("======正在上传素材taskIndex：" + taskIndex + " ,allProgress:" + allProgress);
                            viewCallback.onPublishing(taskIndex, allProgress);
                        }
                        break;
                }
            }
        }

        @Override
        public void onUploadOk(int taskIndex, UploadMaterialEntity entity) {
            KLog.i("====完成任务index：" + taskIndex);
            //这是上传素材的任务
            if (!CollectionUtil.isEmpty(productEntity.shortVideoList)) {
                if (taskIndex < productEntity.shortVideoList.size()) {
                    remainMaterialTaskCount--;
                    nextIndex++;
                    productEntity.shortVideoList.get(taskIndex).originalId = entity.id;
                    KLog.i("====素材上传剩余个数：" + remainMaterialTaskCount);
                    if (remainMaterialTaskCount > 0) {
                        if (nextIndex < taskList.size()) {
                            taskList.get(nextIndex).startUpload();
                        }
                    }
                    if (remainMaterialTaskCount == 0) {
                        nextIndex = 0;
                        getVideoCover(productEntity);
                    }
                }
            }
            switch (taskIndex) {
                case TYPE_COVER:
                    KLog.i("=====视频封面上传成功");
                    uploadCombineVideo();
                    break;
                case TYPE_WEBP:
                    KLog.i("=====视频webp上传成功");
                    uploadCombineVideo();
                    break;
                case TYPE_UPLOAD_VIDEO:
                    KLog.i("=====视频合成上传成功");
//                    publishProduct(null);
                    stepControl(STEP_PUBLISH);
                    break;
                default:
                    break;
            }
            if (viewCallback != null) {
                viewCallback.onPublishing(taskIndex, allProgress);
            }
        }

        @Override
        public void onUploadFail(int index, String message) {
            KLog.i("====素材上传失败index：" + index + " ,message:" + message);
            if (!CollectionUtil.isEmpty(productEntity.shortVideoList)) {
                if (index < productEntity.shortVideoList.size()) {
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(ERROR_UPLOAD_MATERIAL, "upload material error，index：" + index);
                    }
                }
            }
            switch (index) {
                case TYPE_COVER:
                    KLog.i("====上传视频封面失败:" + message);
                    if (GlobalParams.StaticVariable.sSupportWebp) {
//                        getVideoWebp(productEntity);
                    } else {
                        KLog.i("====不支持生成webp:" + message);
                        uploadCombineVideo();
                    }
                    break;
                case TYPE_WEBP:
                    KLog.i("====上传视频webp失败:" + message);
                    uploadCombineVideo();
                    break;
                case TYPE_UPLOAD_VIDEO:
                    KLog.i("====上传合成视频失败:" + message);
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(ERROR_UPLOAD_COMBINE, "upload combine video error");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private boolean isPublishSuccess = false;

    private void publishProduct(String[] atUserIds,int start,int end) {
        if (isPublishSuccess == true) {
            return;
        }
        allProgress = start;
        if(viewCallback!=null)
            viewCallback.onPublishing(TYPE_PUBLISH,start);
        KLog.i("======publishProduct"  + " ,allProgress:" + allProgress);
        Map<String, String> map = new HashMap<>(12);
        String desc = null;
        long topicId = 0;
        if (productEntity.topicInfo != null) {
            desc = productEntity.topicInfo.topicDesc;
            topicId = productEntity.topicInfo.topicId;
        }
        if (!TextUtils.isEmpty(desc)) {
            map.put("title", desc);
        }
        if (topicId > 0) {
            map.put("topic_id", Long.toString(topicId));
        }
        if (videoTask != null && !TextUtils.isEmpty(videoTask.getOssPath()) && !TextUtils.isEmpty(videoTask.getOssSign())) {
            map.put("opus_path", videoTask.getOssPath());
            map.put("opus_file_sign", videoTask.getOssSign());
            map.put("crc64", videoTask.getCrc64());
        }
        if (coverTask != null && !TextUtils.isEmpty(coverTask.getOssPath()) && !TextUtils.isEmpty(coverTask.getOssSign())) {
            map.put("opus_cover", coverTask.getOssPath());
            map.put("opus_cover_sign", coverTask.getOssSign());
        }

        map.put("template_name", productEntity.extendInfo.template_name);
        map.put("bg_name", productEntity.extendInfo.bg_name);

        map.put("opus_length", String.valueOf((int) productEntity.getCombineVideoDuringMs()));
        //本地上传默认不能共同创作
        ProductExtendEntity extendInfo = productEntity.getExtendInfo();
        map.put("is_teamwork", extendInfo.allowTeam ? "1" : "0");
        map.put("opus_width", String.valueOf(VIDEO_EXPORT_WIDTH));
        map.put("opus_height", String.valueOf(VIDEO_EXPORT_HEIGHT));

        String meta = getMaterialMeta();
        if (!TextUtils.isEmpty(meta)) {
            map.put("material_meta_data", meta);
        }
        String users = getAtUserIds(atUserIds);
        if (!TextUtils.isEmpty(users)) {
            map.put("at_user_ids", users);
        }
        map.put("ori_opus_id", Long.toString(productEntity.originalId));
        KLog.i("====开始发布 map:" + CommonUtils.printMap(map));
        executeRequest("publishProduct",
                HttpConstant.TYPE_PUBLISH_PRODUCT,
                getHttpApi().publishProduct(productEntity.isLocalUploadVideo() ? InitCatchData.getUploadLocalOpus() : InitCatchData.getPublishMvProduct(), map))
                .subscribe(new DCNetObserver<PublishResponseEntity>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, PublishResponseEntity response) {
                        KLog.i("======发布作品成功"+response);
                        if (viewCallback != null) {
                            publishResponseEntity = response;
                            viewCallback.onPublishing(TYPE_PUBLISH,end);
                            KLog.i("======publishProduct"  + " ,allProgress:end" + end);
                            viewCallback.onPublishOk(response);
                            isPublishSuccess = true;
                        }
                        DCScriptManager.scriptManager().clearScripts();
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        KLog.i("======发布作品失败：" + serverCode + ",message:" + message);
                        DCScriptManager.scriptManager().clearScripts();
                        if (viewCallback != null) {
                            newStep = STEP_PUBLISH;
                            viewCallback.onPublishFail(serverCode, message);
                            isPublishSuccess = false;
                        }
                    }
                });
    }


    private String getMaterialMeta() {
        if (productEntity != null
                && !CollectionUtil.isEmpty(productEntity.shortVideoList)
                && productEntity.shortVideoList.size() > 0) {
            StringBuilder sb = new StringBuilder(20);
            ShortVideoEntity shortVideoEntity;
            for (int i = 0, n = productEntity.shortVideoList.size(); i < n; i++) {
                shortVideoEntity = productEntity.shortVideoList.get(i);
                if (shortVideoEntity != null && shortVideoEntity.hasEditingVideo()) {
                    sb.append(String.valueOf(shortVideoEntity.originalId))
                            .append(",")
                            .append(String.valueOf(i))
                            .append(";");
                }
            }
            String result = sb.toString();
            if (result.endsWith(";")) {
                return result.substring(0, result.lastIndexOf(";"));
            }
            return result;
        }
        return null;
    }

    private String getAtUserIds(String[] atUserIds) {
        if (atUserIds != null && atUserIds.length > 0) {
            StringBuilder sb = new StringBuilder(20);
            for (String userId : atUserIds) {
                if (!TextUtils.isEmpty(userId)) {
                    sb.append(userId).append(",");
                }
            }
            String result = sb.toString();
            if (result.endsWith(",")) {
                return result.substring(0, result.lastIndexOf(","));
            }
            return result;
        }
        return null;
    }


}
