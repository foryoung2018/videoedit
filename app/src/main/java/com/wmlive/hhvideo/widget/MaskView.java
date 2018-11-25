package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.utils.KLog;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 9/4/2017.
 * 相机预览的蒙版，支持动画
 */
public class MaskView extends View {
    private static final int INTERVAL_OFFSET = 10;
    private static final int INTERVAL_TIME = 5;
    private Paint paint;
    private int maskColor = 0x55000000;
    private int visibleColor = 0xFFFC1C8F;
    private RectF visibleRect;
    private RectF maskRect;
    private float visibleRatio = 0.75f;
    private Xfermode xfermode;
    private boolean isVertical;
    private float viewRatio = 1f;
    private float needOffset = 0;
    private boolean showAnim = true;
    private Bitmap maskBitmap;
    private Bitmap visibleBitmap;

    public int width;
    public int height;

    public MaskView(Context context) {
        this(context, null);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.MaskView, defStyle, 0);
            maskColor = a.getColor(R.styleable.MaskView_maskColor, maskColor);
            showAnim = a.getBoolean(R.styleable.MaskView_showAnim, showAnim);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        initData(context);
    }

    private void initData(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        maskRect = new RectF();
        visibleRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        w = w > 0 ? w : 1;
        h = h > 0 ? h : 1;
        viewRatio = w * 1f / h;
        maskRect.set(0, 0, w, h);
        maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        maskBitmap.eraseColor(maskColor);
        visibleBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        visibleBitmap.eraseColor(visibleColor);
        KLog.i(oldw + "MaskView--onDraw---onSizeChanged->" + w + "Wdith:" + h);
        width = w;
        height = h;
    }

    public void setShowAnim(boolean showAnim) {
        this.showAnim = showAnim;
    }

    public void setVisibleRatio(float ratio) {
        setVisibleRatio(ratio, true);
    }

    public void setVisibleRatio(float ratio, boolean showAnim) {
        this.showAnim = showAnim;
        visibleRatio = ratio;
        isVertical = ratio * 1f / viewRatio > 1;
        KLog.i(viewRatio + "MaskView--onDraw---onSizeChanged-setVisibleRatio--viewRatio>" + isVertical);
        RectF rectF = RecordFileUtil.getClipSrc(width, height, visibleRatio, 0f);
        needOffset = isVertical ? rectF.top : rectF.left;
        if (showAnim) {
            visibleRect.set(
                    isVertical ? rectF.left : 0,
                    isVertical ? 0 : rectF.top,
                    isVertical ? rectF.right : width,
                    isVertical ? height : rectF.bottom);
//            maskRect.set(maskRect.left,maskRect.top,isVertical ? maskRect.right : getWidth(),maskRect.bottom);
        } else {
            visibleRect.set(rectF.left, rectF.top, rectF.right, rectF.bottom);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saveCount = canvas.saveLayer(maskRect, paint, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(maskBitmap, null, maskRect, paint);
        paint.setXfermode(xfermode);
        if (showAnim) {
            visibleRect.set(
                    visibleRect.left + (isVertical ? 0 : INTERVAL_OFFSET),
                    visibleRect.top + (isVertical ? INTERVAL_OFFSET : 0),
                    visibleRect.right - (isVertical ? 0 : INTERVAL_OFFSET),
                    visibleRect.bottom - (isVertical ? INTERVAL_OFFSET : 0));
        }
        KLog.i(needOffset + "MaskView--onDraw----maskRect>" + maskRect);
        KLog.i(needOffset + "MaskView--onDraw----visiRect>" + visibleRect);
        canvas.drawBitmap(visibleBitmap, null, visibleRect, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(saveCount);
        if (showAnim) {
            if (isVertical) {
                if (visibleRect.top < needOffset) {
                    postInvalidateDelayed(INTERVAL_TIME);
                }
            } else {
                if (visibleRect.left < needOffset) {
                    postInvalidateDelayed(INTERVAL_TIME);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
