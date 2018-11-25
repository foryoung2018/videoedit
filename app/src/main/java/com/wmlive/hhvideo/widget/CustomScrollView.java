package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.wmlive.hhvideo.utils.KLog;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * Created by hsing on 2018/1/16.
 */

//public class CustomScrollView extends ScrollView {
public class CustomScrollView extends NestedScrollView {

    private OnScrollChangedListener onScrollChangedListener;
    private long delayMillis = 100;
    private int mTouchSlop;
    private float downX;
    private float downY;
    private OnLoadMoreListener onLoadMoreListener;
    private double lastScrollUpdate = -1;
    private int scrollState = SCROLL_STATE_IDLE;
    private int scrollDistance = 0;

    public CustomScrollView(Context context) {
        super(context);
        init();
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//        setNestedScrollingEnabled(true);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        scrollDistance = t;
//        KLog.i("======CustomScrollView l:" + l + " ,t:" + t + " ,oldl:" + oldl + " ,oldt:" + oldt + " ,ScrollbarPosition:" + getVerticalScrollbarPosition());
        if (lastScrollUpdate == -1) {
            postDelayed(scrollerTask, delayMillis);
        }
        lastScrollUpdate = System.currentTimeMillis();
        checkPosition();
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnScrollListener(OnScrollChangedListener listener) {
        onScrollChangedListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        onLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = e.getRawX();
                downY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
                float cY = e.getRawY();
//                KLog.i("======CustomScrollView downY:" + downY + " ,cY:" + cY);
                if (scrollDistance == 0 && (cY > downY)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    private void checkPosition() {
        if (Math.abs(this.getChildAt(0).getHeight() - this.getHeight() - this.getScrollY()) < 5) {
            KLog.i("xxxx", "滑动到底部 onLoadMore");
            if (onLoadMoreListener != null) {
                onLoadMoreListener.onLoadMore();
            }
        }
    }

    private Runnable scrollerTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > 100) {
                lastScrollUpdate = -1;
            } else {
                postDelayed(this, delayMillis);
            }
        }
    };
}
