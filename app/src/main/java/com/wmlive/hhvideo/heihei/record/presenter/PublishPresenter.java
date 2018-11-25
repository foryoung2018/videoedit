package com.wmlive.hhvideo.heihei.record.presenter;

import android.text.TextUtils;

import com.dongci.sun.gpuimglibrary.common.CutEntity;
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
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.content.ExportContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.listener.WebpJoinListener;
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

import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_EDITING_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_VIDEO_FILE;

//import com.rd.vecore.VirtualVideo;

/**
 * Created by lsq on 9/12/2017.
 */

public class PublishPresenter extends BasePresenter<AbsPublishView> {
    private final String TAG = "PublishPresenter";
    public static final String MODULE_MESSAGE = "message";
    public static final String MODULE_AVATAR = "avatar";
    public static final String MODULE_OPUS = "opus";
    public static final String MODULE_MATERIAL = "material";

    public static final String EXTRA_FORMAT_MP4 = "mp4";
    public static final String EXTRA_FORMAT_JPG = "jpg";
    public static final String EXTRA_FORMAT_WEBP = "webp";
    public static final String EXTRA_FORMAT_JPEG = "jpeg";

    public static final int ERROR_ADD_TASK = 110;
    public static final int ERROR_EXPORT_VIDEO = 120;
    public static final int ERROR_UPLOAD_MATERIAL = 130;
    public static final int ERROR_UPLOAD_COMBINE = 140;
    public static final int ERROR_UPLOAD_WEBP = 150;
    public static final int ERROR_UPLOAD_COVER = 160;
    public static final int ERROR_PUBLISH = 170;

    public static final int TYPE_EXPORT_VIDEO = 310;  //上传素材的任务index必须小于这个值
    public static final int TYPE_UPLOAD_MATERIAL = 320;
    public static final int TYPE_COVER = 330;
    public static final int TYPE_WEBP = 340;
    public static final int TYPE_UPLOAD_VIDEO = 350;
    public static final int TYPE_PUBLISH = 360;
    public static final int TYPE_MATERAL_COVER = 380;

    private List<UploadTask> taskList;
    private UploadTask coverTask;
    private UploadTask webpTask;
    private UploadTask videoTask;
    private ProductEntity productEntity;

    private int remainMaterialTaskCount;
    private long allMaterialLength;
    private int allProgress = 0;
    private int nextIndex = 0;
    private List<Integer> needExportVideos; // 需要导出的裁剪视频
    private int perMaterialTaskProgress;//每个素材上传的进度
    private float perExportVideoProgress = 0f;//每个需要裁剪导出的视频的进度
    private int currentClipIndex;
    //    private int currentStep = 0;//0导出，1上传封面，2
    private int newStep = 1;//新的每一步
    private boolean insertGallery;
    //    PublishActivity act;
    //逐一导出视频序列
    int currentIndex = 0;
    /**
     * 每个导出视频占用的进度
     */
    private float perMaterialProgress = 0f;

    private int transformIndex = 0;
    /**
     * 每个素材导出占比
     */
    private float perMaterialTransform = 0f;

    PublishResponseEntity publishResponseEntity;
    /**
     * 下一个起始百分比
     */
    private int nextStart;


