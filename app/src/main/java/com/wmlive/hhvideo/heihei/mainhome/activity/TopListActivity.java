package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoInfoResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.main.VideoListEventEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.adapter.TopListAdapter;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.mainhome.presenter.Block;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ImBannerPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.TopListPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.VideoDetailPresenter;
import com.wmlive.hhvideo.heihei.personal.activity.WebViewActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity2;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.VerifyDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class TopListActivity extends DcBaseActivity<TopListPresenter> implements
        TopListPresenter.ITopListView,
        RefreshRecyclerView.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, TopListAdapter.TopListListener, VideoDetailPresenter.IVideoDetailView {

    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;
    private TextView tvDetail;
    private TopListAdapter topListAdapter;
    private VerifyDialog verifyDialog;
    private int pageId;
    private VideoDetailPresenter videoDetailPresenter;

    @Override
    protected TopListPresenter getPresenter() {
        return new TopListPresenter(this);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_top_list;
    }

    @Override
    protected void initData() {
        super.initData();
        pageId = CommonUtils.getRandom(1000000, 9999999);
        EventHelper.register(this);
        setTitle("", true);
        videoDetailPresenter = new VideoDetailPresenter(this);
        addPresenter(videoDetailPresenter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        topListAdapter = new TopListAdapter(new ArrayList<>(10), recyclerView, layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(topListAdapter);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.setOnRefreshListener(this);
        topListAdapter.setListListener(this);
        recyclerView.autoRefresh(300);
        tvDetail = new TextView(this);
        tvDetail.setText("榜单规则");
        tvDetail.setTextColor(getResources().getColor(R.color.video_text_color));
        tvDetail.setTextSize(18f);
        tvDetail.setPadding(10, DeviceUtils.dip2px(this, 14), DeviceUtils.dip2px(this, 15), 0);
        setToolbarRightView(tvDetail, new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                String url = InitCatchData.getInitCatchData().getOpus().getOpusTopRule();
                WebViewActivity.startWebActivity(TopListActivity.this, url, "");
                Log.i("===yang ", "onMyClick");
            }
        });

    }

    @Override
    public void onTopListOk(boolean isRefresh, List<ShortVideoItem> videoItemList, boolean hasMore, String detail) {
        List<ShortVideoItem> newList = null;
        int oldAll = topListAdapter.getDataContainer().size();
        if (!CollectionUtil.isEmpty(videoItemList)) {
            newList = new ArrayList<>(videoItemList.size());
            ShortVideoItem item;
            int level;
            for (int i = 0, size = videoItemList.size(); i < size; i++) {
                item = videoItemList.get(i);
                if (item != null) {
                    if (isRefresh) {
                        level = (i + 1);
                    } else {
                        level = oldAll + 1 + i;
                    }
                    item.level = level;
                    item.distinctAllUser();
                    newList.add(item);
                }
            }
        }
        topListAdapter.addData(isRefresh, newList, hasMore);
        if (!TextUtils.isEmpty(detail)) {
            topListAdapter.setTitle(detail);
        }
    }

    @Override
    public void onTopListFail(boolean isRefresh, String message) {
        showToast(message);
        topListAdapter.showError(isRefresh);
    }

    @Override
    public void onRefresh() {
        presenter.getTopList(true);

    }

    @Override
    public void onLoadMore() {
        presenter.getTopList(false);
    }


    @Override
    public void onUserClick(long userId) {

    }

    @Override
    public void onVideoClick(int position, List<ShortVideoItem> list) {
        if (!CollectionUtil.isEmpty(list)) {
            MultiTypeVideoBean multiTypeVideoBean = new MultiTypeVideoBean();
            multiTypeVideoBean.currentPosition = position;
            multiTypeVideoBean.nextPageOffset = presenter.getOffset();
            TransferDataManager.get().setVideoListData(list);
            VideoDetailListActivity.startVideoDetailListActivity(this
                    , pageId, RecommendFragment.TYPE_TOP_LIST, multiTypeVideoBean, null, null);
        }
    }

    @Override
    public void onTopicClick(long topicId) {
        VideoListActivity.startVideoListActivity(this, RecommendFragment.TYPE_TOPIC,
                MultiTypeVideoBean.createTopicParma(topicId, 0, null));
    }

    @Override
    public void onJoinClick(ShortVideoItem item) {
        if (item != null) {
            if (AccountUtil.isLogin()) {
                if (item.is_teamwork == 1) {
                    if (AccountUtil.needVerifyCode()) {
                        showVerifyDialog(item, new Block() {
                            @Override
                            public void run() {
                                if (item != null) {
                                    videoDetailPresenter.getVideoDetail(0, item.getId(), 0);
                                }
                            }
                        });
                    } else {
                        if (item != null) {
                            videoDetailPresenter.getVideoDetail(0, item.getId(), 0);
                        }
                    }
                } else {
                    ToastUtil.showToast(item.teamwork_tips);
                }
            } else {
                showReLogin();
            }
        }
    }

    private void showVerifyDialog(ShortVideoItem shortVideoItem, Block block) {
        if (shortVideoItem != null) {
            if (verifyDialog == null) {
                verifyDialog = new VerifyDialog(TopListActivity.this);
                verifyDialog.setOnVerifyListener(new VerifyDialog.OnVerifyListener() {
                    @Override
                    public void onVerifySuccess() {
                        if (shortVideoItem != null) {
                            videoDetailPresenter.getVideoDetail(0, shortVideoItem.getId(), 0);
                        }
                    }
                });
            }
            if (!verifyDialog.isShowing()) {
                verifyDialog.show();
            }
        }
    }

    @Override
    public void onVideoDetailOk(long videoId, int position, ShortVideoInfoResponse videoInfoBean) {
        if (!CollectionUtil.isEmpty(topListAdapter.getDataContainer())) {
            ShortVideoItem currentItem = null;
            ShortVideoItem item;
            for (int i = 0, size = topListAdapter.getDataContainer().size(); i < size; i++) {
                item = topListAdapter.getDataContainer().get(i);
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
                    }
                    if (videoInfoBean.getUser() != null) {
                        item.setUser(videoInfoBean.getUser());
                    }
                    currentItem = item;
                }
            }
            if (currentItem != null) {
                if (currentItem.is_teamwork == 1) {
                    if (5 == currentItem.origin) {
                        String template = currentItem.creative_template_name;
                        if (RecordMvActivityHelper.isNoInvalidTemplate(this, template)) {
                            ToastUtil.showToast(getResources().getString(R.string.temp_no_can_use));
                        } else {
                            RecordMvActivityHelper.startRecordActivity(this, RecordMvActivityHelper.EXTRA_RECORD_TYPE_REPLACE, currentItem.id);
                        }

                    } else {
                        SelectFrameActivity2.startSelectFrameActivity2(this,
                                SelectFrameActivity.VIDEO_TYPE_TEAMWORK, currentItem.getId(),
                                currentItem.frame_layout);
                    }
                } else {
                    showToast(currentItem.teamwork_tips);
                }
            } else {
                showToast(R.string.hintErrorDataDelayTry);
            }
        }
    }

    @Override
    public void onVideoError(int position, long videoId, String message) {
        showToast(R.string.hintErrorDataDelayTry);
    }

    @Override
    protected void onDestroy() {
        EventHelper.unregister(this);
        super.onDestroy();
    }

    /**
     * 点赞事件
     *
     * @param eventEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeOk(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_LIKE_OK) {
            KLog.i("=====刷新点赞:" + eventEntity.data);
            if (eventEntity.data instanceof ShortVideoLoveResponse) {
                ShortVideoLoveResponse e = (ShortVideoLoveResponse) eventEntity.data;
                topListAdapter.refreshLike(e.opus_id, e);
            }
        }
    }

    //评论成功和删除评论刷新数量
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentOkEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_REFRESH_COMMENT) {
            RefreshCommentBean commentBean = (RefreshCommentBean) eventEntity.data;
            if (null != commentBean) {
                topListAdapter.refreshCommentCount(commentBean);
            }
        }
    }

    //视频删除
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_VIDEO_DELETE) {
            long videoId = (long) eventEntity.data;
            if (!CollectionUtil.isEmpty(topListAdapter.getDataContainer())) {
                for (ShortVideoItem shortVideoItem : topListAdapter.getDataContainer()) {
                    if (shortVideoItem != null && shortVideoItem.getId() == videoId) {
                        onRefresh();
                        break;
                    }
                }
            }
        }
    }

    //同步视频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoListEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_VIDEO_LIST) {
            if (eventEntity.data instanceof VideoListEventEntity) {
                VideoListEventEntity entity = (VideoListEventEntity) eventEntity.data;
                KLog.i("=======同步视频列表");
                if (entity.fromPageId == pageId) {
                    onTopListOk(entity.isRefresh, TransferDataManager.get().getVideoListData(), entity.hasMore, null);
                    presenter.setOffset(entity.nextPageOffset);
                }
            }
        }
    }


}
