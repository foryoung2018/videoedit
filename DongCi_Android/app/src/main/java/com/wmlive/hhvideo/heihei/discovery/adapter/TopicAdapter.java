package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.discovery.viewholder.TopicViewHolder;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/1/2017.
 * 话题页面Adapter
 */

public class TopicAdapter extends RefreshAdapter<TopicViewHolder, ShortVideoItem> {

//    private int imagePadding;

    public TopicAdapter(List<ShortVideoItem> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
//        imagePadding = DeviceUtils.dip2px(refreshView.getContext(), 1);
    }

    @Override
    public TopicViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new TopicViewHolder(parent, R.layout.item_topic);
    }

    @Override
    public void onBindHolder(TopicViewHolder holder, int position, ShortVideoItem data) {
        if (data != null) {
            holder.tvCount.setText(String.valueOf(data.getLike_count()));

            if (!TextUtils.isEmpty(data.getOpus_gif_cover())) {
                holder.ivCover.setController(Fresco.newDraweeControllerBuilder()
                        .setLowResImageRequest(ImageRequest.fromUri(data.getOpus_small_cover()))
                        .setOldController(holder.ivCover.getController())
                        .setUri(data.getOpus_gif_cover()).setAutoPlayAnimations(true).build());
            } else {
                holder.ivCover.setImageURI(data.getOpus_small_cover());
            }

            holder.ivTag.setVisibility(position < 3 ? View.VISIBLE : View.GONE);
            switch (position) {
                case 0:
                    holder.ivTag.setImageResource(R.drawable.icon_detail_hot1);
                    break;
                case 1:
                    holder.ivTag.setImageResource(R.drawable.icon_detail_hot2);
                    break;
                case 2:
                    holder.ivTag.setImageResource(R.drawable.icon_detail_hot3);
                    break;
                default:
                    break;
            }
        }
    }
}
