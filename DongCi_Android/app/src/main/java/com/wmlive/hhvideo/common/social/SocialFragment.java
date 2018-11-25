package com.wmlive.hhvideo.common.social;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

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
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseFragment;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.io.ByteArrayOutputStream;

import cn.wmlive.hhvideo.R;
import cn.wmlive.hhvideo.wxapi.WbPresenter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 1/2/2018.10:35 AM
 *
 * @author lsq
 * @describe 添加描述
 */

@Deprecated
public class SocialFragment extends BaseFragment implements WbPresenter.IWeiboView, WbShareCallback {
    private static final String TAG = "DcAppSocial";
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

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
            KLog.e(TAG, "===开始微信登录");
        } else {
            showToast("微信内部错误");
        }
    }

    /**
     * 微信分享
     *
     * @param flag      0是好友，1是朋友圈  2微信收藏
     * @param shareInfo 标题
     */
    public void wechatShare(int flag, ShareInfo shareInfo) {
        if (shareInfo == null) {
            return;
        }
        disposable = Observable.just(shareInfo.share_image_url)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String url) throws Exception {
                        return GlideLoader.downloadImage(getActivity(), url, 100, 100);
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

    }


    private void wechatShare(int flag, ShareInfo shareInfo, Bitmap thumbBitmap) {
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
            if (TextUtils.isEmpty(shareInfo.share_url)) {
                webpage.webpageUrl = getString(R.string.share_url);
            } else {
                webpage.webpageUrl = shareInfo.share_url;
            }

            Bitmap thumb = thumbBitmap == null ? BitmapFactory.decodeResource(DCApplication.getDCApp().getResources(), R.mipmap.icon) : thumbBitmap;
            Bitmap b = Bitmap.createScaledBitmap(thumb, 128, 128, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, baos);
            msg.thumbData = baos.toByteArray();
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = flag;
            msgApi.sendReq(req);
            thumb.recycle();
            b.recycle();
            KLog.e(TAG, "===开始微信分享");
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

    @Override
    protected int getBaseLayoutId() {
        return 0;
    }

    @Override
    protected void onSingleClick(View v) {

    }

    public void weiboLogin() {
        if (getActivity() == null) {
            return;
        }
        loading();
        type = TYPE_WEIBO_LOGIN;
        WbSdk.install(getActivity(), new AuthInfo(getActivity(), GlobalParams.Social.WEIBO_APP_KEY,
                GlobalParams.Social.WEIBO_REDIRECT_URL, GlobalParams.Social.WEIBO_SCOPE));
        ssoHandler = new SsoHandler(getActivity());
        KLog.e(TAG, "===进入微博登录");
        ssoHandler.authorize(new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken accessToken) {
                KLog.e(TAG, "on weibo auth Success: token：" + accessToken);
                if (accessToken != null && accessToken.isSessionValid()) {
                    String token = accessToken.getToken();
                    String uid = accessToken.getUid();
                    if (wbPresenter == null) {
                        wbPresenter = new WbPresenter(SocialFragment.this);
                        addPresenter(wbPresenter);
                    }
                    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(uid)) {
                        KLog.e(TAG, "开始微博登录：" + token + " uid:" + uid);
                        wbPresenter.weiboLogin(token, uid);
                    } else {
                        dismissLoad();
                        showToast("微博登录失败:token为空");
                    }
                } else {
                    KLog.e(TAG, "on weibo auth not Valid：");
                    dismissLoad();
                    showToast("微博登录失败:token错误");
                }
            }

            @Override
            public void cancel() {
                KLog.e(TAG, "on weibo auth cancel: ");
                dismissLoad();
                showToast("微博登录取消");
            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                KLog.e(TAG, "on weibo auth onFailure: " + wbConnectErrorMessage);
                dismissLoad();
                showToast("微博登录失败：" + (wbConnectErrorMessage != null ? wbConnectErrorMessage.getErrorMessage() : ""));
            }
        });
    }

    public void weiboJump(String id) {
        if (getActivity() == null) {
            return;
        }
        KLog.e(TAG, "===开始微博跳转");
        type = TYPE_WEIBO_JUMP;
        WeiboPageUtils.getInstance(getActivity(), new AuthInfo(getActivity(), GlobalParams.Social.WEIBO_APP_KEY,
                GlobalParams.Social.WEIBO_REDIRECT_URL, GlobalParams.Social.WEIBO_SCOPE))
                .startUserMainPage(id, !WbSdk.isWbInstall(getActivity()));
    }

    /**
     * 微博分享
     */
    public void weiboShare(ShareInfo shareInfo) {
        if (shareInfo == null || getActivity() == null) {
            return;
        }
        loading();
        disposable = Observable.just(shareInfo.share_image_url)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String url) throws Exception {
                        return GlideLoader.downloadImage(getActivity(), url, 100, 100);
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
    }

    private void weiboShare(ShareInfo shareInfo, Bitmap thumbBitmap) {
        type = TYPE_WEIBO_SHARE;
        WbSdk.install(getActivity(), new AuthInfo(getActivity(), GlobalParams.Social.WEIBO_APP_KEY,
                GlobalParams.Social.WEIBO_REDIRECT_URL, GlobalParams.Social.WEIBO_SCOPE));
        shareHandler = new WbShareHandler(getActivity());
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
        if (TextUtils.isEmpty(shareInfo.share_url)) {
            textObject.actionUrl = getString(R.string.share_url);
            webpageObject.actionUrl = getString(R.string.share_url);
        } else {
            textObject.actionUrl = shareInfo.share_url;
            webpageObject.actionUrl = shareInfo.share_url;
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
        KLog.e(TAG, "===开始微博分享");
    }

    /**
     * QQ登录，目前没用
     */
    public void qqLogin() {
        if (getActivity() == null) {
            return;
        }
        loading();
        type = TYPE_QQ_LOGIN;
        Tencent tencent = Tencent.createInstance(GlobalParams.Social.QQ_APP_ID, getActivity());
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
    public void qqShare(ShareInfo shareInfo) {
        if (getActivity() == null || shareInfo == null) {
            return;
        }
        loading();
        type = TYPE_QQ_SHARE;
        Tencent tencent = Tencent.createInstance(GlobalParams.Social.QQ_APP_ID, getActivity());
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
        if (TextUtils.isEmpty(shareInfo.share_url)) {
            url = getString(R.string.share_url);
        } else {
            url = shareInfo.share_url;
        }

        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.share_image_url);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);
        tencent.shareToQQ(getActivity(), params, iUiListener);
    }


    @Override
    public void onWeiboOk(int type, LoginUserResponse response) {
        dismissLoad();
        if (type == 1) {
//            AccountUtil.loginSuccess(response);
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

    public void onNewIntent(Intent intent) {
        KLog.e(TAG, "onNewIntent: ");
        if (shareHandler != null) {
//            shareHandler.doResultIntent(intent, this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.e(TAG, "onActivityResult: ");
        if (type == TYPE_QQ_SHARE || type == TYPE_QQ_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
        } else if (type == TYPE_WEIBO_SHARE || type == TYPE_WEIBO_LOGIN) {
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        } else {
            //do nothing
        }
    }

    private IUiListener iUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            dismissLoad();
            KLog.e(TAG, "QQ auth onComplete: " + (o != null ? o.toString() : "null"));
        }

        @Override
        public void onError(UiError uiError) {
            dismissLoad();
            KLog.e(TAG, "QQ auth  onError: " + uiError.errorDetail);
            showToast(uiError.errorMessage);
        }

        @Override
        public void onCancel() {
            KLog.e(TAG, "QQ auth  onCancel: ");
            dismissLoad();
        }
    };

    @Override
    public void onWbShareSuccess() {
//        showToast("微博分享成功");
        KLog.e(TAG, "====onWbShareSuccess");
        dismissLoad();
    }

    @Override
    public void onWbShareCancel() {
//        showToast("微博分享取消");
        KLog.e(TAG, "====onWbShareCancel");
        dismissLoad();
    }

    @Override
    public void onWbShareFail() {
//        showToast("微博分享失败");
        KLog.e("====onWbShareFail");
        dismissLoad();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = null;
    }
}
