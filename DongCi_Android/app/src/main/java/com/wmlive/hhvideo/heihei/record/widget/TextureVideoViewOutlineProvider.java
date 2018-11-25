package com.wmlive.hhvideo.heihei.record.widget;

import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Author：create by jht on 2018/9/30 16:43
 * Email：haitian.jiang@welines.cn
 */
public class TextureVideoViewOutlineProvider extends ViewOutlineProvider {
    private float mRadius;

    public TextureVideoViewOutlineProvider(float radius) {
        this.mRadius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int leftMargin = 0;
        int topMargin = 0;
        Rect selfRect = new Rect(leftMargin, topMargin,
                rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
        outline.setRoundRect(selfRect, mRadius);
    }
}