    //进度比：导出视频20 (有裁剪视频，导出组合视频占10)，上传素材20,上传封面10，上传webp20，上传视频20，发布10
    public PublishPresenter(AbsPublishView view) {
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
    protected void stepControl(int newStep) {
        if (RecordManager.get().getProductEntity() == null) {
            if (viewCallback != null) {
                viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "no product error");
            }
            return;
        }
        this.newStep = newStep;
        switch (newStep) {
            case 1://裁剪
                clipVideo(0, 2);
                break;
            case 2://设置音量
                setVolume();
                break;
            case 3://音轨 2 -> 1  ,,确定保存的数据存在
                transformAudio2to1(2, 2);//几乎 不执行，
                break;
            case 4://导出 合并后的视频
                exportCombineVideo(RecordManager.get().getProductEntity(), nextStart, 15);
                break;
            case 5://合并音频，音频合并
                mixAudio();
                break;
            case 6:// 合并音视频
                muxVideoAudio();
                break;
            case 7://导出 素材视频
                exportMaterial(true, 15, 25);
                break;
            case 8://合并素材，的 video + audio
                muxMaterial();
                break;
            case 20://最后网络请求
                publishProduct(null);
                break;
            case 9://上传素材
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
//                    stepNext(currentStep);
                    stepControl(9);
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

            }

            @Override
            public void onExportEnd(int var1, String path) {
                if (var1 == SdkConstant.RESULT_SUCCESS) {
//                    stepNext(currentStep);
                    stepControl(7);
                }
            }
        });
    }

    /**
     * 混合音频
     */
    private void mixAudio() {
        RecordUtilSdk.mixAudios(RecordManager.get().getProductEntity(), new VideoListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                if (code == SdkConstant.RESULT_SUCCESS) {//混合成功
                    RecordManager.get().getProductEntity().combineAudio = outpath;
                    combineAudio = outpath;
                    RecordManager.get().updateProduct();
//                    stepNext(currentStep);
                    stepControl(6);
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    private void startPublish(ProductEntity entity, boolean insertGallery) {
        allProgress = 0;
        if (entity.hasLocalUploadVideo()) {
            //这里是本地单个上传，只需要上传转码或者复制后的视频，上传封面
            getVideoCover(entity);
        } else if (entity.hasVideo()) {
            //这里是录制上传
            List<ShortVideoEntity> shortVideoList = entity.shortVideoList;
            needExportVideos = new ArrayList<>(2);
            if (shortVideoList != null && shortVideoList.size() > 0) {
                for (int i = 0, size = shortVideoList.size(); i < size; i++) {
                    ShortVideoEntity videoEntity = shortVideoList.get(i);
                    if (videoEntity != null && !TextUtils.isEmpty(videoEntity.editingVideoPath) && videoEntity.isNeedExport()) {
                        needExportVideos.add(i);
                    }
                }
            }
            if (needExportVideos.size() > 0) {
                // 每个裁剪视频占用的百分比
                perExportVideoProgress = 10f / needExportVideos.size();
            }
//            stepNext(0);
            stepControl(1);
        } else {
            if (viewCallback != null) {
                viewCallback.onPublishFail(0, "no upload video");
            }
        }
    }

    private String combineVideo;
    private String combineAudio;


    /**
     * 音轨2 变 1
     * 可以不用
     */
    private void transformAudio2to1(int start, int end) {
//        perMaterialTransform = 5f / productEntity.shortVideoList.size() * 1f;
        productEntity = RecordManager.get().getProductEntity();
        for (ShortVideoEntity shortVideoEntity : productEntity.shortVideoList) {
            KLog.i(TAG, "transformAudio2to1" + shortVideoEntity.importVideoPath);
        }
        VideoEngine.transformAudio2to1(productEntity.shortVideoList, new VideoListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {
//                allProgress = RecordUtil.calculateProgress(progress, productEntity.shortVideoList.size(), perExportVideoProgress > 0 ? 15 : 20);

            }

            @Override
            public void onFinish(int code, String outpath) {//主线程 进行相应操作
                //更新进度界面
                productEntity = RecordManager.get().getProductEntity();
//                for (ShortVideoEntity shortVideoEntity : productEntity.shortVideoList) {
//                    KLog.i(TAG, "transformAudio2to1--finish" + shortVideoEntity.importVideoPath);
//                    KLog.i(TAG, "transformAudio2to1--finish-Length:>" + VideoUtils.getVideoLength(shortVideoEntity.importVideoPath));
//
//                }
//                stepNext(currentStep);
                stepControl(4);
            }

            @Override
            public void onError() {

            }
        });
    }


    /**
     * 导出的作品，如果有需要导出裁剪视频，总进度为10%，否则为20%，当前需要进度10%或者20%
     *
     * @param entity
     */
    private void exportCombineVideo(ProductEntity entity, int start, int end) {
        RecordFileUtil.cleanFilesByPrefix(entity.baseDir, RecordManager.PREFIX_COMBINE_FILE);
//        currentStep = 3;
        List<ShortVideoEntity> shortVideoList = entity.shortVideoList;
        int max = 100;
        float per = (end - start) * 1f / 100f;

        productEntity = RecordManager.get().getProductEntity();

        if (shortVideoList.size() == 0) {
            viewCallback.onPublishFail(0, "no upload video");
            return;
        }
        RecordUtilSdk.exportCombineVideo(initVideoContent(RecordManager.get().getProductEntity()), initVideoConfig(""), new ExportListener() {
            @Override
            public void onExportStart() {
                KLog.i(TAG, "exportCombineVideo--onExportStart--->");
                if (viewCallback != null) {
                    viewCallback.onPublishStart(TYPE_EXPORT_VIDEO);
                }
            }

            @Override
            public void onExporting(int var1, int var2) {
                allProgress = RecordUtil.calculateProgressNew(var1, start, per);
//                allProgress = RecordUtil.calculateProgress(var1, 100, perExportVideoProgress > 0 ? 15 : 20);
                KLog.i(TAG, var1 + "exportCombineVideo--orting--->" + allProgress);
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                }
            }

            @Override
            public void onExportEnd(int var1, String path) {
                KLog.i(TAG, "导出视频宽高", "=======导出后的视频：" + var1 + "path:" + path + VideoUtils.getVideoLength(path));
                if (var1 == SdkConstant.RESULT_SUCCESS) {
                    entity.combineVideo = path;
                    combineVideo = path;
//                    int[] wh = entity.getProductWH();
//                    KLog.i("导出视频宽高", "=======导出后的视频宽高：" + wh[0] + "x" + wh[1]);
                    RecordUtil.insertOrUpdateProductToDb(entity);
                    if (viewCallback != null) {
                        viewCallback.onPublishing(TYPE_UPLOAD_MATERIAL, allProgress);
                    }
                    if (VideoUtils.getVideoLength(combineVideo) < 500) {//视频 大小太小，视频导出失败
                        if (viewCallback != null) {
                            viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "export video error");
                        }
                    } else {
                        stepControl(5);
                    }
                } else {
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "export video error");
                    }
                    KLog.i(TAG, "=====视频导出失败");
                }
            }
        });
    }

    int localHeight = 960;
    int localWidth = 720;

    private int[] getTargetWidthNew(int videoWidth, int videoHeight) {
        float targetWidth = videoWidth;
        float targetHeight = videoHeight;
        float rate = videoHeight * 1.0f / videoWidth * 1.0f;
        if (videoHeight > localHeight) {
            if (videoWidth > localWidth) {
                targetHeight = localHeight;
                targetWidth = targetHeight * 1.0f / rate;
            } else if (videoWidth < localWidth) {
                targetWidth = localWidth;
                targetHeight = targetWidth * rate;
            } else {//
                targetHeight = localHeight;
                targetWidth = targetHeight * 1.0f / rate;
            }

        } else {
            if (videoWidth > localWidth) {
                targetWidth = localWidth;
                targetHeight = targetWidth * rate;
            } else if (videoWidth < localWidth) {//太小了不用变

            } else {

            }
        }
        if (((int) targetHeight) % 2 != 0) {
            targetHeight = targetHeight + 1;
        }
        if (((int) targetWidth) % 2 != 0) {
            targetWidth = targetWidth + 1;
        }

        return new int[]{(int) targetWidth, (int) targetHeight};
    }


    /**
     * 初始化
     * 导出视频信息
     */
    private MVideoConfig initVideoConfig(String baseDir) {
        //创建一个导出路径
        final String editingPath = RecordFileUtil.createExportFile();
        MVideoConfig videoConfig = new MVideoConfig();
        videoConfig.setVideoPath(editingPath);

        if (RecordManager.get().getProductEntity().frameInfo == null) {//左侧上传，需要配置宽高
            aVideoConfig video = VideoUtils.getMediaInfor(RecordManager.get().getShortVideoEntity(0).editingVideoPath);
//            videoConfig.setKeyFrameTime(video.getVideoFrameRate() == 0 ? 30 : video.getVideoFrameRate());//1s 一个关键帧
            int[] target2 = getTargetWidthNew(video.getVideoWidth(), video.getVideoHeight());
            KLog.i("video-width=>" + video.getVideoWidth() + "height:>" + video.getVideoHeight());
            KLog.i("video-width=2>" + target2[0] + "height:>" + target2[1]);
            videoConfig.setVideoSize(target2[0], target2[1]);
        } else {
//            videoConfig.setKeyFrameTime(24);//1s 一个关键帧
            videoConfig.setVideoSize(RecordManager.get().getProductEntity().frameInfo.opus_width, RecordManager.get().getProductEntity().frameInfo.opus_height);
        }
        return videoConfig;
    }

    /**
     * 播放器内容 Scence，，导出内容，
     * 是否需要build？
     * 需要内容 所有的视频,需要 PlayerExport
     */
    private List<Scene> initVideoContent(ProductEntity entity) {
        // currentIndex 只有在 combine 为false 才起作用
        List<MediaObject> assets = ExportContentFactory.getAllMedias(entity);
        for (int i = 0; i < assets.size(); i++) {
            KLog.i(TAG, i + "export--init-->" + assets.get(i).getFilePath());
        }
//        List<MediaObject> assets = PlayerContentFactory.createMediaObject(true, -1, shortVideoEntities.size(), mFrameInfo);
        List<Scene> scenes = PlayerContentFactory.createScenes(assets);
        return scenes;
    }

    private List<Scene> initWaterVideoContent(ProductEntity entity) {
        // currentIndex 只有在 combine 为false 才起作用
        List<MediaObject> assets = ExportContentFactory.getWaterMedias(entity);

//        List<MediaObject> assets = PlayerContentFactory.createMediaObject(true, -1, shortVideoEntities.size(), mFrameInfo);
        List<Scene> scenes = PlayerContentFactory.createScenes(assets);
        return scenes;
    }

    /**
     * 裁剪视频
     */
    private void clipVideo(int startPercentag, int endPercentag) {
        List<CutEntity> list = initClipData(RecordManager.get().getProductEntity());
        int max = list.size();
        int perEntity = 1;
        if (max == 0) {
            nextStart = 0;
            endPercentag = 0;
        } else {
            nextStart = 2;
            perEntity = (endPercentag - startPercentag) / max;
        }

        int finalPerEntity = perEntity;
        int finalEndPercentag = endPercentag;
        new VideoEngine().cutAudioVideos(list, new VideoListener() {
            @Override
            public void onStart() {
                allProgress = startPercentag;
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                }
            }

            @Override
            public void onProgress(int progress) {
                allProgress = RecordUtil.calculateProgressNew(progress, startPercentag, finalPerEntity);
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                }
            }

            @Override
            public void onFinish(int code, String outpath) {
                allProgress = finalEndPercentag;
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                }
                KLog.i(TAG, "clip--finish-->" + code);
//                stepNext(currentStep);
                stepControl(2);
            }

            @Override
            public void onError() {
                stepControl(2);
            }

        });
    }

    /**
     * 给当前修改音量的audio设置音量
     */
    private void setVolume() {
        if (RecordManager.get().getProductEntity() == null)
            return;
        List<CutEntity> list = initClipVolumeData(RecordManager.get().getProductEntity());
        new VideoEngine().setVolume(list, new VideoListener() {
            @Override
            public void onStart() {
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                }
            }

            @Override
            public void onProgress(int progress) {
//                allProgress = RecordUtil.calculateProgressNew(progress, startPercentag, finalPerEntity);
//                if (viewCallback != null) {
//                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
//                }
            }

            @Override
            public void onFinish(int code, String outpath) {
//                allProgress = finalEndPercentag;
                if (viewCallback != null) {
                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                }
                KLog.i(TAG, "clip--finish-->" + code);
//                stepNext(currentStep);
                stepControl(3);
            }

            @Override
            public void onError() {
//                stepNext(currentStep);
                stepControl(3);
            }

        });
    }

    private List<CutEntity> initClipData(ProductEntity productEntity) {
        List<CutEntity> list = new ArrayList<CutEntity>();
        if (productEntity == null)
            return list;
        if (productEntity.shortVideoList == null)
            return list;
        for (ShortVideoEntity entity : productEntity.shortVideoList) {
            if (entity.trimEnd == 0)
                continue;
            CutEntity cutEntity = new CutEntity();
            cutEntity.path = entity.editingVideoPath;
            cutEntity.audioPath = entity.editingAudioPath;
            cutEntity.start = (long) entity.trimStart * 1000;
            cutEntity.duration = (long) (entity.trimEnd - entity.trimStart) * 1000;
            cutEntity.volume = entity.volume;
            KLog.e(TAG, "initClip-->start>" + cutEntity.start + "trimEnd" + entity.trimEnd + "duration>" + (cutEntity.duration));
            list.add(cutEntity);
        }
        return list;
    }

    /**
     * 设置音量
     *
     * @param productEntity
     * @return
     */
    private List<CutEntity> initClipVolumeData(ProductEntity productEntity) {
        List<CutEntity> list = new ArrayList<CutEntity>();
        for (ShortVideoEntity entity : productEntity.shortVideoList) {
//            if (entity.trimEnd == 0)
//                continue;
            CutEntity cutEntity = new CutEntity();
            cutEntity.path = entity.editingVideoPath;
            cutEntity.audioPath = entity.editingAudioPath;
//            cutEntity.start = (long) entity.trimStart;
//            cutEntity.duration = (long) (entity.trimEnd - entity.trimStart);
            cutEntity.volume = entity.volume;
            String autido = RecordFileUtil.createAudioFile(entity.baseDir);
            cutEntity.cutAudioPath = autido;
//            KLog.i(TAG,"initClip-->start>" + cutEntity.start + "trimEnd" + entity.trimEnd);
//            KLog.i(TAG,"initClip-->start0>" + entity.getTrimRange()[0] + "trimEnd" + entity.getTrimRange()[1]);
            list.add(cutEntity);
        }
        return list;
    }

    /**
     * 导出裁剪视频，如果有需要导出裁剪视频，总进度为10%，否则为20%，当前需要进度10%
     */
    private void exportClipVideo(boolean isFirst) {
//        currentStep = 2;
        if (isFirst) {
            allProgress = perExportVideoProgress > 0 ? 10 : 20;
            currentClipIndex = 0;
        }
        if (needExportVideos.size() > 0 && currentClipIndex < needExportVideos.size()) {
            int currentExportIndex = needExportVideos.get(currentClipIndex);
            if (productEntity != null && !CollectionUtil.isEmpty(productEntity.shortVideoList)) {
                final ShortVideoEntity shortVideoEntity = productEntity.shortVideoList.get(currentExportIndex);
                if (shortVideoEntity != null && !TextUtils.isEmpty(shortVideoEntity.editingVideoPath)) {
                    MVideoConfig mVideoConfig = new MVideoConfig();
//                    mVideoConfig.setVideoPath(RecordFileUtil.createExportFile());
                    final String editVideoPath = RecordFileUtil.createTimestampFile(shortVideoEntity.baseDir,
                            PREFIX_EDITING_FILE, SUFFIX_VIDEO_FILE, true);
                    mVideoConfig.setVideoPath(editVideoPath);
                    //待修改
                    RecordUtilSdk.exportSingleVideo(shortVideoEntity, mVideoConfig, new ExportListener() {
                        @Override
                        public void onExportStart() {

                        }

                        @Override
                        public void onExporting(int var1, int var2) {
                            // 根据裁剪视频的位置算出进度
                            int hasExportProgress = (int) (perExportVideoProgress * currentClipIndex);
                            allProgress = 10 + hasExportProgress + RecordUtil.calculateProgress(var1, 100, perExportVideoProgress);
                            if (viewCallback != null) {
                                viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                            }
                        }

                        @Override
                        public void onExportEnd(int var1, String path) {
                            if (var1 == SdkConstant.RESULT_SUCCESS) {
                                KLog.i(TAG, "exportClipVideo=====onExportEnd--->" + path);
                                shortVideoEntity.editingVideoPath = path;
                                allProgress = 10 + (int) (perExportVideoProgress * (currentClipIndex + 1));
                                if (viewCallback != null) {
                                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                                }
                                currentClipIndex++;
                                exportClipVideo(false);
                            } else {
                                if (viewCallback != null) {
                                    viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "export video error");
                                }
                                KLog.i(TAG, "=====视频导出失败");
                            }
                        }
                    });
//                    RecordUtil.exportClipVideo(DCApplication.getDCApp(), new VirtualVideo(), shortVideoEntity, new VideoJoinListener() {
//                        @Override
//                        public void onJoinStart() {
//                        }
//
//                        @Override
//                        public void onJoining(int progress, int max) {
//                            // 根据裁剪视频的位置算出进度
//                            int hasExportProgress = (int) (perExportVideoProgress * currentClipIndex);
//                            allProgress = 10 + hasExportProgress + RecordUtil.calculateProgress(progress, 1000, perExportVideoProgress);
//                            if (viewCallback != null) {
//                                viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
//                            }
//                        }
//
//                        @Override
//                        public void onJoinEnd(boolean result, String message) {
//                            if (result) {
//                                KLog.i("=====视频导出成功");
//                                shortVideoEntity.editingVideoPath = message;
//                                allProgress = 10 + (int) (perExportVideoProgress * (currentClipIndex + 1));
//                                if (viewCallback != null) {
//                                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
//                                }
//                                currentClipIndex++;
//                                exportClipVideo(false);
//                            } else {
//                                if (viewCallback != null) {
//                                    viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "export video error");
//                                }
//                                KLog.i("=====视频导出失败");
//                            }
//                        }
//                    });
                }
            }
        } else {
            KLog.i(TAG, "=====视频导出成功");
            RecordUtil.insertOrUpdateProductToDb(productEntity);
            uploadMaterial(RecordManager.get().getProductEntity());
        }
    }

    int getMaterialCount() {
        int i = 0;
        for (ShortVideoEntity shortVideoEntity : productEntity.shortVideoList) {
            if (shortVideoEntity.editingVideoPath != null) {
                i++;
            }
        }
        return i;
    }

    float per = 0;

    /**
     * 导出视频的材料，
     */
    private void exportMaterial(boolean isFirst, int start, int end) {
        if (isFirst) {//
            currentIndex = 0;
            per = (end - start) * 1.0f / (100f);
            perMaterialProgress = 10f / getMaterialCount() * 1f;
            currentClipIndex = 0;

        } else {
            currentIndex++;
        }
        KLog.i(newStep + "素材视频导出--->" + currentIndex + "size::>" + productEntity.shortVideoList.size());
        if (currentIndex >= productEntity.shortVideoList.size()) {//下一步,上传素材
//            currentIndex = 0;
//            uploadMaterial(productEntity);
//            stepNext(currentStep);
            stepControl(8);
            return;
        }
        MVideoConfig mVideoConfig = new MVideoConfig();
        ShortVideoEntity shortVideoEntity = productEntity.shortVideoList.get(currentIndex);
        //如果是高画质，不需要原材料视频的导出，
        // 如果是低画质，则需要导出视频
//        if (shortVideoEntity.quality == FrameInfo.VIDEO_QUALITY_HIGH) {//直接进入下一步,,必须导出
////            uploadMaterial(productEntity);
//            stepNext(currentStep);
//            return;
//        }
        String resourcePath = shortVideoEntity.editingVideoPath == null ?
                shortVideoEntity.importVideoPath : shortVideoEntity.editingVideoPath;
        if (shortVideoEntity.baseDir == null || resourcePath == null) {//该项没有视频资源
            exportMaterial(false, 0, 0);
            return;
        }
        KLog.i(TAG, "exportMaterial--videoPath-->" + shortVideoEntity.baseDir);
        final String editVideoPath = RecordFileUtil.createVideoFile(shortVideoEntity.baseDir);
//        final String editVideoPath = RecordFileUtil.createTimestampFile(shortVideoEntity.baseDir,
//                PREFIX_EDITING_FILE, SUFFIX_VIDEO_FILE, true);
        mVideoConfig.setVideoPath(editVideoPath);
        mVideoConfig.setKeyFrameTime(24);
        //不需要裁剪,更改时间。
        shortVideoEntity.setTrimRange(0, VideoUtils.getVideoLength(shortVideoEntity.editingVideoPath));
        KLog.i(TAG, shortVideoEntity.baseDir + "exportMaterial-->" + editVideoPath);
        KLog.i(TAG, currentIndex + "开始导出素材-exportMaterial---before>" + shortVideoEntity.editingAudioPath);
        KLog.i(TAG, currentIndex + "开始导出素材-exportMaterial---before>" + shortVideoEntity.editingVideoPath);
        RecordUtilSdk.exportSingleVideo(shortVideoEntity, mVideoConfig, new ExportListener() {
            @Override
            public void onExportStart() {

            }

            @Override
            public void onExporting(int var1, int var2) {
                // 根据裁剪视频的位置算出进度
//                int hasExportProgress = (int) (perMaterialProgress * currentClipIndex);//currentClipIndex
//
////                allProgress =  hasExportProgress + RecordUtil.calculateProgressNew(var1, start, per); //RecordUtil.calculateProgress(var1, 100, perMaterialProgress);
//
//                int hasExportProgress = (int) (perMaterialProgress * currentClipIndex);
//                allProgress = hasExportProgress + RecordUtil.calculateProgressNew(var1, start, per); //RecordUtil.calculateProgress(var1, 100, perMaterialProgress);
//                if (var1 % 10 == 0)
//                    KLog.i(TAG, hasExportProgress + "allProgress--素材视频导出->" + var1 + "AllProgress>>" + allProgress);
//                if (viewCallback != null) {
//                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
//                }

//                if (viewCallback != null) {
//                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
//                }
            }

            @Override
            public void onExportEnd(int var1, String path) {
                if (var1 == SdkConstant.RESULT_SUCCESS) {
                    if (currentIndex >= productEntity.shortVideoList.size()) {//下一步,上传素材
//                        currentIndex = 0;
//                        stepNext(currentStep);
                        stepControl(8);
                        return;
                    }
                    productEntity.shortVideoList.get(currentIndex).editingVideoPath = path;

                    KLog.i(TAG, "素材视频导出---path>" + path);
                    currentClipIndex++;
                    allProgress = 15 + (int) (perMaterialProgress * (currentClipIndex));
                    KLog.i(TAG, "allProgress--->" + allProgress);

                    if (viewCallback != null) {
                        viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
                    }

                    // TODO: 2018/11/12
//                    final String coverPath = productEntity.shortVideoList.get(currentIndex).baseDir + File.separator + RecordManager.PREFIX_COVER_FILE + RecordFileUtil.getTimestampString() + ".jpg";
//                    boolean result = RecordFileUtil.getVideoCover(path,
//                            coverPath,
//                            GlobalParams.Config.VIDEO_COVER_CLIP_SECOND,
//                            RecordSetting.VIDEO_WIDTH,
//                            RecordSetting.VIDEO_HEIGHT
//                    );
//                    if (result) {
//                        uploadMaterialCover(coverPath);
//                    }

                    KLog.i(TAG, "exportMaterial=====onExportEnd--->" + productEntity.shortVideoList.get(currentIndex).editingVideoPath);
                    exportMaterial(false, 0, 0);
                } else {
                    if (viewCallback != null) {
                        viewCallback.onPublishFail(ERROR_EXPORT_VIDEO, "export video error");
                    }
                    KLog.i(TAG, "=====视频导出失败");
                }
            }
        });
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
        if (productEntity != null && productEntity.hasVideo()) {
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
                    if (productEntity.musicInfo != null) {
                        task.musicId = productEntity.musicInfo.musicId;
                    }
                    task.videoLength = videoEntity.getEditingDuringMs();
                    task.originalId = videoEntity.originalId;
                    task.quality = videoEntity.quality == FrameInfo.VIDEO_QUALITY_HIGH ? "high" : "low";
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
        if (productEntity.hasVideo() || productEntity.isLocalUploadVideo()) {
            if (!TextUtils.isEmpty(productEntity.combineVideoAudio)
                    && new File(productEntity.combineVideoAudio).exists()) {
                final String coverPath = productEntity.baseDir + File.separator + RecordManager.PREFIX_COVER_FILE + RecordFileUtil.getTimestampString() + ".jpg";
                KLog.i(TAG, "====开始生成视频封面");
                Disposable disposable = Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<Integer, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull Integer integer) throws Exception {
                                PlayerEngine playerEngine = new PlayerEngine();
                                playerEngine.setSnapShotResource(productEntity.combineVideo);
                                return playerEngine.getSnapShot((long) GlobalParams.Config.VIDEO_COVER_CLIP_SECOND * 1000,
                                        coverPath);
                            }
                        })
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean result) throws Exception {
                                KLog.i("封面", "====生成视频封面" + (result ? "成功" : "失败"));
                                KLog.i("封面", "====生成视频封面" + coverPath);
                                if (result) {
                                    productEntity.coverPath = coverPath;
                                    RecordUtil.insertOrUpdateProductToDb(productEntity);
                                    uploadCover(coverPath);
                                } else {
                                    if (uploadTaskView != null) {
                                        uploadTaskView.onUploadFail(TYPE_COVER, "create video cover fail");
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                KLog.i(TAG, "====生成视频封面出错");
                                throwable.printStackTrace();
                                if (uploadTaskView != null) {
                                    uploadTaskView.onUploadFail(TYPE_COVER, "create video cover fail");
                                }
                            }
                        });
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

    /**
     * 上传webp,总进度50%，当前需要进度20%
     *
     * @param productEntity
     */
    private void getVideoWebp(final ProductEntity productEntity) {
        allProgress = 50;
        if (productEntity != null && productEntity.hasVideo()) {
            if (!TextUtils.isEmpty(productEntity.combineVideo)
                    && new File(productEntity.combineVideo).exists()) {
                final String webpPath = productEntity.baseDir + File.separator + RecordManager.PREFIX_WEBP_FILE + RecordFileUtil.getTimestampString() + ".webp";
                KLog.i(TAG, "====开始生成视频webp");
                RecordFileUtil.createWebp(productEntity.combineVideo, webpPath, new WebpJoinListener() {
                    @Override
                    public void onStartJoin() {
                        KLog.i(TAG, "===开始生成webp");
                    }

                    @Override
                    public void onJoining(float progress) {
                        KLog.i(TAG, "===正在生成webp progress:" + progress);
                    }

                    @Override
                    public void onJoinEnd(boolean result, String path) {
                        KLog.i(TAG, "====生成webp" + (result ? "成功" : "失败"));
                        if (result) {
                            productEntity.webpPath = webpPath;
                            RecordUtil.insertOrUpdateProductToDb(productEntity);
                            uploadWebp(webpPath);
                        } else {
                            if (uploadTaskView != null) {
                                uploadTaskView.onUploadFail(TYPE_WEBP, "create video webp fail");
                            }
                        }
                    }
                });
            } else {
                KLog.i(TAG, "====作品视频不存在");
                if (uploadTaskView != null) {
                    uploadTaskView.onUploadFail(TYPE_WEBP, "combine video not exist");
                }
            }
        } else {
            KLog.i(TAG, "====作品不存在");
            if (uploadTaskView != null) {
                uploadTaskView.onUploadFail(TYPE_WEBP, "product not exist");
            }
        }
    }

    /**
     * 上传大视频封面
     *
     * @param coverPath
     */
    private void uploadCover(String coverPath) {
        if (coverTask == null) {
            coverTask = new UploadTask(uploadTaskView, TYPE_COVER, MODULE_MATERIAL, EXTRA_FORMAT_JPG, coverPath);
        }
        KLog.i("封面", "====开始上传视频封面coverPath==" + coverPath);
        coverTask.startUpload();
    }


    private void uploadMaterialCover(String coverPath) {
        if (coverTask == null) {
            coverTask = new UploadTask(uploadTaskView, TYPE_MATERAL_COVER, MODULE_MATERIAL, EXTRA_FORMAT_JPG, coverPath);
        }
        KLog.i("封面", "====开始上传视频封面coverPath==" + coverPath);
        coverTask.startUpload();
    }

    private void uploadWebp(String webpPath) {
        if (webpTask == null) {
            webpTask = new UploadTask(uploadTaskView, TYPE_WEBP, MODULE_MATERIAL, EXTRA_FORMAT_WEBP, webpPath);
        }
        KLog.i(TAG, "====开始上传视频webp");
        webpTask.startUpload();
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

//        float heightPercent = 1f - RecordSetting.WATERMARK_HEIGHT * 1.0f / RecordSetting.PRODUCT_HEIGHT;
        // sdk rect !!
//        RecordManager.get().getSetting().setWatermarkShowRectF(new RectF(0, 1f, 1f, 1f));

        RecordUtilSdk.exportWaterMarkVideo(initWaterVideoContent(entity), initVideoConfig(entity.baseDir), new ExportListener() {
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
//                        viewCallback.onPublishOk(publishResponseEntity);
                    }
                } else {
//                    if (viewCallback != null)
//                    viewCallback.onPublishFail(ERROR_ADD_TASK, "");
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
                    KLog.i("封面", "=====视频封面上传成功");
//                    if (GlobalParams.StaticVariable.sSupportWebp && !productEntity.isLocalUploadVideo()) {
//                        getVideoWebp(productEntity);
//                    } else {
//                        KLog.i("====不支持生成webp");
//                    }
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
//        if (webpTask != null && !TextUtils.isEmpty(webpTask.getOssPath()) && !TextUtils.isEmpty(webpTask.getOssSign())) {
//            map.put("opus_gif_cover", webpTask.getOssPath());
//            map.put("opus_gif_cover_sign", webpTask.getOssSign());
//        }
        if (productEntity.musicInfo != null) {
            map.put("music_id", Long.toString(productEntity.musicInfo.musicId));
            map.put("music_time", (int) productEntity.musicInfo.trimStart + ","
                    + (int) productEntity.musicInfo.trimEnd);
        }
        if (productEntity.frameInfo != null) {
            map.put("opus_layout", productEntity.frameInfo.name);
        }
        map.put("opus_length", String.valueOf((int) productEntity.getCombineVideoDuringMs()));
        //本地上传默认不能共同创作
        ProductExtendEntity extendInfo = productEntity.getExtendInfo();
        map.put("is_teamwork", productEntity.isLocalUploadVideo() ?
                "0" : (extendInfo.allowTeam ? "1" : "0"));
        if (extendInfo.isLocalUploadVideo) {
            int[] wh = RecordUtil.getVideoWH(productEntity.combineVideo);
            map.put("opus_width", wh[0] + "");
            map.put("opus_height", wh[1] + "");
        } else {
            map.put("opus_width", String.valueOf(productEntity.frameInfo.opus_width));
            map.put("opus_height", String.valueOf(productEntity.frameInfo.opus_height));
        }
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
                getHttpApi().publishProduct(productEntity.isLocalUploadVideo() ? InitCatchData.getUploadLocalOpus() : InitCatchData.getPublishProduct(), map))
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
