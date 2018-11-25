package com.wmlive.hhvideo.common.manager.greendao;

import android.database.Cursor;
import android.text.TextUtils;

import com.wmlive.hhvideo.heihei.beans.immessage.DcMessage;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.db.MessageDetailDao;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lsq
 * @describe GreenDao数据库从version6升级到version7工具类
 */

public class Migration6to7 {

    public static void migrate6to7(Database db) {
        List<MessageDetail> list = queryAllOldImMessage(db);
        String sql;
        if (!CollectionUtil.isEmpty(list)) {
            StringBuilder sqlStringBuilder = new StringBuilder(200);
            Object[] objects;
            MessageDetail messageDetail;
            for (int i = 0; i < list.size(); i++) {
                messageDetail = list.get(i);
                if (messageDetail != null) {
                    sqlStringBuilder.setLength(0);
                    sqlStringBuilder.append("INSERT INTO ").append(MessageDetailDao.TABLENAME).append(" (");
                    sqlStringBuilder.append("_id,LOCAL_MSG_ID,MSG_ID,CREATE_TIME,DEVICE_ID,MSG_TYPE,TIPS,BELONG_USER_ID,");
                    sqlStringBuilder.append("STATUS,IM_TYPE,MESSAGE_CONTENT,FROM_USER,TO_USER,FROM_USER_ID,TO_USER_ID,");
                    sqlStringBuilder.append("FROM_USER_NAME,IM_TITLE,BRIEF_DESC,EXTEND_DESC)");
                    sqlStringBuilder.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    objects = new Object[]{
                            messageDetail.dcImId,
                            messageDetail.local_msg_id,
                            messageDetail.msg_id,
                            messageDetail.create_time,
                            messageDetail.device_id,
                            messageDetail.getMsg_type(),
                            messageDetail.tips,
                            messageDetail.belongUserId,
                            messageDetail.getStatus(),
                            messageDetail.imType,
                            messageDetail.getMessageContent(),
                            messageDetail.getFromUser(),
                            messageDetail.getToUser(),
                            messageDetail.fromUserId,
                            messageDetail.toUserId,
                            messageDetail.fromUserName,
                            messageDetail.imTitle,
                            messageDetail.briefDesc,
                            messageDetail.extendDesc,
                    };
                    sql = sqlStringBuilder.toString();
                    KLog.e("=======insert a record into MessageDetail table sql string is :" + sql);
                    db.execSQL(sql, objects);
                }
            }
        }
        sql = "DROP TABLE IF EXISTS GIFT_TABLE";
        db.execSQL(sql);
        KLog.e("=====删除表 GIFT_TABLE");
        sql = "DROP TABLE IF EXISTS IMMESSAGE_DEFAULT_TABLE";
        db.execSQL(sql);
        KLog.e("=====删除表 IMMESSAGE_DEFAULT_TABLE");
        sql = "DROP TABLE IF EXISTS IMMESSAGE_USER_TABLE";
        db.execSQL(sql);
        KLog.e("=====删除表 IMMESSAGE_USER_TABLE");
    }

    public static List<MessageDetail> queryAllOldImMessage(Database database) {
        Cursor cursor = database.rawQuery("SELECT * FROM IMMESSAGE_USER_TABLE", null);
        List<MessageDetail> list = new ArrayList<>(4);
        if (cursor != null) {
            MessageDetail messageDetail;
            UserInfo userEntity;
            String toUser;
            while (cursor.moveToNext()) {
                toUser = cursor.getString(3);
                if (!TextUtils.isEmpty(toUser)) {
                    userEntity = JsonUtils.parseObject(toUser, UserInfo.class);
                    if (userEntity != null) {
                        messageDetail = new MessageDetail();
                        messageDetail.belongUserId = userEntity.getId();
                        messageDetail.to_user = new UserInfo();
                        messageDetail.to_user.replace(userEntity);
                        messageDetail.toUserId = userEntity.getId();
                        userEntity = JsonUtils.parseObject(cursor.getString(2), UserInfo.class);
                        if (userEntity != null) {
                            messageDetail.from_user = new UserInfo();
                            messageDetail.from_user.replace(userEntity);
                            messageDetail.fromUserId = userEntity.getId();
                            messageDetail.fromUserName = userEntity.getName();
                        }

                        messageDetail.imType = DcMessage.TYPE_IM;
                        messageDetail.setMsg_type(cursor.getString(1));
                        messageDetail.content = JsonUtils.parseObject(cursor.getString(4), MessageContent.class);
                        if (messageDetail.content != null) {
                            messageDetail.imTitle = messageDetail.content.title;
                            messageDetail.briefDesc = messageDetail.content.desc;
                        }
                        messageDetail.msg_id = cursor.getString(5);
                        messageDetail.create_time = Long.parseLong(cursor.getString(6));
                        messageDetail.tips = cursor.getString(7);
                        messageDetail.setStatus(MessageDetail.IM_STATUS_READ);
                        list.add(messageDetail);
                    }
                }
            }
        }
        KLog.i("=========查询到的IM记录：" + CommonUtils.printList(list));
        return list;
    }

}
