package com.wmlive.hhvideo.heihei.personal.view;

import com.wmlive.hhvideo.common.base.BaseView;

/**
 * Created by XueFei on 2017/6/5.
 * <p>
 * 设置
 */

public interface ISettingView extends BaseView {

    /**
     * 退出登录
     */
    void handleLogoutSucceed();

    void handlerLogoutFailure(String error_msg);
}
