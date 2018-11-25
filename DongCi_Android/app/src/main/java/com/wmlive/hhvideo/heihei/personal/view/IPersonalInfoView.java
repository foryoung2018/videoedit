package com.wmlive.hhvideo.heihei.personal.view;

import com.wmlive.hhvideo.common.base.BaseView;

/**
 * Created by XueFei on 2017/6/1.
 * <p>
 * 个人信息
 */

public interface IPersonalInfoView extends BaseView {

    /**
     * 上传个人信息
     */
    void handleInfoSucceed();

    void handleInfoFailure(String error_msg);
}
