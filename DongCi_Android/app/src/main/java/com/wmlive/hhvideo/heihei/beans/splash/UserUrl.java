package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/5/22.
 */

public class UserUrl extends BaseModel {

    /**
     * getSMSVerificationCode : http://api.dongci-test.wmlives.com/api/user/get-sms-verification-code
     * signIn : http://api.dongci-test.wmlives.com/api/user/sign-in
     * signOut : http://api.dongci-test.wmlives.com/api/user/sign-out
     * signInCLByPhone : http://api.dongci-test.wmlives.com/api/user/sign-in-cl-by-phone
     * updateUser : http://api.dongci-test.wmlives.com/api/user/update-user
     * userHome : http://api.dongci-test.wmlives.com/api/user/home
     * getUser_info : http://api.dongci-test.wmlives.com/api/user/user-info
     * followUser : http://api.dongci-test.wmlives.com/api/user/follow
     * unFollowUsr : http://api.dongci-test.wmlives.com/api/user/unfollow
     * listFollower : http://api.dongci-test.wmlives.com/api/user/list-follower
     * listFans : http://api.dongci-test.wmlives.com/api/user/list-fans
     * listReportType : http://api.dongci-test.wmlives.com/api/user/list-report-type
     * report : http://api.dongci-test.wmlives.com/api/user/report
     * listUserOpus : http://api.dongci-test.wmlives.com/api/user/list-opus
     * listUserLike : http://api.dongci-test.wmlives.com/api/user/list-like
     * goldAccount：/api/user/gold-account
     * coCreateOpus:/api/user/list-co-create
     * verifyInvitationCode : http://api.dongci-test.wmlives.com/api/user/verify-invitation-code
     * blockUser=http://api.dongci-test.wmlives.com/api/user/block
     */

    private String getSMSVerificationCode;
    private String signIn;
    private String signOut;
    private String signInCLByPhone;
    private String updateUser;
    private String userHome;
    private String getUserInfo;
    private String followUser;
    private String unFollowUsr;
    private String listFollower;
    private String listFans;
    private String listReportType;
    private String report;
    private String listUserOpus;
    private String listUserLike;
    private String goldAccount;
    private String recommendUsers;
    private String coCreateOpus;
    private String verifyInvitationCode;
    private String blockUser;

    private String weiboVerified;
    private String bingWweibo;
    private String removeBindWeibo;
    private String userPointList;
    private String userBlacklist;
    private String batchFollowUser;

    public String getUserBlacklist() {
        return userBlacklist;
    }

    public void setUserBlacklist(String userBlacklist) {
        this.userBlacklist = userBlacklist;
    }

    public String getUserPointList() {
        return userPointList;
    }

    public void setUserPointList(String userPointList) {
        this.userPointList = userPointList;
    }

    public String getWeiboVerified() {
        return weiboVerified;
    }

    public void setWeiboVerified(String weiboVerified) {
        this.weiboVerified = weiboVerified;
    }

    public String getBingWweibo() {
        return bingWweibo;
    }

    public void setBingWweibo(String bingWweibo) {
        this.bingWweibo = bingWweibo;
    }

    public String getRemoveBindWeibo() {
        return removeBindWeibo;
    }

    public void setRemoveBindWeibo(String removeBindWeibo) {
        this.removeBindWeibo = removeBindWeibo;
    }

    public String getCoCreateOpus() {
        return coCreateOpus;
    }

    public void setCoCreateOpus(String coCreateOpus) {
        this.coCreateOpus = coCreateOpus;
    }

    public String getGetSMSVerificationCode() {
        return getSMSVerificationCode;
    }

    public void setGetSMSVerificationCode(String getSMSVerificationCode) {
        this.getSMSVerificationCode = getSMSVerificationCode;
    }

    public String getSignIn() {
        return signIn;
    }

    public void setSignIn(String signIn) {
        this.signIn = signIn;
    }

    public String getSignOut() {
        return signOut;
    }

    public void setSignOut(String signOut) {
        this.signOut = signOut;
    }

    public String getSignInCLByPhone() {
        return signInCLByPhone;
    }

    public void setSignInCLByPhone(String signInCLByPhone) {
        this.signInCLByPhone = signInCLByPhone;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUserHome() {
        return userHome;
    }

    public void setUserHome(String userHome) {
        this.userHome = userHome;
    }

    public String getGetUserInfo() {
        return getUserInfo;
    }

    public void setGetUserInfo(String getUserInfo) {
        this.getUserInfo = getUserInfo;
    }

    public String getFollowUser() {
        return followUser;
    }

    public void setFollowUser(String followUser) {
        this.followUser = followUser;
    }

    public String getUnFollowUsr() {
        return unFollowUsr;
    }

    public void setUnFollowUsr(String unFollowUsr) {
        this.unFollowUsr = unFollowUsr;
    }

    public String getListFollower() {
        return listFollower;
    }

    public void setListFollower(String listFollower) {
        this.listFollower = listFollower;
    }

    public String getListFans() {
        return listFans;
    }

    public void setListFans(String listFans) {
        this.listFans = listFans;
    }

    public String getListReportType() {
        return listReportType;
    }

    public void setListReportType(String listReportType) {
        this.listReportType = listReportType;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getListUserOpus() {
        return listUserOpus;
    }

    public void setListUserOpus(String listUserOpus) {
        this.listUserOpus = listUserOpus;
    }

    public String getListUserLike() {
        return listUserLike;
    }

    public void setListUserLike(String listUserLike) {
        this.listUserLike = listUserLike;
    }

    public String getGoldAccount() {
        return goldAccount;
    }

    public void setGoldAccount(String goldAccount) {
        this.goldAccount = goldAccount;
    }

    public String getRecommendUsers() {
        return recommendUsers;
    }

    public void setRecommendUsers(String recommendUsers) {
        this.recommendUsers = recommendUsers;
    }

    public String getVerifyInvitationCode() {
        return verifyInvitationCode;
    }

    public void setVerifyInvitationCode(String verifyInvitationCode) {
        this.verifyInvitationCode = verifyInvitationCode;
    }

    public String getBlockUser() {
        return blockUser;
    }

    public void setBlockUser(String blockUser) {
        this.blockUser = blockUser;
    }

    public String getBatchFollowUser() {
        return batchFollowUser;
    }

    public void setBatchFollowUser(String batchFollowUser) {
        this.batchFollowUser = batchFollowUser;
    }

    @Override
    public String toString() {
        return "UserUrl{" +
                "getSMSVerificationCode='" + getSMSVerificationCode + '\'' +
                ", signIn='" + signIn + '\'' +
                ", signOut='" + signOut + '\'' +
                ", signInCLByPhone='" + signInCLByPhone + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", userHome='" + userHome + '\'' +
                ", getUser_info='" + getUserInfo + '\'' +
                ", followUser='" + followUser + '\'' +
                ", unFollowUsr='" + unFollowUsr + '\'' +
                ", listFollower='" + listFollower + '\'' +
                ", listFans='" + listFans + '\'' +
                ", listReportType='" + listReportType + '\'' +
                ", reportUser='" + report + '\'' +
                ", listUserOpus='" + listUserOpus + '\'' +
                ", listUserLike='" + listUserLike + '\'' +
                ", recommendUsers='" + recommendUsers + '\'' +
                ", coCreateOpus='" + coCreateOpus + '\'' +
                ", verifyInvitationCode='" + verifyInvitationCode + '\'' +
                ", blockUser='" + blockUser + '\'' +
                ", batchFollowUser='" + batchFollowUser + '\'' +
                '}';
    }
}
