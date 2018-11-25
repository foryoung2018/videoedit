package com.wmlive.hhvideo.heihei.mainhome.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoDetailListActivity;
import com.wmlive.hhvideo.heihei.mainhome.adapter.ExplosionAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ExplosionPresenter;
import com.wmlive.hhvideo.heihei.mainhome.view.ExplosionView;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.ExplosionViewHolder;
import com.wmlive.hhvideo.heihei.personal.fragment.UserHomeFragment;
import com.wmlive.hhvideo.heihei.personal.util.SpaceItemDecoration;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.OnFooterClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * A simple {@link Fragment} subclass.
 * 新爆
 * Modify by lsq
 */
public class ExplosionFragment extends DcBaseFragment implements
        ExplosionView, SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener {


    @BindView(R.id.recycle_explosion)
    RefreshRecyclerView recycleExplosion;
    private ExplosionPresenter explosionPresenter;
    private ExplosionAdapter adapter;
    private int space;
    private GridLayoutManager gridLayoutManager;


    public static ExplosionFragment newInstance() {
        ExplosionFragment fragment = new ExplosionFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LAZY_MODE, true);
        bundle.putBoolean(SINGLE_MODE, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_explosion;
    }

    @Override
    protected void initData() {
        super.initData();
        space = DeviceUtils.dip2px(getActivity(), 1);
        explosionPresenter = new ExplosionPresenter(this);

        gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recycleExplosion.setLayoutManager(gridLayoutManager);
        recycleExplosion.addItemDecoration(new SpaceItemDecoration(getActivity(), UserHomeFragment.SPACE_ITEM_DECRRATION, false));
        recycleExplosion.setOnRefreshListener(this);
        recycleExplosion.setOnLoadMoreListener(this);

        adapter = new ExplosionAdapter(new ArrayList<ShortVideoItem>(), recycleExplosion);
        recycleExplosion.setAdapter(adapter);
        adapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener<ShortVideoItem>() {
            @Override
            public void onRecyclerItemClick(int dataPosition, View view, ShortVideoItem data) {
                MultiTypeVideoBean bean = new MultiTypeVideoBean();
                bean.videoId = data.getId();
                TransferDataManager.get().setVideoListData(null);
                DcIjkPlayerManager.get().resetUrl();
                VideoDetailListActivity.startVideoDetailListActivity(getActivity(), 0, RecommendFragment.TYPE_SINGLE_WORK, bean, null, null);
//                VideoListActivity.startVideoListActivity(getActivity(), RecommendFragment.TYPE_EXPLOSION,
//                        MultiTypeVideoBean.createExplosionParma(dataPosition, adapter.getDataContainer()));
            }
        });
        recycleExplosion.getRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == 0) {
//                    Fresco.getImagePipeline().resume();
//                } else {
//                    Fresco.getImagePipeline().pause();
//                }
                showAnimImage(newState == 0);
            }
        });
        recycleExplosion.setOnFooterClickListener(new OnFooterClickListener() {
            @Override
            public void onPageErrorClick() {
                super.onPageErrorClick();
                onRefresh();
            }

            @Override
            public void onFootErrorClick() {
                super.onFootErrorClick();
                onLoadMore();
            }

        });
        recycleExplosion.autoRefresh(300);
    }

    @Override
    protected void onSingleClick(View v) {

    }


    @Override
    public void handleExplosionSucceed(boolean isRefresh, List<ShortVideoItem> shortVideoInfoList, boolean hasMore) {
        adapter.addData(isRefresh, shortVideoInfoList, hasMore);
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        if (requestCode == HttpConstant.TYPE_EXPLOSION_VIDEO
                || requestCode == (HttpConstant.TYPE_EXPLOSION_VIDEO + 1)) {
            adapter.showError(requestCode == HttpConstant.TYPE_EXPLOSION_VIDEO);
        } else {
            super.onRequestDataError(requestCode, message);
        }
    }

    @Override
    public void onRefresh() {
        isFirstLoadData = false;
        explosionPresenter.explosionVideo(true);
    }

    //手动刷新
    public void manualRefresh() {
        if (!isFirstLoadData && adapter != null && explosionPresenter != null) {
            if (recycleExplosion != null && !recycleExplosion.isRefreshing()) {
                recycleExplosion.autoRefresh();
            }
        }
    }

    @Override
    public void onLoadMore() {
        explosionPresenter.explosionVideo(false);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        showAnimImage(!hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
        showAnimImage(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        showAnimImage(true);
    }

    @Override
    public void onVisibleChange(int type, boolean visible) {
        super.onVisibleChange(type, visible);
        if (visible) {
            DcIjkPlayerManager.get().resetUrl();
            DcIjkPlayerManager.get().pausePlay();
        }
        showAnimImage(visible);
    }

    private void showAnimImage(boolean show) {
        int first = gridLayoutManager.findFirstVisibleItemPosition();
        int last = gridLayoutManager.findLastVisibleItemPosition();
        if (adapter != null && first > -1 && last < adapter.getDataContainer().size() && first <= last) {
            for (int i = first; i <= last; i++) {
                RecyclerView.ViewHolder viewHolder = recycleExplosion.getRecycleView().findViewHolderForAdapterPosition(i);
                if (viewHolder instanceof ExplosionViewHolder) {
                    ((ExplosionViewHolder) viewHolder).showAnimImage(show);
                }
            }
        }
    }
}
