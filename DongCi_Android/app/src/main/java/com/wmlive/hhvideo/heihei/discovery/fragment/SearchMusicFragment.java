package com.wmlive.hhvideo.heihei.discovery.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.MusicCategoryBean;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchTopicBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.beans.subject.TopicInfo;
import com.wmlive.hhvideo.heihei.discovery.AudioPlayerManager;
import com.wmlive.hhvideo.heihei.discovery.adapter.SearchMusicAdapter;
import com.wmlive.hhvideo.heihei.discovery.presenter.SearchMusicPresenter;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity2;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.download.Download;
import com.wmlive.hhvideo.widget.SearchView;
import com.wmlive.hhvideo.widget.StatusView;
import com.wmlive.hhvideo.widget.dialog.VerifyDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.OnFooterClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lsq on 6/13/2017.
 * 搜索音乐的页面
 */

public class SearchMusicFragment extends DcBaseFragment<SearchMusicPresenter> implements
        RefreshRecyclerView.OnLoadMoreListener, OnRecyclerItemClickListener<SearchMusicBean>,
        SearchMusicAdapter.OnItemClickListener, SearchMusicPresenter.ISearchMusicView, View.OnLayoutChangeListener {
    @BindView(R.id.tabMusic)
    TabLayout tabMusic;
    @BindView(R.id.viewBlank)
    View viewBlank;
    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;
    private SearchMusicAdapter searchMusicAdapter;
    private long categoryId;//当前分类的id
    private SearchView searchView;
    private boolean searchWithKey = false;
    private String keyword = "";
    private String startType;
    private TopicInfo topicInfo;
    private int keyboardHeight;
    private boolean firstSearch = false;
    private boolean isCollection = false;//是否是收藏的tab
    private Handler handler;
    private VerifyDialog verifyDialog;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search_music;
    }

    @Override
    protected SearchMusicPresenter getPresenter() {
        return new SearchMusicPresenter(this);
    }

    @Override
    public void onVisibleChange(int type, boolean visible) {
        super.onVisibleChange(type, visible);
        if (visible) {
            AudioPlayerManager.get().reset();
        } else {

        }
    }

    public static SearchMusicFragment newInstance(String startType, TopicInfo topicInfo) {
        SearchMusicFragment fragment = new SearchMusicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("startType", startType);
        bundle.putParcelable("topicInfo", topicInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        keyboardHeight = DeviceUtils.getScreenWH(getActivity())[1] / 3;
        setTitle("", true);
        getRootView().addOnLayoutChangeListener(this);
        if (getArguments() != null) {
            startType = getArguments().getString("startType");
            topicInfo = getArguments().getParcelable("topicInfo");
        }
        handler = new Handler(Looper.getMainLooper());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        searchView = (SearchView) toolbar.addCenterView(R.layout.view_discovery_search,
                null, Toolbar.LayoutParams.MATCH_PARENT).findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.showCenterHint(false);
                    tabMusic.setVisibility(View.GONE);
                }
            });
            searchView.setEditHint("搜索歌曲名称");
            searchView.showCenterHint(true);
            searchView.setSearchClickListener(new SearchView.OnSearchClickListener() {

                @Override
                public void onEditViewClick(String text) {

                }

                @Override
                public void onDeleteClick(String s) {

                }

                @Override
                public void onTextChanged(String text) {
                    KLog.i("输入的字符是：" + text);
//                    recyclerView.setVisibility(!TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onKeyDoneClick(String text) {
                    searchMusicAdapter.setEmptyStr(R.string.search_content_null);
                    searchMusicAdapter.addData(true, null, true);
                    searchWithKey = true;
                    keyword = text;
                    presenter.search(true, keyword, "music");
                    firstSearch = true;

                }

                @Override
                public void onEditTextFocusChange(boolean hasFocus) {
//                    tabMusic.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
                    recyclerView.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
                    if (hasFocus) {
                        AudioPlayerManager.get().reset();
                    }
                }
            });
        }
        searchMusicAdapter = new SearchMusicAdapter(new ArrayList<SearchMusicBean>(), recyclerView);
        searchMusicAdapter.setOnRecyclerItemClickListener(this);
        searchMusicAdapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(searchMusicAdapter);
        recyclerView.setRefreshEnable(false);
        recyclerView.setOnLoadMoreListener(this);
        searchMusicAdapter.setShowImg(true);
        recyclerView.setOnFooterClickListener(new OnFooterClickListener() {
            @Override
            public void onPageErrorClick() {
                super.onPageErrorClick();
                if (searchWithKey) {
                    presenter.search(true, keyword, "music");
                } else {
                    presenter.searchMusic(true, categoryId);
                }
            }

            @Override
            public void onFootErrorClick() {
                super.onFootErrorClick();
                if (searchWithKey) {
                    presenter.search(true, keyword, "music");
                } else {
                    presenter.searchMusic(false, categoryId);
                }
            }
        });
        presenter.getMusicCategory();
    }


    //顶部音乐类型的tab
    private void setMusicTab() {
        tabMusic.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AudioPlayerManager.get().reset();
                if (tab != null) {
                    searchWithKey = false;
                    searchMusicAdapter.addData(null, true, false);
                    categoryId = (long) tab.getTag();
                    if (categoryId != -1) { //搜索分类
                        presenter.searchMusic(true, categoryId);
                        isCollection = false;
                        searchMusicAdapter.setEmptyStr(R.string.search_content_null);
                    } else {//搜索我的收藏
                        searchMusicAdapter.setEmptyStr(R.string.search_collection_null);
                        isCollection = true;
                        presenter.getCollect(true);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onLoadMore() {
        presenter.searchMusic(false, categoryId);
    }


    @Override
    public void onRecyclerItemClick(final int dataPosition, View view, final SearchMusicBean data) {
        if (data != null && !TextUtils.isEmpty(data.getMusic_path())) {
            searchMusicAdapter.showItemAdd(dataPosition);
            getWeakHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AudioPlayerManager.get().start(data.getMusic_path());
                }
            }, 200);
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onPause() {
        AudioPlayerManager.get().reset();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        downloadReceiver = null;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        Download.pause(getActivity());
        AudioPlayerManager.get().release();
        super.onDestroy();
    }

    @Override
    public void onUseMusicClick(int position, SearchMusicBean data) {
        if (data != null && !TextUtils.isEmpty(data.getMusic_path())) {
            downloadMusic(data);
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    private DownloadReceiver downloadReceiver;

    //下载音乐
    private void downloadMusic(final SearchMusicBean data) {
        if (data == null) {
            return;
        }
        if (downloadReceiver == null) {
            downloadReceiver = new DownloadReceiver(this, handler, data);
        }
        Download.start(getActivity(), data.getMusic_path(), AppCacheFileUtils.getAppMusicCachePath(), "", "", downloadReceiver);
    }

    private static class DownloadReceiver extends ResultReceiver {
        private SearchMusicBean musicBean;
        private WeakReference<SearchMusicFragment> musicFragment;

        public DownloadReceiver(SearchMusicFragment fragment, Handler handler, SearchMusicBean searchMusicBean) {
            super(handler);
            musicFragment = new WeakReference<>(fragment);
            musicBean = searchMusicBean;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String message = resultData.getString("message");
            if (musicFragment != null && musicFragment.get() != null) {
                switch (resultCode) {
                    case Download.RESULT_PREPARE:
                        musicFragment.get().loading(true, new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Download.pause(musicFragment.get().getActivity());
                            }
                        });
                        break;
                    case Download.RESULT_PAUSE:
                        musicFragment.get().dismissLoad();
                        break;
                    case Download.RESULT_DOWNLOADING:
                        break;
                    case Download.RESULT_ERROR:
                        musicFragment.get().dismissLoad();
                        musicFragment.get().showToast(message);
                        break;
                    case Download.RESULT_COMPLETE:
                        AudioPlayerManager.get().release();
                        musicFragment.get().dismissLoad();
                        Intent intent = new Intent();
                        if (!TextUtils.isEmpty(musicFragment.get().startType) && musicFragment.get().startType.equals("fromDyUIAPI")) {
                            musicFragment.get().getActivity().setResult(RESULT_OK, intent);
                            musicFragment.get().getActivity().finish();
                        } else {
                            musicFragment.get().searchMusicAdapter.onReset();
                            MusicInfoEntity musicInfoEntity = new MusicInfoEntity();
                            musicInfoEntity.musicId = musicBean.getId();
                            musicInfoEntity.setMusicPath(resultData.getString("savePath"));
                            musicInfoEntity.musicIconUrl = musicBean.getAlbum_cover();
                            musicInfoEntity.title = musicBean.getName();
                            musicInfoEntity.author = musicBean.getSinger_name();
                            musicInfoEntity.setTrimRange(0, musicInfoEntity.getDuring());
                            SelectFrameActivity2.startSelectFrameActivity2((BaseCompatActivity) musicFragment.get().getActivity(), musicInfoEntity, SelectFrameActivity.VIDEO_TYPE_RECORD);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onCollectClick(int position, long musicId) {
        if (musicId > 0) {
            presenter.addFavorite(position, musicId);
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onGetCategoryOk(List<MusicCategoryBean.MusicCatBean> list) {
        if (CollectionUtil.isEmpty(list)) {
            tabMusic.setVisibility(View.GONE);
            showStatusPage(StatusView.STATUS_EMPTY, "没有音乐分类信息");
        } else {
            tabMusic.setVisibility(View.VISIBLE);
            setMusicTab();
            tabMusic.addTab(tabMusic.newTab().setText("我的收藏").setTag(-1L), false);
            for (int i = 0, n = list.size(); i < n; i++) {
                if (list.get(i) != null) {
                    tabMusic.addTab(tabMusic.newTab().setText(list.get(i).getCat_name()).setTag(list.get(i).getId()), i == 0);
                }
            }
            if (tabMusic.getTabCount() > 1) {
                if (tabMusic != null && tabMusic.getTabAt(1) != null) {
                    tabMusic.getTabAt(1).select();
                }
            }
        }
    }

    @Override
    public void onSearchMusicOk(final boolean isRefresh, List<SearchMusicBean> list, final boolean hasMore) {
        recyclerView.setVisibility(View.VISIBLE);
        searchMusicAdapter.addData(list, isRefresh, hasMore);
    }

    @Override
    public void onSearchCollectOk(boolean isRefresh, List<SearchMusicBean> list, boolean hasMore) {
        AudioPlayerManager.get().reset();
        recyclerView.setVisibility(View.VISIBLE);
        searchMusicAdapter.addData(list, isRefresh, hasMore);
    }

    @Override
    public void onAddFavoriteResult(int position, boolean ok) {
        showToast(ok ? "已收藏" : "取消收藏");
        if (isCollection) {
            presenter.getCollect(true);
        } else {
            searchMusicAdapter.refreshItemFavorite(position, ok);
        }
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        if (requestCode == HttpConstant.TYPE_DISCOVERY_GET_CATEGORY) {
            showToast("获取音乐分类失败");
            showStatusPage(StatusView.STATUS_ERROR, "获取分类失败，点击重新加载");
        } else if (requestCode == HttpConstant.TYPE_DISCOVERY_SEARCH_BY_CAT
                || (requestCode == HttpConstant.TYPE_DISCOVERY_SEARCH_BY_CAT + 1)
                || requestCode == HttpConstant.TYPE_DISCOVERY_COLLECT_LIST
                || (requestCode == HttpConstant.TYPE_DISCOVERY_COLLECT_LIST + 1)) {
            recyclerView.setVisibility(View.VISIBLE);

            if (requestCode == HttpConstant.TYPE_DISCOVERY_SEARCH_BY_CAT
                    || requestCode == HttpConstant.TYPE_DISCOVERY_COLLECT_LIST) {
                searchMusicAdapter.showError(true);
                AudioPlayerManager.get().reset();
            }
        } else {
            super.onRequestDataError(requestCode, message);
        }
    }

    @Override
    public void onQueryUserOk(boolean isRefresh, String type, List<SearchUserBean> bean, boolean hasMore) {

    }

    @Override
    public void onQueryTopicOk(boolean isRefresh, String type, List<SearchTopicBean> bean, boolean hasMore) {

    }

    @Override
    public void onQueryMusicOk(boolean isRefresh, String type, List<SearchMusicBean> bean, boolean hasMore) {
//        if (null != searchView) {
//            searchView.getEditText().setText("");
//        }
        recyclerView.setVisibility(View.VISIBLE);
        searchMusicAdapter.addData(bean, isRefresh, hasMore);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == DyUIAPIManager.REQUEST_VIDEO_CODE) {
//            DyUIAPIManager.getInstance().onActivityResultByVideoInfos(getActivity(), requestCode, resultCode, data);
//        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //设置adjustSize才生效
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyboardHeight)) {
            KLog.i("键盘弹起");
//            viewBlank.setVisibility(View.VISIBLE);
            tabMusic.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyboardHeight)) {
            KLog.i("键盘关闭");
//            tabMusic.setVisibility(firstSearch ? View.GONE : View.VISIBLE);
//            viewBlank.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSingleClick(View v) {

    }

//    @Override
//    protected void onBack() {
//        searchView.showCenterHint(true);
//        tabMusic.setVisibility(View.VISIBLE);
//    }
}
