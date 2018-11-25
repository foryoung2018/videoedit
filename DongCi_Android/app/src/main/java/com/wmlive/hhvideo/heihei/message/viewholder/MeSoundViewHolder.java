package com.wmlive.hhvideo.heihei.message.viewholder;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.common.ossutils.UpLoadInterface;
import com.wmlive.hhvideo.common.ossutils.UploadALiResultBean;
import com.wmlive.hhvideo.heihei.beans.immessage.DcMessage;
import com.wmlive.hhvideo.heihei.beans.immessage.IMMessageResponse;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.message.activity.IMMessageActivity;
import com.wmlive.hhvideo.heihei.message.utils.IMNetUtils;
import com.wmlive.hhvideo.heihei.message.utils.IMUtils;
import com.wmlive.hhvideo.heihei.personal.util.OssTokenAndUploadUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.UIUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import java.io.File;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.common.GlobalParams.ErrorCode.TYPE_IM_SNED_BLOCK;
import static com.wmlive.hhvideo.common.GlobalParams.ErrorCode.TYPE_NET_ERROR;

/**
 * 自己发送的语音信息
 * Created by admin on 2017/3/27.
 */

public class MeSoundViewHolder extends BaseRecyclerViewHolder implements UpLoadInterface, View.OnClickListener {

    @BindView(R.id.tv_im_detail_me_sound_time)
    public TextView mTvContent;
    @BindView(R.id.iv_im_detail_me_voice_anim)
    public ImageView ivMeVoiceAnim; //播放动画
    @BindView(R.id.im_im_detail_me_sound_flag)
    public ImageView imMeSoundFlag;
    @BindView(R.id.ll_im_detail_me_sound_content)
    public RelativeLayout llMeSoundContent;
    @BindView(R.id.iv_im_detail_me_loading)
    public ImageView mIvLoading;
    @BindView(R.id.iv_im_detail_me_error)
    public ImageView mIvError;
    @BindView(R.id.iv_im_detail_me_retry)
    public ImageView mIvRetry;
    public Animation loadingAnim;

    private String strVoiceLocalPath;//本地路径
    private int voiceTime;//语音时间

    private AnimationDrawable mImageAnimRecord;//录音水纹动画
    private static IMMessageActivity.MyIMResponsHandler mMyIMmsgHandler;
    private MessageDetail mdata;

    private OssTokenAndUploadUtils mOssTokenAndUploadUtils;//oss 工具类

