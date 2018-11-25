package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/5/22.
 */

public class Function extends BaseModel {

    /**
     * chat : true
     * hhlog : true
     */

    private boolean chat;
    private boolean hhlog;

    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }

    public boolean isHhlog() {
        return hhlog;
    }

    public void setHhlog(boolean hhlog) {
        this.hhlog = hhlog;
    }

    @Override
    public String toString() {
        return "Function{" +
                "chat=" + chat +
                ", hhlog=" + hhlog +
                '}';
    }
}
