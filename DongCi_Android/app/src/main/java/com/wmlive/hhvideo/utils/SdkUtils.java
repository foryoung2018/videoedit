package com.wmlive.hhvideo.utils;

import android.os.Build;

/**
 * Created by lsq on 6/13/2017.
 * SDK版本工具
 */

public class SdkUtils {
    /**
     * SDK是否大于7.0
     */
    public static boolean isNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * SDK是否大于6.0
     */
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * SDK是否大于5.0
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * SDK是否大于4.4
     */
    public static boolean isKITKAT() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * SDK是否大于4.3
     */
    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * SDK是否大于4.2
     */
    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * SDK是否大于4.1
     */
    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
