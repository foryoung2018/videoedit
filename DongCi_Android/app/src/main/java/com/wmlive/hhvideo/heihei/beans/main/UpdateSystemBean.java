package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * 系统版本升级
 * Created by kangzhen on 2017/7/20.
 */

public class UpdateSystemBean extends BaseResponse {
    private UpdateInfo info;

    public UpdateInfo getInfo() {
        return info;
    }

    public void setInfo(UpdateInfo info) {
        this.info = info;
    }

    public UpdateSystemBean() {
    }

    public UpdateSystemBean(UpdateInfo info) {
        this.info = info;
    }
}
