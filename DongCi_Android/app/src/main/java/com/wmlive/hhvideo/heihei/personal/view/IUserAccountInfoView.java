package com.wmlive.hhvideo.heihei.personal.view;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 账户 信息
 */

public interface IUserAccountInfoView extends BaseView {

    void handleInfoSucceed(UserAccountResponse response);

    void handleInfoFailure(String error_msg);
}
