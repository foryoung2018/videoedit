package com.wmlive.hhvideo.heihei.personal.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.personal.viewholder.BlankListViewHolder;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by Administrator on 3/22/2018.
 */

public class BlankListAdapter extends RefreshAdapter<BlankListViewHolder, SearchUserBean> {
    private static final String TYPE_REFRESH = "refresh";
    private OnUnblockClickListener onUnblockClickListener;

    public BlankListAdapter(List<SearchUserBean> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public BlankListViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new BlankListViewHolder(parent, R.layout.item_blank_list);
    }

    @Override
    public void onBindHolder(BlankListViewHolder holder, int position, SearchUserBean data) {
        if (data == null) {
            return;
        }
        holder.tvUnlock.setVisibility(data.isBlock ? View.VISIBLE : View.GONE);
        holder.tvName.setText(data.getName());
        GlideLoader.loadCircleImage(data.getCover_url(), holder.ivAvatar, data.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
        holder.tvUnlock.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (onUnblockClickListener != null) {
                    onUnblockClickListener.onUnblockClick(position, data.getId());
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (CollectionUtil.isEmpty(payloads)) {
            onBindViewHolder(holder, position);
        } else {
            if (holder instanceof BlankListViewHolder) {
                SearchUserBean data = getDataContainer().get(position);
                if (data != null) {
                    ((BlankListViewHolder) holder).tvUnlock.setVisibility(data.isBlock ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    public void refreshItem(int position, long userId) {
        if (position > -1 && position < getDataContainer().size()) {
            SearchUserBean userInfo = getDataContainer().get(position);
            if (userInfo != null && userId == userInfo.getId()) {
                userInfo.isBlock = false;
                notifyItemChanged(position, TYPE_REFRESH);
            }
        }
    }

    public void setUnblockClickListener(OnUnblockClickListener onUnblockClickListener) {
        this.onUnblockClickListener = onUnblockClickListener;
    }

    public interface OnUnblockClickListener {
        void onUnblockClick(int dataPosition, long userId);
    }
}
