package com.wmlive.hhvideo.service.view;

import com.wmlive.hhvideo.common.base.BaseView;

/**
 * 上送用户行为结果回掉
 * Created by vhawk on 2017/6/20.
 */
public interface UserBehaviorView extends BaseView {

    /**
     * 上传用户行为成功
     */
    void handleUserBehaviorSucceed(String message);

    /**
     * 上传用户行为失败
     *
     * @param message
     */
    void handleUserBehaviorFailure(String message);

}
