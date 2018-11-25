package com.wmlive.hhvideo.common.manager.greendao;

import android.database.Cursor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ProductExtendEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.db.DaoMaster;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ObjectUtil;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsq on 12/25/2017.12:29 PM
 *
 * @author lsq
 * @describe GreenDao数据库从version5升级到version6工具类
 */

public class Migration5to6 {

    private static final String TAG = "DcApp Migration5to6";

    public static void migrate5to6(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        List<ProductEntity> entityList = queryAllProduct(db);
        MigrationHelper.generateTempTables(db, daoClasses);

        KLog.e("======drop  old tables PRODUCT_ENTITY start");
        String sql = "DROP TABLE IF EXISTS PRODUCT_ENTITY";
        db.execSQL(sql);
        KLog.e("======drop  old tables PRODUCT_ENTITY ok");

        KLog.e("======create all new tables start");
        DaoMaster.createAllTables(db, true);
        KLog.e("======create all new tables ok");
        restore5to6Data(db, entityList, daoClasses);
    }

    private static void restore5to6Data(Database db, List<ProductEntity> entityList, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        DaoConfig daoConfig;
        String sqlString;
        String tableName;
        String tempTableName;
        List<String> tempProperties;
        List<String> properties = new ArrayList<>();
        StringBuilder sqlStringBuilder = new StringBuilder(200);
        String columnName;
        for (int i = 0, tableCount = daoClasses.length; i < tableCount; i++) {
            daoConfig = new DaoConfig(db, daoClasses[i]);
            tableName = daoConfig.tablename;
            tempTableName = daoConfig.tablename.concat(MigrationHelper.TEMP_TABLE_NAME_SUFFIX);
            properties.clear();
            sqlStringBuilder.setLength(0);
            //ProductEntity表需要进行数据处理
            if (MigrationHelper.PRODUCT_TABLE_NAME.equals(tableName)) {
                //查询原表的数据
                if (!CollectionUtil.isEmpty(entityList)) {
                    KLog.i("======product table record size:" + entityList.size());
                    Object[] objects;
                    for (ProductEntity productEntity : entityList) {
                        if (productEntity != null) {
                            sqlStringBuilder.setLength(0);
                            sqlStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                            //插入到新表的数据
                            sqlStringBuilder.append("_id,USER_ID,COMBINE_VIDEO,MODIFY_TIME,BASE_DIR,WEBP_PATH,COVER_PATH,PRODUCT_TYPE,");
                            sqlStringBuilder.append("MUSIC_MIX_FACTOR,ORIGINAL_MIX_FACTOR,ORIGINAL_SHOW_MIX_FACTOR,ORIGINAL_ID,");
                            sqlStringBuilder.append("SHORT_VIDEOS_BYTES,FRAME_INFO_BYTES,MUSIC_INFO_BYTES,TOPIC_INFO_BYTES,EXTEND_INFO_BYTES)");
                            sqlStringBuilder.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                            objects = new Object[]{productEntity.getId(), productEntity.getUserId(),
                                    productEntity.getCombineVideo(), productEntity.getModifyTime(),
                                    productEntity.getBaseDir(), productEntity.getWebpPath(),
                                    productEntity.getCoverPath(), productEntity.getProductType(),
                                    productEntity.getMusicMixFactor(), productEntity.getOriginalMixFactor(),
                                    productEntity.getOriginalShowMixFactor(), productEntity.getOriginalId(),
                                    JSON.toJSONString(productEntity.shortVideoList).getBytes(),
                                    JSON.toJSONString(productEntity.frameInfo).getBytes(),
                                    JSON.toJSONString(productEntity.musicInfo).getBytes(),
                                    JSON.toJSONString(productEntity.topicInfo).getBytes(),
                                    JSON.toJSONString(productEntity.extendInfo).getBytes()};
                            sqlString = sqlStringBuilder.toString();
                            KLog.e("=======insert a record into product table sql string is :" + sqlString);
                            db.execSQL(sqlString, objects);
                        }
                    }
                }
            } else {
                tempProperties = MigrationHelper.getColumns(db, tempTableName);
                for (int j = 0, propertyCount = daoConfig.properties.length; j < propertyCount; j++) {
                    columnName = daoConfig.properties[j].columnName;
                    if (tempProperties.contains(columnName)) {
                        properties.add(columnName);
                    }
                }
                sqlStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                sqlStringBuilder.append(TextUtils.join(",", properties));
                sqlStringBuilder.append(") SELECT ");
                sqlStringBuilder.append(TextUtils.join(",", properties));
                sqlStringBuilder.append(" FROM ").append(tempTableName).append(";");
                sqlString = sqlStringBuilder.toString();
                KLog.e("=======insert data into table sql string is :" + sqlString);
                db.execSQL(sqlString);
            }
            sqlString = "DROP TABLE IF EXISTS \"" + tempTableName + "\"";
            KLog.e("=======drop temp table sql string is :" + sqlString);
            db.execSQL(sqlString);
        }
    }

    private static List<ProductEntity> queryAllProduct(Database database) {
        KLog.i(TAG, "queryAll is start");
        Cursor cursor = database.rawQuery("SELECT * FROM " + MigrationHelper.PRODUCT_TABLE_NAME, null);
        List<ProductEntity> list = new ArrayList<>();
        if (cursor != null) {
            ProductEntity productEntity;
            while (cursor.moveToNext()) {
                productEntity = new ProductEntity();
                productEntity.setId(cursor.getLong(0));
                productEntity.setUserId(cursor.getLong(1));
                productEntity.setCombineVideo(cursor.getString(2));
                productEntity.setModifyTime(cursor.getLong(3));
                productEntity.setBaseDir(cursor.getString(4));
                productEntity.setWebpPath(cursor.getString(5));
                productEntity.setCoverPath(cursor.getString(6));
                productEntity.setProductType(cursor.getShort(7));
                productEntity.setMusicMixFactor(cursor.getInt(8));
                productEntity.setOriginalMixFactor(cursor.getInt(9));
                productEntity.setOriginalShowMixFactor(cursor.getInt(10));
                productEntity.setOriginalId(cursor.getLong(11));

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
        KLog.e(TAG, "queryAll product,size is:" + list.size() + "\nall product:" + CommonUtils.printList(list));
        return list;
    }
}
