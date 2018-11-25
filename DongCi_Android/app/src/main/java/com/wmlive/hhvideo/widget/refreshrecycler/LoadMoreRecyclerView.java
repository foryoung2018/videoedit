package com.wmlive.hhvideo.widget.refreshrecycler;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import static com.wmlive.hhvideo.widget.refreshrecycler.FooterStatusHandle.TYPE_LOADING_MORE;

/**
 * Created by lsq on 6/20/2017.
 * 可加载更多的RecyclerView
 */

public class LoadMoreRecyclerView extends RecyclerView {
    boolean mIsLoadMore = false;  //是否正在加载更多
    private RefreshRecyclerView mRefreshRecycleView;
    private float scale = 1f;

    @Deprecated   //不可用
    public LoadMoreRecyclerView(Context context) {
        super(context);
    }

    public LoadMoreRecyclerView(Context context, RefreshRecyclerView view) {
        super(context);
        mRefreshRecycleView = view;
        post(new Runnable() {
            @Override
            public void run() {
                if (getBottom() != 0 && getChildAt(findLastVisibleItemPosition()) != null
                        && getBottom() >= getChildAt(findLastVisibleItemPosition()).getBottom()) {
                    // 最后一条正在显示的子视图在RecyclerView的上面, 说明子视图未充满RecyclerView
                    getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_IDLE && // 停止滚动
                mRefreshRecycleView.canLoadMore
                && mRefreshRecycleView.onLoadMoreListener != null  // 可以加载更多, 且有加载监听
                && isUp
                && !mIsLoadMore
                && findLastVisibleItemPosition() == getLayoutManager().getItemCount() - 1) { // 滚动到了最后一个子视图
//            if (mRefreshRecycleView.footerStatusHandle != FooterStatusHandle.TYPE_ERROR) {//加载失败的状态下点击才能加载
            if (mRefreshRecycleView.isRefreshing()) {
                return;
            }
            mRefreshRecycleView.setFooterStatus(TYPE_LOADING_MORE);
            mRefreshRecycleView.onLoadMoreListener.onLoadMore(); // 执行加载更多
//            }
        }
    }

//        @Override
//        protected void onMeasure(int widthSpec, int heightSpec) {
//            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//            super.onMeasure(widthSpec, expandSpec);
//        }

    private float oldY = 0.0f;
    private float newY = 0.0f;
    private boolean isUp = false;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                newY = e.getY();
                //下拉
                if (newY - oldY >= ViewConfiguration.get(getContext()).getScaledTouchSlop() * 1.0f) {
                    isUp = false;
                }
                //上拉
                else if (oldY - newY >= ViewConfiguration.get(getContext()).getScaledTouchSlop() * 1.0f) {
                    isUp = true;
                }
                oldY = newY;
                break;

            case MotionEvent.ACTION_UP:
                oldY = 0.0f;
                newY = 0.0f;
                break;
        }
        return super.onTouchEvent(e);
    }

    public void setFlingScale(float scale) {
        this.scale = scale;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= scale;
        return super.fling(velocityX, velocityY);
    }


    /**
     * @return 获取最后一个可见视图的位置
     */
    public int findLastVisibleItemPosition() {
        LayoutManager manager = getLayoutManager();
        // 获取最后一个正在显示的View
        if (manager instanceof GridLayoutManager) {
            return ((GridLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) manager).getSpanCount()];
            ((StaggeredGridLayoutManager) manager).findLastVisibleItemPositions(into);
            return findMax(into);
        }
        return -1;
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
