package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/5/22.
 */

public class SysUrl extends BaseModel {


    /**
     * updateCheck : http://api.dongci-test.wmlives.com/api/sys/update
     * uploadLog : http://api.dongci-test.wmlives.com/api/sys/upload-log
     * ossToken : http://api.dongci-test.wmlives.com/api/sys/oss-token
     * aboutUs : http://api.dongci-test.wmlives.com/static/app/social-pact.html
     * serviceTerms : http://api.dongci-test.wmlives.com/static/app/service-terms.html
     * devicePushInfo :  http://api.dongci-test.wmlives.com/api/sys/push-device
     */

    private String updateCheck;
    private String uploadLog;
    private String ossToken;
    private String aboutUs;
    private String serviceTerms;
    private String devicePushInfo;
    private String shareLog;
    private String usinghelp;
    private String reCheckDeviceInfo;
    private String reGetOssToken;

    public String getReGetOssToken() {
        return reGetOssToken;
    }

    public void setReGetOssToken(String reGetOssToken) {
        this.reGetOssToken = reGetOssToken;
    }

    public String getUpdateCheck() {
        return updateCheck;
    }

    public void setUpdateCheck(String updateCheck) {
        this.updateCheck = updateCheck;
    }

    public String getUploadLog() {
        return uploadLog;
    }

    public void setUploadLog(String uploadLog) {
        this.uploadLog = uploadLog;
    }

    public String getOssToken() {
        return ossToken;
    }

    public void setOssToken(String ossToken) {
        this.ossToken = ossToken;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getServiceTerms() {
        return serviceTerms;
    }

    public void setServiceTerms(String serviceTerms) {
        this.serviceTerms = serviceTerms;
    }

    public String getDevicePushInfo() {
        return devicePushInfo;
    }

    public void setDevicePushInfo(String devicePushInfo) {
        this.devicePushInfo = devicePushInfo;
    }

    public String getShareLog() {
        return shareLog;
    }

    public void setShareLog(String shareLog) {
        this.shareLog = shareLog;
    }

    public String getUsinghelp() {
        return usinghelp;
    }

    public void setUsinghelp(String usinghelp) {
        this.usinghelp = usinghelp;
    }

    public String getReCheckDeviceInfo() {
        return reCheckDeviceInfo;
    }

    public void setReCheckDeviceInfo(String reCheckDeviceInfo) {
        this.reCheckDeviceInfo = reCheckDeviceInfo;
    }

    @Override
    public String toString() {
        return "SysUrl{" +
                "updateCheck='" + updateCheck + '\'' +
                ", uploadLog='" + uploadLog + '\'' +
                ", ossToken='" + ossToken + '\'' +
                ", aboutUs='" + aboutUs + '\'' +
                ", serviceTerms='" + serviceTerms + '\'' +
                ", devicePushInfo='" + devicePushInfo + '\'' +
                ", reCheckDeviceInfo='" + reCheckDeviceInfo + '\'' +
                '}';
    }
}
