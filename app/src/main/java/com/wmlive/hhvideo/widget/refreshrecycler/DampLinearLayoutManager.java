package com.wmlive.hhvideo.widget.refreshrecycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class DampLinearLayoutManager extends LinearLayoutManager {
    private float speedRatio = 1f;
    private boolean scrollEnabled = true;

    public DampLinearLayoutManager(Context context) {
        super(context);
    }

    public DampLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public DampLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int a = super.scrollHorizontallyBy((int) (speedRatio * dx), recycler, state);
        if (a == (int) (speedRatio * dx)) {
            return dx;
        }
        return a;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int a = super.scrollVerticallyBy((int) (speedRatio * dy), recycler, state);
        if (a == (int) (speedRatio * dy)) {
            return dy;
        }
        return a;
    }

    @Override
    public boolean canScrollVertically() {
        return scrollEnabled && super.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally() {
        return scrollEnabled && super.canScrollHorizontally();
    }

    public void setSpeedRatio(float speedRatio) {
        this.speedRatio = speedRatio;
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        this.scrollEnabled = scrollEnabled;
    }
}
