package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/5/22.
 */

public class SearchUrl extends BaseModel {
    /**
     * search : http://api.dongci-test.wmlives.com/api/search
     */

    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "SearchUrl{" +
                "search='" + search + '\'' +
                '}';
    }
}
