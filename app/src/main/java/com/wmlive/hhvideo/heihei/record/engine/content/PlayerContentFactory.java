package com.wmlive.hhvideo.heihei.record.engine.content;

import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.dongci.sun.gpuimglibrary.api.DCVideoManager;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.ClipVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MScene;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wmlive.hhvideo.heihei.record.engine.content.ExportContentFactory.createBgObject;

/**
 * 创建  播放内容
 * 创建Scence
 */
public class PlayerContentFactory {

    public static List<Scene> createScenes(List<MediaObject> assets) {
        if (assets == null)
            return null;
        Scene scene = new MScene();
        scene.assets = assets;
        List<Scene> scenes = Arrays.asList(scene);
        return scenes;
    }


    /**
     * Mv播放,发布页面
     * @param productEntity
     * @param previewWidth  预览宽度
     * @param previewHeight 预览高度
     * @return
     */
    public static List<MediaObject> getMvMediaPlayerPublish(ProductEntity productEntity,int previewWidth,int previewHeight){
        if (productEntity == null ) {
            return null;
        }
        List<MediaObject> meidas = new ArrayList<MediaObject>();
        //
        MediaObject mediaObject = createMediaObjectMvProduct(productEntity,previewWidth,previewHeight);
        if (mediaObject != null){
            long duration = VideoUtils.getVideoLength(productEntity.combineVideo);
            if(duration>0){
                meidas.add(mediaObject);
                MediaObject audio = createAudioObjectMv(productEntity.combineAudio,duration);
                meidas.add(audio);
            }

        }
        return meidas;
    }

    /**
     * Mv播放
     * @param shortVideoEntityMv
     * @param previewWidth  预览宽度
     * @param previewHeight 预览高度
     * @return
     */
    public static List<MediaObject> getMvMediaPlayer(ShortVideoEntity shortVideoEntityMv,int previewWidth,int previewHeight){
        KLog.i("getMvMediaPlayer--->width"+previewWidth+"height:>"+previewHeight);
        if (shortVideoEntityMv == null ) {
            return null;
        }
        List<MediaObject> meidas = new ArrayList<MediaObject>();
        MediaObject mediaObjectVideo = null;
        //如果没有分离视频，只包含合成的视频
        if(!TextUtils.isEmpty(shortVideoEntityMv.combineVideoAudio) && new File(shortVideoEntityMv.combineVideoAudio).exists()
                &&VideoUtils.getVideoLength(shortVideoEntityMv.combineVideoAudio)>0){
            mediaObjectVideo = createMediaObjectMvVideo(shortVideoEntityMv.combineVideoAudio,previewWidth*1.0f/previewHeight*1.0f);
            mediaObjectVideo.assetId = 1;
            meidas.add(mediaObjectVideo);
        }else {
            mediaObjectVideo = createPlayerMediaObjectMv(shortVideoEntityMv,previewWidth*1.0f/previewHeight*1.0f);
            if (mediaObjectVideo != null){
                mediaObjectVideo.assetId = 1;
                long duration = VideoUtils.getVideoLength(shortVideoEntityMv.editingVideoPath);
                if(duration>0){
                    meidas.add(mediaObjectVideo);
                    MediaObject audio = createAudioObjectMv(shortVideoEntityMv.editingAudioPath,duration);
                    if(audio!=null)
                        meidas.add(audio);
                }
            }
        }
        //
        return meidas;
    }


    /**
     * 获取所有视频资源
     *
     * @param productEntity
     * @return
     */
    public static List<MediaObject> getPlayerMediaFromProduct(ProductEntity productEntity) {
        List<MediaObject> meidas = new ArrayList<MediaObject>();
        List<MediaObject> bgs = new ArrayList<MediaObject>();
        if (productEntity == null || productEntity.shortVideoList == null) {
            return meidas;
        }
        //添加画框
        for (int i = 0; i < productEntity.shortVideoList.size(); i++) {
            ShortVideoEntity videoEntity = productEntity.shortVideoList.get(i);
//            MediaObject mediaObject = createPlayerMediaObjectNew(videoEntity, productEntity.frameInfo.getLayoutRelativeRectF(i), productEntity.frameInfo.getLayoutAspectRatio(i));

            MediaObject mediaObject = createPlayerMediaObject(videoEntity, productEntity.frameInfo.getLayoutRelativeRectF(i), productEntity.frameInfo.getLayoutAspectRatio(i));
            if (mediaObject != null) {
                mediaObject.assetId = i+1;
                meidas.add(mediaObject);
            }else {
                //添加背景
                MediaObject mediaObjectBg = createBgObject(RecordFileUtil.getFrameImagePath(productEntity.frameInfo.sep_image), productEntity.frameInfo.getLayoutRelativeRectF(i));
                if (mediaObjectBg != null){
                    mediaObjectBg.assetId = productEntity.shortVideoList.size()+i+1;
                    bgs.add(mediaObjectBg);
                }

            }
        }
        meidas.addAll(bgs);
        return meidas;
    }

