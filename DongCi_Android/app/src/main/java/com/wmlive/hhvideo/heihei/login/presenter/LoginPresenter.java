package com.wmlive.hhvideo.heihei.login.presenter;

import android.text.TextUtils;

import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.login.SmsResponse;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 12/8/2017.12:08 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class LoginPresenter extends BasePresenter<LoginPresenter.ILoginView> {

    public LoginPresenter(ILoginView view) {
        super(view);
    }

    public void sendSmsCode(String mobile) {
        executeRequest(HttpConstant.TYPE_SMS_CODE, getHttpApi().sendSmsCode(InitCatchData.userGetSMSVerificationCode(), mobile))
                .subscribe(new DCNetObserver<SmsResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, SmsResponse response) {
                        if (null != viewCallback) {
                            viewCallback.onSendCodeOk(response.timeout);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.onSendCodeFail(message);
                        }
                    }
                });
    }

    public void mobileLogin(String mobile, String smsCode) {
        executeRequest(HttpConstant.TYPE_MOBILE_LOGIN, getHttpApi().mobileLogin(InitCatchData.userSignInCLByPhone(), mobile, smsCode))
                .subscribe(new DCNetObserver<LoginUserResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, final LoginUserResponse response) {
                        if (null != viewCallback) {
                            if (response != null && !TextUtils.isEmpty(response.getToken())) {
                                viewCallback.onLoginOk();
                                UserInfo userInfo = response.getUser_info();
                                if (userInfo != null) {
                                    TCAgent.onLogin(String.valueOf(userInfo.getId()), TDAccount.AccountType.REGISTERED, userInfo.getName());
                                }
                                AccountUtil.loginSuccess(response, AccountUtil.TYPE_MOBILE);
                                if (viewCallback != null) {
                                    viewCallback.onLoginComplete(response);
                                }
                            } else {
                                if (viewCallback != null) {
                                    viewCallback.onLoginFail("登录信息错误，请稍后再试");
                                }
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onLoginFail(message);
                        }
                    }
                });
    }

    public interface ILoginView extends BaseView {
        void onLoginOk();

        void onLoginComplete(LoginUserResponse response);

        void onLoginFail(String message);

        void onSendCodeFail(String message);

        void onSendCodeOk(int timeout);
    }
}
