package com.wmlive.hhvideo.heihei.mainhome.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.common.manager.gift.GiftPresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.discovery.FollowUserResponseEntity;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftEntity;
import com.wmlive.hhvideo.heihei.beans.gifts.SendGiftResultResponse;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoInfoResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.main.VideoListEventEntity;
import com.wmlive.hhvideo.heihei.beans.main.VideoListScrollSynEventEntity;
import com.wmlive.hhvideo.heihei.beans.main.VideoModifyOpusResponse;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.beans.personal.ReportType;
import com.wmlive.hhvideo.heihei.beans.personal.ReportTypeResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoListActivity;
import com.wmlive.hhvideo.heihei.mainhome.adapter.RecommendAdapter;
import com.wmlive.hhvideo.heihei.mainhome.adapter.VideoDetailListRecyclerAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.Block;
import com.wmlive.hhvideo.heihei.mainhome.presenter.FollowUserPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ModifyOpusPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.RecommendPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.SendGiftPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ShortVideoViewCallback;
import com.wmlive.hhvideo.heihei.mainhome.presenter.TopListPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.VideoDetailPresenter;
import com.wmlive.hhvideo.heihei.mainhome.widget.CommentPanel;
import com.wmlive.hhvideo.heihei.mainhome.widget.GiftView;
import com.wmlive.hhvideo.heihei.mainhome.widget.VideoDetailItemView1;
import com.wmlive.hhvideo.heihei.personal.activity.UserAccountEarningsActivity;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.personal.activity.WebViewActivity;
import com.wmlive.hhvideo.heihei.personal.presenter.UserInfoPresenter;
import com.wmlive.hhvideo.heihei.record.activity.RecordActivitySdk;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity2;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.Download;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.dialog.DownloadProgressDialog;
import com.wmlive.hhvideo.widget.dialog.RemindDialog;
import com.wmlive.hhvideo.widget.dialog.VerifyDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.DampLinearLayoutManager;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;
import com.wmlive.networklib.util.NetUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment.TYPE_RECOMMEND;
import static com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment.TYPE_SINGLE_WORK;

/**
 * Created by lsq on 5/7/2018 - 11:11 AM
 * 类描述：
 * 这个页面只考虑推荐，关注，话题，用户，单个作品跳转
 */
