package com.wmlive.hhvideo.heihei.beans.main;

import android.text.TextUtils;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.utils.CollectionUtil;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by vhawk on 2017/5/24.
 */

public class ShortVideoItem extends BaseModel {

    public static final String VIDEO_SHOW_TYPE_FULL_SCREEN = "full_screen";
    public static final String VIDEO_SHOW_TYPE_TOP_SCREEN = "top_screen";
    public static final String VIDEO_SHOW_TYPE_CENTER_SCREEN = "center_screen";

    public ShortVideoItem() {
    }

    /**
     * opus_small_cover :
     * music_id : 0
     * music_album_cover :
     * music_name :
     * title : 我们都是宇宙中的一颗小小的星辰。晚安。
     * topic_name :
     * like_count : 0
     * example : 0
     * play_count : 0
     * is_delete : 0
     * opus_cover : http://p1.pstatp.com/large/1ef100063df7c5fc27c6.jpeg
     * opus_path : https://api.amemv.com/aweme/v1/play/?video_id=4daf9cf5a99f415fb7b16d23e727a022&line=0&ratio=720p
     * visible : 1
     * wonderful_tag : 推荐
     * topic_id : 2
     * opus_gif_cover : http://p3.pstatp.com/obj/1ef000064100d16cfde4
     * owner_id : 10017
     * id : 10051
     * at_user_ids :
     */

    public int origin;
    public String lat_lon;
    public String at_user_ids;
    public String music_time;
    public String video_file_sign;
    public String web_link;
    public byte is_recommend;
    public long ori_opus_id;
    public String creative_bg_name;
    public String creative_template_name;

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public String getCreative_bg_name() {
        return creative_bg_name;
    }

    public void setCreative_bg_name(String creative_bg_name) {
        this.creative_bg_name = creative_bg_name;
    }

    public String getCreative_template_name() {
        return creative_template_name;
    }

    public void setCreative_template_name(String creative_template_name) {
        this.creative_template_name = creative_template_name;
    }

    public String opus_small_cover;
    public int music_id;
    public String music_album_cover;
    public String music_name;
    public String title;
    public String topic_name;
    public int like_count;
    public int example;
    public int play_count;
    public int is_delete;
    public String opus_cover;
    public String opus_path;
    public int visible;
    public String wonderful_tag;
    public int gift_count;
    public int topic_id;
    public String opus_gif_cover;
    public long owner_id;
    public long id;
    public List<DcDanmaEntity> barrage_list; //弹幕列表

    //以下是二次请求到的数据
    public UserInfo user;
    public ShareInfo share_info;

    public int comment_count;
    public int point_count; // 礼物条目数
    public int total_point; // 礼物总数
    public float feed_width_height_rate; // 视频宽高比
    public boolean is_like;
    public String download_link;
    public String detail_show_type;
    public int opus_width;
    public int opus_height;

    public int level;
    public String recommend_title;
    public String recommend_cover;
    public String teamwork_tips = "此视频暂不可共同创作";

    public List<UserInfo> materials_users;

    public String allUsers;

    @Override
    public String toString() {
        return "ShortVideoItem{" +
                "lat_lon='" + lat_lon + '\'' +
                "opus_path='" + opus_path + '\'' +
                ", video_file_sign='" + video_file_sign + '\'' +
                ", is_recommend=" + is_recommend +
                ", ori_opus_id=" + ori_opus_id +
                ", title='" + title + '\'' +
                ", topic_name='" + topic_name + '\'' +
                ", like_count=" + like_count +
                ", example=" + example +
                ", play_count=" + play_count +
                ", is_delete=" + is_delete +
                ", owner_id=" + owner_id +
                ", id=" + id +
                ", comment_count=" + comment_count +
                ", point_count=" + point_count +
                ", total_point=" + total_point +
                ", feed_width_height_rate=" + feed_width_height_rate +
                ", is_like=" + is_like +
                ", detail_show_type='" + detail_show_type + '\'' +
                ", opus_width=" + opus_width +
                ", opus_height=" + opus_height +
                ", level=" + level +
                ", itemType=" + itemType +
                ", is_teamwork=" + is_teamwork +
                '}';
    }

