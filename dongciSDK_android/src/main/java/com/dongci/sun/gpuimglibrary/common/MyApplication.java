package com.dongci.sun.gpuimglibrary.common;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by qqche_000 on 2017/8/6.
 */

public class MyApplication extends Application {

    public static Context context;
    public static int screenWidth;
    public static int screenHeight;
    public static int StatusBarHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        DisplayMetrics mDisplayMetrics = getApplicationContext().getResources()
                .getDisplayMetrics();

        screenWidth = mDisplayMetrics.widthPixels;
        screenHeight = mDisplayMetrics.heightPixels;

        Resources resources = getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        StatusBarHeight = resources.getDimensionPixelSize(resourceId);

    }

}
