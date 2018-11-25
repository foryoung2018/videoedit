package com.wmlive.hhvideo.common.manager.gift;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftEntity;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftListResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.service.GiftService;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsq on 1/5/2018.5:16 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftPresenter extends BasePresenter<GiftPresenter.IGiftView> {

    public GiftPresenter(IGiftView view) {
        super(view);
    }

    /**
     * 启动App时获取所有的礼物
     */
    public void getAllGiftResource() {
        executeRequest(HttpConstant.TYPE_GET_ALL_GIFT_RESOURCE, getHttpApi().getGiftResource(InitCatchData.getGiftResources()))
                .subscribe(new DCNetObserver<GiftListResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, GiftListResponse response) {
                        KLog.i("========获取初始化礼物资源成功");
                        if (response.user_gold_account != null) {
                            AccountUtil.setUserGoldAccount(response.user_gold_account);
                        }
                        if (viewCallback != null) {
                            if (!CollectionUtil.isEmpty(response.data)) {
                                viewCallback.onGiftListOk(response.data, true, 0);
                            } else {
                                KLog.i("获取到的礼物列表为空");
                                viewCallback.onGiftListFail("获取到的礼物列表为空");
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onGiftListFail(message);
                        }
                    }
                });
    }

    /**
     * 某个作品对应的礼物
     */
    public void getGiftList(long videoId) {
        executeRequest(HttpConstant.TYPE_GET_GIFT_LIST, getHttpApi().getGiftListV2(InitCatchData.getGiftInfoV2(), videoId))
                .subscribe(new DCNetObserver<GiftListResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, GiftListResponse response) {
                        if (response.user_gold_account != null) {
                            AccountUtil.setUserGoldAccount(response.user_gold_account);
                        }
                        if (viewCallback != null) {
                            if (!CollectionUtil.isEmpty(response.data)) {
                                ArrayList<GiftEntity> onlineGift = new ArrayList<>(6);
                                for (GiftEntity giftEntity : response.data) {
                                    if (giftEntity != null && giftEntity.gift_status == 0) {
                                        onlineGift.add(giftEntity);
                                    }
                                }
                                GiftService.checkLocalGift(onlineGift);
                                viewCallback.onGiftListOk(onlineGift, false, videoId);
                            } else {
                                KLog.i("获取到的礼物列表为空");
                                viewCallback.onGiftListFail("获取到的礼物列表为空");
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onGiftListFail(message);
                        }
                    }
                });
    }

    /**
     * 某个IM对应的礼物
     */
    public void getImGiftList(long toUserId) {
        executeRequest(HttpConstant.TYPE_GET_GIFT_LIST, getHttpApi().getImGiftList(InitCatchData.getGiftInfoForIm(), toUserId))
                .subscribe(new DCNetObserver<GiftListResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, GiftListResponse response) {
                        if (response.user_gold_account != null) {
                            AccountUtil.setUserGoldAccount(response.user_gold_account);
                        }
                        if (viewCallback != null) {
                            if (!CollectionUtil.isEmpty(response.data)) {
                                ArrayList<GiftEntity> onlineGift = new ArrayList<>(6);
                                for (GiftEntity giftEntity : response.data) {
                                    if (giftEntity != null && giftEntity.gift_status == 0) {
                                        onlineGift.add(giftEntity);
                                    }
                                }
                                GiftService.checkLocalGift(onlineGift);
                                viewCallback.onGiftListOk(onlineGift, false, toUserId);
                            } else {
                                KLog.i("获取到的礼物列表为空");
                                viewCallback.onGiftListFail("获取到的礼物列表为空");
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onGiftListFail(message);
                        }
                    }
                });
    }

    public interface IGiftView extends BaseView {
        void onGiftListOk(List<GiftEntity> giftEntities, boolean isInit, long giftId);

        void onGiftListFail(String message);
    }
}