    /**
     * 获取所有视频资源
     *
     * @param productEntity
     * @return
     */
    public static List<MediaObject> getPlayerMediaFromProductNew(ProductEntity productEntity) {
        List<MediaObject> meidas = new ArrayList<MediaObject>();
        List<MediaObject> bgs = new ArrayList<MediaObject>();
        if (productEntity == null || productEntity.shortVideoList == null) {
            return meidas;
        }
        //添加画框
        for (int i = 0; i < productEntity.shortVideoList.size(); i++) {
            ShortVideoEntity videoEntity = productEntity.shortVideoList.get(i);
            MediaObject mediaObject = createPlayerMediaObjectNew(videoEntity, productEntity.frameInfo.getLayoutRelativeRectF(i), productEntity.frameInfo.getLayoutAspectRatio(i));

//            MediaObject mediaObject = createPlayerMediaObject(videoEntity, productEntity.frameInfo.getLayoutRelativeRectF(i), productEntity.frameInfo.getLayoutAspectRatio(i));
            if (mediaObject != null){
                mediaObject.assetId = i+1;
                meidas.add(mediaObject);
            }else {
                //添加背景
                MediaObject mediaObjectBg = createBgObject(RecordFileUtil.getFrameImagePath(productEntity.frameInfo.sep_image), productEntity.frameInfo.getLayoutRelativeRectF(i));
                if (mediaObjectBg != null)
                    bgs.add(mediaObjectBg);
            }
        }
        meidas.addAll(bgs);
        return meidas;
    }


    /**
     * 包含音乐的
     *
     * @param productEntity
     * @return
     */
    public static List<MediaObject> getPlayerMediaFromProductWidthAudio(ProductEntity productEntity) {
        List<MediaObject> meidas = getPlayerMediaFromProduct(productEntity);
        long duration = 0;
        for (MediaObject mediaObject : meidas) {
            KLog.i("combinePreview-Medias-duration>" + mediaObject.getFilePath());
            KLog.i("combinePreview-Medias-id>" + mediaObject.assetId);
            if (mediaObject.getType() == MediaObject.MediaObjectTypeVideo)
                duration = Math.max(duration, mediaObject.getDuration());//?duration:mediaObject.getDuration();
        }
        MediaObject audioObject = createAudioObject(productEntity, duration);
        if (audioObject != null){
            audioObject.assetId = 20;
            meidas.add(audioObject);
        }

        return meidas;
    }

    /**
     * 创建音频 assert
     *
     * @param productEntity
     * @return
     */
    private static MediaObject createAudioObject(ProductEntity productEntity, long duration) {
        if (productEntity == null || TextUtils.isEmpty(productEntity.combineAudio)) {
            return null;
        }
        if(new File(productEntity.combineAudio).length()==0)
            return null;
        MediaObject mediaObject = new MAsset(productEntity.combineAudio);
        KLog.i("播放item 的地址：" + productEntity.combineAudio);
        KLog.i("播放item 的地址：showRect");
        mediaObject.setSourceType(MediaObject.MediaObjectTypeAudio);

        mediaObject.setTimeRange(0, duration);
        //获取视频的时长
        mediaObject.setVolume(1.0f);
        return mediaObject;
    }


