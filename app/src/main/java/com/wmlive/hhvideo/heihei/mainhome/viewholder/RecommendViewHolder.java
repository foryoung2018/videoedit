package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.wmlive.hhvideo.heihei.beans.main.DcDanmaEntity;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.mainhome.adapter.RecommendAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ShortVideoViewCallback;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.VideoPlayItemView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * Created by lsq on 7/26/2017.
 * 视频列表的Item
 * 增加弹幕面板
 */

public class RecommendViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.videoPlayItemView)
    public VideoPlayItemView videoPlayItemView;

    public RecommendViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }

    public ViewGroup getPlayerContainer() {
        return videoPlayItemView.flPlayerContainer;
    }

    public ImageView getCover() {
        return videoPlayItemView.ivCover;
    }

    public void initData(int pageId, int videoType, int position, ShortVideoItem item, ShortVideoViewCallback clickListener, RecommendAdapter recommendNewAdapter) {
        videoPlayItemView.initData(pageId, videoType, position, item, clickListener, recommendNewAdapter);
    }

    public void releaseDanmaKu(boolean fullRelease) {
        videoPlayItemView.releaseDanmaKu(fullRelease);
    }

    public void startDanmaKu(final List<DcDanmaEntity> list) {
        videoPlayItemView.startDanmaKu(list);
    }

    public void pauseDanmaKu() {
        videoPlayItemView.pauseDanmaKu();
    }

    public void hideDanmaKu() {
        videoPlayItemView.hideDanmaKu();
    }

    public void resumeDanmaku() {
        videoPlayItemView.resumeDanmaku();
    }


    public void setCoverVisible(boolean show) {
//        videoPlayItemView.setCoverVisible(show);
    }

    public void dismissVideoLoading() {
        KLog.i("======loading圈消失");
        videoPlayItemView.dismissVideoLoading();
    }

    public void setTitle() {
        videoPlayItemView.setTitle();
    }

    public void setPlayIcon(boolean isPlaying) {
        videoPlayItemView.setPlayIcon(isPlaying);
    }

    public void setPlayIcon(boolean isPlaying, boolean isError) {
        videoPlayItemView.setPlayIcon(isPlaying, isError);
    }

    public void viewVisible() {
        videoPlayItemView.viewVisible();
    }

    public void setGiftCount() {
        videoPlayItemView.setGiftCount();
    }

    public void refreshFollow() {
        videoPlayItemView.refreshFollow();
    }

    public void refreshGold() {
        videoPlayItemView.refreshGold();
    }

    public void setCommentCount() {
        videoPlayItemView.setCommentCount();
    }

    public void setLikeCount(boolean showAnim) {
        videoPlayItemView.setLikeCount(showAnim);
    }

    public void switchDanma(boolean open) {
        videoPlayItemView.switchDanma(open);
    }

    public void showGiftIcon(boolean show) {
//        videoPlayItemView.ivGiftView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void sendGift2Danma(long giftId, int totalCount, int hintCount) {
        videoPlayItemView.sendGift2Danma(giftId, totalCount, hintCount);
    }

    public void sendCommentDanma(RefreshCommentBean bean) {
        videoPlayItemView.sendCommentDanma(bean);
    }


    public void showLikeAnim(float rawX, float rawY) {
        int[] targetLocation = new int[2];
        videoPlayItemView.ivLike.getLocationOnScreen(targetLocation);
        videoPlayItemView.showLikeAnim(rawX, rawY, targetLocation[0], targetLocation[1]);
    }


    public void showVideoLoading() {
        videoPlayItemView.showVideoLoading();
    }

    public void refreshPreload(long count) {
        videoPlayItemView.refreshPreload(count);
    }
}
