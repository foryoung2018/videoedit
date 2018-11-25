package com.wmlive.hhvideo.heihei.record.utils;

import android.content.Context;
import android.graphics.RectF;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.text.TextUtils;


//import com.rd.vecore.Music;
//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.exception.InvalidArgumentException;
//import com.rd.vecore.listener.ExportListener;
//import com.rd.vecore.models.AspectRatioFitMode;
//import com.rd.vecore.models.EffectType;
//import com.rd.vecore.models.MediaObject;
//import com.rd.vecore.models.PermutationMode;
//import com.rd.vecore.models.Scene;
//import com.rd.vecore.models.VideoConfig;
//import com.rd.vecore.models.Watermark;
//import com.rd.vecore.utils.ExportUtils;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.manager.greendao.GreenDaoManager;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.ClipVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.EffectEntity;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntityDao;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.listener.VideoJoinListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.manager.RecordSpeed;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.SdkUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_COMBINE_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_EDITING_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_VIDEO_FILE;

/**
 * Created by lsq on 9/14/2017.
 * 视频处理的工具类
 */

public class RecordUtil {


//    public static boolean loadSingleVideo(VirtualVideo virtualVideo, ShortVideoEntity videoEntity) throws InvalidArgumentException {
//        return loadSingleVideo(virtualVideo, videoEntity, false);
//    }

    /**
     * 加载单个视频，加上特效、滤镜、音乐
     * 注意：务必记得在结束的时候调用virtualVideo.release();
     *
     //     * @param videoEntity
     */
//    public static boolean loadSingleVideo(VirtualVideo virtualVideo, ShortVideoEntity videoEntity, boolean hasMusic) throws InvalidArgumentException {
//        if (videoEntity != null
//                && !TextUtils.isEmpty(videoEntity.editingVideoPath)
//                && new File(videoEntity.editingVideoPath).exists()) {
//            boolean hasReverse = false;
    //反转 视频前后反转
//            for (EffectEntity effectEntity : videoEntity.getEffectList()) {
//                if (effectEntity != null) {
//                    if (effectEntity.effectType == EffectType.REVERSE
//                            && !TextUtils.isEmpty(videoEntity.editingReverseVideoPath)
//                            && new File(videoEntity.editingReverseVideoPath).exists()) {
//                        KLog.i("=====添加特效REVERSE");
//                        hasReverse = true;
//                        Scene scene = VirtualVideo.createScene();
//                        if (effectEntity.getStartTime() > 0) {
//                            MediaObject mo = scene.addMedia(videoEntity.editingVideoPath);
//                            mo.setTimeRange(0, effectEntity.getStartTime());
//                            mo.setMixFactor(0);
//                            virtualVideo.addScene(scene);
//                        }
//                        if (effectEntity.getEndTime() > effectEntity.getStartTime()) {
//                            scene = VirtualVideo.createScene();
//                            MediaObject mo = scene.addMedia(videoEntity.editingReverseVideoPath);
//                            mo.setTimeRange(effectEntity.getStartTime(), effectEntity.getEndTime());
//                            mo.setAudioMute(true);
//                            mo.setMixFactor(videoEntity.getOriginalMixFactor());
//                            virtualVideo.addScene(scene);
//                        }
//                        if (effectEntity.getEndTime() < videoEntity.getDuring()) {
//                            scene = VirtualVideo.createScene();
//                            MediaObject mo = scene.addMedia(videoEntity.editingVideoPath);
//                            mo.setTimeRange(effectEntity.getEndTime(), videoEntity.getDuring());
//                            mo.setMixFactor(0);
//                            virtualVideo.addScene(scene);
//                        }
//                        break;
//
//                    }
//                }
//            }
    //如果没有进行反转
//            if (!hasReverse) {
//                Scene scene = VirtualVideo.createScene();
//                MediaObject mo = scene.addMedia(videoEntity.editingVideoPath);
//                mo.setTimeRange(0, 0);
//                mo.setAudioMute(true);
//                mo.setMixFactor(videoEntity.getOriginalMixFactor());
//                virtualVideo.addScene(scene);
//            }
//
//            if (videoEntity.isUseOriginalAudio()) {
//                float startTime = 0;
//                if (videoEntity.getClipList().size() > 0) {
//                    for (ClipVideoEntity clipVideoEntity : videoEntity.getClipList()) {
//                        if (!TextUtils.isEmpty(clipVideoEntity.audioPath)
//                                && new File(clipVideoEntity.audioPath).exists()) {
//                            KLog.i("=====添加原音:" + clipVideoEntity.audioPath);
//                            virtualVideo.addMusic(
//                                    clipVideoEntity.audioPath,
//                                    startTime,
//                                    startTime + clipVideoEntity.getDuring(),
//                                    videoEntity.getOriginalMixFactor(),
//                                    (float) RecordSpeed.getSpeed(clipVideoEntity.speedIndex),
//                                    true);
//                            startTime += clipVideoEntity.getDuring();
//                        }
//                    }
//                } else {
//                    // 有音轨的视频将音频分离加载，使音频大小可调节
//                    Music originalVoice = null;
//                    try {
//                        originalVoice = VirtualVideo.createMusic(videoEntity.editingVideoPath);
//                        originalVoice.setTimeRange(0, videoEntity.getDuring());
//                        originalVoice.setTimelineRange(0, videoEntity.getDuring());
//                        virtualVideo.addMusic(originalVoice, true);
//                    } catch (InvalidArgumentException e) {
//                        e.printStackTrace();
//                    }
//                    if (originalVoice != null) {
//                        originalVoice.setMixFactor(videoEntity.getOriginalMixFactor());
//                    }
//                }
//            }
//            virtualVideo.setOriginalMixFactor(videoEntity.getOriginalMixFactor());
//
//            if (hasMusic) {
//                //添加配乐
//                MusicInfoEntity musicInfo = RecordManager.get().getProductEntity().musicInfo;
//                if (!TextUtils.isEmpty(musicInfo.getMusicPath())) {
//                    virtualVideo.addMusic(musicInfo.getMusicPath(),
//                            musicInfo.trimStart, musicInfo.trimEnd,
//                            RecordSetting.RECORD_MUSIC_DELAY, musicInfo.trimEnd - musicInfo.trimStart + RecordSetting.RECORD_MUSIC_DELAY,
//                            RecordManager.get().getProductEntity().musicMixFactor,
//                            (float) RecordSpeed.NORMAL.value(), false);
//                }
//            }
//
//            for (EffectEntity effectEntity : videoEntity.getEffectList()) {
//                if (effectEntity != null) {
//                    KLog.i("=====添加特效" + effectEntity.effectType);
//                    if (effectEntity.effectType == EffectType.REPEAT
//                            && !TextUtils.isEmpty(videoEntity.editingReverseVideoPath)
//                            && new File(videoEntity.editingReverseVideoPath).exists()) {
//                        virtualVideo.addEffect(effectEntity.effectType, effectEntity.getStartTime(), effectEntity.getEndTime(), videoEntity.editingReverseVideoPath);
//                    } else {
//                        virtualVideo.addEffect(effectEntity.effectType, effectEntity.getStartTime(), effectEntity.getEndTime());
//                    }
//                }
//            }
//            virtualVideo.changeFilter(videoEntity.getFilterId());
//            KLog.i("=====添加滤镜：" + videoEntity.getFilterId());
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * 多次录制的片段合成单个视频
     *
     * @param entity
     * @param listener
     */
    public static void joinAndReverse(final Context context, final ShortVideoEntity entity, final VideoJoinListener listener) {
//        joinAndReverse(context, entity, false, listener);
        joinAndReverse2(context, entity, false, listener);
    }

