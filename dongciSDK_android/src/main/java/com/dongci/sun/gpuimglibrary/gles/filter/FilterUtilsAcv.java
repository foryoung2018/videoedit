package com.dongci.sun.gpuimglibrary.gles.filter;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageToneCurveFilter;

import java.io.IOException;
import java.io.InputStream;

public class FilterUtilsAcv {

    public static GPUImageFilter getFilter(Context context, int index) {
        return getFilterDetail(context, index);
    }

    private static GPUImageFilter getFilterDetail(Context context, int index) {
        // 读取图像
        AssetManager as = context.getAssets();
        InputStream is = null;

        GPUImageToneCurveFilter filter = new GPUImageToneCurveFilter();
        try {
            is = as.open("filter/" + index + "-0.acv");
            filter.setFromCurveFileInputStream(is);
            is.close();
        } catch (IOException e) {
            Log.e("MainActivity", "Error");
        }

        return filter;


    }
}
