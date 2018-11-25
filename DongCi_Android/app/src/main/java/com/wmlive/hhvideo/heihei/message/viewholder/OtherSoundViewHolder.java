package com.wmlive.hhvideo.heihei.message.viewholder;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.message.activity.IMMessageActivity;
import com.wmlive.hhvideo.utils.UIUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 接收的语音信息
 * Created by admin on 2017/3/27.
 */

public class OtherSoundViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.ll_im_detail_other_sound_content)
    public RelativeLayout mOtherSoundContent;
    @BindView(R.id.tv_im_detail_other_sound_time)
    public TextView mTvContent;
    @BindView(R.id.im_im_detail_other_sound_flag)
    public ImageView imOtherSoundFlag;
    @BindView(R.id.iv_im_detail_other_voice_anim)
    public ImageView ivOtherVoiceAnim;//播放动画
    @BindView(R.id.view_im_details_red_bg)
    public View mViewBgRed;

    private String strVoicePath;//语音地址
    private long strVoiceTime;//语音时间
    private AnimationDrawable mImageAnimRecord;//录音水纹动画
    MessageDetail sourceData;
    private static IMMessageActivity.MyIMResponsHandler mMyIMmsgHandler;

    public OtherSoundViewHolder(View itemView, final IMMessageActivity.MyIMResponsHandler handlerCallback) {
        super(itemView);
        mMyIMmsgHandler = handlerCallback;

        mOtherSoundContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sourceData.getStatus() != MessageDetail.IM_STATUS_PLAYED) {
                    mViewBgRed.setVisibility(View.INVISIBLE);
                    sourceData.setStatus(MessageDetail.IM_STATUS_PLAYED);
                    MessageManager.get().parseChatMessageList(sourceData);
//                    MessageManager.get().insertOrReplaceMessageInfo(sourceData);
                }
                Message handlerMsg = Message.obtain();
                Bundle mBundle = new Bundle();
                mBundle.putInt("position", getAdapterPosition());
                mBundle.putString("msg_id", sourceData.msg_id);
                handlerMsg.what = IMMessageActivity.HANDLER_WHAT_PLAY_RECORD_OTHER_FALG;
                handlerMsg.setData(mBundle);
                mMyIMmsgHandler.sendMessage(handlerMsg);
            }
        });
    }

    public void setItemDate(MessageDetail itemDate) {
        this.sourceData = itemDate;
        if (sourceData == null) {
            return;
        }
        MessageContent contentBean = itemDate.content;
        if (contentBean != null) {
            strVoiceTime = contentBean.length;
            strVoicePath = contentBean.audio;
        } else {
            strVoiceTime = 0;
            strVoicePath = "";
        }
        mTvContent.setText(strVoiceTime + "\"");
        setVoiceViewWidth();
        if (itemDate.getStatus() == MessageDetail.IM_STATUS_READ) {
            //此时为已读，显示红点，未播放
            mViewBgRed.setVisibility(View.VISIBLE);
        } else if (itemDate.getStatus() == MessageDetail.IM_STATUS_UNREAD) {
            //未读，更新为已读
            mViewBgRed.setVisibility(View.VISIBLE);
            sourceData.setStatus(MessageDetail.IM_STATUS_READ);
            MessageManager.get().parseChatMessageList(sourceData);
//            MessageManager.get().insertOrReplaceMessageInfo(sourceData);
        } else {
            mViewBgRed.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置宽度
     */
    private void setVoiceViewWidth() {
        ViewGroup.LayoutParams params = mOtherSoundContent.getLayoutParams();
        params.width = UIUtils.reckonViewWidthIM((int) strVoiceTime);
        mOtherSoundContent.setLayoutParams(params);
    }

    //录音水纹动画
    public void startRecordAndPlayerAnimation() {
        if (imOtherSoundFlag != null) {
            mImageAnimRecord = (AnimationDrawable) ivOtherVoiceAnim.getBackground();
            imOtherSoundFlag.setVisibility(View.GONE);
            ivOtherVoiceAnim.setVisibility(View.VISIBLE);
            //完整效果水纹
            mImageAnimRecord.start();
        }
    }

    /**
     * 停止录制和播放动画
     */
    public void stopRecordAndPlayerAnimation() {
        //完整效果水纹
        if (mImageAnimRecord != null) {
            mImageAnimRecord.stop();
            if (imOtherSoundFlag != null) {
                imOtherSoundFlag.setVisibility(View.VISIBLE);
                ivOtherVoiceAnim.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新指定的item
     */
    public void updateOtherSoundViewStatu() {
        if (sourceData.getStatus() != MessageDetail.IM_STATUS_PLAYED) {
            mViewBgRed.setVisibility(View.INVISIBLE);
            sourceData.setStatus(MessageDetail.IM_STATUS_PLAYED);
            MessageManager.get().parseChatMessageList(sourceData);
//            MessageManager.get().insertOrReplaceMessageInfo(sourceData);
        }
    }
}
