package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by vhawk on 2017/6/5.
 */

public class CommentDeleteResponse extends BaseResponse {

    private CommentDataCount data_count;

    public CommentDataCount getData_count() {
        return data_count;
    }

    public CommentDeleteResponse setData_count(CommentDataCount data_count) {
        this.data_count = data_count;
        return this;
    }
}
