package com.wmlive.hhvideo.heihei.beans.main;

import java.io.Serializable;

/**
 * Created by lsq on 6/20/2017.
 */

public class RefreshCommentBean implements Serializable {
    public int pageId;
    public int position;
    public long videoId;
    public int count;
    public String comment;
    public boolean isAddComment;//是增加评论
    public VideoCommentResponse commentResponse;

    public RefreshCommentBean() {
    }

    public RefreshCommentBean(int pageId, String comment, boolean isAddComment, int position, long videoId, int count) {
        this.pageId = pageId;
        this.comment = comment;
        this.isAddComment = isAddComment;
        this.position = position;
        this.videoId = videoId;
        this.count = count;
    }
}
