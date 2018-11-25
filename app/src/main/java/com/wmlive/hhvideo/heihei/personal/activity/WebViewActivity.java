package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.network.DCRequest;
import com.wmlive.hhvideo.heihei.beans.login.VerifyEntity;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.personal.widget.EnhanceSwipeRefreshLayout;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.HeaderUtils;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;
import com.wmlive.hhvideo.utils.RegexUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XueFei on 2017/6/05.
 * Modify by lsq
 * 支持隐式启动，启动的scheme="hhvideo",启动的intent-filter action{@link WebViewActivity#WEB_ACTION}
 * 支持显示纯html代码
 * 支持下拉刷新页面
 */

public class WebViewActivity extends DcBaseActivity implements
        EnhanceSwipeRefreshLayout.CanChildScrollUpCallback,
        SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.srlRoot)
    EnhanceSwipeRefreshLayout srlRoot;
    public static final String WEB_ACTION = "cn.wmlive.hhvideo.webpage";//隐式启动的action

    private WebView webView;
    private WebSettings mSettings;

    public static final String WEB_URL = "webUrl";//浏览的url
    public static final String WEB_TITLE = "WebTitle";//浏览的title，可选
    public static final String WEB_TAG = "WebTag";//浏览的tag，可选
    public static final String WEB_WEIBO_AUTH = "weibo_auth";//微博认证

    private String loadUrl;//浏览的url
    private String tag;//浏览的tag，可选，用于特殊的view操作
    private String title;//浏览的title，可选
    private boolean isWeiboAuth = false;

    private String shareSuccessMethod;
    private String shareCancelMethod;
    private ShareInfo shareInfo;
    private Disposable disposable;
    private PopupWindow shareWindow;
    private ImageView ivShare;

    public static void startWebActivity(Context context, String url, String title) {
        startWebActivity(context, null, url, title);
    }

    /**
     * @param context
     * @param tag
     * @param url
     * @param title
     */
    public static void startWebActivity(Context context, String tag, String url, String title) {
        startWebActivity(context, tag, url, title, null);
    }

    public static void startWebActivity(Context context, String tag, String url, String title, Bundle bundle) {
        startWebActivity(context, bundle, tag, url, title, false);
    }

    public static void startWeiboAuth(Context context, String url, String title, boolean isWeiboAuth) {
        startWebActivity(context, null, null, url, title, isWeiboAuth);
    }

    public static void startWebActivity(Context context, Bundle bundle, String tag, String url, String title, boolean isWeiboAuth, int... flags) {
        Bundle extras = (bundle == null) ? (new Bundle()) : bundle;
        extras.putString(WEB_TAG, tag);
        extras.putString(WEB_URL, url);
        extras.putString(WEB_TITLE, title);
        extras.putBoolean(WEB_WEIBO_AUTH, isWeiboAuth);
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtras(extras);
        if (flags.length > 0) {
            for (int flag : flags) {
                intent.addFlags(flag);
            }
        }
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            Uri uri = intent.getData();
            if (null != uri) {//隐式启动
                loadUrl = uri.getQueryParameter("url");
            } else {//显式启动
                loadUrl = getIntent().getStringExtra(WEB_URL);
                title = getIntent().getStringExtra(WEB_TITLE);
                tag = getIntent().getStringExtra(WEB_TAG);
            }
            KLog.i("uri===-->"+loadUrl+"title"+title+"tag:"+tag);
            isWeiboAuth = getIntent().getBooleanExtra(WEB_WEIBO_AUTH, false);
            if (!TextUtils.isEmpty(loadUrl)) {
                EventHelper.register(this);
                setWebView();
            } else {
                toastFinish();
            }
        } else {
            toastFinish();
        }
    }

    private void setWebView() {
        setTitle("", true);
        setTitleBarIcon();
        srlRoot.setColorSchemeResources(R.color.colorPrimaryDark);
        srlRoot.setOnRefreshListener(this);
        srlRoot.setCanChildScrollUpCallback(this);
        webView = new WebView(this);
        webView.setWebViewClient(new MyWebViewClient());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(layoutParams);
        webView.setWebChromeClient(mWebChromeClient);
        mSettings = webView.getSettings();
        String defaultUA = mSettings.getUserAgentString();
        mSettings.setUserAgentString(defaultUA);
        mSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mSettings.setUseWideViewPort(true);
        mSettings.setDomStorageEnabled(true);
        mSettings.setLoadWithOverviewMode(true);
        // 设置可以支持缩放
        mSettings.setSupportZoom(true);
        // 设置出现缩放工具
        mSettings.setBuiltInZoomControls(true);
        // 自适应屏幕
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setBackgroundColor(getResources().getColor(R.color.transparent));
        webView.getSettings().setSavePassword(false);
        // 同步cookie
        srlRoot.addView(webView, 0);
        //js调用Android的方法
        webView.addJavascriptInterface(new WeiboObj(), "weibo");
        webView.addJavascriptInterface(new WLDCObj(), "wldcObject");
        mSettings.setJavaScriptEnabled(true);
        synCookies();

    }

    private void setTitleBarIcon() {
        ivShare = new ImageView(this);
        ivShare.setImageResource(R.drawable.icon_tab_forwarding);
        ivShare.setPadding(10, 6, DeviceUtils.dip2px(this, 15), 6);
        ivShare.setVisibility(View.GONE);
        setToolbarRightView(ivShare, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KLog.i("========shareInfo:" + shareInfo);
                showShare(shareInfo);
            }
        });
        ivShare.setVisibility(View.GONE);
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (null != srlRoot) {
                if (newProgress > 90) {
                    srlRoot.setRefreshing(false);
                } else {
                    if (!srlRoot.isRefreshing()) {
                        srlRoot.setRefreshing(true);
                    }
                }
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            onPageReceivedTitle(view, title);
        }
    };

    protected void onPageReceivedTitle(WebView view, String title) {
        KLog.e("------title:" + title);
        if (!TextUtils.isEmpty(title)) {
            setTitle(RegexUtil.isUrl(title) ? "" : title, true);
        } else {
            if (TextUtils.isEmpty(title) || !RegexUtil.isContainsChineseChar(title)) {
                setTitle("", true);
            } else {
                setTitle(title, true);
            }
        }
    }

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (webView != null && mSettings != null) {
//            mSettings.setJavaScriptEnabled(true);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (webView != null && mSettings != null) {
//            mSettings.setJavaScriptEnabled(false);
//        }
//    }

    @Override
    public void onRefresh() {
        if (webView != null) {
            loadWebPage(webView.getUrl());
        }
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        if (webView != null) {
            return webView.getScrollY() > 0;
        }
        return false;
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url2) {
            loadWebPage(url2);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }

    private void loadWebPage(String url) {
        if (webView != null && url != null) {
            if (RegexUtil.isUrl(url) || url.startsWith("file:///") || url.startsWith("content://")) {
                webView.loadUrl(url);
            } else if (url.startsWith("hhvideo://")) {
                DcRouter.linkTo(WebViewActivity.this, url);
            } else {
                webView.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null);
            }
        }
    }

    /**
     * 同步一下cookie
     */
    private void synCookies() {
        disposable = Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        CookieSyncManager.createInstance(WebViewActivity.this);
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeAllCookie();
                        cookieManager.setAcceptCookie(true);
                        com.wmlive.networklib.okhttp.CookieManager manager = DCRequest.getRetrofit().getCookieManager();
                        if (manager != null) {
                            Map<String, String> map = manager.getCookieMap();
                            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
                            Map.Entry<String, String> next;
                            while (iterator.hasNext()) {
                                next = iterator.next();
                                KLog.i("==name:" + next.getKey() + ",value:" + next.getValue());
                                cookieManager.setCookie(loadUrl, next.getValue());
                            }
                            cookieManager.setCookie(loadUrl, "app_name" + HeaderUtils.getAppName());
                            cookieManager.setCookie(loadUrl, "app_version=" + HeaderUtils.getAppVersion());
                            cookieManager.setCookie(loadUrl, "os_version=" + HeaderUtils.getOsVersion());
                            cookieManager.setCookie(loadUrl, "os_platform=" + HeaderUtils.getOsPlatform());
                            cookieManager.setCookie(loadUrl, "device_model=" + HeaderUtils.getDeviceModel());
                            cookieManager.setCookie(loadUrl, "device_id=" + HeaderUtils.getDeviceIdMsg());
                            cookieManager.setCookie(loadUrl, "device_resolution=" + HeaderUtils.getDeviceResolution());
                            cookieManager.setCookie(loadUrl, "device_ac=" + HeaderUtils.getDeviceAc());
                            cookieManager.setCookie(loadUrl, "api_version=" + HeaderUtils.getApiVersion());
                            cookieManager.setCookie(loadUrl, "build_number=" + HeaderUtils.getBuildNumber());
                            cookieManager.setCookie(loadUrl, "channel=" + HeaderUtils.getChannel());
                            cookieManager.setCookie(loadUrl, "lat_lon=" + HeaderUtils.getLocationInfo());
                            CookieSyncManager.getInstance().sync();
                            return true;
                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isOk) throws Exception {
                        String cookie = CookieManager.getInstance().getCookie(loadUrl);
                        KLog.i("======已加载的cookie:" + cookie);
                        loadWebPage(loadUrl);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.i("=====webview  set cookie fail");
                        throwable.printStackTrace();
                        loadWebPage(loadUrl);
                    }
                });
    }

    @Override
    protected void onSingleClick(View v) {
        if (v.getId() == R.id.ivBack) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
                //判断task栈里是否存在MainActivity实例
                if (!isExistActivity(MainActivity.class)) {
                    MainActivity.startMainActivity(this);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
            //判断task栈里是否存在MainActivity实例
            if (!isExistActivity(MainActivity.class)) {
                MainActivity.startMainActivity(this);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (null != webView) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        EventHelper.unregister(this);
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
        try {
            if (webView != null) {
                webView.stopLoading();
                webView.removeAllViews();
                webView.setVisibility(View.GONE);
                if (null != srlRoot && srlRoot.getChildCount() > 0) {
                    srlRoot.removeViewAt(0);
                }
                webView.destroy();
                webView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            super.onDestroy();
        }
    }

    @Override
    public void onBack() {
        if (isWeiboAuth) {
            EventHelper.post(GlobalParams.EventType.TYPE_WEIBO_AUTH_OK, true);
            finish();
        } else {
            super.onBack();
        }
        //判断task栈里是否存在MainActivity实例
        if (!isExistActivity(MainActivity.class)) {
            MainActivity.startMainActivity(this);
        }

    }

    private class WeiboObj extends Object {

        @JavascriptInterface
        public void handleClose(String json) {
            KLog.i("JS调用了Android的handleClose方法,返回参数:" + json);
            VerifyEntity verifyEntity = null;
            if (!TextUtils.isEmpty(json)) {
                verifyEntity = JsonUtils.parseObject(json, VerifyEntity.class);
            }
            EventHelper.post(GlobalParams.EventType.TYPE_WEIBO_AUTH_OK, verifyEntity);
            finish();
        }

        @JavascriptInterface
        public void handleWeibo() {
            KLog.i("JS调用了Android的handleWeibo方法");
            if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
                ToastUtil.showToast(R.string.network_null);
                return;
            }
            weiboAuth();
        }

        @JavascriptInterface
        public void handleCopy(String code) {
            KLog.i("JS调用了Android的handleCopy方法");
            ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (cmb != null) {
                cmb.setPrimaryClip(ClipData.newPlainText(null, code));
                showToast(R.string.copy_succeed);
            }
        }


        @JavascriptInterface
        public void handleOfficCopy(String[] info, String code) {
            KLog.i("JS调用了Android的handleOfficCopy方法。参数info:" + Arrays.toString(info) + " ,code:" + code);
            if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
                ToastUtil.showToast(R.string.network_null);
                return;
            }
            CustomDialog customDialog = new CustomDialog(WebViewActivity.this);
            String codeString = TextUtils.isEmpty(code) ? "dongciapp" : code;
            customDialog.setTitle(info != null && info.length > 0 ? info[0] : "申请官方认证请关注");
            customDialog.setContent(info != null && info.length > 1 ? info[1] : "官方微信公众号：" + codeString);
            customDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    customDialog.dismiss();
                }
            });
            customDialog.setPositiveButton("复制公众号", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    customDialog.dismiss();
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cmb != null) {
                        cmb.setPrimaryClip(ClipData.newPlainText(null, codeString));
                        showToast(R.string.copy_succeed);
                    }
                }
            });
            customDialog.show();
        }
    }


    private class WLDCObj extends Object {

        @JavascriptInterface
        public void showClientIsShareSuccessCancel(String shareInfo, boolean isShowShare, String success, String cancel) {
            KLog.i("JS调用了Android的clientShare方法,返回参数 shareInfo:" + shareInfo + " ,success:" + success + " ,cancel:" + cancel + " ,isShowShare:" + isShowShare);
            //右上角显示分享
            srlRoot.post(new Runnable() {
                @Override
                public void run() {
                    ivShare.setVisibility(isShowShare ? View.VISIBLE : View.GONE);
                    shareSuccessMethod = success;
                    shareCancelMethod = cancel;
                    WebViewActivity.this.shareInfo = JsonUtils.parseObject(shareInfo, ShareInfo.class);
                }
            });
        }

        @JavascriptInterface
        public void showSharePanel(String shareInfo, String success, String cancel) {
            KLog.i("JS调用了Android的showSharePanel方法,返回参数 shareInfo:" + shareInfo + " ,success:" + success + " ,cancel:" + cancel);
            srlRoot.post(new Runnable() {
                @Override
                public void run() {
                    shareSuccessMethod = success;
                    shareCancelMethod = cancel;
                    ShareInfo share = JsonUtils.parseObject(shareInfo, ShareInfo.class);
                    showShare(share);
                }
            });
        }
    }

    private void showShare(ShareInfo share) {
        if (share != null) {
            shareWindow = PopupWindowUtils.showNormal(WebViewActivity.this, srlRoot, new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    share.objId = AccountUtil.getUserId();
                    share.shareType = ShareEventEntity.TYPE_USER_HOME;
                    share.needUpload = true;
                    switch (v.getId()) {
                        case R.id.llWeChat:
                            share.shareTarget = ShareEventEntity.TARGET_WECHAT;
                            if (share.wxprogram_share_info != null) {
                                wxMinAppShare(0, share, null);
                            } else {
                                wechatShare(0, share);
                            }
                            break;
                        case R.id.llCircle:
                            share.shareTarget = ShareEventEntity.TARGET_FRIEND;
                            wechatShare(1, share);
                            break;
                        case R.id.llWeibo:
                            share.shareTarget = ShareEventEntity.TARGET_WEIBO;
                            weiboShare(share);
                            break;
                        case R.id.llQQ:
                            share.shareTarget = ShareEventEntity.TARGET_QQ;
                            qqShare(share);
                            break;
                        case R.id.llCopy:
                            share.shareTarget = ShareEventEntity.TARGET_WEB;
                            ClipboardManager cmb = (ClipboardManager) WebViewActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setPrimaryClip(ClipData.newPlainText(null, share.web_link));
                            showToast(R.string.copy_succeed);
                            ShareEventEntity.share(share);
                            break;
                        default:
                            break;
                    }
                    if (null != shareWindow && shareWindow.isShowing()) {
                        shareWindow.dismiss();
                    }
                }
            });
        } else {
            WebViewActivity.this.showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    protected void onWeiboAuthOk(Oauth2AccessToken accessToken) {
        super.onWeiboAuthOk(accessToken);
        dismissLoad();
        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return;
        }
        if (accessToken != null) {
            //调用js的方法,access_token,openId

            KLog.i("======accessToken:" + accessToken.toString());
            String js = "javascript:handleRefreshWeibo('" + accessToken.getToken() + "','" + accessToken.getUid() + "')";
            KLog.i("========调用js的字符串：" + js);
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //此处为 js 返回的结果
                            KLog.i("====服务器返回的结果：" + value);
                        }
                    });
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onShareEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_SHARE_EVENT) {
            KLog.i("======收到分享行为数据:" + eventEntity.data);
            if (eventEntity.data != null && eventEntity.data instanceof ShareEventEntity) {
                ShareEventEntity shareEventEntity = (ShareEventEntity) eventEntity.data;
                String js;
                if (shareEventEntity.isSuccess) {
                    js = "javascript:" + shareSuccessMethod + "('" + shareEventEntity.shareUuid + "','" + shareEventEntity.shareTarget + "')";
                } else {
                    js = "javascript:" + shareCancelMethod + "('" + shareEventEntity.shareUuid + "')";
                }
                KLog.i("=====调用js方法：" + js);
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                //此处为 js 返回的结果
                                KLog.i("====服务器返回的结果：" + value);
                            }
                        });
                    }
                });
            }
        }
    }
}
