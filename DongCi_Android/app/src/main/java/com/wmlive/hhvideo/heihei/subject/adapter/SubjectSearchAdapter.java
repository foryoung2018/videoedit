package com.wmlive.hhvideo.heihei.subject.adapter;


import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.search.SearchTopicBean;
import com.wmlive.hhvideo.heihei.subject.viewholder.SubjectSearchViewHolder;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;


/**
 * 话题搜索页面适配器
 * Created by admin on 2017/5/31.
 */

public class SubjectSearchAdapter extends RefreshAdapter<SubjectSearchViewHolder, SearchTopicBean> {

    public SubjectSearchAdapter(List<SearchTopicBean> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public SubjectSearchViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new SubjectSearchViewHolder(parent, R.layout.item_subject_list_layout);
    }

    @Override
    public void onBindHolder(SubjectSearchViewHolder holder, int position, SearchTopicBean data) {
        if (data != null && data.getVisible() > 0) {
            holder.tv_item_subject_list_name.setText(data.getName());
            holder.tv_item_subject_list_count.setText(String.valueOf(data.getOpus_count()) + "人参与");
            holder.tv_item_subject_list_des.setText(data.getDescription());
        }
    }
}
