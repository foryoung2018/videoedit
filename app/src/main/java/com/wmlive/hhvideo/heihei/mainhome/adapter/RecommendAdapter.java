package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.wmlive.hhvideo.common.VideoProxy;
import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.hhvideo.common.manager.message.BaseTask;
import com.wmlive.hhvideo.dcijkplayer.AbsIjkPlayListener;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.LogFileManager;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoInfoResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.main.VideoModifyOpusResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountEntity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ShortVideoViewCallback;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.RecommendViewHolder;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.RecyclerViewHelper;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.io.File;
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
 * Created by lsq on 6/19/2017.
 * 推荐页的adapter
 */

public class RecommendAdapter extends RefreshAdapter<RecommendViewHolder, ShortVideoItem> {

    public final static String TAG = "RecommendAdapter";
    private ShortVideoViewCallback shortVideoViewCallback;
    private LinearLayoutManager linearLayoutManager;
    private int currentDataPosition = -1;
    private int videoType;
    private RecommendViewHolder currentViewHolder;
    private int nextPosition;//下次拉取数据的位置
    private int pageId;
    private boolean buffered;//视频已经完成缓存
    private CompositeDisposable cacheImageDisposable;
    private Disposable cacheVideoDisposable;
    private boolean scrolling;
    private RecyclerViewHelper recyclerViewHelper;
    private boolean fragmentVisible = false;

    public static final String REFRESH_ALL = "refresh_all";
    public static final String REFRESH_LIKE = "refresh_like";
    public static final String REFRESH_FOLLOW = "refresh_follow";
    public static final String REFRESH_COMMENT = "refresh_comment";
    public static final String REFRESH_GIFT = "refresh_gift";
    public static final String REFRESH_PERMISSION_AND_TITLE = "refresh_permission_and_title";
    public static final String REFRESH_VIDEO_ERROR = "refresh_video_error";

    public RecommendAdapter(int videoType, int pageSize, List<ShortVideoItem> list,
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
    public RecommendViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new RecommendViewHolder(parent, R.layout.item_video_list);
    }