    public String distinctAllUser() {
        Set<String> userNames = new TreeSet<>();
        if (user != null && !TextUtils.isEmpty(user.getName())) {
            userNames.add(user.getName());
        }
        if (!CollectionUtil.isEmpty(materials_users)) {
            for (UserInfo userInfo : materials_users) {
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getName())) {
                    userNames.add(userInfo.getName());
                }
            }
        }
        StringBuilder sb = new StringBuilder(20);
        if (userNames.size() > 0) {
            for (String userName : userNames) {
                sb.append(sb.length() > 0 ? "+@" : "@").append(userName);
            }
        }
        allUsers = sb.toString();
        return allUsers;
    }

    public byte itemType = 0;//item类型，0表示是短视频，1表示广告图片
    public Banner banner;//图片item的内容

    public static ShortVideoItem createShortVideoItem() {
        ShortVideoItem shortVideoItem = new ShortVideoItem();
        shortVideoItem.user = new UserInfo();
        shortVideoItem.share_info = new ShareInfo();
        return shortVideoItem;
    }

    public boolean isDraft = false;

    public boolean isFollow() {
        return user != null && user.isFollowed();
    }

    public String getAuthorName() {
        return user != null ? user.getName() : "";
    }

    public String getAuthorAvatar() {
        return user != null ? user.getCover_url() : "";
    }

    public List<UploadMaterialEntity> materials;

    public short is_teamwork; // 是否共同创作
    public String frame_layout; // 画框类型

    public List<DcDanmaEntity> getBarrage_list() {
        return barrage_list;
    }

    public void setBarrage_list(List<DcDanmaEntity> barrage_list) {
        this.barrage_list = barrage_list;
    }

    public String getDownload_link() {
        return download_link;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }

    public boolean is_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
    }

    public ShareInfo getShare_info() {
        return share_info;
    }

    public void setShare_info(ShareInfo share_info) {
        this.share_info = share_info;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public UserInfo getUser() {
        if (user == null) {
            user = new UserInfo();
        }
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getOpus_small_cover() {
        return opus_small_cover;
    }

    public ShortVideoItem setOpus_small_cover(String opus_small_cover) {
        this.opus_small_cover = opus_small_cover;
        return this;
    }

    public int getMusic_id() {
        return music_id;
    }

    public ShortVideoItem setMusic_id(int music_id) {
        this.music_id = music_id;
        return this;
    }

    public int getGift_count() {
        return gift_count;
    }

    public void setGift_count(int gift_count) {
        this.gift_count = gift_count;
    }

    public String getMusic_album_cover() {
        return music_album_cover;
    }

    public ShortVideoItem setMusic_album_cover(String music_album_cover) {
        this.music_album_cover = music_album_cover;
        return this;
    }

    public String getMusic_name() {
        return music_name;
    }

    public ShortVideoItem setMusic_name(String music_name) {
        this.music_name = music_name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ShortVideoItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public ShortVideoItem setTopic_name(String topic_name) {
        this.topic_name = topic_name;
        return this;
    }

    public int getLike_count() {
        return like_count;
    }

    public ShortVideoItem setLike_count(int like_count) {
        this.like_count = like_count;
        return this;
    }

    public int getExample() {
        return example;
    }

    public ShortVideoItem setExample(int example) {
        this.example = example;
        return this;
    }

    public int getPlay_count() {
        return play_count;
    }

    public ShortVideoItem setPlay_count(int play_count) {
        this.play_count = play_count;
        return this;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public ShortVideoItem setIs_delete(int is_delete) {
        this.is_delete = is_delete;
        return this;
    }

    public String getOpus_cover() {
        return opus_cover;
    }

    public ShortVideoItem setOpus_cover(String opus_cover) {
        this.opus_cover = opus_cover;
        return this;
    }

    public String getOpus_path() {
        return opus_path;
    }

    public ShortVideoItem setOpus_path(String opus_path) {
        this.opus_path = opus_path;
        return this;
    }

    public int getVisible() {
        return visible;
    }

    public ShortVideoItem setVisible(int visible) {
        this.visible = visible;
        return this;
    }

    public String getWonderful_tag() {
        return wonderful_tag;
    }

    public ShortVideoItem setWonderful_tag(String wonderful_tag) {
        this.wonderful_tag = wonderful_tag;
        return this;
    }

    public int getTopic_id() {
        return topic_id;
    }

    public ShortVideoItem setTopic_id(int topic_id) {
        this.topic_id = topic_id;
        return this;
    }

    public String getOpus_gif_cover() {
        return opus_gif_cover;
    }

    public ShortVideoItem setOpus_gif_cover(String opus_gif_cover) {
        this.opus_gif_cover = opus_gif_cover;
        return this;
    }

    public long getOwner_id() {
        return owner_id;
    }

    public ShortVideoItem setOwner_id(long owner_id) {

        this.owner_id = owner_id;
        return this;
    }

    public long getId() {
        return id;
    }

    public ShortVideoItem setId(long id) {
        this.id = id;
        return this;
    }


    /**
     * 添加一个图片的item
     *
     * @return
     */
    public static ShortVideoItem newPictureItem(Banner banner) {
        ShortVideoItem shortVideoItem = new ShortVideoItem();
        shortVideoItem.itemType = 1;
        shortVideoItem.banner = banner;
        return shortVideoItem;
    }

    public short getIs_teamwork() {
        return is_teamwork;
    }

    public void setIs_teamwork(short is_teamwork) {
        this.is_teamwork = is_teamwork;
    }

}
