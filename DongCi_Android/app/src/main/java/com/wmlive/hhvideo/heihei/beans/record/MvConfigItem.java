package com.wmlive.hhvideo.heihei.beans.record;

/**
 * 每一个录制的 视频素材 实体类
 */
public class MvConfigItem {

    public String tips;

    public int res;

    public int state = 2;//0 未下载 1 下载中 2 下载完成

    public boolean isStateOk(){
        return state == 2;
    }

    public boolean isStateNo(){
        return state == 0;
    }

    public int getState(){
        return state;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }





}
