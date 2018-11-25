package com.wmlive.hhvideo.heihei.beans.opus;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by XueFei on 2017/8/4.
 * <p>
 * 评论 点赞
 */

public class OpusLikeCommentResponse extends BaseResponse {
    private int like_count;

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }
}
