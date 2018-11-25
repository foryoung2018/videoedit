package com.wmlive.hhvideo.heihei.record.presenter;

import android.text.TextUtils;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.opus.PublishResponseEntity;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 发布功能，本地导入
 */
public class PublishLocalPresenter extends BasePresenter <AbsPublishView> {
    /**
     * 本地导入的逻辑部分
     * 1.视频已经导出，
     * 2.获取截图，
     * 3.上传截图，视频
     * 4.发送请求。
     */
    public static final int ERROR_ADD_TASK = 110;
    public static final int ERROR_GET_COVER = 120;
    public static final int ERROR_UPLOAD_COVER = 130;
    public static final int ERROR_UPLOAD_VIDEO = 140;
    public static final int ERROR_REQUEST = 150;

    public static final int TYPE_COVER = 200;
    public static final int TYPE_VIDEO = 210;

    private static final String EXTRA_FORMAT_JPG = "jpg";
    private static final String EXTRA_FORMAT_MP4 = "mp4";
    private static final String MODULE_MATERIAL = "material";
    public static final String MODULE_OPUS = "opus";


    private final int STEP_GET_COVER = 0;
    private final int STEP_UPLOAD_COVER = 1;
    private final int STEP_UPLOAD_VIDEO = 2;
    private final int STEP_REQUEST = 3;

    private int newStep = STEP_GET_COVER;
    private UploadTask coverTask;
    private UploadTask videoTask;

    int allProgress;

    public PublishLocalPresenter(AbsPublishView view) {
        super(view);
    }

    public void retryPublish() {
        preparePublish(getProductEntity());
    }

    public void preparePublish(final ProductEntity entity) {
        if (entity != null) {
            entity.productType = ProductEntity.TYPE_PUBLISHING;
            RecordUtil.insertOrUpdateProductToDb(entity);
            KLog.i("=======当前已经进入到第：" + newStep + " 步");
            stepControl(newStep);
        } else {
            if (viewCallback != null) {
                viewCallback.onPublishFail(ERROR_ADD_TASK, "no product error");
            }
            KLog.i("=====没有作品");
        }
    }

    private void stepControl(int newStep) {
        if (getProductEntity() == null) {
            if (viewCallback != null) {
                viewCallback.onPublishFail(ERROR_ADD_TASK, "no product error");
            }
            return;
        }
        this.newStep = newStep;
        switch (newStep) {
            case STEP_GET_COVER://获取图片 ，0，5
                getVideoCover(getProductEntity());
                break;
            case STEP_UPLOAD_COVER://开始上传图片 5~10
                uploadCover(getProductEntity().coverPath);
                break;
            case STEP_UPLOAD_VIDEO://开始上传大视频 10~90
                uploadVideo();
                break;
            case STEP_REQUEST://发送请求，上传请求接口 90~100
                publishProduct();
                break;
        }

    }

