package com.wmlive.hhvideo.heihei.mainhome.view;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;

import java.util.List;

/**
 * Created by vhawk on 2017/6/1.
 * Modify by lsq
 */

public interface ExplosionView extends BaseView {

    /**
     * 下拉最新列表成功
     *
     * @param shortVideoInfoList
     * @param hasMore
     */
    void handleExplosionSucceed(boolean isRefresh, List<ShortVideoItem> shortVideoInfoList, boolean hasMore);

}
