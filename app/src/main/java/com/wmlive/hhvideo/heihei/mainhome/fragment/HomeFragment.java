package com.wmlive.hhvideo.heihei.mainhome.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseFragment;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.common.manager.LogFileManager;
import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchActivity;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.LatestActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MessageActivity;
import com.wmlive.hhvideo.heihei.mainhome.adapter.HomeViewPagerAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.HomePresenter;
import com.wmlive.hhvideo.heihei.mainhome.util.PublishUtils;
import com.wmlive.hhvideo.heihei.mainhome.util.SharedPreferencesUtils;
import com.wmlive.hhvideo.heihei.mainhome.widget.CommentPanel;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.splash.SplashActivity;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.WebpUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.FloatTextView;
import com.wmlive.hhvideo.widget.MainBottomTabView;
import com.wmlive.hhvideo.widget.MainTopTabView;
import com.wmlive.hhvideo.widget.OnMainTabClickListener;
import com.wmlive.hhvideo.widget.SelectStepPanel;
import com.wmlive.hhvideo.widget.UnScrollViewPager;
import com.wmlive.hhvideo.widget.dialog.FunctionTipsDialog;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;
import com.wmlive.hhvideo.widget.dialog.RemindDialog;
import com.wmlive.hhvideo.widget.dialog.VerifyDialog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;
import com.wmlive.networklib.util.NetUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.BuildConfig;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.leolin.shortcutbadger.ShortcutBadger;

import static android.os.Looper.getMainLooper;

/**
 * Created by lsq on 7/4/2017.
 * 首页Fragment
 */

