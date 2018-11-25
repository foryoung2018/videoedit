package cn.wmlive.hhvideo.wxapi;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsq on 12/28/2017.2:58 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class WbPresenter<V extends WbPresenter.IWeiboView> extends BasePresenter<V> {

    public WbPresenter(V view) {
        super(view);
    }

    public void weiboLogin(String token, String uid) {
        executeRequest(HttpConstant.TYPE_LOGIN_SINA, getHttpApi().signIn(InitCatchData.userSignIn(),
                "Weibo", token, uid, null))
                .subscribe(new DCNetObserver<LoginUserResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, LoginUserResponse response) {
                        if (null != viewCallback) {
                            viewCallback.onWeiboOk(1, response);
                        }
                        KLog.i("======微博登录成功");
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.onWeiboFail(2, message);
                        }
                        KLog.i("======微博登录失败：" + message);
                    }
                });
    }

    public interface IWeiboView extends BaseView {
        void onWeiboOk(int type, LoginUserResponse response);

        void onWeiboFail(int type, String message);
    }

}
