package com.wmlive.hhvideo.heihei.message.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wmlive.hhvideo.fresco.FrescoImageHelper;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageJump;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.message.activity.IMMessageActivity;
import com.wmlive.hhvideo.utils.UIUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 接收的系统通知
 * Created by admin on 2017/3/27.
 */

public class OtherSysNotifyViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.ll_im_other_sysnotify_layout)
    public LinearLayout mIMOtherSysnotify;
    @BindView(R.id.tv_im_detail_other_sysnotify_content)
    public TextView mTvContent;
    @BindView(R.id.tv_im_detail_other_sysnotify_img)
    public SimpleDraweeView mIvIcon;

    public Context mContext;
    private String strJumpText; //链接标题
    private String strJumpLink; //链接地址
    private MessageDetail mCurrentItemData;
    private IMMessageActivity.MyIMGotoOtherFragmentHandler gotoHandler;

    public OtherSysNotifyViewHolder(View itemView, Context context, final IMMessageActivity.MyIMGotoOtherFragmentHandler gotoHandler) {
        super(itemView);
        mContext = context;
        this.gotoHandler = gotoHandler;
        mIMOtherSysnotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(strJumpLink)) {
                    Message message = Message.obtain();
                    message.what = IMMessageActivity.HANDLER_WHAT_GOTO_OTHER_PAGE;
                    message.obj = strJumpLink;
                    gotoHandler.sendMessage(message);
                }
            }
        });
    }

    public void setItemDate(MessageDetail itemData) {
        mCurrentItemData = itemData;
        MessageContent contentBean = itemData.content;
        if (contentBean != null) {
            //头像
            setTvImageUrl(contentBean.icon);
            //内容
            String content = contentBean.title + (!TextUtils.isEmpty(contentBean.title) ? "\n" : "") + contentBean.desc;
            if (!TextUtils.isEmpty(contentBean.title)) {
                SpannableString spanString = new SpannableString(content);
                spanString.setSpan(new RelativeSizeSpan(1.15f), 0, contentBean.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, contentBean.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTvContent.append(spanString);
            } else {
                mTvContent.append(content);
            }
            //查看
            MessageJump jumpBean = contentBean.jump;
            if (jumpBean != null) {
                setJumpContent(jumpBean.text, jumpBean.link);
            }
        }
    }

    /**
     * 图片
     */
    public void setTvImageUrl(String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            mIvIcon.setVisibility(View.GONE);
        } else {
            mIvIcon.setVisibility(View.VISIBLE);
            FrescoImageHelper.loadImage(imgPath, mIvIcon);
        }
    }

    /**
     * 设置查看
     *
     * @param strJumpText
     * @param strJumpLink
     */
    public void setJumpContent(String strJumpText, String strJumpLink) {
        this.strJumpText = strJumpText;
        this.strJumpLink = strJumpLink;
        if (!TextUtils.isEmpty(strJumpText)) {
            mTvContent.append(UIUtils.matcherSearchTitle(mContext.getResources().getColor(R.color.hhvideo_color_g), " " + strJumpText, strJumpText));
        }
    }
}
