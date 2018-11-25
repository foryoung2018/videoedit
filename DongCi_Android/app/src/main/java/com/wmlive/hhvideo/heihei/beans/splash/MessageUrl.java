package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * init message 接口
 * Created by kangzhen on 2017/7/6.
 */

public class MessageUrl extends BaseModel {
    /**
     * webSocketServer=ws://socket.dongci-test.wmlives.com/ws/channel
     * ImBanner=http://api.dongci-test.wmlives.com/api/message/banner
     * ImBuyGiftBatch=http://api.dongci-test.wmlives.com/api/message/buy-gift-batch
     * sendImMessage=http://api.dongci-test.wmlives.com/api/message/send
     */

    private String webSocketServer;
    private String sendImMessage;
    private String ImBuyGiftBatch;
    private String ImBanner;
    private String ImFollowerSearch;

    public MessageUrl() {
    }

    public String getWebSocketServer() {
        return webSocketServer;
    }

    public void setWebSocketServer(String webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    public String getSendImMessage() {
        return sendImMessage;
    }

    public void setSendImMessage(String sendImMessage) {
        this.sendImMessage = sendImMessage;
    }

    public String getImBuyGiftBatch() {
        return ImBuyGiftBatch;
    }

    public void setImBuyGiftBatch(String imBuyGiftBatch) {
        ImBuyGiftBatch = imBuyGiftBatch;
    }

    public String getImBanner() {
        return ImBanner;
    }

    public void setImBanner(String imBanner) {
        ImBanner = imBanner;
    }

    public String getImFollowerSearch() {
        return ImFollowerSearch;
    }

    public void setImFollowerSearch(String imFollowerSearch) {
        ImFollowerSearch = imFollowerSearch;
    }

    @Override
    public String toString() {
        return "MessageUrl{" +
                "webSocketServer='" + webSocketServer + '\'' +
                ", sendImMessage='" + sendImMessage + '\'' +
                ", ImBuyGiftBatch='" + ImBuyGiftBatch + '\'' +
                ", ImBanner='" + ImBanner + '\'' +
                '}';
    }
}
