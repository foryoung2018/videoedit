package com.wmlive.hhvideo.heihei.personal.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.beans.personal.ReportType;
import com.wmlive.hhvideo.heihei.beans.personal.ReportTypeResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoListActivity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.mainhome.presenter.FollowUserPresenter;
import com.wmlive.hhvideo.heihei.mainhome.util.ScreenShot;
import com.wmlive.hhvideo.heihei.personal.activity.AddFriendActivity;
import com.wmlive.hhvideo.heihei.personal.activity.DraftBoxActivity;
import com.wmlive.hhvideo.heihei.personal.activity.FansActivity;
import com.wmlive.hhvideo.heihei.personal.activity.FocusActivity;
import com.wmlive.hhvideo.heihei.personal.activity.PersonalInfoActivity;
import com.wmlive.hhvideo.heihei.personal.activity.SettingActivity;
import com.wmlive.hhvideo.heihei.personal.adapter.PersonalProductAdapter;
import com.wmlive.hhvideo.heihei.personal.presenter.UserInfoPresenter;
import com.wmlive.hhvideo.heihei.personal.util.SpaceItemDecoration;
import com.wmlive.hhvideo.heihei.personal.widget.ProductTypePanel;
import com.wmlive.hhvideo.heihei.personal.widget.UserInfoHeader;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.dialog.PopActionSheetNoTitle;
import com.wmlive.hhvideo.widget.dialog.PopReportContentActionSheet;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

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
 * Created by lsq on 10/12/2017.
 * 暂时没用
 */
