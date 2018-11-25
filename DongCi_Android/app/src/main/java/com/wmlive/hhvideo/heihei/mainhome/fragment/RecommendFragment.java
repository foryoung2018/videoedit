package com.wmlive.hhvideo.heihei.mainhome.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseFragment;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.discovery.FollowUserResponseEntity;
import com.wmlive.hhvideo.heihei.beans.discovery.MusicInfoBean;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoInfoResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.main.UserBehavior;
import com.wmlive.hhvideo.heihei.beans.main.VideoListEventEntity;
import com.wmlive.hhvideo.heihei.beans.main.VideoListScrollSynEventEntity;
import com.wmlive.hhvideo.heihei.beans.main.VideoModifyOpusResponse;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.beans.personal.ReportType;
import com.wmlive.hhvideo.heihei.beans.personal.ReportTypeResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.discovery.presenter.TopicPresenter;
import com.wmlive.hhvideo.heihei.discovery.widget.TopicHeader;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoDetailListActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoListActivity;
import com.wmlive.hhvideo.heihei.mainhome.adapter.RecommendAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.Block;
import com.wmlive.hhvideo.heihei.mainhome.presenter.FollowUserPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.RecommendPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ShortVideoViewCallback;
import com.wmlive.hhvideo.heihei.mainhome.presenter.TopListPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.VideoDetailPresenter;
import com.wmlive.hhvideo.heihei.mainhome.util.ScreenShot;
import com.wmlive.hhvideo.heihei.mainhome.widget.CommentPanel;
import com.wmlive.hhvideo.heihei.mainhome.widget.RecommendUserPanel;
import com.wmlive.hhvideo.heihei.message.activity.IMMessageActivity;
import com.wmlive.hhvideo.heihei.personal.activity.AddFriendActivity;
import com.wmlive.hhvideo.heihei.personal.activity.DecibelListActivity;
import com.wmlive.hhvideo.heihei.personal.activity.DraftBoxActivity;
import com.wmlive.hhvideo.heihei.personal.activity.FansActivity;
import com.wmlive.hhvideo.heihei.personal.activity.FocusActivity;
import com.wmlive.hhvideo.heihei.personal.activity.PersonalInfoActivity;
import com.wmlive.hhvideo.heihei.personal.activity.SettingActivity;
import com.wmlive.hhvideo.heihei.personal.activity.UserAccountEarningsActivity;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.personal.activity.WebViewActivity;
import com.wmlive.hhvideo.heihei.personal.presenter.BlockUserPresenter;
import com.wmlive.hhvideo.heihei.personal.presenter.UserAccountInfoPresenter;
import com.wmlive.hhvideo.heihei.personal.presenter.UserInfoPresenter;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountInfoView;
import com.wmlive.hhvideo.heihei.personal.widget.ProductTypePanel;
import com.wmlive.hhvideo.heihei.personal.widget.UserInfoHeader;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity2;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.Download;
import com.wmlive.hhvideo.widget.dialog.AvatarDialog;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.dialog.DownloadProgressDialog;
import com.wmlive.hhvideo.widget.dialog.LoginDialog;
import com.wmlive.hhvideo.widget.dialog.MyDialog;
import com.wmlive.hhvideo.widget.dialog.PopActionSheetNoTitle;
import com.wmlive.hhvideo.widget.dialog.PopReportContentActionSheet;
import com.wmlive.hhvideo.widget.dialog.RemindDialog;
import com.wmlive.hhvideo.widget.dialog.VerifyDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.DampLinearLayoutManager;
import com.wmlive.hhvideo.widget.refreshrecycler.OnFooterClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;
import com.wmlive.networklib.util.NetUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 6/19/2017.
 * 首页推荐和关注列表
 */

