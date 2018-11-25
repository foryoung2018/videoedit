package com.wmlive.hhvideo.heihei.beans.record;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by wenlu on 2017/9/7.
 */

public class EditVideoModel extends BaseModel {

    public Object data;
    public int index;

    public EditVideoModel(int type, Object data) {
        super(type);
        this.data = data;
        this.index = 0;
    }

    public EditVideoModel(int type, Object data, int index) {
        super(type);
        this.data = data;
        this.index = index;
    }
}
