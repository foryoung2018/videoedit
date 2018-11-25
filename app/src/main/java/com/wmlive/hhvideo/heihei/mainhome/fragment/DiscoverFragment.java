package com.wmlive.hhvideo.heihei.mainhome.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.flexbox.FlexboxLayout;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseFragment;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.discovery.FocusBean;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicTypeListBean;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.discovery.activity.RecommendUserActivity;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchActivity;
import com.wmlive.hhvideo.heihei.discovery.adapter.DiscoveryAdapter;
import com.wmlive.hhvideo.heihei.discovery.presenter.DiscoveryPresenter;
import com.wmlive.hhvideo.heihei.discovery.viewholder.DiscoveryViewHolder;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.login.activity.LoginActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.DiscoverMessageActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.TopListActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoListActivity;
import com.wmlive.hhvideo.heihei.mainhome.presenter.Block;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.personal.activity.WebViewActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity2;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.AutoLineFeedView;
import com.wmlive.hhvideo.widget.BadgeView;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.UrlImageSpan;
import com.wmlive.hhvideo.widget.banner.BannerView;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.dialog.VerifyDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.OnFooterClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static android.app.Activity.RESULT_OK;

/**
 *
 * faxian
 */

public class DiscoverFragment extends BaseFragment<DiscoveryPresenter> implements
        SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener,
        DiscoveryAdapter.OnDiscoverClickListener,
        DiscoveryPresenter.IDiscoveryView {

    private static final int toolbarHeight = DeviceUtils.dip2px(DCApplication.getDCApp(), 24);

    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.rlBell)
    RelativeLayout rlBell;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    LinearLayout llGoTopic;
    LinearLayout llGoTopic0;

//    private RatioLayout rlBannerView;

    private DiscoveryAdapter discoveryAdapter;
    private BannerView bannerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomDialog customDialog;

    private View llTopPanel1;
    private View llAroundPanel1;
    private LinearLayout llTopPanel;
    private LinearLayout llAroundPanel;
    private String aroundUrl;
    private List<Banner> bannerList;
    private VerifyDialog verifyDialog;
