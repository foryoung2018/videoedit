package com.wmlive.hhvideo.heihei.mainhome.view;

/**
 * Created by lsq on 5/2/2018 - 3:38 PM
 * 类描述：
 */
public interface RefreshCommentListener {
    void onRefreshComment(boolean isComment,boolean reset, int count);

    void onDismiss();
}
