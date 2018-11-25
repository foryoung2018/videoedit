package com.wmlive.hhvideo.heihei.record.engine.content;

import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.text.TextUtils;

import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建  播放内容
 * 创建Scence
 */
public class ExportContentFactory {

    /**
     * 导出 合并后的视频+ 水印
     * @param productEntity
     * @return
     */
    public static List<MediaObject> getWaterMedias(ProductEntity productEntity){
        List<MediaObject> meidas = new ArrayList<MediaObject>();
        if (productEntity == null || productEntity.shortVideoList == null) {
            return null;
        }
        KLog.i("combineVideoAudio-water->"+productEntity.combineVideoAudio);
        MediaObject mediaObject = createComposedMedia(productEntity.combineVideoAudio);
        if(mediaObject!=null)
            meidas.add(mediaObject);

        return meidas;
    }

    /**
     * 导出所有的视频资源
     * @param productEntity
     * @return
     */
    public static List<MediaObject> getAllMedias(ProductEntity productEntity){
        List<MediaObject> meidas = new ArrayList<MediaObject>();
        List<MediaObject> bgs = new ArrayList<MediaObject>();
        if (productEntity == null || productEntity.shortVideoList == null) {
            return null;
        }

        //添加画框
        for(int i=0;i<productEntity.shortVideoList.size();i++){
            ShortVideoEntity videoEntity = productEntity.shortVideoList.get(i);
            //左侧上传没有 frameinfo 信息
            float videoRatio = 0;

            if(productEntity.frameInfo==null){//左侧上传
                aVideoConfig videoConfig =  VideoUtils.getMediaInfor(videoEntity.editingVideoPath);
                videoRatio = videoConfig.getVideoWidth()*1f/videoConfig.getVideoHeight()*1f;
            }
            RectF rectF = productEntity.frameInfo==null?new RectF(0,0,1,1):productEntity.frameInfo.getLayoutRelativeRectF(i);
            float ratio = productEntity.frameInfo==null?videoRatio:productEntity.frameInfo.getLayoutAspectRatio(i);
            MediaObject mediaObject = createAllMediaObject(videoEntity,rectF,ratio);

            if(mediaObject!=null){
                mediaObject.assetId = i+1;
                meidas.add(mediaObject);
            }else if(productEntity.frameInfo!=null){
                //添加背景

                MediaObject mediaObjectBg = createBgObject(RecordFileUtil.getFrameImagePath(productEntity.frameInfo.publish_image),productEntity.frameInfo.getLayoutRelativeRectF(i));
                if(mediaObjectBg!=null){
                    mediaObjectBg.assetId = productEntity.shortVideoList.size()+i;
                    bgs.add(mediaObjectBg);
                }

            }
        }
        meidas.addAll(bgs);
        return meidas;
    }

    /**
     * 导出所有的视频资源
     * @param videoPath
     * @return
     */
    public static MediaObject getLocalUpload(String videoPath){
        if (TextUtils.isEmpty(videoPath)) {
            return null;
        }
        if(VideoUtils.getVideoLength(videoPath)==0)
            return null;
        //左侧上传没有 frameinfo 信息
        float videoRatio = 0;

        aVideoConfig videoConfig = VideoUtils.getMediaInfor(videoPath);
        videoRatio = videoConfig.getVideoWidth() * 1f / videoConfig.getVideoHeight() * 1f;

        RectF rectF = new RectF(0, 0, 1, 1);
        MediaObject mediaObject = createMediaBase(videoPath, rectF, videoRatio,1.0f);
//
        if (mediaObject != null) {
            mediaObject.assetId = 1;
        }
        return mediaObject;
    }

