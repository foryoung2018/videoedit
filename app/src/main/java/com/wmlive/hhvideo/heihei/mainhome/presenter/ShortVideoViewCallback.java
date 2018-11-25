package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.heihei.mainhome.view.OnVideoDetailViewListener;

/**
 * Created by lsq on 6/19/2017.
 * 视频页面的点击Callback
 */

public interface ShortVideoViewCallback extends OnVideoDetailViewListener {

    void showFullScreen(boolean isFull,int showType);

    void beforePlay();

    void getVideoDetail(int position, long videoId,boolean needBarrage);

    void getNextPageList(int position);

    void onRechargeClick(int position);

    boolean allowPlay();


//    void onClickPause(int position, View view, View cover, ShortVideoItem videoItem);

//    void onUserAvatarClick(boolean isSelf, long userId);

//    void onFollowClick(int position, long videoId, long userId, boolean isFollowed);

//    /**
//     * 点赞和快速点赞
//     *
//     * @param position    点赞的item位置
//     * @param videoId     视频id
//     * @param showFlyAnim 是否显示飞翔的heart
//     * @param doRequest   是否发请求
//     * @param rawDownX    点击的屏幕X坐标
//     * @param rawDownY    点击的屏幕Y坐标
//     * @param targetRawX  目标位置屏幕X坐标
//     * @param targetRawY  目标位置屏幕Y坐标
//     */
//    void onLikeClick(int position, long videoId,
//                     boolean showFlyAnim, boolean doRequest,
//                     float rawDownX, float rawDownY,
//                     float targetRawX, float targetRawY);

//    void onTopicClick(long topicId);
//    void onOpenCommentSend(int pageId, int position, long videoId);

}