    public MeSoundViewHolder(View itemView, Context context, final IMMessageActivity.MyIMResponsHandler handlerCallback) {
        super(itemView);
        mMyIMmsgHandler = handlerCallback;
        mOssTokenAndUploadUtils = new OssTokenAndUploadUtils(context, this);
        mOssTokenAndUploadUtils.setShowWatingDialog(false);
        loadingAnim = AnimationUtils.loadAnimation(context, R.anim.loading_repeat);
        llMeSoundContent.setOnClickListener(this);
        mIvRetry.setOnClickListener(this);
        mIvError.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_im_detail_me_sound_content:
                // 播放录音
                Message handlerMsg = Message.obtain();
                Bundle mBundle = new Bundle();
                mBundle.putInt("position", getAdapterPosition());
                handlerMsg.what = IMMessageActivity.HANDLER_WHAT_PLAY_RECORD_ME_FALG;
                handlerMsg.setData(mBundle);
                mMyIMmsgHandler.sendMessage(handlerMsg);
                break;
            case R.id.iv_im_detail_me_retry:
            case R.id.iv_im_detail_me_error:
                // 重试发送
                if (loadingAnim != null) {
                    mIvLoading.startAnimation(loadingAnim);
                }
                mIvLoading.setVisibility(View.VISIBLE);
                mIvError.setVisibility(View.GONE);
                mIvRetry.setVisibility(View.GONE);
                //开启发送
                if (mdata != null) {
                    IMNetUtils.get().sendMMessageAudio(new IMMsgResponse(mdata), mdata.getToUserId(), mdata.getLocal_msg_id(), mdata.getMessageContent());
                }
                break;
        }
    }

    //===========================播放=================================
    //录音水纹动画
    public void startRecordAndPlayerAnimation() {
        if (imMeSoundFlag != null) {
            mImageAnimRecord = (AnimationDrawable) ivMeVoiceAnim.getBackground();
            imMeSoundFlag.setVisibility(View.GONE);
            ivMeVoiceAnim.setVisibility(View.VISIBLE);
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
            if (imMeSoundFlag != null) {
                imMeSoundFlag.setVisibility(View.VISIBLE);
                ivMeVoiceAnim.setVisibility(View.GONE);
            }
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
            voiceTime = contentBean.length;
            strVoiceLocalPath = contentBean.local_path;
        } else {
            voiceTime = 0;
            strVoiceLocalPath = "";
        }
        mTvContent.setText(voiceTime + "\"");
        setVoiceViewWidth();
        if (itemDate.getStatus() == MessageDetail.IM_STATUS_SENDING) {
            if (loadingAnim != null) {
                mIvLoading.startAnimation(loadingAnim);
            }
            mIvLoading.setVisibility(View.VISIBLE);
            mIvError.setVisibility(View.GONE);
            mIvRetry.setVisibility(View.GONE);
            //开启发送
            requsetSendIMMsgContent();
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
        } else {
            mIvLoading.setVisibility(View.GONE);
            mIvError.setVisibility(View.GONE);
            mIvRetry.setVisibility(View.GONE);
        }
    }

    /**
     * 设置宽度
     */
    private void setVoiceViewWidth() {
        ViewGroup.LayoutParams params = llMeSoundContent.getLayoutParams();
        params.width = UIUtils.reckonViewWidthIM(voiceTime);
        llMeSoundContent.setLayoutParams(params);
    }

    /**
     * 检测本地音频文件是否存在
     *
     * @return
     */
    public static boolean checkLocalSoundFile(String path, boolean showMsg) {
        Message handlerMsg = Message.obtain();
        Bundle mBundle = new Bundle();
        handlerMsg.what = IMMessageActivity.HANDLER_WHAT_SHOW_TOAST_FLAG;
        if (TextUtils.isEmpty(path)) {
            if (showMsg) {
                mBundle.putString("msg", "本地文件丢失");
                handlerMsg.setData(mBundle);
                mMyIMmsgHandler.sendMessage(handlerMsg);
            }
            return false;
        } else {
            try {
                File mFile = new File(path);
                if (mFile != null && !mFile.exists()) {
                    if (showMsg) {
                        mBundle.putString("msg", "本地文件丢失");
                        handlerMsg.setData(mBundle);
                        mMyIMmsgHandler.sendMessage(handlerMsg);
                    }
                    return false;
                }
            } catch (Exception e) {
                if (showMsg) {
                    mBundle.putString("msg", "本地文件丢失");
                    handlerMsg.setData(mBundle);
                    mMyIMmsgHandler.sendMessage(handlerMsg);
                }
                return false;
            }
        }
        return true;
    }

    //====================http请求======================

    @Override
    public void onSuccessUpload(UploadALiResultBean obj) {
        KLog.d("xxxx", "Upload voice onSuccessUpload ");
        if (mdata.content != null) {
            mdata.content.audio = obj.getmOssTokenResult().getFileInfo().getPath();
            mdata.content.sign = obj.getmOssTokenResult().getFileInfo().getSign();
            mdata.content.text = "";
            mdata.content.local_path = strVoiceLocalPath;

            IMNetUtils.get().sendMMessageAudio(new IMMsgResponse(mdata), mdata.getToUserId(), mdata.getLocal_msg_id(), mdata.getMessageContent());
        }
    }

    @Override
    public void onFailsUpload(UploadALiResultBean obj) {
        if (obj.getmOssTokenResult() != null) {
            KLog.d("xxxx", "Upload voice onFailsUpload " + obj.getmOssTokenResult().getError_msg());
        }
        mIvLoading.setVisibility(View.GONE);
        mIvError.setVisibility(View.GONE);
        mIvRetry.setVisibility(View.VISIBLE);
        //更新本地数据
        mdata.setStatus(MessageDetail.IM_STATUS_SENDFAIL);
        MessageManager.get().parseChatMessageList(mdata);
//        MessageManager.get().updateMessageInfo(mdata);
        Message handlerMsg = Message.obtain();
        Bundle mBundle = new Bundle();
        handlerMsg.what = IMMessageActivity.HANDLER_WHAT_SHOW_TOAST_FLAG;
        mBundle.putString("msg", "文件发送失败");
        mMyIMmsgHandler.sendMessage(handlerMsg);
    }

    @Override
    public void onExceptionUpload(UploadALiResultBean obj) {
        if (obj.getmOssTokenResult() != null) {
            KLog.d("xxxx", "Upload voice onExceptionUpload " + obj.getmOssTokenResult().getError_msg());
        }
        mIvLoading.setVisibility(View.GONE);
        mIvError.setVisibility(View.GONE);
        mIvRetry.setVisibility(View.VISIBLE);
        //更新数据库
        mdata.setStatus(MessageDetail.IM_STATUS_SENDFAIL);
        MessageManager.get().parseChatMessageList(mdata);
//        MessageManager.get().updateMessageInfo(mdata);
        Message handlerMsg = Message.obtain();
        Bundle mBundle = new Bundle();
        handlerMsg.what = IMMessageActivity.HANDLER_WHAT_SHOW_TOAST_FLAG;
        mBundle.putString("msg", "文件发送失败");
        mMyIMmsgHandler.sendMessage(handlerMsg);
    }

    @Override
    public void onProgress(OSSRequest request, long currentSize, long totalSize) {

    }

    /**
     * 发送数据
     */
    private void requsetSendIMMsgContent() {
        if (checkLocalSoundFile(strVoiceLocalPath, true)) {
            mdata.setStatus(MessageDetail.IM_STATUS_READ);
            MessageManager.get().parseChatMessageList(mdata);
//            MessageManager.get().updateMessageInfo(mdata);
            KLog.d("xxxx", "start upload voice file to aliyun " + strVoiceLocalPath);
            mOssTokenAndUploadUtils.setStrUploadPath(strVoiceLocalPath);
            mOssTokenAndUploadUtils.getOssTokenUploadByNetwork("mp3", "message");
        }
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
                KLog.e("im_audio", e.getMessage());
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
                //发送失败
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
            MessageManager.get().parseChatMessageList(mdata);
//            MessageManager.get().updateMessageInfo(sourceData);
        }

    }


}