    /**
     * 创建单个 object，只创建录制的视频，本地导入的视频，直接忽略
     * @param videoEntity
     * @param frameRecf
     * @param frameRatio
     * @return
     */
    public static MediaObject createRecordMediaObject(ShortVideoEntity videoEntity, RectF frameRecf,float frameRatio){
        if (videoEntity == null
                || TextUtils.isEmpty(videoEntity.editingVideoPath)
                || !new File(videoEntity.editingVideoPath).exists()) {
            //当前 框无可以编辑的文件
            return null;
        }
        MediaObject mediaObject = new MAsset(videoEntity.editingVideoPath);
        KLog.i("播放item 的地址：" + videoEntity.editingVideoPath);
        KLog.i("播放item 的地址：showRect" + frameRecf);
        //获取视频的时长
        mediaObject.setRectInVideo(frameRecf);
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
//        mediaObject.setAudioMute(true);
        float[] trimRange = videoEntity.getTrimRange();
        float trimDuration = videoEntity.getDuring();
        float voiceStartTime = 0;
        //视频裁剪,
        if ((trimRange[0] != 0 || trimRange[1] != 0)) {
            mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
            voiceStartTime = trimRange[0];
            trimDuration = trimRange[1] - trimRange[0]; // 截取时长
        }
        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(),mediaObject.getWidth()*1.0f/mediaObject.getHeight()*1.0f, 0);
        KLog.i("createMediaObject：frameRatio" + frameRatio+"clipRect-->"+clipRect);
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(videoEntity.volume);
        return mediaObject;
    }


    /**
     * 创建合并资源
     * @return
     */
    public static MediaObject createComposedMedia(String videoPath){
        KLog.i("publish_img----11>"+videoPath);
//        videoEntity./*coverUrl*/
        if ( TextUtils.isEmpty(videoPath)
                || !new File(videoPath).exists()) {
            //当前 框无可以编辑的文件
            return null;
        }
        MediaObject mediaObject = new MAsset(videoPath);
        mediaObject.setSourceType(DCAsset.DCAssetTypeVideo);
//        mediaObject.setTimeRange(0,60000000);
        mediaObject.setStartTimeInScene(0);

        mediaObject.setRectInVideo(new RectF(0,0,1,1));

        mediaObject.setShowRectF(new RectF(0,0,mediaObject.getWidth(),mediaObject.getHeight()));
        mediaObject.setVolume(1.0f);
        //
        return mediaObject;
    }

    /**
     * 创建背景图
     * @return
     */
    public static MediaObject createBgObject(String imgPath,RectF rectF){
        KLog.i("publish_img----11>"+imgPath);
//        videoEntity./*coverUrl*/
        if ( TextUtils.isEmpty(imgPath)
                || !new File(imgPath).exists()) {
            //当前 框无可以编辑的文件
            return null;
        }
        MediaObject mediaObject = new MAsset(imgPath,DCAsset.DCAssetTypeImage);
        mediaObject.setSourceType(DCAsset.DCAssetTypeImage);
        mediaObject.setTimeRange(0,60000000);
        mediaObject.setStartTimeInScene(0);

        mediaObject.setRectInVideo(rectF);
        BitmapFactory.Options options = getImgSize(imgPath);
        mediaObject.setShowRectF(new RectF(rectF.left*options.outWidth,rectF.top*options.outHeight,rectF.right*options.outWidth,rectF.bottom*options.outHeight));
        //
        return mediaObject;
    }



    /**
     * 创建本地和录制的mediaobject
     * @param videoEntity
     * @param frameRecf
     * @param frameRatio
     * @return
     */
    public static MediaObject createAllMediaObject(ShortVideoEntity videoEntity, RectF frameRecf,float frameRatio){
        if (videoEntity == null) {
            //当前 框无可以编辑的文件
            return null;
        }
        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(videoEntity.editingVideoPath)
                || !new File(videoEntity.editingVideoPath).exists()) &&
                (TextUtils.isEmpty(videoEntity.importVideoPath)
                        || !new File(videoEntity.importVideoPath).exists())) {
            return null;
        }
        String path = null;
        if(videoEntity.isImport()){//如果是 本地导入
            path = TextUtils.isEmpty(videoEntity.importVideoPath)?videoEntity.editingVideoPath:videoEntity.importVideoPath;
        }else{
            path = TextUtils.isEmpty(videoEntity.editingVideoPath)?videoEntity.importVideoPath:videoEntity.editingVideoPath;
        }
        if(VideoUtils.getVideoLength(path)==0)//视频没有
            return null;
