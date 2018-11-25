package com.wmlive.hhvideo.utils.download;

import android.util.Log;

/**
 * Created by lsq on 6/5/2017.
 * 下载的log
 */

public class DLog {
    private static boolean sDebug = false;


    public static void setDebug(boolean isDebug) {
        sDebug = isDebug;
    }


    public static void i(String msg) {
        if (sDebug) {
            Log.i("DcApp", msg);
        }
    }

    public static void e(String msg) {
        if (sDebug) {
            Log.e("download_log", msg);
        }
    }
}
