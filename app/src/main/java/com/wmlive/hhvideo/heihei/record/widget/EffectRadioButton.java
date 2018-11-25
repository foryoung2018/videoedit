package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import cn.wmlive.hhvideo.R;


/**
 * 特效按钮
 *
 * @author scott
 */

public class EffectRadioButton extends android.support.v7.widget.AppCompatRadioButton {
    Drawable mDrawableTop;
    Drawable mDrawableSelected;
    private float mAlpha;

    public EffectRadioButton(Context context) {
        super(context);
    }

    public EffectRadioButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EffectRadioButton);
        mAlpha = typedArray.getFloat(R.styleable.EffectRadioButton_rbUncheckedAlpha, 0.5f);
        initView();
    }


    private void initView() {
        // 获取drawableTop
        mDrawableTop = getCompoundDrawables()[1];
        mDrawableSelected = getResources().getDrawable(R.drawable.effect_time_selected);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int selectLeft = mDrawableTop.getIntrinsicWidth() + getPaddingLeft() - mDrawableSelected.getIntrinsicWidth();
        int selectTop = mDrawableTop.getIntrinsicHeight() + getPaddingTop() - mDrawableSelected.getIntrinsicHeight();

        if (isChecked()) {
            setAlpha(1f);
        } else {
            setAlpha(mAlpha);
        }
        super.onDraw(canvas);

        if (isChecked()) {
            mDrawableSelected.setBounds(selectLeft, selectTop, selectLeft + mDrawableSelected.getIntrinsicWidth(),
                    selectTop + mDrawableSelected.getIntrinsicHeight());
            mDrawableSelected.draw(canvas);
        }

    }
}
