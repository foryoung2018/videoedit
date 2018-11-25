package com.wmlive.hhvideo.heihei.subject.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 话题搜索信息展示viewholder
 * <p>
 * Created by kangzhen on 2017/6/2.
 */

public class SubjectSearchViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.tv_item_subject_list_name)
    public TextView tv_item_subject_list_name;
    @BindView(R.id.tv_item_subject_list_count)
    public TextView tv_item_subject_list_count;
    @BindView(R.id.tv_item_subject_list_des)
    public TextView tv_item_subject_list_des;

    public SubjectSearchViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
