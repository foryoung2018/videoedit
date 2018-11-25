package com.wmlive.hhvideo.heihei.record.engine.utils;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.utils.KLog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoUtils {

    /**
     * 根据视频地址，获取显示的时长 原则:
     * 取两个中最大的值的 整数部分
     * @return
     */
    public static String getShowTime(long lastTime,long currentTime){
        int max = Math.max((int)lastTime/1000000,(int)currentTime/1000000);
        return DiscoveryUtil.convertTime(max);
    }
    /**
     * 微秒，获取视频的长度
     * @param url
     * @return
     */
    public static long getVideoLength(String url) {
        if (TextUtils.isEmpty(url)) {
            return 0;
        }
        //创建分离器
        MediaExtractor mMediaExtractor = new MediaExtractor();
        long audioDuration = 0;
        try {
            mMediaExtractor.setDataSource(url);
            //获取每个轨道的信息
            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
                MediaFormat mMediaFormat = mMediaExtractor.getTrackFormat(i);
                String mime = mMediaFormat.getString(MediaFormat.KEY_MIME);
                if (mime!=null&&mime.startsWith("video/")) {
                    audioDuration = mMediaFormat.getLong(MediaFormat.KEY_DURATION);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(mMediaExtractor!=null)
                mMediaExtractor.release();
        }
        return audioDuration;
    }


    /**

     * 获取网络文件大小

     */
    public static long getRemoteFileLength(String downloadUrl) throws IOException{

        if(downloadUrl == null || "".equals(downloadUrl)){

            return 0L ;

        }

        URL url = new URL(downloadUrl);

        HttpURLConnection conn = null;

        try {

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("HEAD");

            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows 7; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36 YNoteCef/5.8.0.1 (Windows)");

            return (long) conn.getContentLength();

        } catch (IOException e) {

            return 0L;

        } finally {

            conn.disconnect();

        }

    }

    public static double getAudioLength(String url){
        Log.d("tag","getMediaInfor---width-height>"+url);
        aVideoConfig videoConfig = new aVideoConfig();
        if (TextUtils.isEmpty(url)) {
            return 0;
        }
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
        mMediaExtractor.release();
        return audioDuration;
    }


    public static aVideoConfig getMediaInfor(String url){
        Log.d("tag","getMediaInfor---width-height>"+url);
        aVideoConfig videoConfig = new aVideoConfig();
        if (TextUtils.isEmpty(url)) {
            return videoConfig;
        }

        DCMediaInfoExtractor.MediaInfo mediaInfo = null;

        //创建分离器
        MediaExtractor mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(url);
            mediaInfo = DCMediaInfoExtractor.extract(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long audioDuration = 0;
        //获取每个轨道的信息
        for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
            try {
                MediaFormat mMediaFormat = mMediaExtractor.getTrackFormat(i);
                String mime = mMediaFormat.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
//                    sourceVTrack = i;
                    int width = mMediaFormat.getInteger(MediaFormat.KEY_WIDTH);
                    int height = mMediaFormat.getInteger(MediaFormat.KEY_HEIGHT);

//                    videoMaxInputSize = mMediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                    long videoDuration = mMediaFormat.getLong(MediaFormat.KEY_DURATION);
                    int fps = mMediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
                    Log.d("tag",mediaInfo.videoInfo.rotation+"getMediaInfor---width-wdith>"+width+"Height:>"+height);
                    videoConfig.setVideoFrameRate(fps);
                    if(mediaInfo==null || mediaInfo.videoInfo.rotation == 0)
                        videoConfig.setVideoSize(width,height);
                    else {
                        videoConfig.setVideoSize(height,width);
                    }
                    Log.d("tag","getMediaInfor---width-height-result>"+videoConfig.getVideoWidth()+"Height:>"+videoConfig.getVideoHeight());
                    videoConfig.setVideoDuration(videoDuration);
                    videoConfig.rotation = mediaInfo.videoInfo.rotation;
//                    clipDuration = videoDuration - clipPoint ;
//                    Log.e(TAG, (clipPoint)+"video==--cut--Time>>"+(videoDuration));
//                    //检测剪辑点和剪辑时长是否正确
//                    if (clipPoint >= videoDuration) {
//                        Log.e(TAG, "clip point is error!");
//                        return false;
//                    }
//                    Log.e(TAG, (clipDuration + clipPoint)+"clip duration is error-pre->"+(  videoDuration));
//                    Log.d(TAG, "clip duration is error-pre->"+((clipDuration + clipPoint) >= videoDuration));
//                    if ((clipDuration != 0) && ((clipDuration + clipPoint) > videoDuration)) {
//                        Log.e(TAG, "clip duration is error!");
//                        return false;
//                    }
//                    Log.d(TAG, "width and height is " + width + " " + height
//                            + ";maxInputSize is " + videoMaxInputSize
//                            + ";duration is " + videoDuration
//                    );
//                    //向合成器添加视频轨
//                    videoTrackIndex = mMediaMuxer.addTrack(mMediaFormat);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaExtractor.release();

        return videoConfig;

    }

}
