package com.wmlive.hhvideo.heihei.login;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.LogFileManager;
import com.wmlive.hhvideo.common.network.DCRequest;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountEntity;
import com.wmlive.hhvideo.service.DcWebSocketService;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.networklib.util.EventHelper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 账号管理类
 * Created by vhawk on 2017/5/23.
 */

public class AccountUtil {
    private static final String KEY_ACCOUNT_DATA = "ACCOUNT_DATA";
    private static LoginUserResponse loginUserInfo = null;
    private static String token;
    private static long userId;
    private static UserAccountEntity userGoldAccount;

    public static final String TYPE_MOBILE = "mobile";
    public static final String TYPE_WX = "wx";
    public static final String TYPE_WB = "wb";
    public static final String TYPE_OTHER = "other";

    private static final String KEY_TOKEN = "dc_token";  //token的key
    private static final String KEY_USER_ID = "dc_user_id";    //userId的key

    private static boolean hasLoadToken;//是否从本地加载过token

    /**
     * 从本地文件初始化用户信息
     */
    private static void initUserInfo() {
        KLog.i("获取本地账号信息");
        loginUserInfo = JsonUtils.parseObject(SPUtils.getString(DCApplication.getDCApp(), KEY_ACCOUNT_DATA, ""), LoginUserResponse.class);
        if (loginUserInfo != null) {
            token = loginUserInfo.getToken();
            userId = loginUserInfo.getUser_info() != null ? loginUserInfo.getUser_info().getId() : 0;
        }
        KLog.i("初始化后的账号信息：" + (loginUserInfo == null ? "null" : loginUserInfo.toString()));
    }

    /**
     * 清除账号信息
     */
    public synchronized static void clearAccount() {
        loginUserInfo = null;
        token = null;
        userId = 0;
        Map<String, Object> map = new HashMap<>(2);
        map.put(KEY_TOKEN, token);
        map.put(KEY_USER_ID, userId);
        saveLocalTokenAndUserId(token, userId);
        SPUtils.putString(DCApplication.getDCApp(), KEY_ACCOUNT_DATA, "");
        DCRequest.getRetrofit().clearCookie();
    }

    /**
     * 保存用户登录结果
     *
     * @param userResponse
     */
    public synchronized static void resetAccount(LoginUserResponse userResponse) {
        hasLoadToken = false;
        if (userResponse != null) {
            loginUserInfo = userResponse;
            token = loginUserInfo.getToken();
            userId = loginUserInfo.getUser_info() != null ? loginUserInfo.getUser_info().getId() : 0;

            String info = JSON.toJSONString(userResponse);
            KLog.i("======updateUserInfo:" + info);
            SPUtils.putString(DCApplication.getDCApp(), KEY_ACCOUNT_DATA, info);
            saveLocalTokenAndUserId(userResponse.getToken(), userResponse.getUser_info() != null ? userResponse.getUser_info().getId() : 0);
        } else {
            clearAccount();
        }
    }

    /**
     * 保存token和userId到本地
     *
     * @param token
     */
    private synchronized static void saveLocalTokenAndUserId(final String token, final long userId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put(KEY_TOKEN, TextUtils.isEmpty(token) ? "null" : token);
        map.put(KEY_USER_ID, userId);
        SPUtils.putMultiParmas(DCApplication.getDCApp(), map);
        KLog.i("====保存到本地token:" + token + " _userId:" + userId);
    }

    /**
     * 获取存储 token
     *
     * @return 本地有token 则原样返回 否则返回 null
     */
    public static String getToken() {
        if (!TextUtils.isEmpty(token) && !"null".equals(token)) {
            KLog.i("====返回内存token:" + token);
            return token;
        }
        if (!hasLoadToken) {//如果没有从本地加载过token
            KLog.i("=======获取本地token和userId1");
            token = SPUtils.getString(DCApplication.getDCApp(), KEY_TOKEN, "null");
            userId = SPUtils.getLong(DCApplication.getDCApp(), KEY_USER_ID, 0);
            KLog.i("=======获取本地token和userId2");
            if (TextUtils.isEmpty(token) || "null".equals(token)) {
                KLog.i("====返回本地token为空:" + token);
                userId = 0L;
                return null;
            }
            KLog.i("====返回本地token:" + token);
            hasLoadToken = true;
        }
        return token;
    }

    /**
     * 获取userId
     *
     * @return
     */
    public static long getUserId() {
        KLog.d("userId=="+userId);
        return userId;
    }

    /**
     * 是当前登录用户
     *
     * @param id
     * @return
     */
    public static boolean isLoginUser(long id) {
        return id > 0 && id == userId;
    }

    /**
     * 获取dongci ID
     *
     * @return
     */
    public static String getDcNum() {
        if (getUserInfo() != null) {
            return getUserInfo().getDc_num();
        } else {
            return "";
        }
    }

    /**
     * 获取
     *
     * @return
     */
    public synchronized static LoginUserResponse getLoginUserInfo() {
        if (null == loginUserInfo) {
            initUserInfo();
        }
        return loginUserInfo;
    }

    /**
     * 是否已登录
     *
     * @return
     */
    public static boolean isLogin() {
        return userId > 0 && !TextUtils.isEmpty(getToken());
    }

    /**
     * 是否需要邀请码验证
     *
     * @return
     */
    public static boolean needVerifyCode() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null && userInfo.invite_verify == 0;
    }


    /**
     * 获取当前用户信息
     *
     * @return
     */
    public static UserInfo getUserInfo() {
        return getLoginUserInfo() == null ? null : getLoginUserInfo().getUser_info();
    }

    /**
     * 获取 是否有管理作品的权限
     *
     * @return
     */
    public static boolean isAuthUser() {
        if (null != loginUserInfo) {
            return loginUserInfo.getUser_info() != null && loginUserInfo.getUser_info().getIs_auth_user();
        }
        return false;
    }

    //获取钻数量
    public static int getGoldCount() {
        return userGoldAccount == null ? 0 : userGoldAccount.getGold();
    }

    public static void setUserGoldCount(int count) {
        if (userGoldAccount == null) {
            userGoldAccount = new UserAccountEntity();
        }
        userGoldAccount.setGold(count);
    }

    public static void setUserGoldAccount(UserAccountEntity userAccountEntity) {
        userGoldAccount = userAccountEntity;
    }

    public static void loginSuccess(LoginUserResponse response, String type) {
        AccountUtil.resetAccount(response);
        Observable.just(1)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        EventHelper.post(GlobalParams.EventType.TYPE_LOGIN_OK);
                        KLog.i("====loginSuccess登录成功");
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        KLog.i("====loginSuccess 启动DcWebSocketService");
                        DcWebSocketService.startSocket(DCApplication.getDCApp(), 1500);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showToast("登录发生错误");
                        LogFileManager.getInstance().saveLogInfo("AccountUtil.loginSuccess", "登录发生错误:" + throwable.getMessage());
                    }
                });

    }

}
