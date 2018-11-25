package com.wmlive.hhvideo.heihei.beans.splash;

import com.alibaba.fastjson.JSON;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.personal.ReportEntry;
import com.wmlive.hhvideo.heihei.beans.personal.ReportType;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

import java.util.List;

/**
 * 所有接口的url
 * Modify by lsq
 */

public class InitCatchData {
    public static final String KEY_INIT_CATCH_DATA = "INIT_CATCH_DATA";
    public static final String INVALID_URI = "http://0.0.0.0";
    private static InitUrlResponse sInitResponse;

    public static InitUrlResponse setInitUrl(InitUrlResponse initResponse) {
        sInitResponse = initResponse;
        return sInitResponse;
    }

    public static void saveInitData(InitUrlResponse initResponse) {
        if (null != initResponse) {
            sInitResponse = initResponse;
            try {
                SPUtils.putString(DCApplication.getDCApp(), KEY_INIT_CATCH_DATA, JSON.toJSONString(initResponse));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static InitUrlResponse getInitCatchData() {
        return getInitCatchData(true);
    }

    public static InitUrlResponse getInitCatchData(boolean initFromLocal) {
        if (sInitResponse == null) {
            if (initFromLocal) {
                try {
                    long start = System.currentTimeMillis();
                    sInitResponse = JSON.parseObject(SPUtils.getString(DCApplication.getDCApp(), KEY_INIT_CATCH_DATA, ""), InitUrlResponse.class);

                    KLog.i("===解析本地的init接口：" + (sInitResponse != null ? sInitResponse.toString() : "null") + "\ntime:" + (System.currentTimeMillis() - start));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sInitResponse;
    }

    private static UserUrl getUserUrl() {
        return getInitCatchData() != null ? getInitCatchData().getUser() : null;
    }


    private static OpusUrl getOpusUrl() {
        return getInitCatchData() != null ? getInitCatchData().getOpus() : null;
    }

    private static MusicUrl getMusicUrl() {
        return getInitCatchData() != null ? getInitCatchData().getMusic() : null;
    }

    public static SocialUrl getSocialUrl() {
        return getInitCatchData() != null ? getInitCatchData().getSocial() : null;
    }

    private static TopicUrl getTopicUrl() {
        return getInitCatchData() != null ? getInitCatchData().getTopic() : null;
    }

    private static SearchUrl getSearchUrl() {
        return getInitCatchData() != null ? getInitCatchData().getSearch() : null;
    }

    private static SysUrl getSysUrl() {
        return getInitCatchData() != null ? getInitCatchData().getSys() : null;
    }

    private static Function getFunction() {
        return getInitCatchData() != null ? getInitCatchData().getFunction() : null;
    }

    private static PayUrl getPayUrl() {
        return getInitCatchData() != null ? getInitCatchData().getPay() : null;
    }

    private static TipsUrl getTipsUrl() {
        return getInitCatchData() != null ? getInitCatchData().getTips() : null;
    }

    //user
    public static String getUserPointList() {
        return getUserUrl() != null ? getUserUrl().getUserPointList() : INVALID_URI;
    }

    public static String getWeiboVerified() {
        return getUserUrl() != null ? getUserUrl().getWeiboVerified() : INVALID_URI;
    }

    public static String getReCheckDeviceInfo() {
        return getSysUrl() != null ? getSysUrl().getReCheckDeviceInfo() : null;
    }

    public static String userGetSMSVerificationCode() {
        return getUserUrl() != null ? getUserUrl().getGetSMSVerificationCode() : INVALID_URI;
    }

    public static String userSignIn() {
        return getUserUrl() != null ? getUserUrl().getSignIn() : INVALID_URI;
    }

    public static String userSignOut() {
        return getUserUrl() != null ? getUserUrl().getSignOut() : INVALID_URI;
    }

    public static String userSignInCLByPhone() {
        return getUserUrl() != null ? getUserUrl().getSignInCLByPhone() : INVALID_URI;
    }

    public static String userUpdateUser() {
        return getUserUrl() != null ? getUserUrl().getUpdateUser() : INVALID_URI;
    }

    public static String userUserHome() {
        return getUserUrl() != null ? getUserUrl().getUserHome() : INVALID_URI;
    }

    public static String bindWeibo() {
        return getUserUrl() != null ? getUserUrl().getBingWweibo() : INVALID_URI;
    }

    public static String unBindWeibo() {
        return getUserUrl() != null ? getUserUrl().getRemoveBindWeibo() : INVALID_URI;
    }

    public static String userGetUserInfo() {
        return getUserUrl() != null ? getUserUrl().getGetUserInfo() : INVALID_URI;
    }

    public static String userFollowUser() {
        return getUserUrl() != null ? getUserUrl().getFollowUser() : INVALID_URI;
    }

    public static String getBatchFollowUser() {
        return getUserUrl() != null ? getUserUrl().getBatchFollowUser() : INVALID_URI;
    }

    public static String userUnFollowUsr() {
        return getUserUrl() != null ? getUserUrl().getUnFollowUsr() : INVALID_URI;
    }

    public static String userListFollower() {
        return getUserUrl() != null ? getUserUrl().getListFollower() : INVALID_URI;
    }

    public static String userListFans() {
        return getUserUrl() != null ? getUserUrl().getListFans() : INVALID_URI;
    }

    public static String userListReportType() {
        return getUserUrl() != null ? getUserUrl().getListReportType() : INVALID_URI;
    }

    public static String userReport() {
        return getUserUrl() != null ? getUserUrl().getReport() : INVALID_URI;
    }

    public static String blockUser() {
        return getUserUrl() != null ? getUserUrl().getBlockUser() : INVALID_URI;
    }

    public static String userListUserOpus() {
        return getUserUrl() != null ? getUserUrl().getListUserOpus() : INVALID_URI;
    }

    public static String userListUserLike() {
        return getUserUrl() != null ? getUserUrl().getListUserLike() : INVALID_URI;
    }

    public static String userVerifyInvitationCode() {
        return getUserUrl() != null ? getUserUrl().getVerifyInvitationCode() : INVALID_URI;
    }

    public static String getUserBlacklist() {
        return getUserUrl() != null ? getUserUrl().getUserBlacklist() : INVALID_URI;
    }

    public static ReportEntry userReposrtList() {
        return getInitCatchData() != null ? getInitCatchData().getConf_data() : new ReportEntry();
    }

    public static String getGoldAccount() {
        return getUserUrl() != null ? getUserUrl().getGoldAccount() : INVALID_URI;
    }

    public static String getCoCreateOpus() {
        return getUserUrl() != null ? getUserUrl().getCoCreateOpus() : INVALID_URI;
    }

    //opus
    public static String opusSaveOpus() {
        return getOpusUrl() != null ? getOpusUrl().getSaveOpus() : INVALID_URI;
    }

    public static String opusListOpusByRecommend() {
        return getOpusUrl() != null ? getOpusUrl().getListOpusByRecommend() : INVALID_URI;
    }

    public static String getOpusTopList() {
        return getOpusUrl() != null ? getOpusUrl().getOpusTopList() : INVALID_URI;
    }

    public static String opusListOpusByRecommendV2() {
        return getOpusUrl() != null ? getOpusUrl().getListOpusByRecommendV2() : INVALID_URI;
    }

    public static String opusListOpusByFollow() {
        return getOpusUrl() != null ? getOpusUrl().getListOpusByFollow() : INVALID_URI;
    }

    public static String opusListOpusByTime() {
        return getOpusUrl() != null ? getOpusUrl().getListOpusByTime() : INVALID_URI;
    }

    public static String opusGetOpus() {
        return getOpusUrl() != null ? getOpusUrl().getGetOpus() : INVALID_URI;
    }

    public static String opusViewOpus() {
        return getOpusUrl() != null ? getOpusUrl().getViewOpus() : INVALID_URI;
    }

    public static String opusLikeOpus() {
        return getOpusUrl() != null ? getOpusUrl().getLikeOpus() : INVALID_URI;
    }

    public static String opusDeleteOpus() {
        return getOpusUrl() != null ? getOpusUrl().getDeleteOpus() : INVALID_URI;
    }

    public static String opusModifyOpus() {
        return getOpusUrl() != null ? getOpusUrl().getModifyOpus() : INVALID_URI;
    }

    public static String opusCommendOpus() {
        return getOpusUrl() != null ? getOpusUrl().getCommendOpus() : INVALID_URI;
    }

    public static String opusListOpusComment() {
        return getOpusUrl() != null ? getOpusUrl().getListOpusComment() : INVALID_URI;
    }

    public static String opusOpusPointList() {
        return getOpusUrl() != null ? getOpusUrl().getOpusPointList() : INVALID_URI;
    }

    public static String opusDeleteComment() {
        return getOpusUrl() != null ? getOpusUrl().getDeleteComment() : INVALID_URI;
    }

    public static String opusListReportType() {
        return getOpusUrl() != null ? getOpusUrl().getListReportType() : INVALID_URI;
    }

    public static String opusReport() {
        return getOpusUrl() != null ? getOpusUrl().getReport() : INVALID_URI;
    }

    public static String opusManage() {
        return getOpusUrl() != null ? getOpusUrl().getManageOpus() : INVALID_URI;
    }

    public static String buyGiftBatch() {
        return getOpusUrl() != null ? getOpusUrl().getBuyGiftBatch() : INVALID_URI;
    }

    public static String opusLikeComment() {
        return getOpusUrl() != null ? getOpusUrl().getLikeComment() : INVALID_URI;
    }

    //music
    public static String musicListOpusByMusic() {
        return getMusicUrl() != null ? getMusicUrl().getListOpusByMusic() : INVALID_URI;
    }

    public static String musicMyFavMusic() {
        return getMusicUrl() != null ? getMusicUrl().getMyFavMusic() : INVALID_URI;
    }

    public static String musicGetMusicByCat() {
        return getMusicUrl() != null ? getMusicUrl().getGetMusicByCat() : INVALID_URI;
    }

    public static String musicGetCategory() {
        return getMusicUrl() != null ? getMusicUrl().getGetCategory() : INVALID_URI;
    }

    public static String musicFavMusic() {
        return getMusicUrl() != null ? getMusicUrl().getFavMusic() : INVALID_URI;
    }

    public static String musicSearch() {
        return getMusicUrl() != null ? getMusicUrl().getSearch() : INVALID_URI;
    }

    //social
    public static String socialGetBanner() {
        return getSocialUrl() != null ? getSocialUrl().getGetBanner() : INVALID_URI;
    }

    public static String getGiftResources() {
        return getSocialUrl() != null ? getSocialUrl().getGiftResources() : INVALID_URI;
    }

    public static String getGiftInfoV2() {
        return getSocialUrl() != null ? getSocialUrl().getGiftInfoV2() : INVALID_URI;
    }

    public static String getGiftInfoForIm() {
        return getSocialUrl() != null ? getSocialUrl().getGiftInfoForIm() : INVALID_URI;
    }

    public static String getLoadSplash() {
        return getSocialUrl() != null ? getSocialUrl().getLoadSplash() : INVALID_URI;
    }

    public static String getCreativeList() {
        return getSocialUrl() != null ? getSocialUrl().getCreativeList() : INVALID_URI;
    }

    /**
     * 获取礼物列表
     *
     * @return
     */
    public static String socialGrGiftInfo() {
        return getSocialUrl() != null ? getSocialUrl().getGiftInfo() : INVALID_URI;
    }

    //topic
    public static String topicListTopic() {
        return getTopicUrl() != null ? getTopicUrl().getListTopic() : INVALID_URI;
    }

    public static String topicListOpusByTopic() {
        return getTopicUrl() != null ? getTopicUrl().getListOpusByTopic() : INVALID_URI;
    }

    public static String topicSearch() {
        return getTopicUrl() != null ? getTopicUrl().getSearch() : INVALID_URI;
    }

    public static String getListSystemNews() {
        return getTopicUrl() != null ? getTopicUrl().getListSystemNews() : INVALID_URI;
    }

    public static String topicCreate() {
        return getTopicUrl() != null ? getTopicUrl().getCreate() : INVALID_URI;
    }

    public static String newTopicCheck() {
        return getTopicUrl() != null ? getTopicUrl().getNewTopicCheck() : INVALID_URI;
    }

    //search
    public static String searchSearch() {
        return getSearchUrl() != null ? getSearchUrl().getSearch() : INVALID_URI;
    }

    //sys
    public static String sysAboutUs() {
        return getSysUrl() != null ? getSysUrl().getAboutUs() : INVALID_URI;
    }

    public static String sysUpdateCheck() {
        return getSysUrl() != null ? getSysUrl().getUpdateCheck() : INVALID_URI;
    }

    public static String sysUploadLog() {
        return getSysUrl() != null ? getSysUrl().getUploadLog() : INVALID_URI;
    }

    public static String sysOssToken() {
        return getSysUrl() != null ? getSysUrl().getOssToken() : INVALID_URI;
    }

    public static String sysReOssToken() {
        return getSysUrl() != null ? getSysUrl().getReGetOssToken() : INVALID_URI;
    }

    public static String sysServiceTerms() {
        return getSysUrl() != null ? getSysUrl().getServiceTerms() : INVALID_URI;
    }

    public static String sysDevicePushInfo() {
        return getSysUrl() != null ? getSysUrl().getDevicePushInfo() : INVALID_URI;
    }

    public static String getUsinghelp() {
        return getSysUrl() != null ? getSysUrl().getUsinghelp() : INVALID_URI;
    }

    public static String getShareLog() {
        return getSysUrl() != null ? getSysUrl().getShareLog() : INVALID_URI;
    }

    //function
    public static boolean functionHhlog() {
        return getFunction() != null ? getFunction().isHhlog() : false;
    }

    public static boolean functionChat() {
        return getFunction() != null ? getFunction().isChat() : false;
    }

    public static String opusLogs() {
        return getOpusUrl() != null ? getOpusUrl().getOpusLogs() : INVALID_URI;
    }

    //举报的类型列表
    public static List<ReportType> getReportEntry() {
        return sInitResponse != null ? (sInitResponse.getConf_data() != null ? sInitResponse.getConf_data().getReport_type() : null) : null;
    }

    public static void setReportEntry(List<ReportType> list) {
        if (null != sInitResponse) {
            if (sInitResponse.getConf_data() == null) {
                sInitResponse.setConf_data(new ReportEntry());
            }
            sInitResponse.getConf_data().setReport_type(list);
        }
    }

    //获取message对象
    public static MessageUrl getMessageObj() {
        return getInitCatchData() != null ? getInitCatchData().getMessage() : null;
    }

    /**
     * 获取websocketServer 地址
     *
     * @return
     */
    public static String getWebSocketServer() {
        return getMessageObj() != null ? getMessageObj().getWebSocketServer() : INVALID_URI;
    }

    // 发送IM消息
    public static String getSendImMessage() {
        return getMessageObj() != null ? getMessageObj().getSendImMessage() : INVALID_URI;
    }

    // IM中购买礼物
    public static String getImBuyGiftBatch() {
        return getMessageObj() != null ? getMessageObj().getImBuyGiftBatch() : INVALID_URI;
    }

    // im页banner
    public static String getImBanner() {
        return getMessageObj() != null ? getMessageObj().getImBanner() : INVALID_URI;
    }

    public static String getImFollowerSearch() {
        return getMessageObj() != null ? getMessageObj().getImFollowerSearch() : INVALID_URI;
    }

    //start pay
    //获取充值列表
    public static String getPaypackageList() {
        return getPayUrl() != null ? getPayUrl().getPaypackageList() : INVALID_URI;
    }

    //获取兑换列表
    public static String getP2gpackageList() {
        return getPayUrl() != null ? getPayUrl().getP2gpackageList() : INVALID_URI;
    }

    //兑换金币
    public static String getDuihuanJinbi() {
        return getPayUrl() != null ? getPayUrl().getP2g() : INVALID_URI;
    }

    //创建订单
    public static String getCcreateOrder() {
        return getPayUrl() != null ? getPayUrl().getCreateOrder() : INVALID_URI;
    }
    //end pay

    //tips
    public static String getSearchTips() {
        return getTipsUrl() != null ? getTipsUrl().getSearchTips() : INVALID_URI;
    }

    public static PayUrl getPayTips() {
        return getTipsUrl() != null ? getTipsUrl().getPay() : null;
    }

    public static String getUploadMaterial() {
        return getOpusUrl() != null ? getOpusUrl().getOpusUploadMaterial() : INVALID_URI;
    }

    public static String getPublishProduct() {
        return getOpusUrl() != null ? getOpusUrl().getSaveOpus() : INVALID_URI;
    }

    public static String getPublishMvProduct() {
        return getOpusUrl() != null ? getOpusUrl().getUploadCreativeOpus() : INVALID_URI;
    }

    public static String getUploadLocalOpus() {
        return getOpusUrl() != null ? getOpusUrl().getUploadLocalOpus() : INVALID_URI;
    }

    public static String getOpusMaterial() {
        return getOpusUrl() != null ? getOpusUrl().getGetOpusMaterial() : INVALID_URI;
    }

    public static String getOpusFrameLayout() {
        return getOpusUrl() != null ? getOpusUrl().getOpusFrameLayout() : INVALID_URI;
    }

    public static String getRecommendUsers() {
        return getUserUrl() != null ? getUserUrl().getRecommendUsers() : INVALID_URI;
    }
}
