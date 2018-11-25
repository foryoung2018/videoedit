package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.TopListFirstViewHolder;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.TopListViewHolder;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/28/2018 - 11:31 AM
 * 类描述：
 */
public class TopListAdapter extends RefreshAdapter<BaseRecyclerViewHolder, ShortVideoItem> {
    private static final int PIC_RADIUS = 5;
    private static final int TYPE_FIRST = 110;
    private TopListListener listListener;
    private LinearLayoutManager layoutManager;
    private String title;

    public void setTitle(String title){
        this.title = title;
        notifyDataSetChanged();
    }


    public TopListAdapter(List<ShortVideoItem> list, RefreshRecyclerView refreshView, LinearLayoutManager layoutManager) {
        super(list, refreshView);
        this.layoutManager = layoutManager;
        setItemTypes(new ArrayList<Integer>() {{
            add(TYPE_FIRST);
        }});
    }

    @Override
    public BaseRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FIRST) {
            return new TopListFirstViewHolder(parent, R.layout.item_top_list_first);
        } else {
            return new TopListViewHolder(parent, R.layout.item_top_list);
        }
    }

    @Override
    public void onBindHolder(BaseRecyclerViewHolder h, int position, ShortVideoItem data) {
        if (data == null) {
            return;
        }
        if (h instanceof TopListViewHolder) {
            TopListViewHolder holder = (TopListViewHolder) h;
            holder.tvLevel.setText(String.valueOf(data.level));
            GlideLoader.loadVideoThumb(data.opus_cover, holder.ivPic, PIC_RADIUS, R.drawable.bg_home_video_default);
            holder.tvName.setText(data.getTitle());
            int levelColorId;
            if (position == 1) {
                levelColorId = R.color.hh_color_level2;
            } else if (position == 2) {
                levelColorId = R.color.hh_color_level3;
            } else {
                levelColorId = R.color.hh_color_level;
            }
            holder.tvLevel.setTextColor(holder.tvLevel.getResources().getColor(levelColorId));
            holder.tvTopic.setText("#" + data.getTopic_name());
            holder.tvLikeCount.setText(CommonUtils.getCountString(data.like_count, true));
            holder.tvCommentCount.setText(CommonUtils.getCountString(data.comment_count, true));
            holder.tvPeoples.setText(data.allUsers);
            holder.tvName.setVisibility(!TextUtils.isEmpty(data.getTitle()) ? View.VISIBLE : View.INVISIBLE);
            holder.tvTopic.setVisibility(!TextUtils.isEmpty(data.getTopic_name()) ? View.VISIBLE : View.INVISIBLE);
            holder.ivPic.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (listListener != null) {
                        listListener.onVideoClick(position, getDataContainer());
                    }
                }
            });
            holder.tvName.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (listListener != null) {
                        listListener.onVideoClick(position, getDataContainer());
                    }
                }
            });
            holder.ivJoin.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (listListener != null) {
                        listListener.onJoinClick(data);
                    }
                }
            });
            holder.tvTopic.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (listListener != null) {
                        listListener.onTopicClick(data.topic_id);
                    }
                }
            });
        } else if (h instanceof TopListFirstViewHolder) {
            TopListFirstViewHolder holder = (TopListFirstViewHolder) h;
            holder.rlPlayerContainer.setRatio(data.feed_width_height_rate);
            holder.ivLevel.setVisibility(View.VISIBLE);
            GlideLoader.loadVideoThumb(data.opus_cover, holder.ivPic, PIC_RADIUS, R.drawable.bg_home_video_default);
            holder.tvName.setText(data.getTitle());
            holder.tvTopic.setText(data.getTopic_name());
            holder.topTitle.setText(title);
            holder.tvLikeCount.setText(CommonUtils.getCountString(data.like_count, true));
            holder.tvCommentCount.setText(CommonUtils.getCountString(data.comment_count, true));
            holder.tvPeoples.setText(data.allUsers);
            holder.tvName.setVisibility(!TextUtils.isEmpty(data.getTitle()) ? View.VISIBLE : View.GONE);
            holder.tvTopic.setVisibility(!TextUtils.isEmpty(data.getTopic_name()) ? View.VISIBLE : View.GONE);
            holder.ivPic.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (listListener != null) {
                        listListener.onVideoClick(position, getDataContainer());
                    }
                }
            });
            holder.tvName.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (listListener != null) {
                        listListener.onVideoClick(position, getDataContainer());
                    }
                }
            });
            holder.ivJoin.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (listListener != null) {
                        listListener.onJoinClick(data);
                    }
                }
            });
            holder.tvTopic.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (listListener != null) {
                        listListener.onTopicClick(data.topic_id);
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            String type = (String) payloads.get(0);
            ShortVideoItem data = getItemData(position);
            switch (type) {
                case RecommendAdapter.REFRESH_COMMENT:
                    if (holder instanceof TopListFirstViewHolder) {
                        ((TopListFirstViewHolder) holder).tvCommentCount.setText(CommonUtils.getCountString(data.comment_count, true));
                    } else if (holder instanceof TopListViewHolder) {
                        ((TopListViewHolder) holder).tvCommentCount.setText(CommonUtils.getCountString(data.comment_count, true));
                    }
                    break;
                case RecommendAdapter.REFRESH_LIKE:
                    if (holder instanceof TopListFirstViewHolder) {
                        ((TopListFirstViewHolder) holder).tvLikeCount.setText(CommonUtils.getCountString(data.like_count, true));
                    } else if (holder instanceof TopListViewHolder) {
                        ((TopListViewHolder) holder).tvLikeCount.setText(CommonUtils.getCountString(data.like_count, true));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //刷新评论数量
    public void refreshCommentCount(RefreshCommentBean bean) {
        if (bean != null) {
            ShortVideoItem item;
            int first = findFirstVisibleDataPosition();
            int end = layoutManager.findLastVisibleItemPosition();
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
            int end = layoutManager.findLastVisibleItemPosition();
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

    private int findFirstVisibleDataPosition() {
        return layoutManager.findFirstVisibleItemPosition() - ((hasHeader() ? 1 : 0));
    }

    @Override
    public int getItemType(int position) {
        return position == 0 ? TYPE_FIRST : TYPE_CONTENT;
    }

    public void setListListener(TopListListener listListener) {
        this.listListener = listListener;
    }

    public interface TopListListener {
        void onUserClick(long userId);

        void onVideoClick(int position, List<ShortVideoItem> list);

        void onTopicClick(long topicId);

        void onJoinClick(ShortVideoItem item);

    }
}
