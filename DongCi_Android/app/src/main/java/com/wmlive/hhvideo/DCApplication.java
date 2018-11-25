package com.wmlive.hhvideo;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tendcloud.tenddata.TCAgent;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.common.manager.greendao.GreenDaoManager;
import com.wmlive.hhvideo.common.receiver.NetWorkStatusReceiver;
import com.wmlive.hhvideo.fresco.FrescoConfigConstants;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.HeaderUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ThreadManager;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.WeakHandler;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

import java.net.Proxy;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.magicwindow.Session;
import cn.wmlive.hhvideo.BuildConfig;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Applicatioin
 * Created by vhawk on 2017/5/22.
 * Modify by lsq
 */

public class DCApplication extends MultiDexApplication implements Handler.Callback {

    private static final int TYPE_EXIT = 100;
    private static final String TAG = "DcAppLog";

    private static boolean sPendingKillApp = false;
    private static DCApplication sContext;

    private WeakHandler weakHandler;

    private Activity currentActivity;  //当前Activity
    private NetWorkStatusReceiver netWorkStateReceiver;
    private boolean inBackground;
    private long latestBackgroundTime;

    private String bugly = "9d068ada48";
    private String bugly_debug = "96a24ced6a";

    @Override
    public void onCreate() {
        super.onCreate();
        if (sPendingKillApp) {
            Log.i(TAG, "======DCApplication is pending exit");
            return;
        } else {
            if (BuildConfig.DEBUG_SWITCH) {
                Log.i(TAG, "======DCApplication onCreate package name: " + ThreadManager.getCurrentProcessName(this));
            }
        }

        //如果app存在多进程，Application的onCreate()会执行多次
        if (BuildConfig.APPLICATION_ID.equals(ThreadManager.getCurrentProcessName(this))) {

            sContext = this;

            GlobalParams.StaticVariable.sStartTimestamp = System.currentTimeMillis();

            latestBackgroundTime = System.currentTimeMillis();

            initChannelCode();

            initBugtags();
            //初始化frecos参数信息
            initFresco();

            // 初始化talkingdata
            if (BuildConfig.DEBUG_SWITCH) {
                TCAgent.LOG_ON = true;
                TCAgent.init(this, "4AFF6170541F45019FF4B948CD210954", HeaderUtils.getChannel());
                TCAgent.setReportUncaughtExceptions(true);
            } else {
                TCAgent.LOG_ON = false;
                TCAgent.init(this, "162E8DEAB17746E7B89099CFF5441038", HeaderUtils.getChannel());
                TCAgent.setReportUncaughtExceptions(true);
            }

            LeakCanary.install(this); //内存泄漏分析

            CrashReport.initCrashReport(getApplicationContext(), GlobalParams.Config.IS_DEBUG?bugly_debug:bugly, GlobalParams.Config.IS_DEBUG);

            //初始化Log
            KLog.init(GlobalParams.Config.IS_DEBUG, GlobalParams.Config.APP_LOG_TAG);

            //初始化SharedPreferences
            SPUtils.init("dc_encrypt_key", "dc_preferences");

            //初始化Toast
            ToastUtil.init(this);

            weakHandler = new WeakHandler(this);

            GreenDaoManager.get().init(this);

            //app 状态切换在API 14之后，在Application类中
            registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());

            registerNetStateReceiver();

            //用于弹幕库图片加载
//            initImageLoader();

            //初始化下载信息
            initFileDownloader();

//            loadUserInfo();

        } else {
            //非嘿嘿的进程
            Log.e(TAG, "A new application start:" + ThreadManager.getCurrentProcessName(this) + "------------------");
        }

