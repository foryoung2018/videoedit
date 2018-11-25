package com.wmlive.hhvideo.heihei.splash.view;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.splash.CheckDeviceResponse;

/**
 * Created by vhawk on 2017/5/22.
 */

public interface SplashView extends BaseView {

    void initApiError(String msg);

    void initApiSucceed();

    void onInitComplete();

    void onCheckDeviceIdOk(CheckDeviceResponse response);

}
