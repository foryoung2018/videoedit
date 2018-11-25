package com.wmlive.hhvideo.widget.refreshrecycler;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.KLog;

import cn.wmlive.hhvideo.R;

import static android.support.v7.widget.RecyclerView.LayoutManager;
import static com.wmlive.hhvideo.widget.refreshrecycler.FooterStatusHandle.TYPE_LOADING_MORE;


/**
 * 可刷新和加载更多的RecycleView, 也可分页，使用时会填充整个父布局
 */
public class RefreshRecyclerView extends SwipeRefreshRelativeLayout {

    private LoadMoreRecyclerView recyclerView;
    OnLoadMoreListener onLoadMoreListener;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    private OnFooterClickListener onFooterClickListener;
    private OnRecyclerScrollListener onRecyclerScrollListener;
    boolean canLoadMore = true;  //是否能加载更多
    boolean loadMoreEnable = true;  //是否需要加载更多功能
    boolean refreshEnable = false;  //是否需要下拉刷新功能
    boolean showFooter = true;      //是否显示Footer
    private View headerView;             // 头部和脚部
    private View footerView;             // 头部和脚部
    private View emptyView;             // 空白页
    FooterStatusHandle footerStatusHandle;  //Footer状态
    private LayoutManager layoutManager;
    private RefreshAdapter refreshAdapter;
    private View customEmptyView;
    private boolean canScrollVertical;
    private float startX;
    private float startY;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        recyclerView = new LoadMoreRecyclerView(context, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        addView(recyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setColorSchemeColors(
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimaryDark));
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            initLayoutManager(layoutManager);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (onRecyclerScrollListener != null) {
                    onRecyclerScrollListener.onScroll(recyclerView, dx, dy);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        setEnabled(refreshEnable);
    }


    public LoadMoreRecyclerView getRecycleView() {
        return recyclerView;
    }


    public void setAdapter(final RefreshAdapter adapter) {
        refreshAdapter = adapter;
        recyclerView.setAdapter(refreshAdapter);
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (adapter.isHeader(position) || adapter.isEmptyOrError(position) || (adapter.isFooter(position) && adapter.hasFooter()))
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }


    public RefreshAdapter getAdapter() {
        return refreshAdapter;
    }

    private void initLayoutManager(final LayoutManager manager) {
        layoutManager = manager;
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getAdapter().isFooter(position) || getAdapter().isHeader(position) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
        }

        recyclerView.setLayoutManager(layoutManager);
        initDefaultFooter();
        setEmptyView();
    }

    public void setLayoutManager(LayoutManager manager) {
        layoutManager = manager;
        recyclerView.setLayoutManager(layoutManager);
    }

    public void setPageSnapEnable() {
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
    }


    public LayoutManager getLayoutManager() {
        return recyclerView.getLayoutManager();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return !isRefreshing() && super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        recyclerView.addItemDecoration(decor);
    }


    /**
     * 设置下拉圈的起始位置和最大位置
     */
    public void setOffset() {
        setProgressViewOffset(true, -20, 40);
    }