        // 移动推送在初始化过程中将启动后台进程channel，必须保证应用进程和channel进程都执行到推送初始化代码
//        if (!GlobalParams.Config.IS_DEBUG) {
        initCloudChannel(this);
    }

    private void loadUserInfo() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        AccountUtil.isLogin();
                        return AccountUtil.getLoginUserInfo() != null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }

    /**
     * 初始化渠道号
     */
    public void initChannelCode() {
        ApplicationInfo appinfo = sContext.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF/dccc_")) {
                    String[] split = entryName.split("_");
                    if (split != null && split.length >= 2) {
                        GlobalParams.StaticVariable.CHANNEL_CODE = entryName.substring(split[0].length() + 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void initImageLoader() {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions
                .Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageForEmptyUri(R.drawable.ic_default_male)
                .showImageOnFail(R.drawable.ic_default_male)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(32 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .threadPoolSize(5)
                .defaultDisplayImageOptions(displayImageOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }


    @Override
    public void onTerminate() {
        unregisterReceiver(netWorkStateReceiver);
        super.onTerminate();
    }

    /**
     * 初始化文件的缓存目录
     */


    private void initBugtags() {
        //在这里初始化BugTagst
        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(true).       //是否获取位置，默认 true
                trackingCrashLog(false).       //是否收集闪退，默认 true
                trackingConsoleLog(false).     //是否收集控制台日志，默认 true
                trackingUserSteps(true).      //是否跟踪用户操作步骤，默认 true
                crashWithScreenshot(true).    //收集闪退是否附带截图，默认 true
                versionName(BuildConfig.VERSION_NAME).         //自定义版本名称，默认 app versionName
                versionCode(BuildConfig.VERSION_CODE).              //自定义版本号，默认 app versionCode
                trackingNetworkURLFilter("(.*)").//自定义网络请求跟踪的 url 规则，默认 null
                enableUserSignIn(true).            //是否允许显示用户登录按钮，默认 true
                startAsync(true).    //设置 为 true 则 SDK 会在异步线程初始化，节省主线程时间，默认 false
                startCallback(null).            //初始化成功回调，默认 null
                remoteConfigDataMode(Bugtags.BTGDataModeProduction).//设置远程配置数据模式，默认Bugtags.BTGDataModeProduction 参见[文档](https://docs.bugtags.com/zh/remoteconfig/android/index.html)
                remoteConfigCallback(null).//设置远程配置的回调函数，详见[文档](https://docs.bugtags.com/zh/remoteconfig/android/index.html)
                enableCapturePlus(false).        //是否开启手动截屏监控，默认 false，参见[文档](https://docs.bugtags.com/zh/faq/android/capture-plus.html)
                //extraOptions(key,value).                //设置 log 记录的行数，详见下文
                        build();
        if (GlobalParams.Config.IS_DEBUG) {
            Bugtags.start("c18456ca0308f5040dfbd13912e97aa6", sContext, Bugtags.BTGInvocationEventBubble, options);
        } else {
            Bugtags.start("c18456ca0308f5040dfbd13912e97aa6", sContext, Bugtags.BTGInvocationEventNone, options);
        }
        //      Bugtags.BTGInvocationEventBubble,在app中显示圆形小球
        //      Bugtags.BTGInvocationEventNone,在app中不显示圆形小球
        //      Bugtags.BTGInvocationEventShake,通过摇一摇让圆形小球显示出来

    }

    /**
     * 初始化下载信息
     */
    private void initFileDownloader() {
        FileDownloader.init(getApplicationContext(), new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15000)
                        .readTimeout(15000)
                        .proxy(Proxy.NO_PROXY)
                )));
    }

    public static DCApplication getDCApp() {
        return sContext;
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case TYPE_EXIT:
                weakHandler.removeCallbacksAndMessages(null);
                weakHandler = null;
                KLog.i("--退出App");
                Session.onKillProcess();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 完全退出App
     */
    public void exitApp() {
        if (weakHandler == null) {
            weakHandler = new WeakHandler(this);
        }
        sPendingKillApp = true;
        DcIjkPlayerManager.get().pausePlay();
        weakHandler.sendEmptyMessageDelayed(TYPE_EXIT, 2000);
    }

    public static boolean isPendingKillApp() {
        return sPendingKillApp;
    }

    /**
     * 初始化freco参数信息
     */
    private void initFresco() {
        Fresco.shutDown();
        FrescoConfigConstants.initialize(this);
    }


    int count = 0;//监听是否在前台，限制极光推送

    public class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            MyAppActivityManager.getInstance().pushActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (inBackground && (System.currentTimeMillis() - latestBackgroundTime) > 30_000) {
                KLog.i("======inBackground=记录一次启动");
            }
            count = count + 1;
            inBackground = (count == 0);
            if (count > 0) {
                // TODO: 9/13/2017 发版时请注释掉，开启Instant run会崩溃
                if (!GlobalParams.Config.IS_DEBUG) {
                    PushServiceFactory.getCloudPushService().turnOffPushChannel(null);
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            currentActivity = activity;
            try {
                if (Fresco.hasBeenInitialized() && Fresco.getImagePipeline().isPaused()) {
                    //恢复图片请求
                    Fresco.getImagePipeline().resume();
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            currentActivity = null;
            try {
                if (Fresco.hasBeenInitialized() && !Fresco.getImagePipeline().isPaused()) {
                    //暂停图片请求
                    Fresco.getImagePipeline().pause();
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            count = count - 1;
            latestBackgroundTime = System.currentTimeMillis();
            inBackground = (count == 0);
            if (count <= 0) {
                // TODO: 9/13/2017 发版时请注释掉，开启Instant run会崩溃
                if (!GlobalParams.Config.IS_DEBUG) {
                    PushServiceFactory.getCloudPushService().turnOnPushChannel(null);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            MyAppActivityManager.getInstance().popActivity(activity);
        }
    }

    public boolean isInBackground() {
        return inBackground;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }


    private void registerNetStateReceiver() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStatusReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
    }

    /**
     * 初始化阿里云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        KLog.i(TAG, ">>>init cloudchannel>>>");
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                KLog.i(TAG, ">>>init cloudchannel success>>>：" + response);
                pushService.bindAccount("test", new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i(TAG, ">>>bind user success>>>" + s);
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        KLog.i(TAG, ">>>bind user onFailed>>>" + s + ">>>" + s1);
                    }
                });
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                KLog.e(TAG, ">>>init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        MiPushRegister.register(applicationContext, "2882303761517591645", "5581759159645");
        HuaWeiRegister.register(applicationContext);
//        GcmRegister.register(applicationContext, "send_id", "application_id");
    }
}
