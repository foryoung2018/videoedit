package com.wmlive.hhvideo.utils;

import android.view.View;

/**
 * 防止快速点击
 * Created by Administrator on 9/15/2016.
 */
public abstract class MyClickListener implements View.OnClickListener {
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;
    private int lastViewId;//上次点击的控件id
    private int clickDelay = MIN_CLICK_DELAY_TIME;

    public MyClickListener() {
    }

    public MyClickListener(int clickDelay) {
        this.clickDelay = clickDelay;
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if ((lastViewId == v.getId())) {
            if (currentTime - lastClickTime > clickDelay) {
                onMyClick(v);
            } else {
                KLog.i("========快速点击无效");
                return;
            }
        } else {
            onMyClick(v);
        }
        lastViewId = v.getId();
        lastClickTime = currentTime;
    }

    protected abstract void onMyClick(View v);
}
