package com.wmlive.hhvideo.heihei.db;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.manager.greendao.GreenDaoManager;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.SdkUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class ProductDbManager {

    /**
     * 更新或插入一个作品
     *
     * @param productEntity
     */
    public static void insertOrUpdateProductToDb(ProductEntity productEntity) {
        if (productEntity != null) {
            productEntity.refreshModifyTime();
            GreenDaoManager.get().getProductEntityDao().insertOrReplace(productEntity);
            KLog.i("======更新数据库信息productEntity:" + productEntity.toString());
        } else {
            KLog.i("======没有数据更新");
        }
    }

    /**
     * 删除一个作品
     *
     * @param entity
     */
    public static void deleteProduct(ProductEntity entity, boolean deleteFiles) {
        if (entity != null && entity.getId() != null) {
            FileUtil.deleteAll(entity.baseDir, deleteFiles);
            KLog.i("=====从本地删除作品id:" + entity.getId());
            GreenDaoManager.get().getProductEntityDao().delete(entity);
        } else {
            KLog.i("=====从本地删除作品不正确:" + entity);
        }
    }

    /**
     * 移到草稿
     */
    public static boolean moveToDraft(ProductEntity productEntity) {
        if (productEntity != null && productEntity.moveToDraft()) {
            insertOrUpdateProductToDb(productEntity);
            return true;
        }
        return false;
    }

    /**
     * 移动到发布状态
     *
     * @param productEntity
     */
    public static void moveToPublishing(ProductEntity productEntity) {
        if (productEntity != null) {
            productEntity.productType = ProductEntity.TYPE_PUBLISHING;
            insertOrUpdateProductToDb(productEntity);
        }
    }

    /**
     * 查询最近的草稿
     *
     * @return
     */
    public static ProductEntity queryLatestDraft() {
        QueryBuilder<ProductEntity> queryBuilder = GreenDaoManager.get().getProductEntityDao().queryBuilder();
        queryBuilder.where(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.UserId.eq(AccountUtil.getUserId()),
                queryBuilder.or(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_EDITING),
                        com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_DRAFT),
                        com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_PUBLISHING)));
        List<ProductEntity> list = queryBuilder
                .orderDesc(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ModifyTime)
                .limit(1)
                .list();
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 查询所有未发布作品
     *
     * @return
     */
    public static List<ProductEntity> queryAllDraft() {
        QueryBuilder<ProductEntity> queryBuilder = GreenDaoManager.get().getProductEntityDao().queryBuilder();
        queryBuilder.where(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.UserId.eq(AccountUtil.getUserId()),
                queryBuilder.or(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_EDITING),
                        com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_DRAFT),
                        com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_PUBLISHING)));
        return queryBuilder
                .orderDesc(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ModifyTime)
                .list();
    }
    //
    public static ProductEntity queryUnfinishedProduct() {
        QueryBuilder<ProductEntity> queryBuilder = GreenDaoManager.get().getProductEntityDao().queryBuilder();
        queryBuilder.where(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.UserId.eq(AccountUtil.getUserId()));
        queryBuilder.whereOr(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_EDITING),
                com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ProductType.eq(ProductEntity.TYPE_PUBLISHING));
        List<ProductEntity> list = queryBuilder
                .orderDesc(com.wmlive.hhvideo.heihei.db.ProductEntityDao.Properties.ModifyTime)
                .limit(1)
                .list();
        return list.size() > 0 ? list.get(0) : null;
    }
    //
//
    public static int calculateProgress(long current, long total, float scale) {
        return (int) (current * 1f / (total == 0 ? 1 : total) * scale);
    }
    //
    public static boolean hasHeadset() {
        AudioManager audioManager = (AudioManager) DCApplication.getDCApp().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            if (SdkUtils.isMarshmallow()) {
                AudioDeviceInfo[] audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
                for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfos) {
                    if (audioDeviceInfo != null) {
                        KLog.e("hasHeadset getType:" + audioDeviceInfo.getType());
                        if (audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
                                || audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                                || audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                                || audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_USB_HEADSET) {
                            return true;
                        }
                    }
                }
                return false;
            } else {
                return audioManager.isWiredHeadsetOn();
            }
        }
        return false;
    }
}
