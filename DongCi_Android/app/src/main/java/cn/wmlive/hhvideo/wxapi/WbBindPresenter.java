package cn.wmlive.hhvideo.wxapi;

import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.WeiboBindEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 1/12/2018.12:08 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class WbBindPresenter extends WbPresenter<WbBindPresenter.IWbBindView> {

    public WbBindPresenter(IWbBindView view) {
        super(view);
    }

    public void bindWeibo(String token, String weiboId) {
        executeRequest(HttpConstant.TYPE_BIND_WEIBO, getHttpApi().bindWeibo(InitCatchData.bindWeibo(), token, weiboId))
                .subscribe(new DCNetObserver<WeiboBindEntity>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, WeiboBindEntity response) {
                        if (viewCallback != null) {
                            viewCallback.onBindOk(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(requestCode, message);
                    }
                });
    }

    public void unBindWeibo() {
        executeRequest(HttpConstant.TYPE_UNBIND_WEIBO, getHttpApi().unbindWeibo(InitCatchData.unBindWeibo(), AccountUtil.getToken()))
                .subscribe(new DCNetObserver<BaseResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onUnBindOk();
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(requestCode, message);
                    }
                });
    }

    public interface IWbBindView extends WbPresenter.IWeiboView {
        void onBindOk(WeiboBindEntity entity);

        void onUnBindOk();
    }
}
