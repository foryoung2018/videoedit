package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JIAN on 2017/5/22.
 */

public class ExtHorizontalScrollView extends HorizontalScrollView {
    private List<ScrollViewListener> mScrollListenerList = null;
    private boolean mAppScroll = true;
    private Handler mHandler;
    private int mLastScrollX;
    private String TAG = "ExtHorizontalScrollView";
    private boolean mIsScrolling;
    private Runnable mScrollEndedRunnable = new Runnable() {
        @Override
        public void run() {
            mIsScrolling = false;
            for (ScrollViewListener listener : mScrollListenerList) {
                listener.onScrollEnd(ExtHorizontalScrollView.this,
                        getScrollX(), getScrollY(), mAppScroll);
            }
            mAppScroll = true;
        }
    };

    public ExtHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TAG = ExtHorizontalScrollView.this.toString();
        mScrollListenerList = new ArrayList<ScrollViewListener>();
        mHandler = new Handler();
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mAppScroll = false;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isTouching = true;
            mHandler.removeCallbacks(mScrollEndedRunnable);
            for (ScrollViewListener listener : mScrollListenerList) {
                listener.onScrollBegin(this, getScrollX(), getScrollY(),
                        false);
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            mHandler.removeCallbacks(mScrollEndedRunnable);
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            isTouching = false;
            mHandler.removeCallbacks(mScrollEndedRunnable);
            mHandler.postDelayed(mScrollEndedRunnable, 300);
        }
        return super.onTouchEvent(ev);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mAppScroll = false;
        mHandler.removeCallbacks(mScrollEndedRunnable);
        mHandler.postDelayed(mScrollEndedRunnable, 300);
    }

    /**
     * @param listener The listener
     */
    public void addScrollListener(ScrollViewListener listener) {
        if (null != listener) {
            mScrollListenerList.add(listener);
        }
    }

    /**
     * @param listener The listener
     */
    public void removeScrollListener(ScrollViewListener listener) {
        if (null != listener) {
            mScrollListenerList.remove(listener);
        }
    }

    /**
     * @return true if scrolling is in progress
     */
    public boolean isScrolling() {
        return mIsScrolling;
    }

    /**
     * The app wants to scroll (as opposed to the user)
     *
     * @param scrollX Horizontal scroll position
     * @param smooth  true to scroll smoothly
     */
    public void appScrollTo(int scrollX, boolean smooth) {
        if (getScrollX() == scrollX) {

        } else {

            mAppScroll = true;

            if (smooth) {
                smoothScrollTo(scrollX, 0);
            } else {
                scrollTo(scrollX, 0);
            }
        }
    }

    /**
     * The app wants to scroll (as opposed to the user)
     *
     * @param scrollX Horizontal scroll offset
     * @param smooth  true to scroll smoothly
     */
    public void appScrollBy(int scrollX, boolean smooth) {

        mAppScroll = true;

        if (smooth) {
            smoothScrollBy(scrollX, 0);
        } else {
            scrollBy(scrollX, 0);
        }
    }

    /**
     * 检测手势是否离开
     */
    private boolean isTouching = false;

    @Override
    public void computeScroll() {
        super.computeScroll();
        int scrollX = getScrollX();

        if (mLastScrollX != scrollX) {
            mLastScrollX = scrollX;
            if (!isTouching) {
                mHandler.removeCallbacks(mScrollEndedRunnable);
                mHandler.postDelayed(mScrollEndedRunnable, 200);
            }
            int scrollY = getScrollY();
            if (mIsScrolling) {
//                Log.e(TAG, "puteScroll--->: ");
                for (ScrollViewListener listener : mScrollListenerList) {
                    listener.onScrollProgress(this, scrollX, scrollY,
                            mAppScroll);
                }
            } else {
                mIsScrolling = true;
            }
        }
    }

    public static interface ScrollViewListener {
        public abstract void onScrollBegin(View view, int scrollX, int scrollY,
                                           boolean appScroll);

        public abstract void onScrollProgress(View view, int scrollX, int scrollY,
                                              boolean appScroll);

        public abstract void onScrollEnd(View view, int scrollX, int scrollY,
                                         boolean appScroll);
    }


}
