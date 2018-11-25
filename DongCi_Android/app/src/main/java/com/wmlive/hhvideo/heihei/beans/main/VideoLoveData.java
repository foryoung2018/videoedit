package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/6/12.
 */

public class VideoLoveData extends BaseModel {

    /**
     * play_count : 275
     * comment_count : 18
     * like_count : 4
     */

    public String play_count;
    public String comment_count;
    public int like_count;

    public String getPlay_count() {
        return play_count;
    }

    public VideoLoveData setPlay_count(String play_count) {
        this.play_count = play_count;
        return this;
    }

    public String getComment_count() {
        return comment_count;
    }

    public VideoLoveData setComment_count(String comment_count) {
        this.comment_count = comment_count;
        return this;
    }

    public int getLike_count() {
        return like_count;
    }

    public VideoLoveData setLike_count(int like_count) {
        this.like_count = like_count;
        return this;
    }

    @Override
    public String toString() {
        return "VideoLoveData{" +
                "play_count='" + play_count + '\'' +
                ", comment_count='" + comment_count + '\'' +
                ", like_count='" + like_count + '\'' +
                '}';
    }
}
