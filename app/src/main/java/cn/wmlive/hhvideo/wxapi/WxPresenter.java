package cn.wmlive.hhvideo.wxapi;

import android.text.TextUtils;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.login.WxTokenEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsq on 12/28/2017.12:58 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class WxPresenter extends BasePresenter<WxPresenter.IWxView> {

    public WxPresenter(IWxView view) {
        super(view);
    }

    public void getWxToken(String code) {
        String getTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + GlobalParams.Social.WECHAT_APP_ID
                + "&secret=" + GlobalParams.Social.WECHAT_APP_SECRET
                + "&code=" + code + "&grant_type=authorization_code";
        KLog.i("===获取微信token的url：" +getTokenUrl);
        executeRequest(HttpConstant.TYPE_GET_WECHAT_TOKEN, getHttpApi().getWxAccessToken(getTokenUrl))
                .subscribe(new DCNetObserver<WxTokenEntity>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, WxTokenEntity response) {
                        if (viewCallback != null) {
                            if (!TextUtils.isEmpty(response.access_token) && !TextUtils.isEmpty(response.openid)) {
                                KLog.i("===获取微信token成功：" + response.toString());
                                viewCallback.onGetWxTokenOk(response);
                            } else {
                                viewCallback.onWxLoginFail("获取token失败");
                                KLog.i("===获取微信token失败：" + response.toString());
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        KLog.i("===获取微信token失败：" + message);
                        if (viewCallback != null) {
                            viewCallback.onWxLoginFail(message);
                        }
                    }
                });
    }

    public void wxLogin(String token, String openId, String unionId) {
        executeRequest(HttpConstant.TYPE_LOGIN_WECHAT, getHttpApi().signIn(InitCatchData.userSignIn(), "Wechat", token, openId, unionId))
                .subscribe(new DCNetObserver<LoginUserResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, LoginUserResponse response) {
                        KLog.i("====微信登录成功：" + response.toString());
                        if (null != viewCallback) {
                            viewCallback.onWxLoginOk(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        KLog.i("====微信登录失败：" + message);
                        if (null != viewCallback) {
                            viewCallback.onWxLoginFail(message);
                        }
                    }
                });
    }

    public interface IWxView extends BaseView {
        void onGetWxTokenOk(WxTokenEntity entity);

        void onWxLoginOk(LoginUserResponse response);

        void onWxLoginFail(String message);
    }
}
