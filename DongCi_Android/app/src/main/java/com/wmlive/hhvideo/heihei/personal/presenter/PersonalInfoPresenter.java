package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.UpdateUserResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.personal.view.IPersonalInfoView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by XueFei on 2017/6/1.
 * <p>
 * 个人信息页
 */

public class PersonalInfoPresenter extends BasePresenter<IPersonalInfoView> {
    public PersonalInfoPresenter(IPersonalInfoView view) {
        super(view);
    }

    /**
     * 更新用户
     *
     * @param name
     * @param gender
     * @param birth_day
     * @param region
     * @param description
     * @param cover_ori
     * @param cover_ori_file_sign
     */
    public void updateUser(String name, String gender, String birth_day, String region, String description, String cover_ori, String cover_ori_file_sign) {
        executeRequest(HttpConstant.TYPE_PERSONAL_UPDATE_USER, getHttpApi().updateUser(InitCatchData.userUpdateUser(), name, gender, birth_day, region, description, cover_ori, cover_ori_file_sign))
                .subscribe(new DCNetObserver<UpdateUserResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, UpdateUserResponse response) {
                        if (viewCallback != null) {
                            if (response != null && response.getUser_info() != null) {
                                AccountUtil.getLoginUserInfo().setUser_info(response.getUser_info());
                                AccountUtil.resetAccount(AccountUtil.getLoginUserInfo());
                                viewCallback.handleInfoSucceed();
                            } else {
                                viewCallback.handleInfoFailure("保存失败");
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.handleInfoFailure(message);
                        }
                    }
                });
    }
}
