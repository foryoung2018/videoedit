package com.wmlive.hhvideo.heihei.beans.login;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by vhawk on 2017/5/23.
 */

public class LoginUserResponse extends BaseResponse {

    private String token;

    private UserInfo user_info;

    public String getToken() {
        return token;
    }

    public LoginUserResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public UserInfo getUser_info() {
        return user_info;
    }

    public LoginUserResponse setUser_info(UserInfo userInfo) {
        this.user_info = userInfo;
        return this;
    }

    @Override
    public String toString() {
        return "LoginUserResponse{" +
                "token='" + token + '\'' +
                ", userInfo=" + user_info +
                '}';
    }
}