//        String path = TextUtils.isEmpty(videoEntity.editingVideoPath)?videoEntity.importVideoPath:videoEntity.editingVideoPath;
        MediaObject mediaObject = new MAsset(path);
        KLog.i("播放item 的地址editingVideoPath：" + videoEntity.editingVideoPath);
//        KLog.i("播放item 的地址importVideoPath：" + videoEntity.importVideoPath);
        KLog.i("播放item 的地址：" + path+ VideoUtils.getVideoLength(path));
        KLog.i("播放item 的地址：showRect" + frameRecf);
        //获取视频的时长
        mediaObject.setRectInVideo(frameRecf);
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObject.setAudioMute(true);
//        float[] trimRange = videoEntity.getTrimRange();
//        float trimDuration = videoEntity.getDuring();
//        float voiceStartTime = 0;
//        //视频裁剪,
//        if ((trimRange[0] != 0 || trimRange[1] != 0)) {
//            mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
//            voiceStartTime = trimRange[0];
//            trimDuration = trimRange[1] - trimRange[0]; // 截取时长
//        }
        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(),frameRatio, 0);
        KLog.i("createMediaObject：volume" + videoEntity.volume);
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(videoEntity.volume);
        return mediaObject;
    }

    public static MediaObject createMediaBase(String path, RectF frameRecf,float frameRatio,float volume){

        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(path)
                || !new File(path).exists())) {
            return null;
        }

        if(VideoUtils.getVideoLength(path)==0)//视频没有
            return null;
        MediaObject mediaObject = new MAsset(path);
        KLog.i("播放item 的地址：" + path+ VideoUtils.getVideoLength(path));
        KLog.i("播放item 的地址：showRect" + frameRecf);
        //获取视频的时长
        mediaObject.setRectInVideo(frameRecf);
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObject.setAudioMute(true);
        aVideoConfig config = VideoUtils.getMediaInfor(path);
        RectF clipRect = null;
        if(config.rotation==0){
            clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(),frameRatio, 0);
        }else {
            clipRect = RecordFileUtil.getClipSrc(mediaObject.getHeight(), mediaObject.getWidth(),1.0f/frameRatio, 0);
        }
        KLog.i(frameRatio+"播放item 的地址：setShowRectF" + clipRect);
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(volume);
        return mediaObject;
    }



    public static MediaObject createAllMediaObjectNew(ShortVideoEntity videoEntity, RectF frameRecf,float frameRatio){
        if (videoEntity == null) {
            //当前 框无可以编辑的文件
            return null;
        }
        // 编辑视频，本地导入视频， 都不存在
        if ((TextUtils.isEmpty(videoEntity.editingVideoPath)
                || !new File(videoEntity.editingVideoPath).exists()) &&
                (TextUtils.isEmpty(videoEntity.importVideoPath)
                        || !new File(videoEntity.importVideoPath).exists())) {
            return null;
        }
        String path = videoEntity.editingVideoPath;
//        if(videoEntity.isImport()){//如果是 本地导入
//            path = TextUtils.isEmpty(videoEntity.importVideoPath)?videoEntity.editingVideoPath:videoEntity.importVideoPath;
//        }else{
//            path = TextUtils.isEmpty(videoEntity.editingVideoPath)?videoEntity.importVideoPath:videoEntity.editingVideoPath;
//        }
//        String path = TextUtils.isEmpty(videoEntity.editingVideoPath)?videoEntity.importVideoPath:videoEntity.editingVideoPath;
        MediaObject mediaObject = new MAsset(path);
        KLog.i("播放item 的地址editingVideoPath：" + videoEntity.editingVideoPath);
        KLog.i("播放item 的地址importVideoPath：" + videoEntity.importVideoPath);
        KLog.i("播放item 的地址：" + path+ VideoUtils.getVideoLength(path));
        KLog.i("播放item 的地址：showRect" + frameRecf);
        //获取视频的时长
        mediaObject.setRectInVideo(frameRecf);
        mediaObject.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        mediaObject.setAudioMute(true);
//        float[] trimRange = videoEntity.getTrimRange();
//        float trimDuration = videoEntity.getDuring();
//        float voiceStartTime = 0;
//        //视频裁剪,
//        if ((trimRange[0] != 0 || trimRange[1] != 0)) {
//            mediaObject.setTimeRange(trimRange[0], trimRange[1]); //设置media时长
//            voiceStartTime = trimRange[0];
//            trimDuration = trimRange[1] - trimRange[0]; // 截取时长
//        }
        RectF clipRect = RecordFileUtil.getClipSrc(mediaObject.getWidth(), mediaObject.getHeight(),frameRatio, 0);
        KLog.i("createMediaObject：volume" + videoEntity.volume);
        mediaObject.setShowRectF(clipRect);
        mediaObject.setVolume(videoEntity.volume);
        return mediaObject;
    }

    /**
     * 该作品 获取导出素材, 本地视频不需要导出，只导出录制视频
     *
     * @param productEntity
     * @return
     */
    public static List<MediaObject> getRecordMedias(ProductEntity productEntity){
        List<MediaObject> meidas = new ArrayList<MediaObject>();
        if (productEntity == null || productEntity.shortVideoList == null) {
            return null;
        }
        //添加画框
        for(int i=0;i<productEntity.shortVideoList.size();i++){
            ShortVideoEntity videoEntity = productEntity.shortVideoList.get(i);
            MediaObject mediaObject = createRecordMediaObject(videoEntity,productEntity.frameInfo.getLayoutRelativeRectF(i),productEntity.frameInfo.getLayoutAspectRatio(i));
            if(mediaObject!=null)
                meidas.add(mediaObject);
        }
        return meidas;
    }



