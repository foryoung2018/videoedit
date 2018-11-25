package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicTypeListBean;
import com.wmlive.hhvideo.heihei.discovery.viewholder.TextTopicViewHolder;
import java.util.List;
import cn.wmlive.hhvideo.R;

/**
 * Created by jht on 8/3/2018
 * 类描述：文字话题列表适配器
 */
public class TextTopicAdapter extends RecyclerView.Adapter<TextTopicViewHolder>{
    private List<TopicTypeListBean.TopicListBean> list;
    private DiscoveryAdapter.OnDiscoverClickListener discoverClickListener;

    public TextTopicAdapter(List<TopicTypeListBean.TopicListBean> list, DiscoveryAdapter.OnDiscoverClickListener discoverClickListener) {
        this.list = list;
        this.discoverClickListener = discoverClickListener;
    }

    @Override
    public TextTopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextTopicViewHolder(parent,R.layout.item_text_topic);
    }

    @Override
    public void onBindViewHolder(TextTopicViewHolder holder, int position) {
        TopicTypeListBean.TopicListBean bean = list.get(holder.getAdapterPosition());
        holder.textTopicNameTv.setText("#"+bean.getName());
        holder.textTopicNameTv.setOnClickListener(view -> {
            if(discoverClickListener!=null){
                discoverClickListener.onTopicClick(0, true, bean.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
