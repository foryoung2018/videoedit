package com.wmlive.hhvideo.heihei.personal.view;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.personal.ListFollowerFansResponse;

/**
 * Created by XueFei on 2017/6/1.
 * <p>
 * 粉丝
 */

public interface IFansView extends BaseView {

    /**
     * 获取粉丝列表
     *
     * @param response
     */
    void onFansListOk(boolean isRefresh, ListFollowerFansResponse response, boolean hasMore);

    void onFansListFail(boolean isRefresh, String message);

}
