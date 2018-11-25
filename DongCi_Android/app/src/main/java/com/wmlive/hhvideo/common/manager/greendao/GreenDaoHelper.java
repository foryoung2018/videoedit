package com.wmlive.hhvideo.common.manager.greendao;

import android.content.Context;

import com.wmlive.hhvideo.heihei.db.DaoMaster;
import com.wmlive.hhvideo.heihei.db.ProductEntityDao;
import com.wmlive.hhvideo.utils.KLog;

import org.greenrobot.greendao.database.Database;

/**
 * 数据库升级工具类
 */

public class GreenDaoHelper extends DaoMaster.OpenHelper {
    public GreenDaoHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
        DaoMaster.createAllTables(db, true);
        KLog.i("=====GreenDaoHelper onCreate:");
    }

    /**
     * 升级步骤参考类{@link MigrationHelper}
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        DaoMaster.createAllTables(db, true);
        for (int currentVersion = oldVersion; currentVersion < newVersion; currentVersion++) {
            KLog.e("====数据库开始从版本" + currentVersion + "升级到版本" + (currentVersion + 1));
            if (oldVersion == 5)
                Migration5to6.migrate5to6(db, ProductEntityDao.class);
            if (oldVersion == 6) {
                Migration6to7.migrate6to7(db);
            }
            if (oldVersion == 7) {
                MigrationHelper.migrate(db, ProductEntityDao.class);
            }
            KLog.e("====数据库开始从版本" + currentVersion + "升级到版本" + (currentVersion + 1) + "成功");
        }
    }

}
