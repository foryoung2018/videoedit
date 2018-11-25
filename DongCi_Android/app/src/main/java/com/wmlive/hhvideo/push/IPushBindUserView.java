package com.wmlive.hhvideo.push;

import com.wmlive.hhvideo.common.base.BaseView;

/**
 * Created by XueFei on 2017/7/13.
 * <p>
 * 推送绑定用户
 */

public interface IPushBindUserView extends BaseView {

    /**
     * 绑定设备ID--推送注册ID
     */
    void handleBindSucceed();

}
