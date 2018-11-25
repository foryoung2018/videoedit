package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 专用于infoFragment(个人页)
 * <p>
 * 不能左右滑动，且滑动到第二个tab，可以直接滑动到首页
 */
public class InfoViewPager extends ViewPager {
    private int startX;
    private int startY;

    private ViewPager mainViewpager;

    public InfoViewPager(Context context) {
        super(context);
    }

    public InfoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            startX = (int) ev.getRawX();
//            startY = (int) ev.getRawY();
//            getParent().requestDisallowInterceptTouchEvent(true);
//        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            int endX = (int) ev.getRawX();
//            int endY = (int) ev.getRawY();
//            if (Math.abs(endX - startX) > Math.abs(endY - startY)) {
//                //左右滑动
//                if (null != mainViewpager) {
//                    mainViewpager.requestDisallowInterceptTouchEvent(false);
//                    mainViewpager.onTouchEvent(ev);
//                }
//            } else {
//                //上下滑动
//                if (null != mainViewpager) {
//                    mainViewpager.requestDisallowInterceptTouchEvent(true);
//                }
//            }
//        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public void setMainViewpager(ViewPager mainViewpager) {
        this.mainViewpager = mainViewpager;
    }
}