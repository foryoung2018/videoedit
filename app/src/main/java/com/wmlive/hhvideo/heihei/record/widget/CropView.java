package com.wmlive.hhvideo.heihei.record.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;


import com.wmlive.hhvideo.heihei.record.utils.CoreUtils;
import com.wmlive.hhvideo.utils.ScreenUtil;

import java.util.ArrayList;

import cn.wmlive.hhvideo.R;

/**
 * 截取音乐组件
 * Created by JIAN on 2017/5/15.
 */

public class CropView extends IProgressBar {
    private float itemWidth = 5;
    private int mItemHorSpacing = 8;
    private int mMarginLeft = 0;
    private int tranblack = 0;
    private float[] fitem = null;

    private float cropRange; // 裁剪范围

    public int getItemWidth() {
        return fitem.length * (8 + mItemHorSpacing);
    }

    private float[] fs;

    public CropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        Resources res = getResources();
        mMarginLeft = ScreenUtil.dip2px(context,20);
        tranblack = res.getColor(R.color.transparent_black);
        pshadow.setColor(res.getColor(R.color.bg_crop_music_shadow));
        pshadow.setAntiAlias(true);
        paint.setAntiAlias(true);
        paint.setColor(res.getColor(R.color.bg_crop_music));
        pProgress.setAntiAlias(true);
        pProgress.setColor(res.getColor(R.color.green));

        pLeft.setAntiAlias(true);
        pLeft.setColor(res.getColor(R.color.transparent_black));

