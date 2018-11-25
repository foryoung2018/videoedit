package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.personal.ListFollowerFansResponse;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.adapter.ContactAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.SearchFollowPresenter;
import com.wmlive.hhvideo.heihei.message.activity.IMMessageActivity;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.personal.view.IFocusView;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.SearchView1;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class ContactActivity extends DcBaseActivity
        implements
        IFocusView, OnRecyclerItemClickListener<SearchUserBean>,
        SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener, SearchFollowPresenter.ISearchFollowView, ContactAdapter.OnContactClickListener {

    @BindView(R.id.rvContact)
    RefreshRecyclerView rvContact;
    @BindView(R.id.searchView)
    SearchView1 searchView;
    private SearchFollowPresenter searchPresenter;
    private ContactAdapter contactAdapter;
    private String keyword = "";

    @Override
    protected void initData() {
        super.initData();
        setTitle("选择联系人", true);
        searchPresenter = new SearchFollowPresenter(this);
        addPresenter(searchPresenter);
        rvContact.setLayoutManager(new LinearLayoutManager(this));
        rvContact.setOnLoadMoreListener(this);
        rvContact.setOnRefreshListener(this);
        contactAdapter = new ContactAdapter(new ArrayList<SearchUserBean>(), rvContact);
        contactAdapter.setOnRecyclerItemClickListener(this);
        contactAdapter.setContactClickListener(this);
        rvContact.setAdapter(contactAdapter);
        contactAdapter.setEmptyStr(R.string.search_null_result);
        searchView.setEditHint(" 搜索用户");
        searchView.setSearchClickListener(new SearchView1.OnSearchClickListener() {
            @Override
            public void onEditViewClick(String text) {
            }

            @Override
            public void onDeleteClick(String s) {
                if (TextUtils.isEmpty(s)) {
                    keyword = "";
                    searchPresenter.search(true, keyword);
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
                searchPresenter.search(true, keyword);
            }

            @Override
            public void onEditTextFocusChange(boolean hasFocus) {

            }
        });
        searchPresenter.search(true, keyword);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_contact;
    }

    @Override
    public void onRefresh() {
        searchPresenter.search(true, keyword);
    }

    @Override
    public void onLoadMore() {
        searchPresenter.search(false, keyword);
    }

    @Override
    public void onFocusListOk(boolean isRefresh, ListFollowerFansResponse response, boolean hasMore) {
        contactAdapter.addDatas(keyword, isRefresh, response.users, hasMore);
    }

    @Override
    public void onFocusFail(boolean isRefresh, String message) {
        showToast(message);
    }

    @Override
    public void onRecyclerItemClick(int dataPosition, View view, SearchUserBean data) {
        if (AccountUtil.isLogin()) {
            if (data == null) {
                showToast(R.string.hintErrorDataDelayTry);
                return;
            }
            if (data.getId() == AccountUtil.getUserId()) {
                showToast("哎哟，不能跟自己聊天!");
                return;
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setId(data.getId());
            userInfo.setName(data.getName());
            userInfo.setRelation(data.getRelation());
            userInfo.setCover_url(data.getCover_url());
            userInfo.setDc_num(data.getDc_num());
            userInfo.setVerify(data.getVerify());
            IMMessageActivity.startIMMessageActivity(this, data.getId(), userInfo);
            finish();
        } else {
            showReLogin();
        }
    }

    @Override
    public void onSearchOk(boolean isRefresh, List<SearchUserBean> bean, boolean hasMore) {
        contactAdapter.addDatas(keyword, isRefresh, bean, hasMore);
    }

    @Override
    public void onSearchFail(boolean isRefresh, String message) {
        showToast(message);
    }

    @Override
    public void onAvatarClick(long userId) {
        UserHomeActivity.startUserHomeActivity(this, userId);
    }
}
