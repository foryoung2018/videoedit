package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by vhawk on 2017/5/25.
 * Modify by lsq
 */

public class VideoCommentListResponse extends BaseResponse {

    /**
     * comments : []
     * has_more : false
     * offset : 0
     */

    private List<Comment> comments;


    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
