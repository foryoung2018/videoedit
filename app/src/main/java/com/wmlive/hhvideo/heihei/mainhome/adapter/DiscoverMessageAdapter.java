package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.discovery.DiscMessageEntity;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.DiscoverMessageViewHolder;
import com.wmlive.hhvideo.utils.TimeUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/28/2018 - 2:48 PM
 * 类描述：
 */
public class DiscoverMessageAdapter extends RefreshAdapter<DiscoverMessageViewHolder, DiscMessageEntity> {
    public DiscoverMessageAdapter(List<DiscMessageEntity> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public DiscoverMessageViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new DiscoverMessageViewHolder(parent, R.layout.item_discover_message);
    }

    @Override
    public void onBindHolder(DiscoverMessageViewHolder holder, int position, DiscMessageEntity data) {
        holder.tvTitle.setText(data.title);
        holder.tvDesc.setText(data.news_desc);
        holder.tvTime.setText(TimeUtil.getMessageData(data.create_time * 1000));
        GlideLoader.loadVideoThumb(data.news_imgae, holder.ivPic,5);
        holder.ivPic.setVisibility(!TextUtils.isEmpty(data.news_imgae) ? View.VISIBLE : View.GONE);
        holder.tvEntry.setVisibility(!TextUtils.isEmpty(data.link) ? View.VISIBLE : View.GONE);
    }
}
