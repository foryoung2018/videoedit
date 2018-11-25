package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.mainhome.widget.VideoDetailItemView1;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/7/2018 - 11:37 AM
 * 类描述：
 */
public class VideoDetailViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.itemVideoDetail)
    public VideoDetailItemView1 itemVideoDetail;

    public VideoDetailViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
//        int l = R.layout.item_video_detail_view_holder;
    }
}
