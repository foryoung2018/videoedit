package com.wmlive.hhvideo.common.base;

import java.io.Serializable;

/**
 * Created by vhawk on 2017/5/22.
 */

public class BaseModel implements Serializable {
    public int type;

    public BaseModel() {

    }

    public BaseModel(int type) {
        this.type = type;
    }
}
