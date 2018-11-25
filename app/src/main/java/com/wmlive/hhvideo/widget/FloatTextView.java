package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.wmlive.hhvideo.utils.KLog;

/**
 * Created by Administrator on 3/16/2018.
 */

public class FloatTextView extends AppCompatTextView implements View.OnTouchListener {

    private int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    public FloatTextView(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public FloatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public FloatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    private float downX;
    private float downY;
    private float lastX;
    private float lastY;
    private boolean isTouch;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = false;
                downX = event.getRawX();
                downY = event.getRawY();
                lastX = downX;
                lastY = downY;
                return false;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getRawX();
                float currentY = event.getRawY();
                float moveX;
                float moveY;
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    moveX = currentX - lastX;
                    moveY = currentY - lastY;
                    KLog.i("====移动==x:" + moveX + " ,y:" + moveY);
                    marginLayoutParams.leftMargin += moveX;
                    marginLayoutParams.topMargin += moveY;
                    setLayoutParams(marginLayoutParams);
                }
                lastX = currentX;
                lastY = currentY;
                isTouch = Math.abs(downX - currentX) > touchSlop || Math.abs(downY - currentY) > touchSlop;
                return isTouch;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return isTouch;
            default:
                break;
        }
        return false;
    }
}
