package com.wmlive.hhvideo.heihei.discovery.activity;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchTopicBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.discovery.adapter.SearchAdapter;
import com.wmlive.hhvideo.heihei.discovery.presenter.SearchPresenter;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoListActivity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.SearchView;
import com.wmlive.hhvideo.widget.refreshrecycler.OnFooterClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/31/2017.
 * <p>
 * 发现的搜索页面
 */
public class SearchActivity extends DcBaseActivity<SearchPresenter> implements
        RefreshRecyclerView.OnLoadMoreListener,
        SearchPresenter.ISearchView,
        OnRecyclerItemClickListener {
    @BindView(R.id.tabMusic)
    TabLayout tabLayout;
    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private String keyword = "";//搜索关键字
    private String searchType = TYPE_USER;//搜索类型

    public static final String TYPE_USER = "user";
    public static final String TYPE_TOPIC = "topic";
    public static final String TYPE_MUSIC = "music";
    private SearchView searchView;
    private TextView tvCancel;

    @Override
    protected SearchPresenter getPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initData() {
        super.initData();
        setSearchView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setRefreshEnable(false);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.setOnFooterClickListener(new OnFooterClickListener() {
            @Override
            public void onPageErrorClick() {
                super.onPageErrorClick();
                presenter.search(true, keyword, searchType);
            }

            @Override
            public void onFootErrorClick() {
                super.onFootErrorClick();
                presenter.search(false, keyword, searchType);
            }
        });
        searchAdapter = new SearchAdapter(new ArrayList(), recyclerView);
        searchAdapter.setOnRecyclerItemClickListener(this);
        searchAdapter.setItemTypes(new ArrayList<Integer>() {{
            add(SearchAdapter.TYPE_USER);
            add(SearchAdapter.TYPE_TOPIC);
//            add(SearchAdapter.TYPE_MUSIC);
        }});
        recyclerView.setAdapter(searchAdapter);

        recyclerView.getRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                KLog.d("onScrollStateChanged: newState==" + newState);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
                recyclerView.requestFocus();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        searchAdapter.setEmptyStr(R.string.discovery_null);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (null != tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            searchType = TYPE_USER;
                            searchView.setEditHint("搜索用户");
                            break;
                        case 1:
                            searchType = TYPE_TOPIC;
                            searchView.setEditHint("搜索话题");
                            break;
                        case 2:
                            searchType = TYPE_MUSIC;
                            searchView.setEditHint("搜索音乐");
                            break;
                        default:
                            break;
                    }
                    searchAdapter.setShowEmptyView(false);
                    searchAdapter.addData(keyword, true, null, searchType, true);
                    presenter.search(true, keyword, searchType);//搜索推荐的内容
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.view_tab_user), true);
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.view_tab_topic));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.view_tab_music));
    }

    //设置搜索框
    private void setSearchView() {
        searchView = findViewById(R.id.searchView);
        searchView.showDiscoverySearch();
        tvCancel = findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(this);
        if (searchView != null) {
            searchView.setSearchClickListener(new SearchView.OnSearchClickListener() {

                @Override
                public void onEditViewClick(String text) {
                }

                @Override
                public void onDeleteClick(String s) {
                    if (TextUtils.isEmpty(s)) {
                        keyword = "";
                        presenter.search(true, keyword, searchType);
                    }
                }

                @Override
                public void onTextChanged(String text) {
                    KLog.i("输入的字符是：" + text);
                    keyword = text;

                }

                @Override
                public void onKeyDoneClick(String text) {
                    keyword = text;
                    presenter.search(true, keyword, searchType);
                }

                @Override
                public void onEditTextFocusChange(boolean hasFocus) {

                }
            });
        }
    }

    @Override
    public void onLoadMore() {
        presenter.search(false, keyword, searchType);
    }

    @Override
    public void onQueryUserOk(boolean isRefresh, String type, List<SearchUserBean> bean, boolean hasMore) {
        searchAdapter.setShowEmptyView(true);
        searchAdapter.addData(keyword, isRefresh, bean, type, hasMore);
    }

    @Override
    public void onQueryTopicOk(boolean isRefresh, String type, List<SearchTopicBean> bean, boolean hasMore) {
        searchAdapter.setShowEmptyView(true);
        searchAdapter.addData(keyword, isRefresh, bean, type, hasMore);
    }

    @Override
    public void onQueryMusicOk(boolean isRefresh, String type, List<SearchMusicBean> bean, boolean hasMore) {
//        searchAdapter.addData(keyword, isRefresh, bean, type, hasMore);
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        if (requestCode == HttpConstant.TYPE_DISCOVERY_SEARCH
                || requestCode == HttpConstant.TYPE_DISCOVERY_SEARCH + 1) {
            searchAdapter.showError(requestCode == HttpConstant.TYPE_DISCOVERY_SEARCH);
        } else {
            super.onRequestDataError(requestCode, message);
        }
    }

    @Override
    public void onRecyclerItemClick(int dataPosition, View view, Object data) {
        if (data != null) {
            TransferDataManager.get().setVideoListData(null);
            if (data instanceof SearchUserBean) {
                UserHomeActivity.startUserHomeActivity(this, ((SearchUserBean) data).getId());
            } else if (data instanceof SearchTopicBean) {
                VideoListActivity.startVideoListActivity(this, RecommendFragment.TYPE_TOPIC, MultiTypeVideoBean.createTopicParma(((SearchTopicBean) data).getId(), 0, null));
            } else if (data instanceof SearchMusicBean) {
                VideoListActivity.startVideoListActivity(this, RecommendFragment.TYPE_MUSIC, MultiTypeVideoBean.createTopicParma(((SearchTopicBean) data).getId(), 0, null));
//                TopicActivity.startMusicActivity(this, ((SearchMusicBean) data).getId());
            }
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
