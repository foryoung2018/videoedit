package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;

public class TopicInfoBean extends BaseModel {
    /**
     * creator_id : 10016
     * default_music_id : 0
     * description : 冰糖葫芦娃
     * id : 2
     * name : 为所欲为
     * opus_count : 632
     * share_info : {"share_desc":"冰糖葫芦娃","share_title":"我在黑黑参加#为所欲为#，超多有趣音乐短视频！赶快来玩！戳这里>>","share_url":"http://api-02.wmlives.com/share/topic/2","share_weibo_desc":"我在黑黑参加#为所欲为#，超多有趣音乐短视频！赶快来玩！戳这里>>"}
     * topic_tag :
     * topic_type : 话题
     * user : {"cover_url":"","dc_num":"10000","description":"","honours":[],"id":10016,"name":"啊是大"}
     * visible : 1
     */

    private int creator_id;
    private int default_music_id;
    private String description;
    private String id = "0";
    private String name;
    private int opus_count;
    private ShareInfo share_info;
    private String topic_tag;
    private String topic_type;
    private UserInfo user;
    private int visible;
    private String cover_url;
    private String link;

    @Override
    public String toString() {
        return "TopicInfoBean{" +
                "creator_id=" + creator_id +
                ", default_music_id=" + default_music_id +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", opus_count=" + opus_count +
                ", share_info=" + share_info +
                ", topic_tag='" + topic_tag + '\'' +
                ", topic_type='" + topic_type + '\'' +
                ", user=" + user +
                ", visible=" + visible +
                ", cover_url='" + cover_url + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public int getDefault_music_id() {
        return default_music_id;
    }

    public void setDefault_music_id(int default_music_id) {
        this.default_music_id = default_music_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id == null || "null".equalsIgnoreCase(id) ? "0" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOpus_count() {
        return opus_count;
    }

    public void setOpus_count(int opus_count) {
        this.opus_count = opus_count;
    }

    public ShareInfo getShare_info() {
        return share_info;
    }

    public void setShare_info(ShareInfo share_info) {
        this.share_info = share_info;
    }

    public String getTopic_tag() {
        return topic_tag;
    }

    public void setTopic_tag(String topic_tag) {
        this.topic_tag = topic_tag;
    }

    public String getTopic_type() {
        return topic_type;
    }

    public void setTopic_type(String topic_type) {
        this.topic_type = topic_type;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }
}
