package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * @author scott
 */
public class ThumbnailScrollView extends HorizontalScrollView {

    private boolean canTouch = true;
    private OnThumbnailScrollChangeListener mListener;
    private long delayMillis = 100;
    private long lastScrollUpdate = -1;

    public ThumbnailScrollView(Context context) {
        super(context);
    }

    public ThumbnailScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbnailScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (lastScrollUpdate == -1) {
            if (mListener != null) {
                mListener.onScrollStart(l, t, oldl, oldt);
            }
            postDelayed(scrollerTask, delayMillis);
        }
        // 更新ScrollView的滑动时间
        lastScrollUpdate = System.currentTimeMillis();
        if (mListener != null) {
            mListener.onThumbnailScroll(l, t, oldl, oldt);
        }
    }


    private Runnable scrollerTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > 100) {
                lastScrollUpdate = -1;
                if (mListener != null) {
                    mListener.onScrollEnd();
                }
            } else {
                postDelayed(this, delayMillis);
            }
        }
    };


    public void setOnThumbnailScrollChangeListener(OnThumbnailScrollChangeListener listener) {
        mListener = listener;
    }

    public interface OnThumbnailScrollChangeListener {
        void onScrollStart(int l, int t, int oldl, int oldt);
        void onThumbnailScroll(int left, int top, int oldLeft, int oldTop);
        void onScrollEnd();
    }

    public boolean isCanTouch() {
        return canTouch;
    }

    public void setCanTouch(boolean canTouch) {
        this.canTouch = canTouch;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (canTouch) {
            return super.onTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (scrollerTask != null) {
            removeCallbacks(scrollerTask);
        }
    }

    @Override
    public void fling(int velocityX) {
        super.fling(velocityX / 1000);
    }
}
