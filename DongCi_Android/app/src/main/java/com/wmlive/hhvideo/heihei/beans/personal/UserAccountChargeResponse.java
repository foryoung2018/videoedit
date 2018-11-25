package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 充值实体
 */

public class UserAccountChargeResponse extends BaseResponse {
    private List<UserAccountChargeEntry> pay_package;

    public UserAccountChargeResponse() {
    }

    public List<UserAccountChargeEntry> getPay_package() {
        return pay_package;
    }

    public void setPay_package(List<UserAccountChargeEntry> pay_package) {
        this.pay_package = pay_package;
    }

    @Override
    public String toString() {
        return "UserAccountChargeResponse{" +
                "pay_package=" + pay_package +
                '}';
    }
}
