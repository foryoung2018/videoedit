package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by XueFei on 2017/8/1.
 * <p>
 * 账户信息类
 */

public class UserAccountResponse extends BaseResponse {
    private UserAccountEntity user_gold_account;

    public UserAccountResponse() {
    }

    public UserAccountEntity getUser_gold_account() {
        return user_gold_account;
    }

    public void setUser_gold_account(UserAccountEntity user_gold_account) {
        this.user_gold_account = user_gold_account;
    }

    @Override
    public String toString() {
        return "UserAccountResponse{" +
                "user_gold_account=" + user_gold_account +
                '}';
    }
}
