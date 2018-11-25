package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by XueFei on 2017/5/31.
 * <p>
 * 个人主页
 */

public class UserHomeResponse extends BaseResponse {
    private UserInfo user_info;

    public UserHomeResponse() {
    }

    public UserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfo user_info) {
        this.user_info = user_info;
    }

    @Override
    public String toString() {
        return "UserHomeResponse{" +
                "user_info=" + user_info +
                '}';
    }
}