public class HomeFragment extends BaseFragment<HomePresenter> implements
        OnMainTabClickListener,
        HomePresenter.IHomeView,
        RecommendFragment.RecommendScrollListener,
        SelectStepPanel.SelectStepPanelListener,
        ViewPager.OnPageChangeListener, DiscoverFragment.ScrolListinner {

    @BindView(R.id.mbTopTabs)
    MainTopTabView mbTopTabs;
    @BindView(R.id.mbBottomTabs)
    MainBottomTabView mbBottomTabs;
    @BindView(R.id.vpViewPager)
    UnScrollViewPager vpViewPager;
    @BindView(R.id.vs_main_guide)
    ViewStub vsMainGuide;
    @BindView(R.id.viewSelectStep)
    SelectStepPanel viewSelectStep;
    @BindView(R.id.viewCommentPanel)
    CommentPanel viewCommentPanel;
    @BindView(R.id.new_function_guide_rl)
    RelativeLayout newFunctionGuideRl;
    @BindView(R.id.colse_iv)
    ImageView colseIv;

    private static final int MSG_GET_DISCOVERY_MESSAGE = 10;//请求发现的消息数量

    //当前tab的位置:0是关注，1是推荐，2是最新，3是个人中心，4是消息，5是发现
    public static int currentTabPosition = 1;
    private int topTabPosition = 1;//顶部3个tab的位置

    //    private RecommendFragment followFragment;
    private RecommendFragment recommendFragment;

    private FloatTextView tvSwitch;
    private ImageView ivMainGuide;
    private VerifyDialog verifyDialog;
    private HomeViewPagerAdapter homeViewPagerAdapter;
    private List<BaseFragment> fragmentList;
    private DiscoverFragment discoverFragment;
    private FunctionTipsDialog functionTipsDialog;

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SINGLE_MODE, true);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Override
    protected int getBaseLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected HomePresenter getPresenter() {
        return new HomePresenter(this);
    }

    //定时轮询发现的消息
    private Handler handler = new Handler(getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_DISCOVERY_MESSAGE:
                    presenter.getDiscoveryMessage(SPUtils.getLong(getActivity(), SPUtils.KEY_LATEST_GET_DISCOVERY_MSG, 0L));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main_guide:
                if (vsMainGuide != null) {
                    SPUtils.putBoolean(getActivity(), SPUtils.KEY_SHOW_GUIDE, true);
                    vsMainGuide.setVisibility(View.GONE);
                }
                break;
            case R.id.colse_iv:
                newFunctionGuideRl.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        KLog.i("======HomeFragment=initData");
        EventHelper.register(this);
        fragmentList = new ArrayList<>(2);
        tvSwitch = (FloatTextView) findViewById(R.id.tvSwitch);
        switchEnvironment();
        initFragment();
        showGuide();
        showNewFunctinTips();
        presenter.getDiscoveryMessage(SPUtils.getLong(getActivity(), SPUtils.KEY_LATEST_GET_DISCOVERY_MSG, 0L));
        GlobalParams.StaticVariable.sCurrentNetwork = NetUtil.getNetworkState(DCApplication.getDCApp());
        mbTopTabs.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    if (GlobalParams.StaticVariable.sCurrentNetwork == 1 && !GlobalParams.StaticVariable.sHasShowedRemind) {
                        new RemindDialog(getActivity())
                                .show();
                        GlobalParams.StaticVariable.sHasShowedRemind = true;
                    }
                }
            }
        }, 1000);
    }

    public CommentPanel getCommentPanel() {
        return viewCommentPanel;
    }

    public void showLatest(boolean show) {
        mbTopTabs.showLatest(show);
        if (!show) {
            if (currentTabPosition == 2) {
                mbTopTabs.setSelect(1);
                onRecommendClick();
            }
        }
    }

    private void initWebp() {
        try {
            WebpUtil.getWebpVersion();
            GlobalParams.StaticVariable.sSupportWebp = true;
        } catch (Exception | Error e) {
            e.printStackTrace();
            LogFileManager.getInstance().saveLogInfo("getWebpVersion", "not support create webp,message:" + e.getMessage());
            GlobalParams.StaticVariable.sSupportWebp = false;
        }
        KLog.i("======是否支持webp生成：" + GlobalParams.StaticVariable.sSupportWebp);
    }

    //线上线下url环境切换
    private void switchEnvironment() {
        tvSwitch.setText(getString(R.string.stringSwitchEnvironment,
                GlobalParams.StaticVariable.sReleaseEnvironment ? "线上" : "线下",
                GlobalParams.StaticVariable.sReleaseEnvironment ? "线下" : "线上"));
        tvSwitch.setVisibility(BuildConfig.DEBUG_SWITCH ? View.VISIBLE : View.GONE);
        tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountUtil.clearAccount();
                SPUtils.putString(DCApplication.getDCApp(), InitCatchData.KEY_INIT_CATCH_DATA, "");
                InitCatchData.setInitUrl(null);
                boolean sReleaseEnvironment = (boolean) SharedPreferencesUtils.getParam(getActivity(), "sReleaseEnvironment", false);
                GlobalParams.StaticVariable.sReleaseEnvironment = !sReleaseEnvironment;
                SharedPreferencesUtils.setParam(getActivity(), "sReleaseEnvironment", !sReleaseEnvironment);
                SplashActivity.startSplashActivity(getActivity());
            }
        });
    }

    private void initFragment() {
        KLog.i("======HomeFragment=initFragment");
        mbTopTabs.setTabClickListener(this);
        mbBottomTabs.setTabClickListener(this);
        viewSelectStep.setStepPanelListener(this);
        colseIv.setOnClickListener(this);
        mbTopTabs.showLatest(AccountUtil.isAuthUser());
        mbTopTabs.setSelect(0);
        mbBottomTabs.setSelect(0);
        initViewPager();
        TaskManager.get().getAllIp();
    }

    private void initViewPager() {
        vpViewPager.removeAllViewsInLayout();
        fragmentList.clear();
        vpViewPager.setOffscreenPageLimit(2);
        vpViewPager.setScrollable(true);
        recommendFragment = RecommendFragment.newInstance(RecommendFragment.TYPE_RECOMMEND);
        recommendFragment.setRecommendScrollListener(this);
//        followFragment = RecommendFragment.newInstance(RecommendFragment.TYPE_FOLLOW);
//        followFragment.setRecommendScrollListener(this);
        discoverFragment = DiscoverFragment.newInstance();
        discoverFragment.setScrolListinner(this);
        fragmentList.add(recommendFragment);
        fragmentList.add(discoverFragment);
        homeViewPagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager(), fragmentList);
        vpViewPager.setAdapter(homeViewPagerAdapter);
        vpViewPager.addOnPageChangeListener(this);
        vpViewPager.setCurrentItem(0);
