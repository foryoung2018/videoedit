package com.dongci.sun.gpuimglibrary.api;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.api.listener.DCCutListener;
import com.dongci.sun.gpuimglibrary.api.listener.DCVideoListener;
import com.dongci.sun.gpuimglibrary.api.listener.DCVideosListener;
import com.dongci.sun.gpuimglibrary.common.FileUtils;
import com.dongci.sun.gpuimglibrary.api.apiTest.KLog;
import com.dongci.sun.gpuimglibrary.common.SLClipVideo;
import com.dongci.sun.gpuimglibrary.common.SLVideoComposer;
import com.dongci.sun.gpuimglibrary.common.SLVideoCompressor1;
import com.dongci.sun.gpuimglibrary.common.SLVideoProcessor;
import com.dongci.sun.gpuimglibrary.common.CutEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DCVideoManager {

    /**
     * 视频合成
     */
    public void compose(final ArrayList videoList, final String outPath, final DCVideoListener listener) {
        listener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SLVideoComposer composer = new SLVideoComposer(videoList, outPath);
                boolean result = composer.joinVideo();
                sendFinish(result, listener, outPath);

            }
        }).start();
    }

    /**
     * 同时拼接视频，音频
     *
     * @param videoList
     * @param outVideoPath
     * @param outPathAudio
     * @param listener
     */
    public void composeVideoAndAudio(final ArrayList<String> videoList, final ArrayList<String> audioList, final String outVideoPath, final String outPathAudio, final DCVideosListener listener) {
        listener.onStart();
        if(videoList.size()==0 || audioList.size()==0){
            listener.onFinish(-1, "", "");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SLVideoProcessor composer = SLVideoProcessor.getInstance();
                boolean result = composer.concatAudios(audioList, outPathAudio);

                //拼接视频
                SLVideoComposer composer1 = new SLVideoComposer(videoList, outVideoPath);
                boolean result1 = composer1.joinVideo();

                sendFinishVideoAudio(result && result1, listener, outVideoPath, outPathAudio);

            }
        }).start();
    }

    /**
     * 音频连接成一个长的音频
     */
    public void composeAudio(final ArrayList videoList, final String outPath, final DCVideoListener listener) {
        listener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SLVideoProcessor composer = SLVideoProcessor.getInstance();
                boolean result = composer.concatAudios(videoList, outPath);
                sendFinish(result, listener, outPath);

            }
        }).start();
    }

    /**
     * 音频 混合
     *
     * @param videoList
     * @param outPath
     * @param listener
     */
    public void mixAudio(final ArrayList videoList, final String outPath, final DCVideoListener listener) {
        listener.onStart();
        Log.i("mixAudio","mixAudio--before-pre>" + outPath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SLVideoProcessor composer = SLVideoProcessor.getInstance();
                Log.i("mixAudio","before"+ new File(outPath).exists());
                KLog.i("mixAudio--before>" + new File(outPath).exists());
                boolean result = composer.mixAudios(videoList, outPath);
                Log.i("mixAudio","mixAudio-->" + result);
                sendFinish(result, listener, outPath);
            }
        }).start();
    }

    /**
     * 同时操作
     *
     * @param code
     * @param listener
     * @param outPath
     */
    private void sendFinishVideoAudio(boolean code, DCVideosListener listener, String... outPath) {
        ListenerObjects o = new ListenerObjects();
        o.code = code ? 1 : 0;
        o.listener = listener;
        o.outPath = outPath[0];
        o.outPath2 = outPath[1];
        Message msg = new Message();
        msg.what = 1;
        msg.obj = o;
        handlers.sendMessage(msg);
    }

    /**
     * 处理多条返回数据
     */
    Handler handlers = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            ListenerObjects o = (ListenerObjects) msg.obj;
            if (msg.what == 1) {//操作結束
                o.listener.onFinish(o.code, o.outPath, o.outPath2);
            } else if (msg.what == 0) {//正在進行
                o.listener.onProgress(o.code);
            }

        }
    };


    private void sendFinish(boolean code, DCVideoListener listener, String... outPath) {
        ListenerObject o = new ListenerObject();
        o.code = code ? 1 : 0;
        o.listener = listener;
        o.outPath = outPath[0];
        Message msg = new Message();
        msg.what = 1;
        msg.obj = o;
        handler.sendMessage(msg);
        Log.i("mixAudio","mixAudio--sendFinish>" + code);
    }

    /**
     * 正在处理中
     *
     * @param code
     * @param listener
     */
    private void sendProgress(int code, DCVideoListener listener) {
        ListenerObject o = new ListenerObject();
        o.code = code;
        o.listener = listener;
        Message msg = new Message();
        msg.what = 0;
        msg.obj = o;
        handler.sendMessage(msg);
    }


    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            ListenerObject o = (ListenerObject) msg.obj;
            if (msg.what == 1) {//操作結束
                o.listener.onFinish(o.code, o.outPath);
            } else if (msg.what == 0) {//正在進行
                o.listener.onProgress(o.code);
            }

        }
    };

    /**
     * 视频压缩
     * 返回ui 线程
     */
    public void compress(String path, String outPath, final DCVideoListener listener) {
        SLVideoCompressor1.compressVideo(path, outPath, new SLVideoCompressor1.onCompressCompleteListener() {
            @Override
            public void onComplete(boolean compressed, String outPath) {
//                listener.onFinish(compressed?1:0,outPath);
                sendFinish(compressed, listener, outPath);
            }
        }, new SLVideoCompressor1.OnVideoProgressListener() {
            @Override
            public void progress(int progress) {
//                listener.onProgress(progress);

                ListenerObject o = new ListenerObject();
                o.code = progress;
                o.listener = listener;
                Message msg = new Message();
                msg.what = 0;
                msg.obj = o;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 视频裁剪w
     * start ： 微秒
     * duration : 微秒
     */
    public void cutRecord(final String path, final long start, final DCVideoListener listener) {
        listener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("tag", "=====onRecordEnd:--cut-before-cut" + start);
                boolean result = new SLClipVideo().clipVideoToEndRecord(path, start);
                String outPath = (path.substring(0, path.lastIndexOf(".")) + "_output.mp4").trim();
//                listener.onFinish(result?1:0,outPath);
                sendFinish(result, listener, outPath);
            }
        }) {
        }.start();
    }

    /**
     * 传进来  纳秒
     *
     * @param path
     * @param outPath
     * @param start
     * @param duration
     * @param listener
     */
    public void cutAudio(final String path, final String outPath, final long start, final long duration, final float volume, final DCVideoListener listener) {
        listener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("tag", "=====onRecordEnd:--cut-detail>" + start + "duration->" + duration + "outPath");
                Log.i("tag", "=====onRecordEnd:--cut-detail>outPath" + outPath);
                boolean result = SLVideoProcessor.getInstance().trimAudio(path, start * 1.0f / 1000000000 * 1.0f, duration * 1.0f / 1000000000 * 1.0f, volume, outPath);
                sendFinish(result, listener, outPath);
            }
        }) {
        }.start();
    }

    /**
     * 同时裁剪 视频 音频
     *
     * @param path
     * @param outPath
     * @param start
     * @param duration
     * @param volume
     * @param listener
     */
    public void cutVideoAndAudio(final String path, final String outPath, final long start, final long duration, final float volume, final DCVideoListener listener) {
        listener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //裁剪视频
                Log.i("tag", "=====onRecordEnd:--cut-before-cut" + start);
                boolean result = new SLClipVideo().clipVideoToEndRecord(path, start);
                String outVideoPath = (path.substring(0, path.lastIndexOf(".")) + "_output.mp4").trim();
//                listener.onFinish(result?1:0,outPath);
//                sendFinish(result,listener,outPath);
                //裁剪音频
                boolean result1 = SLVideoProcessor.getInstance().trimAudio(path, start * 1.0f / 1000000000 * 1.0f, duration * 1.0f / 1000000000 * 1.0f, volume, outPath);
                sendFinish(result1, listener, outPath);
            }
        }) {
        }.start();
    }


    public void splitVideoAudio(final List<CutEntity> mediaObjects, final DCCutListener listener) {
        listener.onCutStart();
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (CutEntity cutEntity : mediaObjects) {
                    KLog.i("splite-before->" + cutEntity.path);
                    KLog.i("splite-after-1>" + cutEntity.cutAudioPath);
                    KLog.i("splite-after-2>" + cutEntity.cutPath);
                    boolean b1 = SLVideoProcessor.getInstance().extractAudio(cutEntity.path, cutEntity.cutAudioPath);
                    boolean b2 = SLVideoProcessor.getInstance().extractVideo(cutEntity.path, cutEntity.cutPath);
                    cutEntity.cutResult = b1 && b2;
                }
                listener.onCutFinish(1, mediaObjects);
            }
        }).start();
    }


    /**
     * 合并音视频
     * 合并 结果 在out 里面
     */
    public void composeVideoAndAudio(final List<String> videos, final List<String> audios, final List<String> outs, final DCVideoListener listener) {
        listener.onStart();
        if (videos.size() == audios.size() && audios.size() == outs.size()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < videos.size(); i++) {
                        KLog.i("composeVideoAndAudio---->videos:-" + videos.get(i));
                        KLog.i("composeVideoAndAudio---->audios:-" + audios.get(i));
                        boolean result = SLVideoProcessor.getInstance().mux(videos.get(i), audios.get(i), outs.get(i));
                        sendProgress(i + 1, listener);
                        KLog.i("composeVideoAndAudio---->result:-" + result);
//                        if(!result) {//合并失败
//                            outs.set(i, null);
//                        }
                    }


                    sendFinish(true, listener, "");
                }
            }) {
            }.start();
        } else {
            KLog.e("composeVideoAndAudio-failed->" + videos.size() + "audio>" + audios.size() + "out:>" + outs);
            listener.onError();
        }

    }

    /**
     * 设置音频的音量
     *
     * @param audios
     * @param audiosVolume
     * @param outs
     * @param listener
     */
    public void setAudioVolume(final List<String> audios, final List<Float> audiosVolume, final List<String> outs, final DCVideoListener listener) {
        listener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < audios.size(); i++) {
                    boolean result = SLVideoProcessor.getInstance().setVolume(audios.get(i), audiosVolume.get(i), outs.get(i));
                    if (!result) {//合并失败
                        outs.set(i, null);
                    }
                }
                KLog.i("dialog---->composeVideoAndAudio-");
                sendFinish(true, listener, "");
            }
        }) {
        }.start();
    }

    /**
     * 视频裁剪w
     * start ： 微秒
     * duration : 微秒
     */
    public void cutRecord(final String path, final long start, final long duration, final DCVideoListener listener) {
        listener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = new SLClipVideo().clipVideo(path, start, duration);
                Log.d("sun", "clipVideo---result>" + result);
                String outPath = (path.substring(0, path.lastIndexOf(".")) + "_output.mp4").trim();
                sendFinish(result, listener, outPath);
            }
        }) {
        }.start();
    }


    /**
     * 视频裁剪w
     * start ： 微秒
     * duration : 微秒
     */
    public void cut(final String path, final long start, final DCVideoListener listener) {
        listener.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("tag", "=====onRecordEnd:--cut-before-cut" + start);
                boolean result = new SLClipVideo().clipVideoToEndRecord(path, start);
                String outPath = (path.substring(0, path.lastIndexOf(".")) + "_output.mp4").trim();
//                listener.onFinish(result?1:0,outPath);
                sendFinish(result, listener, outPath);
            }
        }) {
        }.start();
    }

    /**
     * 视频裁剪w
     * start ： 微秒
     * duration : 微秒
     */
    public void cut(final String path, final long start, final long duration, final DCVideoListener listener) {

        listener.onStart();

        new Thread(new Runnable() {

            @Override
            public void run() {

                Log.e("SLClipVideo-1", "start path:" + path + "start:" + start + "duration:" + duration);
                String outPath = (path.substring(0, path.lastIndexOf(".")) + "_output.mp4").replace(" ", "");

                FileUtils.createFile(outPath);
                Log.e("SLClipVideo-2", "outPath:" + outPath + (new File(outPath).exists()));

                boolean result = new SLClipVideo().clipVideo(path, start, duration);
                Log.e("SLClipVideo-2", "start path:" + path + "start:" + start + "duration:" + duration + "result:" + result);

//                String outPath = (path.substring(0, path.lastIndexOf(".")) + "_output.mp4").replace(" ", "");
                Log.e("SLClipVideo-3", "start path:" + path + "start:" + start + "duration:" + duration + "result:" + result + "outPath:" + outPath + (new File(outPath).exists()));

                sendFinish(result, listener, outPath);

            }

        }).start();

    }


    public void cutList(final List<CutEntity> mediaObjects, final DCCutListener listener) {
        listener.onCutStart();
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (CutEntity cutEntity : mediaObjects) {
                    KLog.i("cut-before->" + cutEntity.path);
                    boolean result = new SLClipVideo().clipVideo(cutEntity.path, cutEntity.start, cutEntity.duration);
                    cutEntity.cutPath = (cutEntity.path.substring(0, cutEntity.path.lastIndexOf(".")) + "_output.mp4").trim();
                    KLog.i(result + "cut-after->" + cutEntity.cutPath);
                    cutEntity.cutResult = result;
                }
                listener.onCutFinish(1, mediaObjects);
            }
        }).start();
    }

    /**
     * 设置音频的 音量
     *
     * @param mediaObjects
     * @param listener
     */
    public static void setAudioVolume(final List<CutEntity> mediaObjects, final DCCutListener listener) {
        listener.onCutStart();
        KLog.i("cut-before-cutAudioVideoList>" + mediaObjects.size());
        new Thread(new Runnable() {

            @Override
            public void run() {
                KLog.i("cut-before-pre>" + mediaObjects.size());
                for (CutEntity cutEntity : mediaObjects) {
                    boolean result = SLVideoProcessor.getInstance().setVolume(cutEntity.audioPath, cutEntity.volume, cutEntity.cutAudioPath);
                    KLog.i("cut-before-end>" + mediaObjects.size());
                    cutEntity.cutResult = result;
                }
                listener.onCutFinish(1, mediaObjects);
            }
        }).start();
    }

    /**
     * 音频 视频 同时裁剪
     *
     * @param mediaObjects
     * @param listener
     */
    public void cutAudioVideoList(final List<CutEntity> mediaObjects, final DCCutListener listener) {
        listener.onCutStart();
        KLog.i("cut-before-cutAudioVideoList>" + mediaObjects.size());
        new Thread(new Runnable() {

            @Override
            public void run() {
                KLog.i("cut-before-pre>" + mediaObjects.size());
                for (CutEntity cutEntity : mediaObjects) {
                    KLog.i("cut-before->" + cutEntity.start + "duration-->" + cutEntity.duration);
                    boolean result = new SLClipVideo().clipVideo(cutEntity.path, cutEntity.start, cutEntity.duration);
                    cutEntity.cutPath = (cutEntity.path.substring(0, cutEntity.path.lastIndexOf(".")) + "_output.mp4").trim();
                    ;

                    cutEntity.cutAudioPath = (cutEntity.audioPath.substring(0, cutEntity.audioPath.length() - 4) + "_output.wav").trim();
                    ;
                    //视频裁剪完成
                    //开始裁剪音频
                    boolean resultAudio = SLVideoProcessor.getInstance().trimAudio(cutEntity.audioPath, cutEntity.start * 1f / 1000000f,
                            cutEntity.duration * 1f / 1000000f, cutEntity.volume, cutEntity.cutAudioPath);
                    KLog.i(result + "cut-after->" + cutEntity.cutPath);
                    KLog.i(result + "cut-after-2>" + cutEntity.cutAudioPath);
                    cutEntity.cutResult = result && resultAudio;
                }
                listener.onCutFinish(1, mediaObjects);
            }
        }).start();
    }


    public void export() {

    }