public class RecommendFragment extends BaseFragment<RecommendPresenter> implements
        SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener,
        RecommendPresenter.IRecommendView,
        ShortVideoViewCallback,
        RecommendAdapter.OnActiveCallback,
        IUserAccountInfoView,
        UserInfoHeader.OnUserInfoClickListener,
        UserInfoPresenter.IUserInfoView,
        FollowUserPresenter.IFollowUserView,
        ProductTypePanel.OnTypeClickListener,
        TopicPresenter.ITopicView,
        TopicHeader.OnHeaderClickListener,
        VideoDetailPresenter.IVideoDetailView,
        BlockUserPresenter.IBlockUserView,
        RecommendUserPanel.OnFollowListener {

    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;
    @BindView(R.id.rlFlyHeart)
    RelativeLayout rlFlyHeart;
    @BindView(R.id.llDraftInfo)
    LinearLayout llDraftInfo;
    @BindView(R.id.tvDraftCount)
    TextView tvDraftCount;
    @BindView(R.id.rlUserHomeInfo)
    RelativeLayout rlUserHomeInfo;
    @BindView(R.id.llExtraProductType)
    ProductTypePanel llExtraProductType;
    @BindView(R.id.ivBigAvatar)
    ImageView ivBigAvatar;
    @BindView(R.id.rlToolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.tvNickname)
    TextView tvNickname;
    @BindView(R.id.ivEmptyHint)
    ImageView ivEmptyHint;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivShare)
    ImageView ivShare;
    @BindView(R.id.ivMessage)
    ImageView ivMessage;
    @BindView(R.id.ivAdd)
    ImageView ivAdd;
    @BindView(R.id.ivSetting)
    ImageView ivSetting;
    @BindView(R.id.ivMore)
    ImageView ivMore;
    @BindView(R.id.viewHolder)
    LinearLayout viewHolder;
    @BindView(R.id.llMenus)
    LinearLayout llMenus;
    @BindView(R.id.blockLayerMask)
    RelativeLayout blockLayerMask;
    @BindView(R.id.unblockUser)
    TextView unblockUser;
    @BindView(R.id.ivBlockBack)
    ImageView ivBlockBack;
    @BindView(R.id.viewRecommendUsers)
    RecommendUserPanel viewRecommendUsers;
    @BindView(R.id.tvJoin)
    TextView tvJoin;
    @BindView(R.id.flJoin)
    FrameLayout flJoin;
    CommentPanel viewCommentPanel;

    private RecommendScrollListener recommendScrollListener;

    private static final int toolbarHeight = DeviceUtils.dip2px(DCApplication.getDCApp(), 44);
    private static final int sSlideValue = (int) (DeviceUtils.getScreenWH(DCApplication.getDCApp())[1] * 0.7f);
    private int blackHeight = 0;

    public static final String KEY_DANMU = "key_danmu";//弹幕key

    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_VIDEO_TYPE = "key_video_type";
    public static final String KEY_VIDEO_LIST = "key_video_list";
    public static final String KEY_SHOW_BACK = "key_show_back";
    public static final String KEY_IN_VIEWPAGER = "key_in_viewpager";
    public static final String KEY_FROM_IMCHAT = "from_imchat";

    public static final int TYPE_RECOMMEND = 10;//推荐
    public static final int TYPE_TOPIC = 30;//话题
    public static final int TYPE_MUSIC = 40;//音乐
    public static final int TYPE_EXPLOSION = 50; //话题
    public static final int TYPE_FOLLOW = 70; //关注
    public static final int TYPE_SINGLE_WORK = 80; //单个作品
    public static final int TYPE_USER_HOME = 100; //用户页面
    public static final int TYPE_TOP_LIST = 110; //用户页面

    private boolean isActive;
    protected int videoType = TYPE_RECOMMEND;//页面的视频类型
    private long typeId;//视频类型的id
    private int pageId;
    private long currentVideoId;//当前视频的id，只能作为举报作品和删除作品的时候使用
    private int currentVideoPosition;//当前位置

    private long userId = 0;
    //    private boolean showBack = true;
    private boolean inViewPager = false;
    private int scrollY = 0;
    private int draftSize = 0;
    public int currentProductType = ProductTypePanel.TYPE_PRODUCT;
    private TranslateAnimation hintAnim;
    private UserInfo userInfo;
    private ShareInfo shareHome;
    private List<ReportType> reportTypeList;
    private UserInfoHeader userInfoHeader;
    private UserInfoPresenter userInfoPresenter;
    private BlockUserPresenter blockUserPresenter;
    private FollowUserPresenter followUserPresenter;
    private TopListPresenter a;

    private MultiTypeVideoBean videoParam;
    private UserAccountInfoPresenter userAccountInfoPresenter;
    private Handler handler;
    private DampLinearLayoutManager linearLayoutManager;
    public RecommendAdapter recommendAdapter;
    private PopupWindow shareWindow;
    private ShareInfo shareInfo;
    private MyDialog useMobileDialog;
    private CustomDialog rechargeDialog;
    private CustomDialog customDialog;
    private DownloadProgressDialog downloadProgressDialog;
    private VerifyDialog verifyDialog;
    private PopActionSheetNoTitle morePopWindow;
    private PopReportContentActionSheet reportPopWindow;
    private TopicHeader topicHeader;
    private TopicPresenter topicPresenter;
    private ShareInfo topicShareInfoBean;
    private TopicInfoBean topicInfoBean;
    private VideoDetailPresenter videoDetailPresenter;
    private boolean fromIMChat = false;
    private boolean hasScrolled = false;
    private LoginDialog loginDialog;
    private AvatarDialog avatarDialog;
    private List<ShortVideoItem> shortVideoItems = new ArrayList<>();

    public static RecommendFragment newInstance(int type) {
        return newInstance(type, null);
    }

    public static RecommendFragment newInstance(int videoType, MultiTypeVideoBean explosionVideoParam) {
        return newInstance(videoType, 0, explosionVideoParam);
    }

    public static RecommendFragment newInstance(int videoType, long userId, MultiTypeVideoBean explosionVideoParam) {
        return newInstance(videoType, userId, explosionVideoParam, false);
    }

    public static RecommendFragment newInstance(int videoType, long userId, MultiTypeVideoBean explosionVideoParam, boolean fromIMChat) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LAZY_MODE, true);
        bundle.putBoolean(SINGLE_MODE, videoType != TYPE_FOLLOW);
        bundle.putInt(KEY_VIDEO_TYPE, videoType);
        bundle.putLong(KEY_USER_ID, userId);
        bundle.putBoolean(KEY_FROM_IMCHAT, fromIMChat);
        if (null != explosionVideoParam) {
            bundle.putSerializable(KEY_VIDEO_LIST, explosionVideoParam);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected RecommendPresenter getPresenter() {
        return new RecommendPresenter(this);
    }

    @Override
    protected int getBaseLayoutId() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.ivBigAvatar:
                ivBigAvatar.setVisibility(View.GONE);
                break;
            case R.id.ivSetting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.ivAdd:
                if (!AccountUtil.isLogin()) {
                    showReLogin();
                    return;
                }
                if (shareInfo != null) {
                    AddFriendActivity.startAddFriendActivity(getActivity(), shareInfo);
                } else {
                    showToast(R.string.hintErrorDataDelayTry);
                }
                break;
            case R.id.ivMore:
                if (!AccountUtil.isLogin()) {
                    showReLogin();
                } else {
                    if (null != reportTypeList && reportTypeList.size() > 0) {
                        boolean isUserBlock = false;
                        if (userInfo != null && userInfo.getRelation() != null) {
                            isUserBlock = userInfo.getRelation().is_block;
                        }
                        morePopWindow = new PopActionSheetNoTitle(getActivity());
                        morePopWindow.setIsUserBlock(isUserBlock);
                        morePopWindow.setOnSnsClickListener(new PopActionSheetNoTitle.OnSnsClickListener() {
                            @Override
                            public void onSnsClick() {
                                reportPopWindow = new PopReportContentActionSheet(getActivity());
                                reportPopWindow.setReportTypeList(reportTypeList);
                                reportPopWindow.setOnSnsClickListener(new PopReportContentActionSheet.OnSnsClickListener() {
                                    @Override
                                    public void onSnsClick(ReportType reportType) {
                                        if (null != reportType) {
                                            if (null != userInfo) {
                                                userInfoPresenter.reportUser(userInfo.getId(), reportType.getId());
                                            }
                                        }
                                        reportPopWindow.dismiss();
                                    }
                                });
                                reportPopWindow.show();
                            }

                            @Override
                            public void onUserBlockClick(boolean isUserBlock) {
                                showBlockDialog(isUserBlock);
                            }
                        });
                        morePopWindow.show();
                    }
                }
                break;
            case R.id.ivMessage:
                if (AccountUtil.isLogin()) {
                    if (fromIMChat) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        IMMessageActivity.startIMMessageActivity((BaseCompatActivity) getActivity(), userId, userInfo);
                    }
                } else {
                    showReLogin();
                }
                break;
            case R.id.ivShare:
                if (shareHome != null) {
                    shareWindow = PopupWindowUtils.showNormal(getActivity(), ivShare, userHomeShareClick);
                } else {
                    showToast(R.string.hintErrorDataDelayTry);
                }
                break;
            case R.id.ivBack:
                if (avatarDialog != null && avatarDialog.isShowing()) {
                    avatarDialog.dismiss();
                    return;
                }
                if (getActivity() instanceof UserHomeActivity) {
                    if (inViewPager) {
                        getActivity().onKeyDown(KeyEvent.KEYCODE_BACK, null);
                    } else {
                        getActivity().onBackPressed();
                    }
                } else {
                    getActivity().onBackPressed();
                }
                break;
            case R.id.llDraftInfo:
                if (draftSize > 0) {
                    startActivity(new Intent(getActivity(), DraftBoxActivity.class));
                } else {
                    showToast(R.string.hintErrorDataDelayTry);
                }
                break;
            case R.id.unblockUser:
                // 解除拉黑
                showBlockDialog(true);
                break;
            case R.id.ivBlockBack:
                if (getActivity() instanceof UserHomeActivity) {
                    if (inViewPager) {
                        getActivity().onKeyDown(KeyEvent.KEYCODE_BACK, null);
                    } else {
                        getActivity().onBackPressed();
                    }
                } else {
                    getActivity().onBackPressed();
                }
                break;
            case R.id.tvJoin:
                onJoinTopicClick(topicInfoBean);
                break;
            default:
                break;
        }
    }

    /**
     * 拉黑对话框
     *
     * @param isUserBlock
     */
    private void showBlockDialog(boolean isUserBlock) {
        CustomDialog blockDialog = new CustomDialog(getActivity(), R.style.BaseDialogTheme);
        blockDialog.setContent(isUserBlock ? R.string.user_unblock_confirm : R.string.user_block_confirm);
        blockDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                blockDialog.dismiss();
                blockUserPresenter.blockUser(0, userInfo.getId(), isUserBlock);
            }
        });
        blockDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                blockDialog.dismiss();
            }
        });
        blockDialog.show();
    }

    public int getPageId() {
        return pageId;
    }


    private CommentPanel getCommentPanel() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof HomeFragment) {
            return ((HomeFragment) parentFragment).getCommentPanel();
        } else {
            if (viewCommentPanel == null) {
                viewCommentPanel = new CommentPanel(getActivity());
                getActivity().getWindow().addContentView(viewCommentPanel, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                viewCommentPanel.setVisibility(View.GONE);
            }
            return viewCommentPanel;
        }
    }


    @Override
    protected void initData() {
        super.initData();
        pageId = CommonUtils.getRandom(1000000, 9999999);
        EventHelper.register(this);
        if (getActivity() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getCommentPanel();
        }
        if (getArguments() != null) {
            videoParam = (MultiTypeVideoBean) getArguments().getSerializable(KEY_VIDEO_LIST);
            videoType = getArguments().getInt(KEY_VIDEO_TYPE, TYPE_RECOMMEND);
            userId = getArguments().getLong(KEY_USER_ID, 0);
            inViewPager = getArguments().getBoolean(KEY_IN_VIEWPAGER, false);
            fromIMChat = getArguments().getBoolean(KEY_FROM_IMCHAT, false);
        }
        handler = new Handler(Looper.getMainLooper());
        linearLayoutManager = new DampLinearLayoutManager(getActivity());
        recyclerView.setFlingScale(0.7f);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnFooterClickListener(new OnFooterClickListener() {
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
        userInfoPresenter = new UserInfoPresenter(this);
        followUserPresenter = new FollowUserPresenter(this);
        blockUserPresenter = new BlockUserPresenter(this);
        userAccountInfoPresenter = new UserAccountInfoPresenter(this);
        videoDetailPresenter = new VideoDetailPresenter(this);
        recyclerView.getRecycleView().addOnScrollListener(onScrollListener);
        addPresenter(userAccountInfoPresenter, videoDetailPresenter, followUserPresenter, userInfoPresenter, blockUserPresenter);
        if (videoType == TYPE_USER_HOME
                || videoType == TYPE_TOPIC
                || videoType == TYPE_MUSIC) {
            presenter.setVideoOffset((videoParam == null || videoParam.getShortVideoItemList() == null) ?
                    0 : videoParam.getShortVideoItemList().size());
            switch (videoType) {
                case TYPE_USER_HOME:

                    break;
                case TYPE_TOPIC:
                    typeId = videoParam.getTopicId();
                    break;
                case TYPE_MUSIC:
                    typeId = videoParam.getMusicId();
                    break;
                default:
                    break;
            }
            recommendAdapter = new RecommendAdapter(videoType, 12,
                    new ArrayList<>(), pageId, recyclerView, linearLayoutManager, this);
            recyclerView.setAdapter(recommendAdapter);
            recyclerView.setOnRefreshListener(this);
            recyclerView.setOnLoadMoreListener(this);
//            recommendAdapter.addDataList(true, null, false);
            if (videoType == TYPE_TOPIC) {
                topicHeader = new TopicHeader(getActivity());
                recyclerView.setHeader(topicHeader);
                topicPresenter = new TopicPresenter(this);
                if (videoParam != null && videoParam.getTopicListBean() != null) {
                    onTopicInfoOk(videoParam.getTopicListBean());
                }
                addPresenter(topicPresenter);
                locateJoin(1, scrollY);
                tvJoin.setOnClickListener(this);
            }
            if (videoType == TYPE_USER_HOME) {
                llExtraProductType.setOnTypeClickListener(this);
                userInfoHeader = new UserInfoHeader(getActivity());
                recyclerView.setHeader(userInfoHeader);
                userInfoHeader.setInfoClickListener(this);
                userInfoHeader.showFollow(userId != AccountUtil.getUserId());
                measureHeight(true);
                rlToolbar.setBackgroundColor(Color.argb(0, 255, 255, 255));
                tvNickname.setAlpha(0);
                llMenus.setAlpha(1);
                ivBigAvatar.setOnClickListener(this);
                ivSetting.setOnClickListener(this);
                ivAdd.setOnClickListener(this);
                ivMore.setOnClickListener(this);
                ivMessage.setOnClickListener(this);
                ivShare.setOnClickListener(this);
                ivBack.setOnClickListener(this);
                llDraftInfo.setOnClickListener(this);
                unblockUser.setOnClickListener(this);
                ivBlockBack.setOnClickListener(this);
                getUserHomeInfo(userId, true);
            }
            recyclerView.autoRefresh(300);
        } else if (videoType == TYPE_SINGLE_WORK) {//单个作品
            typeId = videoParam.getVideoId();
            if (typeId < 1) {
                ToastUtil.showToast(R.string.hintErrorDataDelayTry);
                if (getActivity() != null) {
                    getActivity().finish();
                }
                return;
            }
            recommendAdapter = new RecommendAdapter(videoType, 1, new ArrayList<>(), pageId, recyclerView, linearLayoutManager, this);
            recyclerView.setRefreshEnable(false);
            recyclerView.setLoadMoreEnable(false);
            recyclerView.setAdapter(recommendAdapter);
        } else {//推荐
            recyclerView.setOffset();
            recyclerView.setFooter(R.layout.view_recycler_load_more);
            recommendAdapter = new RecommendAdapter(videoType, 12, new ArrayList<>(), pageId, recyclerView, linearLayoutManager, this);
            recyclerView.setAdapter(recommendAdapter);
            recyclerView.setOnRefreshListener(this);
            recyclerView.setOnLoadMoreListener(this);
            recyclerView.autoRefresh(600);
        }
        rlUserHomeInfo.setVisibility(videoType != TYPE_USER_HOME ? View.GONE : View.VISIBLE);
        recommendAdapter.setOnActiveCallback(this);
        recommendAdapter.setShowImg(true);
        switch (videoType) {
            case TYPE_FOLLOW:
                recyclerView.setCustomEmptyView(R.layout.view_follow_video_rempty);
                recommendAdapter.setEmptyStr(R.string.main_follow_null);
                break;
            case TYPE_USER_HOME:
                recommendAdapter.setEmptyStr(AccountUtil.getUserId() == userId ? R.string.user_no_opus_other : R.string.user_other_no_opus);
                break;
            default:
                recommendAdapter.setEmptyStr(R.string.empty_data_msg);
                break;
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
        viewRecommendUsers.setOnFollowListener(this);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        int currentY = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            KLog.i("======onScrollStateChanged:" + newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                currentY = 0;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollY = recyclerView.computeVerticalScrollOffset();
            currentY += dy;
            if (videoType == TYPE_USER_HOME) {
                locateProductPanel(dy, scrollY);
            } else if (videoType == TYPE_RECOMMEND || videoType == TYPE_FOLLOW) {
                if (recommendScrollListener != null) {
                    recommendScrollListener.onScroll(currentY, scrollY);
                }
            } else if (videoType == TYPE_TOPIC) {
                locateJoin(dy, scrollY);
            }

//            KLog.i("====pageId" + pageId + "==onScrolled dx:" + dx + " ,dy:" + dy + " ,scrollY:" + scrollY + " ,currentY:" + currentY);
            if (currentY > sSlideValue) {
                KLog.i("======onScrolled pausePlay");
                DcIjkPlayerManager.get().pausePlay();
                DcIjkPlayerManager.get().resetUrl();
            }
        }
    };

    private void locateJoin(int dy, int scrollY) {
        if (topicHeader != null) {
            blackHeight = topicHeader.getMeasuredHeight() - topicHeader.getJoinHeight() - DeviceUtils.dip2px(getActivity(), 10);
            if (dy != 0) {
                int tranY;
                if (scrollY > blackHeight) {
                    tranY = 0;
                } else {
                    tranY = (blackHeight - scrollY);
                }
                KLog.i("====locateJoin topicHeader:" + topicHeader.getMeasuredHeight() + " ,JoinHeight:" + topicHeader.getJoinHeight() + " ,blackHeight:" + blackHeight + " ,tranY:" + tranY);
                flJoin.setTranslationY(tranY);
            }
        }
    }

    private int allDy;

    private void locateProductPanel(int dy, int scrollY) {
        if (userInfoHeader != null) {
            blackHeight = userInfoHeader.getHeight()
                    - userInfoHeader.getProductTypeView().getHeight();
        }
        if (dy != 0) {
            allDy += dy;
            int tranY;
            if (scrollY > (blackHeight - toolbarHeight)) {
                tranY = toolbarHeight;
            } else {
                tranY = (blackHeight - scrollY);
            }
            KLog.i("=====locateProductPanel dy:" + dy + " ,scrollY:" + scrollY + " ,recycler offset:" + recyclerView.getRecycleView().computeVerticalScrollOffset() + " ,tranY:" + tranY + " ,allDy:" + allDy);
            viewHolder.setTranslationY(tranY);
        }
        int a = scrollY > (2 * toolbarHeight) ? 255 : (int) (scrollY * 1f / (2 * toolbarHeight) * 255);
        float alpha = Math.abs(scrollY * 1f / (2 * toolbarHeight));
        rlToolbar.setBackgroundColor(Color.argb(a, 255, 255, 255));
        tvNickname.setAlpha(alpha);
        llMenus.setAlpha(1 - alpha);
    }

    public int getScrollY() {
        return scrollY;
    }

    public void getUserHomeInfo(long userId, boolean refreshList) {
        boolean isSelf = (userId == AccountUtil.getUserId()) && AccountUtil.isLogin();
        ivSetting.setVisibility(isSelf ? View.VISIBLE : View.GONE);
        ivAdd.setVisibility(isSelf ? View.VISIBLE : View.GONE);
        ivMessage.setVisibility(isSelf ? View.GONE : View.VISIBLE);
        ivShare.setVisibility(View.VISIBLE);
        ivMore.setVisibility(isSelf ? View.GONE : View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        if (userId != 0) {
            this.userId = userId;
            userInfoPresenter.getPersonalInfo(userId);
            if (refreshList) {
                userInfoHeader.selectItem(currentProductType, true);
            }
        }
    }

    private void measureHeight(boolean locate) {
        if (getActivity() != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (recyclerView == null) {
                        return;
                    }
                    if (videoType == TYPE_USER_HOME && userInfoHeader != null) {
                        blackHeight = userInfoHeader.getHeight()
                                - userInfoHeader.getProductTypeView().getHeight();
                    }
                    if (locate && viewHolder != null) {
                        viewHolder.setTranslationY(blackHeight);
                        viewHolder.setVisibility(videoType == TYPE_USER_HOME ? View.VISIBLE : View.GONE);
                    }
                    if (recyclerView != null && recyclerView.getRecycleView() != null)
                        locateProductPanel(1, recyclerView.getRecycleView().computeVerticalScrollOffset());
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        isFirstLoadData = false;
        isActive = false;
        switch (videoType) {
            case TYPE_RECOMMEND:
                KLog.d("更新列表", "101010101010101010");
                presenter.getRecommendVideoList(true);
                break;
            case TYPE_FOLLOW:
                presenter.getFollowList(true);
                break;
            case TYPE_TOPIC:
                presenter.getTopicList(true, typeId);
                break;
            case TYPE_MUSIC:
                presenter.getMusicList(true, typeId);
                break;
            case TYPE_EXPLOSION:
                presenter.getExplosionList(true);
                break;
            case TYPE_USER_HOME:
                if (currentProductType != ProductTypePanel.TYPE_PRODUCT) {
                    userInfoPresenter.getProductList(true, currentProductType, userId);
                } else {
                    userInfoPresenter.getProductList(true, currentProductType, userId);
                    cheekDraft(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoadMore() {
        switch (videoType) {
            case TYPE_RECOMMEND:
                presenter.getRecommendVideoList(false);
                break;
            case TYPE_TOPIC:
                presenter.getTopicList(false, typeId);
                break;
            case TYPE_MUSIC:
                presenter.getMusicList(false, typeId);
                break;
            case TYPE_EXPLOSION:
                presenter.getExplosionList(false);
                break;
            case TYPE_FOLLOW:
                presenter.getFollowList(false);
                break;
            case TYPE_USER_HOME:
                userInfoPresenter.getProductList(false, currentProductType, userId);
                break;
            default:
                break;
        }
    }

    @Override
    public void onVideoListOk(boolean isRefresh, List<ShortVideoItem> list, List<Banner> bannerList, List<UserInfo> recommendUsers, boolean hasMore) {
        if (!CollectionUtil.isEmpty(list)) {
            shortVideoItems.addAll(list);
        }
        recommendAdapter.setShowEmptyView(true);
        recommendAdapter.addDataList(isRefresh, list, bannerList, hasMore, true);
        KLog.i("=====添加数据完毕");
        if ((videoType == TYPE_TOPIC || videoType == TYPE_EXPLOSION) && isRefresh && !hasScrolled && videoParam != null && !CollectionUtil.isEmpty(list)) {
            //话题页滚动到指定位置
            int target = videoParam.getCurrentPosition() + (recommendAdapter.hasHeader() ? 1 : 0);
            if (target > 1 && target < (list.size() + (recommendAdapter.hasHeader() ? 1 : 0))) {
                recyclerView.scrollToPosition(target);
            }
            hasScrolled = true;
        }
        if (isRefresh) {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    measureHeight(true);
                }
            }, 300);
            scrollY = 0;
            tvNickname.setAlpha(0);
            llMenus.setAlpha(1);
        }

        if (isRefresh && videoType == TYPE_FOLLOW) {
            if (AccountUtil.isLogin()) {
                if (!CollectionUtil.isEmpty(recommendUsers)) {
                    viewRecommendUsers.setData(recommendUsers);
                } else {
                    viewRecommendUsers.setVisibility(View.GONE);
                }
            } else {
                viewRecommendUsers.showRelogin();
            }
        } else {
            viewRecommendUsers.setVisibility(View.GONE);
        }
    }

    /**
     * 获取话题列表页的第一视频的封面
     *
     * @param
     * @return
     */
    public String getFirstThumbUrl() {
        if (!CollectionUtil.isEmpty(shortVideoItems))
            return shortVideoItems.get(0).opus_cover;
        else
            return "";
    }

    @Override
    public void onVideoDetailOk(long videoId, int position, ShortVideoInfoResponse videoInfoBean) {
        dismissLoad();
        recommendAdapter.refreshVideoDetail(videoId, position, videoInfoBean);
    }

    @Override
    public void onVideoError(int position, long videoId, String message) {
        recommendAdapter.onVideoError(position, videoId, message);
        if (CollectionUtil.isEmpty(recommendAdapter.getDataContainer())) {
            showToast(message);
            dismissLoad();
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
    public void onVideoClick(boolean isDetail, int position, View view, View cover, ShortVideoItem videoItem, int showType) {
        if (!isDetail) {
            if (videoItem != null) {
                if (videoItem.itemType == 0) {
                    //是短视频
                    if (videoItem.getId() > 0) {
                        if (!recommendAdapter.isPlayPosition(position)) {
//                        //不是当前Feed流正在播放的视频
                            DcIjkPlayerManager.get().stopPlay();
                            DcIjkPlayerManager.get().resetUrl();
                            DcIjkPlayerManager.get().setVideoUrl(pageId, videoItem.getId(), videoItem.getOpus_path(), null);
//                        return;
                        }
//                        needPause = false;
                        fromDetail = true;
                        currentPlayDataPosition = position;
                        toVideoDetailList(position, view, cover);
//                        VideoDetailActivity.startVideoDetailActivity((BaseCompatActivity) getActivity(), view, cover, videoItem, position);
                    } else {
                        ToastUtil.showToast(R.string.hintErrorDataDelayTry);
                    }
                } else if (videoItem.itemType == 1) {
                    //是图片广告
                    if (videoItem.banner != null) {
                        DcRouter.linkTo(getActivity(), videoItem.banner.link);
                    } else {
                        ToastUtil.showToast(R.string.hintErrorDataDelayTry);
                    }
                } else {
                    //其他类型
                }
            } else {
                ToastUtil.showToast(R.string.hintErrorDataDelayTry);
            }
        }
    }

    private void toVideoDetailList(int position, View view, View cover) {
        if (CollectionUtil.isEmpty(recommendAdapter.getDataContainer())) {
            return;
        }
        MultiTypeVideoBean multiTypeVideoBean = new MultiTypeVideoBean();
        if (videoType == TYPE_TOPIC) {
            multiTypeVideoBean.topicId = typeId;
        } else if (videoType == TYPE_USER_HOME) {
            multiTypeVideoBean.userId = userId;
        } else if (videoType == TYPE_MUSIC) {
            multiTypeVideoBean.musicId = typeId;
        } else if (videoType == TYPE_SINGLE_WORK) {
            multiTypeVideoBean.videoId = typeId;
        }
        multiTypeVideoBean.currentPosition = position - getCurrentAdCount(position);
        multiTypeVideoBean.currentProductType = currentProductType;
        List<ShortVideoItem> list = new ArrayList<>(recommendAdapter.getDataContainer().size());
        for (ShortVideoItem shortVideoItem : recommendAdapter.getDataContainer()) {
            if (shortVideoItem.itemType == 0) {
                //过滤掉广告图片
                list.add(shortVideoItem);
            }
        }
//        multiTypeVideoBean.shortVideoItemList = list;
        TransferDataManager.get().setVideoListData(list);
        multiTypeVideoBean.nextPageOffset = (TYPE_USER_HOME == videoType ? userInfoPresenter.getUserOffset() : presenter.getVideoOffset());
        VideoDetailListActivity.startVideoDetailListActivity((BaseCompatActivity) getActivity()
                , pageId, videoType, multiTypeVideoBean, view, cover);
        return;
    }

    @Override
    public void onContinunousClick(int position, ShortVideoItem videoItem, float rawX, float rawY) {

    }

    @Override
    public void onLikeClick(int position, long videoId,
                            boolean isLike,
                            boolean showFlyAnim, boolean doRequest,
                            float rawDownX, float rawDownY,
                            float targetRawX, float targetRawY) {
        if (AccountUtil.isLogin()) {
            if (!showFlyAnim) {
                if (videoId > 0) {
                    presenter.likeVideo(position, videoId, isLike, false);
                }
            } else {
//                群魔乱舞的心
//                CommonUtils.showFlyHeart(rlFlyHeart, rawDownX, rawDownY, targetRawX, targetRawY);
                if (doRequest) {
                    presenter.likeVideo(position, videoId, isLike, true);
                }
            }
        } else {
            showReLogin();
        }
    }

    @Override
    public void onCommentClick(int position, long videoId, ShortVideoItem shortVideoItem) {
        getCommentPanel().show(pageId, videoId, position, shortVideoItem);
    }

    @Override
    public void onUserClick(long userId) {
        UserHomeActivity.startUserHomeActivity(getActivity(), userId);
    }

    @Override
    public void onLikeOk(long videoId, int position, ShortVideoLoveResponse bean) {
        recommendAdapter.refreshLike(videoId, bean);
    }

    @Override
    public void onLikeFail(long videoId, int position, boolean isFlyLike) {
        showToast("操作失败，请稍后再试");
    }

    @Override
    public void onReportOk() {
        showToast("举报成功");
    }

    @Override
    public void onDeleteVideoOk(final int position, long videoId) {
        if (videoId > 0) {
            if (videoType == RecommendFragment.TYPE_SINGLE_WORK) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                for (ShortVideoItem shortVideoItem : recommendAdapter.getDataContainer()) {
                    if (shortVideoItem != null) {
                        if (videoId == shortVideoItem.getId()) {
                            KLog.i("====列表中存在刚刚删除的视频：" + videoId);
                            DcIjkPlayerManager.get().pausePlay();
                            DcIjkPlayerManager.get().resetUrl();
                            if (videoType == TYPE_USER_HOME) {
                                onTypeClick(currentProductType);
                                userInfoPresenter.getPersonalInfo(userId);
                            } else {
                                //刷新当前列表
//                                shortVideoItems.remove(shortVideoItem);
//                                recommendAdapter.getDataContainer().remove(shortVideoItem);
//                                recommendAdapter.notifyDataSetChanged();
                                //刷新列表
                                onRefresh();
                                fromDetail = false;
                            }
                            break;
                        }
                    }
                }
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
            if (viewRecommendUsers != null) {
                viewRecommendUsers.setVisibility(View.GONE);
            }
            recommendAdapter.setShowEmptyView(true);
            recommendAdapter.setShowImg(true);
            recommendAdapter.showError((requestCode == HttpConstant.TYPE_RECOMMEND_VIDEO_LIST)
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
    public void getVideoDetail(int position, long id, boolean needBarrage) {
        if (id > 0) {
            currentVideoPosition = position;
            currentVideoId = id;
            videoDetailPresenter.getVideoDetail(position, id, needBarrage ? 1 : 0);
        }
    }

    @Override
    public void getNextPageList(int position) {
        if (position > 0 && isVisible) {
            KLog.i("=====自动拉取下一页的数据");
            onLoadMore();
        }
    }

    @Override
    public void onFollowClick(final int position, final long videoId, final long userId, final boolean isFollowed) {
        if (AccountUtil.isLogin()) {
            if (userId > 0) {
                if (isFollowed) {

                } else {
                    followUserPresenter.follow(false, position, userId, videoId, isFollowed, -1);
                }
            } else {
                showToast(R.string.hintErrorDataDelayTry);
            }
        } else {
            showReLogin();
        }
    }

    @Override
    public void onFollowClick(boolean isRecommend, int position, long videoId, long userId, boolean isFollow) {
        if (isRecommend) {
            followUserPresenter.recommendFollow(position, userId, isFollow);
        }
    }

    @Override
    public void onFollowAllClick(int position, String ids) {
        followUserPresenter.followAll(ids);
    }

    @Override
    public void onLoginClick() {
        showReLogin();
    }

    @Override
    public void onShareClick(int position, ShortVideoItem shortVideoItem) {

    }

    @Override
    public void onGiftClick(int position, ShortVideoItem shortVideoItem) {

    }

    @Override
    public void onJoinReplaceClick(int index, ShortVideoItem shortVideoItem) {

    }

    @Override
    public void onJoinSingleClick(int index, ShortVideoItem shortVideoItem) {

    }

    @Override
    public void onJoinCurrentTemplateClick(int index, ShortVideoItem shortVideoItem) {

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
    public boolean allowPlay() {
        boolean disallow = getActivity() == null || !getVisible() || getActivity().isFinishing() || getActivity().isDestroyed();
        KLog.i("======当前页面条件" + (disallow ? "不" : "") + "允许播放");
        return !disallow;
    }

    public boolean fromDetail = false;
    private int currentPlayDataPosition;

    private void cheekDraft(boolean needData) {
        if (userId == AccountUtil.getUserId()
                && AccountUtil.isLogin()) {
            Observable.just(1)
                    .subscribeOn(Schedulers.computation())
                    .map(new Function<Integer, List<ProductEntity>>() {
                        @Override
                        public List<ProductEntity> apply(@NonNull Integer integer) throws Exception {
                            return RecordUtil.queryAllDraft();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<ProductEntity>>() {
                        @Override
                        public void accept(@NonNull List<ProductEntity> productEntities) throws Exception {
                            if (getActivity() != null) {
                                draftSize = productEntities == null ? 0 : productEntities.size();
                                refreshDraft(draftSize);
                                if (needData) {
                                    userInfoPresenter.getProductList(true, currentProductType, userId);
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            if (getActivity() != null) {
                                draftSize = 0;
                                refreshDraft(draftSize);
                                if (needData) {
                                    userInfoPresenter.getProductList(true, currentProductType, userId);
                                }
                            }
                        }
                    });
        } else {
            draftSize = 0;
            refreshDraft(draftSize);
            if (needData) {
                userInfoPresenter.getProductList(true, currentProductType, userId);
            }
        }
    }

    private void refreshDraft(int size) {
        if (llDraftInfo == null)
            return;
        llDraftInfo.setVisibility((userId == AccountUtil.getUserId()
                && AccountUtil.isLogin()
//                && !showBack
                && currentProductType == ProductTypePanel.TYPE_PRODUCT
                && size > 0) ? View.VISIBLE : View.GONE);
        tvDraftCount.setText(String.format(getResString(R.string.stringDraftCount), size));
    }

    //手动刷新
    public void manualRefresh() {
        if (!isFirstLoadData && recommendAdapter != null && presenter != null) {
            if (recyclerView != null && !recyclerView.isRefreshing()) {
                recyclerView.autoRefresh();
            }
        }
    }

    /**
     * 显示邀请码对话框
     */
    public void showVerifyDialog(Block block) {
        if (verifyDialog == null) {
            verifyDialog = new VerifyDialog(getActivity());
            verifyDialog.setOnVerifyListener(new VerifyDialog.OnVerifyListener() {
                @Override
                public void onVerifySuccess() {
                    block.run();
                }
            });
        }
        if (!verifyDialog.isShowing()) {
            verifyDialog.show();
        }
    }

    private MyClickListener userHomeShareClick = new MyClickListener() {
        @Override
        protected void onMyClick(View v) {
            shareHome.objId = userId;
            shareHome.shareType = ShareEventEntity.TYPE_USER_HOME;
            switch (v.getId()) {
                case R.id.llWeChat:
                    // String path = ScreenShot.shootView(userInfoHeader.userInfoTopll);
                    Bitmap bitmap = ScreenShot.getViewBp(userInfoHeader.userInfoTopll);
                    Bitmap b = ScreenShot.scaleBitmap(bitmap, 0.4f);
                    shareHome.shareTarget = ShareEventEntity.TARGET_WECHAT;
                    if (shareHome.wxprogram_share_info != null) {
                        wxMinAppShare(0, shareHome, b);
                    } else {
                        wechatShare(0, shareHome);
                    }
                    break;
                case R.id.llCircle:
                    shareHome.shareTarget = ShareEventEntity.TARGET_FRIEND;
                    wechatShare(1, shareHome);
                    break;
                case R.id.llWeibo:
                    shareHome.shareTarget = ShareEventEntity.TARGET_WEIBO;
                    weiboShare(shareHome);
                    break;
                case R.id.llQQ:
                    shareHome.shareTarget = ShareEventEntity.TARGET_QQ;
                    qqShare(shareHome);
                    break;
                case R.id.llCopy:
                    shareHome.shareTarget = ShareEventEntity.TARGET_WEB;
                    ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setPrimaryClip(ClipData.newPlainText(null, shareHome.web_link));
                    showToast(R.string.copy_succeed);
                    ShareEventEntity.share(shareHome);
                    break;
                default:
                    break;
            }
            if (null != shareWindow && shareWindow.isShowing()) {
                shareWindow.dismiss();
            }
        }
    };

    @Override
    public void onAvatarClick(long userId, boolean editable) {
        if (userId == AccountUtil.getUserId()) {
            if (editable) {
                if (userInfo != null) {
                    PersonalInfoActivity.startPersonalInfoActivity(getActivity(), userInfo);
                } else {
                    showToast(R.string.hintErrorDataDelayTry);
                }
            } else {
//                ivBigAvatar.setVisibility(View.VISIBLE);
                if (getActivity() != null && userInfo != null) {
                    avatarDialog = new AvatarDialog(getActivity(), !userInfo.isFemale(), userInfo.getCover_ori());
                    avatarDialog.show();
                }
            }
        } else {
            //显示大头像
//            ivBigAvatar.setVisibility(View.VISIBLE);
            if (getActivity() != null && userInfo != null) {
                avatarDialog = new AvatarDialog(getActivity(), !userInfo.isFemale(), userInfo.getCover_ori());
                avatarDialog.show();
            }
        }
    }

    @Override
    public void onNameClick(long userId) {
        if (userId == AccountUtil.getUserId()) {
            if (userInfo != null) {
                PersonalInfoActivity.startPersonalInfoActivity(getActivity(), userInfo);
            } else {
                showToast(R.string.hintErrorDataDelayTry);
            }
        }
    }

    @Override
    public void onTypeClick(int index) {
        currentProductType = index;
        int stringId = R.string.empty_data_msg;
        switch (currentProductType) {
            case ProductTypePanel.TYPE_PRODUCT:
                stringId = AccountUtil.getUserId() == userId ? R.string.user_no_opus_other : R.string.user_other_no_opus;
                break;
            case ProductTypePanel.TYPE_TOGETHER:
                stringId = AccountUtil.getUserId() == userId ? R.string.user_together_opus : R.string.user_other_together_opus;
                break;
            case ProductTypePanel.TYPE_LIKE:
                stringId = AccountUtil.getUserId() == userId ? R.string.user_no_like_opus : R.string.user_other_no_like_opus;
                break;
            default:
                break;
        }
        refreshDraft(draftSize);
        recommendAdapter.setEmptyStr(stringId);
        llExtraProductType.selectItem(index);
        if (userInfoHeader != null) {
            userInfoHeader.selectItem(index);
        }
        if (userId != 0) {
            recyclerView.autoRefresh();
        }
        viewHolder.setTranslationY(blackHeight);
        recommendAdapter.setShowEmptyView(false);
        recommendAdapter.addDataList(true, null, true);
    }

    @Override
    public void onFollowClick(long userId, boolean isFollow) {
        if (!AccountUtil.isLogin()) {
            showReLogin();
            return;
        }
        if (userId != AccountUtil.getUserId()) {
            if (isFollow) {
                customDialog = new CustomDialog(getActivity(), R.style.BaseDialogTheme);
                customDialog.setContent(getString(R.string.dialog_focus_tip));
                customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customDialog.dismiss();
                        followUserPresenter.follow(0, userId, isFollow);
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
                followUserPresenter.follow(0, userId, isFollow);
            }
        }
    }

    @Override
    public void onFollowCountClick(long userId) {
        if (userInfo != null) {
            FocusActivity.startFocusActivity(getActivity(), userInfo);
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onFansCountClick(long userId) {
        if (userInfo != null) {
            FansActivity.startFansActivity(getActivity(), userInfo);
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onLikeCountClick(long userId) {

    }

    @Override
    public void onDecibelCountClick(long userId) {
        if (userInfo != null) {
            DecibelListActivity.startDecibelListActivity(getActivity(), userId);
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onWeiboHomeClick(String weiboId) {
        weiboJump(weiboId);
    }

    @Override
    public void onOfficalWebSite(String link) {
        WebViewActivity.startWebActivity(getActivity(), link, "");
    }

    @Override
    public void onAccountClick(long userId) {
        UserAccountEarningsActivity.startUserAccountActivity(getActivity(), userId);
    }

    @Override
    public void onMessageClick(long userId, UserInfo userInfo) {
        if (AccountUtil.isLogin()) {
            if (fromIMChat) {
                getActivity().onBackPressed();
            } else {
                IMMessageActivity.startIMMessageActivity((BaseCompatActivity) getActivity(), userId, userInfo);
            }
        } else {
            showReLogin();
        }
    }

    @Override
    public void onGetUserInfoOk(UserInfo userInfo) {
        userInfoHeader.initData(userInfo);
        userInfoHeader.showFollow(userId != AccountUtil.getUserId());
        this.userInfo = userInfo;
        if (userInfo != null && userInfo.getData() != null) {
            tvNickname.setText(userInfo.getName());
            llExtraProductType.initData(userInfo.getData().getOpus_count(),
                    userInfo.getData().getCo_create_count(),
                    userInfo.getData().getLike_opus_count());
//            GlideLoader.loadImage(userInfo.getCover_url(), ivBigAvatar, userInfo.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
            shareInfo = userInfo.getShare_info();
            shareHome = userInfo.getShare_home();
            // 拉黑
            if (videoType == TYPE_USER_HOME && userInfo.getRelation() != null) {
                if (userInfo.getRelation().is_block) {
                    blockLayerMask.setVisibility(View.VISIBLE);
                    // Stop video
                    DcIjkPlayerManager.get().pausePlay();
                    userInfo.getRelation().is_follow = false;
                    userInfoHeader.setFollowed(false);
                } else {
                    blockLayerMask.setVisibility(View.GONE);
                }
            }
        } else {
            tvNickname.setText("");
            llExtraProductType.initData(0, 0, 0);
            ivBigAvatar.setImageResource(R.drawable.ic_default_male);
        }
        measureHeight(false);
        if (AccountUtil.isLogin() && userInfo != null && !AccountUtil.isLoginUser(userInfo.getId())) {
            //刷新消息数据库中的个人信息
            EventHelper.post(GlobalParams.EventType.TYPE_UPDATE_USER_INFO, userInfo);
        }
    }

    @Override
    public void onGetUserInfoFail(String message) {
        showToast(message);
    }

    @Override
    public void onGetProductFail(boolean isRefresh, String message) {
        recommendAdapter.showError(isRefresh);
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
    public void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll, int position, long userId, long videoId, boolean isFollowed) {
        if (isFollowAll) {
            if (videoType == TYPE_FOLLOW) {
                onRefresh();
            }
        } else if (isRecommendFollow) {
            showToast(isFollowed ? R.string.user_follower : R.string.user_unfollower);
            viewRecommendUsers.refreshItem(position, isFollowed);
        } else {
            if (videoId < 0) {
                if (userInfo != null) {
                    if (userInfo.getRelation() != null) {
                        userInfo.getRelation().is_follow = isFollowed;
                    } else {
                        UserHomeRelation userHomeRelation = new UserHomeRelation();
                        userHomeRelation.is_follow = isFollowed;
                        userInfo.setRelation(userHomeRelation);
                    }
                }
                if (isFollowed) {
                    showToast(R.string.stringFollowed);
                }
                userInfoHeader.setFollowed(isFollowed);
            } else {
                showToast(isFollowed ? R.string.user_follower : R.string.user_unfollower);
                recommendAdapter.refreshFollow(isFollowed, userId);
            }
        }

    }

    @Override
    public void onTopicInfoOk(TopicInfoBean bean) {
        topicInfoBean = bean;
        if (bean != null) {
            if (topicHeader != null) {
                topicShareInfoBean = bean.getShare_info();
                if (ivShare != null) {
                    ivShare.setVisibility(null != topicShareInfoBean ? View.VISIBLE : View.GONE);
                }
                topicHeader.setData(bean).setClickListener(this);
            }

            if (getActivity() instanceof VideoListActivity) {
                ((VideoListActivity) getActivity()).refreshTitle(bean);
            }
        }
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bean != null) {
                    if (getActivity() != null) {
                        flJoin.setVisibility(View.VISIBLE);
                        locateJoin(1, recyclerView.getRecycleView().computeVerticalScrollOffset());
                    }
                } else {
                    flJoin.setVisibility(View.GONE);
                }
            }
        }, 200);
    }


    @Override
    public void onTopicListOk(boolean isRefresh, List<ShortVideoItem> list, boolean hasMore) {
        //不需要
    }

    @Override
    public void onMusicInfoOk(MusicInfoBean bean) {
        //不需要
    }

    @Override
    public void onMusicListOk(boolean isRefresh, List<ShortVideoItem> list, boolean hasMore) {
        //不需要
    }

    @Override
    public void onAddFavoriteResult(int position, boolean ok) {

    }

    @Override
    public void onJoinTopicClick(TopicInfoBean topicInfoBean) {
        if (topicInfoBean != null && typeId > 0) {
            if (!TextUtils.isEmpty(AccountUtil.getToken())) {
                TopicInfoEntity topicInfo = new TopicInfoEntity();
                topicInfo.topicTitle = topicInfoBean.getName();
                topicInfo.topicDesc = topicInfoBean.getDescription();
                topicInfo.topicId = Long.valueOf(topicInfoBean.getId());
                if (AccountUtil.needVerifyCode()) {
                    showVerifyDialog(new Block() {
                        @Override
                        public void run() {
                            SelectFrameActivity2.startSelectFrameActivity2((BaseCompatActivity) getActivity(), topicInfo, SelectFrameActivity.VIDEO_TYPE_RECORD);
                        }
                    });
                } else {
                    SelectFrameActivity2.startSelectFrameActivity2((BaseCompatActivity) getActivity(), topicInfo, SelectFrameActivity.VIDEO_TYPE_RECORD);
                }
            } else {
                showReLogin();
            }
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onAvatarClick(TopicInfoBean topicInfoBean) {

    }

    @Override
    public void onGetBlockUserOk(int position, long userId, boolean isBlock) {
        // 拉黑
        if (userInfo != null && userInfo.getRelation() != null) {
            userInfo.getRelation().is_block = isBlock;
            if (userInfo.getRelation().is_block) {
                blockLayerMask.setVisibility(View.VISIBLE);
                // Stop video
                DcIjkPlayerManager.get().pausePlay();
                userInfo.getRelation().is_follow = false;
                userInfoHeader.setFollowed(false);
            } else {
                blockLayerMask.setVisibility(View.GONE);
            }
        }
    }


    private static class VideoReceiver extends ResultReceiver {
        private WeakReference<RecommendFragment> fragment;

        @SuppressLint("RestrictedApi")
        public VideoReceiver(RecommendFragment fragment, Handler handler) {
            super(handler);
            this.fragment = new WeakReference<>(fragment);
        }

        @SuppressLint("RestrictedApi")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            String savePath = null;
            if (null != resultData) {
                savePath = resultData.getString("savePath");
            }
            if (fragment != null && fragment.get() != null) {
                switch (resultCode) {
                    case Download.RESULT_PREPARE:
                        fragment.get().downloadProgressDialog.showDownload(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Download.pause(fragment.get().getActivity());
                            }
                        });
                        fragment.get().downloadProgressDialog.updateProgress(0);
                        break;
                    case Download.RESULT_START:
                        break;
                    case Download.RESULT_PAUSE:
                        fragment.get().downloadProgressDialog.dissmissDownload();
                        break;
                    case Download.RESULT_DOWNLOADING:
                        if (null != resultData) {
                            fragment.get().downloadProgressDialog.updateProgress(resultData.getInt("percent"));
                        }
                        break;
                    case Download.RESULT_ERROR:
                        KLog.i("=====download error:" + (resultData != null ? resultData.getString("message") : "null"));
                        fragment.get().showToast("保存失败");
                        fragment.get().downloadProgressDialog.dissmissDownload();
                        break;
                    case Download.RESULT_COMPLETE:
                        fragment.get().showToast("保存成功");
                        fragment.get().downloadProgressDialog.dissmissDownload();
                        if (!TextUtils.isEmpty(savePath)) {
//                            EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(fragment.get().currentVideoId, 0, 0, 1));
                            DiscoveryUtil.updateMedia(fragment.get().getActivity(), savePath);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

    }

    //显示举报弹窗
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
            listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_report_works, stringList.toArray(new String[stringList.size()])));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    presenter.reportWorks(currentVideoPosition, currentVideoId, reportTypes.get(position).getId());
                    popupWindow.dismiss();
                }
            });
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void showFullScreen(boolean toFull, int showType) {

    }

    @Override
    public void beforePlay() {
        if (recommendAdapter != null) {
            currentPlayDataPosition = recommendAdapter.getCurrentDataPosition();
            if (!getVisible() || !recommendAdapter.hasContent()) {
                KLog.i(getPageType() + "====beforePlay=页面不可见，暂停播放");
                pausePlay();
            }
        }
    }

    private String getPageType() {
        switch (videoType) {
            case TYPE_FOLLOW:
                return "关注 pageId:" + pageId;
            case TYPE_RECOMMEND:
                return "推荐 pageId:" + pageId;
            case TYPE_USER_HOME:
                return "个人中心 pageId:" + pageId;
            default:
                break;
        }
        return "其他pageId:" + pageId;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (fromDetail) {
            resumePlay(currentPlayDataPosition, fromDetail);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onVisibleChange(int type, boolean visible) {
        KLog.i(getPageType() + "=====onVisibleChange visible:" + visible + " old visible:" + getVisible());
        boolean beforeVisible = getVisible();
        super.onVisibleChange(type, visible);
        if (isLoadFinish) {
            if (visible) {
            } else {
                if (avatarDialog != null && avatarDialog.isShowing()) {
                    avatarDialog.dismiss();
                }
            }
        }
        if (recommendAdapter != null) {
            recommendAdapter.setFragmentVisible(visible);
        }
        if (visible && videoType == TYPE_FOLLOW) {
            if (!AccountUtil.isLogin()) {
                viewRecommendUsers.showRelogin();
//                loginDialog = showReLogin();
//                if (loginDialog != null) {
//                    loginDialog.setCloseListener(new LoginDialog.CloseListener() {
//                        @Override
//                        public void onClose() {
//                            Fragment parentFragment = getParentFragment();
//                            if (parentFragment != null && parentFragment instanceof HomeFragment) {
//                                ((HomeFragment) parentFragment).setCurrentItem(1);
//                            }
//                        }
//                    });
//                }
                return;
            } else {
//                if (loginDialog != null && loginDialog.isShowing()) {
//                    loginDialog.dismiss();
//                }
            }
        }

        if (isLoadFinish) {
            if (videoType == TYPE_USER_HOME) {
                if (visible) {
                    if (AccountUtil.getUserId() == userId && AccountUtil.isLogin()) {
                        cheekDraft(false);
                        getUserHomeInfo(userId, false);
                        resumePlay(currentPlayDataPosition, fromDetail);
                        super.onVisibleChange(type, visible);
                        return;
                    } else {
                        getUserHomeInfo(userId, false);
                    }
                } else {
                    if (avatarDialog != null && avatarDialog.isShowing()) {
                        avatarDialog.dismiss();
                    }
                }
            }

            if (visible) {
                resumePlay(currentPlayDataPosition, fromDetail);
//                showHintAnim();
            } else {
                stopScroll();
                KLog.i(getPageType() + "=====当前页面不可见，强制停止滚动");
//                if (beforeVisible) {
//                    KLog.i(getPageType() + "=====当前页面不可见，之前的状态是可见，暂停视频播放");
//                    pausePlay();
//                }
            }
        }
        if (!visible && beforeVisible) {
            KLog.i(getPageType() + "=====当前页面不可见，之前的状态是可见，暂停视频播放");
            pausePlay();
        }
        if (visible) {
            fromDetail = false;
            DcIjkPlayerManager.get().setNeedPause(true);
        }
    }

    private void stopScroll() {
        if (recyclerView != null && recyclerView.getRecycleView() != null) {
            recyclerView.getRecycleView().stopScroll();
        }
    }

    public void resumePlay() {
        resumePlay(currentPlayDataPosition, fromDetail);
    }

    //恢复播放
    public void resumePlay(int currentPlayDataPosition, boolean fromDetail) {
        if (isLoadFinish && null != recommendAdapter) {
            if (fromDetail) {
                recyclerView.scrollToPosition(currentPlayDataPosition + (recommendAdapter.hasHeader() ? 1 : 0));
            }
            if (shortVideoItems != null
                    && shortVideoItems.size() > 0
                    && DcIjkPlayerManager.get().getPlayer() != null
                    && DcIjkPlayerManager.get().getPlayer().currentId != shortVideoItems.get(currentPlayDataPosition).getId()) {
                fromDetail = false;
            }

//            if (GlobalParams.StaticVariable.sCurrentNetwork == 1 && !GlobalParams.StaticVariable.sAllowdMobile) {//处于移动网络且不允许播放
//                return;
//            }
            boolean finalFromDetail = fromDetail;
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (recyclerView != null) {
                        if (videoType == TYPE_USER_HOME) {
                            locateProductPanel(1, recyclerView.getRecycleView().computeVerticalScrollOffset());
                        }
                        recommendAdapter.resumePlay(currentPlayDataPosition, finalFromDetail);
                    }
                }
            }, 140);

            if (GlobalParams.StaticVariable.sCurrentNetwork == 0) {
                if (useMobileDialog != null) {
                    useMobileDialog.dismiss();
                }
            }
        }
    }

    public void setRecommendScrollListener(RecommendScrollListener listener) {
        recommendScrollListener = listener;
    }

    public interface RecommendScrollListener {
        void onScroll(int currentScrollY, int allScrollY);
    }

    public void pausePlay() {
        if (isLoadFinish && null != recommendAdapter) {
            KLog.i(getPageType() + "==== ,页面暂停播放");
            recommendAdapter.pausePlay();
        }
    }

    @Override
    public void onDestroy() {
        EventHelper.unregister(this);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }

    public boolean onBackPressed() {
        DcIjkPlayerManager.get().setNeedPause(true);
        DcIjkPlayerManager.get().resetUrl();
        if (getCommentPanel() != null && getCommentPanel().getVisibility() == View.VISIBLE) {
            getCommentPanel().dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onActive() {
        if (!isActive && null != recommendAdapter && videoType != RecommendFragment.TYPE_SINGLE_WORK) {
            KLog.i("====激活播放");
            DcIjkPlayerManager.get().setPlayerAlpha(0f);
            getWeakHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (recommendAdapter == null) {
                        return;
                    }
                    recommendAdapter.forcePlay();
                    GiftManager.get().pullGiftList();
                }
            }, 160);
            isActive = true;
        }
    }

    @Override
    public void handleInfoSucceed(UserAccountResponse response) {
        if (response != null) {
            if (recommendAdapter != null) {
                recommendAdapter.refreshGold(response.getUser_gold_account());
            }
        }
    }

    @Override
    public void handleInfoFailure(String error_msg) {

    }

    //评论成功和删除评论刷新数量
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentOkEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_REFRESH_COMMENT) {
            RefreshCommentBean commentBean = (RefreshCommentBean) eventEntity.data;
            if (null != commentBean) {
                recommendAdapter.refreshCommentCount(commentBean);
            }
        }
    }

    //视频删除
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_VIDEO_DELETE) {
            long videoId = (long) eventEntity.data;
            KLog.i("delete---id." + videoId);
            onDeleteVideoOk(-1, videoId);
        }
    }

    //弹幕开关
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchDanmaEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_SWITCH_DANMA) {
            boolean openDanma = (boolean) eventEntity.data;
            if (recommendAdapter != null) {
                recommendAdapter.switchDanma(openDanma);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPublishOkEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_PUBLISH_PRODUCT_OK) {
            KLog.i("=====作品发布成功，草稿箱更新数据");
            if (videoType == TYPE_USER_HOME) {
                userInfoPresenter.getPersonalInfo(userId);
                cheekDraft(true);
            }
        }
    }

    //登录事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocialLoginOk(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_LOGIN_OK) {
            KLog.d("更新列表", "videoType==" + videoType);
            if (videoType == TYPE_USER_HOME) {
                getUserHomeInfo(userId, false);
            }
            if (videoType == TYPE_FOLLOW || AccountUtil.isLoginUser(userId)) {
                onRefresh();
            }
            if (videoType == TYPE_RECOMMEND) {
                onRefresh();
            }
            if (loginDialog != null && loginDialog.isShowing()) {
                loginDialog.dismiss();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenInvalid(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == 30001) {
            if (videoType == TYPE_FOLLOW) {
                recommendAdapter.addDataList(true, null, null, false, isVisible);
            }
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
                recommendAdapter.refreshLike(e.opus_id, e);
            }
        }
    }

    /**
     * 关注用户事件
     *
     * @param eventEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_FOLLOW_OK) {
            if (eventEntity.data != null && eventEntity.data instanceof FollowUserResponseEntity) {
                FollowUserResponseEntity followUserEntity = (FollowUserResponseEntity) eventEntity.data;
                if (viewRecommendUsers != null && viewRecommendUsers.getVisibility() == View.VISIBLE) {
                    viewRecommendUsers.refreshItem(followUserEntity.is_follow, followUserEntity.userId);
                }

                if (userInfo != null) {
                    boolean isCurrentUser = followUserEntity.userId == userInfo.getId();
                    if (userInfoHeader != null && isCurrentUser) {
                        // 是当前用户，刷新当前用户的关注状态
                        userInfoHeader.setFollowed(followUserEntity.is_follow);
                    }
                }
                recommendAdapter.refreshFollow(followUserEntity.is_follow, followUserEntity.userId);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshTitleEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_MODIFY_PERMISSION_OK) {
            if (eventEntity.data != null && eventEntity.data instanceof VideoModifyOpusResponse) {
                VideoModifyOpusResponse videoModifyOpusResponse = (VideoModifyOpusResponse) eventEntity.data;
                recommendAdapter.refreshPermissionsAndTitle(videoModifyOpusResponse.getOpus_id(), videoModifyOpusResponse);
            }
        }
    }

    //同步视频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoListEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_VIDEO_LIST) {
            if (eventEntity.data instanceof VideoListEventEntity) {
                VideoListEventEntity entity = (VideoListEventEntity) eventEntity.data;
                KLog.i("=======同步视频列表");
                if (entity.fromPageId == pageId) {
                    onVideoListOk(entity.isRefresh, TransferDataManager.get().getVideoListData(), TransferDataManager.get().getBannerListData(), null, entity.hasMore);
                    if (TYPE_USER_HOME == videoType) {
                        userInfoPresenter.setOffset(entity.nextPageOffset);
                    } else {
                        presenter.setVideoOffset(entity.nextPageOffset);
                    }
                }
            }
        }
    }

    //同步列表滚动
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoListSynScrollEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_SYN_VIDEO_LIST) {
            if (eventEntity.data instanceof VideoListScrollSynEventEntity) {
                VideoListScrollSynEventEntity entity = (VideoListScrollSynEventEntity) eventEntity.data;
                if (entity.fromPageId == pageId) {
                    int adCount = getCurrentAdCount(entity.scrollToPosition);
                    currentPlayDataPosition = entity.scrollToPosition + adCount;
                    KLog.i("=======同步滑动到位置：" + entity.scrollToPosition + " ,adCount:" + adCount);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetChangeEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_NETWORK_CHANGE) {
            if (eventEntity.data instanceof Integer) {
                if (getVisible() && (int) eventEntity.data == 1 && !GlobalParams.StaticVariable.sHasShowedRemind) {
                    GlobalParams.StaticVariable.sHasShowedRemind = true;
                    new RemindDialog(getActivity()).show();
                }
            }
        }
    }

    private int getCurrentAdCount(int endPosition) {
        ShortVideoItem shortVideoItem;
        int adCount = 0;
        for (int i = 0, size = recommendAdapter.getDataContainer().size(); i < size; i++) {
            if (i <= endPosition) {
                shortVideoItem = recommendAdapter.getDataContainer().get(i);
                if (shortVideoItem.itemType == 1) {
                    adCount++;
                }
            } else {
                break;
            }
        }
        return adCount;
    }

}
