package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import com.wmlive.hhvideo.heihei.record.utils.CoreUtils;

import cn.wmlive.hhvideo.R;


/**
 * 截取音乐组件显示文本
 * Created by JIAN on 2017/5/15.
 */

public class CropViewBg extends View {


    private float itemWidth = 5;
    private int mItemHorSpacing = 10;
    private final int mMarginLeft = CoreUtils.dpToPixel(50);

    public CropViewBg(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Resources res = getResources();
        tranblack60 = res.getColor(R.color.transparent);
        pleft.setAntiAlias(true);
        pleft.setColor(tranblack60);


    }

    private int descentLine = 10;


    private Paint pleft = new Paint();
    private RectF rectItem = new RectF();


    private Rect itemDuration = new Rect();

    private int mrLine = 5, mTextRMargin = 5;

    /**
     * 阴影区域
     *
     * @param rect        阴影区域
     * @param rightMargin 右把手距离右边框的距离
     * @param textRMargin 左右移动把手距离右边框的像素
     */
    public void setItemDuration(Rect rect, int rightMargin, int textRMargin) {

//        Log.e("setItemDuration", "setItemDuration: " + rect.toShortString() + "...." + rightMargin);

        itemDuration.set(rect.left, rect.top, rect.right, rect.bottom);

        mPathLeft.moveTo(itemDuration.left, 0);
        mPathLeft.lineTo(itemDuration.left, getBottom());

        mrLine = getRight() - rightMargin;
        mPathRight.moveTo(mrLine, 0);
        mPathRight.lineTo(mrLine, getBottom());
        mTextRMargin = getRight() - textRMargin;
        invalidate();
    }

    private boolean enableDrawbg = false;

    public void enableDrawBg(boolean enable) {
        enableDrawbg = enable;
        invalidate();
    }

    private Path mPathLeft = new Path(), mPathRight = new Path();
    private Rect rect = new Rect();
    private int tranblack60;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(tranblack60);
        canvas.drawRect(new Rect(getLeft(), itemDuration.top, itemDuration.left, getBottom()), pleft);
        canvas.drawRect(new Rect(itemDuration.right, itemDuration.top, getRight(), getBottom()), pleft);

    }

    private int min;

    public void setMin(int mMin) {
        min = mMin;
        invalidate();
    }

    /**
     * 清除资源
     */
    public void recycle() {

    }


}
