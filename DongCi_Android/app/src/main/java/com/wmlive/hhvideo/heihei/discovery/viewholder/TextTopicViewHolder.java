package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;
import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class TextTopicViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.text_topic_name_tv)
    public TextView textTopicNameTv;
    public TextTopicViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
