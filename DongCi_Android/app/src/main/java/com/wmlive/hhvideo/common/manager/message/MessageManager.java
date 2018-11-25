package com.wmlive.hhvideo.common.manager.message;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.hhvideo.common.manager.greendao.GreenDaoManager;
import com.wmlive.hhvideo.heihei.beans.immessage.DcMessage;
import com.wmlive.hhvideo.heihei.db.Conversation;
import com.wmlive.hhvideo.heihei.db.ConversationDao;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.db.MessageDetailDao;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.message.MessageOperator;
import com.wmlive.hhvideo.heihei.message.RingtoneController;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.UIUtils;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 2/5/2018.10:25 AM
 *
 * @author lsq
 * @describe 系统消息和IM消息的管理类
 */

public class MessageManager {

    private MessageOperator systemOperator;

    private static final class Holder {
        static final MessageManager INSTANCE = new MessageManager();
    }

    public static MessageManager get() {
        return Holder.INSTANCE;
    }

    public MessageManager() {
        systemOperator = new MessageOperator(new PriorityBlockingQueue<MessageDetail>(10));
    }

    public void parseMessage(String messageJson) {
        if (!TextUtils.isEmpty(messageJson)) {
            TaskManager.get().executeTask(new BaseTask() {
                @Override
                public void run() {
                    parse(messageJson, true);
                }
            });
        }
    }

    private void parse(String messageJson, boolean isNewMessage) {
        if (messageJson.startsWith("[") && messageJson.endsWith("]")) {
            KLog.i("=====需要处理的JsonArray消息");
            parseMessageList(JsonUtils.parseArray(messageJson, DcMessage.class), isNewMessage);
        } else if (messageJson.startsWith("{") && messageJson.endsWith("}")) {
            KLog.i("=====需要处理的JsonObject消息");
            DcMessage dcMessage = JsonUtils.parseObject(messageJson, DcMessage.class);
            if (dcMessage != null && dcMessage.message != null) {
                parseMessageList(new ArrayList<DcMessage>(1) {{
                    add(dcMessage);
                }}, isNewMessage);
            }
        } else {
            KLog.e("=====不需要处理的消息:\n" + messageJson);
        }
    }

    /**
     * IM会话页面处理一条自己本地发送的消息
     *
     * @param messageDetail
     */
    public long parseChatMessageList(MessageDetail messageDetail) {
        DcMessage dcMsg = new DcMessage();
        dcMsg.message = messageDetail;
        dcMsg.classify = DcMessage.TYPE_IM_CHAT;
        dcMsg.comments = DcMessage.TYPE_IM_CHAT;
        List<Long> idList = parseMessageList(new ArrayList<DcMessage>(1) {{
            add(dcMsg);
        }}, true, false);
        if (idList != null && idList.size() > 0) {
            return idList.get(0);
        } else {
            return -1L;
        }
    }

    /**
     * 处理从服务器接受到的消息
     *
     * @param messageList
     */

    public void parseMessageList(List<DcMessage> messageList, boolean isNewMessage) {
        parseMessageList(messageList, false, isNewMessage);
    }

