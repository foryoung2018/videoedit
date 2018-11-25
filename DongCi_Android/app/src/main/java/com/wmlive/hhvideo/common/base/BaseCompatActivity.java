package com.wmlive.hhvideo.common.base;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.bugtags.library.Bugtags;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.web.WeiboPageUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.AppStatusManager;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.WeakHandler;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.dialog.CustomProgressDialog;
import com.wmlive.hhvideo.widget.dialog.LoginDialog;
import com.wmlive.networklib.util.EventHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.magicwindow.Session;
import cn.wmlive.hhvideo.R;
import cn.wmlive.hhvideo.wxapi.WbPresenter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 5/27/2017.
 * Activity基类，不含根布局
 */
public abstract class BaseCompatActivity<P extends IBasePresenter>
        extends AppCompatActivity
        implements View.OnClickListener, BaseView, WbPresenter.IWeiboView, WbShareCallback {

    private Set<IBasePresenter> presenterList = new HashSet<>();

    public final String requestTag = getClass().getSimpleName();
    protected P presenter;
    private WeakHandler weakHandler;
    private long lastClickTime;
    //上次点击的控件Id
    private int lastViewId;
    protected Unbinder unbinder;
    protected CustomProgressDialog customProgressDialog;
    private LoginDialog loginDialog;
    public static ShareInfo shareInfo;//这里为了微信分享

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DCApplication.isPendingKillApp()) {
            finish();
        }
        switch (AppStatusManager.getInstance().getAppStatus()) {
            //应用被强杀
            case AppStatusManager.STATUS_FORCE_KILLED:
//                protectApp();
//                break;
                //应用正常启动,在splashactivity中设置
            case AppStatusManager.STATUS_NORMAL:
            default:
                if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
                    finish();
                    return;
                }
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_MODE_OVERLAY);  //防止在WebView中长按复制出现标题栏显示错误
                KLog.i("############_" + requestTag + "_onCreate");
                int layoutId = getLayoutResId();
                if (layoutId > 0) {
                    getDelegate().setContentView(layoutId);
                    unbinder = ButterKnife.bind(this);
                }
                initBaseView();
                presenter = getPresenter();
                addPresenter(presenter);
                initData();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    MyAppActivityManager.getInstance().pushActivity(this);
                }
                break;
        }
        getWeakHandler();
    }

    /**
     * 控制statusbar和toolbar的显示
     *
     * @param status 0：正常显示toolbar和statubar
     * 1：隐藏toolbar，显示statubar
     * 2：隐藏toolbar和statubar
     * 3：显示toolbar，内容沉浸到statubar（5.0以上有效）
     * 4：隐藏toolbar，内容沉浸到statubar（5.0以上有效）
     */

    public static final byte STATUS_NORMAL = 0;
    public static final byte STATUS_SHOW_TOOLBAR_HIDE_STATUSBAR = 1;
    public static final byte STATUS_HIDE_TOOLBAR_SHOW_STATUSBAR = 2;
    public static final byte STATUS_SHOW_TOOLBAR_INFILTRATE_STATUSBAR = 3;
    public static final byte STATUS_HIDE_TOOLBAR_INFILTRATE_STATUSBAR = 4;

    public void changeDecorView(int status) {
        int option = 0;
        switch (status) {
            case STATUS_NORMAL:
                option = View.SYSTEM_UI_FLAG_VISIBLE;
                break;
            case STATUS_SHOW_TOOLBAR_HIDE_STATUSBAR:
                option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                break;
            case STATUS_HIDE_TOOLBAR_SHOW_STATUSBAR:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    option = View.SYSTEM_UI_FLAG_FULLSCREEN;
                }
                break;
            case STATUS_SHOW_TOOLBAR_INFILTRATE_STATUSBAR:
            case STATUS_HIDE_TOOLBAR_INFILTRATE_STATUSBAR:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                }
                break;
            default:
                break;
        }
        getWindow().getDecorView().setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(status == STATUS_SHOW_TOOLBAR_INFILTRATE_STATUSBAR
                    || status == STATUS_HIDE_TOOLBAR_INFILTRATE_STATUSBAR ?
                    Color.TRANSPARENT : getResources().getColor(R.color.colorPrimaryDark));
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (status == STATUS_HIDE_TOOLBAR_INFILTRATE_STATUSBAR
                    || status == STATUS_HIDE_TOOLBAR_SHOW_STATUSBAR
                    || status == STATUS_SHOW_TOOLBAR_HIDE_STATUSBAR) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    /**
     * 弹Toast并且关闭页面
     */
    public void toastFinish() {
        showToast(R.string.hintErrorDataDelayTry);
        finish();
    }

    /**
     * 安全地弹toast
     *
     * @param stringId
     */
    protected void showToast(int stringId) {
        showToast(getString(stringId));
    }

    /**
     * 安全地弹toast
     *
     * @param message
     */
    protected void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                ToastUtil.showToast(message);
            }
        });
    }

    /**
     * 获取一个WeakHandler，用来替换Handler
     *
     * @return
     */
    public WeakHandler getWeakHandler() {
        if (weakHandler == null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                Looper.prepare();
                weakHandler = new WeakHandler();
            } else {
                weakHandler = new WeakHandler();
            }
        }
        return weakHandler;
    }


    @Override
    protected void onStart() {
        super.onStart();
        for (IBasePresenter presenter : presenterList) {
            if (null != presenter) {
                presenter.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.i("==========" + requestTag + "——onResume");
        for (IBasePresenter presenter : presenterList) {
            if (null != presenter) {
                presenter.resume();
            }
        }
        Bugtags.onResume(this);
        Session.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        KLog.i("==========" + requestTag + "——onPause");
        Bugtags.onPause(this);
        Session.onPause(this);
        DeviceUtils.hiddenKeyBoard(findViewById(android.R.id.content));
        for (IBasePresenter presenter : presenterList) {
            if (null != presenter) {
                presenter.pause();
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStop() {
        super.onStop();
        KLog.i("==========" + requestTag + " _onStop");
        for (IBasePresenter presenter : presenterList) {
            if (null != presenter) {
                presenter.stop();
            }
        }
    }

    @Override
    protected void onDestroy() {
        removeWeakHandler();
        Iterator<IBasePresenter> iterator = presenterList.iterator();
        while (iterator.hasNext()) {
            iterator.next().destroy();
            iterator.remove();
        }
        if (loginDialog != null) {
            if (loginDialog.isShowing()) {
                loginDialog.dismiss();
            }
            loginDialog = null;
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = null;

        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        dismissLoad();
        KLog.i("==========" + requestTag + "_onDestroy");
        super.onDestroy();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            MyAppActivityManager.getInstance().popActivity(this);
        }
    }

    /**
     * 移除weakHandler的消息
     */
    public void removeWeakHandler() {
        if (weakHandler != null) {
            weakHandler.removeCallbacksAndMessages(null);
            weakHandler = null;
        }
    }

    /**
     * 添加Presenter
     *
     * @param presenters
     */
    protected void addPresenter(IBasePresenter... presenters) {
        for (IBasePresenter presenter : presenters) {
            if (null != presenter) {
                presenter.bindContext(this);
                presenterList.add(presenter);
            }
        }
    }

    /**
     * 初始化presenter，只针对泛型方式传入的Presenter
     *
     * @return
     */
    protected P getPresenter() {
        return null;
    }

    /**
     * 初始化基础布局，Base之间的继承才调用这个方法，其他情况不用
     */
    void initBaseView() {

    }


    /**
     * 初始化数据
     */
    protected void initData() {

    }

    private static final int MIN_CLICK_DELAY_TIME = 500;
    private int clickDelay = MIN_CLICK_DELAY_TIME;

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if ((lastViewId == v.getId())) {
            if (currentTime - lastClickTime > clickDelay) {
                onSingleClick(v);
            } else {
                KLog.i("========快速点击无效");
            }
        } else {
            onSingleClick(v);
        }
        lastViewId = v.getId();
        lastClickTime = currentTime;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBack() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getFragmentManager().popBackStack();
        } else {
            onBackPressed();
        }
    }

    /**
     * view的点击事件都在此方法中处理
     *
     * @param v
     */
    protected abstract void onSingleClick(View v);

    /**
     * 返回当前activity的布局id
     *
     * @return
     */
    protected abstract int getLayoutResId();


    /**
     * 安全地启动一个隐式Intent
     * 打开相机的隐式Intent，如果系统相机应用被关闭或者不存在相机应用，
     * 又或者是相机应用的某些权限被关闭等等情况都可能导致这个隐式的Intent无法正常工作
     *
     * @param intentName
     */
    protected void startActivity(String intentName) {
        Intent intent = new Intent(intentName);
        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivity(intent);
        } else {
            showToast("无法启动相应的组件");
        }
    }


    /**
     * 请求的返回结果错误
     *
     * @param requestCode
     * @param message
     */
    @Override
    public void onRequestDataError(int requestCode, String message) {
        showToast(message);
        dismissLoad();
    }

    public void loading() {
        loading(true, (DialogInterface.OnDismissListener) null);
    }


    public CustomProgressDialog loading(boolean cancelable, DialogInterface.OnDismissListener listener) {
        if (null == customProgressDialog) {
            customProgressDialog = new CustomProgressDialog(this);
        }
        customProgressDialog.setOnDismissListener(listener);
        customProgressDialog.setCancelable(cancelable);
        customProgressDialog.loading();
        return customProgressDialog;
    }

    public CustomProgressDialog loading(boolean cancelable, String text) {
        if (null == customProgressDialog) {
            customProgressDialog = new CustomProgressDialog(this);
        }
        customProgressDialog.setCancelable(cancelable);
        customProgressDialog.loading(text);
        return customProgressDialog;
    }

    public void dismissLoad() {
        if (null != customProgressDialog) {
            customProgressDialog.dismiss();
        }
        customProgressDialog = null;
    }

    protected void protectApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AppStatusManager.KEY_HOME_ACTION, AppStatusManager.ACTION_RESTART_APP);
        startActivity(intent);
    }

    public LoginDialog showReLogin() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(this);
            loginDialog.setOnLoginClick(new LoginDialog.OnLoginClick() {
                @Override
                public void loginWeChartClick() {
                    wechatLogin();
                    loginDialog.dismiss();
                }

                @Override
                public void loginSinaClick() {
                    weiboLogin();
                    loginDialog.dismiss();
                }

            });
        }
        if (!loginDialog.isShowing()) {
            loginDialog.show();
        }
        return loginDialog;
    }

    private SsoHandler ssoHandler;
    private WbPresenter wbPresenter;
    private WbShareHandler shareHandler;
    private static final short TYPE_NONE = 0;
    private static final short TYPE_WECHAT_LOGIN = 10;
    private static final short TYPE_WECHAT_SHARE = 11;
    private static final short TYPE_WEIBO_LOGIN = 20;
    private static final short TYPE_WEIBO_JUMP = 21;
    private static final short TYPE_WEIBO_SHARE = 22;
    private static final short TYPE_QQ_LOGIN = 30;
    private static final short TYPE_QQ_SHARE = 31;

    private short type = TYPE_NONE;
    private Disposable disposable;

    /**
     * 微信登录
     */
    public void wechatLogin() {
        IWXAPI api = checkWechat();
        if (api != null) {
            type = TYPE_WECHAT_LOGIN;
            SendAuth.Req req = new SendAuth.Req();
            req.scope = GlobalParams.Social.WECHAT_AUTH_SCOPE;
            req.state = GlobalParams.Social.WECHAT_AUTH_STATE;
            api.sendReq(req);
            KLog.e("===开始微信登录");
        } else {
            showToast("微信内部错误");
        }
    }

    /**
     * 微信分享
     *
     * @param flag 0是好友，1是朋友圈  2微信收藏
     * @param info 标题
     */
    public void wechatShare(int flag, ShareInfo info) {
        if (info == null) {
            return;
        }
        shareInfo = info;
        if (!TextUtils.isEmpty(shareInfo.share_image_url)) {
            disposable = Observable.just(shareInfo.share_image_url)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<String, Bitmap>() {
                        @Override
                        public Bitmap apply(String url) throws Exception {
                            int width = 100;
                            Bitmap bitmap = GlideLoader.downloadImage(BaseCompatActivity.this, url, width, width);
                            KLog.e("=====apply：isRecycled>" + bitmap.isRecycled());
                            while (bitmap.isRecycled()){
                                width++;
                                bitmap = GlideLoader.downloadImage(BaseCompatActivity.this, url, width, width);
                                KLog.e("=====apply：isRecycled11>" + bitmap.isRecycled());
                                if(width>110)
                                    break;
                            }
                            KLog.e("=====apply：isRecycled22>" + bitmap.isRecycled());
                            return bitmap;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            wechatShare(flag, shareInfo, bitmap);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            KLog.e("=====获取缩略图失败：" + throwable.getMessage());
                            wechatShare(flag, shareInfo, null);
                        }
                    });
        } else {
            wechatShare(flag, shareInfo, null);
        }

    }

    /**
     * 微信小程序分享
     *
     * @param flag 0是好友
     * @param info 标题
     */
    public void wxMinAppShare(int flag, ShareInfo info, Bitmap bitmap) {
        if (info == null) {
            return;
        }
        shareInfo = info;
        if (!TextUtils.isEmpty(shareInfo.wxprogram_share_info.thumb_data) && bitmap == null) {
            disposable = Observable.just(shareInfo.wxprogram_share_info.thumb_data)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<String, Bitmap>() {
                        @Override
                        public Bitmap apply(String url) throws Exception {
                            return GlideLoader.downloadImage(BaseCompatActivity.this, url, 100, 100);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            wxMinShare(flag, shareInfo, bitmap);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            KLog.e("=====获取缩略图失败：" + throwable.getMessage());
                            wxMinShare(flag, shareInfo, bitmap);
                        }
                    });
        } else {
            wxMinShare(flag, shareInfo, bitmap);
        }

    }

    /**
     * 分享微信小程序
     *
     * @param flag
     * @param shareInfo
     */
    public void wxMinShare(int flag, ShareInfo shareInfo, Bitmap bitmap) {
        IWXAPI msgApi = checkWechat();
        if (msgApi != null) {
            type = TYPE_WECHAT_SHARE;
            WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
            miniProgramObj.webpageUrl = shareInfo.wxprogram_share_info.webpage_url;// 兼容低版本的网页链接
            miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;// 正式版:0，测试版:1，体验版:2
            miniProgramObj.userName = shareInfo.wxprogram_share_info.user_name;     // 小程序原始id
            miniProgramObj.path = shareInfo.wxprogram_share_info.path;            //小程序页面路径
            WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
            if (TextUtils.isEmpty(shareInfo.wxprogram_share_info.title)) {
                msg.title = getString(R.string.share_title, "@动次");
            } else {
                msg.title = shareInfo.wxprogram_share_info.title;
            }
            if (TextUtils.isEmpty(shareInfo.wxprogram_share_info.description)) {
                msg.description = getString(R.string.share_text);
            } else {
                msg.description = shareInfo.wxprogram_share_info.description;
            }
            Bitmap thumb;
            if (bitmap != null) {
                thumb = bitmap;
            } else {
                thumb = BitmapFactory.decodeResource(DCApplication.getDCApp().getResources(), R.mipmap.icon);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb.compress(Bitmap.CompressFormat.PNG, 100, baos);

            int options = 100;
            while (baos.toByteArray().length > 128 * 1024 && options != 10) {
                baos.reset(); //清空output
                thumb.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到output中
                options -= 10;
            }
            msg.thumbData = baos.toByteArray();
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = flag;
            msgApi.sendReq(req);
            thumb.recycle();
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            KLog.e("===开始微信分享");
        } else {
            ToastUtil.showToast("微信分享失败");
        }
    }

    private synchronized void wechatShare(int flag, ShareInfo shareInfo, Bitmap thumbBitmap) {
        IWXAPI msgApi = checkWechat();
        if (msgApi != null) {
            type = TYPE_WECHAT_SHARE;
            WXWebpageObject webpage = new WXWebpageObject();
            WXMediaMessage msg = new WXMediaMessage(webpage);
            if (TextUtils.isEmpty(shareInfo.share_title)) {
                msg.title = getString(R.string.share_title, "@动次");
            } else {
                msg.title = shareInfo.share_title;
            }
            if (TextUtils.isEmpty(shareInfo.share_desc)) {
                msg.description = getString(R.string.share_text);
            } else {
                msg.description = shareInfo.share_desc;
            }
            if (TextUtils.isEmpty(shareInfo.share_wechat)) {
                webpage.webpageUrl = getString(R.string.share_url);
            } else {
                webpage.webpageUrl = flag == 0 ? shareInfo.share_wechat : shareInfo.share_friend;
            }

            Bitmap thumb = thumbBitmap == null ? BitmapFactory.decodeResource(DCApplication.getDCApp().getResources(), R.mipmap.icon) : thumbBitmap;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb.compress(Bitmap.CompressFormat.PNG, 100, baos);

            int options = 100;
            while (baos.toByteArray().length > 32 * 1024 && options != 10) {
                baos.reset(); //清空output
                thumb.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到output中
                options -= 10;
            }
            msg.thumbData = baos.toByteArray();
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = flag;
            msgApi.sendReq(req);
            thumb.recycle();
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            KLog.e("===开始微信分享");
        } else {
            ToastUtil.showToast("微信分享失败");
        }
    }


    @Nullable
    private IWXAPI checkWechat() {
        IWXAPI api = WXAPIFactory.createWXAPI(DCApplication.getDCApp(), GlobalParams.Social.WECHAT_APP_ID, false);
        if (!api.isWXAppInstalled()) {
            ToastUtil.showToast(R.string.socialWechatNotInstall);
            return null;
        }
        if (!api.registerApp(GlobalParams.Social.WECHAT_APP_ID)) {
            ToastUtil.showToast(R.string.socialWechatRegisterFail);
            return null;
        }
        return api;
    }

    public void weiboAuth() {
        weiboLogin(true);
    }

    public void weiboLogin() {
        weiboLogin(false);
    }

    public void weiboLogin(boolean justAuth) {
        loading();
        type = TYPE_WEIBO_LOGIN;
        WbSdk.install(this, new AuthInfo(this, GlobalParams.Social.WEIBO_APP_KEY,
                GlobalParams.Social.WEIBO_REDIRECT_URL, GlobalParams.Social.WEIBO_SCOPE));
        ssoHandler = new SsoHandler(this);
        KLog.e("===进入微博登录");
        ssoHandler.authorize(new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken accessToken) {
                KLog.e("on weibo auth Success: token：" + accessToken);
                if (justAuth) {
                    onWeiboAuthOk(accessToken);
                } else {
                    if (accessToken != null && accessToken.isSessionValid()) {
                        String token = accessToken.getToken();
                        String uid = accessToken.getUid();
                        if (wbPresenter == null) {
                            wbPresenter = new WbPresenter(BaseCompatActivity.this);
                            addPresenter(wbPresenter);
                        }
                        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(uid)) {
                            KLog.e("开始微博登录：" + token + " uid:" + uid);
                            wbPresenter.weiboLogin(token, uid);
                        } else {
                            dismissLoad();
                            showToast("微博登录失败:token为空");
                        }
                    } else {
                        KLog.e("on weibo auth not Valid：");
                        dismissLoad();
                        showToast("微博登录失败:token错误");
                    }
                }
                type = TYPE_NONE;
            }

            @Override
            public void cancel() {
                KLog.e("on weibo auth cancel: ");
                dismissLoad();
                showToast("微博登录取消");
                type = TYPE_NONE;
            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                KLog.e("on weibo auth onFailure: " + wbConnectErrorMessage);
                dismissLoad();
                showToast("微博登录失败：" + (wbConnectErrorMessage != null ? wbConnectErrorMessage.getErrorMessage() : ""));
                type = TYPE_NONE;
            }
        });
    }

    protected void onWeiboAuthOk(Oauth2AccessToken accessToken) {

    }

    public void weiboJump(String id) {
        KLog.e("===开始微博跳转");
        type = TYPE_WEIBO_JUMP;
        WeiboPageUtils.getInstance(this, new AuthInfo(this, GlobalParams.Social.WEIBO_APP_KEY,
                GlobalParams.Social.WEIBO_REDIRECT_URL, GlobalParams.Social.WEIBO_SCOPE))
                .startUserMainPage(id, !WbSdk.isWbInstall(this));
    }

    /**
     * 微博分享
     */
    public void weiboShare(ShareInfo info) {
        if (info == null) {
            return;
        }
        shareInfo = info;
        loading();
        if (!TextUtils.isEmpty(shareInfo.share_image_url)) {
            disposable = Observable.just(shareInfo.share_image_url)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<String, Bitmap>() {
                        @Override
                        public Bitmap apply(String url) throws Exception {
                            return GlideLoader.downloadImage(BaseCompatActivity.this, url, 100, 100);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            weiboShare(shareInfo, bitmap);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            KLog.e("=====获取缩略图失败：" + throwable.getMessage());
                            weiboShare(shareInfo, null);
                        }
                    });
        } else {
            weiboShare(shareInfo, null);
        }
    }

    private void weiboShare(ShareInfo shareInfo, Bitmap thumbBitmap) {
        type = TYPE_WEIBO_SHARE;
        WbSdk.install(this, new AuthInfo(this, GlobalParams.Social.WEIBO_APP_KEY,
                GlobalParams.Social.WEIBO_REDIRECT_URL, GlobalParams.Social.WEIBO_SCOPE));
        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();//分享必须写这句
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        WebpageObject webpageObject = new WebpageObject();
        if (TextUtils.isEmpty(shareInfo.share_title)) {
            textObject.title = getString(R.string.share_title, "@动次");
            webpageObject.title = getString(R.string.share_title, "@动次");
        } else {
            textObject.title = shareInfo.share_title;
            webpageObject.title = shareInfo.share_title;
        }

        if (TextUtils.isEmpty(shareInfo.share_weibo_desc)) {
            textObject.text = getString(R.string.share_text);
            webpageObject.description = getString(R.string.share_text);
        } else {
            textObject.text = shareInfo.share_weibo_desc;
            webpageObject.description = shareInfo.share_weibo_desc;
        }
        if (TextUtils.isEmpty(shareInfo.share_weibo)) {
            textObject.actionUrl = getString(R.string.share_url);
            webpageObject.actionUrl = getString(R.string.share_url);
        } else {
            textObject.actionUrl = shareInfo.share_weibo;
            webpageObject.actionUrl = shareInfo.share_weibo;
        }
        webpageObject.setThumbImage(thumbBitmap == null ? BitmapFactory.decodeResource(getResources(), R.mipmap.icon) : thumbBitmap);
//        try {
//            webpageObject.setThumbImage(BitmapFactory.decodeStream(new URL(textObject.actionUrl).openStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        weiboMessage.textObject = textObject;
        weiboMessage.mediaObject = webpageObject;
        shareHandler.shareMessage(weiboMessage, false);
        KLog.e("===开始微博分享");
    }

    /**
     * QQ登录，目前没用
     */
    public void qqLogin() {
        loading();
        type = TYPE_QQ_LOGIN;
        Tencent tencent = Tencent.createInstance(GlobalParams.Social.QQ_APP_ID, this);
        if (tencent != null) {
            loading();
            tencent.login(this, GlobalParams.Social.QQ_SCOPE, iUiListener);
        } else {
            ToastUtil.showToast("QQ登录失败");
        }
    }

    /**
     * QQ分享
     */
    public void qqShare(ShareInfo info) {
        if (info == null) {
            return;
        }
        shareInfo = info;
        loading();
        type = TYPE_QQ_SHARE;
        Tencent tencent = Tencent.createInstance(GlobalParams.Social.QQ_APP_ID, this);
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        String title;
        if (TextUtils.isEmpty(shareInfo.share_title)) {
            title = getString(R.string.share_title, "@动次");
        } else {
            title = shareInfo.share_title;
        }
        String description;
        if (TextUtils.isEmpty(shareInfo.share_desc)) {
            description = getString(R.string.share_text);
        } else {
            description = shareInfo.share_desc;
        }
        String url;
        if (TextUtils.isEmpty(shareInfo.share_qq)) {
            url = getString(R.string.share_url);
        } else {
            url = shareInfo.share_qq;
        }

        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.share_image_url);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);
        tencent.shareToQQ(this, params, iUiListener);
    }


    @Override
    public void onWeiboOk(int type, LoginUserResponse response) {
        dismissLoad();
        if (type == 1) {
            AccountUtil.loginSuccess(response, AccountUtil.TYPE_WB);
            showToast(R.string.login_suc);
        } else if (type == 2) {
            //do nothing
        }
    }

    @Override
    public void onWeiboFail(int type, String message) {
        dismissLoad();
        showToast(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.e("onActivityResult: ");
        if (type == TYPE_QQ_SHARE || type == TYPE_QQ_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
        } else if (type == TYPE_WEIBO_SHARE || type == TYPE_WEIBO_LOGIN) {
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        } else {
            //do nothing
        }
        type = TYPE_NONE;
    }

    private IUiListener iUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            dismissLoad();
            KLog.e("QQ auth onComplete: " + (o != null ? o.toString() : "null"));
            if (type == TYPE_QQ_SHARE) {
                if (shareInfo != null) {
                    ShareEventEntity.share(shareInfo);
                }
            }
            type = TYPE_NONE;
        }

        @Override
        public void onError(UiError uiError) {
            dismissLoad();
            KLog.e("QQ auth  onError: " + uiError.errorDetail);
            showToast(uiError.errorMessage);
            type = TYPE_NONE;
        }

        @Override
        public void onCancel() {
            KLog.e("QQ auth  onCancel: ");
            dismissLoad();
            if (type == TYPE_QQ_SHARE) {
                if (shareInfo != null) {
                    ShareEventEntity.shareCancel(shareInfo);
                }
                EventHelper.post(GlobalParams.EventType.TYPE_SHARE_CANCEL_EVENT);
            }
            type = TYPE_NONE;
        }
    };

    @Override
    public void onWbShareSuccess() {
//        showToast("微博分享成功");
        KLog.e("====onWbShareSuccess");
        dismissLoad();
        type = TYPE_NONE;
        if (shareInfo != null) {
            ShareEventEntity.share(shareInfo);
        }
    }

    @Override
    public void onWbShareCancel() {
//        showToast("微博分享取消");
        KLog.e("====onWbShareCancel");
        dismissLoad();
        if (shareInfo != null) {
            ShareEventEntity.shareCancel(shareInfo);
        }
        EventHelper.post(GlobalParams.EventType.TYPE_SHARE_CANCEL_EVENT);
        type = TYPE_NONE;
    }

    @Override
    public void onWbShareFail() {
//        showToast("微博分享失败");
        KLog.e("====onWbShareFail");
        dismissLoad();
        type = TYPE_NONE;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        KLog.e(requestTag + " onNewIntent: ");
        if (type == TYPE_QQ_SHARE || type == TYPE_QQ_LOGIN) {
            //do nothing
        } else if (type == TYPE_WEIBO_SHARE || type == TYPE_WEIBO_LOGIN) {
            if (shareHandler != null) {
                shareHandler.doResultIntent(intent, this);
            }
        } else {
            onReloadIntent(intent);
        }
        type = TYPE_NONE;
    }

    protected void onReloadIntent(Intent intent) {

    }

}
