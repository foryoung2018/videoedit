package com.wmlive.hhvideo.heihei.message.utils;

import com.wmlive.hhvideo.common.network.ApiService;
import com.wmlive.hhvideo.common.network.DCRequest;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.immessage.IMMessageResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.IMNetworkObserver;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by hsing on 2018/3/12.
 */

public class IMNetUtils {

    private static final class Holder {
        static final IMNetUtils INSTANCE = new IMNetUtils();
    }

    public static IMNetUtils get() {
        return Holder.INSTANCE;
    }

    /**
     * 发送IM 信息
     * @param imResponse
     * @param toUserId
     * @param msyType
     * @param localMsgId
     * @param content
     */
    public void sendIMMessage(IMResponse imResponse, long toUserId, String msyType, String localMsgId, String content) {
        executeRequest(HttpConstant.TYPE_IM_SEND_MESSAGE, getHttpApi().sendIMMessage(InitCatchData.getSendImMessage(), toUserId, msyType, localMsgId, content))
                .subscribe(new IMNetworkObserver<IMMessageResponse>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, IMMessageResponse response) {
                        if (imResponse != null) {
                            imResponse.onSendIMMessage(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message, IMMessageResponse response) {
//                        ToastUtil.showToast(message);
                        if (imResponse != null) {
                            imResponse.onRequestDataError(HttpConstant.TYPE_IM_SEND_MESSAGE, serverCode, message, response);
                        }
                    }

                });
    }


    /**
     * 发送IM 文本
     * @param imResponse
     * @param toUserId
     * @param localMsgId
     * @param content
     */
    public void sendMMessageText(IMResponse imResponse, long toUserId, String localMsgId, String content) {
        KLog.e("im_request", "toUserId:" + toUserId + "<>localMsgId:" + localMsgId + "<>strContent:" + content);
        sendIMMessage(imResponse, toUserId, "text", localMsgId, content);
    }

    /**
     * 发送IM 语音
     * @param imResponse
     * @param toUserId
     * @param localMsgId
     * @param content
     */
    public void sendMMessageAudio(IMResponse imResponse, long toUserId, String localMsgId, String content) {
        KLog.e("im_request", "toUserId:" + toUserId + "<>localMsgId:" + localMsgId + "<>strContent:" + content);
        sendIMMessage(imResponse, toUserId, "audio", localMsgId, content);
    }

    /**
     * 发送IM 图片
     * @param imResponse
     * @param toUserId
     * @param localMsgId
     * @param content
     */
    public void sendMMessageImage(IMResponse imResponse, long toUserId, String localMsgId, String content) {
        KLog.e("im_request", "toUserId:" + toUserId + "<>localMsgId:" + localMsgId + "<>strContent:" + content);
        sendIMMessage(imResponse, toUserId, "image", localMsgId, content);
    }

    public <T extends BaseResponse> Observable<Response<T>> executeRequest(int requestCode, Observable<Response<T>> observable) {
        return DCRequest.getRetrofit().getObservable(null, requestCode, observable, null);
    }

    /**
     * 获取一个网络请求的Call或者Observable
     *
     * @return HttpApi
     */
    public ApiService getHttpApi() {
        return DCRequest.getHttpApi();
    }

    public interface IMResponse {
        void onSendIMMessage(IMMessageResponse response);

        void onRequestDataError(int requestCode, int serverCode, String message, IMMessageResponse response);
    }
}
