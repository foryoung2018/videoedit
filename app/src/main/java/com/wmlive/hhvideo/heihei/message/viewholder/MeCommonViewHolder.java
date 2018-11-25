package com.wmlive.hhvideo.heihei.message.viewholder;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.immessage.DcMessage;
import com.wmlive.hhvideo.heihei.beans.immessage.IMMessageResponse;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.message.activity.IMMessageActivity;
import com.wmlive.hhvideo.heihei.message.utils.IMNetUtils;
import com.wmlive.hhvideo.heihei.message.utils.IMUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.common.GlobalParams.ErrorCode.TYPE_IM_SNED_BLOCK;
import static com.wmlive.hhvideo.common.GlobalParams.ErrorCode.TYPE_NET_ERROR;


/**
 * 自己的纯文本信息
 * Created by admin on 2017/3/27.
 */

public class MeCommonViewHolder extends BaseRecyclerViewHolder implements View.OnClickListener {

    @BindView(R.id.tv_im_detail_me_content)
    public TextView mTvContent;
    @BindView(R.id.iv_im_detail_me_loading)
    public ImageView mIvLoading;
    @BindView(R.id.iv_im_detail_me_error)
    public ImageView mIvError;
    @BindView(R.id.iv_im_detail_me_retry)
    public ImageView mIvRetry;

    private Animation loadingAnim;
    private static IMMessageActivity.MyIMResponsHandler mMyIMmsgHandler;
    private MessageDetail mdata;
    private String strContextText = "";

