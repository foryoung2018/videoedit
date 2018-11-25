package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by vhawk on 2017/5/25.
 */

public class ShortVideoLoveResponse extends BaseResponse {

    /**
     * comment_count : 0
     * is_like : false
     * like_count : 0
     * opus_id : 10051
     * play_count : 0
     */
    public boolean is_like;
    public long opus_id;

    public VideoLoveData data_count;

    public VideoLoveData getData_count() {
        if (data_count == null) {
            data_count = new VideoLoveData();
        }
        return data_count;
    }

    public ShortVideoLoveResponse setData_count(VideoLoveData data_count) {
        this.data_count = data_count;
        return this;
    }


    public boolean isIs_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
    }

    public long getOpus_id() {
        return opus_id;
    }

    public void setOpus_id(int opus_id) {
        this.opus_id = opus_id;
    }


}
