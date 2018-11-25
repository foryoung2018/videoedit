package com.wmlive.hhvideo.heihei.mainhome.adapter;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.common.VideoProxy;
import com.wmlive.hhvideo.dcijkplayer.AbsIjkPlayListener;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.LogFileManager;
import com.wmlive.hhvideo.heihei.beans.gifts.SendGiftResultResponse;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.DcDanmaEntity;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoInfoResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.main.VideoModifyOpusResponse;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ShortVideoViewCallback;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.VideoDetailViewHolder;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.RecyclerViewHelper;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 5/7/2018 - 11:37 AM
 * 类描述：
 */
public class VideoDetailListRecyclerAdapter extends RefreshAdapter<VideoDetailViewHolder, ShortVideoItem> {

    private ShortVideoViewCallback shortVideoViewCallback;
    private LinearLayoutManager linearLayoutManager;
    private int currentDataPosition = -1;
    private int videoType;
    private VideoDetailViewHolder currentViewHolder;
    private int nextPosition;//下次拉取数据的位置
    private int pageId;
    private boolean buffered;//视频已经完成缓存
    private CompositeDisposable cacheImageDisposable;
    private Disposable cacheVideoDisposable;
    private boolean scrolling;
    private RecyclerViewHelper recyclerViewHelper;

    public VideoDetailListRecyclerAdapter(int videoType, int pageSize, List<ShortVideoItem> list,
                                          int pageId, RefreshRecyclerView refreshView,
                                          LinearLayoutManager layoutManager,
                                          ShortVideoViewCallback clickListener) {
        super(list, refreshView, pageSize);
        this.videoType = videoType;
        linearLayoutManager = layoutManager;
        shortVideoViewCallback = clickListener;
        recyclerViewHelper = new RecyclerViewHelper(layoutManager);
        cacheImageDisposable = new CompositeDisposable();
        this.pageId = pageId;
        DcIjkPlayerManager.get().setCanLoop(true);
//        DcIjkPlayerManager.get().setPlayListener(pageId, playListener);
        refreshView.getRecycleView().addOnScrollListener(onScrollListener);
    }

