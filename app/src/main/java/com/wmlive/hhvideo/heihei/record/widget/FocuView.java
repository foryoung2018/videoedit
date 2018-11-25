package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;


import com.wmlive.hhvideo.heihei.record.utils.CoreUtils;

import cn.wmlive.hhvideo.R;


/**
 * 相机聚焦
 * Created by JIAN on 2017/6/1.
 */

public class FocuView extends View {
    public FocuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.transparent_white));
        rad = CoreUtils.dpToPixel(35);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private Paint mPaint;

    private boolean candraw = false;
    private int dx, dy;
    private int rad = 25;

    void setLocation(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(rad, rad);
        fp.leftMargin = dx - (rad / 2);
        fp.topMargin = dy - (rad / 2);
        fp.rightMargin = 0;
        fp.bottomMargin = 0;
        setLayoutParams(fp);
        FocuView.this.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha_in));
        setVisibility(View.VISIBLE);
        removeCallbacks(mRunnable);
        this.postDelayed(mRunnable, 800);
    }

    void removeAll() {
        this.removeCallbacks(mRunnable);
        alphaGone();
    }

    private void alphaGone() {
        if (getVisibility() == View.VISIBLE) {
            FocuView.this.setVisibility(View.GONE);
            FocuView.this.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha_out));
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            alphaGone();
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(dx, dy, rad, mPaint);
        canvas.drawCircle(dx, dy, 15, mPaint);
    }
}
