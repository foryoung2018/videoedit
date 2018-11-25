package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by XueFei on 2017/6/1.
 * <p>
 * 更改用户信息
 */

public class UpdateUserResponse extends BaseResponse {
    private UserInfo user_info;

    public UpdateUserResponse() {
    }

    public UserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfo user_info) {
        this.user_info = user_info;
    }

    @Override
    public String toString() {
        return "UpdateUserResponse{" +
                "user_info=" + user_info +
                '}';
    }
}
