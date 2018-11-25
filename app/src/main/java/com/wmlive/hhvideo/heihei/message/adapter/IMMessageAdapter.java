package com.wmlive.hhvideo.heihei.message.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.message.activity.IMMessageActivity;
import com.wmlive.hhvideo.heihei.message.viewholder.MeCommonGiftViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.MeCommonViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.MeNoHolderViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.MeSoundViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.NoViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.OtherCommonGiftViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.OtherCommonViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.OtherNoHolderViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.OtherSoundViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.OtherSysNotifyViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.OtherSyshintViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.OtherSystimeViewHolder;
import com.wmlive.hhvideo.utils.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/2/1.
 */

public class IMMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int IM_DETAILS_ME_NO_HOLDER_TYPE = -1;//自己不支持的信息类型
    public static final int IM_DETAILS_OTHER_NO_HOLDER_TYPE = -2;//对方发送的不支持的消息类型
    public static final int IM_DETAILS_ME_COMMON_TYPE = 1;//自己的普通文字信息
    public static final int IM_DETAILS_ME_SOUND_TYPE = 2;//自己的声音
    public static final int IM_DETAILS_ME_COMMON_GIFT_TYPE = 3;//自己的礼物文字信息
    public static final int IM_DETAILS_OTHER_COMMON_GIFT_TYPE = 5;//对方的礼物文字信息
    public static final int IM_DETAILS_OTHER_COMMON_TYPE = 6;//对方的普通文字文字信息
    public static final int IM_DETAILS_OTHER_SOUND_TYPE = 7;//对方的声音信息
    public static final int IM_DETAILS_OTHER_SYSNOTIFY_TYPE = 8;//系统通知信息
    public static final int IM_DETAILS_SYS_TIME_TYPE = 10;//系统时间
    public static final int IM_DETAILS_SYS_HINT_TYPE = 11;//拉黑信息

    public static final int IM_DETAILS_NO_HOLD_TYPE = 20;//无法确定发送者


    Context mContext;
    LayoutInflater mInflater;
    List<MessageDetail> mDatas;
    public long otherUserId;//对方的用户id
    private IMMessageActivity.MyIMResponsHandler mIMMsgHandler;

    //跳转到其他页面
    private IMMessageActivity.MyIMGotoOtherFragmentHandler mIMGotoHandler;

    public IMMessageAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public IMMessageAdapter(Context context, List<MessageDetail> datas, long otherId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        otherUserId = otherId;
    }

    public void setIMMsgHandler(IMMessageActivity.MyIMResponsHandler mHandler) {
        mIMMsgHandler = mHandler;
    }

    public void setIMGotoHanlder(IMMessageActivity.MyIMGotoOtherFragmentHandler gotoHanlder) {
        mIMGotoHandler = gotoHanlder;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
    }

    /**
     * 设置数据源-----前面
     *
     * @param datas
     */
    public void addHeaderRefreshDatas(List<MessageDetail> datas) {
        if (mDatas == null) {
            mDatas = new ArrayList<MessageDetail>();
        }
        mDatas.addAll(0, datas);

        notifyDataSetChanged();
    }

    /**
     * 设置数据源
     *
     * @param datas
     */
    public void setRefreshDatas(List<MessageDetail> datas) {
        if (mDatas == null) {
            mDatas = new ArrayList<MessageDetail>();
        }
        mDatas = datas;
        notifyDataSetChanged();
    }

    /**
     * 尾部增加数据集合
     *
     * @param datas
     */
    public void addFooterRefreshDatas(List<MessageDetail> datas) {
        if (mDatas == null) {
            mDatas = new ArrayList<MessageDetail>();
        }
        int positionStart = mDatas.size();
        int itemCount = datas.size();
        mDatas.addAll(datas);
        this.notifyItemRangeInserted(positionStart, itemCount);
    }

    /**
     * 尾部增加单一条数据
     *
     * @param data
     */
    public void addFooterItemRefreshData(MessageDetail data) {
        if (mDatas == null) {
            mDatas = new ArrayList<MessageDetail>();
        }
        mDatas.add(data);
        this.notifyItemInserted(mDatas.size());

    }

    /**
     * 尾部增加单一条数据
     *
     * @param datas
     */
    public void addFooterItemRefreshDatas(MessageDetail... datas) {
        if (mDatas == null) {
            mDatas = new ArrayList<MessageDetail>();
        }
        if (datas != null && datas.length > 0) {
            for (MessageDetail bean : datas) {
                mDatas.add(bean);
            }
        }

        this.notifyItemInserted(mDatas.size());

    }

    /**
     * 获取指定位置的Item 数据
     *
     * @param position
     * @return
     */
    public MessageDetail getItemDataByPosition(int position) {
        if (position > getItemCount()) {
            return null;
        }
        MessageDetail itemData = null;
        try {
            itemData = mDatas.get(position);
        } catch (Exception e) {

        }
        return itemData;
    }

    /**
     * 根据指定的msgId 获取数据在列表中的position
     *
     * @return
     */
    public int getItemPositionByMsgId(String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return -1;
        }
        int position = -1;
        try {
            int size = mDatas.size();
            for (int i = 0; i < size; i++) {
                MessageDetail itemData = mDatas.get(i);
                if (msgId.equals(itemData.msg_id)) {
                    position = i;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case IM_DETAILS_ME_NO_HOLDER_TYPE:
                //自己发送的不支持的布局
                View itemViewMeNoHolder = mInflater.inflate(R.layout.im_detail_me_no_hold_item_layout, parent, false);
                return new MeNoHolderViewHolder(itemViewMeNoHolder);
            case IM_DETAILS_OTHER_NO_HOLDER_TYPE:
                //接收到的不支持的布局，默认使用纯文本格式
                View itemViewOtherNoHolder = mInflater.inflate(R.layout.im_detail_other_no_holder_item_layout, parent, false);
                return new OtherNoHolderViewHolder(itemViewOtherNoHolder);
            case IM_DETAILS_ME_COMMON_TYPE:
                //自己的发送的文本信息
                View itemViewMeCommon = mInflater.inflate(R.layout.im_detail_me_common_item_layout, parent, false);
                return new MeCommonViewHolder(itemViewMeCommon, mContext, mIMMsgHandler);
            case IM_DETAILS_ME_SOUND_TYPE:
                //自己的声音信息
                View itemViewMeSound = mInflater.inflate(R.layout.im_detail_me_sound_item_layout, parent, false);
                return new MeSoundViewHolder(itemViewMeSound, mContext, mIMMsgHandler);
            case IM_DETAILS_ME_COMMON_GIFT_TYPE:
                //自己发送的礼物文案信息
                View itemViewCommonGift = mInflater.inflate(R.layout.im_detail_me_common_gift_item_layout, parent, false);
                return new MeCommonGiftViewHolder(itemViewCommonGift, mContext);
            case IM_DETAILS_OTHER_COMMON_GIFT_TYPE:
                //接收到的礼物文案信息
                View itemViewOtherCommonGift = mInflater.inflate(R.layout.im_detail_other_common_gift_item_layout, parent, false);
                return new OtherCommonGiftViewHolder(itemViewOtherCommonGift, mContext);
            case IM_DETAILS_OTHER_COMMON_TYPE:
                //接收到的文本信息
                View itemViewOtherCommon = mInflater.inflate(R.layout.im_detail_other_common_item_layout, parent, false);
                return new OtherCommonViewHolder(itemViewOtherCommon);
            case IM_DETAILS_OTHER_SOUND_TYPE:
                //接收到的语音信息
                View itemViewOtherSound = mInflater.inflate(R.layout.im_detail_other_sound_item_layout, parent, false);
                return new OtherSoundViewHolder(itemViewOtherSound, mIMMsgHandler);
            case IM_DETAILS_OTHER_SYSNOTIFY_TYPE:
                //接收到的系统通知
                View itemViewOtherSysNotify = mInflater.inflate(R.layout.im_detail_other_sysnotify_item_layout, parent, false);
                return new OtherSysNotifyViewHolder(itemViewOtherSysNotify, mContext, mIMGotoHandler);
            case IM_DETAILS_SYS_TIME_TYPE:
                //系统时间
                View itemViewOtherSysTime = mInflater.inflate(R.layout.im_detail_other_sys_time_layout, parent, false);
                return new OtherSystimeViewHolder(itemViewOtherSysTime);
            case IM_DETAILS_SYS_HINT_TYPE:
                //拉黑信息
                View itemViewOtherSyshint = mInflater.inflate(R.layout.im_detail_other_sys_hint_layout, parent, false);
                return new OtherSyshintViewHolder(itemViewOtherSyshint);
            case IM_DETAILS_NO_HOLD_TYPE:
                //无法确定发送者
                View itemViewNoHold = mInflater.inflate(R.layout.im_details_no_holder_layout, parent, false);
                return new NoViewHolder(itemViewNoHold);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) {
            return;
        }
        MessageDetail itemData = mDatas.get(position);

        if (holder instanceof MeCommonGiftViewHolder) {
            //自己发送的礼物文案
            MeCommonGiftViewHolder mMeCommonGiftViewHolder = (MeCommonGiftViewHolder) holder;
            mMeCommonGiftViewHolder.setCurrentItemData(itemData);
        } else if (holder instanceof MeCommonViewHolder) {
            //自己发送的文本信息
            MeCommonViewHolder mMeCommonViewHolder = (MeCommonViewHolder) holder;
            mMeCommonViewHolder.setCurrentItemDate(itemData);
        } else if (holder instanceof MeSoundViewHolder) {
            //自己发送的语音信息
            MeSoundViewHolder mMeSoundViewHolder = (MeSoundViewHolder) holder;
            mMeSoundViewHolder.setCurrentItemDate(itemData);
        } else if (holder instanceof OtherCommonGiftViewHolder) {
            //接收到礼物文案
            OtherCommonGiftViewHolder mOtherCommonGiftViewHolder = (OtherCommonGiftViewHolder) holder;
            mOtherCommonGiftViewHolder.setCurrentItemData(itemData);
        } else if (holder instanceof OtherCommonViewHolder) {
            //接收到的文案信息
            OtherCommonViewHolder mOtherCommonViewHolder = (OtherCommonViewHolder) holder;
            mOtherCommonViewHolder.setItemDate(itemData);
        } else if (holder instanceof OtherSoundViewHolder) {
            //接收到的语音信息
            OtherSoundViewHolder mOtherSoundViewHolder = (OtherSoundViewHolder) holder;
            mOtherSoundViewHolder.setItemDate(itemData);
        } else if (holder instanceof OtherSysNotifyViewHolder) {
            //接收到的系统通知
            OtherSysNotifyViewHolder mOtherSysNotifyViewHolder = (OtherSysNotifyViewHolder) holder;
            mOtherSysNotifyViewHolder.setItemDate(itemData);
        } else if (holder instanceof OtherSystimeViewHolder) {
            //系统时间及
            OtherSystimeViewHolder mOtherSystimeViewHolder = (OtherSystimeViewHolder) holder;
            mOtherSystimeViewHolder.setItemDate(itemData);
            mOtherSystimeViewHolder.setSysTimePosition(position);
        } else if (holder instanceof OtherSyshintViewHolder) {
            //接口回调信息
            OtherSyshintViewHolder mOtherSyshintViewHolder = (OtherSyshintViewHolder) holder;
            mOtherSyshintViewHolder.setItemDate(itemData);
        } else if (holder instanceof MeNoHolderViewHolder) {
            //自己发送的不支持的消息
            MeNoHolderViewHolder mMeNoHolderViewHolder = (MeNoHolderViewHolder) holder;
            mMeNoHolderViewHolder.setmTVContent(itemData.getTips());
        } else if (holder instanceof OtherNoHolderViewHolder) {
            //接收到不支持的类型
            OtherNoHolderViewHolder mOtherNoHolderViewHolder = (OtherNoHolderViewHolder) holder;
            mOtherNoHolderViewHolder.setmTvContent(itemData.getTips());
        } else if (holder instanceof NoViewHolder) {
            //唔支持
            NoViewHolder mNoViewHolder = (NoViewHolder) holder;
            mNoViewHolder.setItemDate(itemData);
        } else {
            KLog.e("IMRefreshAdapter", "the holder is not match !");
        }

    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        int iViewType = IM_DETAILS_OTHER_COMMON_TYPE;
        MessageDetail mbean = mDatas.get(position);
        if (mbean.fromUserId == AccountUtil.getUserId()) {
            //我发发送的信息
            switch (mbean.getMsg_type()) {
                case MessageDetail.TYPE_TEXT_CONTENT:
                    //普通文字
                    iViewType = IM_DETAILS_ME_COMMON_TYPE;
                    break;
                case MessageDetail.TYPE_AUDIO_CONTENT:
                    //音频
                    iViewType = IM_DETAILS_ME_SOUND_TYPE;
                    break;
                case MessageDetail.TYPE_IM_GIFT:
                    //礼物
                    iViewType = IM_DETAILS_ME_COMMON_GIFT_TYPE;
                    break;
                case MessageDetail.TYPE_SYSTEM:
                    //系统通知
                    iViewType = IM_DETAILS_ME_NO_HOLDER_TYPE;
                    break;
                case MessageDetail.TYPE_SYSTIME_CONTENT:
                    //系统时间
                    iViewType = IM_DETAILS_SYS_TIME_TYPE;
                    break;
                case MessageDetail.TYPE_SYSHINT_CONTENT:
                    //拉黑
                    iViewType = IM_DETAILS_SYS_HINT_TYPE;
                    break;
                case MessageDetail.TYPE_TIP_CONTENT:
                    //提示
                    iViewType = IM_DETAILS_SYS_HINT_TYPE;
                    break;
            }
        } else if (mbean.fromUserId == otherUserId) {
            //对方发送的消息
            switch (mbean.getMsg_type()) {
                case MessageDetail.TYPE_TEXT_CONTENT:
                    //普通文字
                    iViewType = IM_DETAILS_OTHER_COMMON_TYPE;
                    break;
                case MessageDetail.TYPE_AUDIO_CONTENT:
                    //音频
                    iViewType = IM_DETAILS_OTHER_SOUND_TYPE;
                    break;
                case MessageDetail.TYPE_IM_GIFT:
                    //礼物
                    iViewType = IM_DETAILS_OTHER_COMMON_GIFT_TYPE;
                    break;
                case MessageDetail.TYPE_SYSTEM:
                    //系统通知
                    iViewType = IM_DETAILS_OTHER_SYSNOTIFY_TYPE;
                    break;
                case MessageDetail.TYPE_SYSTIME_CONTENT:
                    //系统返回值
                    iViewType = IM_DETAILS_SYS_TIME_TYPE;
                    break;
                case MessageDetail.TYPE_SYSHINT_CONTENT:
                    //拉黑
                    iViewType = IM_DETAILS_SYS_HINT_TYPE;
                    break;
                case MessageDetail.TYPE_TIP_CONTENT:
                    //提示
                    iViewType = IM_DETAILS_SYS_HINT_TYPE;
                    break;
            }
        } else {
            //无规定的格式
            iViewType = IM_DETAILS_NO_HOLD_TYPE;
        }
        return iViewType;
    }

}
