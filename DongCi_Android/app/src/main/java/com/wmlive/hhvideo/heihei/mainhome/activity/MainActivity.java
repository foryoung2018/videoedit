package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.baidu.aiupdatesdk.AIUpdateSDK;
import com.baidu.aiupdatesdk.CheckUpdateCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.AppStatusManager;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.LogFileManager;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.main.UpdateInfo;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;
import com.wmlive.hhvideo.heihei.beans.splash.CheckDeviceResponse;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.fragment.HomeFragment;
import com.wmlive.hhvideo.heihei.mainhome.presenter.MainPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.UserInfoCallback;
import com.wmlive.hhvideo.heihei.mainhome.util.SharedPreferencesUtils;
import com.wmlive.hhvideo.heihei.personal.presenter.UserAccountInfoPresenter;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountInfoView;
import com.wmlive.hhvideo.heihei.quickcreative.ChooseStyle4QuickActivity;
import com.wmlive.hhvideo.heihei.quickcreative.utils.CopyAssetFilesUtil;
import com.wmlive.hhvideo.heihei.record.activity.LocalPublishActivity;
import com.wmlive.hhvideo.heihei.record.activity.PublishMvActivity;
import com.wmlive.hhvideo.heihei.record.activity.RecordActivitySdk;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.push.IPushBindUserView;
import com.wmlive.hhvideo.push.PushBindUserPresenter;
import com.wmlive.hhvideo.service.DCService;
import com.wmlive.hhvideo.service.DcJobService;
import com.wmlive.hhvideo.service.DcWebSocketService;
import com.wmlive.hhvideo.service.GiftService;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.HeaderUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.location.AMapLocateCallback;
import com.wmlive.hhvideo.utils.location.AMapLocator;
import com.wmlive.hhvideo.utils.location.LocationEntity;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.utils.update.UpdateUtils;
import com.wmlive.hhvideo.widget.MessageView;
import com.wmlive.hhvideo.widget.dialog.BaiduUpdateDialog;
import com.wmlive.hhvideo.widget.dialog.BaseDialog;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.dialog.MyDialog;
import com.wmlive.hhvideo.widget.dialog.UpDateDialog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;
import com.wmlive.networklib.util.NetUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.magicwindow.MLinkAPIFactory;
import cn.magicwindow.mlink.annotation.MLinkDefaultRouter;
import cn.magicwindow.mlink.annotation.MLinkRouter;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 首页Activity
 */
