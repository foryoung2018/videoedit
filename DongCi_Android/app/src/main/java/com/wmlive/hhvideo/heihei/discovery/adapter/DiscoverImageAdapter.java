package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicTypeListBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.discovery.viewholder.DiscoverImageViewHolder;
import com.wmlive.hhvideo.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/15/2017.
 */

public class DiscoverImageAdapter extends RecyclerView.Adapter<DiscoverImageViewHolder> {

    private List<ShortVideoItem> itemList;
    private DiscoveryAdapter.OnDiscoverClickListener clickListener;
    private TopicTypeListBean.TopicListBean topicListBean;
    private static final int NORMAL_MARGIN = DeviceUtils.dip2px(DCApplication.getDCApp(), 6);

    public DiscoverImageAdapter(List<ShortVideoItem> itemList, TopicTypeListBean.TopicListBean topicListBean, DiscoveryAdapter.OnDiscoverClickListener clickListener) {
        this.itemList = itemList;
        this.clickListener = clickListener;
        this.topicListBean = topicListBean;
    }

    @Override
    public DiscoverImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscoverImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_discovery_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(final DiscoverImageViewHolder holder, final int position) {
        final ShortVideoItem shortVideoItem = itemList.get(position);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.rlCover.getLayoutParams();
        layoutParams.setMargins(position == 0 ? NORMAL_MARGIN : 0, 0, (position == itemList.size() - 1) ? NORMAL_MARGIN : 0, 0);
        holder.rlCover.setLayoutParams(layoutParams);
        if (holder.getAdapterPosition() == 10) {
            holder.ivCover.setVisibility(View.GONE);
            holder.viewMore.setVisibility(View.VISIBLE);
        } else if (holder.getAdapterPosition() < 10) {
            holder.ivCover.setVisibility(View.VISIBLE);
            holder.viewMore.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(shortVideoItem.getOpus_gif_cover())) {
                holder.ivCover.setController(Fresco.newDraweeControllerBuilder()
                        .setUri(shortVideoItem.getOpus_gif_cover())
                        .setLowResImageRequest(ImageRequest.fromUri(shortVideoItem.getOpus_small_cover()))
                        .setOldController(holder.ivCover.getController())
                        .setAutoPlayAnimations(true)
                        .build());
            } else {
                holder.ivCover.setImageURI(shortVideoItem.getOpus_small_cover());
            }
        } else {
            holder.ivCover.setVisibility(View.GONE);
            holder.viewMore.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clickListener) {
                    if (position != 10) {
                        List<ShortVideoItem> list = new ArrayList<>(10);
                        list.addAll(itemList);
                        if (list.get(list.size() - 1) == null) {
                            list.remove(list.size() - 1);
                        }
                        clickListener.onPictureClick(topicListBean.getId(), topicListBean, list, shortVideoItem.getId(), holder.getAdapterPosition());
                    } else {
                        if (!TextUtils.isEmpty(topicListBean.getTopic_type())) {
                            switch (topicListBean.getTopic_type()) {
                                case "Topic":
                                    clickListener.onTopicClick(position, true, topicListBean.getId());
                                    break;
                                case "Music":
                                    clickListener.onTopicClick(position, false, topicListBean.getDefault_music_id());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    public void addData(List<ShortVideoItem> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


}