//    /**
//     * 导出所有的视频资源
//     * @param productEntity
//     * @return
//     */
//    public static List<MediaObject> getAllMedias(ProductEntity productEntity){
//        List<MediaObject> meidas = new ArrayList<MediaObject>();
//        List<MediaObject> bgs = new ArrayList<MediaObject>();
//        if (productEntity == null || productEntity.shortVideoList == null) {
//            return null;
//        }
//
//        //添加画框
//        for(int i=0;i<productEntity.shortVideoList.size();i++){
//            ShortVideoEntity videoEntity = productEntity.shortVideoList.get(i);
////            MediaObject mediaObject = createAllMediaObjectNew(videoEntity,productEntity.frameInfo.getLayoutRelativeRectF(i),productEntity.frameInfo.getLayoutAspectRatio(i));
//            //左侧上传没有 frameinfo 信息
//            float videoRatio = 0;
//
//            if(productEntity.frameInfo==null){//左侧上传
//                aVideoConfig videoConfig =  VideoUtils.getMediaInfor(videoEntity.editingVideoPath);
//                videoRatio = videoConfig.getVideoWidth()*1f/videoConfig.getVideoHeight()*1f;
//            }
//            RectF rectF = productEntity.frameInfo==null?new RectF(0,0,1,1):productEntity.frameInfo.getLayoutRelativeRectF(i);
//            float ratio = productEntity.frameInfo==null?videoRatio:productEntity.frameInfo.getLayoutAspectRatio(i);
//            MediaObject mediaObject = createAllMediaObject(videoEntity,rectF,ratio);
//
//            if(mediaObject!=null)
//                meidas.add(mediaObject);
//            else{
//                //添加背景
//                if(productEntity.frameInfo!=null){
//                    MediaObject mediaObjectBg = createBgObject(RecordFileUtil.getFrameImagePath(productEntity.frameInfo.publish_image),productEntity.frameInfo.getLayoutRelativeRectF(i));
//                    if(mediaObjectBg!=null)
//                        bgs.add(mediaObjectBg);
//                }
//
//            }
//        }
//        meidas.addAll(bgs);
//        return meidas;
//    }


    /**
     * 获取图片宽高
     * @param url
     * @return
     */
    public  static BitmapFactory.Options getImgSize(String url){
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile(url,options);
        return options;
    }
}