@MLinkDefaultRouter
@MLinkRouter(keys = {GlobalParams.MWConfig.LINK_KEY_HOME})
public class MainActivity extends DcBaseActivity<MainPresenter> implements
        MainPresenter.IMainNewView,
        UserInfoCallback,
        IPushBindUserView,
        IUserAccountInfoView,
        AMapLocateCallback,
        MessageView.OnNextMessageListener {
    private static final int CHECK_SPLASH_INTERVAL = 5 * 60 * 1000;
    //从jpush广播传过来的内容
    public static final String KEY_CONTENT = "jpush_content";
    @BindView(R.id.flMainContainer)
    public FrameLayout flContainer;
    @BindView(R.id.messageView)
    public MessageView messageView;
    @BindView(R.id.rlrootview)
    public RelativeLayout rlrootview;

    private long exitTime = 0;
    public static int updateApkStatus = -1;//（-1: 默认状态      0：下载中       1：下载完成       2：任务被取消）
    private UpDateDialog mUpDateDialog;
    private HomeFragment homeFragment;
    private MyDialog useMobileDialog;
    private UserAccountInfoPresenter userAccountInfoPresenter;

    private PushBindUserPresenter pushBindUserPresenter;
    private AMapLocator aMapLocator;

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainPresenter getPresenter() {
        return new MainPresenter(this);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventHelper.register(this);
        SharedPreferencesUtils.setParam(this, "isFirstStart", false);
        DiscoveryUtil.updateLocalVideo(DCApplication.getDCApp(), null);
        checkUpgrade();
        disposeJPush();
        RecordManager.get().init(DCApplication.getDCApp(), GlobalParams.Config.IS_DEBUG);
        MLinkAPIFactory.createAPI(this).deferredRouter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onReloadIntent(Intent intent) {
        super.onReloadIntent(intent);
        KLog.i("=======MainActivity=onNewIntent");
        setIntent(intent);
        disposeJPush();
        int action = intent.getIntExtra(AppStatusManager.KEY_HOME_ACTION, AppStatusManager.STATUS_NORMAL);
        switch (action) {
            case AppStatusManager.ACTION_RESTART_APP:
//                protectApp();
//                break;
            default:
                initHome();
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
//        changeDecorView(4);
        updateApkStatus = -1;
        initHome();
        pushBindUserPresenter = new PushBindUserPresenter(this);
        userAccountInfoPresenter = new UserAccountInfoPresenter(this);
        addPresenter(pushBindUserPresenter, userAccountInfoPresenter);
        messageView.setNextMessageListener(this);
        //上传日志
        uploadLog(AppCacheFileUtils.getAppLogPath() + File.separator + LogFileManager.getInstance().getLogFileName());

    }

    private void initHome() {
        if (homeFragment != null) {
            homeFragment.onDestroy();
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(homeFragment)
                    .commit();
        }
        homeFragment = null;
        homeFragment = HomeFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        fragmentManager
                .beginTransaction()
                .add(R.id.flMainContainer, homeFragment, "home_fragment")
                .commitAllowingStateLoss();

        checkInitUrl();
        initUserInfo();
        GlobalParams.StaticVariable.sCurrentNetwork = NetUtil.getNetworkState(MainActivity.this);
        KLog.i("=======sCurrentNetwork:" + GlobalParams.StaticVariable.sCurrentNetwork);
        getFrameLayout();//获取画框数据
        getCreativeList();//获取模板数据
        CopyAssetFilesUtil.copyFiles(MainActivity.this);
        checkUnfinishedProduct();

        bindJPushUser();
        //获取经纬度
        getLocation();
        getWeakHandler().removeCallbacks(checkSplashRunnable);
        getWeakHandler().postDelayed(checkSplashRunnable, 2000);
        //保活IM和上传用户行为进程
        DcJobService.openJobService(this);
    }


    private void checkUnfinishedProduct() {
        Observable.just(1)
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, ProductEntity>() {
                    @Override
                    public ProductEntity apply(@NonNull Integer integer) throws Exception {
                        return AccountUtil.isLogin() ? RecordUtil.queryUnfinishedProduct() : new ProductEntity();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProductEntity>() {
                    @Override
                    public void accept(@NonNull final ProductEntity productEntity) throws Exception {
                        KLog.d("", "数据库存储的作品对象:   productEntity==" + productEntity);
                        if (productEntity != null && (productEntity.hasVideo() || productEntity.isLocalUploadVideo() || productEntity.getExtendInfo().productCreateType == RecordMvActivityHelper.TYPE_RECORD_MV)) {
                            CustomDialog customDialog = new CustomDialog(MainActivity.this, R.style.BaseDialogTheme);
                            customDialog.setContent(getString(R.string.exists_editing_video));
                            customDialog.setCanceledOnTouchOutside(false);
                            customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    customDialog.dismiss();
                                    if (productEntity.isLocalUploadVideo()) {
                                        RecordManager.get().setProductEntity(productEntity);
                                        LocalPublishActivity.startLocalPublishActivity(MainActivity.this, LocalPublishActivity.FORM_DRAFT);
                                    } else {
                                        int step = SPUtils.getInt(MainActivity.this, SPUtils.KEY_EDITING_STEP, 0);
                                        RecordManager.get().setProductEntity(productEntity);
                                        if (productEntity.getExtendInfo().productCreateType == RecordMvActivityHelper.TYPE_RECORD_MV) {
                                            if (step == RecordSetting.STEP_RECORD) {
                                                RecordMvActivity.startRecordMv(MainActivity.this, RecordMvActivityHelper.EXTRA_RECORD_TYPE_DRAFT, 0);
                                            } else if (step == RecordSetting.STEP_PUBLISH) {
                                                PublishMvActivity.startPublishActivity(MainActivity.this, false);
                                            }
                                        } else {
                                            if (step == RecordSetting.STEP_RECORD) {
                                                RecordActivitySdk.startRecordActivity(MainActivity.this, RecordActivitySdk.TYPE_DRAFT);
                                            } else if (step == RecordSetting.STEP_PUBLISH) {
                                                RecordActivitySdk.startRecordActivity(MainActivity.this, RecordActivitySdk.TYPE_DRAFT);
                                            }
                                        }

                                    }
                                }
                            });
                            customDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    customDialog.dismiss();
                                    productEntity.productType = ProductEntity.TYPE_DRAFT;
                                    RecordUtil.insertOrUpdateProductToDb(productEntity);
                                }
                            });
                            customDialog.show();
                        } else {
                            KLog.i("====没有需要编辑的视频");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 上传日志的接口
     *
     * @param filePath
     */
    private void uploadLog(final String filePath) {
        KLog.i("log_update", "=====filePath====：" + filePath);
        Observable.just(1)
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Integer integer) throws Exception {
                        KLog.i("log_update", "=====开始上传Log");
                        LogFileManager.getInstance().setLogFileUpdate(true);
                        if (presenter != null) {
                            presenter.uploadLog(new File(filePath));//测试文件
                        }
                        return true;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        //删除已经上传的日志文件
                        KLog.i("log_update", "=====日志上传结束");
                        LogFileManager.getInstance().setLogFileUpdate(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        KLog.i("log_update", "=====日志上传错误");
                        LogFileManager.getInstance().setLogFileUpdate(false);
                        throwable.printStackTrace();
                    }
                });
    }

    //是否需要拉取url
    private void checkInitUrl() {
        if (GlobalParams.StaticVariable.sInitFromLocal && !isFinishing() && !isDestroyed()) {
            if (presenter != null) {
                presenter.init();
            }
        }
    }

    //检查升级
    private void checkUpgrade() {
        if ("baidu".equals(GlobalParams.StaticVariable.CHANNEL_CODE.toLowerCase())) {
            //百度升级
            AIUpdateSDK.updateCheck(MainActivity.this, checkUpdateCallback);
        } else {
            if (presenter != null) {
                presenter.checkSystemAppUpdate(String.valueOf(GlobalParams.Config.APP_VERSION), GlobalParams.StaticVariable.CHANNEL_CODE);
            }
        }
    }

    // 获取画框数据
    private void getFrameLayout() {
        new RxPermissions(MainActivity.this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean hasGranted) throws Exception {
                        if (hasGranted) {
                            presenter.getFrameLayoutList(MainActivity.this);
                        } else {
                            showToast(getString(R.string.stringPleaseGrantAppWritePermission));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getCreativeList() {
        if (presenter != null) {
            presenter.getCreativeList();
        }
    }

    //初始化个人信息
    private void initUserInfo() {
        if (presenter != null) {
            presenter.silentLogin();
        }
    }

    private CheckUpdateCallback checkUpdateCallback = new CheckUpdateCallback() {
        @Override
        public void onCheckUpdateCallback(com.baidu.aiupdatesdk.UpdateInfo updateInfo) {
            if (null != updateInfo) {
                final BaiduUpdateDialog updateBaiduDialog = new BaiduUpdateDialog(MainActivity.this);
                if (updateInfo.isForceUpdate()) {
                    updateBaiduDialog.setBtnCancelVisibity(View.GONE);
                }
                updateBaiduDialog.setCancelable(!updateInfo.isForceUpdate());
                updateBaiduDialog.setContent(Html.fromHtml(updateInfo.getChangeLog()));
                updateBaiduDialog.setBtnOKText("智能升级");
                updateBaiduDialog.setBtnCancelText("暂不升级");
                updateBaiduDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return true;
                        }
                        return false;
                    }
                });
                updateBaiduDialog.setForceUpdate(updateInfo.isForceUpdate());
                updateBaiduDialog.setBaseDialogOnclicklistener(new BaseDialog.BaseDialogOnclicklistener() {

                    @Override
                    public void onOkClick(Dialog dialog) {
                        AIUpdateSDK.updateDownload(MainActivity.this);
                        updateBaiduDialog.dismiss();
                    }

                    @Override
                    public void onCancleClick(Dialog dialog) {
                        updateBaiduDialog.dismiss();
                    }
                });
                updateBaiduDialog.show();
            }
        }
    };

    @Override
    public void checkSystemAppUpdateSucceed(final UpdateInfo updateInfo) {
        // TODO: 1/11/2018 发版时注释掉这里 !!!
//        if (GlobalParams.Config.IS_DEBUG) {
//            return;
//        }
        if (updateInfo != null && !TextUtils.isEmpty(updateInfo.getApp_url())) {
            mUpDateDialog = new UpDateDialog(MainActivity.this);
            mUpDateDialog.setTitle(updateInfo.getTips_title());
            if (updateInfo.isForce()) {
                mUpDateDialog.setBtnCancelVisibity(View.GONE);
            }
            mUpDateDialog.setContent(updateInfo.getTips_text());
            mUpDateDialog.setBtnOKText(getText(R.string.update_information_ok));
            mUpDateDialog.setCancelable(!updateInfo.isForce());
//            updateDialog.setButOKEnable(true);
            mUpDateDialog.setBaseDialogOnclicklistener(new BaseDialog.BaseDialogOnclicklistener() {

                @Override
                public void onOkClick(Dialog dialog) {
                    UpdateUtils.download(MainActivity.this, updateInfo.getApp_url(), updateInfo.getApp_name(), updateInfo.getFile_name());
                    if (!updateInfo.isForce()) {
                        mUpDateDialog.dismiss();
                    } else {
                        //强制升级
                        mUpDateDialog.setBtnOKText(getText(R.string.update_loading));
                    }
                }

                @Override
                public void onCancleClick(Dialog dialog) {
                    mUpDateDialog.dismiss();
                }
            });
            mUpDateDialog.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (homeFragment != null && homeFragment.onBackPressed()) {
                return true;
            }

            if (RecordManager.get().hasPublishingProduct()) {
                showToast("正在上传作品，请确保上传完成之后再退出应用");
                return true;
            }

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showToast("再按一次退出应用");
                exitTime = System.currentTimeMillis();
            } else {
                try {
                    Fresco.getImagePipeline().pause();
                    Fresco.shutDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                DCApplication.getDCApp().exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * token失效重新登录的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReLoginEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_RELOGIN) {
            KLog.i("======token失效重新登录的事件：" + eventEntity.data);
            homeFragment.showLatest(false);
            Activity activity = DCApplication.getDCApp().getCurrentActivity();
            //清除账号信息
            AccountUtil.clearAccount();
            DcWebSocketService.stopSocket(this);
            if (null != activity && (activity instanceof DcBaseActivity || activity instanceof BaseCompatActivity)) {
                if (activity instanceof MainActivity) {

                } else {
                    activity.finish();
                }

                if (homeFragment.getCurrentItem() == 0) {
                    homeFragment.showFollowPageRelogin();
                } else {
                    showReLogin();
//                    LoginDialog loginDialog = showReLogin();
//                    if (loginDialog != null) {
//                        loginDialog.setCloseListener(new LoginDialog.CloseListener() {
//                            @Override
//                            public void onClose() {
//                                if (homeFragment != null) {
//                                    homeFragment.setCurrentItem(1);
//                                }
//                            }
//                        });
//                    }
                }
            }
//            initHome();
        } else if (eventEntity.code == GlobalParams.EventType.TYPE_LOGIN_OK) {
            // 登陆成功，阿里云推送ID绑定用户
            bindJPushUser();
        }
    }

    //网络变化的事件result为0时正常，1是移动网络，2是无网络
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChange(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_NETWORK_CHANGE) {

            KLog.i("======网络发生变化：" + eventEntity.data);
            if ((int) (eventEntity.data) == 0 || (int) (eventEntity.data) == 1) {
                DcWebSocketService.startSocket(DCApplication.getDCApp(), 1500);
            }

            Activity activity = DCApplication.getDCApp().getCurrentActivity();
//        if (result == 1 && !GlobalParams.StaticVariable.sHasShowdAllowdMobile) {//只有再次切换到wifi网络后才重置
//            if (null != activity && (activity instanceof DcBaseActivity || activity instanceof BaseCompatActivity)) {
//                if (activity instanceof MainActivity) {//只有这两个页面需要弹窗
//                    ((MainActivity) activity).showUseMobileNetwork();
//                } else if (activity instanceof VideoListActivity) {
//                    ((VideoListActivity) activity).showUseMobileNetwork();
//                }
//            }
//            GlobalParams.StaticVariable.sHasShowdAllowdMobile = true;
//        }
            if ((int) (eventEntity.data) == 0) {//切换到wifi网络后
                // 如果之前没有播放，则开始播放
                if (!DcIjkPlayerManager.get().isPlaying()) {
                    if (null != activity && (activity instanceof DcBaseActivity || activity instanceof BaseCompatActivity)) {
                        if (activity instanceof MainActivity) {//只有这两个页面需要弹窗
                            if (HomeFragment.currentTabPosition == 0
                                    || HomeFragment.currentTabPosition == 1) {
//                                homeFragment.resumePlay();
                            }
                        } else if (activity instanceof VideoListActivity) {
                            ((VideoListActivity) activity).resumePlay();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        if (AccountUtil.isLogin()) {
            if (userAccountInfoPresenter != null) {
                userAccountInfoPresenter.getAccountInfo();
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventHelper.unregister(this);
        if (checkSplashRunnable != null) {
            getWeakHandler().removeCallbacks(checkSplashRunnable);
        }
        DcJobService.stopJobService(this);
        GiftManager.get().stopGiftService();
        DcWebSocketService.stopSocket(DCApplication.getDCApp());
        stopService(new Intent(this, DCService.class));
        if (aMapLocator != null) {
            aMapLocator.stopLocate();
            aMapLocator = null;
        }
        super.onDestroy();
    }

    @Override
    public void getUserInfo(long userId) {

    }

    @Override
    public void checkSystemAppUpdateFailure(String updateInfo) {

    }

    @Override
    public void initApiError(String msg) {

    }

    @Override
    public void initApiSucceed() {

    }

    @Override
    public void onInitComplete() {

    }

    @Override
    public void onCheckDeviceIdOk(CheckDeviceResponse response) {

    }

    private void disposeJPush() {
        try {
            String data = getIntent().getStringExtra(KEY_CONTENT);
            DcRouter.linkTo(this, data);
        } catch (Exception e) {

        }
    }


    @Override
    protected void protectApp() {
        KLog.i("应用被回收重启走流程");
//        SplashActivity.startSplashActivity(MainActivity.this);
    }

    @Override
    public void handleBindSucceed() {
        KLog.i("JpushReceiver", ">>>>bind user success>>>");
    }


    private void bindJPushUser() {
        //循环获取JPush ID
        getWeakHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO: 9/13/2017 发版时请注释掉，开启Instant run会崩溃
//                if (!GlobalParams.Config.IS_DEBUG) {
                String regId = PushServiceFactory.getCloudPushService().getDeviceId();
                KLog.i(">>>>regId:" + regId);
                if (!TextUtils.isEmpty(regId)) {
                    if (null != pushBindUserPresenter) {
                        pushBindUserPresenter.bindUser(GlobalParams.StaticVariable.sUniqueDeviceId, regId, 1, "aliyun");
                    }
                } else {
                    bindJPushUser();
                }
            }
        }, 2000);
    }

    private void getLocation() {
        aMapLocator = new AMapLocator(this)
                .setOnceLocation(true)
                .setLocateCallback(this);
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            aMapLocator.startLocate();
                        } else {
                            ToastUtil.showToast("定位权限禁止后该功能不能正常使用，请在应用程序管理中开启权限");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onLocateOk(LocationEntity entity) {
        if (entity != null) {
            HeaderUtils.updateLatlon(entity.latitude, entity.longitude, entity.city);
        }

    }

    @Override
    public void onLocateFailed() {

    }

    @Override
    public void handleInfoSucceed(UserAccountResponse response) {

    }

    @Override
    public void handleInfoFailure(String error_msg) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBellUpdateEvent(EventEntity entity) {
        if (entity.code == GlobalParams.EventType.TYPE_ALERT_SYSTEM_MSG) {
            showSystemMessage();
        }
    }

    private void showSystemMessage() {
        if (messageView != null && messageView.getVisibility() != View.VISIBLE) {
            MessageDetail messageDetail = MessageManager.get().pollSystemMessage();
            if (messageDetail != null) {
                if (MessageDetail.TYPE_IMAGE.equalsIgnoreCase(messageDetail.type)
                        || MessageDetail.TYPE_TOAST.equalsIgnoreCase(messageDetail.type)) {
                    messageView.showToastMessage(messageDetail);
                } else if (MessageDetail.TYPE_ALERT.equalsIgnoreCase(messageDetail.type)) {
                    messageView.showAlertMessage(messageDetail);
                }
            } else {
                messageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNextMessage() {
        showSystemMessage();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        KLog.i("=====key code:" + event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }

    private Runnable checkSplashRunnable = new Runnable() {
        @Override
        public void run() {
            if (presenter != null) {
                presenter.getLoadSplash();
                getWeakHandler().postDelayed(this, CHECK_SPLASH_INTERVAL);
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        DcIjkPlayerManager.get().sendUserBehavior();
    }

    public RelativeLayout getRootView() {
        return rlrootview;
    }
}
