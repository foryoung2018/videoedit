package com.wmlive.hhvideo.heihei.mainhome.view;

import android.view.View;

import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;

/**
 * Created by lsq on 5/7/2018 - 5:53 PM
 * 类描述：
 */
public interface OnVideoDetailViewListener {


    //以下是feed流页和视频详情列表页都有的方法
    void onVideoClick(boolean isDetail,
                      int position, View view, View cover, ShortVideoItem videoItem,
                      int showType);

    void onContinunousClick(int position, ShortVideoItem videoItem, float rawX, float rawY);

    /**
     * 点赞和快速点赞
     *
     * @param position    点赞的item位置
     * @param videoId     视频id
     * @param isFlyLike 是否显示飞翔的heart
     * @param doRequest   是否发请求
     * @param rawDownX    点击的屏幕X坐标
     * @param rawDownY    点击的屏幕Y坐标
     * @param targetRawX  目标位置屏幕X坐标
     * @param targetRawY  目标位置屏幕Y坐标
     */
    void onLikeClick(int position, long videoId,
                     boolean isLike,
                     boolean isFlyLike, boolean doRequest,
                     float rawDownX, float rawDownY,
                     float targetRawX, float targetRawY);

    void onCommentClick(int position, long videoId, ShortVideoItem videoItem);


    void onUserClick(long userId);


    void onFollowClick(int position, long videoId, long userId, boolean isFollowed);


    //以下是视频详情页方法

    void onShareClick(int position, ShortVideoItem shortVideoItem);

    void onGiftClick(int position, ShortVideoItem shortVideoItem);

    void onJoinReplaceClick(int index, ShortVideoItem shortVideoItem);

    void onJoinSingleClick(int index, ShortVideoItem shortVideoItem);

    void onJoinCurrentTemplateClick(int index, ShortVideoItem shortVideoItem);

    void onTopicClick(int topicId);

    void onCloseClick();
}