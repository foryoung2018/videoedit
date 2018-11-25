package com.wmlive.hhvideo.heihei.quickcreative.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.gles.filter.FilterUtils;
import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

import org.jcodec.api.JCodecException;
import org.jcodec.api.android.AndroidFrameGrab;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.tools.MainUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.BuildConfig;
import cn.wmlive.hhvideo.R;

public class CopyAssetFilesUtil {

    public static void copyImgs(Context context) {
        KLog.d("ExtractVideoFrame", "copyImgs: " + System.currentTimeMillis());
        Drawable mask_d1 = context.getDrawable(R.drawable.mask_d1);
        Drawable mask_d2 = context.getDrawable(R.drawable.mask_d2);
        Drawable mask_d3 = context.getDrawable(R.drawable.mask_d3);
        Drawable mask_dd1 = context.getDrawable(R.drawable.mask_dd1);
        Drawable mask_dd2 = context.getDrawable(R.drawable.mask_dd2);
        Drawable mask_dd3 = context.getDrawable(R.drawable.mask_dd3);
        Drawable mask_dd4 = context.getDrawable(R.drawable.mask_dd4);
        Drawable billboard = context.getDrawable(R.drawable.billboard);
        drawableToFile(mask_d1, AppCacheFileUtils.getAppCreativeAssetsPath() + "mask_d1.png");
        drawableToFile(mask_d2, AppCacheFileUtils.getAppCreativeAssetsPath() + "mask_d2.png");
        drawableToFile(mask_d3, AppCacheFileUtils.getAppCreativeAssetsPath() + "mask_d3.png");
        drawableToFile(mask_dd1, AppCacheFileUtils.getAppCreativeAssetsPath() + "mask_dd1.png");
        drawableToFile(mask_dd2, AppCacheFileUtils.getAppCreativeAssetsPath() + "mask_dd2.png");
        drawableToFile(mask_dd3, AppCacheFileUtils.getAppCreativeAssetsPath() + "mask_dd3.png");
        drawableToFile(mask_dd4, AppCacheFileUtils.getAppCreativeAssetsPath() + "mask_dd4.png");
        drawableToFile(billboard, AppCacheFileUtils.getAppCreativeAssetsPath() + "billboard.png");
        KLog.d("CopyAssetFilesUtil", "copyImgs: " + System.currentTimeMillis());
        FilterUtils.filterResFoler = AppCacheFileUtils.getAppCreativeAssetsPath();
    }

    public static void drawableToFile(Drawable drawable, String filePath) {
        if (drawable == null) return;
        try {
            File file = new File(filePath);
            if (file.exists()) return;
            if (!file.exists()) file.createNewFile();
            FileOutputStream out = null;
            out = new FileOutputStream(file);
            ((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFiles(Context context) {

        int version_code = SPUtils.getInt(context, SPUtils.VERSIONCODE, 0);
        if (version_code < BuildConfig.VERSION_CODE) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    copyImgs(context);
                    copyJson(context);
                    SPUtils.putInt(context, SPUtils.VERSIONCODE, BuildConfig.VERSION_CODE);
                }
            }.start();
        }
    }


    public static void copyJson(Context context) {
        copyFilesFassets(context, "defaultJson", AppCacheFileUtils.getAppCreativeAssetsPath());
    }


    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context Context 使用CopyFiles类的Activity
     * @param oldPath String  原文件路径  如：/aa
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public static void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
