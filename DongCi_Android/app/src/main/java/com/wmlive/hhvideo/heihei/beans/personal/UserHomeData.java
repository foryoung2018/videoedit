package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by XueFei on 2017/5/31.
 * <p>
 * 个人主页--作品及喜欢的作品数据
 */

public class UserHomeData extends BaseModel {
    private int view;
    private int like;
    private int like_opus_count;
    private int opus_count;
    private int co_create_count;
    private int all_earn_point;//分贝

    public UserHomeData() {
    }

    public int getAll_earn_point() {
        return all_earn_point;
    }

    public void setAll_earn_point(int all_earn_point) {
        this.all_earn_point = all_earn_point;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getLike_opus_count() {
        return like_opus_count;
    }

    public void setLike_opus_count(int like_opus_count) {
        this.like_opus_count = like_opus_count;
    }

    public int getOpus_count() {
        return opus_count;
    }

    public void setOpus_count(int opus_count) {
        this.opus_count = opus_count;
    }

    public int getCo_create_count() {
        return co_create_count;
    }

    public void setCo_create_count(int co_create_count) {
        this.co_create_count = co_create_count;
    }

    @Override
    public String toString() {
        return "UserHomeData{" +
                "view=" + view +
                ", like=" + like +
                ", like_opus_count=" + like_opus_count +
                ", opus_count=" + opus_count +
                ", opus_count=" + co_create_count +
                '}';
    }
}
