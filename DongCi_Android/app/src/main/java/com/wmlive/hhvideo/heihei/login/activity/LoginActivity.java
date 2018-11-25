package com.wmlive.hhvideo.heihei.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.login.presenter.LoginPresenter;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.RegexUtil;
import com.wmlive.hhvideo.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 登录页面
 */
public class LoginActivity extends DcBaseActivity<LoginPresenter> implements LoginPresenter.ILoginView {

    public static final String KEY_IS_RELOGIN = "key_is_relogin";
    public static final String KEY_LOGIN_RESULT = "key_login_result";
    public static final int REQUEST_CODE_RELOGIN = 100;

    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.tvSendCode)
    TextView tvSendCode;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.tvLogin)
    TextView tvLogin;

    private boolean isRelogin;
    private boolean isCounting;
    private int countdownTimeout = 60;

    public static void startLoginActivity(Activity context, boolean isRelogin) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(KEY_IS_RELOGIN, isRelogin);
        if (isRelogin) {
            context.startActivityForResult(intent, REQUEST_CODE_RELOGIN);
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    protected LoginPresenter getPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login2;
    }

    @Override
    protected void onSingleClick(View v) {
        String phoneNumber = etPhone.getText().toString().trim();
        switch (v.getId()) {
            case R.id.tvSendCode:
                if (!TextUtils.isEmpty(phoneNumber) && RegexUtil.isValidMobile(phoneNumber)) {
                    presenter.sendSmsCode(phoneNumber);
                } else {
                    showToast("请输入正确的手机号码");
                }
                break;
            case R.id.tvLogin:
                if (!TextUtils.isEmpty(phoneNumber) && RegexUtil.isValidMobile(phoneNumber)) {
                    String code = etCode.getText().toString().trim();
                    if (!TextUtils.isEmpty(code) && code.length() == 6) {
                        loading();
                        DeviceUtils.hiddenKeyBoard(tvLogin);
                        presenter.mobileLogin(phoneNumber, code);
                    } else {
                        showToast("请输入正确的验证码");
                    }
                } else {
                    showToast("请输入正确的手机号码");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        isRelogin = getIntent().getBooleanExtra(KEY_IS_RELOGIN, false);
        setTitle("登录", isRelogin);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initTextWatcher();
        initTestUser();
    }


    private void initTextWatcher() {
        tvSendCode.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        tvSendCode.setTag(countdownTimeout);
        etPhone.requestFocus();
        etPhone.addTextChangedListener(phoneWatcher);
        etCode.addTextChangedListener(codeWatcher);
    }

    private TextWatcher phoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString().trim();
            tvSendCode.setEnabled(!isCounting && !TextUtils.isEmpty(text) && RegexUtil.isValidMobile(text));
            String code = etCode.getText().toString().trim();
            KLog.i("=====etPhone onTextChanged phone:" + text + " ,code:" + code);
            tvLogin.setEnabled(!TextUtils.isEmpty(code)
                    && code.length() == 6
                    && !TextUtils.isEmpty(text)
                    && RegexUtil.isValidMobile(text));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher codeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString().trim();
            String phone = etPhone.getText().toString().trim();
            KLog.i("=====etCode onTextChanged phone:" + phone + " ,code:" + text);
            tvLogin.setEnabled(!TextUtils.isEmpty(phone)
                    && RegexUtil.isValidMobile(phone)
                    && !TextUtils.isEmpty(text)
                    && text.length() == 6);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onLoginOk() {
        dismissLoad();
        ToastUtil.showToast("登录成功");
    }

    @Override
    public void onLoginComplete(LoginUserResponse response) {
        if (isRelogin) {
            Intent intent = new Intent();
            intent.putExtra(KEY_LOGIN_RESULT, true);
            setResult(RESULT_OK, intent);
        } else {
            MainActivity.startMainActivity(this);
        }
        finish();
    }

    @Override
    public void onLoginFail(String message) {
        showToast(message);
        dismissLoad();
    }

    @Override
    public void onSendCodeFail(String message) {
        showToast(message);
        isCounting = false;
        tvSendCode.setEnabled(true);
        tvSendCode.setText(getString(R.string.send_sms_code));
    }

    @Override
    public void onSendCodeOk(int timeout) {
        countdownTimeout = timeout;
        showToast(R.string.sms_code_send_succeed);
        etCode.requestFocus();
        isCounting = true;
        tvSendCode.postDelayed(countdownRunnable, 1000);
        tvSendCode.setTag(countdownTimeout);
        tvSendCode.setEnabled(false);
        tvSendCode.setText(countdownTimeout + "s");
    }

    private Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            int count = tvSendCode.getTag() != null ? (int) tvSendCode.getTag() : countdownTimeout;
            if (isCounting && count > 0) {
                tvSendCode.setEnabled(false);
                count--;
                tvSendCode.setText(count + "s");
                tvSendCode.setTag(count);
                tvSendCode.postDelayed(this, 1000);
            } else {
                isCounting = false;
                tvSendCode.setEnabled(true);
                tvSendCode.setText(getString(R.string.send_sms_code));
            }
        }
    };

    @Override
    protected void onDestroy() {
        dismissLoad();
        if (countdownRunnable != null) {
            tvSendCode.removeCallbacks(countdownRunnable);
            countdownRunnable = null;
        }
        if (phoneWatcher != null) {
            etPhone.removeTextChangedListener(phoneWatcher);
            phoneWatcher = null;
        }
        if (codeWatcher != null) {
            etCode.removeTextChangedListener(codeWatcher);
            codeWatcher = null;
        }
        isCounting = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isRelogin) {
            super.onBackPressed();
        } else {
            finish();
            DCApplication.getDCApp().exitApp();
        }
    }

    private void initTestUser() {
        findViewById(R.id.llTestUser).setVisibility(GlobalParams.Config.IS_DEBUG ? View.VISIBLE : View.GONE);
        if (GlobalParams.Config.IS_DEBUG) {
            findViewById(R.id.tvUse1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etPhone.setText(((TextView) v).getText());
                    etCode.setText("666666");
                }
            });
            findViewById(R.id.tvUse2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etPhone.setText(((TextView) v).getText());
                    etCode.setText("666666");
                }
            });
            findViewById(R.id.tvUse3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etPhone.setText(((TextView) v).getText());
                    etCode.setText("666666");
                }
            });
        }

    }
}
