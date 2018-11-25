package com.wmlive.hhvideo.heihei.record.engine.utils;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.DeviceUtils;

/**
 * 播放器 重新设置大小
 */
public class PlayerResize {

    /**
     * 根据宽高，获得适合屏幕的宽高
     * @param videoWidth
     * @param videoHeight
     */
    public static int[]  resize(int videoWidth,int videoHeight){
        int screenWidth = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0];
        int screenHeight = DeviceUtils.getScreenWH(DCApplication.getDCApp())[1];

        float resizeRate = Math.min((float)videoWidth/(float)screenWidth,(float)videoHeight/(float)screenHeight);
        int targetWidth = (int)(videoWidth /resizeRate);
        int targetHeight = (int)(videoHeight /resizeRate);


        return new int[]{targetWidth,targetHeight};

    }

    public static int[]  resize1(int videoWidth,int videoHeight){
        int screenWidth = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0];
        int screenHeight = DeviceUtils.getScreenWH(DCApplication.getDCApp())[1];
        float videoRate = videoWidth*1.0f/videoHeight*1.0f;
        float screenRate = screenWidth *1f/ screenHeight*1f;
        int targetW;
        int targetH;
        if(videoRate>screenRate){
            targetW = screenWidth;
            targetH = (int)(targetW*1f/videoRate);
        }else{
            targetH = screenHeight;
            targetW = (int)(targetH*1f*videoRate);
        }

        return new int[]{targetW,targetH};

    }

}
