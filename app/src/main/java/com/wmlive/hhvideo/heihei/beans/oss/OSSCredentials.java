package com.wmlive.hhvideo.heihei.beans.oss;

/**
 * oss证书信息
 * Created by kangzhen on 2017/6/5.
 */

public class OSSCredentials {
    private String AccessKeySecret;
    private String SecurityToken;
    private String Expiration;
    private String AccessKeyId;

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        SecurityToken = securityToken;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String expiration) {
        Expiration = expiration;
    }

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        AccessKeyId = accessKeyId;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "AccessKeySecret='" + AccessKeySecret + '\'' +
                ", SecurityToken='" + SecurityToken + '\'' +
                ", Expiration='" + Expiration + '\'' +
                ", AccessKeyId='" + AccessKeyId + '\'' +
                '}';
    }
}