    @SuppressLint("NewApi")
    public synchronized List<Long> parseMessageList(List<DcMessage> messageList, boolean isSelfSend, boolean isNewMessage) {
        List<Long> idList = new ArrayList<>(2);
        if (!CollectionUtil.isEmpty(messageList)) {
            boolean hasSystemMessage = false;
            boolean hasImMessage = false;
            boolean hasImChatMessage = false;
            List<MessageDetail> imMessageList = new ArrayList<>(4);
            List<Conversation> conversationList = null;
            Set<Conversation> updateConversationSet = null;
            for (DcMessage dcMessage : messageList) {
                if (dcMessage != null && dcMessage.message != null) {
                    switch (dcMessage.classify) {
                        //私信消息
                        case DcMessage.TYPE_IM_CHAT:
                            addMessage(dcMessage, imMessageList);
                            hasImChatMessage = true;
                            if (conversationList == null) {
                                conversationList = queryAllConversation(AccountUtil.getUserId(), System.currentTimeMillis());
                            }
                            if (updateConversationSet == null) {
                                updateConversationSet = new HashSet<>(2);
                            }
                            addConversation(conversationList, updateConversationSet, dcMessage.message);
                            break;
                        //系统消息
                        case DcMessage.TYPE_IM:
                            addMessage(dcMessage, imMessageList);
                            hasImMessage = true;
                            break;
                        //系统弹窗
                        case DcMessage.TYPE_ACTION:
                            if (systemOperator != null) {
                                systemOperator.addMessage(dcMessage.message);
                                hasSystemMessage = true;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            if (hasImMessage || hasImChatMessage) {
                if (!CollectionUtil.isEmpty(imMessageList)) {
                    GreenDaoManager.get().getMessageDetailDao().insertOrReplaceInTx(imMessageList);
                    List<Long> imKeyList = new ArrayList<>(2);
                    for (MessageDetail messageDetail : imMessageList) {
                        KLog.i("=====插入数据的key:" + (messageDetail == null ? "null" : messageDetail.dcImId));
                        if (messageDetail != null && messageDetail.dcImId != null) {
                            if (DcMessage.TYPE_IM.equals(messageDetail.imType)) {
                                imKeyList.add(messageDetail.dcImId);
                            } else if (DcMessage.TYPE_IM_CHAT.equals(messageDetail.imType)) {
                                idList.add(messageDetail.dcImId);
                            }
                        }
                    }
                    if (hasImMessage) {
                        EventHelper.post(GlobalParams.EventType.TYPE_IM_SYSTEM_MSG, imKeyList);
                    }
                    if (hasImChatMessage) {
                        Set<Long> imChatConIdSet = new HashSet<>(2);
                        for (Conversation conversation : updateConversationSet) {
                            if (conversation != null) {
                                imChatConIdSet.add(conversation.dcConId);
                            }
                        }
                        KLog.i("====当前需要更新的会话：" + imChatConIdSet.size());
                        EventHelper.post(GlobalParams.EventType.TYPE_IM_CHAT_MSG, imChatConIdSet);
                        if (!isSelfSend) {
                            EventHelper.post(GlobalParams.EventType.TYPE_IM_CHAT_NOT_SELF_MSG, idList);
                        }
                        if (isNewMessage) {
                            if (DCApplication.getDCApp().isInBackground()) {
                                String messageText = DCApplication.getDCApp().getString(R.string.received_message);
                                UIUtils.setNotification(messageText, messageText, "");
                            } else {
                                RingtoneController.playIMAcceptMusic();
                            }
                        }
                    }
                }
            }
            if (hasSystemMessage) {
                EventHelper.post(GlobalParams.EventType.TYPE_ALERT_SYSTEM_MSG);
            }
        } else {
            KLog.i("没有消息需要处理");
        }
        return idList;
    }

    public void addMessage(DcMessage dcMessage, List<MessageDetail> imList) {
        dcMessage.message.belongUserId = AccountUtil.getUserId();
        dcMessage.message.imType = dcMessage.classify;
        if (dcMessage.message.content != null) {
            dcMessage.message.imTitle = dcMessage.message.content.title;
            dcMessage.message.briefDesc = dcMessage.message.content.desc;
        }
        if (dcMessage.message.from_user != null) {
            dcMessage.message.fromUserId = dcMessage.message.from_user.getId();
            dcMessage.message.fromUserName = dcMessage.message.from_user.getName();
        }
        if (dcMessage.message.to_user != null) {
            dcMessage.message.toUserId = dcMessage.message.to_user.getId();
        }
        imList.add(dcMessage.message);
    }

    private static Conversation addConversation(List<Conversation> conversationList, Set<Conversation> updateConversationSet, MessageDetail message) {
        Conversation conversation = null;
        if (message != null) {
            boolean exist = false;
            for (Conversation con : conversationList) {
                if (con != null) {
                    if ((con.fromUserId == message.fromUserId && con.toUserId == message.toUserId)
                            || (con.fromUserId == message.toUserId && con.toUserId == message.fromUserId)) {
                        exist = true;
                        updateConversationSet.add(con);
                        KLog.i("======会话存在：" + con);
                        break;
                    }
                }
            }
            if (!exist) {
                conversation = new Conversation(message.fromUserId, message.toUserId);
                conversation.createTime = message.create_time;
                conversation.belongUserId = AccountUtil.getUserId();
                conversationList.add(conversation);
                GreenDaoManager.get().getConversationDao().insert(conversation);
                KLog.i("======插入一个新会话：" + conversation);
                updateConversationSet.add(conversation);
            }
        }
        return conversation;
    }

    /**
     * 查询所有会话
     *
     * @param belongUserId
     * @param startTimestamp
     * @return
     */
    public List<Conversation> queryAllConversation(long belongUserId, long startTimestamp) {
        QueryBuilder<Conversation> qb = GreenDaoManager.get().getConversationDao().queryBuilder();
        qb.where(ConversationDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(ConversationDao.Properties.CreateTime.lt(startTimestamp));
        qb.orderDesc(ConversationDao.Properties.CreateTime);
        return qb.list();
    }

    /**
     * 查询某个会话最后一条消息
     *
     * @param belongUserId
     * @param fromUserId
     * @param toUserId
     * @param startTimestamp
     * @return
     */
    public MessageDetail queryLatestConversationMessage(long belongUserId, long fromUserId, long toUserId, long startTimestamp) {
        List<MessageDetail> list = queryConversationMessage(belongUserId, fromUserId, toUserId, 1, startTimestamp);
        return !CollectionUtil.isEmpty(list) ? list.get(0) : null;
    }

    /**
     * 查询某个会话的所有消息
     *
     * @param belongUserId
     * @param fromUserId
     * @param toUserId
     * @param pageSize
     * @param startTimestamp
     * @return
     */
    public List<MessageDetail> queryConversationMessage(long belongUserId, long fromUserId, long toUserId, int pageSize, long startTimestamp) {
        KLog.i("====查询时间：startTimestamp：" + startTimestamp);
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(MessageDetailDao.Properties.ImType.eq(DcMessage.TYPE_IM_CHAT));
        qb.whereOr(qb.and(MessageDetailDao.Properties.FromUserId.eq(fromUserId), MessageDetailDao.Properties.ToUserId.eq(toUserId)),
                qb.and(MessageDetailDao.Properties.FromUserId.eq(toUserId), MessageDetailDao.Properties.ToUserId.eq(fromUserId)));
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        qb.orderDesc(MessageDetailDao.Properties.Create_time);
        if (pageSize > 0) {
            qb.limit(pageSize);
        }
        return qb.list();
    }

    /**
     * 查询某个会话未读消息数量
     *
     * @param belongUserId
     * @param fromUserId
     * @param toUserId
     * @param startTimestamp
     * @return
     */
    public long queryConversationUnreadCount(long belongUserId, long fromUserId, long toUserId, long startTimestamp) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(MessageDetailDao.Properties.ImType.eq(DcMessage.TYPE_IM_CHAT));
        qb.whereOr(qb.and(MessageDetailDao.Properties.FromUserId.eq(fromUserId), MessageDetailDao.Properties.ToUserId.eq(toUserId)),
                qb.and(MessageDetailDao.Properties.FromUserId.eq(toUserId), MessageDetailDao.Properties.ToUserId.eq(fromUserId)));
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        qb.where(MessageDetailDao.Properties.Status.eq(MessageDetail.IM_STATUS_UNREAD));
        return qb.count();
    }

    /**
     * 查询所有会话的未读消息数量
     *
     * @param belongUserId
     * @param startTimestamp
     * @return
     */
    public long queryAllConversationUnreadCount(long belongUserId, long startTimestamp) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(MessageDetailDao.Properties.ImType.eq(DcMessage.TYPE_IM_CHAT));
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        qb.where(MessageDetailDao.Properties.Status.eq(MessageDetail.IM_STATUS_UNREAD));
        return qb.count();
    }

    /**
     * 查询当前登录用户的所有私信消息
     *
     * @param belongUserId
     * @param pageSize
     * @param startTimestamp
     * @return
     */
    public List<MessageDetail> queryCurrentUserAllConversationMessage(long belongUserId, int pageSize, long startTimestamp) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(MessageDetailDao.Properties.ImType.eq(DcMessage.TYPE_IM_CHAT));
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        qb.orderDesc(MessageDetailDao.Properties.Create_time);
        if (pageSize > 0) {
            qb.limit(pageSize);
        }
        return qb.list();
    }

    /**
     * 删除某个用户的所有聊天Im消息
     *
     * @param fromUserId
     */
    public void deleteConversation(long belongUserId, long fromUserId, long toUserId) {
        QueryBuilder<Conversation> qb = GreenDaoManager.get().getConversationDao().queryBuilder();
        qb.where(ConversationDao.Properties.BelongUserId.eq(belongUserId));
        qb.whereOr(qb.and(ConversationDao.Properties.FromUserId.eq(fromUserId), ConversationDao.Properties.ToUserId.eq(toUserId)),
                qb.and(ConversationDao.Properties.FromUserId.eq(toUserId), ConversationDao.Properties.ToUserId.eq(fromUserId)));
        List<Conversation> list = qb.list();
        GreenDaoManager.get().getConversationDao().deleteInTx(list);

        List<MessageDetail> deleteList = MessageManager.get().queryConversationMessage(belongUserId, fromUserId, toUserId, -1, System.currentTimeMillis());
        GreenDaoManager.get().getMessageDetailDao().deleteInTx(deleteList);
    }


    /**
     * 退出登录时删除多余的记录
     */
    public void deleteRedundantRecord() {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(0));
        List<MessageDetail> list = qb.list();
        if (!CollectionUtil.isEmpty(list)) {
            GreenDaoManager.get().getMessageDetailDao().deleteInTx(list);
        }
    }

    /**
     * 查询某个Id之后会话的消息
     *
     * @param belongUserId
     * @param fromUserId
     * @param lastIMMsgId
     * @return
     */
    public List<MessageDetail> queryImChatMessageByImId(long belongUserId, long fromUserId, long lastIMMsgId) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(MessageDetailDao.Properties.ImType.eq(DcMessage.TYPE_IM_CHAT));
        if (fromUserId > -1) {
            qb.where(MessageDetailDao.Properties.FromUserId.eq(fromUserId));
        }
        qb.where(MessageDetailDao.Properties.DcImId.gt(lastIMMsgId));
        qb.orderAsc(MessageDetailDao.Properties.Create_time);
        qb.orderAsc(MessageDetailDao.Properties.DcImId);
        return qb.list();
    }

