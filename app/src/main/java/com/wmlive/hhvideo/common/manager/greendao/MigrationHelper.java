package com.wmlive.hhvideo.common.manager.greendao;

import android.database.Cursor;
import android.text.TextUtils;

import com.wmlive.hhvideo.heihei.db.DaoMaster;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lsq on 12/20/2017.7:23 PM
 *
 * @author lsq
 * @describe GreenDao升级工具
 */

public class MigrationHelper {

    public static final String TEMP_TABLE_NAME_SUFFIX = "_TEMP";
    public static final String PRODUCT_TABLE_NAME = "PRODUCT_ENTITY";

    public static void migrate(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        generateTempTables(db, daoClasses);
        KLog.i("======drop all old tables start");
        DaoMaster.dropAllTables(db, true);
        KLog.i("======drop all old tables ok");
        KLog.i("======create all new tables start");
        DaoMaster.createAllTables(db, false);
        KLog.i("======create all new tables ok");
        restoreData(db, daoClasses);
    }

    public static void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        String sqlString;
        DaoConfig daoConfig;
        StringBuilder sqlStringBuilder = new StringBuilder(200);
        List<String> properties = new ArrayList<>();
        List<String> tableColumns;
        String columnName;
        for (int i = 0, tableCount = daoClasses.length; i < tableCount; i++) {
            daoConfig = new DaoConfig(db, daoClasses[i]);
            String divider = "";
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat(TEMP_TABLE_NAME_SUFFIX);
            properties.clear();
            sqlStringBuilder.setLength(0);
            KLog.i("======create temp table:" + tableName + " start");
            sqlStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (");
            tableColumns = getColumns(db, tableName);
            for (int j = 0, propertyCount = daoConfig.properties.length; j < propertyCount; j++) {
                columnName = daoConfig.properties[j].columnName;
                if (tableColumns.contains(columnName)) {
                    properties.add(columnName);
                    String columnType = getTypeByClass(daoConfig.properties[j].type);
                    sqlStringBuilder.append(divider).append(columnName).append(" ").append(columnType);
                    if (daoConfig.properties[j].primaryKey) {
                        sqlStringBuilder.append(" PRIMARY KEY");
                    }
                    divider = ",";
                }
            }
            sqlStringBuilder.append(");");
            sqlString = sqlStringBuilder.toString();
            KLog.i("=======create temp table" + tableName + " sql string is :" + sqlString);
            db.execSQL(sqlString);
            KLog.i("======create temp table " + tableName + " is ok");

            KLog.i("======insert data to temp table " + tableName + " start");
            sqlStringBuilder.setLength(0);
            sqlStringBuilder.append("INSERT INTO ").append(tempTableName).append(" (");
            sqlStringBuilder.append(TextUtils.join(",", properties));
            sqlStringBuilder.append(") SELECT ");
            sqlStringBuilder.append(TextUtils.join(",", properties));
            sqlStringBuilder.append(" FROM ").append(tableName).append(";");
            sqlString = sqlStringBuilder.toString();
            KLog.i("=======insert temp table" + tableName + " sql string is :" + sqlString);
            db.execSQL(sqlString);
            KLog.i("======insert data to temp table " + tableName + " is ok");
        }
        KLog.i("======all data insert to temp tables is ok");
    }

    public static void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
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
            tempTableName = daoConfig.tablename.concat(TEMP_TABLE_NAME_SUFFIX);
            properties.clear();
            sqlStringBuilder.setLength(0);
            tempProperties = getColumns(db, tempTableName);
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
            KLog.i("=======insert data into table sql string is :" + sqlString);
            db.execSQL(sqlString);
            sqlString = "DROP TABLE  IF EXISTS \"" + tempTableName + "\"";
            KLog.i("=======drop temp table sql string is :" + sqlString);
            db.execSQL(sqlString);
        }
    }

    public static String getTypeByClass(Class<?> type) {
        if (type.equals(String.class)) {
            return "TEXT";
        }
        if (type.equals(Long.class) || type.equals(Integer.class) || type.equals(long.class)) {
            return "INTEGER";
        }
        if (type.equals(Boolean.class)) {
            return "BOOLEAN";
        }
        if (type.equals(byte[].class)) {
            return "BLOB";
        }
        //   如果有其他类型的，请在此添加
        return "TEXT";
    }

    public static List<String> getColumns(Database db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
            if (cursor != null) {
                columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        } catch (Exception e) {
            KLog.i("===table " + tableName + " getColumns exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        KLog.i("===table " + tableName + " getColumns :" + CommonUtils.printList(columns));
        return columns;
    }
}
