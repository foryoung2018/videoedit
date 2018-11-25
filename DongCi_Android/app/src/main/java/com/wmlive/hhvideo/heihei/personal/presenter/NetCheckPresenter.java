package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.personal.view.INetCheckView;
import com.wmlive.hhvideo.utils.HeaderUtils;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by jht on 2018/9/9.
 * <p>
 * 网络检测日志上传
 */

public class NetCheckPresenter extends BasePresenter<INetCheckView> {
    public NetCheckPresenter(INetCheckView view) {
        super(view);
    }

    /**
     * 上传网络检测日志
     */
    public void uploadNetCheckLog(String content) {
        String url = InitCatchData.getInitCatchData().log.getNetworkCheckLog();
        executeRequest(HttpConstant.TYPE_PERSONAL_SIGNOUT, getHttpApi().sendNetCheckLog(url,
                GlobalParams.StaticVariable.sUniqueDeviceId, content, HeaderUtils.getOsPlatform(),
                HeaderUtils.getAppVersion(), HeaderUtils.getDeviceModel()))
                .subscribe(new DCNetObserver<BaseResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                        if (null != viewCallback) {
                            viewCallback.handleNetlogUploadSucceed(message);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.handleNetlogUploadFailure(message);
                        }
                    }
                });
    }
}