//    public static long getVideoDuration(String url){
//        String result = "0";
//        try{
//            Log.d("DCVideoManager","videoplay======VirtualVideo视频获取到的时间-media--pre>" + (new File(url).exists())+url);
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            mediaMetadataRetriever.setDataSource(url);
//            result = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            Log.d("DCVideoManager","videoplay======VirtualVideo视频获取到的时间-media--retri>" + result);
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.e("DCVideoManager","videoplay======VirtualVideo视频获取到的时间-media--retri>" + e);
//        }
//        return Long.parseLong(result);
////            DCMediaInfoExtractor.MediaInfo mediaInfo = DCMediaInfoExtractor.extract(url);
////            return mediaInfo.durationUs;
//    }

    /**
     * 微妙
     *
     * @param url
     * @return
     */
    public static long getVideoLength(String url) {
        //创建分离器
        MediaExtractor mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long audioDuration = 0;
        //获取每个轨道的信息
        for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
            try {
                MediaFormat mMediaFormat = mMediaExtractor.getTrackFormat(i);
                String mime = mMediaFormat.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    audioDuration = mMediaFormat.getLong(MediaFormat.KEY_DURATION);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mMediaExtractor != null) {
            mMediaExtractor.release();
        }
        Log.d("tag", "audioDuration--->" + audioDuration);
        return audioDuration;
    }

    class ListenerObject {
        DCVideoListener listener;
        String outPath;
        int code;
    }

    class ListenerObjects {
        DCVideosListener listener;
        String outPath;
        String outPath2;
        int code;
    }

}
