package com.wmlive.hhvideo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.wmlive.hhvideo.utils.DeviceUtils;

/**
 * Created by lsq on 6/15/2017.
 * 解决popwindow在navigationbar显示与不显示的偏移问题
 * 某些操蛋的华为手机可以手动隐藏navigation bar
 */

public class SuitableLinearLayout extends LinearLayout {
    private Context context;

    public SuitableLinearLayout(Context context) {
        this(context, null);
    }

    public SuitableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuitableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isOffset()) {
            if (this.getPaddingBottom() != 0) {
                this.setPadding(0, 0, 0, 0);
            }
        } else {
//            this.setPadding(0, 0, 0, DeviceUtils.getNavigationBarHeight(context));
            invalidate();
        }
        super.onLayout(true, l, t, r, DeviceUtils.getNavigationBarHeight(context));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public boolean isOffset() {
        return getDecorViewHeight() > getScreenHeight();
    }


    public int getDecorViewHeight() {
        return ((Activity) this.context).getWindow().getDecorView().getHeight();
    }

    public int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) this.context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
