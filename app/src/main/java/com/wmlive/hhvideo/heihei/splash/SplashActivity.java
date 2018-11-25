package com.wmlive.hhvideo.heihei.splash;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.VideoProxy;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.AppStatusManager;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.common.network.DCRequest;
import com.wmlive.hhvideo.heihei.beans.main.SplashResourceEntity;
import com.wmlive.hhvideo.heihei.beans.splash.CheckDeviceResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.beans.splash.InitUrlResponse;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.util.SharedPreferencesUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.splash.adapter.FmPagerAdapter;
import com.wmlive.hhvideo.heihei.splash.fragment.GuidePagerFragment;
import com.wmlive.hhvideo.heihei.splash.fragment.ImgGuidePagerFragment;
import com.wmlive.hhvideo.heihei.splash.presenter.SplashPresenter;
import com.wmlive.hhvideo.heihei.splash.view.ExtendedViewPager;
import com.wmlive.hhvideo.heihei.splash.view.SplashView;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DcDeviceHelper;
import com.wmlive.hhvideo.utils.DensityUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.observer.DcObserver;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.networklib.util.NetUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.magicwindow.MLinkAPIFactory;
import cn.magicwindow.MWConfiguration;
import cn.magicwindow.MagicWindowSDK;
import cn.magicwindow.mlink.MLinkCallback;
import cn.magicwindow.mlink.MLinkIntentBuilder;
import cn.magicwindow.mlink.YYBCallback;
import cn.wmlive.hhvideo.BuildConfig;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends DcBaseActivity<SplashPresenter> implements SplashView {

    private static final String TAG = SplashActivity.class.getSimpleName();
    @BindView(R.id.ivSlogan)
    ImageView ivSlogan;
    @BindView(R.id.svAdvert)
    SimpleDraweeView svAdvert;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.imageGuide)
    ImageView imageGuide;
    @BindView(R.id.tvCopyright)
    TextView tvCopyright;
    @BindView(R.id.tvSkip)
    TextView tvSkip;
    @BindView(R.id.guide_rl)
    RelativeLayout guideRl;
    @BindView(R.id.vp_guide)
    ExtendedViewPager vpGuide;
    @BindView(R.id.ll_dot)
    LinearLayout llDot;
    @BindView(R.id.tv_enter)
    TextView tvEnter;
    @BindView(R.id.rlGuide)
    RelativeLayout rlGuide;
    @BindView(R.id.btStart)
    Button btStart;
    private boolean isLaunchError;//启动失败
    private long showTime;
    private boolean hasShowedAdvert;
    private boolean isShowGuide;
    private boolean clickGotoPage; // 点击跳转页面
    private boolean hasInitComplete; // 初始化完成
    private SplashResourceEntity splashResourceEntity;
    private Disposable advertDisposable;
    private Disposable initDisposable;
    //首次安装引导页
    private FmPagerAdapter pagerAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private int[] videoRes = new int[]{R.raw.guide1};
    private int[] imgRes = new int[]{R.raw.guide1};
    private LinearLayout.LayoutParams params1, params2;
    private int versionCode = 0;

    public static void startSplashActivity(Context context) {
        MyAppActivityManager.getInstance().clear();
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return DCApplication.isPendingKillApp() ? 0 : R.layout.activity_splash;
    }

    @Override
    protected SplashPresenter getPresenter() {
        return new SplashPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        KLog.d(TAG, "onCreate: ");
        AppStatusManager.getInstance().setAppStatus(AppStatusManager.STATUS_NORMAL);
        RecordManager.get().init(DCApplication.getDCApp(), GlobalParams.Config.IS_DEBUG);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        GlobalParams.StaticVariable.sReleaseEnvironment = (boolean) SharedPreferencesUtils.getParam(this, "sReleaseEnvironment", false);
        super.onCreate(savedInstanceState);

    }

    private void showGuide() {
        rlGuide.setVisibility(View.VISIBLE);
        btStart.setOnClickListener(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        float width = (float) metric.widthPixels;  // 屏幕宽度（像素）
        float height = (float) metric.heightPixels;  // 屏幕高度（像素）
        if (width / height < 0.52) {
            imageGuide.setImageResource(R.drawable.guide_bg_narrow);
        }
    }

    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    @Override
    protected void initData() {
        if (!DCApplication.isPendingKillApp()) {
            super.initData();
//            changeDecorView(4);


//            if(version>SPUtils.getInt(this, SPUtils.VERSIONCODE, 0)){
//            }else{
//                initVideoGuide();
            initLogo();
//            }

            int version = packageCode(this);
            if (version > SPUtils.getInt(this, SPUtils.VERSIONCODE, 0)) {
                isShowGuide = true;
            } else {
                initLogo();
//                initVideoGuide();
                isShowGuide = false;
            }
            new RxPermissions(this)
                    .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                initFileAndNetwork();
                            } else {
                                initApiError("请允许动次必要的权限");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            initApiError("请允许动次必要的权限");
                        }
                    });

        }
        if (tvSkip != null) {
            tvSkip.setBackground(getResources().getDrawable(R.drawable.bg_skip_transparent_shape));
            tvSkip.setOnClickListener(this);
        }

    }

    private void initLogo() {
        tvCopyright.setText(String.format(Locale.getDefault(), getString(R.string.stringSplashCopyright),
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
        int imageId;
        KLog.i("=====current channel is " + GlobalParams.StaticVariable.CHANNEL_CODE);
        switch (GlobalParams.StaticVariable.CHANNEL_CODE) {
            case GlobalParams.Config.CHN_360:
                imageId = R.drawable.ic_logo_360;
                break;
            case GlobalParams.Config.CHN_BAIDU:
                imageId = R.drawable.ic_logo_baidu;
                break;
            case GlobalParams.Config.CHN_HUAWEI:
                imageId = R.drawable.ic_logo_huawei;
                break;
            default:
                imageId = R.drawable.bg_default_logo;
                break;
        }
        ivLogo.setImageResource(imageId);
    }

    @Override
    protected void onResume() {

        super.onResume();

//        int version = packageCode(this);
//        if(version>SPUtils.getInt(this, SPUtils.VERSIONCODE, 0)){
//        return;
//        }


        if (isShowGuide) {
            getWeakHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showGuide();
                }
            }, 3000);
        }

        if (!isShowGuide) {
            getWeakHandler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!isShowGuide) {
                        tvSkip.setVisibility(View.VISIBLE);
                    }

                    if (!DCApplication.isPendingKillApp() && !hasShowedAdvert) {
                        hasShowedAdvert = true;
                        advertDisposable = GiftManager.get().getAdvertResource()
                                .subscribe(new Consumer<SplashResourceEntity>() {
                                    @Override
                                    public void accept(SplashResourceEntity entity) throws Exception {
                                        KLog.i("=======需要显示的图片是：" + entity.toString());
                                        splashResourceEntity = entity;
                                        if (splashResourceEntity != null && !TextUtils.isEmpty(splashResourceEntity.localPath)) {
                                            ivSlogan.setVisibility(View.GONE);
                                            svAdvert.setVisibility(View.VISIBLE);
                                            svAdvert.setOnClickListener(SplashActivity.this);
                                            if ("webp".equalsIgnoreCase(splashResourceEntity.media_type)) {
                                                showTime = (splashResourceEntity.show_time + 2) * 1000;
                                                Uri uri = new Uri.Builder()
                                                        .scheme(UriUtil.LOCAL_FILE_SCHEME)
                                                        .path(splashResourceEntity.localPath)
                                                        .build();
                                                svAdvert.setController(Fresco.newDraweeControllerBuilder()
                                                        .setUri(uri)
                                                        .setAutoPlayAnimations(true)
                                                        .build());
                                            }
                                        } else {
                                            ivSlogan.setVisibility(View.VISIBLE);
                                            svAdvert.setVisibility(View.GONE);
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        KLog.i("======getAdvertResource Exception：" + throwable.getMessage());
                                        ivSlogan.setVisibility(View.VISIBLE);
                                        svAdvert.setVisibility(View.GONE);
                                    }
                                });
                    }
                }
            }, 1000);
        }
    }


    private void checkAppVersion() {
        String newVersion = SPUtils.getString(DCApplication.getDCApp(), SPUtils.KEY_CURRENT_APP_VERSION, null);
        if (!TextUtils.isEmpty(newVersion)) {
            if (!newVersion.equalsIgnoreCase(GlobalParams.Config.APP_VERSION)) {
                SPUtils.putString(DCApplication.getDCApp(), SPUtils.KEY_OLD_APP_VERSION, newVersion);
                SPUtils.putString(DCApplication.getDCApp(), SPUtils.KEY_CURRENT_APP_VERSION, GlobalParams.Config.APP_VERSION);
                KLog.i("====app从版本" + newVersion + "升级到" + GlobalParams.Config.APP_VERSION);
            }
        } else {
            SPUtils.putString(DCApplication.getDCApp(), SPUtils.KEY_CURRENT_APP_VERSION, GlobalParams.Config.APP_VERSION);
        }
    }

    private void initFileAndNetwork() {
        initDisposable = Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Integer integer) throws Exception {
                        checkAppVersion();
                        return AppCacheFileUtils.isAppCacheDirectoryInit(DCApplication.getDCApp());
                    }
                })
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean dirOk) throws Exception {
                        if (!dirOk) {
                            KLog.i("初始化app缓存目录");
                            AppCacheFileUtils.initAppInternalCacheDirectory(DCApplication.getDCApp());
                            AppCacheFileUtils.initAppInternalFilesDirectory(DCApplication.getDCApp());
                            AppCacheFileUtils.initAppExternalDirectory();
                        }
                        return true;
                    }
                })
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {
                        DCRequest.getInstance().initRxJavaRetrofit(DCApplication.getDCApp(),
                                GlobalParams.Config.IS_DEBUG,
                                AppCacheFileUtils.getAppHttpCachePath(),
                                GlobalParams.Config.APP_LOG_TAG);

                        InitCatchData.getInitCatchData();
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        //初始化视频缓存库
                        KLog.i("=====初始化视频缓存库");
                        initMagicWindow();
//                        DcIjkPlayerManager.get().init(DCApplication.getDCApp(), GlobalParams.Config.IS_DEBUG);
                        checkDeviceId();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        isLaunchError = true;
                        svAdvert.setVisibility(View.GONE);
                        KLog.i("=====initFileAndNetwork fail:" + throwable.getMessage());
//                        showStatusPage(2, "\n请稍后尝试重启App\n或者检查手机设置");
//                        rlGuide.setVisibility(View.GONE);
                    }
                });
    }


    private void initMagicWindow() {
        MWConfiguration config = new MWConfiguration(this);
        config.setLogEnable(GlobalParams.Config.IS_DEBUG);
        MagicWindowSDK.initSDK(config);
        MLinkAPIFactory.createAPI(this).registerWithAnnotation(this);
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.svAdvert:
                if (splashResourceEntity != null && !TextUtils.isEmpty(splashResourceEntity.link)) {
                    KLog.i("xxxx", "linkTo " + splashResourceEntity.link);

                    boolean isSuccess = DcRouter.linkTo(SplashActivity.this, splashResourceEntity.link, true, "splashpage");
                    if (isSuccess) {
                        clickGotoPage = true;
                        getWeakHandler().removeCallbacksAndMessages(null);
                        finish();
                    }
                }
                break;
            case R.id.tvSkip:
            case R.id.btStart:
                clickGotoPage = true;
                getWeakHandler().removeCallbacksAndMessages(null);
                if (hasInitComplete || NetUtil.getNetworkState(this) != 2) {
                    Log.i("tag", "autoskip-click");
                    MainActivity.startMainActivity(this);
                    Log.i(TAG, "===yang onSingleClick");
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void requestInitData() {
        InitUrlResponse initResponse = InitCatchData.getInitCatchData(false);
        if (initResponse != null && !TextUtils.isEmpty(initResponse.getVersion())) {
            GlobalParams.StaticVariable.sApiServiceVersion = initResponse.getVersion();
            onInitComplete();
        } else {
            isLaunchError = false;
            KLog.i("====从网络获取Init数据");
            presenter.init();
        }
    }

    private void checkDeviceId() {
        isLaunchError = false;
        String oldDeviceId;
        String newDeviceId = SPUtils.getString(this, SPUtils.KEY_NEW_UNIQUE_DEVICE_ID, null);
        KLog.i("checkDeviceId", "====newDeviceId==" + newDeviceId);
        if (!TextUtils.isEmpty(newDeviceId)) {
            KLog.i("====本地存的id正确，且经过服务器检查:" + newDeviceId);
            saveDeviceId(newDeviceId);
            requestInitData();
            return;
        } else {
            newDeviceId = DcDeviceHelper.getDeviceId(this);
            if (!TextUtils.isEmpty(newDeviceId)) {
                KLog.i("====本地备份id存在：" + newDeviceId);
                saveDeviceId(newDeviceId);
                requestInitData();
                return;
            } else {
                KLog.i("====本地备份id不存在：" + newDeviceId);
                oldDeviceId = DCApplication.getDCApp().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                        .getString(SPUtils.KEY_UNIQUE_DEVICE_ID, null);
                if (TextUtils.isEmpty(oldDeviceId)) {
                    oldDeviceId = SPUtils.getString(this, SPUtils.KEY_UNIQUE_DEVICE_ID, null);
                }
                if (!TextUtils.isEmpty(oldDeviceId)) {
                    newDeviceId = DcDeviceHelper.createRandomUUID();
                    KLog.i("====本地SP存过旧的id:" + oldDeviceId + " ,生成一个新的id:" + newDeviceId);
                } else {
                    oldDeviceId = DeviceUtils.getDeviceId();
                    newDeviceId = DcDeviceHelper.createRandomUUID();
                    KLog.i("====本地SP没存过id，可能是老版本，老版本生成一个：" + oldDeviceId + " ,新版本id:" + newDeviceId);
                }
            }
        }
        presenter.checkDeviceId(oldDeviceId, newDeviceId);
    }

    @Override
    public void initApiError(String msg) {
        isLaunchError = true;
        KLog.i("0.0.0.0--》initApiError-PRE00->" + InitCatchData.getInitCatchData());
        if (InitCatchData.getInitCatchData() == null) {
            KLog.i("0.0.0.0--》initApiError-PRE");
            CommonUtils.parseLocalApi().subscribe(new DcObserver<>());
            KLog.i("0.0.0.0--》initApiError");
        }
        if (TextUtils.isEmpty(GlobalParams.StaticVariable.sUniqueDeviceId)) {
            saveDeviceId(DcDeviceHelper.createRandomUUID());
        }
//        showStatusPage(2, msg + "\n请稍后尝试重启App\n或者检查手机网络设置");
//        rlGuide.setVisibility(View.GONE);
        getWeakHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!SplashActivity.this.isDestroyed()) {

                    if (!isShowGuide) {
                        MainActivity.startMainActivity(SplashActivity.this);
                        finish();
                    }
                    Log.i(TAG, "===yang initApiError");
                }
            }
        }, 1000);
    }

    @Override
    public void initApiSucceed() {
        KLog.i("所有初始化接口 加载成功");
        GlobalParams.StaticVariable.sInitFromLocal = false;
    }

    @Override
    public void onInitComplete() {
        KLog.i("所有初始化接口 加载完成");

        hasInitComplete = true;
        if (!SharedPreferencesUtils.isFirstStart(this)) {
            if (clickGotoPage) {
                finish();
                return;
            }
            getWeakHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getIntent() != null) {
                        if (getIntent().getData() != null) {
                            Uri uri = getIntent().getData();
                            KLog.i("MLink", "======onInitComplete mw router uri:" + (uri == null ? "null" : uri.toString()));

                            String path = uri.getPath();
                            switch (path) {
                                case "/action/creative":
                                case "/action/multigrid":
                                case "/action/jointeamwork":
                                    MainActivity.startMainActivity(SplashActivity.this, uri.toString());
                                    break;
                                default:
                                    MLinkAPIFactory.createAPI(SplashActivity.this).router(uri);
//                                    DcRouter.linkTo(SplashActivity.this, uri.toString());
                            }
                            finish();
                        } else {
                            //以下是支持应用宝下载直跳
                            MLinkAPIFactory.createAPI(SplashActivity.this)
                                    .checkYYB(SplashActivity.this, new YYBCallback() {
                                        @Override
                                        public void onFailed(Context context) {
                                            KLog.i("MLink", "======checkYYB onFailed");

                                            if (!isShowGuide) {
                                                MainActivity.startMainActivity(SplashActivity.this);
                                                SplashActivity.this.finish();
                                            }
                                            KLog.i("MLink", "===yang onInitComplete");
                                        }

                                        @Override
                                        public void onSuccess() {
                                            SplashActivity.this.finish();
                                            KLog.i("MLink", "======checkYYB onSuccess");
                                        }
                                    });
                        }
                    }
                }
