package com.wmlive.hhvideo.heihei.message.viewholder;

import android.view.View;
import android.widget.TextView;

import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 接收的纯文本文案信息
 * Created by admin on 2017/3/27.
 */

public class OtherCommonViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.tv_im_detail_other_content)
    public TextView mTvContent;
    @BindView(R.id.view_im_details_red_bg)
    public View mViewBgRed;
    MessageDetail sourceData;

    public OtherCommonViewHolder(View itemView) {
        super(itemView);
    }

    public void setItemDate(MessageDetail itemDate) {
        this.sourceData = itemDate;
        if (sourceData == null) {
            return;
        }
        MessageContent contentBean = itemDate.content;
        if (contentBean != null) {
            mTvContent.setText(contentBean.desc);
        } else {
            mTvContent.setText("");
        }
        if (itemDate.getStatus() == MessageDetail.IM_STATUS_UNREAD) {
            //未读信息
            sourceData.setStatus(MessageDetail.IM_STATUS_READ);
            MessageManager.get().parseChatMessageList(sourceData);
//            MessageManager.get().insertOrReplaceMessageInfo(sourceData);
        }
    }


}
