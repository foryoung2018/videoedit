package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.wmlive.hhvideo.utils.KLog;

/**
 * Created by Administrator on 2018/6/14.
 */

public class TopDeleteView extends FrameLayout {
    public TopDeleteView(@NonNull Context context) {
        super(context);
    }

    public TopDeleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopDeleteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        KLog.d("event=="+event.getAction());


        return super.onTouchEvent(event);
    }
}
