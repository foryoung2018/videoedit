package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wmlive.hhvideo.heihei.beans.discovery.FocusBean;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.FocusItemViewHolder;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import java.util.List;
import cn.wmlive.hhvideo.R;

/**
 * Created by jht on 7/31/2018 - 14:12 PM
 * 类描述：聚焦
 */
public class FocusSingerAdapter extends RecyclerView.Adapter<FocusItemViewHolder> {
    private List<FocusBean> focusList;
    private DiscoveryAdapter.OnDiscoverClickListener focusSingerClickListener;

    public FocusSingerAdapter(List<FocusBean> focusList, DiscoveryAdapter.OnDiscoverClickListener focusSingerClickListener) {
        this.focusList = focusList;
        this.focusSingerClickListener = focusSingerClickListener;
    }

    @Override
    public FocusItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_focus_singer,parent,false);
        return new FocusItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FocusItemViewHolder holder, int position) {
        FocusBean bean = focusList.get(position);
        holder.songNameTv.setText(bean.getTitle());
        holder.singerNameTv.setText(bean.getSub_title());
        GlideLoader.loadImage(bean.getCover(), holder.singerCoverIv);
        holder.singerCoverIv.setOnClickListener(view -> {
            if(focusSingerClickListener!=null){
                focusSingerClickListener.onFocusClick(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return focusList == null ? 0 : focusList.size();
    }
}