    /**
     * 是否需要下拉刷新的功能
     *
     * @param refreshEnable
     */
    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
        setEnabled(refreshEnable);
    }

    /**
     * 是否需要加载更多的功能
     *
     * @param loadMoreEnable
     */
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        if (!loadMoreEnable) {
            footerView = null;
        } else {
            if (footerView == null) {
                initDefaultFooter();
            }
        }
    }

    public void setFlingScale(float scale) {
        if (recyclerView != null) {
            recyclerView.setFlingScale(scale);
        }
    }

    public boolean isLoadMoreEnable() {
        return loadMoreEnable;
    }

    /**
     * 设置是否还可以加载更多
     *
     * @param canLoadMore
     */
    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    /**
     * @return 是否还可以加载更多
     */
    public boolean getCanLoadMore() {
        return canLoadMore;
    }


    /**
     * footer的点击事件
     *
     * @param listener
     */
    public void setOnFooterClickListener(OnFooterClickListener listener) {
        onFooterClickListener = listener;
    }


    private void setEmptyView() {
        emptyView = LayoutInflater.from(getContext()).inflate(R.layout.view_empty, recyclerView, false);
        emptyView.setVisibility(INVISIBLE);
    }

    public void setNoUserEmptyView(boolean isShowEmptyView) {
        if (null != emptyView) {
            emptyView.setVisibility(isShowEmptyView ? VISIBLE : View.GONE);
        }
    }

    /**
     * 显示错误页
     */
    public void showError() {
        if (emptyView != null) {
            emptyView.setVisibility(VISIBLE);
            KLog.i("=======emptyView visible " + emptyView.getVisibility());
            TextView tvReload = (TextView) emptyView.findViewById(R.id.tvReload);
            tvReload.setVisibility(View.VISIBLE);
            TextView tvMessage = emptyView.findViewById(R.id.tvMessage);
            tvMessage.setVisibility(VISIBLE);
            tvMessage.setText("哎呀!网络不给力~");
            tvReload.setOnClickListener(new ClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (null != onFooterClickListener) {
                        onFooterClickListener.onPageErrorClick();
                    }
                }
            });
        }
    }

    /**
     * 显示空白页
     *
     * @param message
     */
    public void showEmpty(String message) {
        if (emptyView != null) {
            TextView textView = (TextView) emptyView.findViewById(R.id.tvMessage);
            textView.setText(message);
            textView.setVisibility(View.VISIBLE);
            emptyView.findViewById(R.id.tvReload).setVisibility(GONE);
        }
    }

    /**
     * 是否展示空界面 图片
     *
     * @param isShow
     */
    public void showEmptyViewImg(boolean isShow) {
        if (emptyView != null) {
            emptyView.findViewById(R.id.iv_img).setVisibility(isShow ? View.VISIBLE : GONE);
        }
    }

    public View getEmptyView() {
        if (customEmptyView != null) {
            return customEmptyView;
        }
        return emptyView;
    }

    /**
     * 设置下拉刷新的监听
     *
     * @param listener
     */
    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        if (null == listener) {
            setEnabled(false);
            refreshEnable = false;
        } else {
            refreshEnable = true;
            setEnabled(true);
            onRefreshListener = listener;
            super.setOnRefreshListener(onRefreshListener);
        }
    }

    /**
     * 设置加载更多监听
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        onLoadMoreListener = listener;
        setLoadMoreEnable(null != onLoadMoreListener);
    }

    public void setOnScrollListener(OnRecyclerScrollListener listener) {
        onRecyclerScrollListener = listener;
    }

    public void loadMore() {
        if (canLoadMore) {
            this.setFooterStatus(TYPE_LOADING_MORE);
            this.onLoadMoreListener.onLoadMore(); // 执行加载更多
        }
    }

    public void setCustomEmptyView(int resId) {
        setCustomEmptyView(resId, -1, "");
    }

    public void setCustomEmptyView(int resId, int viewHeight, String msg) {
        this.customEmptyView = LayoutInflater.from(getContext()).inflate(resId, recyclerView, false);
        TextView tvNoMore = customEmptyView.findViewById(R.id.tv_no_more);
        if (!TextUtils.isEmpty(msg)) {
            tvNoMore.setText(msg);
        }
        if (viewHeight != -1) {
            ViewGroup.LayoutParams layoutParams = customEmptyView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = viewHeight;
                customEmptyView.setLayoutParams(layoutParams);
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public interface OnRecyclerScrollListener {
        void onScroll(RecyclerView recyclerView, int dx, int dy);
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        autoRefresh(0);
    }

    /**
     * 自动刷新
     *
     * @param delay 延时
     */
    public void autoRefresh(int delay) {
        setFooterVisiable(GONE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.mIsLoadMore && !isRefreshing()) {
                    setRefreshing(true);
                    if (onRefreshListener != null) {
                        onRefreshListener.onRefresh();
                    }
                }
            }
        }, delay);
    }

    private void setFooterVisiable(int visiable) {
        if (footerView != null) {
            footerView.findViewById(R.id.ll_root).setVisibility(visiable);
        }
    }

    /**
     * 是否还有更多
     *
     * @param hasMore
     */
    public void setFooterHasMore(boolean hasMore) {
        setFooterStatus(hasMore ? FooterStatusHandle.TYPE_PULL_LOAD_MORE : FooterStatusHandle.TYPE_NO_MORE);
    }

    /**
     * 设置Footer显示状态
     */
    public void setFooterStatus(FooterStatusHandle type) {
        if (recyclerView == null) {
            throw new NullPointerException("-----recyclerView is null!!!");
        }
        footerStatusHandle = type;
        switch (type) {
            case TYPE_PULL_LOAD_MORE:
                recyclerView.mIsLoadMore = false;
                canLoadMore = true;
                break;
            case TYPE_LOADING_MORE:
                recyclerView.mIsLoadMore = true;
                canLoadMore = true;
                break;
            case TYPE_ERROR:
                recyclerView.mIsLoadMore = false;
                canLoadMore = true;
                break;
            case TYPE_NO_MORE:
                recyclerView.mIsLoadMore = false;
                canLoadMore = false;
                break;
        }
        refreshFooter(type);
        this.setEnabled(refreshEnable && type != TYPE_LOADING_MORE);
        if (this.isRefreshing()) {
            this.setRefreshing(false);
        }
    }

    private void refreshFooter(FooterStatusHandle type) {
        if (canLoadMore) {
            if (getFooter() != null) {
                getFooter().findViewById(R.id.ll_root).setVisibility(VISIBLE);
                if (type == FooterStatusHandle.TYPE_PULL_LOAD_MORE) {
                    getFooter().findViewById(R.id.ll_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_pull_load_more).setVisibility(VISIBLE);
                    getFooter().findViewById(R.id.tv_error).setVisibility(GONE);
                    return;
                }

                if (type == TYPE_LOADING_MORE) {
                    getFooter().findViewById(R.id.ll_more).setVisibility(VISIBLE);
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_pull_load_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_error).setVisibility(GONE);
                    return;
                }

                if (type == FooterStatusHandle.TYPE_ERROR) {
                    getFooter().findViewById(R.id.ll_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_pull_load_more).setVisibility(GONE);
                    getFooter().findViewById(R.id.tv_error).setVisibility(VISIBLE);
                }
            }
        } else {
            if (getFooter() != null) {
                getFooter().findViewById(R.id.ll_root).setVisibility(VISIBLE);
                getFooter().findViewById(R.id.ll_more).setVisibility(GONE);
                getFooter().findViewById(R.id.tv_pull_load_more).setVisibility(GONE);
                getFooter().findViewById(R.id.tv_error).setVisibility(GONE);
                if (getShowFooterWithNoMore()) {
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(VISIBLE);
                } else {
                    getFooter().findViewById(R.id.tv_no_more).setVisibility(GONE);
                }
            }
        }
    }


    /**
     * 添加头部
     *
     * @param headerView 作为头部的布局
     */
    public void setHeader(View headerView) {
        if (headerView != null && getAdapter() != null) {
            if (this.headerView != null) {
                this.headerView = headerView;
                getAdapter().notifyItemChanged(0);
            } else {
                this.headerView = headerView;
                getAdapter().notifyItemInserted(0);
            }
        }
    }

    /**
     * 添加头部
     */
    public void setHeader(int resId) {
        setHeader(LayoutInflater.from(getContext()).inflate(resId, recyclerView, false));
    }

    public View getHeader() {
        return headerView;
    }

    /**
     * 删除头部
     */
    public void removeHeader() {
        this.headerView = null;
    }

    /**
     * 添加脚部
     */
    public void setFooter(View footerView) {
        this.footerView = footerView;
    }


    /**
     * 设置mHeader是否可见
     *
     * @param visible
     */
    public void setHeaderVisible(boolean visible) {
        if (headerView != null) {
            int oldVisible = headerView.getVisibility();
            int newVisible = visible ? VISIBLE : GONE;
            if (oldVisible != newVisible) {
                headerView.setVisibility(newVisible);
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    /**
     * footerView
     *
     * @param visible
     */
    public void setFooterVisible(boolean visible) {
        if (footerView != null) {
            int oldVisible = footerView.getVisibility();
            int newVisible = visible ? VISIBLE : GONE;
            if (oldVisible != newVisible) {
                footerView.setVisibility(newVisible);
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    /**
     * 添加脚部
     */
    public void setFooter(int resId) {
        this.footerView = LayoutInflater.from(getContext()).inflate(resId, recyclerView, false);
        footerView.findViewById(R.id.tv_error).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFooterClickListener != null) {
                    setFooterStatus(TYPE_LOADING_MORE);
                    onFooterClickListener.onFootErrorClick();
                }
            }
        });

        footerView.findViewById(R.id.tv_pull_load_more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFooterClickListener != null) {
                    setFooterStatus(TYPE_LOADING_MORE);
                    onFooterClickListener.onLoadMoreClick();
                }
            }
        });
    }

    /**
     * 初始化默认的footer
     */
    private void initDefaultFooter() {
        setFooter(R.layout.view_recycler_load_more);
    }

    /**
     * @return 脚部视图
     */
    public View getFooter() {
        return footerView;
    }


