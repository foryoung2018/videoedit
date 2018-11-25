package com.wmlive.hhvideo.utils;

import android.content.Context;

/**
 * Author：create by jht on 2018/9/27 12:23
 * Email：haitian.jiang@welines.cn
 */
public class DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        // 用 final float scale = context.getResources().getDisplayMetrics().density; 有些误差
        final float scale = context.getResources().getDisplayMetrics().ydpi / 160.0f;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().ydpi / 160.0f;
        return (int) (pxValue / scale + 0.5f);
    }

}
