package com.wmlive.hhvideo.heihei.personal.view;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeCreateOrderResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeResponse;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 充值列表
 */

public interface IUserAccountChargeView extends BaseView {
    void handleChargeListSucceed(UserAccountChargeResponse response);

    void handleChargeListFailure(String error_msg);

    void handleChargeCreateOrderSucceed(UserAccountChargeCreateOrderResponse response);

    void handleChargeCreateOrderFailure(long id, String error_msg);
}