//    //没有更多时是否显示Footer
//    public void setShowFooterWithNoMore(boolean show) {
//        showFooter = show;
//    }

    public boolean getShowFooterWithNoMore() {
        return showFooter;
    }

//    /**
//     * @return 获取最后一个可见视图的位置
//     */
//    public int findLastVisibleItemPosition() {
//        LayoutManager manager = recyclerView.getLayoutManager();
//        // 获取最后一个正在显示的View
//        if (manager instanceof GridLayoutManager) {
//            return ((GridLayoutManager) manager).findLastVisibleItemPosition();
//        } else if (manager instanceof LinearLayoutManager) {
//            return ((LinearLayoutManager) manager).findLastVisibleItemPosition();
//        } else if (manager instanceof StaggeredGridLayoutManager) {
//            int[] into = new int[((StaggeredGridLayoutManager) manager).getSpanCount()];
//            ((StaggeredGridLayoutManager) manager).findLastVisibleItemPositions(into);
//            return findMax(into);
//        }
//        return -1;
//    }

    /**
     * 设置子视图充满一行
     *
     * @param view 子视图
     */
    public void setFullSpan(View view, int height) {
        LayoutManager manager = getLayoutManager();
        // 根据布局设置参数, 使"加载更多"的布局充满一行
        if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                    StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                    height == -1 ? StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT : StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            params.setFullSpan(true);
            view.setLayoutParams(params);
        }
    }

