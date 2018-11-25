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
 * Created by jht on 2018/9/26.
 * 登录对话框
 */

public class FunctionTipsDialog extends Dialog {
    public Activity mContext;
    private CloseListener closeListener;

    public FunctionTipsDialog(Activity context) {
        super(context, R.style.BaseDialogTheme);
        mContext = context;
        setContentView(R.layout.new_function_guide_dialog);
        ButterKnife.bind(this);
        setCancelable(false);
        setOwnerActivity(context);
        setCanceledOnTouchOutside(true);
    }

    @OnClick(R.id.colse_iv)
    void OnDismiss() {
        this.dismiss();
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public interface CloseListener{
        void onClose();
    }
}
