package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.BlockUserResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by Administrator on 3/22/2018.
 */

public class BlockUserPresenter extends BasePresenter<BlockUserPresenter.IBlockUserView> {

    public BlockUserPresenter(IBlockUserView view) {
        super(view);
    }

    /**
     * 拉黑用户
     *
     * @param userId
     * @param isUserBlock 1:取消拉黑，0：拉黑
     */
    public void blockUser(int position, long userId, boolean isUserBlock) {
        executeRequest(HttpConstant.TYPE_PERSONAL_BLOCK, getHttpApi().blockUser(InitCatchData.blockUser(), userId, isUserBlock ? 1 : 0))
                .subscribe(new DCNetObserver<BlockUserResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BlockUserResponse response) {
                        if (null != viewCallback) {
                            viewCallback.onGetBlockUserOk(position, userId, response.is_block);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_PERSONAL_BLOCK, message);
                    }
                });
    }

    public interface IBlockUserView extends BaseView {
        void onGetBlockUserOk(int position, long userId, boolean isBlock);
    }
}
