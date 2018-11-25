package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 进度条的base
 * Created by JIAN on 2017/5/15.
 */

public class IProgressBar extends View {
    protected int mMax = 1000, mMin = 0;
    protected int mProgress = 0;
    /**
     * 总时长
     */
    protected int duration = mMax;

    public IProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 设置运行的时间轴
     *
     * @param mduration
     */
    public void setDuration(int mduration) {
        duration = mduration;
        if (mMax >= duration) {
            mMax = duration;
        }
        invalidate();
    }

    /**
     * 获取时间轴的总时间
     *
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 时间进度转为像素
     *
     * @param progress
     * @return
     */
    protected int progressTodp(int progress) {
        return (int) (getWidth() * ((progress + 0.0f) / duration));
    }


    /**
     * 设置最小最大时间区间值
     *
     * @param min
     * @param max
     */
    public void setInterval(int min, int max) {
        mMin = min;
        if (max < mMin) {
            max = mMin;
        }
        mMax = max;
        if (duration < max) {
            duration = max;
        }
//        Log.e("setInterval", "setInterval: "+mProgress );
        mProgress = 0;
        invalidate();
    }

    /**
     * 设置最小
     *
     * @param min
     */
    public void setMin(int min) {
        mMin = Math.min(min, mMax);
        invalidate();
    }


    public int getMin() {
        return mMin;
    }

    public int getMax() {
        return mMax;
    }

    /**
     * 更新进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
//        Log.e("setProgress", "setProgress: " + progress + "........" + (progress - mProgress));
        mProgress = progress;
//        Log.e("setProgress", "setProgress: "+mProgress);
        invalidate();
    }

    /**
     * 获取当前进度
     *
     * @return
     */
    public int getProgress() {
        return mProgress;
    }

    private PaintFlagsDrawFilter filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
            | Paint.FILTER_BITMAP_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(filter);

    }

    /**
     * 释放资源
     */
    public void recycle() {
    }

}
