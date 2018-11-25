package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * 分享小程序实体
 * Author：create by jht on 2018/9/11 21:01
 * Email：haitian.jiang@welines.cn
 */
public class WxMinAppShareInfo extends BaseModel {

    /**
     * description :
     * path : /pages/detail/main?id=1626959574
     * thumb_data : http://s1.wmlives.com/data/dongci/opus_cover/2018061115100701LVWlBp5MK.jpg!video_cover_img
     * title : 花花花花 - 草丛
     * user_name : gh_1366becac5cd
     */

    public String description;
    public String path;
    public String thumb_data;
    public String title;
    public String user_name;
    public String webpage_url;

    public String getWebpage_url() {
        return webpage_url;
    }

    public void setWebpage_url(String webpage_url) {
        this.webpage_url = webpage_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb_data() {
        return thumb_data;
    }

    public void setThumb_data(String thumb_data) {
        this.thumb_data = thumb_data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
