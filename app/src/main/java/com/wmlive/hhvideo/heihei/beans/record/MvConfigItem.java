package com.wmlive.hhvideo.heihei.beans.record;

/**
 * 每一个录制的 视频素材 实体类
 */
public class MvConfigItem {

    public final static int STATE_DEFAULT = 0;
    public final static int STATE_RECORDING = 1;
    public final static int STATE_RECORD_END = 2;
    public final static int STATE_DONWLOAD_PPE = 3;
    public final static int STATE_DONWLOADING = 4;
    public final static int STATE_DONWLOAD_FINISH = 5;
    public final static int STATE_DONWLOAD_ERROR = 6;

    public static int STATE_PLAYING = 7;//正在播放
    public String tips;

    public int res;



    /**
     * 0 默认状态，未录制，1 录制中 2 录制完成
     * 3 未下载，4，下载中 ，5 下载完成
     */
    private int state = 0;
    //
    //

    public boolean isStateOk(){
        return state == 2;
    }

    public boolean isStateNo(){
        return state == 0;
    }

    public void setState(int state){
        this.state = state;
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

    public boolean hasVideo(){
        return state==STATE_DEFAULT || state==STATE_RECORD_END || state == STATE_DONWLOAD_FINISH;
    }



}
