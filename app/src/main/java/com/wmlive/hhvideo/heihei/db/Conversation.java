package com.wmlive.hhvideo.heihei.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

/**
 * Created by Administrator on 3/15/2018.
 */
@Entity
public class Conversation {
    @Id(autoincrement = true)
    public Long dcConId;
    public long fromUserId;
    public long toUserId;
    public int position;

    public long belongUserId;
    public long unreadCount;

    public long createTime;
    public String fromUserName; //对方用户名
    public String fromUserAvatar; //对方用户头像
    public String imTitle;      //消息title
    public String briefDesc;    //消息简述
    public String extendDesc;//扩展字段

    public Conversation() {
    }

    public Conversation(long fromUserId, long toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    @Generated(hash = 1252330603)
    @Keep
    public Conversation(Long dcConId, long fromUserId, long toUserId, int position,
                        long belongUserId, long unreadCount, long createTime,
                        String fromUserName, String fromUserAvatar, String imTitle,
                        String briefDesc, String extendDesc) {
        this.dcConId = dcConId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.position = position;
        this.belongUserId = belongUserId;
        this.unreadCount = unreadCount;
        this.createTime = createTime;
        this.fromUserName = fromUserName;
        this.fromUserAvatar = fromUserAvatar;
        this.imTitle = imTitle;
        this.briefDesc = briefDesc;
        this.extendDesc = extendDesc;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "dcConId=" + dcConId +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", position=" + position +
                ", belongUserId=" + belongUserId +
                ", unreadCount=" + unreadCount +
                ", createTime=" + createTime +
                ", fromUserName='" + fromUserName + '\'' +
                ", fromUserAvatar='" + fromUserAvatar + '\'' +
                ", imTitle='" + imTitle + '\'' +
                ", briefDesc='" + briefDesc + '\'' +
                ", extendDesc='" + extendDesc + '\'' +
                '}';
    }

    public Long getDcConId() {
        return this.dcConId;
    }

    public void setDcConId(Long dcConId) {
        this.dcConId = dcConId;
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

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getBelongUserId() {
        return this.belongUserId;
    }

    public void setBelongUserId(long belongUserId) {
        this.belongUserId = belongUserId;
    }

    public long getUnreadCount() {
        return this.unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getFromUserName() {
        return this.fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserAvatar() {
        return this.fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
        this.fromUserAvatar = fromUserAvatar;
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

    public String getExtendDesc() {
        return this.extendDesc;
    }

    public void setExtendDesc(String extendDesc) {
        this.extendDesc = extendDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Conversation)) {
            return false;
        }
        Conversation that = (Conversation) o;
        return fromUserId == that.fromUserId && toUserId == that.toUserId
                || fromUserId == that.toUserId && toUserId == that.fromUserId;
    }

    @Override
    public int hashCode() {
        return dcConId != null ? dcConId.hashCode() : 0;
    }
}
