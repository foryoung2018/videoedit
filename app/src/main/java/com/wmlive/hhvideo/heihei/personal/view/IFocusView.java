package com.wmlive.hhvideo.heihei.personal.view;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.personal.ListFollowerFansResponse;

/**
 * Created by XueFei on 2017/6/1.
 * <p>
 * 关注
 */

public interface IFocusView extends BaseView {

    /**
     * 获取关注列表
     *
     * @param response
     */
    void onFocusListOk(boolean isRefresh, ListFollowerFansResponse response, boolean hasMore);

    void onFocusFail(boolean isRefresh, String message);


}
