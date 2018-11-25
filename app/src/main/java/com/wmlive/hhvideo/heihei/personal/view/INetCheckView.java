package com.wmlive.hhvideo.heihei.personal.view;

import com.wmlive.hhvideo.common.base.BaseView;

/**
 * Created by jht on 2018/9/9.
 * <p>
 * 网络检测
 */

public interface INetCheckView extends BaseView {

    /**
     * 网络检测日志上传
     */
    void handleNetlogUploadSucceed(String msg);

    void handleNetlogUploadFailure(String error_msg);
}