        pItem.setAntiAlias(true);
        pItem.setColor(res.getColor(R.color.transparent_black90));
        DisplayMetrics metrics = CoreUtils.getMetrics();
        pTextRight.setAntiAlias(true);
        pTextRight.setTextSize(res.getDimensionPixelSize(R.dimen.text_size_10));
        pTextRight.setColor(res.getColor(R.color.transparent_white));
        pItemProgress.setAntiAlias(true);
        pItemProgress.setColor(res.getColor(R.color.green));
        pItemProgress.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        if (metrics.density >= 3.0) {//xxhdpi
            mItemHorSpacing = 8;
            itemWidth = 8;
            fitem = new float[]{
                    0.2f, 0.3f, 0.45f, 0.2f, 0.1f, 0.15f, 0.4f, 0.2f, 0.15f, 0.2f, 0.15f, 0.15f, 0.25f, 0.5f, 0.35f, 0.25f, 0.15f, 0.17f, 0.18f, 0.15f, 0.35f, 0.55f, 0.45f,
                    0.2f, 0.3f, 0.45f, 0.2f, 0.1f, 0.15f, 0.4f, 0.2f, 0.1f, 0.1f, 0.1f};
        } else if (metrics.density >= 2.0) {
            mItemHorSpacing = 6;
            itemWidth = 6;
            fitem = new float[]{
                    0.2f, 0.3f, 0.45f, 0.2f, 0.19f, 0.15f, 0.4f, 0.2f, 0.15f, 0.2f, 0.15f, 0.15f, 0.25f, 0.5f, 0.35f, 0.25f, 0.15f, 0.17f, 0.18f, 0.15f, 0.35f, 0.55f, 0.45f,
                    0.4f, 0.2f, 0.3f, 0.5f, 0.25f,
                    0.4f, 0.2f, 0.3f, 0.5f, 0.25f};
        } else {
            mItemHorSpacing = 5;
            itemWidth = 5;
            fitem = new float[]{
                    0.2f, 0.3f, 0.45f, 0.2f, 0.19f, 0.15f, 0.4f, 0.2f, 0.15f, 0.2f, 0.15f, 0.15f, 0.25f, 0.5f, 0.35f, 0.25f, 0.15f, 0.17f, 0.18f, 0.15f, 0.35f, 0.55f, 0.45f,
                    0.4f, 0.2f, 0.3f, 0.5f, 0.25f};
        }
        fs = fitem;

    }

    public void setDuration(int mduration, int itemMaxDuration) {
        if (itemMaxDuration <= 0) {
            itemMaxDuration = 1;
        }
        int line = mduration / itemMaxDuration;
        int itemLen = fitem.length;
        fs = new float[itemLen * line];
        for (int i = 0; i < fs.length; i++) {
            fs[i] = fitem[i % itemLen];
        }
        super.setDuration(mduration);
    }

    private int getViewHeight() {
        return getHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            list.clear();
            //算出大致需要多少个竖条     目标:(让发布裁剪和录制裁剪时的单个竖条的宽度大致一样，总数可增减或减少)
            int nums = (int) (getWidthVisible() / (itemWidth + mItemHorSpacing));
            //+0.0f 防止精度丢失，不准
            //根据竖条的总数，决定单个竖条的宽度
            itemWidth = (getWidthVisible() - mItemHorSpacing * (nums - 1) + 0.0f) / nums;
            float radomH;
            float min = itemWidth;
            float tleft = 0;

            int tbottom = getBottom();
            int tlen = fs.length;
            int tindex = 0;
            int viewH = getViewHeight();
            for (int i = 0; i < nums; i++) {
                tleft = itemWidth * i + mItemHorSpacing * i + mMarginLeft;
                tindex = i % tlen;
                radomH = (viewH * fs[tindex] + min); //防止rectF,向下移动时(offset)，遮挡太矮的rectF
                list.add(new RectF(tleft, viewH - radomH, tleft + itemWidth, tbottom));
            }
        }
    }

    private ArrayList<RectF> list = new ArrayList<RectF>();
    private Paint paint = new Paint(), pProgress = new Paint(), pItem = new Paint(), pshadow = new Paint(), pLeft = new Paint();
    private RectF mRectItem;
    private RectF mRectTemp = new RectF();

    private int mleft = mMarginLeft, mright = mMarginLeft;

    /**
     * @param left  左边线距离左边框的像素
     * @param right 右边线距离右边框的像素
     * @return
     */
    public void setMargin(int left, int right) {
        mleft = left;
        mright = right;
    }

    private int getWidthVisible() {
        return getWidth() - mleft - mright;

    }

    public int getMarginLeft() {
        return mMarginLeft;
    }

    private Paint pTextRight = new Paint();
    private Paint pItemProgress = new Paint();

    /**
     * 进度转px
     *
     * @param progress
     * @return
     */
    @Override
    protected int progressTodp(int progress) {


        return (int) ((progress + 0.0f) * getWidthVisible() / getDuration()) + mMarginLeft;
    }

    private boolean enableDrawbg = true;

    public void enableDrawBg(boolean enable) {
        enableDrawbg = enable;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int len = 0;
        if (null != list && (len = list.size()) > 0) {
            float tmin = progressTodp(getMin());
            float tmax = progressTodp(getMax());
            float titemProgressPx = progressTodp(mProgress);
//            if (enableDrawbg) {
//                rectItem.set(tmin, Math.min(getTop(), getBottom()), tmax, getBottom());
//                canvas.drawRect(rectItem, pItem);
//            }

            @SuppressLint("WrongConstant") int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.CLIP_SAVE_FLAG);
            for (int i = 0; i < len; i++) {
                mRectItem = list.get(i);
                mRectTemp.set(mRectItem);
                mRectTemp.top = Math.min((int) (mRectTemp.top * 1.1), mRectTemp.bottom - 5);
                canvas.drawRoundRect(mRectTemp, itemWidth / 2, itemWidth / 2, paint);
                if (tmin <= mRectItem.right && tmax >= mRectItem.left) {//绘制player的播放进度
                    if (mRectItem.left <= titemProgressPx) {//只绘制可见部分的进度
                        mRectTemp.set(Math.max(mRectItem.left, tmin), mRectItem.top, Math.min(titemProgressPx, mRectItem.right), mRectItem.bottom);
                        mRectTemp.top = Math.min((int) (mRectTemp.top * 1.1), mRectTemp.bottom - 5);
                        canvas.drawRect(mRectTemp, pItemProgress);
                    }
                }
            }
            canvas.restoreToCount(sc);
        }
    }


    private float downX = 0;
    private float offX = 0;
    private boolean isTouched = false;

    public void setScrollX(int offX, int screenWpx) {
        float tminpx = Math.abs(offX);
        float nDuration = getMax() - getMin();
        float tempMin = getDuration() * ((tminpx + 0.0f) / getWidthVisible());
//        if (tempMin + nDuration > duration) {
//            //do nothing
//        } else {
        mMin = (int) tempMin;
        mMax = (int) (mMin + nDuration);
        mProgress = mMin;
        invalidate();
        if (null != listener) {
            listener.onRangbarChanging(mMin, mMax, mProgress);
        }
//        }
    }

    /**
     * .最小刻度把手的左边距
     *
     * @return
     */
    public int getLeftMinBar() {
        return progressTodp(getMin()) - mMarginLeft;
    }

    public int getProgressPx() {
        return progressTodp(mProgress) - mMarginLeft;
    }


    /**
     * 清除资源
     */
    public void recycle() {
        super.recycle();
        list.clear();
        list = null;
        paint = null;
        pProgress = null;
        pTextRight = null;
        listener = null;
    }

    private onRangSeekbarListener listener;

    public void setOnRangSeekbarListener(onRangSeekbarListener _listener) {
        listener = _listener;
    }

    public void setCropRange(float maxValue) {
        this.cropRange = maxValue;
    }

    public float getCropRange() {
        return cropRange;
    }
}
