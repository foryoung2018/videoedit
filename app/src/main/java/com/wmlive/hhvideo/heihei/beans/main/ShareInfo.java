package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/6/7.
 */

public class ShareInfo extends BaseModel {


    /**
     * "share_weibo_desc": "是从学校习惯传在[动次]发了短视频，又想骗我看十遍",
     * "share_title": "是从学校习惯传在[动次]发了短视频，又想骗我看十遍",
     * "share_url": "http://m-dev.dongci-test.wmlives.com/#/share/opus/12043",
     * "share_desc": "动次短视频，互动新玩法！",
     * "share_image_url": "http://s1.wmlives.com/data/dongci/opus_cover/201709232211176LfIxR8TMZY.jpg!video_img_thumbnail",
     * "web_link": "http://m-dev.dongci-test.wmlives.com/web/share?obj_type=opus&share_from_uid=10017&obj_id=12043&share_target=web&share_uuid=73e93b00416411e89114184f32f22864/#/share/opus/12043",
     * <p>
     * <p>
     * "share_friend": "http://m-dev.dongci-test.wmlives.com/web/share?obj_type=opus&share_from_uid=10017&obj_id=12043&share_target=friend&share_uuid=73e93b00416411e89114184f32f22864/#/share/opus/12043",
     * "share_wechat": "http://m-dev.dongci-test.wmlives.com/web/share?obj_type=opus&obj_id=12043&share_from_uid=10017&share_target=wecaht&share_uuid=73e93b00416411e89114184f32f22864#/share/opus/12043",
     * "share_weibo": "http://m-dev.dongci-test.wmlives.com/web/share?obj_type=opus&share_from_uid=10017&obj_id=12043&share_target=weibo&share_uuid=73e93b00416411e89114184f32f22864/#/share/opus/12043",
     * "share_qq": "http://m-dev.dongci-test.wmlives.com/web/share?obj_type=opus&share_from_uid=10017&obj_id=12043&share_target=qq&share_uuid=73e93b00416411e89114184f32f22864/#/share/opus/12043"
     * "share_uuid": "73e93b00416411e89114184f32f22864",
     * "share_obj_type": "opus",
     */

    public String share_weibo_desc;
    public String share_title;
    public String share_url;
    public String share_desc;
    public String share_image_url;
    public String web_link;

    public String share_friend;
    public String share_wechat;
    public String share_weibo;
    public String share_qq;
    public String share_obj_type;
    public String share_uuid;

    public boolean needUpload = true;//是否需要调用分享上传接口
    public String shareType;
    public String shareTarget;
    public long objId;//分享对象的id
    public String download_link;//附加数据：分享的下载链接
    public WxMinAppShareInfo wxprogram_share_info;

    @Override
    public String toString() {
        return "ShareInfo{" +
                "share_weibo_desc='" + share_weibo_desc + '\'' +
                ", share_title='" + share_title + '\'' +
                ", share_url='" + share_url + '\'' +
                ", share_desc='" + share_desc + '\'' +
                ", share_image_url='" + share_image_url + '\'' +
                ", web_link='" + web_link + '\'' +
                ", share_friend='" + share_friend + '\'' +
                ", share_wechat='" + share_wechat + '\'' +
                ", share_weibo='" + share_weibo + '\'' +
                ", share_qq='" + share_qq + '\'' +
                ", share_obj_type='" + share_obj_type + '\'' +
                ", share_uuid='" + share_uuid + '\'' +
                ", needUpload=" + needUpload +
                ", shareType='" + shareType + '\'' +
                ", shareTarget='" + shareTarget + '\'' +
                ", objId=" + objId +
                ", download_link='" + download_link + '\'' +
                ", wxprogram_share_info=" + wxprogram_share_info +
                '}';
    }
}
