package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;

/**
 * Created by lsq on 8/1/2017.
 * 弹幕的实体类
 */

public class DcDanmaEntity extends BaseModel {
    public long user_id;
    public int comm_type; //弹幕类型：0普通文字弹幕   1：礼物弹幕
    public String title;   //普通文字弹幕的内容
    public DcDanmaCommEntity comm_text;  //礼物弹幕的内容
    public int like_count;
    public int visible;
    public long create_time;
    public UserInfo user;
    public long reply_user_id;
    public String reply_user_name;
    public long opus_id;
    public long id;
    public String at_user_ids;

    public int gift_point;
    public int prize_point;
    public String total_point = "0";

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getComm_type() {
        return comm_type;
    }

    public void setComm_type(int comm_type) {
        this.comm_type = comm_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DcDanmaCommEntity getComm_text() {
        return comm_text;
    }

    public void setComm_text(DcDanmaCommEntity comm_text) {
        this.comm_text = comm_text;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
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

    public int getGift_point() {
        return gift_point;
    }

    public void setGift_point(int gift_point) {
        this.gift_point = gift_point;
    }

    public int getPrize_point() {
        return prize_point;
    }

    public void setPrize_point(int prize_point) {
        this.prize_point = prize_point;
    }

    public String getTotal_point() {
        return total_point;
    }

    public void setTotal_point(String total_point) {
        this.total_point = total_point;
    }
}