//        vpViewPager.setScrollable(AccountUtil.isLogin());
    }

    public void setCurrentItem(int position) {
        if (vpViewPager != null) {
            vpViewPager.setCurrentItem(position);
        }
    }

    public int getCurrentItem() {
        if (vpViewPager != null) {
            return vpViewPager.getCurrentItem();
        }
        return 1;
    }

    public void showFollowPageRelogin() {
//        followFragment.viewRecommendUsers.showRelogin();
    }

    @Override
    public void onResume() {
        super.onResume();
        mbTopTabs.showLatest(AccountUtil.isAuthUser());
        setMessageCount(MessageManager.get().getImAllUnreadCount(true));
    }

    @Override
    public boolean onHomeClick() {
        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return false;
        }

//        mbTopTabs.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public boolean onFollowClick() {//关注页面
        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return false;
        }

        if (vpViewPager.getCurrentItem() == 0) {
            if (discoverFragment != null) {
//                discoverFragment.manualRefresh();
            }
            return false;
        }

//        if (AccountUtil.isLogin()) {
        vpViewPager.setCurrentItem(0);
//        onScroll(0, followFragment.getScrollY());
        onScroll(0, discoverFragment.getScrollY());
        topTabPosition = 0;
        return true;
//        } else {
//            showReLogin();
//            return false;
//        }
    }

    @Override
    public boolean onDiscoveryTab() {

        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return false;
        }

        if (vpViewPager.getCurrentItem() == 1) {

            return false;
        }

        vpViewPager.setCurrentItem(1);
        onScroll(0, discoverFragment.getScrollY());
        KLog.d("recommendFragment.getScrollY()==" + discoverFragment.getScrollY());

        return true;

    }

    @Override
    public boolean onRecommendClick() {//推荐页面
        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return false;
        }

        if (vpViewPager.getCurrentItem() == 0) {
            if (recommendFragment != null) {
                recommendFragment.manualRefresh();
            }
            return false;
        }

