package com.wmlive.hhvideo.heihei.record.engine.content;

import android.content.Context;

import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.utils.ScreenUtil;

public class PlayerViewFactory {

    public static int[] measureViewWH(Context context,String path){
        aVideoConfig mediaInfor = VideoUtils.getMediaInfor(path);
        float v = mediaInfor.getVideoWidth() * 1.0f / mediaInfor.getVideoHeight();
        int screenWidth = ScreenUtil.getWidth(context);
        int height = ScreenUtil.getHeight(context) - ScreenUtil.dip2px(context, 158);
        int[] result = new int[2];
        int videoViewWidth;
        int videoViewHeight;
        if (v >= 1) {
            videoViewWidth = screenWidth;
            videoViewHeight = (int) (screenWidth / v);
        } else {
            videoViewHeight = height;
            videoViewWidth = (int) (height * v);
        }
        result[0] = videoViewWidth;
        result[1] = videoViewHeight;
        return result;
    }

    public static int[] measureViewWHLocalPublish(Context context,String path,int dpValue){
        aVideoConfig mediaInfor = VideoUtils.getMediaInfor(path);
        float v = mediaInfor.getVideoWidth() * 1.0f / mediaInfor.getVideoHeight();
        int screenWidth = ScreenUtil.getWidth(context);
        int height = ScreenUtil.getHeight(context) - ScreenUtil.dip2px(context, dpValue);
        int[] result = new int[2];
        int videoViewWidth;
        int videoViewHeight;
        if (v >= 1) {
            videoViewWidth = screenWidth;
            videoViewHeight = (int) (screenWidth / v);
        } else {
            videoViewHeight = height;
            videoViewWidth = (int) (height * v);
        }
        result[0] = videoViewWidth;
        result[1] = videoViewHeight;
        return result;
    }
}
