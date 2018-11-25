package com.wmlive.hhvideo.heihei.personal.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.curllibrary.Curl;
import com.netease.LDNetDiagnoService.LDNetDiagnoService;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.personal.presenter.NetCheckPresenter;
import com.wmlive.hhvideo.heihei.personal.view.INetCheckView;
import com.wmlive.hhvideo.utils.HeaderUtils;
import com.wmlive.hhvideo.utils.ToastUtil;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * 网络诊断
 * Author：create by jht on 2018/9/3 16:22
 * Email：haitian.jiang@welines.cn
 */
public class CheckNetWorkActivity extends DcBaseActivity implements com.netease.LDNetDiagnoService.LDNetDiagnoListener, INetCheckView {
    @BindView(R.id.check_net_btn)
    TextView checkNetBtn;
    @BindView(R.id.check_info_tv)
    TextView checkInfoTv;
    @BindView(R.id.show_detail_tv)
    TextView showDetailTv;
    @BindView(R.id.re_check_tv)
    TextView reCheckTv;
    @BindView(R.id.tips_tv)
    TextView tipsTv;
    @BindView(R.id.dialog_iv)
    ImageView dialogIv;
    @BindView(R.id.rLayout)
    LinearLayout rLayout;
    @BindView(R.id.result_iv)
    ImageView resultIv;

    private boolean isRunning = false;
    private LDNetDiagnoService _netDiagnoService;
    private NetCheckPresenter netCheckPresenter;
    private String checkInfoDetais;
    private Animator loadingAnimator;
    private String reslog;

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.check_net_btn:
                checkNetStatus();
                break;
            case R.id.re_check_tv:
                isRunning = false;
                checkNetStatus();
                break;
            case R.id.show_detail_tv:
                if (!TextUtils.isEmpty(checkInfoDetais)) {
                    Intent intent = new Intent(this, CheckNetDetailsActivity.class);
                    intent.putExtra("checkInfoDetais", checkInfoDetais);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    private void initVideoLoading() {
        loadingAnimator = AnimatorInflater.loadAnimator(this, R.animator.loading);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.setTarget(dialogIv);
    }

    public void checkNetStatus() {
        showVideoLoading();
        tipsTv.setVisibility(View.VISIBLE);
        checkNetBtn.setVisibility(View.INVISIBLE);
        showDetailTv.setVisibility(View.INVISIBLE);
        reCheckTv.setVisibility(View.INVISIBLE);
        resultIv.setVisibility(View.INVISIBLE);

        tipsTv.setText("自动检测中");
        dialogIv.setImageDrawable(getResources().getDrawable(R.drawable.icon_setting_net_loading));
        if (!isRunning) {
            String ua = System.getProperty("http.agent");
            int code = Curl.trace("https://api-02.wmlive.cn/opus/play?id=1609503887&sign=cd0c541d69e6a22a252542b077ef8a56", ua);
            reslog = Curl.tracegetres(code);
            String ip = Curl.getfinalip();
            if (TextUtils.isEmpty(ip)) {
                ip = "api-02.wmlive.cn";
            }
            _netDiagnoService = new LDNetDiagnoService(getApplicationContext(),
                    HeaderUtils.getAppName(), HeaderUtils.getAppName(), HeaderUtils.getAppVersion(), AccountUtil.getDcNum(),
                    GlobalParams.StaticVariable.sUniqueDeviceId, ip, GlobalParams.StaticVariable.netName, "ISOCountyCode",
                    "MobilCountryCode", "MobileNetCode", this);
            // 设置是否使用JNIC 完成traceroute
            _netDiagnoService.setIfUseJNICTrace(true);
            _netDiagnoService.execute();
            Curl.tracereset();
        } else {
            _netDiagnoService.cancel(true);
            _netDiagnoService = null;
        }

        isRunning = !isRunning;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_network_check;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(R.string.setting_network_check, true);
        netCheckPresenter = new NetCheckPresenter(this);
        addPresenter(netCheckPresenter);
        bindListener();
        initVideoLoading();
        new RxPermissions(this)
                .request(Manifest.permission.READ_PHONE_STATE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                        } else {
                            ToastUtil.showToast("请在应用程序管理中开启读取手机信息权限");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void bindListener() {
        checkNetBtn.setOnClickListener(this);
        showDetailTv.setOnClickListener(this);
        reCheckTv.setOnClickListener(this);
    }

    @Override
    public void OnNetDiagnoFinished(String log) {
        checkInfoDetais = log + reslog;
        dismissVideoLoading();
        netCheckPresenter.uploadNetCheckLog(log);
    }


    @Override
    public void OnNetDiagnoUpdated(String log) {
    }

    @Override
    public void handleNetlogUploadSucceed(String msg) {
        tipsTv.setText("检测完成,结果已上报");
        showDetailTv.setVisibility(View.VISIBLE);
        reCheckTv.setVisibility(View.VISIBLE);
        checkNetBtn.setVisibility(View.GONE);
        resultIv.setVisibility(View.VISIBLE);
        resultIv.setImageDrawable(getResources().getDrawable(R.drawable.icon_setting_net_success));
        //showToast(msg);
    }

    @Override
    public void handleNetlogUploadFailure(String error_msg) {
        tipsTv.setText("检测完成,上传失败");
        showDetailTv.setVisibility(View.VISIBLE);
        reCheckTv.setVisibility(View.VISIBLE);
        checkNetBtn.setVisibility(View.GONE);
        resultIv.setVisibility(View.VISIBLE);
        resultIv.setImageDrawable(getResources().getDrawable(R.drawable.icon_setting_net_fail));
        showToast(error_msg);
    }


    public void dismissVideoLoading() {
        if (loadingAnimator != null) {
            loadingAnimator.cancel();
            loadingAnimator = null;
        }
        if (dialogIv != null) {
            dialogIv.clearAnimation();
            dialogIv.setVisibility(View.INVISIBLE);
        }
    }

    public void showVideoLoading() {
        if (dialogIv != null) {
            dialogIv.setVisibility(View.VISIBLE);
        }
        initVideoLoading();
        loadingAnimator.start();
    }
}