//    private BadgeView bvMsgCount;

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LAZY_MODE, true);
        bundle.putBoolean(SINGLE_MODE, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected DiscoveryPresenter getPresenter() {
        return new DiscoveryPresenter(this);
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                if (getActivity() != null) {
                    getActivity().finish();
                }
                break;
//            case R.id.llTopPanel:
            case R.id.llTopPanel1:
                startActivity(new Intent(getActivity(), TopListActivity.class));
                break;
//            case R.id.llAroundPanel:
            case R.id.llAroundPanel1:
                if (!TextUtils.isEmpty(aroundUrl)) {
                    WebViewActivity.startWebActivity(getActivity(), aroundUrl, "");
                } else {
                    showToast(R.string.hintErrorDataDelayTry);
                }
                break;
            case R.id.rlBell:
//                if (AccountUtil.isLogin()) {
//                startActivity(new Intent(getActivity(), DiscoverMessageActivity.class));
//                GlobalParams.StaticVariable.sDiscoverUnreadCount = 0;
//                bvMsgCount.setUnreadCount(0);
//                } else {
//                    showReLogin();
//                }
                break;
            case R.id.llSearch:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected int getBaseLayoutId() {
        return R.layout.activity_discovery;
    }

    @Override
    protected void initData() {
        super.initData();
        EventHelper.register(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        discoveryAdapter = new DiscoveryAdapter(new ArrayList<TopicTypeListBean.TopicListBean>(), recyclerView);
        discoveryAdapter.setOnPictureClickListener(this);
//        bvMsgCount = new BadgeView(getActivity());
//        bvMsgCount.setTargetView(rlBell);
//        bvMsgCount.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
//        bvMsgCount.setBadgeMargin(0, 4, 4, 0);
//        bvMsgCount.setBackground(10, Color.parseColor("#FF0000"));
//        bvMsgCount.setUnreadCount(GlobalParams.StaticVariable.sDiscoverUnreadCount);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.include_discovery_banner, recyclerView, false);
        bannerView = view.findViewById(R.id.bannerView);
        llTopPanel1 = view.findViewById(R.id.llTopPanel1);
        llAroundPanel1 = view.findViewById(R.id.llAroundPanel1);
//        llTopPanel = view.findViewById(R.id.llTopPanel);
//        llAroundPanel = view.findViewById(R.id.llAroundPanel);
        llGoTopic = view.findViewById(R.id.llGoTopic);
//        llGoTopic0 = view.findViewById(R.id.llGoTopic0);
//        allTopics.setMaxRow(2);
//        allTopics.setViewMargin(10);
        ivBack.setOnClickListener(this);
        rlBell.setOnClickListener(this);
//        llTopPanel.setOnClickListener(this);
        llTopPanel1.setOnClickListener(this);
//        llAroundPanel.setOnClickListener(this);
        llAroundPanel1.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        recyclerView.setAdapter(discoveryAdapter);
        recyclerView.setOnRefreshListener(this);
        recyclerView.setProgressViewOffset(true, toolbarHeight, toolbarHeight + 100);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.getRecycleView().addOnScrollListener(onScrollListener);
        recyclerView.setOnFooterClickListener(new OnFooterClickListener() {
            @Override
            public void onPageErrorClick() {
                super.onPageErrorClick();
                presenter.getTopicList(true);
            }

            @Override
            public void onFootErrorClick() {
                super.onFootErrorClick();
                presenter.getTopicList(false);
            }
        });
        recyclerView.setHeader(view);
        recyclerView.autoRefresh(500);
    }

    //设置轮播图
    private void initBannerView() {
        bannerView.setViewFactory(new BannerViewFactory());
        bannerView.setOnItemClickListener(new BannerView.OnItemClickListener<Banner>() {
            @Override
            public void onItemClick(int position, Banner o) {
                if (null != o) {
                    DcRouter.linkTo(getActivity(), o.link);
                }
            }
        });

    }

    @Override
    public void onVisibleChange(int type, boolean visible) {
        super.onVisibleChange(type, visible);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != bannerView) {
            bannerView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != bannerView) {
            bannerView.stop();
        }
    }

    @Override
    public void onPictureClick(long topicId, TopicTypeListBean.TopicListBean topicListBean,
                               List<ShortVideoItem> listBean, long videoId, int position) {
        DcIjkPlayerManager.get().resetUrl();
        TransferDataManager.get().setVideoListData(listBean);
        VideoListActivity.startVideoListActivity(getContext(), RecommendFragment.TYPE_TOPIC,
                MultiTypeVideoBean.createTopicParma(topicId, position, topicListBean, null));
    }

    @Override
    public void onTopicClick(int position, boolean isTopic, long id) {
        if (id > 0) {
            if (isTopic) {
                VideoListActivity.startVideoListActivity(getActivity(), RecommendFragment.TYPE_TOPIC,
                        MultiTypeVideoBean.createTopicParma(id, 0, null));
            } else {
            }
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onAvatarClick(long userId) {
        UserHomeActivity.startUserHomeActivity(getActivity(), userId);
    }

    @Override
    public void onFollowClick(final long userId, final int position, final boolean isFollowed) {
        if (AccountUtil.isLogin()) {
            if (userId > 0) {
                if (isFollowed) {
                    customDialog = new CustomDialog(getActivity(), R.style.BaseDialogTheme);
                    customDialog.setContent(getString(R.string.dialog_focus_tip));
                    customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            customDialog.dismiss();
                            presenter.follow(position, userId, isFollowed);
                        }
                    });
                    customDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            customDialog.dismiss();
                        }
                    });
                    customDialog.show();
                } else {
                    presenter.follow(position, userId, isFollowed);
                }
            } else {
                showToast(R.string.hintErrorDataDelayTry);
            }
        } else {
            showReLogin();
        }
    }

    @Override
    public void onMoreFollowClick() {
        startActivity(new Intent(getActivity(), RecommendUserActivity.class));
    }

    @Override
    public void onJoinClick(ShortVideoItem item) {
        if (item != null) {
            if (AccountUtil.isLogin()) {
                if (item.is_teamwork == 1) {
                    if (AccountUtil.needVerifyCode()) {
                        showVerifyDialog(item, new Block() {
                            @Override
                            public void run() {
                                startSelectFrameActivity(item);
                            }
                        });
                    } else {
                        startSelectFrameActivity(item);
                    }
                } else {
                    ToastUtil.showToast(item.teamwork_tips);
                }
            } else {
                showReLogin();
            }
        }
    }

    private void startSelectFrameActivity(ShortVideoItem shortVideoItem){
        if (shortVideoItem != null) {
            if (shortVideoItem.is_teamwork == 1) {
                if(5==shortVideoItem.origin){
                    String template = shortVideoItem.creative_template_name;
                    if (RecordMvActivityHelper.isNoInvalidTemplate(getActivity(), template)) {
                        ToastUtil.showToast(getResources().getString(R.string.temp_no_can_use));
                    }else{
                        RecordMvActivityHelper.startRecordActivity((BaseCompatActivity) getActivity(),RecordMvActivityHelper.EXTRA_RECORD_TYPE_USE_CURENT_TEMPLATE,shortVideoItem.id);
                    }

                }else{
                    SelectFrameActivity2.startSelectFrameActivity2((BaseCompatActivity) getActivity(),
                            SelectFrameActivity.VIDEO_TYPE_TEAMWORK, shortVideoItem.getId(),
                            shortVideoItem.frame_layout);
                }
            } else {
                showToast(shortVideoItem.teamwork_tips);
            }
        }
    }

    @Override
    public void onFocusClick(FocusBean focusBean) {
        if (!TextUtils.isEmpty(focusBean.getLink())) {
            DcRouter.linkTo(getActivity(), focusBean.getLink());
        }
    }

    public void showVerifyDialog(ShortVideoItem shortVideoItem, Block block) {
        if (shortVideoItem != null) {
            if (verifyDialog == null) {
                verifyDialog = new VerifyDialog(getActivity());
                verifyDialog.setOnVerifyListener(new VerifyDialog.OnVerifyListener() {
                    @Override
                    public void onVerifySuccess() {
                        if (shortVideoItem.is_teamwork == 1) {
                            SelectFrameActivity2.startSelectFrameActivity2((BaseCompatActivity) getActivity(),
                                    SelectFrameActivity.VIDEO_TYPE_TEAMWORK, shortVideoItem.getId(),
                                    shortVideoItem.frame_layout);
                        } else {
                            showToast(shortVideoItem.teamwork_tips);
                        }
                    }
                });
            }
            if (!verifyDialog.isShowing()) {
                verifyDialog.show();
            }
        }

    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        if (requestCode == HttpConstant.TYPE_DISCOVERY_BANNER) {
            if (null != bannerView) {
                bannerView.setVisibility(View.GONE);
            }
        } else if (requestCode == HttpConstant.TYPE_DISCOVERY_TOPIC_HOME
                || requestCode == (HttpConstant.TYPE_DISCOVERY_TOPIC_HOME + 1)) {
            discoveryAdapter.showError(requestCode == HttpConstant.TYPE_DISCOVERY_TOPIC_HOME);
        } else if (requestCode == HttpConstant.TYPE_USER_FOLLOW) {
            showToast(message);
        }
    }

    @Override
    public void onBannerOk(final List<Banner> bannerList) {
        this.bannerList = bannerList;
        if (!CollectionUtil.isEmpty(bannerList)) {
            bannerView.setVisibility(View.VISIBLE);
            initBannerView();
            bannerView.setDataList(bannerList);
            bannerView.start();
        } else {
            bannerView.setVisibility(View.GONE);
        }
    }
    @Override
    public void onGetTopicListOk(boolean isRefresh, List<TopicTypeListBean.TopicListBean> list,
                                 boolean hasMore, boolean hasRecommend, boolean hasFocus, String aroundUrl,String bgUrl) {
        discoveryAdapter.addDatas(isRefresh, list, hasMore, hasRecommend, hasFocus);
        Log.i("===yang","onGetTopicListOk " + bgUrl);
        if (isRefresh) {
            this.aroundUrl = aroundUrl;
        }

        if(!TextUtils.isEmpty(bgUrl)){
//            llGoTopic0.setVisibility(View.GONE);
            Glide.with(getContext()).asDrawable().load(bgUrl).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    Log.i("===yang","bitmap " + resource);
                    llGoTopic.setBackground(resource);
                }
            });
        }else{
//            llGoTopic0.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll, int position, long userId, long videoId, boolean isFollowed) {
        discoveryAdapter.refreshFollow(position, userId, isFollowed);
        showToast(isFollowed ? R.string.user_follower : R.string.user_unfollower);
    }

    private class BannerViewFactory implements BannerView.ViewFactory<Banner> {
        @Override
        public View create(Banner item, int position, ViewGroup container) {
            ImageView iv = new ImageView(container.getContext());
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            GlideLoader.loadImage(item.cover, iv);
            return iv;
        }
    }


    @Override
    public void onRefresh() {
        presenter.getBanner();
        presenter.getTopicList(true);
    }

    @Override
    public void onLoadMore() {
        presenter.getTopicList(false);
    }

    private void showAnimImage(boolean show) {
        int first = linearLayoutManager.findFirstVisibleItemPosition();
        int last = linearLayoutManager.findLastVisibleItemPosition();
        if (discoveryAdapter != null && first > -1 && last < discoveryAdapter.getDataContainer().size() && first <= last) {
            for (int i = first; i <= last; i++) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getRecycleView().findViewHolderForAdapterPosition(i);
                if (viewHolder instanceof DiscoveryViewHolder) {
                    ((DiscoveryViewHolder) viewHolder).showAnimImage(show);
                    KLog.i("======showAnimImage position:" + i + " status:" + show);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && data != null
                && requestCode == LoginActivity.REQUEST_CODE_RELOGIN) {
            onRefresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventHelper.unregister(this);
    }

    public int getScrollY() {
        return scrollY;
    }

    private int scrollY;
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        int currentY = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                currentY = 0;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollY = recyclerView.computeVerticalScrollOffset();
            currentY += dy;
            if (scrolListinner != null) {
                scrolListinner.onScroll(currentY, scrollY);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocialLoginOk(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_LOGIN_OK) {
            onRefresh();
        }
    }

    /**
     * 点赞事件
     *
     * @param eventEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeOk(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_LIKE_OK) {
            KLog.i("=====刷新点赞:" + eventEntity.data);
            if (eventEntity.data instanceof ShortVideoLoveResponse) {
                ShortVideoLoveResponse e = (ShortVideoLoveResponse) eventEntity.data;
                discoveryAdapter.refreshLike(e.opus_id, e);
            }
        }
    }

    //评论成功和删除评论刷新数量
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentOkEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_REFRESH_COMMENT) {
            RefreshCommentBean commentBean = (RefreshCommentBean) eventEntity.data;
            if (null != commentBean) {
                discoveryAdapter.refreshCommentCount(commentBean);
            }
        }
    }

    public void setScrolListinner(ScrolListinner scrolListinner) {
        this.scrolListinner = scrolListinner;
    }

    private ScrolListinner scrolListinner;

    public interface ScrolListinner {
        void onScroll(int currentScrollY, int allScrollY);
    }
}
