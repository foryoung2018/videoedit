package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.app.Dialog;

import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.activity.LoginActivity;
import com.wmlive.hhvideo.heihei.personal.activity.WebViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wmlive.hhvideo.R;

/**
 * Created by vhawk on 2017/6/2.
 * 登录对话框
 */

public class LoginDialog extends Dialog {
    private OnLoginClick mOnLoginClick;
    public Activity mContext;
    private CloseListener closeListener;

    public LoginDialog(Activity context) {
        super(context, R.style.BaseDialogTheme);
        mContext = context;
        setContentView(R.layout.dialog_longin_select);
        ButterKnife.bind(this);
        setCancelable(false);
        setOwnerActivity(context);
        setCanceledOnTouchOutside(false);
    }

    @OnClick(R.id.textView5)
    void onUserRuleClick() {
        WebViewActivity.startWebActivity(mContext, InitCatchData.sysServiceTerms(), mContext.getString(R.string.setting_protocol));
    }

    @OnClick(R.id.iv_dismiss)
    void OnDismiss() {
        this.dismiss();
        if (closeListener != null) {
            closeListener.onClose();
        }
    }

    @OnClick(R.id.iv_mobile_login)
    void onMobileLoginClick() {
        LoginActivity.startLoginActivity(mContext, true);
        this.dismiss();
    }

    @OnClick(R.id.iv_wechat_login)
    void onWeChatLoginClick() {
        if (mOnLoginClick != null) {
            mOnLoginClick.loginWeChartClick();
        }
    }

    @OnClick(R.id.iv_sina_login)
    void onSinaLoginClick() {
        if (mOnLoginClick != null) {
            mOnLoginClick.loginSinaClick();
        }
    }

    public void setOnLoginClick(OnLoginClick onLoginClick) {
        mOnLoginClick = onLoginClick;
    }

    public interface OnLoginClick {
        void loginWeChartClick();

        void loginSinaClick();
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public interface CloseListener{
        void onClose();
    }
}
