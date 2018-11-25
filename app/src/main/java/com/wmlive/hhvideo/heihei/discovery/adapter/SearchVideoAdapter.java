package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.wmlive.hhvideo.heihei.beans.discovery.LocalVideoBean;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.discovery.viewholder.SearchVideoViewHolder;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/13/2017.
 * 搜索本地视频的Adapter
 */

public class SearchVideoAdapter extends RecyclerView.Adapter<SearchVideoViewHolder> {
    private List<LocalVideoBean> dataList;

    public SearchVideoAdapter() {
//        setHasStableIds(true);
        dataList = new ArrayList<>();
    }

    public void setData(List<LocalVideoBean> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public SearchVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchVideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_video, parent, false));
    }

    @Override
    public void onBindViewHolder(final SearchVideoViewHolder holder, final int position) {
        final LocalVideoBean bean = dataList.get(holder.getAdapterPosition());
        holder.tvDuring.setText(String.valueOf(DiscoveryUtil.convertTime((int) bean.duration / 1000)));

        String path = DiscoveryUtil.getThumbnailPathForLocalFile(holder.ivCover.getContext(), bean.id);

        if (!TextUtils.isEmpty(path) && (new File(path).exists())) {
            Glide.with(holder.ivCover.getContext())
                    .load(path).transition(DrawableTransitionOptions.withCrossFade(150))
                    .apply(new RequestOptions().override(128, 128).placeholder(R.drawable.bg_video_default_4_3))
                    .into(holder.ivCover);

        } else if(!TextUtils.isEmpty(bean.path) && (new File(bean.path).exists())) {
            Glide.with(holder.ivCover.getContext())
                    .load(Uri.fromFile(new File(bean.path))).transition(DrawableTransitionOptions.withCrossFade(150))
                    .apply(new RequestOptions().override(128, 128).placeholder(R.drawable.bg_video_default_4_3))
                    .into(holder.ivCover);
        } else{
            holder.ivCover.setImageResource(R.drawable.bg_video_default_4_3);
        }
        holder.ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onVideoSelect(dataList.get(holder.getAdapterPosition()));
                    KLog.i("path--->"+dataList.get(holder.getAdapterPosition()).path);
                }
            }
        });
    }

    public void addData(List<LocalVideoBean> list) {
//        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }
    public void addData(LocalVideoBean bean) {
//        dataList.clear();
        dataList.add(bean);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    private OnVideoSelectListener listener;

    public void setOnVideoSelectListener(OnVideoSelectListener listener) {
        this.listener = listener;
    }

    public interface OnVideoSelectListener {
        void onVideoSelect(LocalVideoBean bean);
    }

}
