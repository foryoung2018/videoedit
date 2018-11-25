package com.wmlive.hhvideo.common.manager;

import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsq on 5/17/2018 - 5:05 PM
 * 类描述：页面之间大量数据传输
 */
public class TransferDataManager {

    private List<ShortVideoItem> dataList = new ArrayList<>(12);

    private List<Banner> bannerList = new ArrayList<>(4);

    private static final class Holder {
        final static TransferDataManager HOLDER = new TransferDataManager();
    }

    public static TransferDataManager get() {
        return TransferDataManager.Holder.HOLDER;
    }


    public synchronized void setVideoListData(List<ShortVideoItem> list) {
        if (dataList == null) {
            dataList = new ArrayList<>(12);
        }
        dataList.clear();
        if (!CollectionUtil.isEmpty(list)) {
            dataList.addAll(list);
        }
    }

    public synchronized List<ShortVideoItem> getVideoListData() {
        if (dataList == null) {
            dataList = new ArrayList<>(12);
        }
        return dataList;
    }

    public synchronized void setBannerListData(List<Banner> list) {
        if (bannerList == null) {
            bannerList = new ArrayList<>(4);
        }
        bannerList.clear();
        if (!CollectionUtil.isEmpty(list)) {
            bannerList.addAll(list);
        }
    }

    public synchronized List<Banner> getBannerListData() {
        if (bannerList == null) {
            bannerList = new ArrayList<>(4);
        }
        return bannerList;
    }
}