    /**
     * 导出封面以及上传封面，总进度40%，当前需要进度10%
     *
     * @param productEntity
     */
    private void getVideoCover(final ProductEntity productEntity) {
        allProgress = 1;
        if (viewCallback != null) {
            viewCallback.onPublishing(TYPE_COVER, allProgress);
        }
        if (productEntity.hasVideo() || productEntity.isLocalUploadVideo()) {
            if (!TextUtils.isEmpty(productEntity.combineVideoAudio)
                    && new File(productEntity.combineVideoAudio).exists()) {
                final String coverPath = productEntity.baseDir + File.separator + RecordManager.PREFIX_COVER_FILE + RecordFileUtil.getTimestampString() + ".jpg";
                KLog.i("====开始生成视频封面");
                Disposable disposable = Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .map(new Function <Integer, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull Integer integer) throws Exception {
                                allProgress = 2;
                                if (viewCallback != null) {
                                    viewCallback.onPublishing(TYPE_COVER, allProgress);
                                }
                                PlayerEngine playerEngine = new PlayerEngine();
                                playerEngine.setSnapShotResource(productEntity.combineVideo);
                                return playerEngine.getSnapShot((long) GlobalParams.Config.VIDEO_COVER_CLIP_SECOND * 1000,
                                        coverPath);
                            }
                        })
                        .subscribe(new Consumer <Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean result) throws Exception {
                                KLog.i("====生成视频封面" + (result ? "成功" : "失败"));
                                KLog.i("====生成视频封面" + coverPath);
                                if (result) {
                                    allProgress = 4;
                                    if (viewCallback != null) {
                                        viewCallback.onPublishing(TYPE_COVER, allProgress);
                                    }
                                    productEntity.coverPath = coverPath;
                                    RecordUtil.insertOrUpdateProductToDb(productEntity);
                                    stepControl(STEP_UPLOAD_COVER);
                                } else {
                                    if (uploadTaskView != null) {
                                        uploadTaskView.onUploadFail(TYPE_COVER, "create video cover fail");
                                    }
                                }
                            }
                        }, new Consumer <Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                KLog.i("====生成视频封面出错");
                                throwable.printStackTrace();
                                if (uploadTaskView != null) {
                                    uploadTaskView.onUploadFail(TYPE_COVER, "create video cover fail");
                                }
                            }
                        });
            } else {
                KLog.i("====作品视频不存在");
                if (uploadTaskView != null) {
                    uploadTaskView.onUploadFail(TYPE_COVER, "combine video not exist");
                }
            }
        } else {
            KLog.i("====作品不存在");
            if (uploadTaskView != null) {
                uploadTaskView.onUploadFail(TYPE_COVER, "product not exist");
            }
        }
    }


    private void uploadCover(String coverPath) {
        allProgress = 5;
        if (viewCallback != null) {
            viewCallback.onPublishing(STEP_UPLOAD_COVER, allProgress);
        }
        if (coverTask == null) {
            coverTask = new UploadTask(uploadTaskView, TYPE_COVER, MODULE_MATERIAL, EXTRA_FORMAT_JPG, coverPath);
        }
        KLog.i("====开始上传视频封面");
        coverTask.startUpload();
    }

    private void uploadVideo() {
        allProgress = 10;
        if (viewCallback != null) {
            viewCallback.onPublishing(STEP_UPLOAD_VIDEO, allProgress);
        }
        if (videoTask == null) {
            videoTask = new UploadTask(uploadTaskView, TYPE_VIDEO, MODULE_OPUS, EXTRA_FORMAT_MP4, getProductEntity().combineVideoAudio);
        }
        KLog.i("combineVideoAudio====开始上传合成的视频" + getProductEntity().combineVideoAudio);
        videoTask.startUpload();
    }

    private ProductEntity getProductEntity() {
        return RecordManager.get().getProductEntity();
    }

    private void publishProduct() {
        allProgress = 90;
        if (viewCallback != null) {
            viewCallback.onPublishing(STEP_REQUEST, allProgress);
        }

        Map <String, String> map = new HashMap <>(12);
        String desc = null;
        long topicId = 0;
        if (getProductEntity().topicInfo != null) {
            desc = getProductEntity().topicInfo.topicDesc;
            topicId = getProductEntity().topicInfo.topicId;
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


        map.put("opus_length", String.valueOf((int) getProductEntity().getCombineVideoDuringMs()));
        //本地上传默认不能共同创作
        map.put("is_teamwork", "0");
        aVideoConfig aVideoConfig =VideoUtils.getMediaInfor(getProductEntity().combineVideoAudio);
//        int[] wh = RecordUtil.getVideoWH(getProductEntity().combineVideo);
        map.put("opus_width", aVideoConfig.getVideoWidth() + "");
        map.put("opus_height", aVideoConfig.getVideoHeight()+ "");
        KLog.i("opus--width-->"+aVideoConfig.getVideoWidth()+"height:>"+aVideoConfig.getVideoHeight());
//        String meta = getMaterialMeta();
//        if (!TextUtils.isEmpty(meta)) {
//            map.put("material_meta_data", meta);
//        }
//        String users = getAtUserIds(atUserIds);
//        if (!TextUtils.isEmpty(users)) {
//            map.put("at_user_ids", users);
//        }
        map.put("ori_opus_id", Long.toString(getProductEntity().originalId));
        KLog.i("====开始发布 map:" + CommonUtils.printMap(map));
        executeRequest("publishProduct",
                HttpConstant.TYPE_PUBLISH_PRODUCT,
                getHttpApi().publishProduct(InitCatchData.getUploadLocalOpus(), map))
                .subscribe(new DCNetObserver <PublishResponseEntity>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, PublishResponseEntity response) {
                        KLog.i("======发布作品成功");
                        if (viewCallback != null) {
                            allProgress = 100;
                            viewCallback.onPublishing(STEP_REQUEST, allProgress);

                            viewCallback.onPublishOk(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        KLog.i("======发布作品失败：" + serverCode + ",message:" + message);
                        if (viewCallback != null) {
                            newStep = STEP_REQUEST;
                            viewCallback.onPublishFail(ERROR_REQUEST, message);
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
                    case TYPE_VIDEO:
                        viewCallback.onPublishing(taskIndex, allProgress + RecordUtil.calculateProgress(currentSize, totalSize, 80));
                        break;
                    case TYPE_COVER://5~10
                        viewCallback.onPublishing(taskIndex, allProgress + RecordUtil.calculateProgress(currentSize, totalSize, 5));
                        break;
                }
            }
        }

        @Override
        public void onUploadOk(int taskIndex, UploadMaterialEntity entity) {
            KLog.i("====完成任务index：" + taskIndex);
            switch (taskIndex) {
                case TYPE_COVER:
                    KLog.i("=====视频封面上传成功");
                    stepControl(STEP_UPLOAD_VIDEO);
                    break;
                case TYPE_VIDEO://去发布
                    stepControl(STEP_REQUEST);
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
            switch (index) {
                case TYPE_COVER://图片上传出错，直接执行上传视频
                    stepControl(STEP_UPLOAD_VIDEO);
                    break;
                case TYPE_VIDEO:
                    KLog.i("====上传合成视频失败:" + message);
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(ERROR_UPLOAD_VIDEO, "upload combine video error");
                    }
                    break;
                default:
                    break;
            }
        }
    };


}
