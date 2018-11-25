package com.wmlive.hhvideo.heihei.mainhome.widget.swipe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 2/8/2018.2:35 PM
 *
 * @author lsq
 * @describe 消息模块侧滑删除RecyclerView，Github链接 https://github.com/yanzhenjie/SwipeRecyclerView
 */

public class MessageRecyclerView extends SwipeRefreshLayout {
    private SwipeMenuRecyclerView swipeMenuRecyclerView;

    public MessageRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MessageRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        swipeMenuRecyclerView = new SwipeMenuRecyclerView(context);
        addView(swipeMenuRecyclerView,
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        swipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        DefineLoadMoreView loadMoreView = new DefineLoadMoreView(context);
        swipeMenuRecyclerView.addFooterView(loadMoreView);
        swipeMenuRecyclerView.setLoadMoreView(loadMoreView);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        swipeMenuRecyclerView.setAdapter(adapter);
    }

    public void setSwipeMenuCreator(SwipeMenuCreator menuCreator) {
        swipeMenuRecyclerView.setSwipeMenuCreator(menuCreator);
    }

    public void setSwipeMenuItemClickListener(SwipeMenuItemClickListener menuItemClickListener) {
        swipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
    }

    public void setLoadMoreListener(SwipeMenuRecyclerView.LoadMoreListener loadMoreListener) {
        swipeMenuRecyclerView.setLoadMoreListener(loadMoreListener);
    }

    public final void loadMoreFinish(boolean hasMore) {
        swipeMenuRecyclerView.loadMoreFinish(false, hasMore);
    }

    public final void loadMoreFinish(boolean dataEmpty, boolean hasMore) {
        swipeMenuRecyclerView.loadMoreFinish(dataEmpty, hasMore);
    }

    public void scrollToPosition(int position) {
        swipeMenuRecyclerView.scrollToPosition(position);
    }

    private class DefineLoadMoreView extends LinearLayout implements SwipeMenuRecyclerView.LoadMoreView, View.OnClickListener {
        private ProgressBar progressBar;
        private TextView tvMessage;

        private SwipeMenuRecyclerView.LoadMoreListener mLoadMoreListener;

        public DefineLoadMoreView(Context context) {
            super(context);
            setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
            setGravity(Gravity.CENTER);
            setVisibility(GONE);

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

            int minHeight = (int) (displayMetrics.density * 60 + 0.5);
            setMinimumHeight(minHeight);

            inflate(context, R.layout.view_swipe_recycler_footer, this);
            progressBar = (ProgressBar) findViewById(R.id.loading_view);
            tvMessage = (TextView) findViewById(R.id.tv_message);
            setOnClickListener(this);
        }

        /**
         * 马上开始回调加载更多了，这里应该显示进度条。
         */
        @Override
        public void onLoading() {
            setVisibility(VISIBLE);
            progressBar.setVisibility(VISIBLE);
            tvMessage.setVisibility(VISIBLE);
            tvMessage.setText(getResources().getString(R.string.string_recycler_loading));
        }

        /**
         * 加载更多完成了。
         *
         * @param dataEmpty 是否请求到空数据。
         * @param hasMore   是否还有更多数据等待请求。
         */
        @Override
        public void onLoadFinish(boolean dataEmpty, boolean hasMore) {
            if (!hasMore) {
                setVisibility(VISIBLE);
                if (dataEmpty) {
                    progressBar.setVisibility(GONE);
                    tvMessage.setVisibility(VISIBLE);
                    tvMessage.setText("暂无数据");
                } else {
                    progressBar.setVisibility(GONE);
                    tvMessage.setVisibility(VISIBLE);
                    tvMessage.setText(getResources().getString(R.string.string_recycler_no_more));
                }
            } else {
                setVisibility(INVISIBLE);
            }
        }

        /**
         * 调用了setAutoLoadMore(false)后，在需要加载更多的时候，这个方法会被调用，并传入加载更多的listener。
         */
        @Override
        public void onWaitToLoadMore(SwipeMenuRecyclerView.LoadMoreListener loadMoreListener) {
            this.mLoadMoreListener = loadMoreListener;

            setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
            tvMessage.setVisibility(VISIBLE);
            tvMessage.setText(getResources().getString(R.string.string_recycler_pull_to_load));
        }

        /**
         * 加载出错啦，下面的错误码和错误信息二选一。
         *
         * @param errorCode    错误码。
         * @param errorMessage 错误信息。
         */
        @Override
        public void onLoadError(int errorCode, String errorMessage) {
            setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
            tvMessage.setVisibility(VISIBLE);

            // 这里要不直接设置错误信息，要不根据errorCode动态设置错误数据。
            tvMessage.setText(errorMessage);
        }

        /**
         * 非自动加载更多时mLoadMoreListener才不为空。
         */
        @Override
        public void onClick(View v) {
            if (mLoadMoreListener != null) {
                mLoadMoreListener.onLoadMore();
            }
        }
    }
}