    /**
     * 创建播放的内容，无论录制还是本地都加载进来
     *
     * @param path
     * @param rectFInVideo
     * @param clipRect
     * @param volume
     * @return
     */
    private static MediaObject createMediaVideoBase(String path, RectF rectFInVideo,RectF clipRect, float volume) {
        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(path)
                || !new File(path).exists())) {
            return null;
        }
        long leng = VideoUtils.getVideoLength(path);
        KLog.i("视频地址:地址>" + path);
        KLog.i("视频地址:时长>" + leng);
        if (leng == 0)//视频为空的
            return null;
        MAsset mediaObject = new MAsset(path);
        mediaObject.setSourceType(MediaObject.MediaObjectTypeVideo);
        //获取视频的时长
        mediaObject.setRectInVideo(rectFInVideo);
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObject.setAudioMute(false);
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(volume);
        return mediaObject;
    }

    /**
     * 视频裁剪
     * @param path
     * @return
     */
    public static MediaObject createMediaFromTrim(String path) {
        RectF rectFInVideo = new RectF(0,0,1,1);
        aVideoConfig config = VideoUtils.getMediaInfor(path);
        KLog.i("trim---width"+config.getVideoWidth()+"height::>"+config.getVideoHeight());
        RectF clipRect = null;
        if(config.rotation==0){
            clipRect = new RectF(0,0,config.getVideoWidth(),config.getVideoHeight());
        }else {
            clipRect = new RectF(0,0,config.getVideoHeight(),config.getVideoWidth());
        }
        MediaObject mediaObject = createMediaVideoBase(path,rectFInVideo,clipRect,1.0f);
        return mediaObject;
    }

    /**
     * 创建播放的内容，无论录制还是本地都加载进来
     *
     * @param videoEntity
     * @param frameRecf
     * @param frameRatio
     * @return
     */
    public static MediaObject createPlayerMediaObject(ShortVideoEntity videoEntity, RectF frameRecf, float frameRatio) {
//        KLog.i("视频地址:pre0>" + videoEntity);
        if (videoEntity == null) {
            //当前 框无可以编辑的文件
            return null;
        }
//        KLog.i("视频地址:pre1>" + videoEntity.editingVideoPath);
        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(videoEntity.editingVideoPath)
                || !new File(videoEntity.editingVideoPath).exists()) &&
                (TextUtils.isEmpty(videoEntity.importVideoPath)
                        || !new File(videoEntity.importVideoPath).exists())) {
            return null;
        }
        String path = TextUtils.isEmpty(videoEntity.editingVideoPath) ? videoEntity.importVideoPath : videoEntity.editingVideoPath;
        KLog.i("视频地址:pre2>" + path);
        long leng = VideoUtils.getVideoLength(path);
        KLog.i("视频地址:时长>" + leng);
        if (leng == 0)//视频为空的
            return null;

        MediaObject mediaObject = new MAsset(path);
        KLog.i("播放item 的地址：" + path);
        KLog.i("播放item 的地址：showRect" + frameRecf);
        //获取视频的时长
        mediaObject.setRectInVideo(frameRecf);
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObject.setAudioMute(true);
        float[] trimRange = videoEntity.getTrimRange();
        float trimDuration = videoEntity.getDuring();
        float voiceStartTime = 0;
        KLog.i("播放item 的地址：：setTimeRange-pre" + trimRange[0]+"11->"+trimRange[1]);
        //视频裁剪,
        if ((trimRange[0] != 0 || trimRange[1] != 0)) {
            mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
            voiceStartTime = trimRange[0];
            trimDuration = trimRange[1] - trimRange[0]; // 截取时长

        }else if(trimRange[1] == 0){//
//            videoEntity.setTrimRange(trimRange[0],leng);
            mediaObject.setTimeRange(trimRange[0],leng);
        }
//        if(trimRange[0]> RecordSetting.MAX_UPLOAD_VIDEO_DURATION*1000){
//            trimRange[0] = trimRange[0]/1000;
//            mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
//        }
//        if(trimRange[1]> RecordSetting.MAX_UPLOAD_VIDEO_DURATION*1000){
//            trimRange[1] = trimRange[1]/1000;
//            mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
//        }
//        frameRatio = 0.4f;
        KLog.i("播放item 的地址：：frameRatio:" + frameRatio);
        KLog.i("播放item 的地址：：getWidth:" + mediaObject.getWidth()+"height:>"+mediaObject.getHeight());
        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(), frameRatio, 0);
        KLog.i("播放item 的地址：：clipRect" + clipRect);
//        KLog.i("播放item 的地址：：TimeRange" + mediaObject.getTimeRange());
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(videoEntity.volume);
        return mediaObject;
    }

    /**
     * 创建播放的内容，无论录制还是本地都加载进来
     * new 使用合成 后的视频
     *
     * @param videoEntity
     * @param frameRecf
     * @param frameRatio
     * @return
     */
    public static MediaObject createPlayerMediaObjectNew(ShortVideoEntity videoEntity, RectF frameRecf, float frameRatio) {
        if (videoEntity == null) {
            //当前 框无可以编辑的文件
            return null;
        }
        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(videoEntity.combineVideoAudio)
                || !new File(videoEntity.combineVideoAudio).exists())) {
            return null;
        }
        String path = videoEntity.combineVideoAudio;

        long leng = VideoUtils.getVideoLength(path);
        if (leng == 0)//视频为空的
            return null;

        MediaObject mediaObject = new MAsset(path);
        KLog.i("播放item 的地址：" + path);
        KLog.i("播放item 的地址：showRect" + frameRecf);
        //获取视频的时长
        mediaObject.setRectInVideo(frameRecf);
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
//        mediaObject.setAudioMute(true);
        float[] trimRange = videoEntity.getTrimRange();