//            }, showTime > 0 ? showTime : 10);
            }, 3000);
        }
    }

    @Override
    public void onCheckDeviceIdOk(CheckDeviceResponse response) {
        if (response != null
                && !TextUtils.isEmpty(response.device_id)) {
            saveDeviceId(response.device_id);
            requestInitData();
        } else {
            initApiError("请检查网络设置");
        }
    }

    private void saveDeviceId(String deviceId) {
        KLog.i("====将deviceI保存到本地:" + deviceId);
        GlobalParams.StaticVariable.sUniqueDeviceId = deviceId;
        SPUtils.putString(this, SPUtils.KEY_NEW_UNIQUE_DEVICE_ID, deviceId);
        DcDeviceHelper.directWriteId(this, deviceId);
    }

    /**
     * 初始化视频引导页
     */
    private void initVideoGuide() {
        if (SharedPreferencesUtils.isFirstStart(this)) {
            guideRl.setVisibility(View.VISIBLE);
            tvEnter.setVisibility(View.INVISIBLE);
            vpGuide.setOffscreenPageLimit(4);
            for (int i = 0; i < videoRes.length; i++) {
                GuidePagerFragment fragment = new GuidePagerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("res", videoRes[i]);
                bundle.putInt("page", i);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }
            pagerAdapter = new FmPagerAdapter(fragments, getSupportFragmentManager());
            vpGuide.setAdapter(pagerAdapter);
        }
    }

    /**
     * 初始化图片引导页
     */
    private void initImgGuide() {
        if (SharedPreferencesUtils.isFirstStart(this)) {
            guideRl.setVisibility(View.VISIBLE);
            tvEnter.setVisibility(View.INVISIBLE);
            vpGuide.setOffscreenPageLimit(4);
            for (int i = 0; i < imgRes.length; i++) {
                ImgGuidePagerFragment fragment = new ImgGuidePagerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("res", videoRes[i]);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }
            pagerAdapter = new FmPagerAdapter(fragments, getSupportFragmentManager());
            vpGuide.setAdapter(pagerAdapter);
            tvEnter.setOnClickListener(v -> {
                //进入主页面 
                if (hasInitComplete) {


                    if (!isShowGuide) {
                        MainActivity.startMainActivity(SplashActivity.this);
                        SplashActivity.this.finish();
                    }
                    Log.i(TAG, "===yang initImgGuide");
                }
            });
        }
        initDot();
        setPageChange();
    }

    /**
     * 创建小圆点
     */
    private void initDot() {
        params1 = new LinearLayout.LayoutParams(DensityUtil.dip2px(getApplicationContext(), 7), DensityUtil.dip2px(getApplicationContext(), 7));
        params1.leftMargin = DensityUtil.dip2px(getApplicationContext(), 15);
        params2 = new LinearLayout.LayoutParams(DensityUtil.dip2px(getApplicationContext(), 10), DensityUtil.dip2px(getApplicationContext(), 10));
        params2.leftMargin = DensityUtil.dip2px(getApplicationContext(), 15);
        View dot;
        for (int i = 0; i < videoRes.length; i++) {
            dot = new View(this);
            if (i == 0) {
                dot.setLayoutParams(params2);
                dot.setBackgroundResource(R.drawable.dot_focus);
            } else {
                dot.setLayoutParams(params1);
                dot.setBackgroundResource(R.drawable.dot_unfocus);
            }
            llDot.addView(dot);
        }
    }

    /**
     * 根据viewPager项切换指示点位置
     */
    private void setCurrentdot(int position) {
        for (int i = 0; i < llDot.getChildCount(); i++) {
            View dot = llDot.getChildAt(i);
            if (i == position) {
                dot.setLayoutParams(params2);
                dot.setBackgroundResource(R.drawable.dot_focus);
            } else {
                dot.setLayoutParams(params1);
                dot.setBackgroundResource(R.drawable.dot_unfocus);
            }

        }
    }

    private void setPageChange() {
        vpGuide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentdot(position);
                if (position == 2) {
                    tvEnter.setVisibility(View.VISIBLE);
                    setShowAnimation(tvEnter, 5000);
                } else {
                    tvEnter.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * View渐现动画效果
     */
    public void setShowAnimation(View view, int duration) {
        if (null == view || duration < 0) {
            return;
        }
        AlphaAnimation mShowAnimation = null;
        if (null != mShowAnimation) {
            mShowAnimation.cancel();
        }
        mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        view.startAnimation(mShowAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (advertDisposable != null && !advertDisposable.isDisposed()) {
            advertDisposable.dispose();
        }
        advertDisposable = null;
        if (initDisposable != null && !initDisposable.isDisposed()) {
            initDisposable.dispose();
        }
        initDisposable = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isLaunchError) {
            finish();
            DCApplication.getDCApp().exitApp();
        } else {
            //启动正常，屏蔽back事件
        }
        return true;
    }

}