    @Override
    public void onBindHolder(RecommendViewHolder holder, int position, ShortVideoItem data) {
        if (data != null) {
            holder.initData(pageId, videoType, position, data, shortVideoViewCallback, this);
        }
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
                        forcePlay();
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

    public void forcePlay() {
        forcePlay(true, recyclerViewHelper.findSnapViewPosition());
    }

    public void forcePlay(boolean needDetail, int firstP) {
        if (isHeader(firstP)) {
            firstP = 1;
        }
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
        currentDataPosition = firstP;
        ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
        if (shortVideoItem != null && !TextUtils.isEmpty(shortVideoItem.getOpus_path())) {
            boolean isNewUrl = !shortVideoItem.getOpus_path().equals(DcIjkPlayerManager.get().getUrl());
            if (currentDataPosition == 0) {
//                EventHelper.post(GlobalParams.EventType.TYPE_USER_BEHAVIOR, new UserBehavior(shortVideoItem.getId(), 2000, 0, 0));
            }
            if (isNewUrl || !isSamePosition) {
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
                        KLog.i("======preparePlay2");
                        DcIjkPlayerManager.get().setClickPause(true);
                        DcIjkPlayerManager.get().attachPlayer(currentViewHolder.getPlayerContainer(), new DcIjkPlayerManager.OnPlayerDetachListener() {
                            @Override
                            public void onDetach() {
                                KLog.i("=======onDetach1 preparePlay");
                                if (currentViewHolder != null) {
                                    KLog.i("=======onDetach2 preparePlay");
                                    currentViewHolder.setCoverVisible(true);
                                }
                            }
                        });
                        buffered = false;
                        KLog.i("======preparePlay3");
                        allowPlay();
                    }
                }
            }
        }
    }

    //允许播放
    public void allowPlay() {
        pauseOtherHolder(currentDataPosition - 1);
        pauseOtherHolder(currentDataPosition + 1);
        final ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
        if (shortVideoItem != null) {

            //释放掉之前一个视频的缓存下载
            KLog.i("======停止缓存下一个视频");
            TaskManager.get().executeTask(new BaseTask() {
                @Override
                public void run() {
                    VideoProxy.get().stopCacheFile();
                }
            });
            //播放下一个视频
            DcIjkPlayerManager.get().setClickPause(true);
            KLog.i("======开始播放下一个视频1");
            DcIjkPlayerManager.get().setVideoUrl(pageId, shortVideoItem.getId(), shortVideoItem.getOpus_path(), playListener);
            KLog.i("======开始播放下一个视频2");
            //缓存下一个视频
            if (fragmentVisible) {
                cacheNextVideo();
            }
        }
    }

    public void setFragmentVisible(boolean fragmentVisible) {
        this.fragmentVisible = fragmentVisible;
    }

    private void pauseOtherHolder(int position) {
        position = position + (hasHeader() ? 1 : 0);
        RecyclerView.ViewHolder holder = getRecycleView()
                .getRecycleView()
                .findViewHolderForLayoutPosition(position);
        KLog.i("====pauseOtherHolder1 position:" + position);
        if (holder != null && holder instanceof RecommendViewHolder) {
            KLog.i("====pauseOtherHolder2 position:" + position);
            ((RecommendViewHolder) holder).releaseDanmaKu(false);
        }
    }

    /**
     * 缓存下一个视频
     */
    private void cacheNextVideo() {
        //缓存下一个视频
        ShortVideoItem next = getItemData(currentDataPosition + 1);
        if (next != null) {
            if (next.itemType != 0) {
                next = getItemData(currentDataPosition + 2);
            }
            cacheVideo(next);
        }
    }

    private void cacheVideo(ShortVideoItem next) {
        if (next != null && next.itemType == 0 && !TextUtils.isEmpty(next.getOpus_path())) {
            if (cacheVideoDisposable != null && !cacheVideoDisposable.isDisposed()) {
                cacheVideoDisposable.dispose();
            }
            if (VideoProxy.get().getProxy() != null && !VideoProxy.get().getProxy().isCached(next.getOpus_path())) {
                cacheVideoDisposable = Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .delay(2, TimeUnit.SECONDS)
                        .map(new Function<Integer, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull Integer integer) throws Exception {
                                KLog.i("=====开始缓存下一个视频：" + next.getOpus_path());
                                VideoProxy.get().cacheFile(next.getOpus_path());
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

    private void findCurrentViewHolder() {
        KLog.i("======findCurrentViewHolder1");
        if (currentDataPosition != -1) {
            KLog.i("======findCurrentViewHolder2");
            RecyclerView.ViewHolder view = getRecycleView().getRecycleView().findViewHolderForLayoutPosition(currentDataPosition + (hasHeader() ? 1 : 0));
            if (null != view && view instanceof RecommendViewHolder) {
                KLog.i("========viewHolder:" + view.getClass().getSimpleName());
                currentViewHolder = (RecommendViewHolder) view;
            }
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
                currentViewHolder.setPlayIcon(true);
                ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
                if (shortVideoItem != null
                        && VideoProxy.get().getProxy() != null
                    /*&& VideoProxy.get().getProxy().isCaching(shortVideoItem.getOpus_path())*/) {
                    currentViewHolder.dismissVideoLoading();
                } else {
                    currentViewHolder.showVideoLoading();
                }
            }
        }

        @Override
        public void onPlayPrepared() {
            KLog.i("=====onPlayPrepared11111");
            currentViewHolder.setPlayIcon(true);
            if (pageId == DcIjkPlayerManager.get().getPageId()) {
                if (shortVideoViewCallback != null) {
                    if (shortVideoViewCallback.allowPlay()) {
                        KLog.i("======检测页面允许播放");
                        DcIjkPlayerManager.get().startPlay();
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
                currentViewHolder.dismissVideoLoading();
                KLog.i("=====onVideoRenderingStart viewHolder不为空");
                currentViewHolder.getCover().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (currentViewHolder != null) {
                            currentViewHolder.setCoverVisible(false);
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
            LogFileManager.getInstance().saveLogInfo("player", DcIjkPlayerManager.get() + "url:" + DcIjkPlayerManager.get().getUrl() + " onPlayBufferStart");
//            DcIjkPlayerManager.get().setPlayerAlpha(1);
            if (currentViewHolder != null && !buffered) {
                currentViewHolder.showVideoLoading();
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
                currentViewHolder.dismissVideoLoading();
                if (buffered) {
                    currentViewHolder.setCoverVisible(false);
                }
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
                currentViewHolder.dismissVideoLoading();
            }
        }

        @Override
        public void onPlayPause() {
            if (null != currentViewHolder) {
                KLog.i("=====视频暂停");
                currentViewHolder.dismissVideoLoading();
                currentViewHolder.setPlayIcon(true);
            }
        }

        @Override
        public void onPlayResume() {
            if (null != currentViewHolder) {
                KLog.i("=====视频恢复播放");
                currentViewHolder.dismissVideoLoading();
                currentViewHolder.setPlayIcon(true);
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
        }

        @Override
        public void onDoubleClick(float x, float y) {
            super.onDoubleClick(x, y);
        }
    };

    //显示暂停状态
    private void showPauseState(boolean isError) {
        if (null != currentViewHolder) {
            currentViewHolder.dismissVideoLoading();
            currentViewHolder.setPlayIcon(false, isError);
        }
    }
    /**
     * 首次激活播放器
     */
    public interface OnActiveCallback {
        void onActive();
    }

    //视频被删除了
    public void onVideoError(int position, long videoId, String message) {
        if (position > -1 && position < getDataContainer().size()) {
            ShortVideoItem shortVideoItem = getItemData(position);
            if (shortVideoItem != null && shortVideoItem.getId() == videoId) {//更新被删除视频的item
                shortVideoItem.setIs_delete(1);
                notifyItemChanged(position + (hasHeader() ? 1 : 0), REFRESH_VIDEO_ERROR);
            }

            ShortVideoItem current = getItemData(currentDataPosition);
            if (current != null && current.getId() == videoId) {//如果是当前的正在播放的item
                ToastUtil.showToast(message);
                DcIjkPlayerManager.get().resetUrl();
                pausePlay();
                if (currentViewHolder != null) {
                    currentViewHolder.dismissVideoLoading();
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
                    if (i >= first && i <= end) {
                        KLog.i("=======需要刷新的item position:" + (i + (hasHeader() ? 1 : 0) + " ,data position:" + i));
                        notifyItemChanged(i + (hasHeader() ? 1 : 0), RecommendAdapter.REFRESH_COMMENT);
                    }
                }
            }
        }
    }

    //刷新点赞
    public void refreshLike(long videoId, ShortVideoLoveResponse bean) {
        if (bean != null) {
            ShortVideoItem item;
            int first = findFirstVisibleDataPosition();
            int end = linearLayoutManager.findLastVisibleItemPosition();
            for (int i = 0, size = getDataContainer().size(); i < size; i++) {
                item = getDataContainer().get(i);
                if (item != null && item.getId() == videoId) {
                    item.setIs_like(bean.isIs_like());
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

    //刷新关注
    public void refreshFollow(boolean isFollowed,
                              long userId) {
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

    private int findFirstVisibleDataPosition() {
        return linearLayoutManager.findFirstVisibleItemPosition() - ((hasHeader() ? 1 : 0));
    }

    //评论列表中的弹幕开关切换
    public void switchDanma(boolean open) {
        if (currentViewHolder != null) {
            currentViewHolder.switchDanma(open);
        }
    }

    //刷新钻数量
    public void refreshGold(UserAccountEntity accountResponse) {
        if (currentViewHolder != null) {
            currentViewHolder.refreshGold();
        }
    }

    //礼物赠送成功之后发送礼物的弹幕
    public void addGiftDanma2(long giftId, int totalCount, int hintCount) {
        if (currentViewHolder != null) {
            currentViewHolder.refreshGold();
            if (SPUtils.getBoolean(DCApplication.getDCApp(), SPUtils.KEY_SHOW_DAMANKU, true)) {
                currentViewHolder.sendGift2Danma(giftId, totalCount, hintCount);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            //需要局部刷新的view
            if (holder instanceof RecommendViewHolder) {
                String type = (String) payloads.get(0);
                switch (type) {
                    case REFRESH_ALL://刷新评论数量
                        ((RecommendViewHolder) holder).setCommentCount();
                        ((RecommendViewHolder) holder).setGiftCount();
                        ((RecommendViewHolder) holder).refreshFollow();
                        ((RecommendViewHolder) holder).refreshGold();
                        ((RecommendViewHolder) holder).setLikeCount(false);
                        ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
                        if (null != shortVideoItem) {
                            if (shortVideoItem.getIs_delete() == 1) {
                                onVideoError(DCApplication.getDCApp().getString(R.string.stringWorkDeleted));
                            } else {//如果有弹幕，需要开启弹幕
                            }
                        }
                        break;
                    case REFRESH_COMMENT://刷新评论数量
                        ((RecommendViewHolder) holder).setCommentCount();
                        break;
                    case REFRESH_LIKE://点赞
                        ((RecommendViewHolder) holder).setLikeCount(true);
                        break;
                    case REFRESH_FOLLOW://关注
                        ((RecommendViewHolder) holder).refreshFollow();
                        break;
                    case REFRESH_VIDEO_ERROR:
                        ((RecommendViewHolder) holder).dismissVideoLoading();
                        break;
                    case REFRESH_PERMISSION_AND_TITLE:
                        ((RecommendViewHolder) holder).setTitle();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void onVideoError(String errorMessage) {
        findCurrentViewHolder();
        if (null != currentViewHolder) {
            currentViewHolder.dismissVideoLoading();
            currentViewHolder.setPlayIcon(true);
            currentViewHolder.setCoverVisible(true);
            currentViewHolder.hideDanmaKu();
        }
        DcIjkPlayerManager.get().pausePlay();
        DcIjkPlayerManager.get().setClickPause(false);
        DcIjkPlayerManager.get().resetUrl();
        ToastUtil.showToast(errorMessage);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        KLog.i("=========onViewAttachedToWindow1:" + holder.getAdapterPosition());
        if (holder instanceof RecommendViewHolder) {
            KLog.i("=========onViewAttachedToWindow2:" + holder.getAdapterPosition());
            ((RecommendViewHolder) holder).viewVisible();
            ((RecommendViewHolder) holder).getRootView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != onActiveCallback) {
                        onActiveCallback.onActive();
                    }
                }
            }, 20);
        }
    }

    /**
     * 恢复播放
     */
    public void resumePlay(int currentPlayDataPosition, boolean fromDetail) {
        DcIjkPlayerManager.get().setNeedPause(true);
        KLog.i("========adapter resumePlay currentPlayDataPosition:" + currentPlayDataPosition + " ,fromDetail:" + fromDetail);
        if (fromDetail) {
            KLog.i("====从详情回来，且播放的位置与Feed位置不一样");
            currentDataPosition = currentPlayDataPosition;
            playSomePosition(fromDetail);
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
            if (currentViewHolder != null) {
                currentViewHolder.resumeDanmaku();
            }
        } else {
            KLog.i("=====检测到当前滑动到的位置与之前位置一样");
            currentDataPosition = currentPlayDataPosition;
            playSomePosition(fromDetail);
        }
    }

    private void playSomePosition(boolean fromDetail) {
        if (currentDataPosition > -1 && currentDataPosition < getDataContainer().size()) {
            findCurrentViewHolder();
            ShortVideoItem shortVideoItem = getItemData(currentDataPosition);
            if (currentViewHolder != null && shortVideoItem != null) {
                if (shortVideoItem.itemType == 1) {
                    KLog.i("=====这是图片，不播放");
                    return;
                }
                if (shortVideoItem.getIs_delete() == 0) {
                    currentViewHolder.resumeDanmaku();
                    if (shortVideoViewCallback != null) {//获取视频详情
                        shortVideoViewCallback.getVideoDetail(currentDataPosition, getItemData(currentDataPosition).getId(), false);
                    }
                    if (fromDetail && shortVideoItem.getOpus_path().equals(DcIjkPlayerManager.get().getUrl())) {
                        KLog.i("======从详情返回,需要继续播放");
                        DcIjkPlayerManager.get().attachPlayer(currentViewHolder.getPlayerContainer(), false, null);
                        DcIjkPlayerManager.get().resumePlay(shortVideoItem.getId(), pageId, playListener);
                        return;
                    }
                    if (!shortVideoItem.getOpus_path().equals(DcIjkPlayerManager.get().getUrl())) {
                        KLog.i("======不是从详情返回1");
                        DcIjkPlayerManager.get().attachPlayer(currentViewHolder.getPlayerContainer(), null);
                        buffered = false;
                        DcIjkPlayerManager.get().setVideoUrl(pageId, shortVideoItem.getId(), shortVideoItem.getOpus_path(), playListener);
                    } else {
                        if (pageId != DcIjkPlayerManager.get().getPageId()) {
                            KLog.i("======不是从详情返回2");
                            DcIjkPlayerManager.get().attachPlayer(currentViewHolder.getPlayerContainer(), null);
                            buffered = false;
                            DcIjkPlayerManager.get().setVideoUrl(pageId, shortVideoItem.getId(), shortVideoItem.getOpus_path(), playListener);
                        } else {
                            KLog.i("======不是从详情返回3");
                            if (currentViewHolder.getPlayerContainer().getChildCount() == 0) {
                                DcIjkPlayerManager.get().attachPlayer(currentViewHolder.getPlayerContainer(), null);
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

    /**
     * 暂停播放
     */
    public void pausePlay() {
        if (pageId == DcIjkPlayerManager.get().getPageId()) {
            KLog.i("======暂停播放pageId：" + pageId + " ijkId:" + DcIjkPlayerManager.get().getPageId());
            if (currentViewHolder != null) {
                currentViewHolder.pauseDanmaKu();
            }
            DcIjkPlayerManager.get().pausePlay();
        }
    }

    /**
     * 释放播放器
     */
    public void releasePlayer() {
        KLog.i("====释放播放器");
        if (currentViewHolder != null) {
            currentViewHolder.releaseDanmaKu(true);
        }
        if (cacheVideoDisposable != null && !cacheVideoDisposable.isDisposed()) {
            cacheVideoDisposable.dispose();
        }
        cacheVideoDisposable = null;
        DcIjkPlayerManager.get().releasePlayer(true);
        if (cacheImageDisposable != null && !cacheImageDisposable.isDisposed()) {
            cacheImageDisposable.dispose();
        }
        cacheImageDisposable = null;
        playListener = null;
        onActiveCallback = null;
    }

    public void addDataList(boolean isRefresh, List<ShortVideoItem> newDataList,
                            boolean hasMore) {
        addDataList(isRefresh, newDataList, null, hasMore, true);
    }

    public void addDataList(boolean isRefresh, List<ShortVideoItem> newDataList, List<Banner> bannerList, boolean hasMore, boolean stopPlay) {
        KLog.i("======插入数据2size：" + (newDataList == null ? 0 : newDataList.size()));
        if (stopPlay) {
            resetPlay(isRefresh);
        }
        //添加广告图片
        if (!CollectionUtil.isEmpty(bannerList) && !CollectionUtil.isEmpty(newDataList)) {
            int nextListSize = newDataList.size();
            for (Banner banner : bannerList) {
                if (banner != null && banner.banner_index < nextListSize) {
                    newDataList.add(banner.banner_index, ShortVideoItem.newPictureItem(banner));
                    nextListSize++;
                    KLog.i("=====图片添加位置：" + banner.banner_index + " ,newDataList size:" + nextListSize);
                }
            }
        }
        addData(isRefresh, newDataList, hasMore);
        if (isRefresh) {
            getRecycleView().scrollToPosition(0);
        }
    }

    private void resetPlay(boolean isRefresh) {
        if (isRefresh) {
            if (currentViewHolder != null) {
                currentViewHolder.releaseDanmaKu(false);
            }
            DcIjkPlayerManager.get().pausePlay();
            DcIjkPlayerManager.get().resetUrl();
            currentDataPosition = 0;
            nextPosition = 0;
        }
    }

    public void setOnActiveCallback(OnActiveCallback onActiveCallback) {
        this.onActiveCallback = onActiveCallback;
    }

    private OnActiveCallback onActiveCallback;
}
