package com.wmlive.hhvideo.heihei.message.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.mainhome.util.CenterImageSpan;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/3/19.
 */

public class OtherCommonGiftViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.tv_im_detail_other_content_gift)
    public TextView mTvContent;

    private Context mContext;
    private MessageDetail mData;

    public OtherCommonGiftViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;
    }

    public void setCurrentItemData(MessageDetail itemData) {
        if (itemData == null) {
            return;
        }
        mData = itemData;
        MessageContent contentBean = itemData.content;
        if (contentBean != null) {
//            mTvContent.setText(itemData.content.desc);
            SpannableStringBuilder builder = new SpannableStringBuilder(itemData.content.desc + "  ");
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_profile_gift_48_48);
            int imageWidth = DeviceUtils.dip2px(DCApplication.getDCApp(), 16);
            drawable.setBounds(0, 0, imageWidth, imageWidth);
            CenterImageSpan imageSpan = new CenterImageSpan(drawable);
            builder.setSpan(imageSpan, builder.length() - 1, builder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mTvContent.setText(builder);
        }
        if (itemData.getStatus() == MessageDetail.IM_STATUS_UNREAD) {
            //未读信息
            mData.setStatus(MessageDetail.IM_STATUS_READ);
            MessageManager.get().parseChatMessageList(mData);
//            MessageManager.get().insertOrReplaceMessageInfo(mData);
        }
    }
}
