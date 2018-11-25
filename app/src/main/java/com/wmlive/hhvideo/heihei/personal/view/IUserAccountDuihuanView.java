package com.wmlive.hhvideo.heihei.personal.view;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountDuihuanResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 兑换列表
 */

public interface IUserAccountDuihuanView extends BaseView {

    void handleDuihuanListSucceed(UserAccountDuihuanResponse response);

    void handleDuihuanListFailure(String error_msg);

    void handleDuihuanJinbiSucceed(UserAccountResponse response);

    void handleDuihuanJinbiFailure(long id,String error_msg);
}
