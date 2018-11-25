package com.wmlive.hhvideo.heihei.message.viewholder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.utils.TimeUtil;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 系统时间
 * Created by admin on 2017/4/17.
 */

public class OtherSystimeViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.rl_other_sys_time)
    public RelativeLayout mRLOtherSysTime;
    @BindView(R.id.tv_im_detail_sys_content_time)
    public TextView mTvContent;

    private MessageDetail mCurrentItemData;
    private int position;

    public OtherSystimeViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 设置item数据
     *
     * @param itemDate
     */
    public void setItemDate(MessageDetail itemDate) {
        mCurrentItemData = itemDate;
        MessageContent contentBean = mCurrentItemData.content;
        if (contentBean != null) {
            setTVContent(contentBean.text);
        } else {
            setTVContent("");
        }
    }

    /**
     * 设置时间
     *
     * @param content
     */
    public void setTVContent(String content) {
        try {
            long time = Long.parseLong(content) * 1000;
            mTvContent.setText(TimeUtil.getNewChatTime(time));
        } catch (Exception e) {
            mTvContent.setText(content);
        }
    }

    public void setSysTimePosition(int position) {
        this.position = position;
        if (this.position == 0) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = 10;
            layoutParams.bottomMargin = 24;
            mRLOtherSysTime.setLayoutParams(layoutParams);
        }
    }
}
