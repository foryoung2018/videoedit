package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by XueFei on 2017/8/1.
 */

public class UserAccountChargeCreateOrderResponse extends BaseResponse {
    private String data;

    public UserAccountChargeCreateOrderResponse() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
