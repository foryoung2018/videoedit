package com.wmlive.hhvideo.widget.ZTablayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * create by ggq
 */
public class IndicationTabLayout extends LinearLayout {
    //指示器
    private int mSelectedIndicatorHeight;
    private Paint mSelectedIndicatorPaint;
    private int mSelectedPosition = -1;
    private float mSelectionOffset;
    private int mIndicatorLeft = -1;
    private int mIndicatorRight = -1;


    public IndicationTabLayout(Context context) {
        this(context, null);
    }

    public IndicationTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicationTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        mSelectedIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setSelectedIndicatorColor(int color) {
        if (mSelectedIndicatorPaint.getColor() != color) {
            mSelectedIndicatorPaint.setColor(color);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setSelectedIndicatorHeight(int height) {
        if (mSelectedIndicatorHeight != height) {
            mSelectedIndicatorHeight = height;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        updateIndicatorPosition();
    }

    private void updateIndicatorPosition() {
        final View selectedTitle = getChildAt(mSelectedPosition);
        int left, right;

        if (selectedTitle != null && selectedTitle.getWidth() > 0) {
            int selectTextPadding = (int) ((selectedTitle.getWidth() - measureTextLength(selectedTitle)) / 2 + 0.5f);
            left = selectedTitle.getLeft() + selectTextPadding;
            right = selectedTitle.getRight() - selectTextPadding;

            if (mSelectionOffset > 0f && mSelectedPosition < getChildCount() - 1) {
                View nextTitle = getChildAt(mSelectedPosition + 1);
                int textPadding = (int) ((nextTitle.getWidth() - measureTextLength(nextTitle)) / 2 + 0.5f);
                int moveLeft = nextTitle.getLeft() + textPadding - left;
                int moveRight = nextTitle.getRight() - textPadding - right;
                left = (int) (left + moveLeft * mSelectionOffset);
                right = (int) (right + moveRight * mSelectionOffset);
            }
        } else {
            left = right = -1;
        }
        setIndicatorPosition(left, right);
    }

    private void setIndicatorPosition(int left, int right) {
        if (left != mIndicatorLeft || right != mIndicatorRight) {
            mIndicatorLeft = left;
            mIndicatorRight = right;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    private float measureTextLength(View measureView) {
        if (measureView instanceof TextView) {
            TextView textView = ((TextView) measureView);
            String text = textView.getText().toString();
            return textView.getPaint().measureText(text);
        }
        return 0;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mIndicatorLeft >= 0 && mIndicatorRight > mIndicatorLeft) {
            canvas.drawRect(mIndicatorLeft, getHeight() - mSelectedIndicatorHeight,
                    mIndicatorRight, getHeight(), mSelectedIndicatorPaint);
        }
    }
}
