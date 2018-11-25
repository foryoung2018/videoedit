package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.discovery.BannerListBean;
import com.wmlive.hhvideo.heihei.beans.immessage.DcMessage;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.MessageBannerViewHolder;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.MessageViewHolder;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.TimeUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.BadgeView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 2/8/2018.2:56 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final byte TYPE_BANNER = 1;
    public static final byte TYPE_MESSAGE = 2;
    private static final String REFRESH_UNREAD_COUNT = "unreadCount";
    private static final String REFRESH_IM_LATEST = "latest";
    private static final String REFRESH_FOLLOW = "follow";
    private static final String REFRESH_BANNER = "banner";
    private List<MessageDetail> dataList;
    private MessageClickListener onItemClickListener;
    private boolean hasBanner = false;
    private boolean showBanner = false;
    private List<Banner> bannerList;
    private boolean isImMessage = false;

    public MessageAdapter(List<MessageDetail> dataList) {
        this(dataList, false);
    }

    public MessageAdapter(List<MessageDetail> dataList, boolean hasBanner) {
        this.dataList = dataList;
        this.hasBanner = hasBanner;
    }

    public MessageAdapter(List<MessageDetail> dataList, boolean hasBanner, boolean isImMessage) {
        this.dataList = dataList;
        this.hasBanner = hasBanner;
        this.isImMessage = isImMessage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (hasBanner) {
            switch (viewType) {
                case TYPE_BANNER:
                    return new MessageBannerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_banner, parent, false));
                case TYPE_MESSAGE:
                    return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
                default:
                    return null;
            }
        } else {
            return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder) {
            MessageViewHolder messageHolder = (MessageViewHolder) holder;
            int dataPosition = position - (hasBanner ? 1 : 0);
            MessageDetail message = dataList.get(dataPosition);
            if (message == null) {
                return;
            }
            setFollowStatus(messageHolder.tvFollow, message);
            boolean show = MessageDetail.TYPE_CO_CREATE.equals(message.getMsg_type())
                    || MessageDetail.TYPE_LIKE.equals(message.getMsg_type())
                    || MessageDetail.TYPE_GIFT_V2.equals(message.getMsg_type())
                    || MessageDetail.TYPE_GIFT.equals(message.getMsg_type())
                    || MessageDetail.TYPE_COMMENT.equals(message.getMsg_type());

            messageHolder.ivTypeIcon.setVisibility(MessageDetail.TYPE_CO_CREATE.equals(message.getMsg_type())
                    || MessageDetail.TYPE_GIFT.equals(message.getMsg_type())
                    || MessageDetail.TYPE_GIFT_V2.equals(message.getMsg_type()) ? View.VISIBLE : View.GONE);
            int iconResId = 0;
            switch (message.getMsg_type()) {
                case MessageDetail.TYPE_CO_CREATE:
                    iconResId = R.drawable.icon_message_create;
                    break;
                case MessageDetail.TYPE_GIFT_V2:
                case MessageDetail.TYPE_GIFT:
                    iconResId = R.drawable.icon_profile_gift_48_48;
                    break;
                default:
                    break;
            }
            messageHolder.ivTypeIcon.setImageResource(iconResId);
            messageHolder.ivVideoThumb.setVisibility(show ? View.VISIBLE : View.GONE);

            messageHolder.tvAction.setVisibility(isImMessage ? View.GONE : View.VISIBLE);
            messageHolder.tvAction.setText(message.imTitle);

            String videoThumb = null;
            if (message.content != null && message.content.jump != null) {
                videoThumb = message.content.jump.icon;
            }
            GlideLoader.loadVideoThumb(videoThumb, messageHolder.ivVideoThumb,5);
            messageHolder.ivVideoThumb.setOnClickListener(new ClickListener(dataPosition, message));
            setMessageContent(messageHolder, message);
            messageHolder.rlMessageRoot.setOnClickListener(new ClickListener(dataPosition, message));
            messageHolder.ivFollow.setOnClickListener(new ClickListener(dataPosition, message));
            messageHolder.tvFollow.setOnClickListener(new ClickListener(dataPosition, message));
            messageHolder.ivAvatar.setOnClickListener(new ClickListener(dataPosition, message));
            messageHolder.tvName.setOnClickListener(new ClickListener(dataPosition, message));
        } else if (holder instanceof MessageBannerViewHolder) {
            showBanner((MessageBannerViewHolder) holder);
        }
    }

    private void showBanner(MessageBannerViewHolder bannerViewHolder) {
        bannerViewHolder.ivBanner.setVisibility(showBanner && !CollectionUtil.isEmpty(bannerList) ? View.VISIBLE : View.GONE);
        if (!CollectionUtil.isEmpty(bannerList)) {
            Banner banner = bannerList.get(0);
            if (banner != null) {
                String url = null;
                if (bannerViewHolder.llBannerRoot.getTag() != null) {
                    url = (String) bannerViewHolder.llBannerRoot.getTag();
                }
                bannerViewHolder.llBannerRoot.setTag(banner.cover);
                if (!TextUtils.isEmpty(banner.cover)) {
                    if (!banner.cover.equals(url)) {
                        GlideLoader.loadCornerImage(banner.cover, bannerViewHolder.ivBanner,
                                DeviceUtils.dip2px(bannerViewHolder.ivBanner.getContext(), 5));
                    }
                } else {
                    GlideLoader.loadCornerImage(banner.cover, bannerViewHolder.ivBanner,
                            DeviceUtils.dip2px(bannerViewHolder.ivBanner.getContext(), 5));
                }
                bannerViewHolder.ivBanner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DcRouter.linkTo(v.getContext(), banner.link);
                    }
                });
            }
        }
        bannerViewHolder.llEmpty.setVisibility(!CollectionUtil.isEmpty(getDataList()) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (CollectionUtil.isEmpty(payloads)) {
            onBindViewHolder(holder, position);
        } else {
            String type = (String) payloads.get(0);
            if (holder instanceof MessageViewHolder) {
                MessageViewHolder messageHolder = (MessageViewHolder) holder;
                MessageDetail message = dataList.get(hasBanner ? (position - 1) : position);
                if (REFRESH_FOLLOW.equals(type)) {
                    setFollowStatus(messageHolder.tvFollow, message);
                } else if (REFRESH_UNREAD_COUNT.equals(type)) {
                    setUnreadCount(messageHolder.badgeView, message.unreadCount);
                } else if (REFRESH_IM_LATEST.equals(type)) {
                    setMessageContent(messageHolder, message);
                }
            } else if (holder instanceof MessageBannerViewHolder) {
                if (REFRESH_BANNER.equals(type)) {
                    showBanner((MessageBannerViewHolder) holder);
                }
            }
        }
    }

    private void setMessageContent(MessageViewHolder messageHolder, MessageDetail message) {
        String name;
        String avatar = null;
        UserInfo userInfo = null;
        if (message.fromUserId == AccountUtil.getUserId()) {
            if (message.to_user != null) {
                name = message.to_user.getName();
                avatar = message.to_user.getCover_url();
                userInfo = message.to_user;
            } else {
                name = "未知用户";
            }
        } else {
            if (message.from_user != null) {
                name = message.from_user.getName();
                avatar = message.from_user.getCover_url();
                userInfo = message.from_user;
            } else {
                name = "未知用户";
            }
        }
        messageHolder.tvName.setText(name);
        GlideLoader.loadCircleImage(avatar, messageHolder.ivAvatar, userInfo != null && userInfo.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
        if (userInfo != null && userInfo.getVerify() != null && !TextUtils.isEmpty(userInfo.getVerify().icon)) {
            messageHolder.ivVerifyIcon.setVisibility(View.VISIBLE);
            GlideLoader.loadImage(userInfo.getVerify().icon, messageHolder.ivVerifyIcon);
        } else {
            messageHolder.ivVerifyIcon.setVisibility(View.GONE);
        }

        String desc;
        int colorId;
        if (DcMessage.TYPE_IM_CHAT.equals(message.imType)) {
            if (MessageDetail.TYPE_AUDIO_CONTENT.equals(message.getMsg_type())) {
                colorId = message.getStatus() == MessageDetail.IM_STATUS_PLAYED ? R.color.hh_color_d : R.color.hh_color_e;
            } else {
                //其他的类型在这里加else if
                colorId = R.color.hh_color_d;
            }
            if (message.getStatus() == MessageDetail.IM_STATUS_BAN) {
                messageHolder.ivStatus.setImageResource(R.drawable.hh_chat_icon_error);
                messageHolder.ivStatus.setVisibility(View.VISIBLE);
            } else if (message.getStatus() == MessageDetail.IM_STATUS_SENDFAIL) {
                messageHolder.ivStatus.setImageResource(R.drawable.hh_chat_icon_return);
                messageHolder.ivStatus.setVisibility(View.VISIBLE);
            } else {
                messageHolder.ivStatus.setVisibility(View.GONE);
            }
        } else {
            messageHolder.ivStatus.setVisibility(View.GONE);
            colorId = R.color.hh_color_d;
        }
        desc = !TextUtils.isEmpty(message.briefDesc) ? message.briefDesc : message.tips;
        messageHolder.tvDesc.setTextColor(messageHolder.tvDesc.getResources().getColor(colorId));
        messageHolder.ivImGift.setVisibility(DcMessage.TYPE_IM_CHAT.equals(message.imType)
                && MessageDetail.TYPE_IM_GIFT.equals(message.getMsg_type()) ? View.VISIBLE : View.GONE);
        messageHolder.tvDesc.setText(desc);
        String timeText = TimeUtil.getBellData(message.create_time * 1000);
        messageHolder.tvBottomTime.setText(timeText);
        messageHolder.tvRightTime.setText(timeText);
        messageHolder.tvBottomTime.setVisibility(DcMessage.TYPE_IM_CHAT.equals(message.imType) ? View.GONE : View.VISIBLE);
        messageHolder.tvRightTime.setVisibility(DcMessage.TYPE_IM_CHAT.equals(message.imType) ? View.VISIBLE : View.GONE);
        setUnreadCount(messageHolder.badgeView, message.unreadCount);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? (hasBanner ? 1 : 0) : (dataList.size() + (hasBanner ? 1 : 0));
    }

    @Override
    public int getItemViewType(int position) {
        return (hasBanner && position == 0) ? TYPE_BANNER : TYPE_MESSAGE;
    }

    public List<MessageDetail> getDataList() {
        return dataList;
    }

    public MessageDetail getLastItem() {
        return dataList.size() > 0 ? dataList.get(dataList.size() - 1) : null;
    }

    private void setUnreadCount(BadgeView badgeView, long unreadCount) {
        badgeView.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);
        badgeView.setText(unreadCount > 99 ? "···" : String.valueOf(unreadCount));
        badgeView.setBackground(10, Color.parseColor("#FF0000"));
    }

    private void setFollowStatus(View ivFollow, MessageDetail message) {
        if (message.from_user != null) {
            boolean show = MessageDetail.TYPE_FOLLOW.equals(message.getMsg_type());
            ivFollow.setVisibility(show && !message.from_user.isFollowed() ? View.VISIBLE : View.GONE);
        } else {
            ivFollow.setVisibility(View.GONE);
        }
    }


    private class ClickListener extends MyClickListener {
        private MessageDetail message;
        private int dataPosition;

        public ClickListener(int dataPosition, MessageDetail message) {
            this.dataPosition = dataPosition;
            this.message = message;
        }

        @Override
        protected void onMyClick(View view) {
            if (onItemClickListener != null) {
                switch (view.getId()) {
                    case R.id.rlMessageRoot:
                        if (DcMessage.TYPE_IM_CHAT.equals(message.imType)) {
                            toChat();
                        } else {
                            switch (message.getMsg_type()) {
                                case MessageDetail.TYPE_FOLLOW:
                                    if (message.from_user != null) {
                                        onItemClickListener.onAvatarClick(message.from_user.getId());
                                    }
                                    break;
                                case MessageDetail.TYPE_LIKE:
                                case MessageDetail.TYPE_CO_CREATE:
                                    if (message.content != null && message.content.jump != null) {
                                        onItemClickListener.onVideoClick(dataPosition, message.content.jump.link);
                                    }
                                    break;
                                case MessageDetail.TYPE_COMMENT:
                                    if (message.content != null && message.content.extr_param != null) {
                                        onItemClickListener.onCommentClick(dataPosition,
                                                message.fromUserId,
                                                message.fromUserName,
                                                message.content.extr_param.opus_id);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case R.id.tvName:
                    case R.id.ivAvatar:
                        if (DcMessage.TYPE_IM_CHAT.equals(message.imType)) {
                            toChat();
                        } else {
                            if (message.from_user != null) {
                                onItemClickListener.onAvatarClick(message.from_user.getId());
                            }
                        }
                        break;
                    case R.id.ivFollow:
                    case R.id.tvFollow:
                        if (message.from_user != null) {
                            onItemClickListener.onFollowClick(dataPosition, message.from_user.getId(), message.from_user.isFollowed());
                        }
                        break;
                    case R.id.ivVideoThumb:
                        if (message.content != null && message.content.jump != null) {
                            onItemClickListener.onVideoClick(dataPosition, message.content.jump.link);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        private void toChat() {
            if (message != null) {
                UserInfo userInfo;
                if (message.fromUserId == AccountUtil.getUserId()) {
                    //主动发起会话
                    userInfo = message.to_user;
                } else {
                    //被动接收会话
                    userInfo = message.from_user;
                }
                if (userInfo != null) {
                    onItemClickListener.onChatClick(dataPosition, userInfo);
                }
            }
        }
    }

    public void addData(int dataIndex, List<MessageDetail> list) {
        if (!CollectionUtil.isEmpty(list)) {
            dataList.addAll(dataIndex, list);
            notifyItemRangeInserted(dataIndex + (hasBanner ? 1 : 0), list.size());
        }
    }

    public void addData(boolean isRefresh, List<MessageDetail> list) {
        if (isRefresh) {
            dataList.clear();
        }
        if (!CollectionUtil.isEmpty(list)) {
            dataList.addAll(list);
        }
        if (isRefresh) {
            notifyDataSetChanged();
        } else {
            if (!CollectionUtil.isEmpty(list)) {
                notifyItemRangeInserted(dataList.size() - list.size(), list.size());
            }
        }
    }

    public void addData(MessageDetail messageDetail) {
        dataList.add(0, messageDetail);
        notifyItemInserted(hasBanner ? 1 : 0);
    }

    /**
     * 删除某个会话
     *
     * @param viewPosition
     */
    public void deleteConversation(int viewPosition) {
        int p = viewPosition - (hasBanner ? 1 : 0);
        if (p > -1 && p < dataList.size()) {
            MessageDetail messageDetail = dataList.remove(p);
            MessageManager.get().deleteConversation(AccountUtil.getUserId(), messageDetail.fromUserId, messageDetail.toUserId);
            notifyItemRemoved(viewPosition);
        }
    }

    /**
     * 刷新全部的未读数量
     */
    public void refreshAllItemMessageCount() {
        notifyItemRangeChanged(hasBanner ? 1 : 0, getDataList().size());
    }

    /**
     * 刷新某个会话
     *
     * @param dataPosition
     */
    public void refreshItemMessage(int dataPosition) {
        if (dataPosition > -1 && dataPosition < dataList.size()) {
            notifyItemChanged(dataPosition + (hasBanner ? 1 : 0), REFRESH_IM_LATEST);
            //最新的到第一位
            List<MessageDetail> detailList = getDataList();
            int dataSize = detailList.size();
            if (dataSize > 1 && dataPosition != 0) {
                MessageDetail first = detailList.get(dataPosition);
                if (detailList.get(0).create_time < detailList.get(dataPosition).create_time) {
                    for (int i = dataPosition; i > 0; i--) {
                        detailList.set(i, dataList.get(i - 1));
                    }
                    detailList.set(0, first);
                    notifyItemRangeChanged(1, dataPosition + 1);
                }
            }
        }
    }

    /**
     * 显示或者不显示Banner
     */
    public void refreshBanner(BannerListBean listBean) {
        bannerList = listBean != null ? listBean.banners : null;
        showBanner = !CollectionUtil.isEmpty(bannerList);
        notifyItemChanged(0, REFRESH_BANNER);
    }

    public void refreshEmpty() {
        notifyItemChanged(0, REFRESH_BANNER);
    }

    /**
     * 刷新关注状态
     *
     * @param position
     * @param isFollowed
     */
    public void refreshItemFollow(int position, boolean isFollowed) {
        if (position > -1 && position < dataList.size()) {
            if (dataList.get(position).from_user != null) {
                dataList.get(position).from_user.setFollowed(isFollowed);
                notifyItemChanged(position + (hasBanner ? 1 : 0), REFRESH_FOLLOW);
            }
        }
    }

    public void setMessageClickListener(MessageClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface MessageClickListener {
        void onAvatarClick(long userId);

        void onFollowClick(int dataPosition, long userId, boolean isFollowed);

        void onCommentClick(int dataPosition, long userId, String userName, long videoId);

        void onVideoClick(int dataPosition, String videoDeepLink);

        void onChatClick(int dataPosition, UserInfo userInfo);
    }
}