@Deprecated
public class UserHomeFragment extends DcBaseFragment<UserInfoPresenter> implements
        RefreshRecyclerView.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        UserInfoPresenter.IUserInfoView,
        UserInfoHeader.OnUserInfoClickListener,
        ProductTypePanel.OnTypeClickListener,
        FollowUserPresenter.IFollowUserView,
        OnRecyclerItemClickListener<ShortVideoItem> {

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_SHOW_BACK = "show_back";
    private static final String KEY_IN_VIEWPAGER = "in_viewpager";

    public static int SPACE_ITEM_DECRRATION = 10;//视频间距

    @BindView(R.id.llExtraProductType)
    ProductTypePanel llExtraProductType;
    @BindView(R.id.rvRecyclerView)
    RefreshRecyclerView recyclerView;
    @BindView(R.id.ivBigAvatar)
    ImageView ivBigAvatar;
    @BindView(R.id.rlToolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.tvNickname)
    TextView tvNickname;
    @BindView(R.id.tvEmptyHint)
    TextView tvEmptyHint;
    @BindView(R.id.ivEmptyHint)
    ImageView ivEmptyHint;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivShare)
    ImageView ivShare;
    @BindView(R.id.ivAdd)
    ImageView ivAdd;
    @BindView(R.id.ivSetting)
    ImageView ivSetting;
    @BindView(R.id.ivMore)
    ImageView ivMore;

    private FollowUserPresenter followUserPresenter;
    public int currentProductType = ProductTypePanel.TYPE_PRODUCT;
    private long userId = 0;
    private boolean showBack = true;
    private boolean inViewPager = false;
    private UserInfoHeader userInfoHeader;
    private PersonalProductAdapter personalProductAdapter;
    private UserInfo userInfo;
    private CustomDialog customDialog;
    private TranslateAnimation translateAnimation;
    private int scrollHeight = 0;
    private int scrollY = 0;
    private ProductEntity draftEntity = null;
    private ShareInfo shareInfo;
    private ShareInfo shareHome;
    private PopupWindow shareWindow;
    private List<ReportType> reportTypeList;
    private PopActionSheetNoTitle popReportActionSheet;
    private PopReportContentActionSheet popReportContentActionSheet;

    public static UserHomeFragment newInstance(long userId) {
        return newInstance(userId, true, false);
    }

    public static UserHomeFragment newInstance(boolean inViewPager, long userId) {
        return newInstance(userId, true, inViewPager);
    }

    public static UserHomeFragment newInstance(long userId, boolean showBack, boolean inViewPager) {
        UserHomeFragment fragment = new UserHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        bundle.putBoolean(LAZY_MODE, true);
        bundle.putBoolean(SINGLE_MODE, true);
        bundle.putBoolean(KEY_SHOW_BACK, showBack);
        bundle.putBoolean(KEY_IN_VIEWPAGER, inViewPager);
        fragment.setArguments(bundle);
        KLog.i("=======UserHomeFragment");
        return fragment;
    }

    @Override
    protected UserInfoPresenter getPresenter() {
        return new UserInfoPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user_home;
    }

    @Override
    protected void initData() {
        super.initData();
        if (getArguments() != null) {
            userId = getArguments().getLong(KEY_USER_ID, 0);
            showBack = getArguments().getBoolean(KEY_SHOW_BACK, true);
            inViewPager = getArguments().getBoolean(KEY_IN_VIEWPAGER, false);
        }
        ivBigAvatar.setOnClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        recyclerView.setOnRefreshListener(this);
        recyclerView.setOnLoadMoreListener(this);
        personalProductAdapter = new PersonalProductAdapter(new ArrayList<ShortVideoItem>(), recyclerView);
        recyclerView.addItemDecoration(new SpaceItemDecoration(getActivity(), UserHomeFragment.SPACE_ITEM_DECRRATION, true));
        recyclerView.setAdapter(personalProductAdapter);
        personalProductAdapter.setOnRecyclerItemClickListener(this);
        llExtraProductType.setOnTypeClickListener(this);
        userInfoHeader = new UserInfoHeader(getActivity());
        recyclerView.setHeader(userInfoHeader);
        userInfoHeader.setInfoClickListener(this);
        getUserInfo(userId);
        userInfoHeader.showFollow(userId != AccountUtil.getUserId());
        rlToolbar.setBackgroundColor(Color.argb(0, 33, 37, 39));
        tvNickname.setAlpha(0);
        recyclerView.getRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollY += dy;
                llExtraProductType.setVisibility(scrollY > scrollHeight ? View.VISIBLE : View.GONE);
                int a = scrollY > scrollHeight ? 255 : (int) (scrollY * 1f / scrollHeight * 255);
                KLog.i("=====onScrolled scrollY:" + scrollY + " ,scrollHeight:" + scrollHeight);
                rlToolbar.setBackgroundColor(Color.argb(a, 33, 37, 39));
                tvNickname.setAlpha(scrollY * 1f / scrollHeight);
            }

        });
        measureHeight();
        followUserPresenter = new FollowUserPresenter(this);
        addPresenter(followUserPresenter);
        ivSetting.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        reportTypeList = new ArrayList<>();
        List<ReportType> list = InitCatchData.userReposrtList().getReport_type();
        for (ReportType type : list) {
            if (type.getResource() == 1) {
                reportTypeList.add(type);
            }
        }
        if (CollectionUtil.isEmpty(reportTypeList)) {
            presenter.getReportList();
        }

    }

    private void measureHeight() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                scrollHeight = userInfoHeader.getHeight() - userInfoHeader.getProductTypeView().getHeight() - DeviceUtils.dip2px(getContext(), 64);
                KLog.i("=====scrollHeight:" + scrollHeight);
            }
        });
    }

    @Override
    public void onVisibleChange(int type, boolean visible) {
        super.onVisibleChange(type, visible);
        if (visible) {
            if (AccountUtil.getUserId() == userId && AccountUtil.isLogin() && currentProductType == ProductTypePanel.TYPE_PRODUCT) {
                getUserInfo(userId);
            }
        } else {
            if (ivBigAvatar.getVisibility() == View.VISIBLE) {
                ivBigAvatar.setVisibility(View.GONE);
            }
        }
    }

    public void getUserInfo(long userId) {
        boolean isSelf = userId == AccountUtil.getUserId();
        ivSetting.setVisibility(!showBack && isSelf ? View.VISIBLE : View.GONE);
        ivAdd.setVisibility(!showBack && isSelf ? View.VISIBLE : View.GONE);
        ivMore.setVisibility(isSelf ? View.GONE : View.VISIBLE);
        ivShare.setVisibility(View.VISIBLE);
        ivBack.setVisibility(showBack ? View.VISIBLE : View.GONE);
        if (userId != 0) {
            this.userId = userId;
            presenter.getPersonalInfo(userId);
            userInfoHeader.selectItem(currentProductType, true);
        }
    }

    @Override
    public void onLoadMore() {
        presenter.getProductList(false, currentProductType, userId);
    }

    @Override
    public void onRefresh() {
        if (currentProductType != ProductTypePanel.TYPE_PRODUCT) {
            presenter.getProductList(true, currentProductType, userId);
        } else {
            cheekDraft();
        }
    }

    @Override
    public void onGetUserInfoOk(UserInfo userInfo) {
        userInfoHeader.initData(userInfo);
        userInfoHeader.showFollow(userId != AccountUtil.getUserId());
        this.userInfo = userInfo;
        if (userInfo.getData() != null) {
            tvNickname.setText(userInfo.getName());
            llExtraProductType.initData(userInfo.getData().getOpus_count(),
                    userInfo.getData().getCo_create_count(),
                    userInfo.getData().getLike_opus_count());
            GlideLoader.loadImage(userInfo.getCover_ori(), ivBigAvatar, userInfo.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
            shareInfo = userInfo.getShare_info();
            shareHome = userInfo.getShare_home();
        } else {
            tvNickname.setText("");
            llExtraProductType.initData(0, 0, 0);
            ivBigAvatar.setImageResource(R.drawable.ic_default_male);
        }
        measureHeight();
    }

    @Override
    public void onGetUserInfoFail(String message) {
        showToast(message);
    }

    @Override
    public void onVideoListOk(boolean isRefresh, List<ShortVideoItem> list, List<Banner> bannerList, List<UserInfo> userInfos, boolean hasMore) {
        if (isRefresh) {
            if (currentProductType == ProductTypePanel.TYPE_PRODUCT && draftEntity != null) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                final ShortVideoItem shortVideoItem = new ShortVideoItem();
                shortVideoItem.isDraft = true;
                shortVideoItem.setOpus_small_cover(draftEntity.coverPath);
                list.add(0, shortVideoItem);
            }
            personalProductAdapter.addData(isRefresh, list, hasMore);
            llExtraProductType.setVisibility(View.GONE);
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (recyclerView != null && recyclerView.getRecycleView() != null) {
                        recyclerView.getRecycleView().scrollToPosition(0);
                    }
                }
            }, 300);
            scrollY = 0;
            tvNickname.setAlpha(0);
        } else {
            personalProductAdapter.addData(isRefresh, list, hasMore);
        }
        showHintAnim(!personalProductAdapter.hasContent(),
                AccountUtil.getUserId() == userId
                        && (getActivity() instanceof MainActivity)
                        && currentProductType == ProductTypePanel.TYPE_PRODUCT);
    }

    @Override
    public void onGetProductFail(boolean isRefresh, String message) {

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
    public void onRequestDataError(int requestCode, String message) {
        if (requestCode == HttpConstant.TYPE_PERSONAL_LIKE_LIST || requestCode == (HttpConstant.TYPE_PERSONAL_LIKE_LIST + 1)) {
            personalProductAdapter.showError(requestCode == HttpConstant.TYPE_PERSONAL_LIKE_LIST);
        } else {
            super.onRequestDataError(requestCode, message);
        }
    }

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
                ivBigAvatar.setVisibility(View.VISIBLE);
            }
        } else {
            //显示大头像
            ivBigAvatar.setVisibility(View.VISIBLE);
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
        }
        personalProductAdapter.setEmptyStr(stringId);
        if (userId != 0) {
            recyclerView.autoRefresh();
        }
        llExtraProductType.selectItem(index);
        userInfoHeader.selectItem(index);
        personalProductAdapter.addData(true, null, true);
    }


    private void cheekDraft() {
        if (userId == AccountUtil.getUserId()
                && AccountUtil.isLogin()) {
            Observable.just(1)
                    .subscribeOn(Schedulers.computation())
                    .map(new Function<Integer, ProductEntity>() {
                        @Override
                        public ProductEntity apply(@NonNull Integer integer) throws Exception {
                            ProductEntity productEntity = RecordUtil.queryLatestDraft();
                            return productEntity == null ? new ProductEntity() : productEntity;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ProductEntity>() {
                        @Override
                        public void accept(@NonNull ProductEntity productEntity) throws Exception {
                            if (productEntity != null && productEntity.hasVideo()) {
                                draftEntity = productEntity;
                            } else {
                                draftEntity = null;
                            }
                            presenter.getProductList(true, currentProductType, userId);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            draftEntity = null;
                            presenter.getProductList(true, currentProductType, userId);
                        }
                    });
        } else {
            draftEntity = null;
            presenter.getProductList(true, currentProductType, userId);
        }
    }

    @Override
    public void onFollowClick(final long userId, final boolean isFollow) {
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

    }

    @Override
    public void onWeiboHomeClick(String weiboId) {

    }

    @Override
    public void onOfficalWebSite(String link) {

    }

    @Override
    public void onAccountClick(long userId) {

    }

    @Override
    public void onMessageClick(long userId, UserInfo userInfo) {

    }


    @Override
    public void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll, int position, long userId, long videoId, boolean isFollowed) {
        if (userInfo != null) {
            if (userInfo.getRelation() != null) {
                userInfo.getRelation().is_follow = isFollowed;
            } else {
                UserHomeRelation userHomeRelation = new UserHomeRelation();
                userHomeRelation.is_fans = isFollowed;
                userInfo.setRelation(userHomeRelation);
            }
        }
        if (isFollowed) {
            showToast(R.string.stringFollowed);
        }
        userInfoHeader.setFollowed(isFollowed);
    }

    private void showHintAnim(boolean show, boolean showAnim) {
        ivEmptyHint.setVisibility(show && showAnim ? View.VISIBLE : View.GONE);
        tvEmptyHint.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show && showAnim) {
            translateAnimation = new TranslateAnimation(0f, 0f, 0f, -20f);
            translateAnimation.setDuration(800);
            translateAnimation.setRepeatCount(Animation.INFINITE);
            translateAnimation.setRepeatMode(Animation.REVERSE);
            ivEmptyHint.setAnimation(translateAnimation);
            translateAnimation.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ivEmptyHint != null) {
            ivEmptyHint.clearAnimation();
        }
        if (translateAnimation != null) {
            translateAnimation.cancel();
            translateAnimation = null;
        }
    }

    private MyClickListener shareClick = new MyClickListener() {
        @Override
        protected void onMyClick(View v) {
            shareHome.objId = userId;
            shareHome.shareType = ShareEventEntity.TYPE_USER_HOME;
            switch (v.getId()) {
                case R.id.llWeChat:
                    wechatShare(0, shareHome);
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
                    ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setPrimaryClip(ClipData.newPlainText(null, shareHome.web_link));
                    showToast(R.string.copy_succeed);
                    shareHome.shareTarget = ShareEventEntity.TARGET_WEB;
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

//    @Override
//    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//        dismissLoad();
//        showToast(R.string.share_suc);
//    }
//
//    @Override
//    public void onError(Platform platform, int i, Throwable throwable) {
//        dismissLoad();
//        showToast(R.string.share_faid);
//    }
//
//    @Override
//    public void onCancel(Platform platform, int i) {
//        dismissLoad();
//        showToast(R.string.share_cancel);
//    }

    @Override
    public void onRecyclerItemClick(int dataPosition, View view, ShortVideoItem data) {
        if (data != null) {
            if (data.isDraft) {
                startActivity(new Intent(getActivity(), DraftBoxActivity.class));
            } else {
                VideoListActivity.startVideoListActivity(getActivity(),
                        getVideoType(currentProductType),
                        MultiTypeVideoBean.createUserParma(userId, dataPosition, personalProductAdapter.getDataContainer()));
            }
        }
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
                        popReportActionSheet = new PopActionSheetNoTitle(getActivity());
                        popReportActionSheet.setOnSnsClickListener(new PopActionSheetNoTitle.OnSnsClickListener() {
                            @Override
                            public void onSnsClick() {
                                popReportContentActionSheet = new PopReportContentActionSheet(getActivity());
                                popReportContentActionSheet.setReportTypeList(reportTypeList);
                                popReportContentActionSheet.setOnSnsClickListener(new PopReportContentActionSheet.OnSnsClickListener() {
                                    @Override
                                    public void onSnsClick(ReportType reportType) {
                                        if (null != reportType) {
                                            if (null != userInfo) {
                                                presenter.reportUser(userInfo.getId(), reportType.getId());
                                            }
                                        }
                                        popReportContentActionSheet.dismiss();
                                    }
                                });
                                popReportContentActionSheet.show();
                            }

                            @Override
                            public void onUserBlockClick(boolean isUserBlock) {

                            }
                        });
                        popReportActionSheet.show();
                    }
                }
                break;
            case R.id.ivShare:
                if (shareHome != null) {
                    shareWindow = PopupWindowUtils.showNormal(getActivity(), ivShare, shareClick);
                } else {
                    showToast(R.string.hintErrorDataDelayTry);
                }
                break;
            case R.id.ivBack:
                if (ivBigAvatar.getVisibility() == View.VISIBLE) {
                    ivBigAvatar.setVisibility(View.GONE);
                    return;
                }
                if (inViewPager) {
                    getActivity().onKeyDown(KeyEvent.KEYCODE_BACK, null);
                } else {
                    getActivity().finish();
                }
                break;
        }
    }

    @Override
    protected void onBack() {
        if (ivBigAvatar.getVisibility() == View.VISIBLE) {
            ivBigAvatar.setVisibility(View.GONE);
            return;
        }
        super.onBack();
    }

    private int getVideoType(int type) {
//        switch (type) {
//            case ProductTypePanel.TYPE_LIKE:
//                return RecommendFragment.TYPE_LIKE;
//            case ProductTypePanel.TYPE_PRODUCT:
//                return RecommendFragment.TYPE_USER;
//            case ProductTypePanel.TYPE_TOGETHER:
//                return RecommendFragment.TYPE_USER;
//        }
//        return RecommendFragment.TYPE_USER;
        return RecommendFragment.TYPE_USER_HOME;
    }

}
