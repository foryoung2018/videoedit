package cn.wmlive.hhvideo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wmlive.hhvideo.heihei.personal.fragment.UserAccountChargeFragment;
import com.wmlive.hhvideo.heihei.personal.pay.WechatPayUtil;
import com.wmlive.hhvideo.utils.ToastUtil;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WechatPayUtil.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Intent intent = new Intent(UserAccountChargeFragment.WECHAT_PAY_SUCCEED);
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                ToastUtil.showToast("支付成功");
                intent.putExtra("result", 1);

            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                ToastUtil.showToast("支付取消");
                intent.putExtra("result", 0);
            } else {
                ToastUtil.showToast("支付失败");
                intent.putExtra("result", -1);
            }
            sendBroadcast(intent);
        }
        finish();
    }
}