//        float trimDuration = videoEntity.getDuring();
        float voiceStartTime = 0;
        //视频裁剪,
        if ((trimRange[0] != 0 || trimRange[1] != 0)) {
            mediaObject.setTimeRange(trimRange[0]/1000f, trimRange[1]/1000f); //设置media时长

//            voiceStartTime = trimRange[0];
//            trimDuration = trimRange[1] - trimRange[0]; // 截取时长
        }
        KLog.i("setTimeRange::start" + String.valueOf(trimRange[0])+"duration::>"+String.valueOf(trimRange[1]));
        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(), frameRatio, 0);
        KLog.i("播放item 的地址：：clipRect" + clipRect);
        KLog.i("播放item 的地址：：TimeRange" + mediaObject.getTimeRange());
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(videoEntity.volume);
        return mediaObject;
    }





    private static MediaObject createPlayerMediaObjectMv(ShortVideoEntity videoEntity,float rate) {
        KLog.i("视频地址:pre0>" + videoEntity);
        if (videoEntity == null) {
            //当前 框无可以编辑的文件
            return null;
        }
        KLog.i("视频地址:pre1>" + videoEntity.editingVideoPath);
        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(videoEntity.editingVideoPath)
                || !new File(videoEntity.editingVideoPath).exists())
               ) {
            return null;
        }
        String path = videoEntity.editingVideoPath;
        long leng = VideoUtils.getVideoLength(path);
        if (leng == 0)//视频为空的
            return null;
        MediaObject mediaObject = new MAsset(path);
        //获取视频的时长
        mediaObject.setRectInVideo(new RectF(0,0,1.0f,1.0f));
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObject.setAudioMute(true);

        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(), rate, 0);
//        RectF clipRect = new RectF(0,0,480,640);
        KLog.i("播放item 的地址：：path" + path);
//        KLog.i("播放item 的地址：：TimeRange" + mediaObject.getTimeRange());
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(videoEntity.volume);
        return mediaObject;
    }

    /**
     * 创建音频 assert
     *
     * @param path
     * @return
     */
    public static MediaObject createAudioObjectMv(String path, long duration) {
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            return null;
        }

        if(new File(path).length()==0)
            return null;
        MediaObject mediaObject = new MAsset(path);
        KLog.i("播放item音频 的地址：" + path);
        mediaObject.setSourceType(MediaObject.MediaObjectTypeAudio);

        mediaObject.setTimeRange(0, duration);
        //获取视频的时长
        mediaObject.setVolume(1.0f);
        return mediaObject;
    }

    private static MediaObject createMediaObjectMvProduct(ProductEntity videoEntity,int viewWidth,int viewHeight) {
        KLog.i("视频地址:pre0>" + videoEntity);
        if (videoEntity == null) {
            //当前 框无可以编辑的文件
            return null;
        }
        KLog.i("视频地址:pre1>" + videoEntity.combineVideo);
        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(videoEntity.combineVideo)
                || !new File(videoEntity.combineVideo).exists())
                ) {
            return null;
        }
        String path = videoEntity.combineVideo;
        long leng = VideoUtils.getVideoLength(path);
        if (leng == 0)//视频为空的
            return null;
        aVideoConfig videoConfig = VideoUtils.getMediaInfor(path);
        MediaObject mediaObject = new MAsset(path);
        //获取视频的时长
        RectF rectInVideo = RecordFileUtil.getRectInVideo(videoConfig.getVideoWidth(),videoConfig.getVideoHeight(),viewWidth,viewHeight);
        KLog.i("播放item 的地址：：rectInVideo" + rectInVideo);
        mediaObject.setRectInVideo(rectInVideo);
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObject.setAudioMute(true);

//        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(), rate, 0);
        RectF clipRect = new RectF(0,0,mediaObject.getWidth(), mediaObject.getHeight());//不裁剪
        KLog.i("播放item 的地址：：clipRect" + clipRect);
        KLog.i("播放item 的地址：：TimeRange" + mediaObject.getTimeRange());
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(1);
        return mediaObject;
    }

    private static MediaObject createMediaObjectMvVideo(String path,float rate) {
        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(path)
                || !new File(path).exists())
                ) {
            return null;
        }
        long leng = VideoUtils.getVideoLength(path);
        if (leng == 0)//视频为空的
            return null;
        MediaObject mediaObject = new MAsset(path);
        //获取视频的时长
        mediaObject.setRectInVideo(new RectF(0,0,1.0f,1.0f));
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObject.setAudioMute(true);

        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(), rate, 0);
        KLog.i("播放item 的地址：：clipRect" + clipRect);
        KLog.i("播放item 的地址：：TimeRange" + mediaObject.getTimeRange());
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(1);
        return mediaObject;
    }

}