    /**
     * 多次录制的片段合成单个视频
     *
     * @param entity
     * @param isReverse 是否倒序
     * @param listener
     */
    public static void joinAndReverse(final Context context, final ShortVideoEntity entity, final boolean isReverse, final VideoJoinListener listener) {
        joinAndReverse2(context, entity, isReverse, listener);
//        if (entity != null && entity.hasClipVideo()) {
////            RecordFileUtil.cleanFilesByPrefix(entity.baseDir, PREFIX_EDITING_FILE);
//            final String editingPath = RecordFileUtil.createTimestampFile(entity.baseDir,
//                    PREFIX_EDITING_FILE, SUFFIX_VIDEO_FILE, true);
//            ArrayList<MediaObject> mediaObjects = new ArrayList<>();
//            for (ClipVideoEntity clipVideoEntity : entity.getClipList()) {
//                if (clipVideoEntity != null && clipVideoEntity.hasVideo()) {
//                    try {
//                        mediaObjects.add(new MediaObject(clipVideoEntity.videoPath));
//                    } catch (InvalidArgumentException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    KLog.i("====clipVideoEntity 为空或者文件不存在");
//                }
//            }
//
//            ExportUtils.fastSave(context.getApplicationContext(), mediaObjects, editingPath, new ExportListener() {
//                @Override
//                public void onExportStart() {
//                    KLog.i("======onExportStart");
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    KLog.i("======onExporting progress:" + progress);
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    KLog.i("======onExportEnd");
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        entity.editingVideoPath = editingPath;
//                        if (isReverse) {
//                            boolean fastReverse = true;
//                            for (ClipVideoEntity clipVideoEntity : entity.getClipList()) {
//                                if (!clipVideoEntity.supportFastReverse) {
//                                    fastReverse = false;
//                                    break;
//                                }
//                            }
////                            RecordFileUtil.cleanFilesByPrefix(entity.baseDir, PREFIX_REVERSE_FILE);
//                            final String reversePath = RecordFileUtil.createTimestampFile(entity.baseDir,
//                                    PREFIX_REVERSE_FILE, SUFFIX_VIDEO_FILE, true);
//                            entity.editingReverseVideoPath = reversePath;
//                            KLog.i("=======supportFastReverse:" + fastReverse);
//                            reverseVideo(context.getApplicationContext(), entity.editingVideoPath, reversePath, fastReverse, listener);
//                        } else {
//                            if (listener != null) {
//                                listener.onJoinEnd(true, null);
//                            }
//                        }
//                    } else {
//                        FileUtils.deleteAll(editingPath);
//                        if (listener != null) {
//                            listener.onJoinEnd(false, null);
//                        }
//                    }
//                }
//            });
////            } else {//单个片段
////                ClipVideoEntity clip = entity.getClipList().get(0);
////                entity.editingVideoPath = clip.videoPath;
////                if (isReverse) {
////                    RecordFileUtil.cleanFilesByPrefix(entity.baseDir, PREFIX_REVERSE_FILE);
////                    final String reversePath = RecordFileUtil.createTimestampFile(entity.baseDir,
////                            PREFIX_REVERSE_FILE, SUFFIX_VIDEO_FILE, true);
////                    entity.editingReverseVideoPath = reversePath;
////                    KLog.i("=======supportFastReverse:" + clip.supportFastReverse);
////                    reverseVideo(context.getApplicationContext(), entity.editingVideoPath, reversePath, clip.supportFastReverse, listener);
////                } else {
////                    if (listener != null) {
////                        listener.onJoinEnd(true, null);
////                    }
////                }
////            }
//        } else {
//            if (listener != null) {
//                KLog.i("====没有视频");
//                listener.onJoinEnd(true, null);
//            }
//        }
    }