    /**
     * 查询未读消息数量
     *
     * @param userId         用户id，如果是未登录，传0
     * @param msgType        IM消息类型，参考{@link MessageDetail 的IM消息类型},如果为null，则查询所有类型的消息数量
     * @param startTimestamp 开始时间戳
     * @param status         IM消息阅读状态，参考{@link MessageDetail 的消息阅读状态},如果为0，则查询所有状态
     * @return
     */
    public long getUnreadCount(long userId, String msgType, long startTimestamp, int status) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(userId));
        if (status > 0) {
            qb.where(MessageDetailDao.Properties.Status.eq(status));
        }
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        if (!TextUtils.isEmpty(msgType)) {
            qb.where(MessageDetailDao.Properties.Msg_type.eq(msgType));
        }
        return qb.count();
    }

    /**
     * 查询未读消息，按照时间戳倒序排列
     *
     * @param userId         用户id，如果是未登录，传0
     * @param msgType        IM消息类型，参考{@link MessageDetail 的IM消息类型},如果为null，则查询所有类型的消息数量
     * @param startTimestamp 开始时间戳
     * @param pageSize       查询分页大小
     * @param status         IM消息阅读状态，参考{@link MessageDetail 的消息阅读状态},如果为0，则查询所有状态
     * @return
     */
    public List<MessageDetail> queryMessage(long userId, String msgType, long startTimestamp, int pageSize, int status) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(userId));
        if (status > 0) {
            qb.where(MessageDetailDao.Properties.Status.eq(status));
        }
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        if (!TextUtils.isEmpty(msgType)) {
            qb.where(MessageDetailDao.Properties.Msg_type.eq(msgType));
        }
        return qb
                .orderDesc(MessageDetailDao.Properties.Create_time)
                .limit(pageSize)
                .list();
    }

    public List<MessageDetail> queryMessage(long userId, String msgType, String type2, long startTimestamp, int pageSize, int status) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(userId));
        if (status > 0) {
            qb.where(MessageDetailDao.Properties.Status.eq(status));
        }
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        if (!TextUtils.isEmpty(msgType)) {
            qb.whereOr(MessageDetailDao.Properties.Msg_type.eq(msgType), MessageDetailDao.Properties.Msg_type.eq(type2));
        }
        return qb
                .orderDesc(MessageDetailDao.Properties.Create_time)
                .limit(pageSize)
                .list();
    }

    /**
     * 查询作品的消息
     *
     * @param userId
     * @param startTimestamp
     * @param pageSize
     * @param status
     * @return
     */
    public List<MessageDetail> queryProductMessage(long userId, long startTimestamp, int pageSize, int status) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(userId));
        if (status > 0) {
            qb.where(MessageDetailDao.Properties.Status.eq(status));
        }
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        qb.whereOr(MessageDetailDao.Properties.Msg_type.eq(MessageDetail.TYPE_GIFT_V2),
                MessageDetailDao.Properties.Msg_type.eq(MessageDetail.TYPE_GIFT),
                MessageDetailDao.Properties.Msg_type.eq(MessageDetail.TYPE_CO_CREATE),
                MessageDetailDao.Properties.Msg_type.eq(MessageDetail.TYPE_COMMENT)
        );
        return qb
                .orderDesc(MessageDetailDao.Properties.Create_time)
                .limit(pageSize)
                .list();
    }

    /**
     * 查询作品未读数量
     *
     * @param userId
     * @param startTimestamp
     * @param status
     * @return
     */
    public long queryProductUnreadCount(long userId, long startTimestamp, int status) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(userId));
        if (status > 0) {
            qb.where(MessageDetailDao.Properties.Status.eq(status));
        }
        qb.where(MessageDetailDao.Properties.Create_time.lt(startTimestamp));
        qb.whereOr(MessageDetailDao.Properties.Msg_type.eq(MessageDetail.TYPE_GIFT_V2),
                MessageDetailDao.Properties.Msg_type.eq(MessageDetail.TYPE_GIFT),
                MessageDetailDao.Properties.Msg_type.eq(MessageDetail.TYPE_CO_CREATE),
                MessageDetailDao.Properties.Msg_type.eq(MessageDetail.TYPE_COMMENT)
        );
        return qb.count();
    }

    /**
     * 查询私信和其他im消息的未读数量
     *
     * @param includeChat
     * @return
     */
    public long getImAllUnreadCount(boolean includeChat) {
        long start = System.currentTimeMillis();
        KLog.i("====开始查询IM消息数量");
        long productUnreadCount = MessageManager.get().queryProductUnreadCount(AccountUtil.getUserId(),
                System.currentTimeMillis(), MessageDetail.IM_STATUS_UNREAD);
        long followUnreadCount = MessageManager.get().queryFansUnreadCount(System.currentTimeMillis());
        long likeUnreadCount = MessageManager.get().queryLikeUnreadCount(System.currentTimeMillis());
        long chatUnreadCount = 0;
        if (includeChat) {
            chatUnreadCount = MessageManager.get().queryAllConversationUnreadCount(AccountUtil.getUserId(), System.currentTimeMillis());
        }
        KLog.i("======查询IM消息数量结束：" + (System.currentTimeMillis() - start) + "\nchatUnreadCount:" + chatUnreadCount +
                " productUnreadCount:" + productUnreadCount +
                " followUnreadCount:" + followUnreadCount +
                " likeUnreadCount:" + likeUnreadCount);
        return productUnreadCount + followUnreadCount + likeUnreadCount + chatUnreadCount;
    }

    /**
     * 查询粉丝未读数量
     *
     * @return
     */
    public long queryFansUnreadCount(long startTimestamp) {
        return getUnreadCount(AccountUtil.getUserId(), MessageDetail.TYPE_FOLLOW, startTimestamp, MessageDetail.IM_STATUS_UNREAD);
    }

    /**
     * 查询粉丝的消息
     *
     * @return
     */
    public List<MessageDetail> queryFansMessage(long userId, long startTimestamp, int pageSize, int status) {
        return queryMessage(userId, MessageDetail.TYPE_FOLLOW, startTimestamp, pageSize, status);
    }

    /**
     * 查询点赞未读数量
     *
     * @return
     */
    public long queryLikeUnreadCount(long startTimestamp) {
        return getUnreadCount(AccountUtil.getUserId(), MessageDetail.TYPE_LIKE, startTimestamp, MessageDetail.IM_STATUS_UNREAD);
    }

    /**
     * 查询点赞的消息
     *
     * @return
     */
    public List<MessageDetail> queryLikeMessage(long userId, long startTimestamp, int pageSize, int status) {
        return queryMessage(userId, MessageDetail.TYPE_LIKE, MessageDetail.TYPE_FOLLOW, startTimestamp, pageSize, status);
    }

    /**
     * 查询某个用户的所有消息
     *
     * @param userId
     * @return
     */
    public List<MessageDetail> queryUserMessage(long userId) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.whereOr(MessageDetailDao.Properties.FromUserId.eq(userId), MessageDetailDao.Properties.ToUserId.eq(userId));
        return qb.list();
    }


    /**
     * 更新单个消息到数据库
     *
     * @param message
     */
    public void updateMessageInfo(MessageDetail message) {
        GreenDaoManager.get().getMessageDetailDao().update(message);
    }

    /**
     * 更新单个消息到数据库
     *
     * @param message
     */
    public long insertOrReplaceMessageInfo(MessageDetail message) {
        return GreenDaoManager.get().getMessageDetailDao().insertOrReplace(message);
    }

    /**
     * 更新多个消息内容到数据库
     *
     * @param list
     */
    public void updateMessageInfo(List<MessageDetail> list) {
        GreenDaoManager.get().getMessageDetailDao().updateInTx(list);
    }

    public void updateMessageRead(List<MessageDetail> list) {
        if (!CollectionUtil.isEmpty(list)) {
            for (MessageDetail messageDetail : list) {
                if (messageDetail != null) {
                    messageDetail.setStatus(MessageDetail.IM_STATUS_READ);
                }
            }
            GreenDaoManager.get().getMessageDetailDao().updateInTx(list);
        }
    }

    public void clearSystemMessage() {
        if (systemOperator != null) {
            systemOperator.shutdown();
        }
    }

    public MessageDetail pollSystemMessage() {
        if (systemOperator != null) {
            return systemOperator.pollMessage();
        }
        return null;
    }

    /**
     * 最后一条数据的时间
     *
     * @param belongUserId
     * @param fromUserId
     * @return
     */
    public long getLastCreateTimeByUserId(long belongUserId, long fromUserId) {
        long lastTime = 0L;
        MessageDetail messageDetail = queryLatestImChatMessage(belongUserId, fromUserId);
        if (messageDetail != null) {
            lastTime = messageDetail.getCreate_time();
        }
        return lastTime;
    }

    /**
     * 查询某个会话的最近一条聊天消息
     *
     * @param belongUserId
     * @param fromUserId
     * @return
     */
    public MessageDetail queryLatestImChatMessage(long belongUserId, long fromUserId) {
        List<MessageDetail> messageDetailList = queryImChatMessage(belongUserId, fromUserId, 1);
        return CollectionUtil.isEmpty(messageDetailList) ? null : messageDetailList.get(0);
    }

    /**
     * 查询某个会话的消息
     *
     * @param belongUserId
     * @param fromUserId   -1代表查询所有会话的消息
     * @param pageSize     0代表没有限制
     * @return
     */
    public List<MessageDetail> queryImChatMessage(long belongUserId, long fromUserId, int pageSize) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(MessageDetailDao.Properties.ImType.eq(DcMessage.TYPE_IM_CHAT));
        if (fromUserId > -1) {
            qb.whereOr(MessageDetailDao.Properties.FromUserId.eq(fromUserId), MessageDetailDao.Properties.ToUserId.eq(fromUserId));
        }
        qb.orderDesc(MessageDetailDao.Properties.Create_time);
        if (pageSize > 0) {
            qb.limit(pageSize);
        }
        return qb.list();
    }

    /**
     * 查询dcImId之前的数据
     *
     * @param belongUserId
     * @param fromUserId
     * @param pageSize
     * @param dcImId
     * @return
     */
    public List<MessageDetail> queryImChatMessageBeforeDcImId(long belongUserId, long fromUserId, int pageSize, long dcImId) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(MessageDetailDao.Properties.ImType.eq(DcMessage.TYPE_IM_CHAT));
        if (fromUserId > -1) {
            qb.whereOr(MessageDetailDao.Properties.FromUserId.eq(fromUserId), MessageDetailDao.Properties.ToUserId.eq(fromUserId));
        }
        qb.where(MessageDetailDao.Properties.DcImId.lt(dcImId));
        qb.orderDesc(MessageDetailDao.Properties.Create_time);
        if (pageSize > 0) {
            qb.limit(pageSize);
        }
        return qb.list();
    }


    /**
     * 设置会话消息已读
     *
     * @param belongUserId
     * @param fromUserId
     */
    public void setImChatMessageRead(long belongUserId, long fromUserId) {
        QueryBuilder<MessageDetail> qb = GreenDaoManager.get().getMessageDetailDao().queryBuilder();
        qb.where(MessageDetailDao.Properties.BelongUserId.eq(belongUserId));
        qb.where(MessageDetailDao.Properties.FromUserId.eq(fromUserId));
        qb.where(MessageDetailDao.Properties.ImType.eq(DcMessage.TYPE_IM_CHAT));
        List<MessageDetail> list = qb.list();
        if (!CollectionUtil.isEmpty(list)) {
            for (MessageDetail messageDetail : list) {
                if (messageDetail.getStatus() != MessageDetail.IM_STATUS_READ
                        && messageDetail.getStatus() != MessageDetail.IM_STATUS_PLAYED) {
                    messageDetail.setStatus(MessageDetail.IM_STATUS_READ);
                }
            }
            GreenDaoManager.get().getMessageDetailDao().updateInTx(list);
        }
    }

}
