package com.wmlive.hhvideo.heihei.message.viewholder;

import android.view.View;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * 无法确定发送者
 * Created by admin on 2017/3/27.
 */

public class NoViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.tv_im_detail_no_holder)
    public TextView mTv_content;
    private MessageDetail mCurrentItemData;

    public NoViewHolder(View itemView) {
        super(itemView);
    }

    public void setItemDate(MessageDetail itemDate) {
        this.mCurrentItemData = itemDate;
    }

    public void setTVContent(String content) {
        mTv_content.setText(content);
    }

}
