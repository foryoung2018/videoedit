package com.wmlive.hhvideo.heihei.record.engine;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.dongci.sun.gpuimglibrary.api.DCVideoExportManager;
import com.dongci.sun.gpuimglibrary.api.DCVideoManager;
import com.dongci.sun.gpuimglibrary.api.listener.DCCutListener;
import com.dongci.sun.gpuimglibrary.api.listener.DCPlayerListener;
import com.dongci.sun.gpuimglibrary.api.listener.DCVideoListener;
import com.dongci.sun.gpuimglibrary.api.listener.DCVideosListener;
import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
import com.dongci.sun.gpuimglibrary.player.DCScene;
import com.dongci.sun.gpuimglibrary.common.SLVideoProcessor;
import com.dongci.sun.gpuimglibrary.common.CutEntity;
import com.dongci.sun.gpuimglibrary.thirdParty.mp4compose.FillMode;
import com.dongci.sun.gpuimglibrary.thirdParty.mp4compose.composer.Mp4Composer;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.VideoConfigImpl;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideosListener;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.model.TranslateModel;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频的相关操作:
 * 1.合成 2.压缩 3.裁剪 4.导出
 * 5.获取视频信息 6.获取视频长度
 * 6
 */
public class VideoEngine implements VideoEngineImpl {

    DCVideoManager dcVideoManager;

    DCVideoExportManager dcVideoExportManager;

    public void VideoEngine() {
        dcVideoManager = new DCVideoManager();
        dcVideoExportManager = getDcVideoExportManager();
    }

    private DCVideoExportManager getDcVideoExportManager() {
        if (dcVideoExportManager == null)
            dcVideoExportManager = new DCVideoExportManager();
        return dcVideoExportManager;
    }

    private DCVideoManager getDcVideoManager() {
        if (dcVideoManager == null)
            dcVideoManager = new DCVideoManager();
        return dcVideoManager;
    }

