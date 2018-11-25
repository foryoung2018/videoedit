package com.wmlive.hhvideo.heihei.db;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.alibaba.fastjson.JSON;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageButton;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.utils.JsonUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Transient;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by lsq on 2/1/2018.6:30 PM
 *
 * @author lsq
 * @describe 消息详情
 */
@Entity
public class MessageDetail implements Comparable<MessageDetail> {

    //系统弹窗类型对应字段type
    @Transient
    public static final String TYPE_TOAST = "toast";
    @Transient
    public static final String TYPE_IMAGE = "image";
    @Transient
    public static final String TYPE_ALERT = "alert";

    //IM消息类型，对应字段msg_type
    @Transient
    public static final String TYPE_LIKE = "like";
    @Transient
    public static final String TYPE_COMMENT = "comment";
    @Transient
    public static final String TYPE_FOLLOW = "follow";
    @Transient
    public static final String TYPE_GIFT = "gift";
    @Transient
    public static final String TYPE_GIFT_V2 = "gift_v2";
    @Transient
    public static final String TYPE_SYSTEM = "system";
    @Transient
    public static final String TYPE_CO_CREATE = "co_create";
    @Transient
    public static final String TYPE_TEXT_CONTENT = "text";//文本
    @Transient
    public static final String TYPE_AUDIO_CONTENT = "audio";//音频
    @Transient
    public static final String TYPE_SYSNOTIFY_CONTENT = "sysnotify";//系统通知
    @Transient
    public static final String TYPE_SYSTIME_CONTENT = "systime";//系统时间
    @Transient
    public static final String TYPE_SYSHINT_CONTENT = "syshint";//拉黑信息
    @Transient
    public static final String TYPE_TIP_CONTENT = "im_tip";//IM提示消息
    @Transient
    public static final String TYPE_IM_GIFT = "im_gift";//IM礼物消息

    //消息阅读状态
    @Transient
    public static final int IM_STATUS_NONE = 0;//已读和未读
    @Transient
    public static final int IM_STATUS_UNREAD = 1;//信息未读
    @Transient
    public static final int IM_STATUS_READ = 2;//信息已读
    @Transient
    public static final int IM_STATUS_PLAYED = 3;//已播放
    @Transient
    public static final int IM_STATUS_SENDING = 4;//发送中
    @Transient
    public static final int IM_STATUS_SENDFAIL = 5;//发送失败
    @Transient
    public static final int IM_STATUS_SENT = 6;//发送成功
    @Transient
    public static final int IM_STATUS_BAN = 7;//无法发送给对方消息

    //消息格式---类型
    @Transient
    public static final int IM_TYPE_TEXT = 0;//文本呢
    @Transient
    public static final int IM_TYPE_AUDIO = 1;//音频
    @Transient
    public static final int IM_TYPE_GIFT = 2;//礼物
    @Transient
    public static final int IM_TYPE_SYSTOPIC = 3;//系统主题
    @Transient
    public static final int IM_TYPE_SYSNOTIFY = 4;//系统通知
    @Transient
    public static final int IM_TYPE_IMAGE = 5;//图片
    @Transient
    public static final int IM_TYPE_SYSTIME = 6;//系统时间
    @Transient
    public static final int IM_TYPE_SYSVALUE = 7;//接口返回值

    //以下是系统弹窗的消息字段，不需要存储数据库
    @Transient
    public short priority = 50;//系统弹窗消息的优先级
    @Transient
    public short timeout;//系统弹窗消息的消失时间，单位秒
    @Transient
    public String type; //系统弹窗消息的类型，目前有TYPE_TOAST,TYPE_IMAGE,TYPE_ALERT
    @Transient
    public String title;//系统弹窗的title
    @Transient
    public String desc;//系统弹窗的desc
    @Transient
    public String image;//系统弹窗的图片
    @Transient
    public String jump;//系统弹窗的跳转
    @Transient
    public List<MessageButton> buttons;//系统弹窗的按钮

    //以下是IM消息的消息字段，需要存储到数据库
    @Transient
    public MessageContent content;
    @Transient
    public UserInfo from_user;
    @Transient
    public UserInfo to_user;

    @Id(autoincrement = true)
    public Long dcImId;

    public String local_msg_id;//本地id
    public String msg_id;
    public long create_time;
    public String device_id;
    @MsgType
    private String msg_type; //IM消息类型：点赞，评论，创作，文本，语音等 TYPE_TEXT_CONTENT TYPE_AUDIO_CONTENT
    public String tips;   //提示信息，比如：不支持的消息类型

    public long belongUserId;//自定义的字段，当前登录用户的id，0表示未登录的消息
    @MsgStatus
    private int status = IM_STATUS_UNREAD;//自定义的字段，消息状态：已读，未读等
    @Transient
    public long unreadCount;//自定义的字段，未读数量，仅用于某个会话的消息数量显示
    public String imType;  //IM消息的类型，目前主要是DcMessage的TYPE_IM和DcMessage的TYPE_IM_CHAT,方便查找出TYPE_IM_CHAT
    public String messageContent;
    public String fromUser;
    public String toUser;
    public long fromUserId;
    public long toUserId;

    public String fromUserName; //对方用户名
    public String imTitle;      //消息title
    public String briefDesc;    //消息简述
    public String extendDesc;//扩展字段