//        if (AccountUtil.isLogin()) {
        vpViewPager.setCurrentItem(0);
        onScroll(0, recommendFragment.getScrollY());
        topTabPosition = 0;
        return true;
    }

    @Override
    public boolean onLatestClick() {//最新页面
        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return false;
        }
        startActivity(new Intent(getActivity(), LatestActivity.class));
        return false;
    }

    @Override
    public boolean onMineClick() {//个人信息页面
        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return false;
        }

        if (AccountUtil.isLogin()) {
            UserHomeActivity.startUserHomeActivity(getActivity(), AccountUtil.getUserId());
            return true;
        } else {
            showReLogin();
            return false;
        }
    }

    @Override
    public boolean onDiscoveryClick() {
//        DiscoveryActivity.startDiscoveryActivity(getActivity());
        startActivity(new Intent(getActivity(), SearchActivity.class));
        mbTopTabs.showBellDot(false);
        return true;
    }


    @Override
    public boolean onBellClick() {//进入铃铛页面
        if (!AccountUtil.isLogin()) {
            showReLogin();
            return false;
        }
        startActivity(new Intent(getActivity(), MessageActivity.class));
        return true;
    }

    @Override
    public boolean onPublishClick() {
//        ChooseStyle4QuickActivity.startChooseStyleQuikActivity(getContext());
//        return true;

//        if (InitCatchData.getInitCatchData() != null && InitCatchData.getInitCatchData().getTips() != null && InitCatchData.getInitCatchData().getTips().recordCheck != null) {//服务器控制是否可录制的
//            if (InitCatchData.getInitCatchData().getTips().recordCheck.showTip) {
//                ToastUtil.showToast(InitCatchData.getInitCatchData().getTips().recordCheck.tips);
//                return false;
//            }
//        }
////            KLog.i("===规则地址：",InitCatchData.getInitCatchData().getOpus().getOpusTopRule());
//        if (RecordManager.get().getProductEntity() != null && RecordManager.get().getProductEntity().productType == ProductEntity.TYPE_PUBLISHING) {//有作品正在发布中
//            ToastUtil.showToast("有作品正在上传中\n请稍后再试");
//            return false;
//        }
        if (PublishUtils.showToast()) {
            return false;
        }

        if (AccountUtil.needVerifyCode()) {
            showVerifyDialog();
            return false;
        } else {
            final BaseModel count = new BaseModel();
            new RxPermissions(getActivity())
                    .requestEach(RecordSetting.RECORD_PERMISSIONS)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) throws Exception {
                            KLog.i("====请求权限：" + permission.toString());
                            if (!permission.granted) {
                                if (Manifest.permission.CAMERA.equals(permission.name)) {
                                    new PermissionDialog((BaseCompatActivity) getActivity(), 20).show();
                                } else if (Manifest.permission.RECORD_AUDIO.equals(permission.name)) {
                                    new PermissionDialog((BaseCompatActivity) getActivity(), 10).show();
                                }
                            } else {
                                count.type++;
                            }
                            if (count.type == 3) {
                                KLog.i("=====获取权限：成功");
                                int result = -1;//-1表示权限获取失败，-2表示相机初始化失败，0表示权限和相机都成功
                                result = RecordManager.get().initRecordCore(getActivity()) ? 0 : -2;
                                if (result == 0) {
                                    //设置是不是显示新功能提示弹窗
                                    SharedPreferencesUtils.setParam(getActivity(), "isGuideTipsShow", false);
                                    showNewFunctinTips();
                                    viewSelectStep.show();
                                } else if (result == -1) {
                                    ToastUtil.showToast("请在系统设置中允许App运行必要的权限");
                                } else {
                                    KLog.i("=====初始化相机失败");
                                    ToastUtil.showToast("初始化相机失败");
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.getMessage();
                            KLog.i("=====初始化相机失败:" + throwable.getMessage());
                            ToastUtil.showToast("初始化相机失败");
                        }
                    });
            return true;
        }
    }

    /**
     * 显示邀请码对话框
     */
    public void showVerifyDialog() {
        if (verifyDialog == null) {
            verifyDialog = new VerifyDialog(getActivity());
            verifyDialog.setOnVerifyListener(new VerifyDialog.OnVerifyListener() {
                @Override
                public void onVerifySuccess() {
                    SelectFrameActivity.startSelectFrameActivity((BaseCompatActivity) getActivity(), new TopicInfoEntity(), SelectFrameActivity.VIDEO_TYPE_RECORD);
                }
            });
        }
        if (!verifyDialog.isShowing()) {
            verifyDialog.show();
        }
    }


    private void showGuide() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        initWebp();
                        return SPUtils.getBoolean(getActivity(), SPUtils.KEY_SHOW_GUIDE, false);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //hide
                            if (vsMainGuide == null) {
                                vsMainGuide = (ViewStub) findViewById(R.id.vs_main_guide);
                            }
                            vsMainGuide.setVisibility(View.GONE);
                        } else {
                            //show
                            if (vsMainGuide == null) {
                                vsMainGuide = (ViewStub) findViewById(R.id.vs_main_guide);
                            }
                            vsMainGuide.inflate();
                            ivMainGuide = (ImageView) findViewById(R.id.iv_main_guide);
                            ivMainGuide.setOnClickListener(HomeFragment.this);
                            GlideLoader.loadImage(R.drawable.icon_home_guide, ivMainGuide, R.drawable.icon_home_guide);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public boolean onBackPressed() {
        if (viewSelectStep != null && viewSelectStep.getVisibility() == View.VISIBLE) {
            viewSelectStep.dismiss();
            return true;
        }
        if (viewCommentPanel != null && viewCommentPanel.getVisibility() == View.VISIBLE) {
            viewCommentPanel.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onGetDiscoveryMessage(boolean hasNew, int nextTime, int unreadCount) {
//        mbTopTabs.showBellDot(hasNew);
        GlobalParams.StaticVariable.sDiscoverUnreadCount = unreadCount;
        KLog.i("====收到Discovery的消息,hasNew:" + hasNew + " ,nextTime:" + nextTime + " ,unreadCount:" + unreadCount);
        if (handler != null) {
            handler.sendEmptyMessageDelayed(MSG_GET_DISCOVERY_MESSAGE, nextTime > 0 ? (nextTime * 1000) : 60000);
        }
    }


    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        handler = null;
        EventHelper.unregister(this);
        super.onDestroy();
    }

    public void setMessageCount(long count) {
        KLog.i("======Home需要显示的消息数量：" + count);
        mbBottomTabs.setMessageCount(count);
        if (count > 99) {
            ShortcutBadger.applyCount(DCApplication.getDCApp(), 99);
        } else if (count > 0) {
            ShortcutBadger.applyCount(DCApplication.getDCApp(), (int) count);
        } else {
            ShortcutBadger.removeCount(DCApplication.getDCApp());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImChatMessageEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_IM_CHAT_MSG) {
            KLog.i("====收到新的ImChat消息");
            if (eventEntity.data != null && eventEntity.data instanceof HashSet) {
                setMessageCount(MessageManager.get().getImAllUnreadCount(true));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImMessageEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_IM_SYSTEM_MSG) {
            KLog.i("====收到新的系统Im消息");
            if (eventEntity.data != null && eventEntity.data instanceof List) {
                List<Long> idList = (List<Long>) eventEntity.data;
                if (!CollectionUtil.isEmpty(idList)) {
                    setMessageCount(MessageManager.get().getImAllUnreadCount(true));
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshImCount(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_REFRESH_HOME_IM_COUNT) {
            if (eventEntity.data != null) {
                setMessageCount((long) eventEntity.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocialLoginOk(EventEntity eventEntity) {
        //TODO 临时方案，登录后自动跳转
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_LOGIN_OK) {
            vpViewPager.setScrollable(AccountUtil.isLogin());
            if(!isQuickShootLogin){
                        RecordMvActivity.startRecordMv((BaseCompatActivity) getActivity(), RecordMvActivityHelper.EXTRA_RECORD_TYPE_RECORD, 1);
                viewSelectStep.dismiss();
                isQuickShootLogin = true;
            }
            if(!isUploadLogin){
                SearchVideoActivity.startSearchVideoActivity((BaseCompatActivity) getActivity(), 0, SearchVideoActivity.TYPE_FROM_DIRECT_UPLOAD, 0);
                viewSelectStep.dismiss();
                isUploadLogin = true;
            }
            if(!isRecordLogin){
                SelectFrameActivity.startSelectFrameActivity((BaseCompatActivity) getActivity(), new MusicInfoEntity(), SelectFrameActivity.VIDEO_TYPE_RECORD);
                viewSelectStep.dismiss();
                isRecordLogin = true;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenInvalid(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == 30001) {
//            vpViewPager.setScrollable(AccountUtil.isLogin());

        }
    }

    /**
     * 默认指定选中tab
     * 0:home  1:discover   3:bell    4:me    5:attention  6:recommend  7 latest
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showTabsByIndex(final EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_SHOW_MAIN_FIRST) {
            KLog.i("=====收到切换tab消息");
            if (getActivity() != null) {
                mbTopTabs.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (eventEntity.data != null && eventEntity.data instanceof Integer) {
                            final int index = (int) eventEntity.data;
                            KLog.i("=====收到切换tab 的index:" + index + " ,topTabPosition:" + topTabPosition);
                            if (index > 4) {
                                onHomeClick();
                                mbBottomTabs.setSelect(0);
                                mbTopTabs.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        switch (index) {
                                            case 5:
                                                if (onFollowClick()) {
                                                    KLog.i("======转到tab：" + (index - 5));
                                                    mbTopTabs.setSelect(index - 5);
                                                } else {
                                                    KLog.i("======转到tab：" + (index - 5) + "失败");
                                                }
                                                break;
                                            case 6:
                                                if (onRecommendClick()) {
                                                    KLog.i("======转到tab：" + (index - 5));
                                                    mbTopTabs.setSelect(index - 5);
                                                } else {
                                                    KLog.i("======转到tab：" + (index - 5) + "失败");
                                                }
                                                break;
                                            case 7:
                                                if (onLatestClick()) {
                                                    KLog.i("======转到tab：" + (index - 5));
                                                    mbTopTabs.setSelect(index - 5);
                                                } else {
                                                    KLog.i("======转到tab：" + (index - 5) + "失败");
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }, 150);
                            } else {
                                switch (index) {
                                    case 0:
                                        onHomeClick();
                                        break;
                                    case 1:
                                        onDiscoveryClick();
                                        break;
                                    case 3:
                                        onBellClick();
                                        break;
                                    case 4:
                                        onMineClick();
                                        break;
                                    default:
                                        break;
                                }
                                mbBottomTabs.setSelect(index);
                            }
                        }
                    }
                }, 150);
            }
        }
    }


    @Override
    public void onScroll(int currentScrollY, int allScrollY) {
        if (mbTopTabs != null) {
            mbTopTabs.zoom(currentScrollY, allScrollY);
        }
    }

    private boolean isQuickShootLogin = true;
    private boolean isUploadLogin = true;
    private boolean isRecordLogin = true;

    @Override
    public void toQuickShoot() {
        if (AccountUtil.isLogin()) {
            RecordMvActivity.startRecordMv((BaseCompatActivity) getActivity(), RecordMvActivityHelper.EXTRA_RECORD_TYPE_RECORD, 1);
            viewSelectStep.dismiss();
        } else {
            isQuickShootLogin = false;
            isUploadLogin = true;
            isRecordLogin = true;
            showReLogin();
        }
    }

    @Override
    public void toUpload() {
        if (AccountUtil.isLogin()) {
            viewSelectStep.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SearchVideoActivity.startSearchVideoActivity((BaseCompatActivity) getActivity(), 0, SearchVideoActivity.TYPE_FROM_DIRECT_UPLOAD, 0);
                }
            }, 500);
            viewSelectStep.dismiss();
        } else {
            isUploadLogin = false;
            isQuickShootLogin = true;
            isRecordLogin = true;
            showReLogin();
        }

    }

    @Override
    public void toRecord() {
        if (AccountUtil.isLogin()) {
            viewSelectStep.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SelectFrameActivity.startSelectFrameActivity((BaseCompatActivity) getActivity(), new MusicInfoEntity(), SelectFrameActivity.VIDEO_TYPE_RECORD);
                }
            }, 500);
            viewSelectStep.dismiss();

        } else {
            isRecordLogin = false;
            isQuickShootLogin = true;
            isUploadLogin = true;
            showReLogin();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mbTopTabs.setSelect(position);
        if (position == 1) {
            onScroll(0, discoverFragment.getScrollY());
//            vpViewPager.setScrollable(true);
        } else if (position == 0) {
            onScroll(0, recommendFragment.getScrollY());
//            vpViewPager.setScrollable(AccountUtil.isLogin());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 新功能提示弹窗
     */
    public void showNewFunctinTips() {
        if (SharedPreferencesUtils.isShowGuideTips(getActivity())) {
            newFunctionGuideRl.setVisibility(View.VISIBLE);
        } else {
            newFunctionGuideRl.setVisibility(View.GONE);
        }
    }
}