//    /**
//     * 可分页加载更多的RecyclerView
//     */
//    public class LoadMoreRecyclerView extends RecyclerView {
//        private boolean mIsLoadMore = false;  //是否正在加载更多
//        private WeakReference<RefreshRecyclerView> mRefreshRecycleView;
//
//        public LoadMoreRecyclerView(Context context, RefreshRecyclerView view) {
//            super(context);
//            mRefreshRecycleView = new WeakReference<>(view);
//            post(new Runnable() {
//                @Override
//                public void run() {
//                    if (getBottom() != 0 && getChildAt(findLastVisibleItemPosition()) != null && getBottom() >= getChildAt(findLastVisibleItemPosition()).getBottom()) {
//                        // 最后一条正在显示的子视图在RecyclerView的上面, 说明子视图未充满RecyclerView
//                        getAdapter().notifyDataSetChanged();
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onScrollStateChanged(int state) {
//            super.onScrollStateChanged(state);
//            Log.i("onScrollStateChanged", "onScrollStateChanged: " + state);
//            RefreshRecyclerView view = mRefreshRecycleView.get();
//            if (view != null) {
//                if (state == SCROLL_STATE_IDLE && // 停止滚动
//                        view.canLoadMore
//                        && view.onLoadMoreListener != null  // 可以加载更多, 且有加载监听
//                        && isUp
//                        && !mIsLoadMore
//                        && view.findLastVisibleItemPosition() == view.getLayoutManager().getItemCount() - 1) { // 滚动到了最后一个子视图
//                    if (view.footerStatusHandle != FooterStatusHandle.TYPE_ERROR) {
//                        if (view.isRefreshing()) {
//                            return;
//                        }
//                        view.setFooterStatus(TYPE_LOADING_MORE);
//                        view.onLoadMoreListener.onLoadMore(); // 执行加载更多
//                    }
//                }
//            }
//        }
//
////        @Override
////        protected void onMeasure(int widthSpec, int heightSpec) {
////            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
////            super.onMeasure(widthSpec, expandSpec);
////        }
//
//        private float oldY = 0.0f;
//        private float newY = 0.0f;
//        private boolean isUp = false;
//
//        @Override
//        public boolean onTouchEvent(MotionEvent e) {
//            switch (e.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    oldY = e.getY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    newY = e.getY();
//                    //下拉
//                    if (newY - oldY >= ViewConfiguration.get(getContext()).getScaledTouchSlop() * 1.0f) {
//                        isUp = false;
//                    }
//                    //上拉
//                    else if (oldY - newY >= ViewConfiguration.get(getContext()).getScaledTouchSlop() * 1.0f) {
//                        isUp = true;
//                    }
//                    oldY = newY;
//                    break;
//
//                case MotionEvent.ACTION_UP:
//                    oldY = 0.0f;
//                    newY = 0.0f;
//                    break;
//            }
//            return super.onTouchEvent(e);
//        }
//
////        @Override
////        public boolean fling(int velocityX, int velocityY) {
////            velocityX = solveVelocity(velocityX);
////            velocityY = solveVelocity(velocityY);
////            return super.fling(velocityX, velocityY);
////        }
////
////        private int solveVelocity(int velocity) {
////            if (velocity > 0) {
////                return Math.min(velocity, 8000);
////            } else {
////                return Math.max(velocity, -8000);
////            }
////        }
//    }


    public void scrollToPosition(int position) {
        KLog.i("=====RefreshRecyclerView=scrollToPosition");
        recyclerView.scrollToPosition(position);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (recyclerView != null) {
                    canScrollVertical = recyclerView.canScrollVertically(-1);
                }
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
                if (!canScrollVertical) {
                    float x = ev.getX();
                    float y = ev.getY();
                    float deltaX = startX - x;
                    float deltaY = startY - y;
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        return false;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
