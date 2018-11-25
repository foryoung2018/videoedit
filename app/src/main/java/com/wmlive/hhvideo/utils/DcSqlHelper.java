package com.wmlive.hhvideo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ProductExtendEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.db.MessageDetailDao;
import com.wmlive.hhvideo.heihei.db.ProductEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsq on 12/20/2017.4:09 PM
 *
 * @author lsq
 * @describe 数据库测试类，主要测试查询作品
 */

public class DcSqlHelper extends SQLiteOpenHelper {
    private static final String TAG = "DcSqlHelper";

    private static DcSqlHelper dcSqlHelper;

    /**
     * @param context
     * @param name
     * @param factory
     * @param version 这个版本需要跟GreenDao的schemaVersion保存一致
     */
    public DcSqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "db_dongci", factory, 7);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String constraint = "IF NOT EXISTS ";
        db.execSQL("CREATE TABLE " + constraint + "\"PRODUCT_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"COMBINE_VIDEO\" TEXT," + // 2: combineVideo
                "\"MODIFY_TIME\" INTEGER NOT NULL ," + // 3: modifyTime
                "\"BASE_DIR\" TEXT," + // 4: baseDir
                "\"WEBP_PATH\" TEXT," + // 5: webpPath
                "\"COVER_PATH\" TEXT," + // 6: coverPath
                "\"PRODUCT_TYPE\" INTEGER NOT NULL ," + // 7: productType
                "\"MUSIC_MIX_FACTOR\" INTEGER NOT NULL ," + // 8: musicMixFactor
                "\"ORIGINAL_MIX_FACTOR\" INTEGER NOT NULL ," + // 9: originalMixFactor
                "\"ORIGINAL_SHOW_MIX_FACTOR\" INTEGER NOT NULL ," + // 10: originalShowMixFactor
                "\"ORIGINAL_ID\" INTEGER NOT NULL ," + // 11: originalId
                "\"SHORT_VIDEOS_BYTES\" BLOB," + // 12: shortVideosBytes
                "\"FRAME_INFO_BYTES\" BLOB," + // 13: frameInfoBytes
                "\"MUSIC_INFO_BYTES\" BLOB," + // 14: musicInfoBytes
                "\"TOPIC_INFO_BYTES\" BLOB," + // 15: topicInfoBytes
                "\"EXTEND_INFO_BYTES\" BLOB);"); // 16: extendInfoBytes
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static DcSqlHelper get() {
        if (dcSqlHelper == null) {
            dcSqlHelper = new DcSqlHelper(DCApplication.getDCApp(), "db_dongci", null, 5);
        }
        return dcSqlHelper;
    }

    public void queryAllTableName() {
        Cursor cursor = getReadableDatabase().rawQuery("select name from sqlite_master where type='table' order by name", null);
        if (cursor != null) {
            StringBuilder names = new StringBuilder(20);
            while (cursor.moveToNext()) {
                //遍历出表名
                names.append(cursor.getString(0)).append(",");
            }
            KLog.i(TAG, "存在的表有：" + names.toString());
            cursor.close();
        }

    }

    public void queryAll() {
        Cursor cursor = getReadableDatabase().query("PRODUCT_ENTITY", null, null, null, null, null, null);
        KLog.i(TAG, "queryAll is start");
        if (cursor != null) {
            StringBuilder sb;
            while (cursor.moveToNext()) {
                sb = new StringBuilder(200);
                sb.append("_id:").append(String.valueOf(cursor.getLong(0)));
                sb.append(" USER_ID:").append(String.valueOf(cursor.getInt(1)));
                sb.append(" COMBINE_VIDEO:").append(String.valueOf(cursor.getString(2)));
                sb.append(" MODIFY_TIME:").append(String.valueOf(cursor.getInt(3)));
                sb.append(" BASE_DIR:").append(String.valueOf(cursor.getString(4)));
                sb.append(" WEBP_PATH:").append(String.valueOf(cursor.getString(5)));
                sb.append(" COVER_PATH:").append(String.valueOf(cursor.getString(6)));
                sb.append(" PRODUCT_TYPE:").append(String.valueOf(cursor.getInt(7)));
                sb.append(" MUSIC_MIX_FACTOR:").append(String.valueOf(cursor.getInt(8)));
                sb.append(" ORIGINAL_MIX_FACTOR:").append(String.valueOf(cursor.getInt(9)));
                sb.append(" ORIGINAL_SHOW_MIX_FACTOR:").append(String.valueOf(cursor.getInt(10)));
                sb.append(" ORIGINAL_ID:").append(String.valueOf(cursor.getInt(11)));

                sb.append(" SHORT_VIDEOS_BYTES:").append(new String(cursor.getBlob(12)));
                sb.append(" FRAME_INFO_BYTES:").append(new String(cursor.getBlob(13)));
                sb.append(" MUSIC_INFO_BYTES:").append(new String(cursor.getBlob(14)));
                sb.append(" TOPIC_INFO_BYTES:").append(new String(cursor.getBlob(15)));
                byte[] bytes = cursor.getBlob(16);
                if (bytes != null) {
                    sb.append(" EXTEND_INFO_BYTES:").append(new String(bytes));
                }
                KLog.i(TAG, " a  record:" + sb.toString());
            }
            cursor.close();
        } else {
            KLog.i(TAG, "queryAll is null");
        }
    }

    public List<ProductEntity> queryAllProduct() {
        Cursor cursor = getReadableDatabase().query("PRODUCT_ENTITY", null, null, null, null, null, null);
        KLog.i(TAG, "queryAll is start");
        List<ProductEntity> list = new ArrayList<>();
        if (cursor != null) {
            ProductEntity productEntity;
            while (cursor.moveToNext()) {
                productEntity = new ProductEntity();
                productEntity.setId(cursor.getLong(0));
                productEntity.setUserId(cursor.getInt(1));
                productEntity.setCombineVideo(cursor.getString(2));
                productEntity.setModifyTime(cursor.getInt(3));
                productEntity.setBaseDir(cursor.getString(4));
                productEntity.setWebpPath(cursor.getString(5));
                productEntity.setCoverPath(cursor.getString(6));
                productEntity.setProductType(cursor.getShort(7));
                productEntity.setMusicMixFactor(cursor.getInt(8));
                productEntity.setOriginalMixFactor(cursor.getInt(9));
                productEntity.setOriginalShowMixFactor(cursor.getInt(10));
                productEntity.setOriginalId(cursor.getInt(11));

                productEntity.shortVideoList = ObjectUtil.toParcelObjList(cursor.getBlob(12), ShortVideoEntity.CREATOR);
                productEntity.frameInfo = (FrameInfo) ObjectUtil.toSerializableObj(cursor.getBlob(13));
                productEntity.musicInfo = ObjectUtil.toParcelObj(cursor.getBlob(14), MusicInfoEntity.class.getClassLoader());
                productEntity.topicInfo = ObjectUtil.toParcelObj(cursor.getBlob(15), TopicInfoEntity.class.getClassLoader());
                productEntity.extendInfo = ObjectUtil.toParcelObj(cursor.getBlob(16), ProductExtendEntity.class.getClassLoader());
                list.add(productEntity);
            }
            cursor.close();
        } else {
            KLog.i(TAG, "queryAll is null");
        }
        KLog.i(TAG, "queryAll product,size is:" + list.size() + "\nall product:" + CommonUtils.printList(list));
        return list;
    }

    public String setFrameInfoBytes(long id, byte[] frameInfoBytes) {
        if (frameInfoBytes != null) {
            String string = new String(frameInfoBytes);
            KLog.i(TAG, "product id:" + id + " 布局信息,setFrameInfoBytes: " + string);
            FrameInfo frameInfo = (FrameInfo) ObjectUtil.toSerializableObj(frameInfoBytes);
            if (frameInfo != null) {
                return frameInfo.toString();
            }
        }
        return "null";
    }

    public String setMusicInfoBytes(byte[] musicInfoBytes) {
        if (musicInfoBytes != null) {
            MusicInfoEntity musicInfo = ObjectUtil.toParcelObj(musicInfoBytes, MusicInfoEntity.class.getClassLoader());
            if (musicInfo != null) {
                return musicInfo.toString();
            }
        }
        return "null";
    }

    public String setShortVideosBytes(byte[] shortVideosBytes) {
        if (shortVideosBytes != null) {
            return CommonUtils.printList(ObjectUtil.toParcelObjList(shortVideosBytes, ShortVideoEntity.CREATOR));
        }
        return "null";
    }

    public String setTopicInfoBytes(byte[] topicInfoBytes) {
        if (topicInfoBytes != null) {
            TopicInfoEntity topicInfo = ObjectUtil.toParcelObj(topicInfoBytes, TopicInfoEntity.class.getClassLoader());
            if (topicInfo != null) {
                return topicInfo.toString();
            }
        }
        return "null";
    }

    public String setExtendInfoBytes(byte[] extendInfoBytes) {
        if (extendInfoBytes != null) {
            ProductExtendEntity extendInfo = ObjectUtil.toParcelObj(extendInfoBytes, ProductExtendEntity.class.getClassLoader());
            if (extendInfo != null) {
                return extendInfo.toString();
            }
        }
        return "null";
    }

    public String getTablePath(String tableName) {
        String pathDatabase = DCApplication.getDCApp().getDatabasePath(tableName + ".db").getPath();
        KLog.d(TAG, tableName + " Table Path " + pathDatabase);
        return pathDatabase;
    }

    public void queryMessageAll() {
        Cursor cursor = getReadableDatabase().query(MessageDetailDao.TABLENAME, null, null, null, null, null, null);
        KLog.i(TAG, "queryAll is start");
        if (cursor != null) {
            StringBuilder sb;
            while (cursor.moveToNext()) {
                sb = new StringBuilder(200);
                sb.append(" DcImId:").append(String.valueOf(cursor.getLong(0)));
                sb.append(" Local_msg_id:").append(String.valueOf(cursor.getString(1)));
                sb.append(" Msg_id:").append(String.valueOf(cursor.getString(2)));
                sb.append(" Create_time:").append(String.valueOf(cursor.getLong(3)));
                sb.append(" Device_id:").append(String.valueOf(cursor.getString(4)));
                sb.append(" Msg_type:").append(String.valueOf(cursor.getString(5)));
                sb.append(" Tips:").append(String.valueOf(cursor.getString(6)));
                sb.append(" BelongUserId:").append(String.valueOf(cursor.getLong(7)));
                sb.append(" Status:").append(String.valueOf(cursor.getInt(8)));
                sb.append(" ImType:").append(String.valueOf(cursor.getString(9)));
                sb.append(" MessageContent:").append(String.valueOf(cursor.getString(10)));
                sb.append(" FromUser:").append(String.valueOf(cursor.getString(11)));
                sb.append(" ToUser:").append(String.valueOf(cursor.getString(12)));
                sb.append(" FromUserId:").append(String.valueOf(cursor.getLong(13)));
                sb.append(" ToUserId:").append(String.valueOf(cursor.getLong(14)));
                sb.append(" FromUserName:").append(String.valueOf(cursor.getString(15)));
                sb.append(" ImTitle:").append(String.valueOf(cursor.getString(16)));
                sb.append(" BriefDesc:").append(String.valueOf(cursor.getString(17)));
                sb.append(" ExtendDesc:").append(String.valueOf(cursor.getString(18)));
                KLog.i(TAG, " Message Info: " + sb.toString());
            }
            cursor.close();
        } else {
            KLog.i(TAG, "queryAll is null");
        }
    }

}
