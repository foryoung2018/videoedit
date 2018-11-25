package com.wmlive.hhvideo.heihei.subject;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.search.SearchNotExistsInfoBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchResponse;
import com.wmlive.hhvideo.heihei.beans.search.SearchTopicBean;
import com.wmlive.hhvideo.heihei.beans.subject.TopicCreateBean;
import com.wmlive.hhvideo.heihei.beans.subject.TopicInfo;
import com.wmlive.hhvideo.heihei.subject.View.SubjectSearchView;
import com.wmlive.hhvideo.heihei.subject.adapter.SubjectSearchAdapter;
import com.wmlive.hhvideo.heihei.subject.presenter.SubjectSearchPresenter;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.SearchView1;
import com.wmlive.hhvideo.widget.refreshrecycler.DividerItemDecoration;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 话题的搜索页面
 */
public class SubjectSearchActivity extends DcBaseActivity<SubjectSearchPresenter>
        implements RefreshRecyclerView.OnLoadMoreListener,
        SubjectSearchView,
        OnRecyclerItemClickListener<SearchTopicBean> {
    public static final int REQUEST_CODE_CREATE_TOPIC = 10001;//添加话题
    public static final int EDITTEXT_TITLE_MAX_LENGTH = 14;

    @BindView(R.id.recyclerView_subject_search)
    RefreshRecyclerView recyclerView_subject_search;

    private SubjectSearchPresenter subjectSearchPresenter;
    private SubjectSearchAdapter subjectSearchAdapter;

    private View mItemSearchTopicHeaderView;//header view
    private TextView mTVItemDefaultHeadeView;
    private RelativeLayout mRLItemKeywordHeaderView;
    private TextView mTVItemNameKeywordHeaderView;
    private TextView mTVItemDesKeywordHeaderView;
    private TextView mTVItemAddKeywordHeaderView;


    private String strKeywords;//关键字
    private int offset = 0;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_subject_search;
    }

    @Override
    protected SubjectSearchPresenter getPresenter() {
        if (subjectSearchPresenter == null) {
            subjectSearchPresenter = new SubjectSearchPresenter(this);
        }
        return subjectSearchPresenter;
    }

    @Override
    protected void initData() {
        super.initData();
        //设置搜索框
        setTitle("", true);
        final SearchView1 searchView = (SearchView1) toolbar.addCenterView(R.layout.view_discovery_search1,
                null, Toolbar.LayoutParams.MATCH_PARENT).findViewById(R.id.searchView);
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int destLength = dest.length();
                if (destLength + source.length() > EDITTEXT_TITLE_MAX_LENGTH) {
                    ToastUtil.showToast(R.string.subject_add_error);
                    return source.subSequence(0, EDITTEXT_TITLE_MAX_LENGTH - destLength);
                }
                return source;
            }
        };
        searchView.etInput.setFilters(new InputFilter[]{inputFilter});
        searchView.setEditHint("输入话题");
        if (searchView != null) {
            searchView.setSearchClickListener(new SearchView1.OnSearchClickListener() {

                @Override
                public void onEditViewClick(String text) {
                    DeviceUtils.showKeyBoard(searchView.getEditText());
                }

                @Override
                public void onDeleteClick(String s) {

                }

                @Override
                public void onTextChanged(String text) {
                    KLog.i("输入的字符是：" + text);

                }

                @Override
                public void onKeyDoneClick(String text) {
                    //单击键盘完成
                    strKeywords = text;
                    getSearchKeywordsTopic(strKeywords);
                }

                @Override
                public void onEditTextFocusChange(boolean hasFocus) {

                }
            });
        }
        //初始化header
        initSearchTopicHeader();
        //recyleView
        recyclerView_subject_search.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_subject_search.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView_subject_search.setRefreshEnable(false);
        recyclerView_subject_search.setOnLoadMoreListener(this);
        subjectSearchAdapter = new SubjectSearchAdapter(new ArrayList<SearchTopicBean>(), recyclerView_subject_search);
        subjectSearchAdapter.setOnRecyclerItemClickListener(this);
        recyclerView_subject_search.setAdapter(subjectSearchAdapter);
        recyclerView_subject_search.setHeader(mItemSearchTopicHeaderView);

        //获取默认的话题信息
        getSearchDefaultTopic();
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    public void onRequestDataError(int requestCode, String message) {

    }

    @Override
    public void onLoadMore() {
        getSearchMoreTopic();
    }

    @Override
    public void searchTopicSuccess(SearchResponse searchResponse, String keywrods) {
        //非更多信息
        if (searchResponse != null) {
            if (TextUtils.isEmpty(keywrods)) {
                //默认搜索的结果
                setDefaultSearchTopicHeader(true);
                recyclerView_subject_search.setHeaderVisible(true);
            } else {
                //关键字搜索的结果
                if (searchResponse.getNot_exists_info() != null) {
                    setKeywordSearchTopicHeader(searchResponse.getNot_exists_info());
                    recyclerView_subject_search.setHeaderVisible(true);
                } else {
                    recyclerView_subject_search.setHeaderVisible(false);
                }
            }
            offset = searchResponse.getOffset();
            subjectSearchAdapter.addData(true, searchResponse.getTopic_list(), searchResponse.isHas_more());
        }
    }

    @Override
    public void searchTopicMoreSuccess(SearchResponse searchResponse, String keywrods) {
        //更多,header不删除
        if (searchResponse != null) {
            List<SearchTopicBean> datas = searchResponse.getTopic_list();
            subjectSearchAdapter.addData(false, datas, searchResponse.isHas_more());
        }
    }

    @Override
    public void onRecyclerItemClick(int dataPosition, View view, SearchTopicBean data) {
        //item 单击的回调
        TopicInfo topicInfo = new TopicInfo();
        topicInfo.setTitle(data.getName());
        topicInfo.setDesc(data.getDescription());
        topicInfo.setTopicId(data.getId());
        Intent intent = new Intent();
        intent.putExtra(TopicInfo.INTENT_EXTRA_KEY_NAME, topicInfo);
        SubjectSearchActivity.this.setResult(RESULT_OK, intent);
        SubjectSearchActivity.this.finish();
    }

    /**
     * 获取默认的话题搜索信息
     */
    private void getSearchDefaultTopic() {
        offset = 0;
        strKeywords = "";
        subjectSearchPresenter.getSubjectByKeyword(strKeywords, offset, false);
    }

    /**
     * 首次关键字搜索
     *
     * @param keywords
     */
    private void getSearchKeywordsTopic(String keywords) {
        strKeywords = keywords;
        subjectSearchPresenter.getSubjectByKeyword(keywords, 0, false);
    }

    /**
     * 获取更多搜索话题
     */
    private void getSearchMoreTopic() {
        subjectSearchPresenter.getSubjectByKeyword(strKeywords, offset, true);
    }

    /**
     * 初始化header
     *
     * @return
     */
    private void initSearchTopicHeader() {
        mItemSearchTopicHeaderView = LayoutInflater.from(this).inflate(R.layout.item_search_topic_header_layout, null);
        //默认heeader
        mTVItemDefaultHeadeView = (TextView) mItemSearchTopicHeaderView.findViewById(R.id.tv_subject_search_title);
        //关键字无数据时header
        mRLItemKeywordHeaderView = (RelativeLayout) mItemSearchTopicHeaderView.findViewById(R.id.rl_subject_search_title);
        mTVItemNameKeywordHeaderView = (TextView) mItemSearchTopicHeaderView.findViewById(R.id.tv_item_subject_name);
        mTVItemAddKeywordHeaderView = (TextView) mItemSearchTopicHeaderView.findViewById(R.id.tv_item_subject_add);
        mTVItemDesKeywordHeaderView = (TextView) mItemSearchTopicHeaderView.findViewById(R.id.tv_item_subject_des);
        mRLItemKeywordHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发话题
                Intent mIntent = new Intent();
                mIntent.setClass(SubjectSearchActivity.this, SubjectAddActivity.class);
                mIntent.putExtra(SubjectAddActivity.SUBJECT_TITLE_FLAG, strKeywords);
                startActivityForResult(mIntent, REQUEST_CODE_CREATE_TOPIC);
            }
        });
    }

    /**
     * 设置添加话题header
     *
     * @param bean
     */
    private void setKeywordSearchTopicHeader(SearchNotExistsInfoBean bean) {
        if (mTVItemDefaultHeadeView != null) {
            mTVItemDefaultHeadeView.setVisibility(View.GONE);
        }
        if (mTVItemNameKeywordHeaderView != null && mTVItemDesKeywordHeaderView != null && bean != null) {
            mTVItemNameKeywordHeaderView.setText(bean.getName());
            mTVItemDesKeywordHeaderView.setText(bean.getDesc());
        }
        if (mRLItemKeywordHeaderView != null) {
            mRLItemKeywordHeaderView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 默认header 显示
     *
     * @param visible
     */
    private void setDefaultSearchTopicHeader(boolean visible) {
        if (visible) {
            mTVItemDefaultHeadeView.setVisibility(View.VISIBLE);
            mRLItemKeywordHeaderView.setVisibility(View.GONE);
        } else {
            mTVItemDefaultHeadeView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            if (resultCode == RESULT_OK) {
                if (REQUEST_CODE_CREATE_TOPIC == requestCode) {
                    TopicCreateBean topicCreateBean = (TopicCreateBean) data.getSerializableExtra(SubjectAddActivity.KEY_TOPIC);
                    if (topicCreateBean != null) {
                        TopicInfo topicInfo = new TopicInfo();
                        topicInfo.setTitle(topicCreateBean.getName());
                        topicInfo.setDesc(topicCreateBean.getDescription());
                        topicInfo.setTopicId(topicCreateBean.getId());
                        Intent intent = new Intent();
                        intent.putExtra(TopicInfo.INTENT_EXTRA_KEY_NAME, topicInfo);
                        SubjectSearchActivity.this.setResult(RESULT_OK, intent);
                        SubjectSearchActivity.this.finish();
                    }
//                if(TextUtils.isEmpty(strKeywords)){
//                    getSearchDefaultTopic();
//                }else{
//                    getSearchKeywordsTopic(strKeywords);
//                }
                }
            }
        }
    }

}
