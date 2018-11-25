package com.wmlive.hhvideo.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.KLog;

import cn.wmlive.hhvideo.R;

/**
 * 视频的跑马灯效果
 * Created by vhawk on 2017/6/22.
 * modify by lsq
 */

public class MarqueeView extends HorizontalScrollView {

    private static final String TAG = "MarqueeView";
    private TextView tvName1;
    private TextView tvName2;
    private TextView tvName3;
    private AnimatorSet animatorSet;

    private int animDuring = 6000;
    private float textViewWidth;

    public MarqueeView(Context context) {
        super(context);
        initView(context);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setHorizontalScrollBarEnabled(false);
        setScrollBarSize(0);
        View view = View.inflate(context, R.layout.view_marquee, this);
        tvName1 = (TextView) view.findViewById(R.id.tv_s1);
        tvName2 = (TextView) view.findViewById(R.id.tv_s2);
        tvName3 = (TextView) view.findViewById(R.id.tv_s3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public void setTextContent(String textContent) {
        cancel();
        if (!TextUtils.isEmpty(textContent)) {
            tvName1.setText(textContent);
            tvName2.setText(textContent);
            tvName3.setText(textContent);
        }
    }

    public void start() {
        cancel();
        if (animatorSet != null) {
            if (!animatorSet.isStarted()) {
                initAnim();
            }
        } else {
            initAnim();
        }
        animatorSet.start();
    }

    private void initAnim() {
        int viewWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tvName1.measure(viewWidth, height);
        textViewWidth = tvName1.getMeasuredWidth();
        animDuring = (int) (textViewWidth * 8);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(createAnim(tvName1), createAnim(tvName2), createAnim(tvName3));
    }

    private ObjectAnimator createAnim(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0f, -textViewWidth);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(animDuring);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        return animator;
    }

    public void pause() {
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.pause();
            KLog.v(TAG, "pause");
        }
    }

    public void resume() {
        if (animatorSet != null && animatorSet.isPaused()) {
            animatorSet.resume();
            KLog.v(TAG, "resume");
        }
    }

    public boolean isStarted() {
        if (animatorSet != null) {
            return animatorSet.isStarted();
        }
        return false;
    }

    public void cancel() {
        KLog.v(TAG, "cancel");
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet = null;
        }
        if (tvName1 != null) {
            tvName1.clearAnimation();
        }
        if (tvName2 != null) {
            tvName2.clearAnimation();
        }
        if (tvName3 != null) {
            tvName3.clearAnimation();
        }
    }
}
