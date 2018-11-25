package com.wmlive.hhvideo.heihei.mainhome.util;

import android.graphics.Point;
import android.text.TextUtils;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.main.DanmuEntity;
import com.wmlive.hhvideo.heihei.beans.main.DcDanmaEntity;
import com.wmlive.hhvideo.heihei.beans.main.DcDanmaWrapEntity;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.BuildConfig;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.DanmakuFactory;

/**
 * Created by lsq on 7/26/2017.
 * 弹幕生成工具类
 */

public class DanmakuUtil {
    public static final String TAG = "Util";

    public static String create(List<DanmuEntity> danmuEntities) {
        if (danmuEntities != null && danmuEntities.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder(1000);
            stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                    .append("<i><chatserver>chat.bilibili.com</chatserver><chatid>8729348</chatid><mission>0</mission><maxlimit>1000</maxlimit><source>k-v</source>");
            for (DanmuEntity entity : danmuEntities) {
                if (entity != null && !TextUtils.isEmpty(entity.text)) {
                    stringBuilder.append(entity.createDanmuString());
                }
            }
            stringBuilder.append("</i>");
            return stringBuilder.toString();
        }
        return null;
    }

    public static String createTestData() {
        List<DanmuEntity> entityList = new ArrayList<>();
        DanmuEntity entity;
        float startTime = 3;
        for (int i = 0; i < 1; i++) {
            entity = new DanmuEntity(startTime, i, "这是弹幕" + i);
            entityList.add(entity);
            startTime += (i % 5 == 0 ? 2 : 0.2);   //每隔2秒
        }
        return create(entityList);
    }

    /**
     * 添加多条弹幕
     *
     * @param danmakuView
     * @param danmaEntityList
     * @param danmakuFactory
     */
    public static void createDanmaEntity(IDanmakuView danmakuView, List<DcDanmaEntity> danmaEntityList, DanmakuFactory danmakuFactory, DanmakuContext context) {
        if (!CollectionUtil.isEmpty(danmaEntityList)) {
            BaseDanmaku danmaku;
            DcDanmaWrapEntity dcDanmaWrapEntity;
            DcDanmaEntity dcDanmaEntity;
            for (int i = 0, n = danmaEntityList.size(); i < n; i++) {
                danmaku = danmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL, context);
                if (danmaku == null || danmakuView == null) {
                    return;
                }
                dcDanmaEntity = danmaEntityList.get(i);
                if (dcDanmaEntity != null) {
                    dcDanmaWrapEntity = new DcDanmaWrapEntity();
                    dcDanmaWrapEntity.dcDanmaEntity = dcDanmaEntity;
                    //danmaku.setTime(danmakuView.getCurrentTime() + (i % 5 == 0 ? 600 : 1100) * i);
                    danmaku.setTime(danmakuView.getCurrentTime() + (i / 7) * 5000 + (i % 7) * 800);
                    if (dcDanmaWrapEntity.dcDanmaEntity.title != null && dcDanmaWrapEntity.dcDanmaEntity.title.length() > GlobalParams.DanmuConfig.DANMU_TITLE_LENGTH) {
                        danmaku.setDuration(new Duration((long) (DanmakuFactory.COMMON_DANMAKU_DURATION * (dcDanmaWrapEntity.dcDanmaEntity.title.length() / GlobalParams.DanmuConfig.DANMU_TITLE_DURATION_BASE))));
                    }
                    danmaku.setTag(dcDanmaWrapEntity);
                    danmakuView.addDanmaku(danmaku);
                    KLog.i("======添加一条弹幕:" + dcDanmaWrapEntity.dcDanmaEntity.title);
                }
            }
        }
    }

    /**
     * 添加一条弹幕
     *
     * @param danmakuView
     * @param danmaEntity
     * @param danmakuFactory
     */
    public static void createDanmaEntity(IDanmakuView danmakuView, DcDanmaEntity danmaEntity, DanmakuFactory danmakuFactory) {
        BaseDanmaku danmaku = danmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || danmakuView == null) {
            return;
        }
        if (danmaEntity != null) {
            DcDanmaWrapEntity dcDanmaWrapEntity = new DcDanmaWrapEntity();
            dcDanmaWrapEntity.dcDanmaEntity = danmaEntity;
            danmaku.priority = 1;
            danmaku.setTime(danmakuView.getCurrentTime() + 500);
            if (dcDanmaWrapEntity.dcDanmaEntity.title != null && dcDanmaWrapEntity.dcDanmaEntity.title.length() > GlobalParams.DanmuConfig.DANMU_TITLE_LENGTH) {
                danmaku.setDuration(new Duration((long) (DanmakuFactory.COMMON_DANMAKU_DURATION * (dcDanmaWrapEntity.dcDanmaEntity.title.length() / GlobalParams.DanmuConfig.DANMU_TITLE_DURATION_BASE))));
            }
            danmaku.setTag(dcDanmaWrapEntity);
            danmakuView.addDanmaku(danmaku);
            KLog.i("======添加一条弹幕");
        }
    }


    /**
     * 创建点，按照菱形错开
     *
     * @param limitWidth  宽度范围
     * @param limitHeight 高度范围
     * @param diameter    正方形边长
     * @return
     */
    public static List<Point> createRandomPoint(int limitWidth, int limitHeight, int diameter) {
        return createRandomPoint(limitWidth, limitHeight, 0, 0, diameter);
    }

    /**
     * 创建点，按照菱形错开
     *
     * @param limitWidth  宽度范围
     * @param limitHeight 高度范围
     * @param offsetX     水平偏移
     * @param offsetY     垂直偏移
     * @param diameter    正方形边长
     * @return
     */
    public static List<Point> createRandomPoint(int limitWidth, int limitHeight, int offsetX, int offsetY, int diameter) {
        List<Point> points = new ArrayList<>();
        Point point;
        for (int i = 0, h = (int) Math.floor(limitHeight * 1.0 / diameter); i < h; i++) {
            if (((i + 1) * diameter + offsetY) > limitHeight) {
                //高度超出范围
                break;
            }
            for (int j = 0, w = (int) Math.floor(limitWidth * 1.0 / diameter); j < w; j++) {
                if (i % 2 == 0) {
                    if (((j + 1) * diameter + offsetX) > limitWidth) {
                        //宽度超出范围
                        break;
                    }
                    point = new Point(j * diameter + diameter / 2 + offsetX, i * diameter + diameter / 2 + offsetY);
                    points.add(point);
                } else {  //这里错开diameter/2
                    if (((j + 1.5) * diameter + offsetX) > limitWidth) {
                        //宽度超出范围
                        break;
                    }
                    point = new Point((j + 1) * diameter + offsetX, i * diameter + diameter / 2 + offsetY);
                    points.add(point);
                }
            }
        }
        if (BuildConfig.DEBUG_SWITCH) {
            for (Point point1 : points) {
                KLog.i("生成的点：" + point1.toString());
            }
        }
        return points;
    }

}