    /**
     * 这个转换慢
     *
     * @param context
     * @param entity
     * @param isReverse
     * @param listener
     */
    public static void joinAndReverse2(final Context context, final ShortVideoEntity entity, final boolean isReverse, final VideoJoinListener listener) {
//        if (entity != null && entity.hasClipVideo()) {
////            RecordFileUtil.cleanFilesByPrefix(entity.baseDir, PREFIX_EDITING_FILE);
//            VirtualVideo exportVideo = new VirtualVideo();
//            VideoConfig videoConfig = new VideoConfig();
//            videoConfig.enableHWDecoder(CoreUtils.hasJELLY_BEAN_MR2());
//            videoConfig.enableHWDecoder(CoreUtils.hasJELLY_BEAN_MR2());
////            videoConfig.setVideoSize(RecordSetting.VIDEO_DATA_WIDTH, RecordSetting.VIDEO_DATA_HEIGHT);
//            videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoPublishBitrate);
//            videoConfig.setAspectRatio(RecordManager.get().getSetting().getVideoRatio());
//            videoConfig.setOptimizeForNet(true);
//            videoConfig.setKeyFrameTime(10);
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            final String editingPath = RecordFileUtil.createTimestampFile(entity.baseDir,
//                    PREFIX_EDITING_FILE, SUFFIX_VIDEO_FILE, true);
//            Scene scene = VirtualVideo.createScene();
//            for (ClipVideoEntity clipVideoEntity : entity.getClipList()) {
//                if (clipVideoEntity != null && clipVideoEntity.hasVideo()) {
//                    try {
//                        if (clipVideoEntity.videoWidth > 0 && clipVideoEntity.videoHeight > 0) {
//                            videoConfig.setVideoSize(clipVideoEntity.videoWidth, clipVideoEntity.videoHeight);
//                        }
//                        scene.addMedia(clipVideoEntity.videoPath);
//                    } catch (InvalidArgumentException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    KLog.i("====clipVideoEntity 为空或者文件不存在");
//                }
//            }
//            exportVideo.addScene(scene);
////            ExportUtils.fastSave(context.getApplicationContext(), mediaObjects, editingPath, new ExportListener() {
//            exportVideo.export(context.getApplicationContext(), editingPath, videoConfig, new ExportListener() {
//                @Override
//                public void onExportStart() {
//                    KLog.i("======onExportStart");
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    KLog.i("======onExporting progress:" + progress);
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    KLog.i("======onExportEnd");
//                    exportVideo.release();
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        entity.editingVideoPath = editingPath;
////                        if (isReverse) {//没有时间特效，不需要这个反转
////                            boolean fastReverse = true;
////                            for (ClipVideoEntity clipVideoEntity : entity.getClipList()) {
////                                if (!clipVideoEntity.supportFastReverse) {
////                                    fastReverse = false;
////                                    break;
////                                }
////                            }
//////                            RecordFileUtil.cleanFilesByPrefix(entity.baseDir, PREFIX_REVERSE_FILE);
////                            final String reversePath = RecordFileUtil.createTimestampFile(entity.baseDir,
////                                    PREFIX_REVERSE_FILE, SUFFIX_VIDEO_FILE, true);
////                            entity.editingReverseVideoPath = reversePath;
////                            KLog.i("=======supportFastReverse:" + fastReverse);
////                            reverseVideo(context.getApplicationContext(), entity.editingVideoPath, reversePath, fastReverse, listener);
////                        } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(true, null);
//                        }
////                        }
//                    } else {
//                        FileUtils.deleteAll(editingPath);
//                        if (listener != null) {
//                            listener.onJoinEnd(false, null);
//                        }
//                    }
//                }
//            });
////            } else {//单个片段
////                ClipVideoEntity clip = entity.getClipList().get(0);
////                entity.editingVideoPath = clip.videoPath;
////                if (isReverse) {
////                    RecordFileUtil.cleanFilesByPrefix(entity.baseDir, PREFIX_REVERSE_FILE);
////                    final String reversePath = RecordFileUtil.createTimestampFile(entity.baseDir,
////                            PREFIX_REVERSE_FILE, SUFFIX_VIDEO_FILE, true);
////                    entity.editingReverseVideoPath = reversePath;
////                    KLog.i("=======supportFastReverse:" + clip.supportFastReverse);
////                    reverseVideo(context.getApplicationContext(), entity.editingVideoPath, reversePath, clip.supportFastReverse, listener);
////                } else {
////                    if (listener != null) {
////                        listener.onJoinEnd(true, null);
////                    }
////                }
////            }
//        } else {
//            if (listener != null) {
//                KLog.i("====没有视频");
//                listener.onJoinEnd(true, null);
//            }
//        }
    }


    /**
     * 视频倒序
     *
     * @param videoPath 视频路径
     * @param isFast
     */
    public static void reverseVideo(Context context, String videoPath, final String reversePath, boolean isFast, final VideoJoinListener listener) {
//        VideoConfig reverseConfig = new VideoConfig();
//        VirtualVideo.getMediaInfo(videoPath, reverseConfig);
//        reverseConfig.setVideoSize(reverseConfig.getVideoWidth(), reverseConfig.getVideoHeight());
//        reverseConfig.enableHWDecoder(false);
//        reverseConfig.enableHWEncoder(true);
//        reverseConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//        reverseConfig.setAspectRatio(0);
//        ExportListener exportListener = new ExportListener() {
//            @Override
//            public void onExportStart() {
//                KLog.i("======onExportStart");
//                if (listener != null) {
//                    listener.onJoinStart();
//                }
//            }
//
//            @Override
//            public boolean onExporting(int progress, int max) {
//                KLog.i("======onExporting progress:" + progress);
//                if (listener != null) {
//                    listener.onJoining(progress, max);
//                }
//                return true;
//            }
//
//            @Override
//            public void onExportEnd(int result) {
//                KLog.i("======onExportEnd");
//                if (result >= VirtualVideo.RESULT_SUCCESS) {
//                    if (listener != null) {
//                        listener.onJoinEnd(true, reversePath);
//                    }
//                } else {
//                    FileUtils.deleteAll(reversePath);
//                    if (listener != null) {
//                        listener.onJoinEnd(false, null);
//                    }
//                }
//            }
//        };
//        if (isFast) {
//            ExportUtils.fastReverseSave(context.getApplicationContext(), videoPath, reversePath, exportListener);
//        } else {
//            ExportUtils.reverseSave(context.getApplicationContext(), videoPath, reversePath, reverseConfig, exportListener);
//        }
    }

    //    public static boolean previewCombineVideo(VirtualVideo exportVideo, final ProductEntity productEntity, boolean trimVideo, boolean usePublishImage) {
//        if (productEntity != null && productEntity.shortVideoList != null) {
//            int videoCount = productEntity.shortVideoList.size();
//            Scene scene = VirtualVideo.createScene();
//            FrameInfo mFrameInfo = productEntity.frameInfo;
//            //背景图
////            if (usePublishImage) {
//                if (mFrameInfo != null && !TextUtils.isEmpty(mFrameInfo.publish_image)) {
////                    String frameImage = RecordFileUtil.getFrameImagePath(mFrameInfo.publish_image);
////                    KLog.e("frameImage publish_image:" + frameImage);
////                    if (frameImage != null && new File(frameImage).exists()) {
////                        MediaObject mediabg = null;
////                        try {
////                            mediabg = new MediaObject(DCApplication.getDCApp(), frameImage);
////                            mediabg.setShowRectF(new RectF(0, 0, 1, 1));//显示区域
////                            scene.addMedia(mediabg);
////                            KLog.i("====视频预览背景图:" + frameImage);
////                        } catch (InvalidArgumentException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                }
////            } else {
////                if (mFrameInfo != null && !TextUtils.isEmpty(mFrameInfo.sep_image)) {
////                    String frameImage = RecordFileUtil.getFrameImagePath(mFrameInfo.sep_image);
////                    KLog.e("frameImage publish_image:" + frameImage);
////                    if (frameImage != null && new File(frameImage).exists()) {
////                        MediaObject mediabg = null;
////                        try {
////                            mediabg = new MediaObject(DCApplication.getDCApp(), frameImage);
////                            mediabg.setShowRectF(new RectF(0, 0, 1, 1));//显示区域
////                            scene.addMedia(mediabg);
////                            KLog.i("====视频预览背景图:" + frameImage);
////                        } catch (InvalidArgumentException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                }
////            }
//            //
//            ShortVideoEntity videoEntity;
//            int count = 0;
//            for (int i = 0; i < videoCount; i++) {
//                videoEntity = productEntity.shortVideoList.get(i);
//                if (videoEntity != null
//                        && !TextUtils.isEmpty(videoEntity.editingVideoPath)
//                        && new File(videoEntity.editingVideoPath).exists()) {
//                    MediaObject media = null;
//                    try {
//                        media = new MediaObject(videoEntity.editingVideoPath);
//                    } catch (InvalidArgumentException e) {
//                        e.printStackTrace();
//                    }
//                    if (media != null) {
//                        RectF rectF = productEntity.frameInfo.getLayoutRelativeRectF(i);
//                        KLog.i("=======显示的区域1是：" + rectF);
//                        media.setShowRectF(rectF);
//                        media.setAspectRatioFitMode(AspectRatioFitMode.KEEP_ASPECTRATIO_EXPANDING);
//                        media.setAudioMute(true);
////                        media.setMixFactor(videoEntity.getOriginalMixFactor());
//                        float[] trimRange = videoEntity.getTrimRange();
//                        float trimDuration = videoEntity.getDuring();
//                        float voiceStartTime = 0;
//                        if (trimVideo && (trimRange[0] != 0 || trimRange[1] != 0)) {
//                            media.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
//                            voiceStartTime = trimRange[0];
//                            trimDuration = trimRange[1] - trimRange[0]; // 截取时长
//                        }
//                        RectF clipRect = RecordFileUtil.getClipSrc(media.getWidth(), media.getHeight(), productEntity.frameInfo.getLayoutAspectRatio(i), 0f);
//                        KLog.i("=======显示的区域2是：" + clipRect + " ,ratio:" + productEntity.frameInfo.getLayoutAspectRatio(i));
//                        media.setClipRectF(clipRect);
//                        // 添加原声
//                        videoEntity.originalMusicList = new ArrayList<>();
//                        if (videoEntity.isUseOriginalAudio()) {
//                            float totalTime = 0; // 所有视频时间
//                            float timeline = 0; // 添加的视频时间线位置
//                            if (videoEntity.getClipList().size() > 0) {
//                                for (ClipVideoEntity clipVideoEntity : videoEntity.getClipList()) {
//                                    if (clipVideoEntity != null
//                                            && !TextUtils.isEmpty(clipVideoEntity.audioPath)
//                                            && new File(clipVideoEntity.audioPath).exists()) {
//                                        if (voiceStartTime < totalTime + clipVideoEntity.getDuring() && totalTime < trimDuration) {
//                                            // 多段音频裁剪，找到在裁剪范围内的音频
//                                            float startTime = voiceStartTime - totalTime;
//                                            // 确定每个音频开始裁剪的位置(第一个找到的音频裁剪开始位置可能不为0 )
//                                            KLog.i("=====添加原音:" + clipVideoEntity.audioPath);
//                                            float during = clipVideoEntity.getDuring();
//                                            if (totalTime + clipVideoEntity.getDuring() > trimDuration) {
//                                                during = trimDuration - totalTime;
//                                            }
//                                            try {
//                                                exportVideo.addMusic(
//                                                        clipVideoEntity.audioPath,
//                                                        startTime,  // 音乐截取开始位置
//                                                        startTime + during, // 音乐截取结束位置
//                                                        timeline, // 音乐在主时间线的开始位置
//                                                        timeline + during, // 音乐在主时间线的结束位置
//                                                        videoEntity.getOriginalMixFactor(),
//                                                        (float) RecordSpeed.getSpeed(clipVideoEntity.speedIndex),
//                                                        true);
////                                                Music originalVoice =
//    //                                                        videoEntity.originalMusicList.add(originalVoice);
//                                            } catch (InvalidArgumentException e) {
//                                                e.printStackTrace();
//                                            }
//                                            timeline += clipVideoEntity.getDuring() - startTime;
//                                            totalTime += clipVideoEntity.getDuring();
//                                            voiceStartTime = totalTime;
//                                        } else {
//                                            totalTime += clipVideoEntity.getDuring();
//                                        }
//                                    }
//                                }
//                            } else {
//                                // 有音轨的视频将音频分离加载，使音频大小可调节
//                                Music originalVoice = null;
//                                try {
//                                    originalVoice = VirtualVideo.createMusic(videoEntity.editingVideoPath);
//                                    if (trimVideo && (trimRange[0] != 0 || trimRange[1] != 0)) {
//                                        float during = trimRange[1] - trimRange[0];
//                                        originalVoice.setTimeRange(trimRange[0], trimRange[1]);
//                                        originalVoice.setTimelineRange(0, during);
//                                    } else {
//                                        originalVoice.setTimeRange(0, videoEntity.getDuring());
//                                        originalVoice.setTimelineRange(0, videoEntity.getDuring());
//                                    }
//                                    exportVideo.addMusic(originalVoice, true);
//                                } catch (InvalidArgumentException e) {
//                                    e.printStackTrace();
//                                }
//                                if (originalVoice != null) {
//                                    originalVoice.setMixFactor(videoEntity.getOriginalMixFactor());
//                                }
//                                videoEntity.originalMusicList.add(originalVoice);
////                                media.setMixFactor(videoEntity.getOriginalMixFactor());
//                            }
//                        }
//                        scene.addMedia(media);
//                        count++;
//                    }
//                }
//            }
////            exportVideo.setOriginalMixFactor(productEntity.originalMixFactor);
//
//            if (count > 0) {
//                KLog.i("======需要组合的视频个数：" + count);
//                scene.setPermutationMode(PermutationMode.COMBINATION_MODE);
//                exportVideo.addScene(scene);
//                if (productEntity.musicInfo != null) {
//                    KLog.i("======配乐的信息：" + productEntity.musicInfo.toString());
//                    if (!TextUtils.isEmpty(productEntity.musicInfo.getMusicPath())) {
//                        File file = new File(productEntity.musicInfo.getMusicPath());
//                        if (file.exists() && !file.isDirectory()) {
//                            try {
//                                exportVideo.addMusic(
//                                        productEntity.musicInfo.getMusicPath(),
//                                        productEntity.musicInfo.trimStart,
//                                        productEntity.musicInfo.trimEnd,
//                                        RecordSetting.RECORD_MUSIC_DELAY,
//                                        productEntity.musicInfo.trimEnd - productEntity.musicInfo.trimStart + RecordSetting.RECORD_MUSIC_DELAY,
//                                        productEntity.musicMixFactor,
//                                        (float) RecordSpeed.NORMAL.value(),
//                                        false);
//                            } catch (InvalidArgumentException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
////    public static boolean previewCombineVideo(VirtualVideo exportVideo, final String combineVideo) {
////        if (!TextUtils.isEmpty(combineVideo) && new File(combineVideo).exists()) {
////            Scene scene = VirtualVideo.createScene();
////            MediaObject media = null;
////            try {
////                media = new MediaObject(combineVideo);
////            } catch (InvalidArgumentException e) {
////                e.printStackTrace();
////            }
////            if (media != null) {
////                scene.addMedia(media);
////            }
////            exportVideo.addScene(scene);
////        } else {
////            return false;
////        }
////        return true;
////    }
//
    public static int[] getVideoWH(String videoPath) {
        int[] wh = new int[2];
        long start = System.currentTimeMillis();
        KLog.i("===开始获取视频的宽高：");
        if (!TextUtils.isEmpty(videoPath)) {
            File file = new File(videoPath);
            if (file.exists() && !file.isDirectory()) {
                MVideoConfig videoConfig = new MVideoConfig();
                new VideoEngine().getMediaInfo(videoPath, videoConfig);
                wh[0] = videoConfig.getVideoWidth();
                wh[1] = videoConfig.getVideoHeight();
            }
        }
        KLog.i("===获取视频的宽高，耗时：" + (System.currentTimeMillis() - start) + " ,w:" + wh[0] + " ,h:" + wh[1]);
        return wh;
    }


    /**
     * 本地上传单个视频的转换
     *
     * @param context
     * @param productEntity
     * @param listener
     */
//    public static void transformVideo(final Context context, String srtVideoPath, ProductEntity productEntity, final VideoJoinListener listener) {
//        if (productEntity != null) {
//            VirtualVideo exportVideo = new VirtualVideo();
//            VideoConfig videoConfig = new VideoConfig();
//            videoConfig.enableHWDecoder(CoreUtils.hasJELLY_BEAN_MR2());
//            videoConfig.enableHWDecoder(CoreUtils.hasJELLY_BEAN_MR2());
//            videoConfig.setVideoSize(productEntity.getExceptWH()[0], productEntity.getExceptWH()[1]);
//            videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoPublishBitrate);
//            videoConfig.setAspectRatio(productEntity.getExceptRatio());
//            videoConfig.setOptimizeForNet(true);
//            videoConfig.setKeyFrameTime(10);
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            final String exportPath = RecordFileUtil.createVideoFile(productEntity.baseDir);
//            KLog.i("======导出文件路径：" + exportPath);
//            Scene scene = VirtualVideo.createScene();
//            try {
//                scene.addMedia(srtVideoPath);
//            } catch (InvalidArgumentException e) {
//                e.printStackTrace();
//                KLog.i("======导出数据错误:" + e.getMessage());
//                if (listener != null) {
//                    listener.onJoinEnd(false, null);
//                }
//                return;
//            }
//            exportVideo.addScene(scene);
//            exportVideo.export(context, exportPath, videoConfig, new ExportListener() {
//
//                @Override
//                public void onExportStart() {
//                    KLog.i("=======onExportStart");
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    KLog.i("=======onExporting:" + progress);
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    KLog.i("=======onExportEnd result：" + result);
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        if (listener != null) {
//                            productEntity.combineVideo = exportPath;
//                            listener.onJoinEnd(true, exportPath);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(false, exportPath);
//                        }
//                    }
//                    exportVideo.release();
//                }
//            });
//        } else {
//            KLog.i("======作品为空，无需导出");
//            if (listener != null) {
//                listener.onJoinEnd(false, null);
//            }
//        }
//    }

    /**
     * 导出组合的视频
     */
//    public static void exportCombineVideo(final Context context, final VirtualVideo exportVideo, final ProductEntity productEntity, final VideoJoinListener listener) {
//        if (previewCombineVideo(exportVideo, productEntity, true, true)) {
//            VideoConfig videoConfig = new VideoConfig();
//            int opus_width = RecordManager.get().getFrameInfo().opus_width;
//            int opus_height = RecordManager.get().getFrameInfo().opus_height;
//            videoConfig.setVideoSize(opus_width == 540 ? 544 : opus_width, opus_height);
//            KLog.d("ggq", "RecordManager.get().getSetting().productWidth==" + RecordManager.get().getSetting().productWidth + "  RecordManager.get().getSetting().productHeight==" + RecordManager.get().getSetting().productHeight);
//            KLog.d("导出视频宽高", "RecordManager.get().getFrameInfo().opus_width==" + RecordManager.get().getFrameInfo().opus_width + "  RecordManager.get().getFrameInfo().opus_height" + RecordManager.get().getFrameInfo().opus_height);
//            KLog.d("导出视频宽高", "videoConfig==rate=" + videoConfig.getAspectRatio() + "  width==" + videoConfig.getVideoWidth() + "  height==" + videoConfig.getVideoHeight());
//            videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoPublishBitrate);
//            videoConfig.setOptimizeForNet(true);
//            videoConfig.setKeyFrameTime(10);
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            if (RecordSetting.SET_AUDIO_RATE) {
//                videoConfig.setAudioEncodingParameters(1, RecordSetting.AUDIO_SAMPLING_RATE, RecordSetting.AUDIO_ENCODING_BITRATE);
//            }

//
//    /**
//     * 本地上传单个视频的转换
//     *
//     * @param context
//     * @param productEntity
//     * @param listener
//     */
//    public static void transformVideo(final Context context, String srtVideoPath, ProductEntity productEntity, final VideoJoinListener listener) {
//        if (productEntity != null) {
//            VirtualVideo exportVideo = new VirtualVideo();
//            VideoConfig videoConfig = new VideoConfig();
//            videoConfig.enableHWDecoder(CoreUtils.hasJELLY_BEAN_MR2());
//            videoConfig.enableHWDecoder(CoreUtils.hasJELLY_BEAN_MR2());
//            videoConfig.setVideoSize(productEntity.getExceptWH()[0], productEntity.getExceptWH()[1]);
//            videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoPublishBitrate);
//            videoConfig.setAspectRatio(productEntity.getExceptRatio());
//            videoConfig.setOptimizeForNet(true);
//            videoConfig.setKeyFrameTime(10);
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            final String exportPath = RecordFileUtil.createVideoFile(productEntity.baseDir);
//            KLog.i("======导出文件路径：" + exportPath);
//            Scene scene = VirtualVideo.createScene();
//            try {
//                scene.addMedia(srtVideoPath);
//            } catch (InvalidArgumentException e) {
//                e.printStackTrace();
//                KLog.i("======导出数据错误:" + e.getMessage());
//                if (listener != null) {
//                    listener.onJoinEnd(false, null);
//                }
//                return;
//            }
//            exportVideo.addScene(scene);
//            exportVideo.export(context, exportPath, videoConfig, new ExportListener() {
//
//                @Override
//                public void onExportStart() {
//                    KLog.i("=======onExportStart");
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    KLog.i("=======onExporting:" + progress);
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    KLog.i("=======onExportEnd result：" + result);
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        if (listener != null) {
//                            productEntity.combineVideo = exportPath;
//                            listener.onJoinEnd(true, exportPath);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(false, exportPath);
//                        }
//                    }
//                    exportVideo.release();
//                }
//            });
//        } else {
//            KLog.i("======作品为空，无需导出");
//            if (listener != null) {
//                listener.onJoinEnd(false, null);
//            }
//        }
//    }
//
//    /**
//     * 导出组合的视频
//     */
//    public static void exportCombineVideo(final Context context, final VirtualVideo exportVideo, final ProductEntity productEntity, final VideoJoinListener listener) {
//        if (previewCombineVideo(exportVideo, productEntity, true, true)) {
//            VideoConfig videoConfig = new VideoConfig();
//            videoConfig.setVideoSize(RecordManager.get().getSetting().productWidth, RecordManager.get().getSetting().productHeight);
//            videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoPublishBitrate);
//            videoConfig.setAspectRatio(RecordManager.get().getSetting().getVideoRatio());
//            videoConfig.setOptimizeForNet(true);
//            videoConfig.setKeyFrameTime(10);
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            if (RecordSetting.SET_AUDIO_RATE) {
//                videoConfig.setAudioEncodingParameters(1, RecordSetting.AUDIO_SAMPLING_RATE, RecordSetting.AUDIO_ENCODING_BITRATE);
//            }
////            String watermarkPath = RecordManager.get().getSetting().getWatermarkPath();
////            if (!TextUtils.isEmpty(watermarkPath)) {
////                File file = new File(watermarkPath);
////                if (file.exists() && !file.isDirectory()) {
////                    Watermark watermark = new Watermark(watermarkPath);
////                    watermark.setPath(watermarkPath);
////                    watermark.setShowRect(RecordManager.get().getSetting().getWatermarkShowRectF());
////                    watermark.setStartTime(0);
////                    watermark.setEndTime(RecordManager.get().getSetting().maxVideoDuration);
////                    exportVideo.addWatermark(watermark);
////                }
////            }
//
//            final String exportPath = productEntity.baseDir
//                    + File.separator
//                    + PREFIX_COMBINE_FILE
//                    + RecordFileUtil.getTimestampString()
//                    + SUFFIX_VIDEO_FILE;
//            KLog.i("======导出文件路径：" + exportPath);
//            exportVideo.export(context, exportPath, videoConfig, new ExportListener() {
//
//                @Override
//                public void onExportStart() {
//                    KLog.i("=======onExportStart");
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    KLog.i("=======onExporting:" + progress);
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    KLog.i("=======onExportEnd result：" + result);
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        if (listener != null) {
//                            listener.onJoinEnd(true, exportPath);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(false, exportPath);
//                        }
//                    }
//                    exportVideo.release();
//                }
//            });
//        } else {
//            if (listener != null) {
//                listener.onJoinEnd(false, "====product is null or video is null");
//            }
//        }
//    }
//
//
//    public static void exportWatermarkVideo(final ProductEntity productEntity, Context context, VirtualVideo exportVideo, VideoJoinListener listener) {
//        if (previewCombineVideo(exportVideo, productEntity.combineVideo)) {
//            VideoConfig videoConfig = new VideoConfig();
//            videoConfig.setVideoSize(RecordManager.get().getSetting().productWidth, RecordManager.get().getSetting().productHeight);

//            String watermarkPath = RecordManager.get().getSetting().getWatermarkPath();
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            if (!TextUtils.isEmpty(watermarkPath)) {
//                File file = new File(watermarkPath);
//                if (file.exists() && !file.isDirectory()) {
//                    Watermark watermark = new Watermark(watermarkPath);
//                    watermark.setPath(watermarkPath);
//                    watermark.setShowRect(RecordManager.get().getSetting().getWatermarkShowRectF());
//                    watermark.setStartTime(0);
//                    watermark.setEndTime(exportVideo.getDuration());
//                    exportVideo.addWatermark(watermark);
//                }
//            }


//            final String exportPath = productEntity.baseDir
//                    + File.separator
//                    + PREFIX_COMBINE_FILE
//                    + RecordFileUtil.getTimestampString()
//                    + SUFFIX_VIDEO_FILE;
//            KLog.i("ggq","======导出文件路径：" + exportPath);
//            KLog.d("ggq", "videoConfig==" + videoConfig.toString());
//            KLog.d("导出视频宽高", "videoConfig==rate=" + videoConfig.getAspectRatio() + "  width==" + videoConfig.getVideoWidth() + "  height==" + videoConfig.getVideoHeight());
//            exportVideo.export(context, exportPath, videoConfig, new ExportListener() {
//
//                @Override
//                public void onExportStart() {
//                    KLog.i("=======onExportStart==" + videoConfig.getVideoWidth());
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    KLog.i("=======onExporting:" + progress);
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    KLog.i("=======onExportEnd result：" + result);
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        if (listener != null) {
//                            listener.onJoinEnd(true, exportPath);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(false, exportPath);
//                        }
//                    }
//                    exportVideo.release();
//                }
//            });
//        } else {
//            if (listener != null) {
//                listener.onJoinEnd(false, "====product is null or video is null");
//            }
//        }
//    }


//    public static void exportWatermarkVideo(final ProductEntity productEntity, Context context, VirtualVideo exportVideo, VideoJoinListener listener) {
//        if (previewCombineVideo(exportVideo, productEntity.combineVideo)) {
//            VideoConfig videoConfig = new VideoConfig();
//            videoConfig.setVideoSize(RecordManager.get().getFrameInfo().opus_width, RecordManager.get().getFrameInfo().opus_height);
//            String watermarkPath = RecordManager.get().getSetting().getWatermarkPath();
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            if (!TextUtils.isEmpty(watermarkPath)) {
//                File file = new File(watermarkPath);
//                if (file.exists() && !file.isDirectory()) {
//                    Watermark watermark = new Watermark(watermarkPath);
//                    watermark.setPath(watermarkPath);
//                    watermark.setShowRect(RecordManager.get().getSetting().getWatermarkShowRectF());
//                    watermark.setStartTime(0);
//                    watermark.setEndTime(exportVideo.getDuration());
//                    exportVideo.addWatermark(watermark);
//                }
//            }
//
//            final String exportPath = productEntity.baseDir
//                    + File.separator
//                    + PREFIX_COMBINE_FILE
//                    + "watermark_"
//                    + RecordFileUtil.getTimestampString()
//                    + SUFFIX_VIDEO_FILE;
//            KLog.i("导出视频宽高", "======导出文件路径：" + exportPath);
//            exportVideo.export(context, exportPath, videoConfig, new ExportListener() {
//
//                @Override
//                public void onExportStart() {
//                    KLog.i("=======onExportStart");
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    KLog.i("=======onExporting:" + progress);
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    KLog.i("=======onExportEnd result：" + result);
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        if (listener != null) {
//                            listener.onJoinEnd(true, exportPath);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(false, exportPath);
//                        }
//                    }
//                    exportVideo.release();
//                }
//            });
//        } else {
//            if (listener != null) {
//                listener.onJoinEnd(false, "====product is null or video is null");
//            }
//        }
//    }

    /**
     * 导出裁剪视频
     *
     * @param context
     * @param virtualVideo
     * @param shortVideoEntity
     * @param listener
     */
//    public static void exportClipVideo(Context context, final VirtualVideo virtualVideo, ShortVideoEntity shortVideoEntity, final VideoJoinListener listener) {
//        Scene scene = VirtualVideo.createScene();
//        MediaObject mediaObject = null;
//        try {
//            mediaObject = new MediaObject(shortVideoEntity.editingVideoPath);
//        } catch (InvalidArgumentException e) {
//            e.printStackTrace();
//        }
//        if (mediaObject != null) {
//            float voiceStartTime = 0;
//            float[] trimRange = shortVideoEntity.getTrimRange();
//            float trimDuration = shortVideoEntity.getDuring();
//            if (trimRange[0] != 0 || trimRange[1] != 0) {
//                mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
//                voiceStartTime = trimRange[0];
//                trimDuration = trimRange[1] - trimRange[0]; // 截取时长
//            }
////            mediaObject.setTimeRange(shortVideoEntity.getTrimRange()[0], shortVideoEntity.getTrimRange()[1]);
//            mediaObject.setAspectRatioFitMode(AspectRatioFitMode.KEEP_ASPECTRATIO_EXPANDING);
////            if (!shortVideoEntity.isImport()) {
////                mediaObject.setAudioMute(true);
//=======
//
//            final String exportPath = productEntity.baseDir
//                    + File.separator
//                    + PREFIX_COMBINE_FILE
//                    + "watermark_"
//                    + RecordFileUtil.getTimestampString()
//                    + SUFFIX_VIDEO_FILE;
//            KLog.i("======导出文件路径：" + exportPath);
//            exportVideo.export(context, exportPath, videoConfig, new ExportListener() {
//
//                @Override
//                public void onExportStart() {
//                    KLog.i("=======onExportStart");
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    KLog.i("=======onExporting:" + progress);
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    KLog.i("=======onExportEnd result：" + result);
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        if (listener != null) {
//                            listener.onJoinEnd(true, exportPath);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(false, exportPath);
//                        }
//                    }
//                    exportVideo.release();
//                }
//            });
//        } else {
//            if (listener != null) {
//                listener.onJoinEnd(false, "====product is null or video is null");
//            }
//        }
//    }
//
//    /**
//     * 导出裁剪视频
//     *
//     * @param context
//     * @param virtualVideo
//     * @param shortVideoEntity
//     * @param listener
//     */
//    public static void exportClipVideo(Context context, final VirtualVideo virtualVideo, ShortVideoEntity shortVideoEntity, final VideoJoinListener listener) {
//        Scene scene = VirtualVideo.createScene();
//        MediaObject mediaObject = null;
//        try {
//            mediaObject = new MediaObject(shortVideoEntity.editingVideoPath);
//        } catch (InvalidArgumentException e) {
//            e.printStackTrace();
//        }
//        if (mediaObject != null) {
//            float voiceStartTime = 0;
//            float[] trimRange = shortVideoEntity.getTrimRange();
//            float trimDuration = shortVideoEntity.getDuring();
//            if (trimRange[0] != 0 || trimRange[1] != 0) {
//                mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
//                voiceStartTime = trimRange[0];
//                trimDuration = trimRange[1] - trimRange[0]; // 截取时长
//            }
////            mediaObject.setTimeRange(shortVideoEntity.getTrimRange()[0], shortVideoEntity.getTrimRange()[1]);
//            mediaObject.setAspectRatioFitMode(AspectRatioFitMode.KEEP_ASPECTRATIO_EXPANDING);
////            if (!shortVideoEntity.isImport()) {
////                mediaObject.setAudioMute(true);
////            }
//            mediaObject.setMixFactor(shortVideoEntity.getOriginalMixFactor());
//            // 添加原声 (1 录制时，原声没有添加进来 2 发布页调节音量需重新导出视频)
//            if (shortVideoEntity.isUseOriginalAudio()) {
//                float totalTime = 0; // 所有视频时间
//                float timeline = 0; // 添加的视频时间线位置
//                if (shortVideoEntity.getClipList().size() > 0) {
//                    for (ClipVideoEntity clipVideoEntity : shortVideoEntity.getClipList()) {
//                        if (clipVideoEntity != null
//                                && !TextUtils.isEmpty(clipVideoEntity.audioPath)
//                                && new File(clipVideoEntity.audioPath).exists()) {
//                            KLog.i("=====添加原音:" + clipVideoEntity.audioPath);
//
//                            if (voiceStartTime < totalTime + clipVideoEntity.getDuring() && totalTime < trimDuration) {
//                                // 多段音频裁剪，找到在裁剪范围内的音频
//                                float startTime = voiceStartTime - totalTime;
//                                // 确定每个音频开始裁剪的位置(第一个找到的音频裁剪开始位置可能不为0 )
//                                KLog.i("=====添加原音:" + clipVideoEntity.audioPath);
//                                float during = clipVideoEntity.getDuring();
//                                if (totalTime + clipVideoEntity.getDuring() > trimDuration) {
//                                    during = trimDuration - totalTime;
//                                }
//                                try {
//                                    virtualVideo.addMusic(
//                                            clipVideoEntity.audioPath,
//                                            startTime,  // 音乐截取开始位置
//                                            startTime + during, // 音乐截取结束位置
//                                            timeline, // 音乐在主时间线的开始位置
//                                            timeline + during, // 音乐在主时间线的结束位置
//                                            shortVideoEntity.getOriginalMixFactor(),
//                                            (float) RecordSpeed.getSpeed(clipVideoEntity.speedIndex),
//                                            true);
//                                } catch (InvalidArgumentException e) {
//                                    e.printStackTrace();
//                                }
//                                timeline += clipVideoEntity.getDuring() - startTime;
//                                totalTime += clipVideoEntity.getDuring();
//                                voiceStartTime = totalTime;
//                            } else {
//                                totalTime += clipVideoEntity.getDuring();
//                            }
//                        }
//                    }
//                } else {
////                    // 有音轨的视频将音频分离加载，使音频大小可调节
////                    Music originalVoice = null;
////                    try {
////                        originalVoice = VirtualVideo.createMusic(shortVideoEntity.editingVideoPath);
////                        virtualVideo.addMusic(originalVoice, true);
////                    } catch (InvalidArgumentException e) {
////                        e.printStackTrace();
////                    }
////                    if (originalVoice != null) {
////                        originalVoice.setMixFactor(shortVideoEntity.getOriginalMixFactor());
////                    }
//                }
//            }
//
//            scene.addMedia(mediaObject);
//            virtualVideo.addScene(scene);
//
//            final String videoFile = RecordFileUtil.createVideoFile(shortVideoEntity.baseDir);
//            VideoConfig videoConfig = new VideoConfig();
//            VirtualVideo.getMediaInfo(shortVideoEntity.editingVideoPath, videoConfig, true);
//            videoConfig.enableHWDecoder(false);
//            videoConfig.enableHWEncoder(true);
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            videoConfig.setKeyFrameTime(10);//关键帧间隔设置为0，方便快速倒序
//            if (RecordSetting.SET_AUDIO_RATE) {
//                videoConfig.setAudioEncodingParameters(1, RecordSetting.AUDIO_SAMPLING_RATE, RecordSetting.AUDIO_ENCODING_BITRATE);
//            }
//
//            // 根据素材真实尺寸设置参数
//            if (shortVideoEntity.quality == FrameInfo.VIDEO_QUALITY_HIGH) {
//                // 大尺寸素材
//                videoConfig.setVideoSize(RecordSetting.PRODUCT_WIDTH, RecordSetting.PRODUCT_HEIGHT);
//                videoConfig.setVideoEncodingBitRate(RecordSetting.VIDEO_PUBLISH_BITRATE);
//            } else {
//                // 小尺寸素材
//                videoConfig.setVideoSize(RecordSetting.VIDEO_WIDTH, RecordSetting.VIDEO_HEIGHT);
//                videoConfig.setVideoEncodingBitRate(RecordSetting.VIDEO_PUBLISH_BITRATE_SMALL);
//            }
//            videoConfig.setAspectRatio(RecordManager.get().getSetting().getVideoRatio());
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            videoConfig.setOptimizeForNet(true);
//
//            virtualVideo.export(context, videoFile, videoConfig, new ExportListener() {
//                @Override
//                public void onExportStart() {
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }

//                }
//            }
//
//            scene.addMedia(mediaObject);
//            virtualVideo.addScene(scene);
//
//            final String videoFile = RecordFileUtil.createVideoFile(shortVideoEntity.baseDir);
//            VideoConfig videoConfig = new VideoConfig();
//            VirtualVideo.getMediaInfo(shortVideoEntity.editingVideoPath, videoConfig, true);
//            videoConfig.enableHWDecoder(false);
//            videoConfig.enableHWEncoder(true);
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            videoConfig.setKeyFrameTime(10);//关键帧间隔设置为0，方便快速倒序
//            if (RecordSetting.SET_AUDIO_RATE) {
//                videoConfig.setAudioEncodingParameters(1, RecordSetting.AUDIO_SAMPLING_RATE, RecordSetting.AUDIO_ENCODING_BITRATE);
//            }
//
//            // 根据素材真实尺寸设置参数
//            if (shortVideoEntity.quality == FrameInfo.VIDEO_QUALITY_HIGH) {
//                // 大尺寸素材
//                videoConfig.setVideoSize(RecordSetting.PRODUCT_WIDTH, RecordSetting.PRODUCT_HEIGHT);
//                videoConfig.setVideoEncodingBitRate(RecordSetting.VIDEO_PUBLISH_BITRATE);
//            } else {
//                // 小尺寸素材
//                videoConfig.setVideoSize(RecordSetting.VIDEO_WIDTH, RecordSetting.VIDEO_HEIGHT);
//                videoConfig.setVideoEncodingBitRate(RecordSetting.VIDEO_PUBLISH_BITRATE_SMALL);
//            }
////            videoConfig.setAspectRatio(RecordManager.get().getSetting().getVideoRatio());
//            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//            videoConfig.setOptimizeForNet(true);
//            KLog.d("导出视频宽高", "width==" + videoConfig.getVideoWidth() + "  height==" + videoConfig.getVideoHeight());
//            virtualVideo.export(context, videoFile, videoConfig, new ExportListener() {
//                @Override
//                public void onExportStart() {
//                    if (listener != null) {
//                        listener.onJoinStart();
//                    }
//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result) {
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        if (listener != null) {
//                            listener.onJoinEnd(true, videoFile);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(false, videoFile);
//                        }
//                    }
//                    virtualVideo.release();
//                }
//            });
//        } else {
//            if (listener != null) {
//                listener.onJoinEnd(false, null);
//            }
//        }
//    }


//                }
//
//                @Override
//                public boolean onExporting(int progress, int max) {
//                    if (listener != null) {
//                        listener.onJoining(progress, max);
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onExportEnd(int result,String path) {
//                    if (result >= VirtualVideo.RESULT_SUCCESS) {
//                        if (listener != null) {
//                            listener.onJoinEnd(true, videoFile);
//                        }
//                    } else {
//                        if (listener != null) {
//                            listener.onJoinEnd(false, videoFile);
//                        }
//                    }
//                    virtualVideo.release();
//                }
//            });
//        } else {
//            if (listener != null) {
//                listener.onJoinEnd(false, null);
//            }
//        }
//    }
//

    /**
     * 更新或插入一个作品
     *
     * @param productEntity
     */
    public static void insertOrUpdateProductToDb(ProductEntity productEntity) {
        if (productEntity != null) {
            productEntity.refreshModifyTime();
            GreenDaoManager.get().getProductEntityDao().insertOrReplace(productEntity);
            KLog.i("======更新数据库信息productEntity:" + productEntity.toString());
        } else {
            KLog.i("======没有数据更新");
        }
        KLog.d("getProductEntity().frameInfo==" + productEntity.frameInfo);
//        List<ProductEntity> list = queryAllDraft();
//        for (ProductEntity entity : list) {
//            KLog.i("query==>" + entity.getId() + entity.frameInfo);
//        }
        queryProduct();

    }

    /**
     * 临时使用
     */
    private static void queryProduct() {
        Disposable disposable = Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(new Function<Integer, List<ProductEntity>>() {
                    @Override
                    public List<ProductEntity> apply(@NonNull Integer integer) throws Exception {
                        KLog.i("====开始查询数据");
                        return RecordUtil.queryAllDraft();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductEntity>>() {
                    @Override
                    public void accept(@NonNull List<ProductEntity> productEntities) throws Exception {
//                        KLog.i("====结束草稿查询:" + CommonUtils.printList(productEntities));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        KLog.e("====查询数据出错:" + throwable.getMessage());
                    }
                });
    }

    /**
     * 删除一个作品
     *
     * @param entity
     */
    public static void deleteProduct(ProductEntity entity, boolean deleteFiles) {
        if (entity != null && entity.getId() != null) {
            FileUtil.deleteAll(entity.baseDir, deleteFiles);
            KLog.i("=====从本地删除作品id:" + entity.getId());
            GreenDaoManager.get().getProductEntityDao().delete(entity);
        } else {
            KLog.i("=====从本地删除作品不正确:" + entity);
        }
    }

    /**
     * 移到草稿
     */
    public static boolean moveToDraft(ProductEntity productEntity) {
        if (productEntity != null && productEntity.moveToDraft()) {
            insertOrUpdateProductToDb(productEntity);
            return true;
        }
        return false;
    }

    /**
     * 移动到发布状态
     *
     * @param productEntity
     */
    public static void moveToPublishing(ProductEntity productEntity) {
        KLog.e("=====query-draft:-moveToPublishing" + productEntity);
        if (productEntity != null) {
            productEntity.productType = ProductEntity.TYPE_PUBLISHING;
            insertOrUpdateProductToDb(productEntity);
        }
    }

    /**
     * 查询最近的草稿
     *
     * @return
     */
    public static ProductEntity queryLatestDraft() {
        QueryBuilder<ProductEntity> queryBuilder = GreenDaoManager.get().getProductEntityDao().queryBuilder();
        queryBuilder.where(ProductEntityDao.Properties.UserId.eq(AccountUtil.getUserId()),
                queryBuilder.or(ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_EDITING),
                        ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_DRAFT),
                        ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_PUBLISHING)));
        List<ProductEntity> list = queryBuilder
                .orderDesc(ProductEntityDao.Properties.ModifyTime)
                .limit(1)
                .list();
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 查询所有未发布作品
     *
     * @return
     */
    public static List<ProductEntity> queryAllDraft() {
        QueryBuilder<ProductEntity> queryBuilder = GreenDaoManager.get().getProductEntityDao().queryBuilder();
        queryBuilder.where(ProductEntityDao.Properties.UserId.eq(AccountUtil.getUserId()),
                queryBuilder.or(ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_EDITING),
                        ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_DRAFT),
                        ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_PUBLISHING)));
        return queryBuilder
                .orderDesc(ProductEntityDao.Properties.ModifyTime)
                .list();
    }

    //
    public static ProductEntity queryUnfinishedProduct() {

        QueryBuilder<ProductEntity> queryBuilder = GreenDaoManager.get().getProductEntityDao().queryBuilder();
        queryBuilder.where(ProductEntityDao.Properties.UserId.eq(AccountUtil.getUserId()));
        queryBuilder.whereOr(ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_EDITING),
                ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_PUBLISHING));
        List<ProductEntity> list = queryBuilder
                .orderDesc(ProductEntityDao.Properties.ModifyTime)
                .limit(1)
                .list();
        KLog.e("=====query-draft:-queryUnfinishedProduct  list==" + list);
        return list.size() > 0 ? list.get(0) : null;
    }

    //
//
    public static int calculateProgress(long current, long total, float scale) {
        return (int) (current * 1f / (total == 0 ? 1 : total) * scale);
    }

    /**
     * startPercentag + var1 * perEntity;
     *
     * @param current
     * @return
     */
    public static int calculateProgressNew(long current, int start, float per) {
        return start + (int) (current * per);
    }

    //
    public static boolean hasHeadset() {
        AudioManager audioManager = (AudioManager) DCApplication.getDCApp().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            if (SdkUtils.isMarshmallow()) {
                AudioDeviceInfo[] audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
                for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfos) {
                    if (audioDeviceInfo != null) {
                        KLog.e("hasHeadset getType:" + audioDeviceInfo.getType());
                        if (audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
                                || audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                                || audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                                || audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_USB_HEADSET) {
                            return true;
                        }
                    }
                }
                return false;
            } else {
                return audioManager.isWiredHeadsetOn();
            }
        }
        return false;
    }
//
}
