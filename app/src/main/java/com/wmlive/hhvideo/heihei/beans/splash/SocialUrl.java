package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/5/22.
 */

public class SocialUrl extends BaseModel {

    /**
     * getBanner : http://api.dongci-test.wmlives.com/api/social/get-banner
     */

    private String getBanner;
    private String giftInfo;
    private String giftResources;
    private String giftInfoV2;
    private String giftInfoForIm;
    private String loadSplash;
    private String creativeList;
    private String getCreativeSocial;

    public String getGetCreativeSocial() {
        return getCreativeSocial;
    }

    public void setGetCreativeSocial(String getCreativeSocial) {
        this.getCreativeSocial = getCreativeSocial;
    }


    public String getCreativeList() {
        return creativeList;
    }

    public void setCreativeList(String creativeList) {
        this.creativeList = creativeList;
    }

    public String getLoadSplash() {
        return loadSplash;
    }

    public void setLoadSplash(String loadSplash) {
        this.loadSplash = loadSplash;
    }

    public String getGetBanner() {
        return getBanner;
    }

    public void setGetBanner(String getBanner) {
        this.getBanner = getBanner;
    }

    public String getGiftInfo() {
        return giftInfo;
    }

    public void setGiftInfo(String giftInfo) {
        this.giftInfo = giftInfo;
    }

    public String getGiftResources() {
        return giftResources;
    }

    public void setGiftResources(String giftResources) {
        this.giftResources = giftResources;
    }

    public String getGiftInfoV2() {
        return giftInfoV2;
    }

    public void setGiftInfoV2(String giftInfoV2) {
        this.giftInfoV2 = giftInfoV2;
    }

    public String getGiftInfoForIm() {
        return giftInfoForIm;
    }

    public void setGiftInfoForIm(String giftInfoForIm) {
        this.giftInfoForIm = giftInfoForIm;
    }

    @Override
    public String toString() {
        return "SocialUrl{" +
                "getBanner='" + getBanner + '\'' +
                ", giftInfo='" + giftInfo + '\'' +
                ", giftResources='" + giftResources + '\'' +
                ", giftInfoV2='" + giftInfoV2 + '\'' +
                ", giftInfoForIm='" + giftInfoForIm + '\'' +
                ", loadSplash='" + loadSplash + '\'' +
                '}';
    }
}
