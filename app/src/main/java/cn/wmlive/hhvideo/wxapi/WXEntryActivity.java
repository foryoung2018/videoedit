package cn.wmlive.hhvideo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.login.WxTokenEntity;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.CustomProgressDialog;
import com.wmlive.networklib.util.EventHelper;

import cn.wmlive.hhvideo.R;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler, WxPresenter.IWxView {
    private IWXAPI api;
    private WxPresenter presenter;
    private CustomProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, GlobalParams.Social.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);
        KLog.e("=WXEntryActivity=====onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        KLog.e("=WXEntryActivity=====onNewIntent=api:" + api);
        if (api != null) {
            api.handleIntent(intent, this);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        KLog.e("WXEntryActivity===onReq: " + baseReq);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        KLog.e("onResp: ");
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            //授权
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    String code = null;
                    if (baseResp instanceof SendAuth.Resp) {
                        code = ((SendAuth.Resp) baseResp).code;
                    }
                    showDialog();
                    KLog.i("===微信授权成功code:" + code);
                    if (!TextUtils.isEmpty(code)) {
                        if (presenter == null) {
                            presenter = new WxPresenter(this);
                        }
                        presenter.getWxToken(code);
                    } else {
                        ToastUtil.showToast(R.string.socialWechatLoginFail);
                        dismissFinish();
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ToastUtil.showToast(R.string.socialWechatAuthCancel);
                    KLog.i("===微信授权取消");
                    dismissFinish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    ToastUtil.showToast(R.string.socialWechatAuthDenied);
                    KLog.i("===微信授权被拒绝");
                    dismissFinish();
                    break;
                default:
                    dismissFinish();
                    break;
            }

        } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
            //分享
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // ToastUtil.showToast(R.string.socialWechatShareOk);
                    KLog.i("===微信分享成功");
                    if (BaseCompatActivity.shareInfo != null) {
                        ShareEventEntity.share(BaseCompatActivity.shareInfo);
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    KLog.i("===微信分享取消");
                    EventHelper.post(GlobalParams.EventType.TYPE_SHARE_CANCEL_EVENT);
                    // ToastUtil.showToast(R.string.socialWechatShareCancel);
                    if (BaseCompatActivity.shareInfo != null) {
                        ShareEventEntity.shareCancel(BaseCompatActivity.shareInfo);
                    }
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    KLog.i("===微信分享被拒绝");
                    // ToastUtil.showToast(R.string.socialWechatShareDenied);
                    break;
                default:
                    break;
            }
            dismissFinish();
        }
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new CustomProgressDialog(this);
        }
        dialog.setCancelable(false);
        if (!dialog.isShowing()) {
            dialog.loading();
        }
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        ToastUtil.showToast(message);
        dismissFinish();
    }

    @Override
    public void onGetWxTokenOk(WxTokenEntity entity) {
        if (presenter != null) {
            presenter.wxLogin(entity.access_token, entity.openid, entity.unionid);
        } else {
            dismissFinish();
        }
    }

    @Override
    public void onWxLoginOk(LoginUserResponse response) {
        AccountUtil.loginSuccess(response, AccountUtil.TYPE_WX);
        ToastUtil.showToast(R.string.login_suc);
        dismissFinish();
    }

    @Override
    public void onWxLoginFail(String message) {
        ToastUtil.showToast(message);
        dismissFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.i("====WXEntryActivity onDestroy");
        if (presenter != null) {
            presenter.destroy();
            presenter = null;
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void dismissFinish() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        finish();
    }
}
