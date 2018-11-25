package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.DeviceUtils;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/10/2017.
 * 保持宽高比布局
 */

public class RatioLayout extends RelativeLayout {
    // 宽和高的比例
    private float ratio = 0.0f;

    public RatioLayout(Context context) {
        this(context, null);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        ratio = a.getFloat(R.styleable.RatioLayout_ratio, 0.0f);
        a.recycle();
    }

    //设置宽高比
    public void setRatio(float f) {
        setRatio(f, false);
    }

    public void setRatio(float f, boolean refresh) {
        ratio = f;
        if (refresh) {
        }
            requestLayout();
    }

    //测量宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int[] screenSize = DeviceUtils.getScreenWH(DCApplication.getDCApp());
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && ratio != 0.0f) {
            height = (int) (width / ratio + 0.5f);
            if (height <= screenSize[1]) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + getPaddingTop() + getPaddingBottom(),
                        MeasureSpec.EXACTLY);
            } else {
                height = screenSize[1];
                width = (int) (height * ratio + 0.5f);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width + getPaddingLeft() + getPaddingRight(),
                        MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + getPaddingTop() + getPaddingBottom(),
                        MeasureSpec.EXACTLY);
            }
        } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY && ratio != 0.0f) {
            width = (int) (height * ratio + 0.5f);
            if (width <= screenSize[0]) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width + getPaddingLeft() + getPaddingRight(),
                        MeasureSpec.EXACTLY);
            } else {
                width = screenSize[0];
                height = (int) (width / ratio + 0.5f);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width + getPaddingLeft() + getPaddingRight(),
                        MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + getPaddingTop() + getPaddingBottom(),
                        MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
