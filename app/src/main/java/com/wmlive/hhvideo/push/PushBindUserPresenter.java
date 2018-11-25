package com.wmlive.hhvideo.push;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by XueFei on 2017/7/13.
 * <p>
 * 推送绑定用户
 */

public class PushBindUserPresenter extends BasePresenter<IPushBindUserView> {
    public PushBindUserPresenter(IPushBindUserView view) {
        super(view);
    }

    /**
     * 推送绑定用户
     *
     * @param device_uuid
     * @param apns_token
     * @param certype     :1:正式环境，0：测试环境
     * @param apns_type   : 推送平台，（jiguang， aliyun）
     */
    public void bindUser(String device_uuid, String apns_token, int certype, String apns_type) {
        executeRequest(HttpConstant.TYPE_JPUSH_BIND_USER, getHttpApi().bindUser(InitCatchData.sysDevicePushInfo(), device_uuid, apns_token, certype, apns_type))
                .subscribe(new DCNetObserver<BaseResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                        if (null != viewCallback) {
                            viewCallback.handleBindSucceed();
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {

                    }
                });
    }
}
