package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by hsing on 2018/4/18.
 */

public class VideoModifyOpusResponse extends BaseResponse {

    private short is_teamwork;
    private int opus_id;
    private String title;

    public short getIs_teamwork() {
        return is_teamwork;
    }

    public void setIs_teamwork(short is_teamwork) {
        this.is_teamwork = is_teamwork;
    }

    public int getOpus_id() {
        return opus_id;
    }

    public void setOpus_id(int opus_id) {
        this.opus_id = opus_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
