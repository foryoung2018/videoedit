package com.wmlive.hhvideo.dcijkplayer;

import android.util.Log;

/**
 * Created by yangjiangang on 2018/7/27.
 */

public class L {

    private static final String TAG = "DcAppLogIjk";
    private static boolean sIsDebug = true;

    public static void setDebug(boolean isDebug) {
        sIsDebug = isDebug;
    }

    public static void i(String message) {
        if (sIsDebug) {
            Log.i(TAG, message);
        }
    }

    public static void d(String message) {
        if (sIsDebug) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if (sIsDebug) {
            Log.e(TAG, message);
        }
    }

}