public class VideoDetailListFragment extends DcBaseFragment<RecommendPresenter>
        implements RecommendAdapter.OnActiveCallback,
        SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener,
        RecommendPresenter.IRecommendView,
        UserInfoPresenter.IUserInfoView,
        FollowUserPresenter.IFollowUserView,
        ShortVideoViewCallback, GiftPresenter.IGiftView,
        ModifyOpusPresenter.IModifyOpusView,
        VideoDetailPresenter.IVideoDetailView,
        GiftView.GiftViewListener, SendGiftPresenter.ISendGiftView, TopListPresenter.ITopListView {


    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;

    private static final int OPUS_DESC_MAX_LENGTH = 20;
    public static final String KEY_FROM_PAGE_ID = "key_from_page_id";
    public static final String KEY_VIDEO_TYPE = "key_video_type";
    public static final String KEY_VIDEO_LIST = "key_video_list";

    private GiftView giftView;
    private CustomDialog rechargeDialog;
    private VideoDetailListRecyclerAdapter recyclerAdapter;
    private DownloadProgressDialog downloadProgressDialog;
    private VerifyDialog verifyDialog;
    private PopupWindow shareWindow;
    private PopupWindow popupWindow;
    private CommentPanel viewCommentPanel;

    private int fromPageId;
    private int enterPosition;
    private int videoType = TYPE_RECOMMEND;//页面的视频类型
    private List<ShortVideoItem> videoList;
    private int pageId = CommonUtils.getRandom(1000000, 9999999);
    private long typeId;//视频类型的id
    private int currentProductType;

    private Handler handler;
    private boolean isActive = false;
    private UserInfoPresenter userInfoPresenter;
    private FollowUserPresenter followUserPresenter;
    private GiftPresenter giftPresenter;
    private ModifyOpusPresenter modifyOpusPresenter;
    private VideoDetailPresenter videoDetailPresenter;
    private SendGiftPresenter sendGiftPresenter;
    private TopListPresenter topListPresenter;
    private List<ReportType> reportTypeList;
    private long currentVideoId;
    private DampLinearLayoutManager linearLayoutManager;
    private boolean manualStop = false;
    private String pageFrom;

    //从个人页面跳进来需要设置MultiTypeVideoBean的currentProductType
    public static VideoDetailListFragment newInstance(int fromPageId, int videoType, MultiTypeVideoBean multiTypeVideoBean) {
        VideoDetailListFragment fragment = new VideoDetailListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FROM_PAGE_ID, fromPageId);
        bundle.putInt(KEY_VIDEO_TYPE, videoType);
        bundle.putSerializable(KEY_VIDEO_LIST, (Serializable) multiTypeVideoBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected RecommendPresenter getPresenter() {
        return new RecommendPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video_detail_list;
    }

    @Override
    protected void onSingleClick(View v) {

    }

    private boolean directPlay;

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getArguments();
        EventHelper.register(this);
        if (bundle != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            fromPageId = bundle.getInt(KEY_FROM_PAGE_ID);
            videoType = bundle.getInt(KEY_VIDEO_TYPE);
            MultiTypeVideoBean multiTypeVideoBean = (MultiTypeVideoBean) bundle.getSerializable(KEY_VIDEO_LIST);
            if (multiTypeVideoBean != null) {
                if (videoType == RecommendFragment.TYPE_USER_HOME) {
                    typeId = multiTypeVideoBean.getUserId();
                    currentProductType = multiTypeVideoBean.currentProductType;
                    userInfoPresenter = new UserInfoPresenter(this);
                    addPresenter(userInfoPresenter);
                } else if (videoType == RecommendFragment.TYPE_SINGLE_WORK) {
                    typeId = multiTypeVideoBean.getVideoId();
                } else if (videoType == RecommendFragment.TYPE_TOPIC) {
                    typeId = multiTypeVideoBean.getTopicId();
                } else if (videoType == RecommendFragment.TYPE_MUSIC) {
                    typeId = multiTypeVideoBean.getMusicId();
                }
//                videoList = multiTypeVideoBean.getShortVideoItemList();
                pageFrom = multiTypeVideoBean.pageFrom;
                videoList = TransferDataManager.get().getVideoListData();
                enterPosition = multiTypeVideoBean.currentPosition;
                if (videoType == RecommendFragment.TYPE_USER_HOME) {
                    userInfoPresenter.setOffset(multiTypeVideoBean.nextPageOffset);
                } else if (videoType == RecommendFragment.TYPE_TOP_LIST) {
                    topListPresenter = new TopListPresenter(this);
                    addPresenter(topListPresenter);
                    topListPresenter.setOffset(multiTypeVideoBean.nextPageOffset);
                } else {
                    presenter.setVideoOffset(multiTypeVideoBean.nextPageOffset);
                }
            }
        }
        handler = new Handler(Looper.getMainLooper());
        if (videoType == RecommendFragment.TYPE_SINGLE_WORK || videoType == RecommendFragment.TYPE_EXPLOSION) {
            recyclerView.setRefreshEnable(false);
            recyclerView.setLoadMoreEnable(false);
        } else {
            recyclerView.setOnRefreshListener(this);
            recyclerView.setOnLoadMoreListener(this);
        }
        followUserPresenter = new FollowUserPresenter(this);
        giftPresenter = new GiftPresenter(this);
        modifyOpusPresenter = new ModifyOpusPresenter(this);
        videoDetailPresenter = new VideoDetailPresenter(this);
        sendGiftPresenter = new SendGiftPresenter(this);
        addPresenter(followUserPresenter, giftPresenter, modifyOpusPresenter, videoDetailPresenter, sendGiftPresenter);
        linearLayoutManager = new DampLinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setPageSnapEnable();
        recyclerAdapter = new VideoDetailListRecyclerAdapter(videoType, 12, new ArrayList<>(4), pageId,
                recyclerView, linearLayoutManager, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.getRecycleView().addOnScrollListener(onScrollListener);
        recyclerAdapter.setOnActiveCallback(this);
        recyclerAdapter.setEmptyStr(0);
        recyclerAdapter.addData(true, videoList, true);
        viewCommentPanel = new CommentPanel(getActivity());
        getActivity().getWindow().addContentView(viewCommentPanel, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewCommentPanel.setVisibility(View.GONE);
        if (!CollectionUtil.isEmpty(videoList) && enterPosition < videoList.size()) {
            recyclerView.scrollToPosition(enterPosition);
            directPlay = true;
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerAdapter.resumePlay(enterPosition, true, false);
                }
            }, 50);
        } else {
            if (RecommendFragment.TYPE_SINGLE_WORK != videoType && RecommendFragment.TYPE_SINGLE_WORK != videoType) {
                recyclerView.autoRefresh(500);
            }
        }
        reportTypeList = new ArrayList<>();
        List<ReportType> list = InitCatchData.userReposrtList().getReport_type();
        for (ReportType type : list) {
            if (type.getResource() == 1) {
                reportTypeList.add(type);
            }
        }
        if (CollectionUtil.isEmpty(reportTypeList)) {
            userInfoPresenter.getReportList();
        }
        if (RecommendFragment.TYPE_SINGLE_WORK == videoType) {
            getVideoDetail(0, typeId, true);
        }
        giftView = new GiftView(getActivity());
        getActivity().getWindow().addContentView(giftView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        giftView.initData();
        giftView.setVisibility(View.INVISIBLE);
        giftView.setGiftViewListener(this);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            KLog.i("======onScrollStateChanged:" + newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        VideoListScrollSynEventEntity eventEntity = new VideoListScrollSynEventEntity();
                        eventEntity.fromPageId = fromPageId;
                        eventEntity.scrollToPosition = recyclerAdapter.getCurrentDataPosition();
                        EventHelper.post(GlobalParams.EventType.TYPE_SYN_VIDEO_LIST, eventEntity);
                    }
                }, 100);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    @Override
    public void onVisibleChange(int type, boolean visible) {
        KLog.i(getPageType() + "=====onVisibleChange visible:" + visible + " old visible:" + getVisible());
        boolean beforeVisible = getVisible();
        super.onVisibleChange(type, visible);
        if (isLoadFinish) {
            if (videoType == RecommendFragment.TYPE_USER_HOME) {
                if (visible) {
                    if (AccountUtil.getUserId() == typeId && AccountUtil.isLogin()) {
                        resumePlay(recyclerAdapter.getCurrentDataPosition(), false);
                        super.onVisibleChange(type, visible);
                        return;
                    }
                }
            }

            if (visible) {
                resumePlay(recyclerAdapter.getCurrentDataPosition(), false);
            } else {
                stopScroll();
                KLog.i(getPageType() + "=====当前页面不可见，强制停止滚动");
            }
        }
        if (!visible && beforeVisible) {
            KLog.i(getPageType() + "=====当前页面不可见，之前的状态是可见，暂停视频播放");
            pausePlay();
        }


    }

    private void stopScroll() {
        if (recyclerView != null && recyclerView.getRecycleView() != null) {
            recyclerView.getRecycleView().stopScroll();
        }
    }

    public void resumePlay() {
        resumePlay(recyclerAdapter.getCurrentDataPosition(), false);
    }

    //恢复播放
    public void resumePlay(int currentPlayDataPosition, boolean fromDetail) {
        if (isLoadFinish && null != recyclerAdapter) {
//            if (GlobalParams.StaticVariable.sCurrentNetwork == 1 && !GlobalParams.StaticVariable.sAllowdMobile) {//处于移动网络且不允许播放
//                return;
//            }

            if (manualStop) {
                KLog.i("===手动暂停了，不需要播放");
                return;
            }
            recyclerAdapter.resumePlay(currentPlayDataPosition, fromDetail, manualStop);
        }
    }

    public void pausePlay() {
        if (isLoadFinish && null != recyclerAdapter) {
            KLog.i(getPageType() + "==== ,页面暂停播放");
            recyclerAdapter.pausePlay();
        }
    }

    private String getPageType() {
        switch (videoType) {
            case RecommendFragment.TYPE_FOLLOW:
                return "关注 pageId:" + pageId;
            case RecommendFragment.TYPE_RECOMMEND:
                return "推荐 pageId:" + pageId;
            case RecommendFragment.TYPE_USER_HOME:
                return "个人中心 pageId:" + pageId;
            default:
                break;
        }
        return "其他pageId:" + pageId;
    }

    @Override
    public void onRefresh() {
        isActive = false;
        switch (videoType) {
            case RecommendFragment.TYPE_RECOMMEND:
                presenter.getRecommendVideoList(true);
                break;
            case RecommendFragment.TYPE_FOLLOW:
                presenter.getFollowList(true);
                break;
            case RecommendFragment.TYPE_TOPIC:
                presenter.getTopicList(true, typeId);
                break;
            case RecommendFragment.TYPE_MUSIC:
                presenter.getMusicList(true, typeId);
                break;
            case RecommendFragment.TYPE_EXPLOSION:
                presenter.getExplosionList(true);
                break;
            case RecommendFragment.TYPE_USER_HOME:
                userInfoPresenter.getProductList(true, currentProductType, typeId);
                break;
            case RecommendFragment.TYPE_TOP_LIST:
                topListPresenter.getTopList(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoadMore() {
        switch (videoType) {
            case RecommendFragment.TYPE_RECOMMEND:
                presenter.getRecommendVideoList(false);
                break;
            case RecommendFragment.TYPE_TOPIC:
                presenter.getTopicList(false, typeId);
                break;
            case RecommendFragment.TYPE_MUSIC:
                presenter.getMusicList(false, typeId);
                break;
            case RecommendFragment.TYPE_EXPLOSION:
                presenter.getExplosionList(false);
                break;
            case RecommendFragment.TYPE_FOLLOW:
                presenter.getFollowList(false);
                break;
            case RecommendFragment.TYPE_USER_HOME:
                userInfoPresenter.getProductList(false, currentProductType, typeId);
                break;
            case RecommendFragment.TYPE_TOP_LIST:
                topListPresenter.getTopList(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActive() {
        if (!isActive && null != recyclerAdapter && videoType != RecommendFragment.TYPE_SINGLE_WORK) {
            KLog.i("====激活播放");
            if (!directPlay) {
                DcIjkPlayerManager.get().setPlayerAlpha(0f);
                getWeakHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerAdapter == null) {
                            return;
                        }
                        recyclerAdapter.forcePlay();
                    }
                }, 160);
            }
            directPlay = false;
            isActive = true;
        }
    }

    @Override
    public void onVideoListOk(boolean isRefresh, List<ShortVideoItem> list, List<Banner> bannerList, List<UserInfo> userInfos, boolean hasMore) {
        recyclerAdapter.setEmptyStr(R.string.empty_data_msg);
        recyclerAdapter.addData(isRefresh, list, hasMore);
        if (!CollectionUtil.isEmpty(list)) {
            VideoListEventEntity eventEntity = new VideoListEventEntity();
            eventEntity.isRefresh = isRefresh;
            eventEntity.fromPageId = fromPageId;
//            eventEntity.videoList = list;
//            eventEntity.bannerList = bannerList;
            TransferDataManager.get().setVideoListData(list);
            TransferDataManager.get().setBannerListData(bannerList);
            eventEntity.hasMore = hasMore;
            eventEntity.nextPageOffset = presenter.getVideoOffset();
            EventHelper.post(GlobalParams.EventType.TYPE_VIDEO_LIST, eventEntity);
        }

        if (!recyclerAdapter.hasContent()) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }

    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        dismissLoad();
        if ((requestCode == HttpConstant.TYPE_RECOMMEND_VIDEO_LIST)
                || (requestCode == HttpConstant.TYPE_RECOMMEND_VIDEO_LIST + 1) ||
                (requestCode == HttpConstant.TYPE_PERSONAL_PRODUCT_LIST)
                || (requestCode == HttpConstant.TYPE_PERSONAL_PRODUCT_LIST + 1) ||
                (requestCode == HttpConstant.TYPE_DISCOVERY_MUSIC_LIST)
                || (requestCode == HttpConstant.TYPE_DISCOVERY_MUSIC_LIST + 1) ||
                (requestCode == HttpConstant.TYPE_DISCOVERY_TOPIC_LIST)
                || (requestCode == HttpConstant.TYPE_DISCOVERY_TOPIC_LIST + 1) ||
                (requestCode == HttpConstant.TYPE_EXPLOSION_VIDEO)
                || (requestCode == HttpConstant.TYPE_EXPLOSION_VIDEO + 1)
                || requestCode == HttpConstant.TYPE_PERSONAL_LIKE_LIST
                || requestCode == (HttpConstant.TYPE_PERSONAL_LIKE_LIST + 1) ||
                (requestCode == HttpConstant.TYPE_FOLLOW_VIDEO_LIST)
                || (requestCode == HttpConstant.TYPE_FOLLOW_VIDEO_LIST + 1)) {
            recyclerAdapter.showError((requestCode == HttpConstant.TYPE_RECOMMEND_VIDEO_LIST)
                    || (requestCode == HttpConstant.TYPE_PERSONAL_PRODUCT_LIST)
                    || (requestCode == HttpConstant.TYPE_DISCOVERY_MUSIC_LIST)
                    || (requestCode == HttpConstant.TYPE_DISCOVERY_TOPIC_LIST)
                    || (requestCode == HttpConstant.TYPE_EXPLOSION_VIDEO)
                    || requestCode == HttpConstant.TYPE_PERSONAL_LIKE_LIST
                    || (requestCode == HttpConstant.TYPE_FOLLOW_VIDEO_LIST));
            if (NetUtil.getNetworkState(DCApplication.getDCApp()) == 2) {
                ToastUtil.showToast("请检查网络设置");
            }
        } else if (requestCode == HttpConstant.TYPE_VIDEO_INFO) {
//            showToast("获取视频详情失败，请稍后再试");
        } else if (requestCode == HttpConstant.TYPE_REPORT_TYPE_LIST) {
            showToast("获取举报信息失败，请稍后再试");
        } else if (requestCode == HttpConstant.TYPE_REPORT) {
            showToast("举报失败，请稍后再试");
        } else if (requestCode == HttpConstant.TYPE_VIDEO_DELETE) {
            showToast("删除作品失败，请稍后再试");
        } else if (requestCode == HttpConstant.TYPE_VIDEO_LOVE) {
            showToast("点赞失败，请稍后再试");
        } else if (requestCode == HttpConstant.TYPE_USER_FOLLOW) {
            showToast("关注失败，请稍后再试");
        } else {
            super.onRequestDataError(requestCode, message);
        }
    }

    @Override
    public void onGetProductFail(boolean isRefresh, String message) {
        recyclerAdapter.showError(isRefresh);
        showToast(message);
    }

    @Override
    public void onGetReportListOk(ReportTypeResponse reportTypeResponse) {
        reportTypeList = reportTypeResponse.getReport_type_list();
    }

    @Override
    public void onGetReportUserOk() {
        showToast(R.string.user_report_suc);
    }

    @Override
    public void onVideoDetailOk(long videoId, int position, ShortVideoInfoResponse videoInfoBean) {
        dismissLoad();
        recyclerAdapter.setEmptyStr(R.string.empty_data_msg);
        recyclerAdapter.refreshVideoDetail(videoId, position, videoInfoBean);
    }

    @Override
    public void onVideoError(int position, long videoId, String message) {
        recyclerAdapter.onVideoError(position, videoId, message);
        recyclerAdapter.setEmptyStr(R.string.empty_data_msg);
        if (CollectionUtil.isEmpty(recyclerAdapter.getDataContainer())) {
            showToast(message);
            dismissLoad();
        }
        if (videoType == TYPE_SINGLE_WORK) {
            showToast(message);
            if (getActivity() != null) {
                if (!TextUtils.isEmpty(pageFrom) && "splashpage".equals(pageFrom)) {
                    MainActivity.startMainActivity(getActivity());
                } else {
                    getActivity().finish();
                }
            }
        }
    }

    @Override
    public void onReportListOk(List<ReportType> list) {
        InitCatchData.setReportEntry(list);
        if (currentVideoId > 0 && !CollectionUtil.isEmpty(list)) {
            if (AccountUtil.isLogin()) {
                showReportPop(list);
            } else {
                showReLogin();
            }
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onReportOk() {
        showToast("举报成功");
    }

    @Override
    public void onDeleteVideoOk(int position, long videoId) {
        showToast("删除作品成功");
        EventHelper.post(GlobalParams.EventType.TYPE_VIDEO_DELETE, videoId);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void showFullScreen(boolean isFull, int showType) {
        manualStop = showType == VideoDetailItemView1.SHOW_TYPE_FRAME_VIEW;
//        boolean forbidScroll = isFull || showType == VideoDetailItemView1.SHOW_TYPE_FRAME_VIEW;
        boolean forbidScroll = isFull;
        linearLayoutManager.setScrollEnabled(!forbidScroll);
        recyclerView.setRefreshEnable(!forbidScroll);
        KLog.i("=======showFullScreen:" + isFull + " ,showType:" + showType + " ,forbidScroll:" + forbidScroll);
    }

    @Override
    public void beforePlay() {
        if (recyclerAdapter != null) {
            if (!getVisible() || !recyclerAdapter.hasContent()) {
                KLog.i(getPageType() + "====beforePlay=页面不可见，暂停播放");
                pausePlay();
            }
        }
    }

    @Override
    public void getVideoDetail(int position, long videoId, boolean needBarrage) {
        if (videoId > 0) {
            currentVideoId = videoId;
            videoDetailPresenter.getVideoDetail(position, videoId, needBarrage ? 1 : 0);
        }
    }

    @Override
    public void getNextPageList(int position) {
        if (position > 0 && isVisible && videoType != RecommendFragment.TYPE_EXPLOSION) {
            KLog.i("=====自动拉取下一页的数据");
            onLoadMore();
        }
    }


    @Override
    public void onTopicInfoOk(TopicInfoBean bean) {
        //不需要
    }

    @Override
    public void onGetUserInfoOk(UserInfo userInfo) {
        //不需要
    }

    @Override
    public void onGetUserInfoFail(String message) {
        //不需要
    }


    @Override
    public void onVideoClick(boolean isDetail, int position, View view, View cover, ShortVideoItem videoItem, int showType) {
        //不需要
    }

    @Override
    public void onContinunousClick(int position, ShortVideoItem videoItem, float rawX, float rawY) {
        KLog.i("========点赞");
        onLikeClick(position, videoItem.getId(), videoItem.is_like(), true, true,
                0, 0, 0, 0);
    }

    @Override
    public void onLikeClick(int position, long videoId, boolean isLike,
                            boolean isFlyLike, boolean doRequest,
                            float rawDownX, float rawDownY, float targetRawX, float targetRawY) {
        if (AccountUtil.isLogin()) {
            if (!isFlyLike) {
                if (videoId > 0) {
                    presenter.likeVideo(position, videoId, isLike, false);
                }
            } else {
                if (doRequest) {
                    presenter.likeVideo(position, videoId, isLike, true);
                }
            }
        } else {
            showReLogin();
        }
    }

    @Override
    public void onLikeOk(long videoId, int position, ShortVideoLoveResponse bean) {
        if (bean != null) {
            recyclerAdapter.refreshLike(false, bean);
            EventHelper.post(GlobalParams.EventType.TYPE_LIKE_OK, bean);
        }
    }

    @Override
    public void onLikeFail(long videoId, int position, boolean isFlyLike) {
        showToast("操作失败，请稍后再试");
        if (isFlyLike) {//说明连续点赞失败，需要回置like
            ShortVideoLoveResponse response = new ShortVideoLoveResponse();
            response.opus_id = videoId;
            response.is_like = false;
            recyclerAdapter.refreshLike(true, response);
        }
    }

    @Override
    public void onCommentClick(int position, long videoId, ShortVideoItem videoItem) {
        viewCommentPanel.show(pageId, videoId, position, videoItem);
    }

    @Override
    public void onGiftDismiss(int position) {

    }

    @Override
    public void doPayGift(int position, long videoId, String giftIds, String giftsAmount, int all, int decibelCount) {
        KLog.i("=====doPayGift videoId:" + videoId + " ids:" + giftIds + " ,giftsAmount：" + giftsAmount);
        if (videoId > 0 && !TextUtils.isEmpty(giftIds) && !TextUtils.isEmpty(giftsAmount)) {
            sendGiftPresenter.sendGift(position, videoId, giftIds, giftsAmount);
        }
    }

    @Override
    public void onRechargeClick(int position) {
        if (AccountUtil.isLogin()) {
            UserAccountEarningsActivity.startUserAccountActivity(getActivity(), AccountUtil.getUserId());
        } else {
            showReLogin();
        }
    }

    @Override
    public void onGoldNotEnough() {
        if (rechargeDialog == null) {
            rechargeDialog = new CustomDialog(getActivity(), R.style.BaseDialogTheme);
            rechargeDialog.setContent(getString(R.string.stringGoldNotEnough));
            rechargeDialog.setCanceledOnTouchOutside(false);
            rechargeDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    rechargeDialog.dismiss();
                    onRechargeClick(1);
                }
            });
            rechargeDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    rechargeDialog.dismiss();
                }
            });
        }
        if (!rechargeDialog.isShowing()) {
            rechargeDialog.show();
        }
    }

    @Override
    public boolean allowPlay() {
        boolean disallow = getActivity() == null || !getVisible() || getActivity().isFinishing() || getActivity().isDestroyed();
        KLog.i("======当前页面条件" + (disallow ? "不" : "") + "允许播放");
        return !disallow;
    }

    @Override
    public void onUserClick(long userId) {
        UserHomeActivity.startUserHomeActivity(getActivity(), userId);
    }

    @Override
    public void onJoinReplaceClick(int index, ShortVideoItem shortVideoItem) {
        if (AccountUtil.isLogin()) {
            if (AccountUtil.needVerifyCode()) {
                showVerifyDialog(shortVideoItem, new Block() {
                    @Override
                    public void run() {
                        startRecordActivity(shortVideoItem, index);
                    }
                },false);
            } else {
                startRecordActivity(shortVideoItem, index);
            }
        } else {
            showReLogin();
        }
    }

    /**
     * 替换进入录制页
     *
     * @param shortVideoItem
     */
    private void startRecordActivity(ShortVideoItem shortVideoItem, int index) {
        if (shortVideoItem != null) {
            RecordActivitySdk.startRecordActivity((BaseCompatActivity) getActivity(),
                    RecordActivitySdk.TYPE_TOGETHER, shortVideoItem.getId(),
                    shortVideoItem.frame_layout, index, 0);
        }
    }

    @Override
    public void onJoinSingleClick(int index, ShortVideoItem shortVideoItem) {
        if (AccountUtil.isLogin()) {
            if (AccountUtil.needVerifyCode()) {
                showVerifyDialog(shortVideoItem, new Block() {
                    @Override
                    public void run() {
                        startSelectFrameActivity(shortVideoItem,true);
                    }
                },true);
            } else {
                startSelectFrameActivity(shortVideoItem,true);
            }
        } else {
            showReLogin();
        }
    }

    @Override
    public void onJoinCurrentTemplateClick(int index, ShortVideoItem shortVideoItem) {
        if (AccountUtil.isLogin()) {
            if (AccountUtil.needVerifyCode()) {
                showVerifyDialog(shortVideoItem, new Block() {
                    @Override
                    public void run() {
                        startSelectFrameActivity(shortVideoItem,false);
                    }
                },false);
            } else {
                startSelectFrameActivity(shortVideoItem,false);
            }
        } else {
            showReLogin();
        }
    }

    /**
     * 打开画框选取界面
     *
     * @param shortVideoItem
     */
    private void startSelectFrameActivity(ShortVideoItem shortVideoItem, boolean isJoinSingle) {
        if (shortVideoItem != null) {
            if (shortVideoItem.is_teamwork == 1) {
                if (5 == shortVideoItem.origin) {
                    String template = shortVideoItem.creative_template_name;
                    if (RecordMvActivityHelper.isNoInvalidTemplate(getActivity(), template)) {
                        ToastUtil.showToast(getResources().getString(R.string.temp_no_can_use));
                    } else {
                        if (isJoinSingle) {
                            RecordMvActivityHelper.startRecordActivity((BaseCompatActivity) getActivity(), RecordMvActivityHelper.EXTRA_RECORD_TYPE_REPLACE, shortVideoItem.id);
                        } else {
                            RecordMvActivityHelper.startRecordActivity((BaseCompatActivity) getActivity(), RecordMvActivityHelper.EXTRA_RECORD_TYPE_USE_CURENT_TEMPLATE, shortVideoItem.id);
                        }
                    }
                } else {
                    SelectFrameActivity2.startSelectFrameActivity2((BaseCompatActivity) getActivity(),
                            SelectFrameActivity.VIDEO_TYPE_TEAMWORK, shortVideoItem.getId(),
                            shortVideoItem.frame_layout);
                }
            } else {
                showToast(shortVideoItem.teamwork_tips);
            }
        }
    }

    public void showVerifyDialog(ShortVideoItem shortVideoItem, Block block,boolean isJoinSingle) {
        if (shortVideoItem != null) {
            if (verifyDialog == null) {
                verifyDialog = new VerifyDialog(getActivity());
                verifyDialog.setOnVerifyListener(new VerifyDialog.OnVerifyListener() {
                    @Override
                    public void onVerifySuccess() {
                        startSelectFrameActivity(shortVideoItem,isJoinSingle);
                    }
                });
            }
            if (!verifyDialog.isShowing()) {
                verifyDialog.show();
            }
        }

    }

    @Override
    public void onFollowClick(int position, long videoId, long userId, boolean isFollowed) {
        if (AccountUtil.isLogin()) {
            if (userId > 0) {
                if (!isFollowed) {
                    followUserPresenter.follow(false, position, userId, videoId, isFollowed, -1);
                }
            } else {
                showToast(R.string.hintErrorDataDelayTry);
            }
        } else {
            showReLogin();
        }
    }

    private ShortVideoItem shortVideoItem;

    @Override
    public void onShareClick(int position, ShortVideoItem shortVideoItem) {
        UserInfo author = shortVideoItem.getUser();
        boolean isSelf = AccountUtil.isLogin() && (AccountUtil.getUserId() == author.getId());
        this.shortVideoItem = shortVideoItem;
        if (isSelf) {
            shareWindow = PopupWindowUtils.showSelfShare(getActivity(), recyclerView, videoShareClickListener, AccountUtil.isAuthUser(), shortVideoItem.getIs_teamwork() == 1);
        } else {
            shareWindow = PopupWindowUtils.showOtherShare(getActivity(), recyclerView, videoShareClickListener, AccountUtil.isAuthUser());
        }
    }

    private MyClickListener videoShareClickListener = new MyClickListener() {
        @Override
        protected void onMyClick(View v) {
            if (v.getId() == R.id.llManager) {
                if (null != shareWindow) {
                    shareWindow.dismiss();
                }
                String url = InitCatchData.opusManage();
                if (!TextUtils.isEmpty(url)) {
                    url = url + "?token=" + AccountUtil.getToken() + "&opus_id=" + shortVideoItem.getId();
                }
                WebViewActivity.startWebActivity(getActivity(), url, getString(R.string.pop_manager_title));
                return;
            } else if (v.getId() == R.id.llEdit) {
                if (null != shareWindow) {
                    shareWindow.dismiss();
                }
                if (AccountUtil.isLogin()) {
                    if (shortVideoItem != null) {
                        showEditPop();
                    } else {
                        showToast(R.string.hintErrorDataDelayTry);
                    }
                } else {
                    showReLogin();
                }

                return;
            }
            if (shortVideoItem != null && null != shortVideoItem.getShare_info()) {
                shortVideoItem.getShare_info().objId = shortVideoItem.getId();
                shortVideoItem.getShare_info().shareType = ShareEventEntity.TYPE_OPUS;
                switch (v.getId()) {
                    case R.id.llWeChat:
                        shortVideoItem.getShare_info().shareTarget = ShareEventEntity.TARGET_WECHAT;
                        if (shortVideoItem.getShare_info().wxprogram_share_info != null) {
                            wxMinAppShare(0, shortVideoItem.getShare_info(), null);
                        } else {
                            wechatShare(0, shortVideoItem.getShare_info());
                        }
                        break;
                    case R.id.llCircle:
                        shortVideoItem.getShare_info().shareTarget = ShareEventEntity.TARGET_FRIEND;
                        wechatShare(1, shortVideoItem.getShare_info());
                        break;
                    case R.id.llWeibo:
                        shortVideoItem.getShare_info().shareTarget = ShareEventEntity.TARGET_WEIBO;
                        weiboShare(shortVideoItem.getShare_info());
                        break;
                    case R.id.llQQ:
                        shortVideoItem.getShare_info().shareTarget = ShareEventEntity.TARGET_QQ;
                        qqShare(shortVideoItem.getShare_info());
                        break;
                    case R.id.llCopy:
                        ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setPrimaryClip(ClipData.newPlainText(null, shortVideoItem.getShare_info().web_link));
                        showToast(R.string.copy_succeed);
                        shortVideoItem.getShare_info().shareTarget = ShareEventEntity.TARGET_WEB;
                        ShareEventEntity.share(shortVideoItem.getShare_info());
                        break;
                    case R.id.llSave:
                        downloadProgressDialog = new DownloadProgressDialog(getActivity());
                        new RxPermissions(getActivity())
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@NonNull Boolean hasGranted) throws Exception {
                                        if (hasGranted) {
                                            Download.start(getActivity()
                                                    , shortVideoItem.getShare_info().download_link
                                                    , Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera"
                                                    , "", "mp4"
                                                    , new VideoReceiver(VideoDetailListFragment.this, handler));
                                        } else {
                                            showToast(getString(R.string.stringPleaseGrantAppWritePermission));
                                        }
                                    }
                                });
                        break;
                    case R.id.llDelete:
                        showDeleteVideoDialog();
                        break;
                    case R.id.llReport:
                        if (AccountUtil.isLogin()) {
                            List<ReportType> list = InitCatchData.getReportEntry();
                            if (!CollectionUtil.isEmpty(list)) {
                                if (shortVideoItem.getId() > 0) {
                                    showReportPop(list);
                                } else {
                                    showToast(R.string.hintErrorDataDelayTry);
                                }
                            } else {
                                presenter.getReportType();
                            }
                        } else {
                            showReLogin();
                        }
                        break;
                    default:
                        break;
                }
                if (null != shareWindow) {
                    shareWindow.dismiss();
                }
            }
        }
    };

    @Override
    public void onModifyOpusOk(int position, long videoId, VideoModifyOpusResponse response) {
        if (response != null) {
            recyclerAdapter.refreshPermissionsAndTitle(videoId, response);
            showToast(getResources().getString(R.string.save_suc));
            EventHelper.post(GlobalParams.EventType.TYPE_MODIFY_PERMISSION_OK, response);
        }
    }

    @Override
    public void onModifyOpusFail(int position, long videoId, String message) {
        showToast(message);
    }

    @Override
    public void onSendGiftOk(int position, long videoId, String giftId, SendGiftResultResponse sendGiftResultResponse) {
        getWeakHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sendGiftResultResponse != null
                        && sendGiftResultResponse.settle_msg != null
                        && !CollectionUtil.isEmpty(sendGiftResultResponse.settle_msg.prize_message)) {
                    giftView.showFreeResultPanel(sendGiftResultResponse.settle_msg);
                    giftView.setVisibility(View.VISIBLE);
                    giftView.removeSendGift(giftId);
                } else {
                    giftView.removeSendGift(giftId);
                    if (giftView.getVisibility() == View.VISIBLE) {
                        giftView.dismiss();
                    }
                }
            }
        }, 400);
        recyclerAdapter.refreshGift(videoId, sendGiftResultResponse);
    }

    @Override
    public void onSendGiftFail(int position, long videoId, String giftId, String message) {
        showToast(message);
        if (giftView != null) {
            giftView.removeSendGift(giftId);
            giftView.dismiss();
        }
    }

    @Override
    public void onTopListOk(boolean isRefresh, List<ShortVideoItem> list, boolean hasMore,String detail) {
        recyclerAdapter.setEmptyStr(R.string.empty_data_msg);
        recyclerAdapter.addData(isRefresh, list, hasMore);
        if (!CollectionUtil.isEmpty(list)) {
            VideoListEventEntity eventEntity = new VideoListEventEntity();
            eventEntity.isRefresh = isRefresh;
            eventEntity.fromPageId = fromPageId;
//            eventEntity.videoList = list;
//            eventEntity.bannerList = bannerList;
            TransferDataManager.get().setVideoListData(list);
            eventEntity.hasMore = hasMore;
            eventEntity.nextPageOffset = presenter.getVideoOffset();
            EventHelper.post(GlobalParams.EventType.TYPE_VIDEO_LIST, eventEntity);
        }

        if (!recyclerAdapter.hasContent()) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void onTopListFail(boolean isRefresh, String message) {
        recyclerAdapter.setEmptyStr(R.string.empty_data_msg);
        recyclerAdapter.addData(isRefresh, null, false);
        showToast(message);
    }

    private static class VideoReceiver extends ResultReceiver {
        private WeakReference<VideoDetailListFragment> activity;

        @SuppressLint("RestrictedApi")
        public VideoReceiver(VideoDetailListFragment activity, Handler handler) {
            super(handler);
            this.activity = new WeakReference<>(activity);
        }

        @SuppressLint("RestrictedApi")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            String savePath = null;
            if (null != resultData) {
                savePath = resultData.getString("savePath");
            }
            if (activity != null && activity.get() != null) {
                switch (resultCode) {
                    case Download.RESULT_PREPARE:
                        activity.get().downloadProgressDialog.showDownload(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Download.pause(activity.get().getActivity());
                            }
                        });
                        activity.get().downloadProgressDialog.updateProgress(0);
                        break;
                    case Download.RESULT_START:
                        break;
                    case Download.RESULT_PAUSE:
                        activity.get().downloadProgressDialog.dissmissDownload();
                        break;
                    case Download.RESULT_DOWNLOADING:
                        if (null != resultData) {
                            activity.get().downloadProgressDialog.updateProgress(resultData.getInt("percent"));
                        }
                        break;
                    case Download.RESULT_ERROR:
                        KLog.i("=====download error:" + (resultData != null ? resultData.getString("message") : "null"));
                        activity.get().showToast("保存失败");
                        activity.get().downloadProgressDialog.dissmissDownload();
                        break;
                    case Download.RESULT_COMPLETE:
                        activity.get().showToast("保存成功");
                        activity.get().downloadProgressDialog.dissmissDownload();
                        if (!TextUtils.isEmpty(savePath)) {
//                            EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR,
//                                    new UserBehavior(activity.get().shortVideoItem.getId(),
//                                            0, 0, 1));
                            DiscoveryUtil.updateMedia(activity.get().getActivity(), savePath);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

    }

    private void showReportPop(final List<ReportType> list) {
        if (!CollectionUtil.isEmpty(list)) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.ppw_report_works, null);
            ListView listView = (ListView) view.findViewById(R.id.lvReportList);
            final PopupWindow popupWindow = PopupWindowUtils.createPopWindowFromBottom(recyclerView, view);
            view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            List<String> stringList = new ArrayList<>();
            final List<ReportType> reportTypes = new ArrayList<>();
            for (ReportType reportType : list) {
                if (reportType.getResource() == 0) {
                    reportTypes.add(reportType);
                    stringList.add(reportType.getDesc());
                }
            }
            listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_report_works,
                    stringList.toArray(new String[stringList.size()])));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    presenter.reportWorks(0, shortVideoItem.getId(), reportTypes.get(position).getId());
                    popupWindow.dismiss();
                }
            });
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    /**
     * 视频编辑
     */
    private void showEditPop() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.ppw_edit_video, null);
        popupWindow = PopupWindowUtils.createPopWindowFromBottom(recyclerView, view, 0.5f);
        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });
        EditText etOpusDesc = view.findViewById(R.id.etOpusDesc);
        CheckBox cbAllow = view.findViewById(R.id.cbAllow);
        TextView tvDescCount = view.findViewById(R.id.tvDescCount);
        TextView btnSave = view.findViewById(R.id.btnSave);

        cbAllow.setChecked(shortVideoItem.getIs_teamwork() == 1);
        etOpusDesc.setText(shortVideoItem.getTitle());
        setCountText(tvDescCount, etOpusDesc.getText().length());
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                KLog.d("xxxx", "source " + source + " start " + start + " end " + end + " dstart " + dstart + " dend " + dend);
                if (!TextUtils.isEmpty(source)) {
                    int destLength = dest.length();
                    if (destLength + source.length() > OPUS_DESC_MAX_LENGTH) {
                        // 超出字数限制
                        ToastUtil.showToast(R.string.subject_add_error);
                        int incrementLength = OPUS_DESC_MAX_LENGTH - destLength;
                        if (incrementLength >= 0 && incrementLength < source.length()) {
                            return source.subSequence(0, incrementLength);
                        } else {
                            return "";
                        }
                    } else {
                        // 字数范围内
                        return source;
                    }
                } else {
                    // 删除操作
                    return source;
                }
            }
        };
        etOpusDesc.setFilters(new InputFilter[]{inputFilter});
        etOpusDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                setCountText(tvDescCount, charSequence.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etOpusDesc.getText().length() > OPUS_DESC_MAX_LENGTH) {
                    ToastUtil.showToast(R.string.subject_add_error);
                } else {
                    if (shortVideoItem != null) {
                        modifyOpusPresenter.modifyOpus(0, shortVideoItem.getId(), etOpusDesc.getText().toString(), cbAllow.isChecked() ? "1" : "0");
                    }
                    popupWindow.dismiss();
                }
            }
        });
    }

    private void setCountText(TextView tvDescCount, int length) {
        String textCount = length + "/" + OPUS_DESC_MAX_LENGTH;
        if (length >= OPUS_DESC_MAX_LENGTH) {
            SpannableString spanString = new SpannableString(textCount);
            ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.hh_color_f));
            spanString.setSpan(span, 0, String.valueOf(length).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tvDescCount.setText(spanString);
        } else {
            tvDescCount.setText(textCount);
        }
    }

    /**
     * 保存弹窗
     */
    private void showSaveDialog() {
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setContent("是否放弃编辑？");
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void showDeleteVideoDialog() {
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setContent("是否删除视频？");
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.deleteVideo(0, shortVideoItem.getId());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onGiftClick(int position, ShortVideoItem shortVideoItem) {
        if (AccountUtil.isLogin()) {
            giftPresenter.getGiftList(shortVideoItem.getId());
//            giftView.setVisibility(View.VISIBLE);
//            giftView.showGiftPanel(0, videoId, null);
        } else {
            showReLogin();
        }
    }

    @Override
    public void onTopicClick(int topicId) {
        if (topicId > 0) {
            VideoListActivity.startVideoListActivity(getActivity(), RecommendFragment.TYPE_TOPIC,
                    MultiTypeVideoBean.createTopicParma(topicId, 0, null));
        }
    }

    @Override
    public void onCloseClick() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll, int position, long userId, long videoId, boolean isFollowed) {
        showToast(isFollowed ? R.string.user_follower : R.string.user_unfollower);
        recyclerAdapter.refreshFollow(videoId, isFollowed);
    }

    @Override
    public void onGiftListOk(List<GiftEntity> giftEntities, boolean isInit, long videoId) {
        if (!CollectionUtil.isEmpty(giftEntities)) {
            giftView.setVisibility(View.VISIBLE);
            giftView.showGiftPanel(0, videoId, null, giftEntities);
        } else {
            showToast(getString(R.string.stringGiftDataError));
        }
    }

    @Override
    public void onGiftListFail(String message) {
        showToast(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventHelper.unregister(this);
    }

    public boolean onBackPress() {
        if (recyclerAdapter.isFullScreen()) {
            return true;
        }
        if (viewCommentPanel != null && viewCommentPanel.getVisibility() == View.VISIBLE) {
            viewCommentPanel.dismiss();
            return true;
        }
        if (giftView != null && giftView.getVisibility() == View.VISIBLE) {
            giftView.dismiss();
            return true;
        }
        return false;
    }

    /**
     * 关注用户消息
     *
     * @param eventEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_FOLLOW_OK) {
            if (eventEntity.data != null && eventEntity.data instanceof FollowUserResponseEntity) {
                FollowUserResponseEntity followUserEntity = (FollowUserResponseEntity) eventEntity.data;
                recyclerAdapter.refreshFollow(followUserEntity.userId, followUserEntity.is_follow);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetChangeEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_NETWORK_CHANGE) {
            if (eventEntity.data instanceof Integer) {
                if ((int) eventEntity.data == 1 && !GlobalParams.StaticVariable.sHasShowedRemind) {
                    GlobalParams.StaticVariable.sHasShowedRemind = true;
                    new RemindDialog(getActivity()).show();
                }
            }
        }
    }

    //    //评论成功和删除评论刷新数量
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentOkEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_REFRESH_COMMENT) {
            RefreshCommentBean commentBean = (RefreshCommentBean) eventEntity.data;
            if (null != commentBean) {
                recyclerAdapter.refreshCommentCount(commentBean);
            }
        }
    }
}
