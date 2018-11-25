package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;

/**
 * Created by vhawk on 2017/5/25.
 */

public class Comment extends BaseModel {

    /**
     * user_id : 10020
     * title : 哈哈哈哈哈
     * visible : 1
     * create_time : 1495908825
     * user : {"name":"","cover_url":"","honours":[],"dc_num":"","id":10020,"description":""}
     * reply_user_id : 0
     * opus_id : 10051
     * id : 7
     * at_user_ids :
     */

    private long user_id;
    private String title;
    private int visible;
    private int create_time;
    private UserInfo user;
    private long reply_user_id;
    private String reply_user_name;
    private long opus_id;
    private long id;
    private String at_user_ids;
    private boolean is_like;
    private CommText comm_text;
    private int like_count;
    private int comm_type;

    public Comment() {
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public UserInfo getUser() {
        if (user == null) {
            user = new UserInfo();
        }
        return user;
    }

    public void setUser(UserInfo userEntity) {
        this.user = userEntity;
    }

    public long getReply_user_id() {
        return reply_user_id;
    }

    public void setReply_user_id(long reply_user_id) {
        this.reply_user_id = reply_user_id;
    }

    public String getReply_user_name() {
        return reply_user_name;
    }

    public void setReply_user_name(String reply_user_name) {
        this.reply_user_name = reply_user_name;
    }

    public long getOpus_id() {
        return opus_id;
    }

    public void setOpus_id(long opus_id) {
        this.opus_id = opus_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAt_user_ids() {
        return at_user_ids;
    }

    public void setAt_user_ids(String at_user_ids) {
        this.at_user_ids = at_user_ids;
    }

    public boolean is_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
    }

    public CommText getComm_text() {
        return comm_text;
    }

    public void setComm_text(CommText comm_text) {
        this.comm_text = comm_text;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getComm_type() {
        return comm_type;
    }

    public void setComm_type(int comm_type) {
        this.comm_type = comm_type;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "user_id=" + user_id +
                ", title='" + title + '\'' +
                ", visible=" + visible +
                ", create_time=" + create_time +
                ", user=" + user +
                ", reply_user_id=" + reply_user_id +
                ", opus_id=" + opus_id +
                ", id=" + id +
                ", at_user_ids='" + at_user_ids + '\'' +
                ", is_like=" + is_like +
                ", comm_text=" + comm_text +
                ", like_count=" + like_count +
                ", comm_type=" + comm_type +
                '}';
    }
}
