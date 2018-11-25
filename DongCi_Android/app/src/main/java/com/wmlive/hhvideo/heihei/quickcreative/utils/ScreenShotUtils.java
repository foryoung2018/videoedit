package com.wmlive.hhvideo.heihei.quickcreative.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.wmlive.hhvideo.heihei.record.manager.RecordManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ScreenShotUtils {


    /**
     * 获取播放截图
     * @param mPlayerLayout
     * @param mTextureView
     * @return
     */
    public static String getTextureViewShot(ViewGroup mPlayerLayout, TextureView mTextureView) {
        Bitmap content = mTextureView.getBitmap();
        File imgFile = new File(RecordManager.get().getProductEntity().baseDir, UUID.randomUUID() + ".png");
        OutputStream fout = null;
        try {
            fout = new FileOutputStream(imgFile);
            content.compress(Bitmap.CompressFormat.PNG, 70, fout);
            fout.flush();
            fout.close();
            return imgFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取和保存当前屏幕的截图 返回真实路径
     * <p>
     * 这里TextureView 实现 textureview + 整个ViewGroup 截图 =叠加 Canva绘制
     */
    public static String cropVideoImage(TextureView textureView, View layoutView, WindowManager windowManager) {
        if (textureView == null || layoutView == null) {
            return null;
        }

        File imgFile = new File(RecordManager.get().getProductEntity().baseDir, UUID.randomUUID() + ".png");
        Bitmap content = textureView.getBitmap();
        layoutView.setDrawingCacheEnabled(true);
//        Bitmap layout = layoutView.getDrawingCache();
        Bitmap layout = convertViewToBitmap(layoutView);
        Bitmap screenshot = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);

//        Display defaultDisplay = windowManager.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        defaultDisplay.getMetrics(metrics);

        //拼接
        Canvas canvas = new Canvas(screenshot);
        canvas.drawBitmap(content, (layout.getWidth() - content.getWidth()) / 2, (layout.getHeight() - content.getHeight()) / 2, new Paint());
        canvas.drawBitmap(layout, 0, 0, new Paint());
        canvas.save();
        canvas.restore();

        OutputStream fout = null;
        try {
            fout = new FileOutputStream(imgFile);
            screenshot.compress(Bitmap.CompressFormat.PNG, 70, fout);
            fout.flush();
            fout.close();

            return imgFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap convertViewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


}
