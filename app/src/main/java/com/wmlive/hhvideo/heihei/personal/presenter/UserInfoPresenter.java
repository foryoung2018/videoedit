package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.personal.ListLikeResponse;
import com.wmlive.hhvideo.heihei.beans.personal.ReportTypeResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.personal.widget.ProductTypePanel;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by lsq on 10/12/2017.
 */

public class UserInfoPresenter extends BaseUserInfoPresenter<UserInfoPresenter.IUserInfoView> {

    private int offset = 0;

    public UserInfoPresenter(IUserInfoView view) {
        super(view);
    }

    public int getUserOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * 获取作品列表
     *
     * @param user_id
     */
    public void getProductList(final boolean isRefresh, final int type, long user_id) {
        Observable<Response<ListLikeResponse>> observable = null;
        offset = isRefresh ? 0 : offset;
        if (type == ProductTypePanel.TYPE_LIKE) {
            observable = getHttpApi().getPersonalLoveList(InitCatchData.userListUserLike(), user_id, offset);
        } else if (type == ProductTypePanel.TYPE_TOGETHER) {
            observable = getHttpApi().getTogetherList(InitCatchData.getCoCreateOpus(), user_id, offset);
        } else if (type == ProductTypePanel.TYPE_PRODUCT) {
            observable = getHttpApi().getPersonalProductList(InitCatchData.userListUserOpus(), user_id, offset);
        }
        executeRequest(isRefresh ? HttpConstant.TYPE_PERSONAL_LIKE_LIST : (HttpConstant.TYPE_PERSONAL_LIKE_LIST + 1), observable)
                .subscribe(new DCNetObserver<ListLikeResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, ListLikeResponse response) {
                        if (null != viewCallback) {
                            offset = response.getOffset();
                            List<ShortVideoItem> list = null;
                            switch (type) {
                                case ProductTypePanel.TYPE_LIKE:
                                    list = response.getUser_like_list();
                                    break;
                                case ProductTypePanel.TYPE_TOGETHER:
                                    list = response.getCo_create_opus();
                                    break;
                                case ProductTypePanel.TYPE_PRODUCT:
                                    list = response.getList_user_opus();
                                    break;
                                default:
                                    break;
                            }
                            viewCallback.onVideoListOk(isRefresh, list, null, null,response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onGetProductFail(isRefresh, message);
                        }
                    }
                });
    }

    /**
     * 获取举报内容
     */
    public void getReportList() {
        executeRequest(HttpConstant.TYPE_PERSONAL_REPORT_LIST, getHttpApi().getListReportType(InitCatchData.userListReportType()))
                .subscribe(new DCNetObserver<ReportTypeResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, ReportTypeResponse response) {
                        if (null != viewCallback) {
                            viewCallback.onGetReportListOk(response);
                            InitCatchData.userReposrtList().setReport_type(response.getReport_type_list());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_PERSONAL_REPORT_LIST, message);
                    }
                });
    }

    /**
     * 举报
     *
     * @param user_id
     * @param report_type_id
     */
    public void reportUser(long user_id, int report_type_id) {
        executeRequest(HttpConstant.TYPE_PERSONAL_REPORT, getHttpApi().reportUser(InitCatchData.userReport(), user_id, report_type_id))
                .subscribe(new DCNetObserver<BaseResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                        if (null != viewCallback) {
                            viewCallback.onGetReportUserOk();
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_PERSONAL_REPORT, message);
                    }
                });
    }


    public interface IUserInfoView extends BaseUserInfoPresenter.IBaseUserInfoView {

        void onVideoListOk(boolean isRefresh, List<ShortVideoItem> list, List<Banner> bannerList, List<UserInfo> recommendUsers, boolean hasMore);

        void onGetProductFail(boolean isRefresh, String message);

        void onGetReportListOk(ReportTypeResponse reportTypeResponse);

        void onGetReportUserOk();

    }

}
