package com.wmlive.hhvideo.heihei.personal.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lsq on 6/12/2017.
 * 解决WebView与Swiperefreshlayout下拉刷新冲突的问题
 */

public class EnhanceSwipeRefreshLayout extends SwipeRefreshLayout {
    public EnhanceSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public EnhanceSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private CanChildScrollUpCallback mCanChildScrollUpCallback;

    public interface CanChildScrollUpCallback {
        boolean canSwipeRefreshChildScrollUp();
    }

    public void setCanChildScrollUpCallback(CanChildScrollUpCallback canChildScrollUpCallback) {
        mCanChildScrollUpCallback = canChildScrollUpCallback;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mCanChildScrollUpCallback != null) {
            return mCanChildScrollUpCallback.canSwipeRefreshChildScrollUp();
        }
        return super.canChildScrollUp();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return !isRefreshing() && super.onStartNestedScroll(child, target, nestedScrollAxes);
    }
}