    @Override
    public VideoDetailViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new VideoDetailViewHolder(parent, R.layout.item_video_detail_view_holder);
    }

    @Override
    public void onBindHolder(VideoDetailViewHolder holder, int position, ShortVideoItem data) {
        holder.itemVideoDetail.initData(pageId, this, videoType, data, position, playListener);
        holder.itemVideoDetail.pauseShadowView.setVisibility(View.GONE);
        holder.itemVideoDetail.setOnVideoDetailViewClickListener(shortVideoViewCallback);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            KLog.i("====RecommendAdapter=onScrollStateChanged:" + newState);
            if (newState == 0 && scrolling) {
                scrolling = false;
                if (shortVideoViewCallback != null) {
                    if (shortVideoViewCallback.allowPlay()) {
                        forcePlay(true);
                    }
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrolling = true;
        }
    };

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            //需要局部刷新的view
            if (holder instanceof VideoDetailViewHolder) {
                VideoDetailViewHolder detailViewHolder = (VideoDetailViewHolder) holder;
                String type = (String) payloads.get(0);
                switch (type) {
                    case RecommendAdapter.REFRESH_ALL://刷新评论数量
                        detailViewHolder.itemVideoDetail.setComment();
                        detailViewHolder.itemVideoDetail.setGift();
                        detailViewHolder.itemVideoDetail.setFollow();
                        detailViewHolder.itemVideoDetail.setLike(false);
                        ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
                        detailViewHolder.itemVideoDetail.refreshShortVideoItem(shortVideoItem);
                        if (null != shortVideoItem) {
                            if (shortVideoItem.getIs_delete() == 1) {
                                onVideoError(DCApplication.getDCApp().getString(R.string.stringWorkDeleted));
                            } else {//如果有弹幕，需要开启弹幕
                                if (!CollectionUtil.isEmpty(shortVideoItem.getBarrage_list())) {
                                    if (!detailViewHolder.itemVideoDetail.isRolling()) {
                                        detailViewHolder.itemVideoDetail.runRollComment(true);
                                    }
                                } else {
                                    detailViewHolder.itemVideoDetail.setRollCommentVisiable(false);
                                }
                            }
                        }
                        break;
                    case RecommendAdapter.REFRESH_COMMENT://刷新评论数量
                        detailViewHolder.itemVideoDetail.setComment();
                        ShortVideoItem shortVideoItem1 = getItemData(currentDataPosition);
                        if (!CollectionUtil.isEmpty(shortVideoItem1.getBarrage_list())) {
                            detailViewHolder.itemVideoDetail.runRollComment(true);
                        } else {
                            detailViewHolder.itemVideoDetail.setRollCommentVisiable(false);
                        }
                        break;
                    case RecommendAdapter.REFRESH_LIKE://点赞
                        detailViewHolder.itemVideoDetail.setLike(true);
                        break;
                    case RecommendAdapter.REFRESH_FOLLOW://关注
                        detailViewHolder.itemVideoDetail.setFollow();
                        break;
                    case RecommendAdapter.REFRESH_VIDEO_ERROR:
                        detailViewHolder.itemVideoDetail.dismissVideoLoading();
                        break;
                    case RecommendAdapter.REFRESH_GIFT://刷新礼物数
                        detailViewHolder.itemVideoDetail.setGift();
                        break;
                    case RecommendAdapter.REFRESH_PERMISSION_AND_TITLE:
                        detailViewHolder.itemVideoDetail.setTitle();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //刷新视频详情信息
    public void refreshVideoDetail(long videoId, int position, final ShortVideoInfoResponse videoInfoBean) {
        if (videoType == RecommendFragment.TYPE_SINGLE_WORK) {//单个作品
            if (videoInfoBean != null && videoInfoBean.getOpus() != null) {
                if (videoId == videoInfoBean.getOpus().getId()) {
                    if (videoInfoBean.getOpus() != null) {
                        if (videoInfoBean.getUser() != null) {
                            videoInfoBean.getOpus().setUser(videoInfoBean.getUser());
                        }
                    }
                    currentDataPosition = 0;
                    final ShortVideoItem shortVideoItem = videoInfoBean.getOpus();
                    shortVideoItem.materials = videoInfoBean.materials;
                    addData(true, new ArrayList<ShortVideoItem>() {{
                        add(shortVideoItem);
                    }}, false);
                } else {
                    showError(true);
                }
            } else {
                showError(true);
            }
        } else if (null != videoInfoBean) {
            ShortVideoItem item;
            int first = findFirstVisibleDataPosition();
            int end = linearLayoutManager.findLastVisibleItemPosition();
            for (int i = 0, size = getDataContainer().size(); i < size; i++) {
                item = getDataContainer().get(i);
                if (item != null && item.getId() == videoId) {
                    if (videoInfoBean.getOpus() != null) {
                        item.setComment_count(videoInfoBean.getOpus().getComment_count());
                        item.setShare_info(videoInfoBean.getOpus().getShare_info());
                        item.setIs_like(videoInfoBean.getOpus().is_like());
                        item.setLike_count(videoInfoBean.getOpus().getLike_count());
                        item.setIs_delete(videoInfoBean.getOpus().getIs_delete());
                        item.total_point = videoInfoBean.getOpus().total_point;
                        item.setIs_teamwork(videoInfoBean.getOpus().is_teamwork);
                        item.teamwork_tips = videoInfoBean.getOpus().teamwork_tips;
                        item.detail_show_type = videoInfoBean.getOpus().detail_show_type;
                        item.opus_width = videoInfoBean.getOpus().opus_width;
                        item.opus_height = videoInfoBean.getOpus().opus_height;
                        item.feed_width_height_rate = videoInfoBean.getOpus().feed_width_height_rate;
                        if (null != item.getShare_info() && videoInfoBean.getOpus().getShare_info() != null) {
                            item.getShare_info().download_link = videoInfoBean.getOpus().getDownload_link();
                        }
                        item.setBarrage_list(videoInfoBean.getOpus().getBarrage_list());//增加弹幕
                    }
                    if (videoInfoBean.getUser() != null) {
                        item.setUser(videoInfoBean.getUser());
                    }
                    item.materials = videoInfoBean.materials;
                    if (i >= first && i <= end) {
                        KLog.i("=======需要刷新的item position:" + (i + (hasHeader() ? 1 : 0) + " ,data position:" + i));
                        notifyItemChanged(i + (hasHeader() ? 1 : 0), RecommendAdapter.REFRESH_ALL);
                    }
                }
            }
        }
    }

    //刷新评论数量
    public void refreshCommentCount(RefreshCommentBean bean) {
        if (bean != null) {
            ShortVideoItem item;
            int first = findFirstVisibleDataPosition();
            int end = linearLayoutManager.findLastVisibleItemPosition();
            for (int i = 0, size = getDataContainer().size(); i < size; i++) {
                item = getDataContainer().get(i);
                if (item != null && item.getId() == bean.videoId) {
                    item.setComment_count(bean.count);
                    addLatestCommentToRolling(item, bean);
                    if (i >= first && i <= end) {
                        KLog.i("=======需要刷新的item position:" + (i + (hasHeader() ? 1 : 0) + " ,data position:" + i));
                        notifyItemChanged(i + (hasHeader() ? 1 : 0), RecommendAdapter.REFRESH_COMMENT);
                    }
                }
            }
        }
    }

    /**
     * 把最新的一条评论添加到滚动评论列表头部
     *
     * @param item 当前position位置的视频数据实体
     * @param bean 评论提交后，返回的评论刷新实体
     */
    public void addLatestCommentToRolling(ShortVideoItem item, RefreshCommentBean bean) {
        DcDanmaEntity danmaEntity = new DcDanmaEntity();
        danmaEntity.title = bean.comment;
        danmaEntity.comm_type = 0;
        UserInfo userInfo = new UserInfo();
        userInfo.setName(AccountUtil.getUserInfo().getName());
        danmaEntity.setUser(userInfo);
        item.barrage_list.add(0, danmaEntity);
    }


    //刷新点赞
    public void refreshLike(boolean justReset, ShortVideoLoveResponse bean) {
        if (bean != null) {
            ShortVideoItem item;
            int first = findFirstVisibleDataPosition();
            int end = linearLayoutManager.findLastVisibleItemPosition();
            for (int i = 0, size = getDataContainer().size(); i < size; i++) {
                item = getDataContainer().get(i);
                if (item != null && item.getId() == bean.opus_id) {
                    item.setIs_like(bean.isIs_like());
                    if (!justReset) {
                        if (null != bean.getData_count()) {
                            item.setLike_count(bean.getData_count().getLike_count());
                        }
                        if (i >= first && i <= end) {
                            KLog.i("=======需要刷新的item position:" + (i + (hasHeader() ? 1 : 0) + " ,data position:" + i));
                            notifyItemChanged(i + (hasHeader() ? 1 : 0), RecommendAdapter.REFRESH_LIKE);
                        }
                    }
                }
            }
        }
    }

    //刷新关注
    public void refreshFollow(long userId, boolean isFollowed) {
        ShortVideoItem item;
        int first = findFirstVisibleDataPosition();
        int end = linearLayoutManager.findLastVisibleItemPosition();
        for (int i = 0, size = getDataContainer().size(); i < size; i++) {
            item = getDataContainer().get(i);
            if (item != null && item.getOwner_id() == userId) {
                item.getUser().setFollowed(isFollowed);
                if (i >= first && i <= end) {
                    KLog.i("=======需要刷新的item position:" + (i + (hasHeader() ? 1 : 0) + " ,data position:" + i));
                    notifyItemChanged(i + (hasHeader() ? 1 : 0), RecommendAdapter.REFRESH_FOLLOW);
                }
            }
        }
    }

    //刷新礼物数量
    public void refreshGift(long videoId, SendGiftResultResponse resultResponse) {
        if (resultResponse != null && resultResponse.opus_static != null) {
            ShortVideoItem item;
            int first = findFirstVisibleDataPosition();
            int end = linearLayoutManager.findLastVisibleItemPosition();
            for (int i = 0, size = getDataContainer().size(); i < size; i++) {
                item = getDataContainer().get(i);
                if (item != null && item.getId() == videoId) {
                    item.setGift_count(resultResponse.opus_static.gift_count);
                    item.total_point = resultResponse.opus_static.total_point;
                    item.comment_count = resultResponse.opus_static.comment_count;
                    item.like_count = resultResponse.opus_static.like_count;
                    if (i >= first && i <= end) {
                        KLog.i("=======需要刷新的item position:" + (i + (hasHeader() ? 1 : 0) + " ,data position:" + i));
                        notifyItemChanged(i + (hasHeader() ? 1 : 0), RecommendAdapter.REFRESH_GIFT);
                    }
                }
            }
        }
    }

    //刷新Title和权限
    public void refreshPermissionsAndTitle(long videoId, VideoModifyOpusResponse response) {
        if (response != null) {
            ShortVideoItem item;
            int first = findFirstVisibleDataPosition();
            int end = linearLayoutManager.findLastVisibleItemPosition();
            for (int i = 0, size = getDataContainer().size(); i < size; i++) {
                item = getDataContainer().get(i);
                if (item != null && item.getId() == videoId) {
                    item.is_teamwork = response.getIs_teamwork();
                    item.setTitle(response.getTitle());
                    if (i >= first && i <= end) {
                        KLog.i("=======需要刷新的item position:" + (i + (hasHeader() ? 1 : 0) + " ,data position:" + i));
                        notifyItemChanged(i + (hasHeader() ? 1 : 0), RecommendAdapter.REFRESH_PERMISSION_AND_TITLE);
                    }
                }
            }
        }
    }

    public boolean isFullScreen() {
        if (currentViewHolder != null) {
            if (currentViewHolder.itemVideoDetail.isFullScreen()) {
                currentViewHolder.itemVideoDetail.onSingleClick(1, 1);
                return true;
            }
        }
        return false;
    }

    private int findFirstVisibleDataPosition() {
        return linearLayoutManager.findFirstVisibleItemPosition() - ((hasHeader() ? 1 : 0));
    }

    public void forcePlay() {
        forcePlay(true, recyclerViewHelper.findSnapViewPosition());
    }

    public void forcePlay(boolean needDetail) {
        forcePlay(needDetail, recyclerViewHelper.findSnapViewPosition());
    }

    public void forcePlay(boolean needDetail, int firstP) {
        if (isHeader(firstP)) {
            firstP = 1;
        }
        Log.d("缓存时间测试", "forcePlay: "+ System.currentTimeMillis());
        int adjustPoint = firstP - (hasHeader() ? 1 : 0);
        KLog.i("=====需要播放的位置：" + firstP + " ,adjustPoint:" + adjustPoint);
        ShortVideoItem shortVideoItem = getItemData(firstP - (hasHeader() ? 1 : 0));

        if (shortVideoItem != null) {
            if (shortVideoItem.itemType == 1) {
                //当前位置是图片，暂停上一个播放
                DcIjkPlayerManager.get().pausePlay();
                currentDataPosition = adjustPoint;
                return;
            }
            if (adjustPoint != -1
//                && (firstP == lastP)
                    && ((adjustPoint == 0 && currentDataPosition == 0) || currentDataPosition != adjustPoint)
                    && isContent(firstP)) {
                preparePlay(needDetail, adjustPoint);
            } else {
                KLog.i("=====不能播放的位置：" + firstP + " ,adjustPoint:" + adjustPoint);
            }
        }
    }

    public boolean isPlayPosition(int position) {
        return position == currentDataPosition;
    }

    public int getCurrentDataPosition() {
        return currentDataPosition;
    }

    public void preparePlay(boolean needDetail, int firstP) {
        boolean isSamePosition = (currentDataPosition == firstP);
        int prePosition = currentDataPosition;
        currentDataPosition = firstP;
        ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
        if (shortVideoItem != null && !TextUtils.isEmpty(shortVideoItem.getOpus_path())) {
            boolean isNewUrl = !shortVideoItem.getOpus_path().equals(DcIjkPlayerManager.get().getUrl());
            if (currentDataPosition == 0) {
                if (DcIjkPlayerManager.get().getPlayer() != null) {
                    int current = DcIjkPlayerManager.get().getPlayer().getCurrentPosition();

                }
//                EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(shortVideoItem.getId(), 2000, 0, 0));
            }
            if (isNewUrl || !isSamePosition) {
//                pauseOtherHolder(currentDataPosition);
                findCurrentViewHolder();
                KLog.i("======preparePlay1:" + currentViewHolder);
                if (currentViewHolder != null) {
                    if (null != shortVideoViewCallback) {
                        if (needDetail && shortVideoItem.getIs_delete() == 0) {//获取视频详情
                            KLog.i("======preparePlay4");
                            shortVideoViewCallback.getVideoDetail(currentDataPosition, getItemData(currentDataPosition).getId(), true);
                        }
                        if (videoType != RecommendFragment.TYPE_SINGLE_WORK
                                && currentDataPosition >= (getDataContainer().size() - 2)
                                && (nextPosition <= getDataContainer().size())) {//触发拉取下一页的数据
                            KLog.i("======拉取下一页的数据");
                            nextPosition = getDataContainer().size();
                            if (hasMore()) {
                                shortVideoViewCallback.getNextPageList(nextPosition);
                            }
                        }
                    }
                    if (shortVideoItem.getIs_delete() == 1) {//视频在列表中已被删除
                        onVideoError(DCApplication.getDCApp().getString(R.string.stringWorkDeleted));
                    } else {
                        DcIjkPlayerManager.get().setClickPause(true);
                        DcIjkPlayerManager.get().attachPlayer(currentViewHolder.itemVideoDetail.flPlayerContainer,
                                null);
                        buffered = false;
                        KLog.i("======preparePlay3");
                        Log.d("缓存时间测试", "preparePlay: "+System.currentTimeMillis());
                        allowPlay();
                    }
                }
            } else {

            }
        } else {

        }
    }

    /**
     * 恢复播放
     */
    public void resumePlay(int currentPlayDataPosition, boolean fromDetail, boolean manualStop) {
        DcIjkPlayerManager.get().setNeedPause(true);
        KLog.i("========adapter resumePlay currentPlayDataPosition:" + currentPlayDataPosition + " ,fromDetail:" + fromDetail);
        if (fromDetail) {
            KLog.i("====从详情回来，且播放的位置与Feed位置不一样");
            currentDataPosition = currentPlayDataPosition;
            DcIjkPlayerManager.get().setNeedPause(false);
            findCurrentViewHolder();
            playSomePosition(fromDetail, manualStop);
            return;
        }
        int findPosition = recyclerViewHelper.findSnapViewPosition();
        if (isHeader(findPosition) && findPosition == currentDataPosition) {
            KLog.i("====找到的位置是header");
            DcIjkPlayerManager.get().resetUrl();
        }
        if ((findPosition - (hasHeader() ? 1 : 0)) != currentDataPosition) {
            KLog.i("=====检测到当前滑动到的位置与之前位置不一样");
            forcePlay(true, findPosition);
        } else {
            KLog.i("=====检测到当前滑动到的位置与之前位置一样");
            findCurrentViewHolder();
            playSomePosition(fromDetail, manualStop);
        }
    }

    private void playSomePosition(boolean fromDetail, boolean manualStop) {
        if (currentDataPosition > -1 && currentDataPosition < getDataContainer().size()) {
            ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
            if (currentViewHolder != null && shortVideoItem != null) {
                if (shortVideoItem.itemType == 1) {
                    KLog.i("=====这是图片，不播放");
                    return;
                }
                if (shortVideoItem.getIs_delete() == 0) {
                    if (shortVideoViewCallback != null) {//获取视频详情
                        shortVideoViewCallback.getVideoDetail(currentDataPosition, getItemData(currentDataPosition).getId(), true);
                    }
                    if (fromDetail && shortVideoItem.getOpus_path().equals(DcIjkPlayerManager.get().getUrl())) {
                        KLog.i("======从详情返回,需要继续播放");
                        DcIjkPlayerManager.get().attachPlayer(currentViewHolder.itemVideoDetail.flPlayerContainer, false, null);
                        DcIjkPlayerManager.get().resumePlay(shortVideoItem.getId(), pageId, playListener);
                        return;
                    }
                    if (!shortVideoItem.getOpus_path().equals(DcIjkPlayerManager.get().getUrl())) {
                        KLog.i("======已经不是之前的视频了，重新播放");
                        DcIjkPlayerManager.get().attachPlayer(currentViewHolder.itemVideoDetail.flPlayerContainer, null);
                        buffered = false;
                        DcIjkPlayerManager.get().setVideoUrl(pageId, shortVideoItem.getId(), shortVideoItem.getOpus_path(), playListener);
                    } else {
                        if (pageId != DcIjkPlayerManager.get().getPageId()) {
                            DcIjkPlayerManager.get().attachPlayer(currentViewHolder.itemVideoDetail.flPlayerContainer, null);
                            buffered = false;
                            DcIjkPlayerManager.get().setVideoUrl(pageId, shortVideoItem.getId(), shortVideoItem.getOpus_path(), playListener);
                        } else {
                            if (currentViewHolder.itemVideoDetail.flPlayerContainer.getChildCount() == 0) {
                                DcIjkPlayerManager.get().attachPlayer(currentViewHolder.itemVideoDetail.flPlayerContainer, null);
                            }
                            DcIjkPlayerManager.get().resumePlay(shortVideoItem.getId(), pageId, playListener);
                        }
                    }
                } else {
                    onVideoError(DCApplication.getDCApp().getString(R.string.stringWorkDeleted));
                }
            }
        }
    }

    //允许播放
    public void allowPlay() {
        final ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
        if (shortVideoItem != null) {

            //释放掉之前一个视频的缓存下载
            KLog.i("======停止缓存下一个视频");
            VideoProxy.get().stopCacheFile();
            Log.d("缓存时间测试", "allowPlay111: "+System.currentTimeMillis());
            //播放下一个视频
            DcIjkPlayerManager.get().setClickPause(true);
            KLog.i("======开始播放下一个视频1");
            Log.d("缓存时间测试", "allowPlay222: "+System.currentTimeMillis());
            DcIjkPlayerManager.get().setVideoUrl(pageId, shortVideoItem.getId(), shortVideoItem.getOpus_path(), playListener);
            KLog.i("======开始播放下一个视频2");

            currentVideoPath = shortVideoItem.getOpus_path();
            //缓存下一个视频
            cacheNextVideo();
        }
    }

    private String nextVideoPath;
    private String currentVideoPath;

    /**
     * 缓存下一个视频
     */
    private void cacheNextVideo() {
        //缓存下一个视频
        final ShortVideoItem next = getItemData(currentDataPosition + 1);
        if (next != null && !TextUtils.isEmpty(next.getOpus_path())) {
            nextVideoPath = next.getOpus_path();
            if (cacheVideoDisposable != null && !cacheVideoDisposable.isDisposed()) {
                cacheVideoDisposable.dispose();
            }
            if (VideoProxy.get().getProxy() != null && !VideoProxy.get().getProxy().isCached(nextVideoPath)) {
                cacheVideoDisposable = Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .delay(0, TimeUnit.SECONDS)
                        .map(new Function<Integer, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull Integer integer) throws Exception {
                                VideoProxy.get().cacheFile(nextVideoPath);
                                return true;
                            }
                        })
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean aBoolean) throws Exception {

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });
            }
        }
    }

    //视频被删除了
    public void onVideoError(int position, long videoId, String message) {
        if (position > -1 && position < getDataContainer().size()) {
            ShortVideoItem shortVideoItem = getItemData(position);
            if (shortVideoItem != null && shortVideoItem.getId() == videoId) {//更新被删除视频的item
                shortVideoItem.setIs_delete(1);
                notifyItemChanged(position + (hasHeader() ? 1 : 0), RecommendAdapter.REFRESH_VIDEO_ERROR);
            }

            ShortVideoItem current = getItemData(currentDataPosition);
            if (current != null && current.getId() == videoId) {//如果是当前的正在播放的item
                ToastUtil.showToast(message);
                DcIjkPlayerManager.get().resetUrl();
                pausePlay();
                if (currentViewHolder != null) {
                    currentViewHolder.itemVideoDetail.dismissVideoLoading();
                }
            }
        }
    }

    private void onVideoError(String errorMessage) {
        findCurrentViewHolder();
        if (null != currentViewHolder) {
            currentViewHolder.itemVideoDetail.dismissVideoLoading();
            currentViewHolder.itemVideoDetail.setPlayIcon(true);
        }
        DcIjkPlayerManager.get().pausePlay();
        DcIjkPlayerManager.get().setClickPause(false);
        DcIjkPlayerManager.get().resetUrl();
        ToastUtil.showToast(errorMessage);
    }

    private void resetPlay(boolean isRefresh) {
        if (isRefresh) {
//            if (currentViewHolder != null) {
//                currentViewHolder.releaseDanmaKu(false);
//            }
            DcIjkPlayerManager.get().pausePlay();
            DcIjkPlayerManager.get().resetUrl();
            currentDataPosition = 0;
            nextPosition = 0;
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        if (pageId == DcIjkPlayerManager.get().getPageId()) {
            KLog.i("======暂停播放pageId：" + pageId + " ijkId:" + DcIjkPlayerManager.get().getPageId());
            if (currentViewHolder != null) {
//                currentViewHolder.pauseDanmaKu();
            }
            DcIjkPlayerManager.get().pausePlay();
        }
    }

    private void findCurrentViewHolder() {
        Log.d("缓存时间测试", "findCurrentViewHolder: "+ System.currentTimeMillis());

        KLog.i("======findCurrentViewHolder1");
        if (currentDataPosition != -1) {
            KLog.i("======findCurrentViewHolder2");
            RecyclerView.ViewHolder view = getRecycleView().getRecycleView().findViewHolderForLayoutPosition(currentDataPosition + (hasHeader() ? 1 : 0));
            if (null != view && view instanceof VideoDetailViewHolder) {
                KLog.i("========viewHolder:" + view.getClass().getSimpleName());
                currentViewHolder = (VideoDetailViewHolder) view;
            }
        }
    }

    private void pauseOtherHolder(int position) {
        if (position > 0) {
            position = position + (hasHeader() ? 1 : 0);
            RecyclerView.ViewHolder holder = getRecycleView()
                    .getRecycleView()
                    .findViewHolderForLayoutPosition(position);
            if (holder != null && holder instanceof VideoDetailViewHolder) {
                KLog.i("====pauseOtherHolder2 position:" + position + " ,currentDataPosition:" + currentDataPosition);
            }
        }
    }

    //显示暂停状态
    private void showPauseState(boolean isError) {
        if (null != currentViewHolder) {
            currentViewHolder.itemVideoDetail.dismissVideoLoading();
            currentViewHolder.itemVideoDetail.setPlayIcon(false, isError);
        }
    }

    private AbsIjkPlayListener playListener = new AbsIjkPlayListener() {

        @Override
        public void onLoopStart() {
            super.onLoopStart();
            if (null != currentViewHolder) {
                ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
                if (null != shortVideoItem && shortVideoItem.getIs_delete() == 1) {
                    onVideoError(DCApplication.getDCApp().getString(R.string.stringWorkDeleted));
                }
            }
        }

        @Override
        public void onPlayPreparing() {
            KLog.i("=====onPlayPreparing11111");
            if (null != currentViewHolder) {
                currentViewHolder.itemVideoDetail.setPlayIcon(true);
                ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
                if (shortVideoItem != null
                        && VideoProxy.get().getProxy() != null
                        ) {
                    currentViewHolder.itemVideoDetail.dismissVideoLoading();
                } else {
//                    currentViewHolder.itemVideoDetail.showVideoLoading();
                }
            }
        }

        @Override
        public void onPlayPrepared() {
            KLog.i("=====onPlayPrepared11111");
            currentViewHolder.itemVideoDetail.setPlayIcon(true);
            if (pageId == DcIjkPlayerManager.get().getPageId()) {
                if (shortVideoViewCallback != null) {
                    if (shortVideoViewCallback.allowPlay()) {
                        KLog.i("======检测页面允许播放");
                        Log.d("缓存时间测试", "onPlayPrepared : " + System.currentTimeMillis());
                        DcIjkPlayerManager.get().startPlay();
                        if (currentViewHolder != null && !currentViewHolder.itemVideoDetail.isPlayStatus()) {
                            KLog.i("=======手动暂停了，不能播放");
                            DcIjkPlayerManager.get().pausePlay();
                        }
                    } else {
                        KLog.i("======检测页面不允许播放1");
                    }
                } else {
                    KLog.i("======检测页面不允许播放2");
                }
            }
            LogFileManager.getInstance().saveLogInfo("player", "url:" + DcIjkPlayerManager.get().getUrl() + " onPlayPrepared");
        }

        @Override
        public void onVideoRenderingStart() {
            super.onVideoRenderingStart();
            if (null != currentViewHolder) {//以这里开始播放为准
                currentViewHolder.itemVideoDetail.dismissVideoLoading();
                KLog.i("=====onVideoRenderingStart viewHolder不为空");
                currentViewHolder.itemVideoDetail.flPlayerContainer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (currentViewHolder != null) {
                            if (shortVideoViewCallback != null) {
                                shortVideoViewCallback.beforePlay();
                            }
                            DcIjkPlayerManager.get().setPlayerAlpha(1f);
                        }
                    }
                }, DcIjkPlayerManager.START_PLAY_DELAY);
            } else {
                KLog.i("=====onAudioRenderingStart viewHolder为空");
            }
            LogFileManager.getInstance().saveLogInfo("player", "url:" + DcIjkPlayerManager.get().getUrl() + " onAudioRenderingStart");
        }

        @Override
        public void onPlayBufferStart() {
            super.onPlayBufferStart();
            LogFileManager.getInstance().saveLogInfo("player", "url:" + DcIjkPlayerManager.get().getUrl() + " onPlayBufferStart");
            if (currentViewHolder != null && !buffered) {
                currentViewHolder.itemVideoDetail.showVideoLoading();
            }
        }

        @Override
        public void onPlayCompleted() {
            super.onPlayCompleted();
            buffered = true;
        }

        @Override
        public void onPlayBufferEnd() {
            super.onPlayBufferEnd();
            LogFileManager.getInstance().saveLogInfo("player", "url:" + DcIjkPlayerManager.get().getUrl() + " onPlayBufferEnd");
            if (currentViewHolder != null) {
                currentViewHolder.itemVideoDetail.dismissVideoLoading();

            }
            if (shortVideoViewCallback != null) {
                shortVideoViewCallback.beforePlay();
            }
        }

        @Override
        public void onPlayStop() {
            showPauseState(false);
        }

        @Override
        public void onClickPause() {
            super.onClickPause();
            if (null != currentViewHolder) {
                KLog.i("=====视频点击暂停");
                currentViewHolder.itemVideoDetail.dismissVideoLoading();
//                currentViewHolder.setPlayIcon(false);
            }
        }

        @Override
        public void onPlayPause() {
            if (null != currentViewHolder) {
                KLog.i("=====视频暂停");
                currentViewHolder.itemVideoDetail.dismissVideoLoading();
                currentViewHolder.itemVideoDetail.setPlayIcon(true);
            }
        }

        @Override
        public void onPlayResume() {
            if (null != currentViewHolder) {
                KLog.i("=====视频恢复播放");
                currentViewHolder.itemVideoDetail.dismissVideoLoading();
                currentViewHolder.itemVideoDetail.setPlayIcon(true);
                currentViewHolder.itemVideoDetail.pauseShadowView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPlayError(int errorCode) {
            KLog.i("=====视频播放出错：" + errorCode);
            LogFileManager.getInstance().saveLogInfo("player", "url:" + DcIjkPlayerManager.get().getUrl() + " onPlayError errorCode:" + errorCode);
            showPauseState(true);
        }

        @Override
        public void onFileError(int code, String errorMessage) {
            LogFileManager.getInstance().saveLogInfo("player", "url:" + DcIjkPlayerManager.get().getUrl() + " onFileError code:" + code);
            onVideoError(errorMessage);
        }

        @Override
        public void onPlayTimeCompleted(long videoId, String url, int videoDuring) {
            super.onPlayTimeCompleted(videoId, url, videoDuring);
            KLog.i("=======发送播放位置：videoId：" + videoId + " videoDuring:" + videoDuring);
//            EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(videoId, videoDuring, 0, 0));
        }

        @Override
        public void onDoubleClick(float x, float y) {
            super.onDoubleClick(x, y);
        }
    };

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof VideoDetailViewHolder) {
            KLog.i("=========onView AttachedToWindow currentP:" + currentDataPosition + ",holder position:" + holder.getAdapterPosition());
            ((VideoDetailViewHolder) holder).getRootView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != onActiveCallback) {
                        onActiveCallback.onActive();
                    }
                }
            }, 20);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        KLog.i("=========onView DetachedFromWindow1 currentP:" + currentDataPosition + ",holder position:" + holder.getAdapterPosition());
        if (holder instanceof VideoDetailViewHolder) {
            ((VideoDetailViewHolder) holder).itemVideoDetail.setRollCommentVisiable(false);
            ((VideoDetailViewHolder) holder).itemVideoDetail.runRollComment(false);
//            if (currentDataPosition != holder.getAdapterPosition()) {
            KLog.e("=====onView DetachedF" + holder.getAdapterPosition() + " 切换到：" + currentDataPosition);
            ((VideoDetailViewHolder) holder).itemVideoDetail.showNormal();
//            }
        }
    }

    public void setOnActiveCallback(RecommendAdapter.OnActiveCallback onActiveCallback) {
        this.onActiveCallback = onActiveCallback;
    }

    private RecommendAdapter.OnActiveCallback onActiveCallback;


}
