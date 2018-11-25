package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 兑换实体
 */

public class UserAccountDuihuanResponse extends BaseResponse {
    private List<UserAccountDuihuanEntry> package_list;

    public UserAccountDuihuanResponse() {
    }

    public List<UserAccountDuihuanEntry> getPackage_list() {
        return package_list;
    }

    public void setPackage_list(List<UserAccountDuihuanEntry> package_list) {
        this.package_list = package_list;
    }

    @Override
    public String toString() {
        return "UserAccountDuihuanResponse{" +
                "package_list=" + package_list +
                '}';
    }
}
