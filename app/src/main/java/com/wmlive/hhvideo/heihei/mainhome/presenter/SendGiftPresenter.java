package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.gifts.SendGiftResultResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 1/15/2018.12:15 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class SendGiftPresenter extends BasePresenter<SendGiftPresenter.ISendGiftView> {


    public SendGiftPresenter(ISendGiftView view) {
        super(view);
    }

    //送礼物
    public void sendGift(final int position, final long videoId, final String giftId, final String count) {
        executeRequest(HttpConstant.TYPE_SEND_GIFT, getHttpApi().sendGiftV2(InitCatchData.buyGiftBatch(), videoId, giftId, count))
                .subscribe(new DCNetObserver<SendGiftResultResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, SendGiftResultResponse response) {
                        if (response.user_gold_account != null) {
                            AccountUtil.setUserGoldAccount(response.user_gold_account);
                        }
                        if (null != viewCallback) {
                            viewCallback.onSendGiftOk(position, videoId, giftId, response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onSendGiftFail(position, videoId, giftId, message);
                        }
                    }
                });
    }

    // IM用户送礼物
    public void sendGift(final long userId, final String giftId, final String count) {
        executeRequest(HttpConstant.TYPE_IM_BUY_GIFT, getHttpApi().buyIMGift(InitCatchData.getImBuyGiftBatch(), userId, giftId, count))
                .subscribe(new DCNetObserver<SendGiftResultResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, SendGiftResultResponse response) {
                        if (response.user_gold_account != null) {
                            AccountUtil.setUserGoldAccount(response.user_gold_account);
                        }
                        if (null != viewCallback) {
                            viewCallback.onSendGiftOk(0, userId, giftId, response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onSendGiftFail(0, userId, giftId, message);
                        }
                    }
                });
    }

    public interface ISendGiftView extends BaseView {
        void onSendGiftOk(int position, long videoId, String giftId, SendGiftResultResponse sendGiftResultResponse);

        void onSendGiftFail(int position, long videoId, String giftId, String message);
    }
}
