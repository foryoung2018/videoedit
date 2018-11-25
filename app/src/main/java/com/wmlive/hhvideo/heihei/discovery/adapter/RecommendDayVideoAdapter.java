package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.TransferDataManager;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.discovery.viewholder.RecommendDayVideoViewHolder;
import com.wmlive.hhvideo.heihei.mainhome.activity.VideoDetailListActivity;
import com.wmlive.hhvideo.heihei.mainhome.adapter.RecommendAdapter;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/28/2018 - 4:36 PM
 * 类描述：
 */
public class RecommendDayVideoAdapter extends RecyclerView.Adapter<RecommendDayVideoViewHolder> {
    public static final int MAX_RATIO = 1000;
    private List<ShortVideoItem> dataList;
    private DiscoveryAdapter.OnDiscoverClickListener recommendDayClickListener;
    private LinearLayoutManager linearLayoutManager;

    public RecommendDayVideoAdapter(List<ShortVideoItem> dataList, DiscoveryAdapter.OnDiscoverClickListener listener, LinearLayoutManager layoutManager) {
        linearLayoutManager = layoutManager;
        this.dataList = dataList;
        this.recommendDayClickListener = listener;
    }

    @Override
    public RecommendDayVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R
                .layout.item_recommend_day_video, parent, false);
        return new RecommendDayVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommendDayVideoViewHolder holder, int position) {
        if (dataList!=null && dataList.size() > 0) {
            int index = position % dataList.size();
            ShortVideoItem item = dataList.get(index);
            GlideLoader.loadImage(item.recommend_cover, holder.ivPic);
            holder.tvName.setText(item.getAuthorName());
            GlideLoader.loadCircleImage(item.getAuthorAvatar(), holder.ivAvatar, item.getUser() != null && item.getUser().isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
            holder.tvLikeCount.setText(CommonUtils.getCountString(item.like_count, true));
            holder.tvCommentCount.setText(CommonUtils.getCountString(item.comment_count, true));
            holder.tvTopic.setVisibility(!TextUtils.isEmpty(item.recommend_title) ? View.VISIBLE : View.INVISIBLE);
            holder.tvTopic.setText(item.recommend_title);
//            holder.tvTopic.setText("#这是话题");
//            holder.tvTopic.setVisibility(View.VISIBLE);
//            holder.tvTopic.setOnClickListener(new MyClickListener() {
////                @Override
////                protected void onMyClick(View v) {
////                    VideoListActivity.startVideoListActivity(holder.tvTopic.getContext(), RecommendFragment.TYPE_TOPIC,
////                            MultiTypeVideoBean.createTopicParma(item.topic_id, 0, null));
////                }
////            });
            holder.ivPic.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    MultiTypeVideoBean multiTypeVideoBean = new MultiTypeVideoBean();
                    multiTypeVideoBean.currentPosition = index;
                    TransferDataManager.get().setVideoListData(dataList);
                    DcIjkPlayerManager.get().resetUrl();
                    VideoDetailListActivity.startVideoDetailListActivity(holder.ivPic.getContext()
                            , 0, RecommendFragment.TYPE_EXPLOSION, multiTypeVideoBean, null, null);

                }
            });
            holder.ivJoin.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (recommendDayClickListener != null) {
                        recommendDayClickListener.onJoinClick(item);
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(RecommendDayVideoViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            String type = (String) payloads.get(0);
            ShortVideoItem item = dataList.get(position % dataList.size());
            if (item != null) {
                switch (type) {
                    case RecommendAdapter.REFRESH_COMMENT:
                        holder.tvCommentCount.setText(CommonUtils.getCountString(item.comment_count, true));
                        break;
                    case RecommendAdapter.REFRESH_LIKE:
                        holder.tvLikeCount.setText(CommonUtils.getCountString(item.like_count, true));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //刷新评论数量
    public void refreshCommentCount(RefreshCommentBean bean) {
        if (bean != null) {
            int size = dataList.size();
            if (size > 0) {
                ShortVideoItem item;
                int first = linearLayoutManager.findFirstVisibleItemPosition();
                int end = linearLayoutManager.findLastVisibleItemPosition();
                for (int i = 0; i < size; i++) {
                    item = dataList.get(i);
                    if (item != null && item.getId() == bean.videoId) {
                        item.setComment_count(bean.count);
                        for (int j = first; j <= end; j++) {
                            if (j % size == i) {
                                notifyItemChanged(j, RecommendAdapter.REFRESH_COMMENT);
                                KLog.i("======刷新位置:" + j + " ,数据位置:" + i);
                            }
                        }
                    }
                }
            }
        }
    }

    //刷新点赞
    public void refreshLike(long videoId, ShortVideoLoveResponse bean) {
        if (bean != null) {
            int size = dataList.size();
            if (size > 0) {
                ShortVideoItem item;
                int first = linearLayoutManager.findFirstVisibleItemPosition();
                int end = linearLayoutManager.findLastVisibleItemPosition();
                for (int i = 0; i < size; i++) {
                    item = dataList.get(i);
                    if (item != null && item.getId() == videoId) {
                        item.setIs_like(bean.isIs_like());
                        if (null != bean.getData_count()) {
                            item.setLike_count(bean.getData_count().getLike_count());
                        }
                        for (int j = first; j <= end; j++) {
                            if (j % size == i) {
                                notifyItemChanged(j, RecommendAdapter.REFRESH_LIKE);
                                KLog.i("======刷新位置:" + j + " ,数据位置:" + i);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addData(List<ShortVideoItem> list) {
        dataList.clear();
        if (list != null) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size() * MAX_RATIO;
    }

}