    @Generated(hash = 736797642)
    @Keep
    public MessageDetail(Long dcImId, String local_msg_id, String msg_id, long create_time,
                         String device_id, String msg_type, String tips, long belongUserId, int status,
                         String imType, String messageContent, String fromUser, String toUser, long fromUserId,
                         long toUserId, String fromUserName, String imTitle, String briefDesc, String extendDesc) {
        this.dcImId = dcImId;
        this.local_msg_id = local_msg_id;
        this.msg_id = msg_id;
        this.create_time = create_time;
        this.device_id = device_id;
        this.msg_type = msg_type;
        this.tips = tips;
        this.belongUserId = belongUserId;
        this.status = status;
        this.imType = imType;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.fromUserName = fromUserName;
        this.imTitle = imTitle;
        this.briefDesc = briefDesc;
        this.extendDesc = extendDesc;
        setMessageContent(messageContent);
        setFromUser(fromUser);
        setToUser(toUser);
    }

    @Generated(hash = 1681087717)
    public MessageDetail() {
    }

    public long getBelongUserId() {
        return this.belongUserId;
    }

    public void setBelongUserId(long belongUserId) {
        this.belongUserId = belongUserId;
    }

    @MsgStatus
    public int getStatus() {
        return this.status;
    }

    public void setStatus(@MsgStatus int status) {
        this.status = status;
    }

    public long getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(long createTime) {
        this.create_time = createTime;
    }

    public String getMsg_id() {
        return this.msg_id;
    }

    public void setMsg_id(String msgId) {
        this.msg_id = msgId;
    }

    public String getDevice_id() {
        return this.device_id;
    }

    public void setDevice_id(String deviceId) {
        this.device_id = deviceId;
    }

    @MsgType
    public String getMsg_type() {
        return this.msg_type;
    }

    public void setMsg_type(@MsgType String msgType) {
        this.msg_type = msgType;
    }

    public String getTips() {
        return this.tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getImType() {
        return this.imType;
    }

    public void setImType(String imType) {
        this.imType = imType;
    }

    public long getFromUserId() {
        return this.fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getToUserId() {
        return this.toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessageContent() {
        if (content != null) {
            return JSON.toJSONString(content);
        }
        return null;
    }

    public void setMessageContent(String messageContent) {
        this.content = JsonUtils.parseObject(messageContent, MessageContent.class);
    }

    public void setMessageContent(MessageContent content) {
        this.content = content;
    }

    public String getFromUser() {
        if (from_user != null) {
            return JSON.toJSONString(from_user);
        }
        return null;
    }

    public void setFromUser(String fromUser) {
        this.from_user = JsonUtils.parseObject(fromUser, UserInfo.class);
    }

    public String getToUser() {
        if (to_user != null) {
            return JSON.toJSONString(to_user);
        }
        return null;
    }

    public void setToUser(String toUser) {
        this.to_user = JsonUtils.parseObject(toUser, UserInfo.class);
    }

    public String getFromUserName() {
        return this.fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getImTitle() {
        return this.imTitle;
    }

    public void setImTitle(String imTitle) {
        this.imTitle = imTitle;
    }

    public String getBriefDesc() {
        return this.briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public Long getDcImId() {
        return this.dcImId;
    }

    public void setDcImId(Long dcImId) {
        this.dcImId = dcImId;
    }

    public String getLocal_msg_id() {
        return this.local_msg_id;
    }

    public void setLocal_msg_id(String local_msg_id) {
        this.local_msg_id = local_msg_id;
    }

    public String getExtendDesc() {
        return this.extendDesc;
    }

    public void setExtendDesc(String extendDesc) {
        this.extendDesc = extendDesc;
    }

    @Override
    public int compareTo(@NonNull MessageDetail o) {
        //priority由高到低,相等时timestamp降序
        if (this == o) {
            return 0;
        }
        if (this.priority > o.priority) {
            return -1;
        } else if (this.priority < o.priority) {
            return 1;
        } else {
            //按照时间降序排列
            if (this.create_time > o.create_time) {
                return -1;
            } else if (this.create_time < o.create_time) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return "MessageDetail{" +
                "priority=" + priority +
                ", timeout=" + timeout +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", image='" + image + '\'' +
                ", jump='" + jump + '\'' +
                ", buttons=" + buttons +
                ", content=" + content +
                ", from_user=" + from_user +
                ", to_user=" + to_user +
                ", dcImId=" + dcImId +
                ", local_msg_id='" + local_msg_id + '\'' +
                ", msg_id='" + msg_id + '\'' +
                ", create_time=" + create_time +
                ", device_id='" + device_id + '\'' +
                ", msg_type='" + msg_type + '\'' +
                ", tips='" + tips + '\'' +
                ", belongUserId=" + belongUserId +
                ", status=" + status +
                ", unreadCount=" + unreadCount +
                ", imType='" + imType + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", fromUserName='" + fromUserName + '\'' +
                ", imTitle='" + imTitle + '\'' +
                ", briefDesc='" + briefDesc + '\'' +
                ", extendDesc='" + extendDesc + '\'' +
                '}';
    }

    @StringDef({TYPE_LIKE, TYPE_COMMENT, TYPE_FOLLOW, TYPE_GIFT, TYPE_GIFT_V2, TYPE_SYSTEM, TYPE_CO_CREATE, TYPE_TEXT_CONTENT,
            TYPE_AUDIO_CONTENT, TYPE_SYSNOTIFY_CONTENT, TYPE_SYSTIME_CONTENT, TYPE_SYSHINT_CONTENT, TYPE_TIP_CONTENT, TYPE_IM_GIFT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MsgType {
    }

    @IntDef({IM_STATUS_NONE, IM_STATUS_UNREAD, IM_STATUS_READ, IM_STATUS_PLAYED,
            IM_STATUS_SENDING, IM_STATUS_SENDFAIL, IM_STATUS_SENT, IM_STATUS_BAN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MsgStatus {
    }
}
