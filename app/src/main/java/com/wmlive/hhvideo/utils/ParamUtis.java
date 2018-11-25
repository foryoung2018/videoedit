package com.wmlive.hhvideo.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/6/25.
 */

public class ParamUtis {
    public static void setLayoutParams(Context context, View v, int height) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        switch (height) {
            case 750://1:1
                layoutParams.width = ScreenUtil.getWidth(context);
                layoutParams.height = layoutParams.width;
                break;
            case 1334://9:16
                layoutParams.height = ScreenUtil.dip2px(context, 400);
                layoutParams.width = layoutParams.height * 9 / 16;
                break;
            case 422://16:9
                layoutParams.width = ScreenUtil.getWidth(context);
                layoutParams.height = layoutParams.width * 9 / 16;
                break;
            case 1000://3:4
                layoutParams.height = ScreenUtil.dip2px(context, 400);
                layoutParams.width = layoutParams.height * 3 / 4;
                break;
        }
        v.setLayoutParams(layoutParams);
        KLog.d("ggq", "height" + layoutParams.height + "  width==" + layoutParams.width);
    }

    /**
     * @param context
     * @param v
     * @param height
     */
    public static void setLayoutParams2(Context context, View v, int height) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        switch (height) {
            case 750://1:1
                layoutParams.width = ScreenUtil.getWidth(context);
                layoutParams.height = layoutParams.width;
                break;
            case 1334://9:16
                layoutParams.height = ScreenUtil.dip2px(context, 400);
                layoutParams.width = layoutParams.height * 544 / 960;
                break;
            case 425://16:9
                layoutParams.width = ScreenUtil.getWidth(context);
                layoutParams.height = layoutParams.width * 9 / 16;
                break;
            case 422://16:9
                layoutParams.width = ScreenUtil.getWidth(context);
                layoutParams.height = layoutParams.width * 9 / 16;
                break;
            case 1000://3:4
                layoutParams.height = ScreenUtil.dip2px(context, 400);
                layoutParams.width = layoutParams.height * 3 / 4;
                break;
        }
        v.setLayoutParams(layoutParams);
        KLog.d("ggq", "height" + layoutParams.height + "  width==" + layoutParams.width);
    }

    /**
     * list画框设置大小
     * 按照高度40适配
     *
     * @param context
     * @param v
     * @param height
     */
    public static void setLayoutParams3(Context context, View v, int height) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        switch (height) {
            case 750://1:1
                layoutParams.width = ScreenUtil.dip2px(context, 40);
                layoutParams.height = layoutParams.width;
                break;
            case 1334://9:16
                layoutParams.height = ScreenUtil.dip2px(context, 40);
                layoutParams.width = layoutParams.height * 544 / 960;
                break;
            case 422://16:9
                layoutParams.width = ScreenUtil.dip2px(context, 40);
                layoutParams.height = layoutParams.width * 9 / 16;
                break;
            case 425://16:9
                layoutParams.width = ScreenUtil.dip2px(context, 40);
                layoutParams.height = layoutParams.width * 9 / 16 + 1;
                break;
            case 1000://3:4
                layoutParams.height = ScreenUtil.dip2px(context, 40);
                layoutParams.width = layoutParams.height * 3 / 4;
                break;
        }
        v.setLayoutParams(layoutParams);
        KLog.d("ggqRV", "height==" + height + "  layoutParams.height==" + layoutParams.height + "  layoutParams.width==" + layoutParams.width);
    }

    /**
     * 不同画框比例下 动态设置view高度
     *
     * @param context
     * @param v
     * @param height     cavance_Height
     * @param deltHeight 屏幕高度要减去的高度
     */
    public static void setLayoutParam(Context context, View v, int height, int deltHeight) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        int screenHeight = ScreenUtil.getHeight(context);
        int viewHeight = screenHeight - ScreenUtil.dip2px(context, deltHeight);//动态得到控件的高度
        switch (height) {
            case 750://1:1 按照宽度适配
                layoutParams.width = Math.min(ScreenUtil.getWidth(context), viewHeight);
                layoutParams.height = layoutParams.width;
                break;
            case 1334://9:16  按照高度适配
                layoutParams.width = viewHeight * 544 / 960;
                layoutParams.height = viewHeight;
                break;
            case 422://16:9  按照宽度适配
                layoutParams.width = ScreenUtil.getWidth(context);
                layoutParams.height = layoutParams.width * 9 / 16 + ScreenUtil.dip2px(context, 2);
                break;
            case 425://16:9  按照宽度适配
                layoutParams.width = ScreenUtil.getWidth(context);
                layoutParams.height = layoutParams.width * 9 / 16 + ScreenUtil.dip2px(context, 3);
                break;
            case 1000://3:4 按照比例的较小者适配
                int width = ScreenUtil.getWidth(context);
                int maxWidth = viewHeight * 3 / 4;

                if (width > maxWidth) {
                    layoutParams.width = maxWidth;
                    layoutParams.height = viewHeight;
                } else {
                    layoutParams.width = width;
                    layoutParams.height = width * 4 / 3;
                }

                break;
        }
        v.setLayoutParams(layoutParams);
        KLog.d("ggqbig", "height==" + height + "  layoutParams.height==" + layoutParams.height + "  layoutParams.width==" + layoutParams.width + "  viewHeight==" + viewHeight);
    }

    public static void setViewParames3_4(Context context, View v, int deltValue) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        int screenHeight = ScreenUtil.getHeight(context);
        int screeWidth = ScreenUtil.getWidth(context);
        int viewHeight = screenHeight - ScreenUtil.dip2px(context, deltValue);//动态得到控件的高度

        if (viewHeight / 4 * 3 > screeWidth) {
            layoutParams.width = screeWidth;
            layoutParams.height = screeWidth * 4 / 3;
        } else {
            layoutParams.height = viewHeight;
            layoutParams.width = viewHeight / 4 * 3;
        }
        v.setLayoutParams(layoutParams);

    }


}
