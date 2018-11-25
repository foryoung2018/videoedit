package com.wmlive.hhvideo.heihei.record.presenter;

import android.text.TextUtils;

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

    private List<UploadTask> taskList;
    private UploadTask coverTask;
    private UploadTask videoTask;
    private ProductEntity productEntity;

    private int remainMaterialTaskCount;
    private long allMaterialLength;
    private int allProgress = 0;
    private int nextIndex = 0;
    private int perMaterialTaskProgress;//每个素材上传的进度
    private int newStep = 1;//新的每一步
    private boolean insertGallery;
    PublishResponseEntity publishResponseEntity;


    public PublishMvPresenter(AbsPublishView view) {
        super(view);
        taskList = new ArrayList<>(5);
    }

    public void retryPublish() {
        preparePublish(productEntity, insertGallery);
    }

    public void preparePublish(final ProductEntity entity, final boolean insertGallery) {
        if (entity != null) {
            productEntity = entity;
            productEntity.productType = ProductEntity.TYPE_PUBLISHING;

            RecordUtil.insertOrUpdateProductToDb(productEntity);
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
            case 1://合并音视频 video + audio
                muxVideoAudio();
                break;
            case 2://合并素材的 video + audio
                muxMaterial();
                break;
            case 20://最后网络请求
                publishProduct(null);
                break;
            case 3://上传素材
            default://30~50
                uploadMaterial(RecordManager.get().getProductEntity());
                break;
        }
    }

    /**
     * 将所有的视频都进行合并
     * 小格子视频
     */
    private void muxMaterial() {
        int start = 25;
        RecordUtilSdk.muxAudioVideo(RecordManager.get().getProductEntity(), new ExportListener() {


            @Override
            public void onExportStart() {
                KLog.i(TAG, newStep + "muxMaterial-onExportStart=-->" + allProgress);
            }

            @Override
            public void onExporting(int progress, int max) {
                allProgress = start + progress;
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                }
            }

            @Override
            public void onExportEnd(int var1, String path) {
                KLog.i(TAG, newStep + "muxMaterial-onExportEnd=-->" + allProgress);
                if (var1 == SdkConstant.RESULT_SUCCESS) {
                    stepControl(3);
                    allProgress = allProgress > 30 ? allProgress : 30;//结束位置，显示30
                    if (viewCallback != null) {
                        viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                    }
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
    private void muxVideoAudio() {
        RecordUtilSdk.muxAudioVideoCombine(RecordManager.get().getProductEntity(), new ExportListener() {

            @Override
            public void onExportStart() {

            }

            @Override
            public void onExporting(int progress, int max) {
                allProgress = progress;
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                }
            }

            @Override
            public void onExportEnd(int var1, String path) {
                if (var1 == SdkConstant.RESULT_SUCCESS) {
                    allProgress = allProgress > 25 ? allProgress : 25;//结束位置，显示25
                    if (viewCallback != null) {
                        viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                    }
                    stepControl(2);
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
     * @param productEntity
     */
    private void uploadMaterial(ProductEntity productEntity) {
        //导出水印视频
        if (insertGallery) {
            exportWatermarkVideo(productEntity);
        }
        allProgress = 30;
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
                perMaterialTaskProgress = (int) (20 * 1f / remainMaterialTaskCount);
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
        allProgress = productEntity.isLocalUploadVideo() ? 50 : 50;
        if (productEntity!=null) {
            if (!TextUtils.isEmpty(productEntity.combineVideoAudio)
                    && new File(productEntity.combineVideoAudio).exists()) {
                uploadCover(productEntity.coverPath );
//                final String coverPath = productEntity.baseDir + File.separator + RecordManager.PREFIX_COVER_FILE + RecordFileUtil.getTimestampString() + ".jpg";
//                KLog.i(TAG, "====开始生成视频封面");
//                Disposable disposable = Observable.just(1)
//                        .subscribeOn(Schedulers.io())
//                        .map(new Function<Integer, Boolean>() {
//                            @Override
//                            public Boolean apply(@NonNull Integer integer) throws Exception {
//                                PlayerEngine playerEngine = new PlayerEngine();
//                                playerEngine.setSnapShotResource(productEntity.combineVideo);
//                                return playerEngine.getSnapShot((long) GlobalParams.Config.VIDEO_COVER_CLIP_SECOND * 1000,
//                                        coverPath);
//                            }
//                        })
//                        .subscribe(new Consumer<Boolean>() {
//                            @Override
//                            public void accept(@NonNull Boolean result) throws Exception {
//                                KLog.i(TAG, "====生成视频封面" + (result ? "成功" : "失败"));
//                                if (result) {
//                                    productEntity.coverPath = coverPath;
//                                    RecordUtil.insertOrUpdateProductToDb(productEntity);
//                                    uploadCover(coverPath);
//                                } else {
//                                    if (uploadTaskView != null) {
//                                        uploadTaskView.onUploadFail(TYPE_COVER, "create video cover fail");
//                                    }
//                                }
//                            }
//                        }, new Consumer<Throwable>() {
//                            @Override
//                            public void accept(@NonNull Throwable throwable) throws Exception {
//                                KLog.i(TAG, "====生成视频封面出错");
//                                throwable.printStackTrace();
//                                if (uploadTaskView != null) {
//                                    uploadTaskView.onUploadFail(TYPE_COVER, "create video cover fail");
//                                }
//                            }
//                        });
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
        allProgress = productEntity.isLocalUploadVideo() ? 60 : 60;
        if (videoTask == null) {
            videoTask = new UploadTask(uploadTaskView, TYPE_UPLOAD_VIDEO, MODULE_OPUS, EXTRA_FORMAT_MP4, videoPath);
        }
        KLog.i(TAG, "combineVideoAudio====开始上传合成的视频" + videoPath);
        videoTask.startUpload();
    }

    private void exportWatermarkVideo(ProductEntity entity) {
        RecordUtilSdk.exportWaterMarkVideo(initWaterVideoContent(entity), initVideoConfig(), new ExportListener() {
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
                        viewCallback.onExportLocal(0,publishResponseEntity);
                    }
                } else {
                    KLog.i(TAG, "=====水印视频导出失败");
                }
            }
        });
    }

    //进度比：导出视频20，上传素材20,上传封面10，上传webp20，上传视频20，发布10

    private AbsUploadTaskView uploadTaskView = new AbsUploadTaskView() {

        @Override
        public void onUploading(int taskIndex, long currentSize, long totalSize) {
            if (currentSize % 20480 == 0) {
                KLog.i("====正在上传index:" + taskIndex + ",currentSize:" + currentSize + ",totalSize:" + totalSize);
            }
            if (viewCallback != null) {
                switch (taskIndex) {
                    case TYPE_UPLOAD_VIDEO:
                        KLog.i("大视频上传中。。.》" + (allProgress + RecordUtil.calculateProgress(currentSize, totalSize, productEntity.isLocalUploadVideo() ? 20 : 20)));
                        viewCallback.onPublishing(taskIndex, allProgress + RecordUtil.calculateProgress(currentSize, totalSize, productEntity.isLocalUploadVideo() ? 20 : 20));
                        break;
                    case TYPE_WEBP://不上传了
                        viewCallback.onPublishing(taskIndex, allProgress + RecordUtil.calculateProgress(currentSize, totalSize, 20));
                        break;
                    case TYPE_COVER://50~60
                        viewCallback.onPublishing(taskIndex, allProgress + RecordUtil.calculateProgress(currentSize, totalSize, 10));
                        break;
                    default://上传素材的任务
                        if (taskIndex < productEntity.shortVideoList.size()) {
                            int p = RecordUtil.calculateProgress(currentSize, totalSize, perMaterialTaskProgress);
                            allProgress = 30 + p + (taskList.size() - remainMaterialTaskCount) * perMaterialTaskProgress;
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
                    RecordUtil.insertOrUpdateProductToDb(productEntity);
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
                    stepControl(20);
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

    private void publishProduct(String[] atUserIds) {
        if (isPublishSuccess == true) {
            return;
        }
        allProgress = 90;
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
        map.put("is_teamwork", productEntity.isLocalUploadVideo() ?
                "0" : (extendInfo.allowTeam ? "1" : "0"));
        map.put("opus_width", String.valueOf(RecordSetting.VIDEO_EXPORT_WIDTH));
        map.put("opus_height", String.valueOf(RecordSetting.VIDEO_EXPORT_HEIGHT));

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
                        KLog.i("======发布作品成功");
                        if (viewCallback != null) {
                            publishResponseEntity = response;
                            viewCallback.onPublishOk(response);
                            isPublishSuccess = true;
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        KLog.i("======发布作品失败：" + serverCode + ",message:" + message);
                        if (viewCallback != null) {
                            newStep = 8;
                            viewCallback.onPublishFail(ERROR_PUBLISH, message);
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
