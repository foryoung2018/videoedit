package com.wmlive.hhvideo.heihei.subject.View;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.search.SearchResponse;

/**
 * 话题搜索
 * Created by kangzhen on 2017/6/1.
 */

public interface SubjectSearchView extends BaseView {
    /**
     * 搜索话题的列表信息
     */
    void searchTopicSuccess(SearchResponse searchResponse, String keywrods);

    /**
     * 搜索话题的列表信息(更多)
     *
     * @param searchResponse
     * @param keywrods
     */
    void searchTopicMoreSuccess(SearchResponse searchResponse, String keywrods);
}
