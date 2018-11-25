package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.wmlive.hhvideo.common.network.DCRequest;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.CustomInputText;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import retrofit2.Response;

/**
 * 验证邀请码对话框
 * Created by wenlu on 2017/10/18.
 */

public class VerifyDialog extends Dialog implements CustomInputText.OnTextChangeListener {

    @BindView(R.id.customInputText)
    CustomInputText customInputText;
    @BindView(R.id.tvActivate)
    TextView tvActivate;

    private Context mContext;
    private String mActivationCode;
    private OnVerifyListener mOnVerifyListener;

    public VerifyDialog(@NonNull Activity context) {
        super(context, R.style.BaseDialogTheme);
        init(context);
    }

    private void init(Activity context) {
        mContext = context;
        setContentView(R.layout.dialog_verify_visit);
        ButterKnife.bind(this);
        setCancelable(false);
        setOwnerActivity(context);
        setCanceledOnTouchOutside(false);
        tvActivate.setClickable(false);
        customInputText.setOnTextChangeListener(this);
    }

    @OnClick(R.id.iv_dismiss)
    void onDismiss() {
        this.dismiss();
    }

    @OnClick(R.id.tvActivate)
    void onActivate() {
        verifyInvitationCode(mActivationCode);
    }

    public void verifyInvitationCode(String activationCode) {
        executeRequest(HttpConstant.TYPE_USER_VERIFY_INVITATION_CODE, DCRequest.getHttpApi().getVerifyInvitationCode(InitCatchData.userVerifyInvitationCode(), activationCode))
                .subscribe(new DCNetObserver<LoginUserResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, final LoginUserResponse response) {
                        if (response == null) {
                            ToastUtil.showToast(message);
                            return;
                        }
                        ToastUtil.showToast("激活成功");
                        if (mOnVerifyListener != null) {
                            mOnVerifyListener.onVerifySuccess();
                        }

                        response.setToken(AccountUtil.getToken());
                        AccountUtil.resetAccount(response);
                        dismiss();
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        ToastUtil.showToast(message);
                    }
                });
    }

    public <T extends BaseResponse> Observable<Response<T>> executeRequest(int requestCode, Observable<Response<T>> observable) {
        return DCRequest.getRetrofit().getObservable(null, requestCode, observable, null);
    }

    @Override
    public void onDifference() {

    }

    @Override
    public void onEqual(String text) {

    }

    @Override
    public void onTextChange(String text, boolean isComplete) {
        mActivationCode = text;
        if (text != null && text.length() > 0) {
            tvActivate.setBackground(mContext.getResources().getDrawable(R.drawable.bg_btn_activate_shape));
            tvActivate.setTextColor(mContext.getResources().getColor(R.color.white));
            tvActivate.setClickable(true);
        } else {
            tvActivate.setBackground(mContext.getResources().getDrawable(R.drawable.bg_btn_gray_shape));
            tvActivate.setTextColor(mContext.getResources().getColor(R.color.hh_color_b));
            tvActivate.setClickable(false);
        }
    }

    public void setOnVerifyListener(OnVerifyListener verifyListener) {
        mOnVerifyListener = verifyListener;
    }

    public interface OnVerifyListener {
        void onVerifySuccess();
    }

}
