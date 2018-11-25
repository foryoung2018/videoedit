package com.wmlive.hhvideo.heihei.beans.opus;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.networklib.util.EventHelper;

/**
 * 分享事件
 */
public class ShareEventEntity extends BaseModel {
    public static final String TYPE_OPUS = "opus";
    public static final String TYPE_TOPIC = "topic";
    public static final String TYPE_MY = "my";
    public static final String TYPE_USER_HOME = "user_home";

    public static final String TARGET_WECHAT = "wechat";
    public static final String TARGET_FRIEND = "friend";
    public static final String TARGET_WEIBO = "weibo";
    public static final String TARGET_QQ = "qq";
    public static final String TARGET_WEB = "web";

    public String shareUuid;//分享信息中获取的uuid
    public String objId;//分享对象的ID
    public String objType;//分享对象类型：’opus’, ‘topic’, ‘my’, ‘user_home’
    public String shareTarget;//分享目标: ‘wechat’, ‘friend’, ‘weibo’, ‘qq’, ‘web’

    public boolean isSuccess;//分享成功
    public boolean needUpload = true;//是否需要调用分享上传接口

    public static void share(ShareInfo shareInfo) {
        ShareEventEntity shareEventEntity = new ShareEventEntity();
        shareEventEntity.shareUuid = shareInfo.share_uuid;
        shareEventEntity.objId = String.valueOf(shareInfo.objId);
        shareEventEntity.objType = shareInfo.share_obj_type;
        shareEventEntity.shareTarget = shareInfo.shareTarget;
        shareEventEntity.isSuccess = true;
        shareEventEntity.needUpload = shareInfo.needUpload;
        EventHelper.post(GlobalParams.EventType.TYPE_SHARE_EVENT, shareEventEntity);
    }

    /**
     * 取消分享
     *
     * @param shareInfo
     */
    public static void shareCancel(ShareInfo shareInfo) {
        ShareEventEntity shareEventEntity = new ShareEventEntity();
        shareEventEntity.shareUuid = shareInfo.share_uuid;
        shareEventEntity.objId = String.valueOf(shareInfo.objId);
        shareEventEntity.objType = shareInfo.share_obj_type;
        shareEventEntity.shareTarget = shareInfo.shareTarget;
        shareEventEntity.isSuccess = false;
        shareEventEntity.needUpload = shareInfo.needUpload;
        EventHelper.post(GlobalParams.EventType.TYPE_SHARE_EVENT, shareEventEntity);
    }

    @Override
    public String toString() {
        return "ShareEventEntity{" +
                "shareUuid='" + shareUuid + '\'' +
                ", objId='" + objId + '\'' +
                ", objType='" + objType + '\'' +
                ", shareTarget='" + shareTarget + '\'' +
                '}';
    }
}
