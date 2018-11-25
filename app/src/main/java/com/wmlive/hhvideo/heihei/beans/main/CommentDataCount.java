package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;

public class CommentDataCount extends BaseModel {

    /**
     * play_count : 413
     * comment_count : 7
     * like_count : 6
     */

    public int play_count;
    public int comment_count;
    public int like_count;
    public int gift_count;
    public int total_point;
    public long opus_id;

    @Override
    public String toString() {
        return "CommentDataCount{" +
                "play_count=" + play_count +
                ", comment_count=" + comment_count +
                ", like_count=" + like_count +
                ", gift_count=" + gift_count +
                ", total_point=" + total_point +
                ", opus_id=" + opus_id +
                '}';
    }
}
