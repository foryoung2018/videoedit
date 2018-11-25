package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by lsq on 6/2/2017.
 */

public class TopicTypeListBean extends BaseResponse {

    private List<TopicListBean> topic_list;
    public String discover_btn;

    private List<UserInfo> recommend_users;

    public String peripheral_url;  //领取周边链接地址

    public List<ShortVideoItem> day_recommend;
    public List<TopicListBean> text_topic;
    public List<FocusBean> discover_focus;//聚焦列表数据集合

    public List<UserInfo> getRecommend_users() {
        return recommend_users;
    }

    public void setRecommend_users(List<UserInfo> recommend_users) {
        this.recommend_users = recommend_users;
    }

    public List<TopicListBean> getTopic_list() {
        return topic_list;
    }

    public void setTopic_list(List<TopicListBean> topic_list) {
        this.topic_list = topic_list;
    }

    public static TopicListBean newTopicListBean() {
        return new TopicListBean();
    }

    public static class TopicListBean extends BaseModel {
        public TopicListBean() {

        }

        /**
         * creator_id : 10016
         * default_music_id : 0
         * description : 冰糖葫芦娃
         * id : 2
         * name : 为所欲为
         * opus_count : 632
         * topic_opus_list : [{"at_user_ids":"","example":1,"id":10018,"is_delete":0,"like_count":1,"music_album_cover":"","music_id":23,"music_name":"","opus_cover":"static/image/1.jpg","opus_gif_cover":"static/image/1.jpg","opus_path":"static/opus/3.mp4","opus_small_cover":"","owner_id":10016,"play_count":3,"title":"瓦大喜哇","topic_id":2,"topic_name":"hello world","visible":1,"wonderful_tag":"精选"},{"at_user_ids":"10015","example":0,"id":10017,"is_delete":0,"like_count":0,"music_album_cover":"","music_id":1000,"music_name":"","opus_cover":"static/image/1.jpg","opus_gif_cover":"static/image/1.jpg","opus_path":"static/opus/3.mp4","opus_small_cover":"","owner_id":10017,"play_count":0,"title":"端午尬舞","topic_id":2,"topic_name":"sorry","visible":1,"wonderful_tag":"推荐"}]
         * topic_tag :
         * topic_type : 话题
         */

        public boolean isFirst;
        private String creator_id;
        private long default_music_id;
        private String description;
        private long id;
        private String name;
        private int opus_count;
        private String topic_tag;
        private String topic_type;
        private int visible;
        private List<ShortVideoItem> topic_opus_list;
        private ShareInfo share_info;

        public List<UserInfo> recommend_users;
        public List<ShortVideoItem> day_recommend;
        public List<FocusBean> discover_focus;
        public List<TopicListBean> text_topic;

        public String getCreator_id() {
            return creator_id;
        }

        public void setCreator_id(String creator_id) {
            this.creator_id = creator_id;
        }

        public long getDefault_music_id() {
            return default_music_id;
        }

        public void setDefault_music_id(long default_music_id) {
            this.default_music_id = default_music_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
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

        public List<ShortVideoItem> getTopic_opus_list() {
            return topic_opus_list;
        }

        public void setTopic_opus_list(List<ShortVideoItem> topic_opus_list) {
            this.topic_opus_list = topic_opus_list;
        }

        public int getVisible() {
            return visible;
        }

        public void setVisible(int visible) {
            this.visible = visible;
        }

        public ShareInfo getShare_info() {
            return share_info;
        }

        public void setShare_info(ShareInfo share_info) {
            this.share_info = share_info;
        }
    }
}
