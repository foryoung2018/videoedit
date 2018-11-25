package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.util.SystemClock;

/**
 * Created by lsq on 8/2/2017.
 */

public class MyImageWare extends NonViewAware implements Serializable {
    private long start;
    private int id;
    private WeakReference<IDanmakuView> danmakuViewRef;
    private BaseDanmaku danmaku;
    public Bitmap bitmap;

    public MyImageWare(String imageUri, BaseDanmaku danmaku, int width, int height, IDanmakuView danmakuView) {
        this(imageUri, new ImageSize(width, height), ViewScaleType.FIT_INSIDE);
        if (danmaku == null) {
            throw new IllegalArgumentException("danmaku may not be null");
        }
        this.danmaku = danmaku;
//        this.id = danmaku.hashCode(); // 弹幕中用户图像与礼物图像danmaku相同，同一id前面的会被取消，使用imageUri
        this.id = TextUtils.isEmpty(imageUri) ? super.hashCode() : danmaku.hashCode() + imageUri.hashCode();
        this.danmakuViewRef = new WeakReference<>(danmakuView);
        this.start = SystemClock.uptimeMillis();
    }

    @Override
    public int getId() {
        return this.id;
    }

    public String getImageUri() {
        return this.imageUri;
    }

    private MyImageWare(ImageSize imageSize, ViewScaleType scaleType) {
        super(imageSize, scaleType);
    }

    private MyImageWare(String imageUri, ImageSize imageSize, ViewScaleType scaleType) {
        super(imageUri, imageSize, scaleType);
    }

    @Override
    public boolean setImageDrawable(Drawable drawable) {
        return super.setImageDrawable(drawable);
    }

    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
//            if (this.danmaku.isTimeOut() || this.danmaku.isFiltered()) {
//                return true;
//            }

        this.bitmap = bitmap;
        IDanmakuView danmakuView = danmakuViewRef.get();
        if (danmakuView != null) {
            danmakuView.invalidateDanmaku(danmaku, true);
        }
        return true;
    }
}
