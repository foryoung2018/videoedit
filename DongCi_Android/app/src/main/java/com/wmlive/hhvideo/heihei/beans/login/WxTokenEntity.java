package com.wmlive.hhvideo.heihei.beans.login;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by lsq on 12/28/2017.1:07 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class WxTokenEntity extends BaseResponse {
    public String access_token;
    public String expires_in;
    public String openid;
    public String refresh_token;
    public String scope;
    public String unionid;

    @Override
    public String toString() {
        return "WxTokenEntity{" +
                "access_token='" + access_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", openid='" + openid + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", scope='" + scope + '\'' +
                ", unionid='" + unionid + '\'' +
                '}';
    }
}
