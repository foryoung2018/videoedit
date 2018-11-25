package com.dongci.sun.gpuimglibrary.common;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.dongci.sun.gpuimglibrary.R;

/**
 * 支持设置环形border
 *
 * @author JIAN
 */
public class ExtCircleImageView extends android.support.v7.widget.AppCompatImageView implements Checkable {

    private int borderWidth = 5;
    private int drawBorderColor = 0;
    private int drawBgColor = 0;

    public ExtCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray tArray = context.obtainStyledAttributes(attrs,
                R.styleable.ExtCircle);

        ischecked = tArray.getBoolean(R.styleable.ExtCircle_circleChecked,
                false);

        Resources res = getResources();

        drawBgColor = tArray.getInt(R.styleable.ExtCircle_circleBgColor,
                res.getColor(R.color.transparent));
        drawBorderColor = tArray.getInt(
                R.styleable.ExtCircle_circleBorderColor,
                res.getColor(R.color.main_orange));

        tArray.recycle();
    }

    public ExtCircleImageView(Context context) {
        super(context);
    }

    private int progress = 100;

    /**
     * 设置当前下载进度
     *
     * @param pro 未下载时设置为0
     */
    public void setProgress(int pro) {
        // Log.e("setProgress", pro + "xxxxx");
        progress = Math.min(100, pro);
        if (pro > 0) {
            if (!ischecked) {
                ischecked = true;
            }
        }
        this.invalidate();
    }

    private boolean isCircle = false;

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = this.getDrawable();
        if (drawable == null) {
            super.onDraw(canvas);
            return;
        }
        try {
            Bitmap bitmap = GraphicsHelper.getBitmap(drawable);
            if (null != bitmap) {
                int w = this.getWidth();
                int h = this.getHeight();

                GraphicsHelper.drawSqareCornerBitmap(canvas, w, h, bitmap,
                        w / 2, borderWidth, drawBorderColor, drawBgColor,
                        ischecked, progress);

                bitmap.recycle();
            }
        } catch (Exception e) {
        }
    }


    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setBorderColor(int borderColor) {
        this.drawBorderColor = borderColor;
        invalidate();
    }

    public void setBgColor(int bgColor) {
        this.drawBgColor = bgColor;
        invalidate();
    }

    @Override
    public boolean isChecked() {
        return ischecked;
    }

    @Override
    public void toggle() {
    }

    private boolean ischecked = false;

    @Override
    public void setChecked(boolean checked) {
        // Log.e("setcheked", checked + "..." + this.toString());
        ischecked = checked;
        invalidate();
    }

}