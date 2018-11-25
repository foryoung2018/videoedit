package com.wmlive.hhvideo.heihei.record.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import com.wmlive.hhvideo.heihei.record.uird.BitmapUtils;
import com.wmlive.hhvideo.heihei.record.utils.PaintUtils;
import com.wmlive.hhvideo.utils.ScreenUtil;

import cn.wmlive.hhvideo.R;

/**
 * Created by JIAN on 2017/6/17.
 */

public class ExtMenuBtn extends View {
    private Paint mPaint, mPtext;
    private String text;
    private int[] textHeight;
    private int textWidth;
    private String TAG = "menubtn";
    private Bitmap mRecorderFilter0;
    private Rect mRecorderFilter0Rect;
    private final Paint paintImage;
    private int drawableWidth;

    public ExtMenuBtn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TAG = ExtMenuBtn.class.getName();
        Resources res = getResources();
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        src = new Rect(0, 0, 50, 50);
        srcText = new Rect(0, 0, 50, 50);
        mPaint.setAntiAlias(true);
        mPtext = new Paint();
        mPtext.setAntiAlias(true);
        mPtext.setColor(Color.WHITE);
        mPtext.setTextSize(res.getDimensionPixelSize(R.dimen.text_size_12));
        mPtext.setShadowLayer(3, 3, 3, res.getColor(R.color.transparent_black));
        text = res.getString(R.string.filter);
        textHeight = PaintUtils.getHeight(mPtext);
        textWidth = PaintUtils.getWidth(mPtext, text);
        paintImage = new Paint();
        paintImage.setAntiAlias(true);
        pCrop = new Paint();
        pCrop.setStrokeWidth(2);
        drawableWidth = dip2px(getContext(), 24);
        pCrop.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mRecorderFilter0 = BitmapFactory.decodeResource(getResources(), R.drawable.recorder_filter_0);
        mRecorderFilter0Rect = new Rect(0, 0, drawableWidth, drawableWidth);
    }


    private Paint pCrop = null;
    private Rect mDstRect = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.WHITE);
        if (null != mTempBmp) {
            int ml = (getWidth() - itemW) / 2;
            int mt = 0;

            @SuppressLint("WrongConstant") int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.CLIP_SAVE_FLAG);
            mDstRect.set(ml, mt, ml + itemW, mt + mBmpHeight);
            canvas.drawCircle(mDstRect.centerX(), mDstRect.centerY(), mBmpHeight / 2, mPaint);
            canvas.drawBitmap(mTempBmp, src, mDstRect, pCrop);
            canvas.restoreToCount(sc);

            mDstRect.set(ml, mDstRect.bottom, ml + itemW, mDstRect.bottom + mTextbmpHeight);
            canvas.drawBitmap(mTempBmpText, srcText, mDstRect, mPaint);
        } else if (mRecorderFilter0 != null) {
            int ml = (getWidth() - mRecorderFilter0.getWidth()) / 2;
            int mt = 0;
            mDstRect.set(ml, mt, ml + mRecorderFilter0.getWidth(), mt + mRecorderFilter0.getHeight());
            canvas.drawBitmap(mRecorderFilter0, mRecorderFilter0Rect, mDstRect, mPaint);
        }
    }


    private Bitmap mTempBmp, mTempBmpText;
    private double filterProportion;
    private Rect src, srcText;
    private int itemW, dp2px = ScreenUtil.dip2px(getContext(),3);
    private int mBmpHeight = ScreenUtil.dip2px(getContext(),24);
    private int mTextbmpHeight = ScreenUtil.dip2px(getContext(),5);


    private int FILTER_SRCBMP_SIZE = 80;
    private BitmapFactory.Options vpo = null;


    private Bitmap getRoundBmp(int drawableId) {
        Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), drawableId);
        if (null == vpo) {
            vpo = new BitmapFactory.Options();
            vpo.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.icon_video_topbar_filter_nor, vpo);
        }
        Bitmap scaledBitmap = BitmapUtils.getScaleBitmap(tempBitmap, FILTER_SRCBMP_SIZE, FILTER_SRCBMP_SIZE);

        if (null != tempBitmap) {
            tempBitmap.recycle();
        }
        Bitmap roundBmp = Bitmap.createBitmap(FILTER_SRCBMP_SIZE, FILTER_SRCBMP_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundBmp);
        canvas.drawColor(Color.TRANSPARENT);
        int srcW = scaledBitmap.getWidth();
        int srcH = scaledBitmap.getHeight();
        //int padding = (vpo.outWidth - srcW) / 2;
        canvas.drawBitmap(scaledBitmap, new Rect(0, 0, srcW, srcH), new Rect(0, 0, srcW, srcH), paintImage);
        if (null != scaledBitmap) {
            scaledBitmap.recycle();
        }
        return roundBmp;
    }

    public void setRoundSrc(boolean left2Right, int left, int center, int right, double filterProportion, int strLeft, int strCenter, int strRight) {
        setSrc(left2Right, getRoundBmp(left), getRoundBmp(center), getRoundBmp(right), filterProportion, strLeft, strCenter, strRight);
    }

    /**
     * @param left2Right       true,从右往左; false 从左往右
     * @param left
     * @param center
     * @param right
     * @param filterProportion
     */
    public void setSrc(boolean left2Right, Bitmap left, Bitmap center, Bitmap right, double filterProportion, int strLeft, int strCenter, int strRight) {
        this.filterProportion = filterProportion;
        itemW = left.getWidth();
        int mw = itemW * 3;
//        mBmpHeight = left.getHeight();
        mTempBmp = Bitmap.createBitmap(mw, mBmpHeight, Bitmap.Config.ARGB_8888);
        mTextbmpHeight = textHeight[0] + dp2px;
        mTempBmpText = Bitmap.createBitmap(mw, mTextbmpHeight, Bitmap.Config.ARGB_8888);
        int mleft;
        if (left2Right) {
            mleft = itemW * 2 - (int) (itemW * filterProportion);
        } else {
            mleft = itemW - (int) (itemW * filterProportion);
        }
        Canvas canvas = new Canvas(mTempBmp);
        canvas.drawColor(Color.WHITE);
        int nw = left.getWidth();
        Rect rect = new Rect(0, 0, nw, mBmpHeight);
        canvas.drawBitmap(left, rect, rect, mPaint);
        Resources res = getResources();

        Rect tsrc = new Rect(rect);
        rect.offset(nw, 0);
        canvas.drawBitmap(center, tsrc, rect, mPaint);

        rect.offset(nw, 0);
        canvas.drawBitmap(right, tsrc, rect, mPaint);
        canvas.save();
        src.set(mleft, 0, mleft + itemW, mBmpHeight);
//        canvas = new Canvas(mTempBmpText);
//        int bline = textHeight[0] - textHeight[1] + dp2px;
//        String tstrLeft = res.getString(strLeft);
//        canvas.drawText(tstrLeft, (itemW - PaintUtils.getWidth(mPtext, tstrLeft)) / 2, bline, mPtext);
//        tstrLeft = res.getString(strCenter);
//        canvas.drawText(tstrLeft, (itemW - PaintUtils.getWidth(mPtext, tstrLeft)) / 2 + itemW, bline, mPtext);
//        tstrLeft = res.getString(strRight);
//        canvas.drawText(tstrLeft, (itemW - PaintUtils.getWidth(mPtext, tstrLeft)) / 2 + itemW * 2, bline, mPtext);
//        canvas.save();
//        srcText.set(mleft, 0, mleft + itemW, mTextbmpHeight);
        invalidate();
    }

    /**
     * @param left2Right       true,从右往左; false 从左往右
     * @param filterProportion
     */
    public void setProportion(boolean left2Right, double filterProportion) {
        this.filterProportion = filterProportion;
        int mleft = itemW;
        if (left2Right) {
            mleft = itemW * 2 - (int) (itemW * filterProportion);
        } else {
            mleft = itemW - (int) (itemW * filterProportion);
        }
        if (null != mTempBmp) {
            src.set(mleft, 0, mleft + itemW, itemW + mBmpHeight);
            srcText.set(mleft, 0, mleft + itemW, itemW + mTextbmpHeight);
            invalidate();
        }
    }

    public void setCancelingProportion(boolean left2Right, double filterProportion) {
        this.filterProportion = filterProportion;
        int mleft;
        if (left2Right) {
            mleft = itemW - (int) (itemW * filterProportion);
        } else {
            mleft = itemW * 2 - (int) (itemW * filterProportion);
        }
        if (null != mTempBmp) {
            src.set(mleft, 0, mleft + itemW, itemW + mBmpHeight);
            srcText.set(mleft, 0, mleft + itemW, itemW + mTextbmpHeight);
            invalidate();
        }

    }

    public void recycle() {
        if (null != mTempBmp) {
            if (!mTempBmp.isRecycled()) {
                mTempBmp.recycle();
            }
            mTempBmp = null;
        }
        if (null != mTempBmpText) {
            if (!mTempBmpText.isRecycled()) {
                mTempBmpText.recycle();
            }
            mTempBmpText = null;
        }
        if (null != mRecorderFilter0) {
            if (!mRecorderFilter0.isRecycled()) {
                mRecorderFilter0.recycle();
            }
            mRecorderFilter0 = null;
        }
        mPtext = null;
        mPaint = null;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
