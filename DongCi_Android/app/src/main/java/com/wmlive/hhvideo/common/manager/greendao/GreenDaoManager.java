package com.wmlive.hhvideo.common.manager.greendao;

import android.content.Context;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.db.ConversationDao;
import com.wmlive.hhvideo.heihei.db.DaoMaster;
import com.wmlive.hhvideo.heihei.db.DaoSession;
import com.wmlive.hhvideo.heihei.db.MessageDetailDao;
import com.wmlive.hhvideo.heihei.db.ProductEntityDao;

/**
 * Created by lsq on 9/16/2017.
 * 所有的数据表管理类
 */

public class GreenDaoManager {
    private DaoSession daoSession;

    private static final class HOLDER {
        static final GreenDaoManager INSTANCE = new GreenDaoManager();
    }

    public static GreenDaoManager get() {
        return HOLDER.INSTANCE;
    }

    public void init(Context context) {
        if (daoSession == null) {
            GreenDaoHelper daoHelper = new GreenDaoHelper(context.getApplicationContext(), GlobalParams.Config.GREENDAO_DB_NAME);
            DaoMaster daoMaster = new DaoMaster(daoHelper.getWritableDatabase());
            daoSession = daoMaster.newSession();
        }
    }

    public ProductEntityDao getProductEntityDao() {
        init(DCApplication.getDCApp());
        return daoSession.getProductEntityDao();
    }

    public MessageDetailDao getMessageDetailDao() {
        init(DCApplication.getDCApp());
        return daoSession.getMessageDetailDao();
    }

    public ConversationDao getConversationDao() {
        init(DCApplication.getDCApp());
        return daoSession.getConversationDao();
    }
}