    @Override
    public void compose(ArrayList videoList, String outPath, VideoListener listener) {
        getDcVideoManager().compose(videoList, outPath, new DCVideoListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                listener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                listener.onError();
            }
        });
    }

    @Override
    public void composeAudio(ArrayList videoList, String outPath, VideoListener listener) {
        getDcVideoManager().composeAudio(videoList, outPath, new DCVideoListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                listener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                listener.onError();
            }
        });
    }

    /**
     * 同时组合 视频 和音频
     * @param audioList
     * @param outVideoPath
     * @param outAudioPath
     */
    public void composeVideoAndAudio(ArrayList<String> videoList, ArrayList<String> audioList, String outVideoPath, String outAudioPath, VideosListener listener){
        getDcVideoManager().composeVideoAndAudio(videoList, audioList,outVideoPath,outAudioPath, new DCVideosListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String... outpath) {
                listener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                listener.onError(1, "");
            }
        });
    }

    @Override
    public void mixAudio(ArrayList videoList, String outPath, VideoListener listener) {
        getDcVideoManager().mixAudio(videoList, outPath, new DCVideoListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                listener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                listener.onError();
            }
        });
    }

    @Override
    public void muxAudioVideo(ArrayList videoList, ArrayList audioList, ArrayList outPaths, VideoListener listener) {
        getDcVideoManager().composeVideoAndAudio(videoList, audioList,outPaths, new DCVideoListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                listener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                listener.onError();
            }
        });
    }



    @Override
    public void compress(String path, String outPath, final VideoListener listener) {
        getDcVideoManager().compress(path, outPath, new DCVideoListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                listener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                listener.onError();
            }
        });
    }


    private static String zhPattern = "[\u4e00-\u9fa5]+";//正则表达式，用于匹配url里面的中文



    public static String encode(String str, String charset) throws UnsupportedEncodingException {

        Pattern p = Pattern.compile(zhPattern);

        Matcher m = p.matcher(str);

        StringBuffer b = new StringBuffer();

        while (m.find()) {

            m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));

        }

        m.appendTail(b);

        return b.toString();

    }



    public void cutVideoRecord(String path, final long start, final long duration, final VideoListener listener) {
//        final String outPath = copyFile(path);
////        final String outPath1 = copyFileTest(path,"GBK");
////        final String outPath2 = copyFileTest(path,"ISO-8859-1");
//
//        path = outPath==null?path:outPath;
        try {
            path = encode(path,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        KLog.e("SLClipVideo---cutVideoRecord-pre0>"+path);
        getDcVideoManager().cut(path, start, duration, new DCVideoListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                //如果复制了
//                FileUtil.deleteFile(outPath);
                listener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                //如果复制了
//                FileUtil.deleteFile(outPath);
                listener.onError();
            }
        });
    }

    @Override
    public void cutVideo(String path, final long start, final long duration, final VideoListener listener) {
//        final String outPath = copyFile(path);
//        final String outPath1 = copyFileTest(path,"GBK");


//        path = outPath==null?path:outPath;
        getDcVideoManager().cut(path, start, duration, new DCVideoListener() {

            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                //如果复制了
//                FileUtil.deleteFile(outPath);
                listener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                //如果复制了
//                FileUtil.deleteFile(outPath);
                listener.onError();
            }

        });

    }
    /**
     * 复制文件
     * @param filePath
     */
    public String copyFileTest(String filePath,String encode){
        Uri uri = Uri.parse("file://"+filePath);
        KLog.e("SLClipVideo---inputPath-pre0>"+uri.getEncodedPath());
        String path = filePath;
        try {
            path = URLEncoder.encode(filePath,encode);
//            path = URLDecoder.decode(filePath,encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        KLog.e("SLClipVideo---inputPath0>"+path+(path!=null&& path.contains(" ")));
//        if(path!=null&& path.contains(" ")){//需要复制
            String path1 = path.replace(" ","");
            File file = new File(path1);
            KLog.e(encode+"SLClipVideo---inputPath1>"+file);
            String outPath = RecordFileUtil.createVideoFile(file.getPath());
            KLog.e("SLClipVideo---inputPath2>"+outPath);
            return outPath;
//        }
//        return null;
    }





    /**
     * 复制文件
     * @param filePath
     */
    public String copyFile(String filePath){
        String path = filePath;
        try {
            path = URLDecoder.decode(filePath,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        KLog.e("SLClipVideo---inputPath>"+path+(path!=null&& path.contains(" ")));
        if(path!=null&& path.contains(" ")){//需要复制
            File file = new File(path);
            KLog.e("SLClipVideo---inputPath0>"+file);
            String outPath = RecordFileUtil.createVideoFile(file.getPath());
            KLog.e("SLClipVideo---inputPath>"+outPath);
            return outPath;
        }
        return null;
    }


    /**
     * 裁剪多个视频
     *
     * @param list
     * @param dcCutListener
     */
    public void cutVideos(List<CutEntity> list, DCCutListener dcCutListener) {
        getDcVideoManager().cutList(list, dcCutListener);
    }

    @Override
    public void cutAudioVideos(List<CutEntity> list, VideoListener videoListener) {
        getDcVideoManager().cutAudioVideoList(list, new DCCutListener() {
            @Override
            public void onCutStart() {
                videoListener.onStart();
            }

            @Override
            public boolean onCuting(int var1) {
                videoListener.onProgress(var1);
                return false;
            }

            @Override
            public void onCutFinish(int var1, List<CutEntity> list) {
                //将裁剪后的数据更新
                if(RecordManager.get().getProductEntity()!=null){
                    for(int i=0;i<RecordManager.get().getProductEntity().shortVideoList.size();i++){
                        for(CutEntity cutEntity:list){
                            if(cutEntity.cutResult && cutEntity.path.equals(RecordManager.get().getShortVideoEntity(i).editingVideoPath)){
                                RecordManager.get().getShortVideoEntity(i).editingVideoPath = cutEntity.cutPath;
                                RecordManager.get().getShortVideoEntity(i).editingAudioPath = cutEntity.cutAudioPath;
                                KLog.e("clipvideo--length>"+cutEntity.cutPath+VideoUtils.getVideoLength(cutEntity.cutPath));
                                KLog.i("clipAudio-->"+cutEntity.cutAudioPath);
                                break;
                            }
                        }
                    }
                    RecordManager.get().updateProduct();
                }
                videoListener.onFinish(SdkConstant.RESULT_SUCCESS,"");
            }
        });
    }


    public static void transformAudio2to1(List<ShortVideoEntity> shortVideoList, VideoListener videoListener) {
        if (videoListener == null || shortVideoList.size() == 0)
            return;
        videoListener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < shortVideoList.size(); i++) {
                    if (!TextUtils.isEmpty(shortVideoList.get(i).importVideoPath)) {//本地视频

                        String outpath = transformAudio2to1(shortVideoList.get(i).importVideoPath);
                        shortVideoList.get(i).editingVideoPath = outpath;
                        shortVideoList.get(i).importVideoPath = outpath;
                    }
                    Message msg1 = handler.obtainMessage(2);
                    msg1.obj = videoListener;
                    msg1.arg1 = 0;
                    msg1.arg2 = i + 1;
                    handler.sendMessage(msg1);
                }
                RecordManager.get().updateProduct();
                Message msg = handler.obtainMessage(2);
                msg.obj = videoListener;
                msg.arg1 = 1;
                msg.arg2 = SdkConstant.RESULT_SUCCESS;
                handler.sendMessage(msg);

            }
        }).start();

    }

    /**
     * 将视频的 双音道 变成单轨
     * 针对 格子的单视频
     *
     * @param path
     */
    public static String transformAudio2to1(String path) {
        if (path == null) {
            return null;
        }
        String[] temp = path.split(File.separator);
        String basePath = "";
        for (int i = 0; i < temp.length - 1; i++) {
            basePath = basePath + temp[i] + File.separator;
        }
        try {
            KLog.i(path + "outPath---pre>");
            DCMediaInfoExtractor.MediaInfo mediaInfo = DCMediaInfoExtractor.extractAudio(path);
            String outPath = RecordFileUtil.createVideoFile(basePath);
            KLog.i(path + "outPath--->" + outPath);
            if (mediaInfo.audioInfo.channelCount != 1 || mediaInfo.audioInfo.sampleRate != 44100) {
                boolean b = SLVideoProcessor.getInstance().transcodeAudio(path, 44100, 1, 96000, outPath);

                if (b)
                    return outPath;
                else {
                    ToastUtil.showToast("无法使用该视频");
                    return "";
                }
            } else {//不需要处理
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

//    public static void rotate(List<ShortVideoEntity> shortVideoList, VideoListener videoListener) {
//        if (videoListener == null)
//            return;
//        videoListener.onStart();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < shortVideoList.size(); i++) {
//                    if (shortVideoList.get(i).editingVideoPath != null) {//本地视频
////                        String outpath = rotate(shortVideoList.get(i).editingVideoPath);
////                        shortVideoList.get(i).editingVideoPath = outpath;
////                        rotateNew();
//
//                    }
//
//                    Message msg1 = handler.obtainMessage(1);
//                    msg1.obj = videoListener;
//                    msg1.arg1 = 0;
//                    msg1.arg2 = i + 1;
//                    handler.sendMessage(msg1);
//                }
//                RecordManager.get().updateProduct();
//                Message msg = handler.obtainMessage(1);
//                msg.obj = videoListener;
//                msg.arg1 = 1;
//                handler.sendMessage(msg);
//
//            }
//        }).start();
//
//    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://旋转
                    if(msg.arg1 == 2){
                        ((VideoListener) msg.obj).onStart();
                    }else if (msg.arg1 == 1) {//结束
                        if(msg.arg2 == SdkConstant.RESULT_SUCCESS){
                            Bundle bundle = msg.getData();
                            String path = bundle.getString("url");
                            ((VideoListener) msg.obj).onFinish(msg.arg2, path);
                        }else {
                            ((VideoListener) msg.obj).onError();
                        }
                    } else if (msg.arg1 == 0) {//正在处理中
                        ((VideoListener) msg.obj).onProgress(msg.arg2);
                    }
                    break;
                case 2://2 ->1
                    if (msg.arg1 == 1) {//结束
                        ((VideoListener) msg.obj).onFinish(SdkConstant.RESULT_SUCCESS, "");
                    } else if (msg.arg1 == 0) {
                        ((VideoListener) msg.obj).onProgress(msg.arg2);
                    }
                    break;
            }
        }
    };

    /**
     * 将视频旋转
     * 内部方法
     */
    public static String rotate(String path) {
        if (path == null) {
            return null;
        }
        KLog.i("videoEngine-->rotate" + path);
        String[] temp = path.split(File.separator);
        String basePath = "";
        for (int i = 0; i < temp.length - 1; i++) {
            basePath = basePath + temp[i] + File.separator;
        }
        String outPath = RecordFileUtil.createVideoFile(basePath);

        DCMediaInfoExtractor.MediaInfo mediaInfo = null;
        try {
            mediaInfo = DCMediaInfoExtractor.extract(path);
            KLog.i("videoEngine-->rotation" + mediaInfo.videoInfo.rotation);
            if (mediaInfo.videoInfo.rotation != 0) {
                SLVideoProcessor.getInstance().rotateVideo(path, outPath);
                DCMediaInfoExtractor.MediaInfo mediaInfo1 = DCMediaInfoExtractor.extract(outPath);
                KLog.i(mediaInfo1.videoInfo.rotation + "videoEngine-->out" + outPath);
                return outPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 新的旋转
     */
    public static void rotateNew(String path,String outPath,VideoListener videoListener){
        //start
        Message msg1 = handler.obtainMessage(1);
        msg1.obj = videoListener;
        msg1.arg1 = 2;
        handler.sendMessage(msg1);
        new Mp4Composer(path, outPath)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .listener(new Mp4Composer.Listener(){

                    @Override
                    public void onProgress(double progress) {
                        Message msg1 = handler.obtainMessage(1);
                        msg1.obj = videoListener;
                        msg1.arg1 = 0;
                        msg1.arg2 = (int)(progress*100);
                        handler.sendMessage(msg1);
                    }



                    @Override
                    public void onCompleted() {
                        Message msg = handler.obtainMessage(1);
                        msg.obj = videoListener;
                        msg.arg1 = 1;
                        msg.arg2 = SdkConstant.RESULT_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putString("url",outPath);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
//                        listener.onFinish(SdkConstant.RESULT_SUCCESS,outPath);
                    }

                    @Override
                    public void onCanceled() {
                        Message msg = handler.obtainMessage(1);
                        msg.obj = videoListener;
                        msg.arg1 = 1;
                        msg.arg2 = SdkConstant.RESULT_SAVE_CANCEL;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        KLog.e("rotate-->failed"+exception);
                        Message msg = handler.obtainMessage(1);
                        msg.obj = videoListener;
                        msg.arg1 = 1;
                        msg.arg2 = SdkConstant.RESULT_CORE_ERROR_ENCODE_VIDEO;
                        handler.sendMessage(msg);
                    }
                })
                .start();
    }



    /**
     * sdk 提供 视频导出方法
     *
     * @param list
     * @param videoConfig
     * @param exportListener
     */
    @Override
    public void export(List<Scene> list, VideoConfigImpl videoConfig, ExportListener exportListener) {
        if (list == null) {
            KLog.e("没视频导出鸡毛");
            return;
        }
        List<DCScene> dcScenes = TranslateModel.scenceToDC(list);

        DCMediaInfoExtractor.MediaInfo mediaInfo = ((MVideoConfig) videoConfig).getMediaInfo();
//        mediaInfo.audioInfo = null;//只有视频

        getDcVideoExportManager().export(dcScenes, mediaInfo, new DCPlayerListener() {
            @Override
            public void onPrepared() {
                KLog.d("export--onPrepared>");
                exportListener.onExportStart();
            }

            @Override
            public void onProgress(float progress) {
                KLog.d("export--onProgress>" + progress);
                exportListener.onExporting((int) progress, 0);
            }

            @Override
            public void onComplete() {
                KLog.d("export--onComplete>");
//                releaseExport();
                exportListener.onExportEnd(SdkConstant.RESULT_SUCCESS, ((MVideoConfig) videoConfig).getFilePath());
            }

            @Override
            public boolean onPlayerError(int var1, int var2) {
                KLog.d("export--onPlayerError>");
//                releaseExport();
                return false;
            }
        });
    }

    @Override
    public void splitVideoAudio(List<CutEntity> list, VideoListener videoListener) {
        getDcVideoManager().splitVideoAudio(list, new DCCutListener() {

            @Override
            public void onCutStart() {
                videoListener.onStart();
            }

            @Override
            public boolean onCuting(int var1) {
                videoListener.onProgress(var1);
                return false;
            }

            @Override
            public void onCutFinish(int var1, List<CutEntity> list) {
                if(var1==SdkConstant.RESULT_SUCCESS){
                    if(RecordManager.get().getProductEntity().shortVideoList==null){
                        ToastUtil.showToast("数据错误，请重试");
                        return;
                    }

                    for(int i=0;i<RecordManager.get().getProductEntity().shortVideoList.size();i++){
                        for(CutEntity cutEntity:list){
                            KLog.i(cutEntity.cutResult+"split--finish-pre0"+cutEntity.path);
                            KLog.i(cutEntity.cutResult+"split--finish-pre1"+RecordManager.get().getShortVideoEntity(i).combineVideoAudio);
                            if(cutEntity.cutResult && cutEntity.path.equals(RecordManager.get().getShortVideoEntity(i).combineVideoAudio)){
                                RecordManager.get().getShortVideoEntity(i).editingVideoPath = cutEntity.cutPath;
                                RecordManager.get().getShortVideoEntity(i).editingAudioPath = cutEntity.cutAudioPath;
                                KLog.i("split--finish"+cutEntity.cutAudioPath);
                                break;
                            }
                        }
                    }
                    RecordManager.get().updateProduct();
                }
                videoListener.onFinish(var1,"");            }
        });
    }

    @Override
    public void splitVideoAudio(String video, String outVideo, String outAudio, VideoListener videoListener) {
        List<CutEntity> list = new ArrayList<CutEntity>(1);
        CutEntity cutEntity = new CutEntity();
        cutEntity.path = video;
        cutEntity.cutAudioPath = outAudio;
        cutEntity.cutPath = outVideo;
        list.add(cutEntity);
        splitVideoAudio(list,videoListener);
    }

    /**
     * 设置音量
     * @param list
     * @param videoListener
     */
    @Override
    public void setVolume(List<CutEntity> list, VideoListener videoListener) {
        KLog.i("音量调整后--pre>"+list.size());
        DCVideoManager.setAudioVolume(list, new DCCutListener() {
            @Override
            public void onCutStart() {
                videoListener.onStart();
            }

            @Override
            public boolean onCuting(int var1) {
                videoListener.onProgress(var1);
                return false;
            }

            @Override
            public void onCutFinish(int var1, List<CutEntity> list) {
                KLog.i("音量调整后--finish>"+var1+"cutSize:>"+list.size());
                if(var1==SdkConstant.RESULT_SUCCESS){
                    for(int i=0;i<RecordManager.get().getProductEntity().shortVideoList.size();i++){
                        for(CutEntity cutEntity:list){
                            if(cutEntity.cutResult && cutEntity.audioPath.equals(RecordManager.get().getShortVideoEntity(i).editingAudioPath)){
                                RecordManager.get().getShortVideoEntity(i).editingAudioPath = cutEntity.cutAudioPath;
                                KLog.i("音量调整后-->"+cutEntity.volume+cutEntity.cutAudioPath);
                            }
                        }
                    }
                    RecordManager.get().updateProduct();
                }
                videoListener.onFinish(var1,"");
            }
        });
    }

    /**
     * 设置音量
     * @param list
     * @param videoListener
     */
    public void setVolumeWithNoSet(List<CutEntity> list, VideosListener videoListener) {
        KLog.i("音量调整后--pre>"+list.size());
        DCVideoManager.setAudioVolume(list, new DCCutListener() {
            @Override
            public void onCutStart() {
                videoListener.onStart();
            }

            @Override
            public boolean onCuting(int var1) {
                videoListener.onProgress(var1);
                return false;
            }

            @Override
            public void onCutFinish(int var1, List<CutEntity> list) {
                KLog.i("音量调整后--finish>"+var1+"cutSize:>"+list.size());
                String[] s = null;
                if(var1==SdkConstant.RESULT_SUCCESS){
                    int count = list.size();
                    s = new String[count];
                    for(int i=0;i<count;i++){
                        CutEntity cutEntity = list.get(i);
//                        if( cutEntity.audioPath.equals(RecordManager.get().getShortVideoEntity(i).editingAudioPath)){
                            s[i] = cutEntity.cutAudioPath;
                            KLog.i("音量调整后-->"+cutEntity.volume+cutEntity.cutAudioPath);
//                        }
                    }
                }

                videoListener.onFinish(var1,s);
            }
        });
    }

    /**
     * 根据地址,获取视频的信息
     *
     * @param path
     * @param videoConfig
     */
    public static void getMediaInfo(String path, MVideoConfig videoConfig) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        try {
            retr.setDataSource(path);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        int width = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); // 视频宽度
        int height = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); // 视频高度
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向


        videoConfig.setVideoSize(width, height);
        if (retr != null) {
            retr.release();
        }
    }
}