    //    private String strIMDetailsTimeMsgId = "";//本地时间id
    public MeCommonViewHolder(View itemView, Context context, IMMessageActivity.MyIMResponsHandler handlerCallback) {
        super(itemView);
        mMyIMmsgHandler = handlerCallback;
        loadingAnim = AnimationUtils.loadAnimation(context, R.anim.loading_repeat);
        mIvRetry.setOnClickListener(this);
        mIvError.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_im_detail_me_retry:
            case R.id.iv_im_detail_me_error:
                if (loadingAnim != null) {
                    mIvLoading.startAnimation(loadingAnim);
                }
                mIvLoading.setVisibility(View.VISIBLE);
                mIvError.setVisibility(View.GONE);
                mIvRetry.setVisibility(View.GONE);
                //开启发送
                requsetSendIMMsgContent(mdata);
                break;
        }
    }

    /**
     * 设置数据
     *
     * @param itemDate
     */
    public void setCurrentItemDate(MessageDetail itemDate) {
        if (itemDate == null) {
            return;
        }
        mdata = itemDate;
        MessageContent contentBean = itemDate.content;
        if (contentBean != null) {
            strContextText = itemDate.content.text;
        }
        mTvContent.setText(strContextText);
        if (itemDate.getStatus() == MessageDetail.IM_STATUS_SENDING) {
            if (loadingAnim != null) {
                mIvLoading.startAnimation(loadingAnim);
            }
//            strIMDetailsTimeMsgId = itemDate.getLocalMsgId();
            mIvLoading.setVisibility(View.VISIBLE);
            mIvError.setVisibility(View.GONE);
            mIvRetry.setVisibility(View.GONE);
            //开启发送
            requsetSendIMMsgContent(itemDate);
        } else if (itemDate.getStatus() == MessageDetail.IM_STATUS_BAN) {
            //拉黑，无法发送
            mIvLoading.setVisibility(View.GONE);
            mIvError.setVisibility(View.VISIBLE);
            mIvRetry.setVisibility(View.GONE);
        } else if (itemDate.getStatus() == MessageDetail.IM_STATUS_SENDFAIL) {
            //发送失败
            mIvLoading.setVisibility(View.GONE);
            mIvError.setVisibility(View.GONE);
            mIvRetry.setVisibility(View.VISIBLE);
//            strIMDetailsTimeMsgId = itemDate.getLocalMsgId();
        } else {
            mIvLoading.setVisibility(View.GONE);
            mIvError.setVisibility(View.GONE);
            mIvRetry.setVisibility(View.GONE);
        }
    }

    /**
     * 发送数据
     *
     * @param itemDate
     */
    private void requsetSendIMMsgContent(MessageDetail itemDate) {
//        new IMMessagePresenter(this).sendIMMessage(itemDate.getToUserId(), "text", itemDate.msg_id, itemDate.messageContent);
        IMNetUtils.get().sendMMessageText(new IMMsgResponse(itemDate), itemDate.getToUserId(), itemDate.getLocal_msg_id(), itemDate.getMessageContent());
    }

    public class IMMsgResponse implements IMNetUtils.IMResponse {

        MessageDetail sourceData;//元数据

        public IMMsgResponse(MessageDetail itemDate) {
            sourceData = itemDate;
        }

        @Override
        public void onSendIMMessage(IMMessageResponse response) {

            Message handlerMsg = Message.obtain();
            try {
                try {
                    if (mIvLoading != null) {
                        mIvLoading.clearAnimation();
                    }
                } catch (Exception e) {

                }
                mIvLoading.setVisibility(View.GONE);
                mIvError.setVisibility(View.GONE);
                mIvRetry.setVisibility(View.GONE);

                //发送成功，更新本地数据库
                //IM中消息体部分
                long createTime = 0L;//创建时间
                String strMsgId = "";//服务器返回的消息Id
                //IM中用户信息部分
                if (response != null && response.message != null && response.message.message != null) {
                    strMsgId = response.message.message.getMsg_id();
                    createTime = response.message.message.create_time;
                    //更新本地信息
                    sourceData.setMsg_id(strMsgId);
                    sourceData.setCreate_time(createTime);
                    sourceData.setStatus(MessageDetail.IM_STATUS_SENT);
                    handlerMsg.what = IMMessageActivity.HANDLER_WHAT_RESPONSE_HINT_FLAG;
                    mMyIMmsgHandler.sendMessage(handlerMsg);
                }
                MessageManager.get().parseChatMessageList(sourceData);
//                MessageManager.get().updateMessageInfo(sourceData);
            } catch (Exception e) {
                KLog.e("im_text", e.getMessage());
            }
        }

        @Override
        public void onRequestDataError(int requestCode, int serverCode, String message, IMMessageResponse response) {
            Message handlerMsg = Message.obtain();
            Bundle mBundle = new Bundle();
            KLog.i("发送失败 serverCode" + serverCode + " message " + message);
            mIvLoading.setVisibility(View.GONE);
            mIvError.setVisibility(View.GONE);
            mIvRetry.setVisibility(View.GONE);
            String strLocalMsgId = IMUtils.getLocalMsgId();
            sourceData.setLocal_msg_id(strLocalMsgId);
            if (serverCode == TYPE_IM_SNED_BLOCK) {
                mIvError.setVisibility(View.VISIBLE);
                //将拉黑信息加入数据库,新数据
                if (response != null && response.message != null && response.message.message != null) {
                    MessageDetail imBlockMessage = response.message.message;
                    imBlockMessage.setStatus(MessageDetail.IM_STATUS_READ);
                    imBlockMessage.belongUserId = AccountUtil.getUserId();
                    imBlockMessage.setImType(DcMessage.TYPE_IM_CHAT);
                    MessageManager.get().parseChatMessageList(imBlockMessage);
//                    MessageManager.get().insertOrReplaceMessageInfo(imBlockMessage);
                    if (MessageDetail.TYPE_TIP_CONTENT.equals(imBlockMessage.getMsg_type())) {
                        handlerMsg.what = IMMessageActivity.HANDLER_WHAT_SHOW_TIP_FLAG;
                        mMyIMmsgHandler.sendMessage(handlerMsg);
                    }
                }
                // 消息无法发送
                sourceData.setStatus(MessageDetail.IM_STATUS_BAN);
            } else if (serverCode == TYPE_NET_ERROR) {
                //网络问题发送失败，发送重试
                mIvRetry.setVisibility(View.VISIBLE);
                handlerMsg.what = IMMessageActivity.HANDLER_WHAT_SHOW_TOAST_FLAG;
                mBundle.putString("msg", message);
                //发送失败，可以重新发送
                sourceData.setStatus(MessageDetail.IM_STATUS_SENDFAIL);
            } else {
//            } else if (serverCode == TYPE_IM_SEND_ERROR) {
                // 发送错误，toast提示
                mIvError.setVisibility(View.VISIBLE);
                handlerMsg.what = IMMessageActivity.HANDLER_WHAT_SHOW_TOAST_FLAG;
                mBundle.putString("msg", message);
                handlerMsg.setData(mBundle);
                mMyIMmsgHandler.sendMessage(handlerMsg);
                sourceData.setStatus(MessageDetail.IM_STATUS_BAN);
            }
            MessageManager.get().parseChatMessageList(sourceData);
//            MessageManager.get().updateMessageInfo(sourceData);
        }

    }
}
