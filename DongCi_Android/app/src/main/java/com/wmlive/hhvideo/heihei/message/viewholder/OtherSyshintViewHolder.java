package com.wmlive.hhvideo.heihei.message.viewholder;

import android.view.View;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 接收到的接口返信息
 * Created by admin on 2017/3/27.
 */

public class OtherSyshintViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.tv_im_detail_sys_hint)
    public TextView mTvContent;

    private MessageDetail mCurrentItemData;

    public OtherSyshintViewHolder(View itemView) {
        super(itemView);
    }

    public void setItemDate(MessageDetail itemDate) {
        mCurrentItemData = itemDate;
        if (mCurrentItemData != null) {
            MessageContent contentBean = mCurrentItemData.content;
            if (contentBean != null) {
                setTVContent(contentBean.desc);
            }
        }
    }

    public void setTVContent(String content) {
        mTvContent.setText(content);
    }

}
