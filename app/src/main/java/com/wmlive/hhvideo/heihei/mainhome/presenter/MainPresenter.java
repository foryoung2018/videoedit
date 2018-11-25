package com.wmlive.hhvideo.heihei.mainhome.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.manager.LogFileManager;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.FrameSortBean;
import com.wmlive.hhvideo.heihei.beans.frame.Frames;
import com.wmlive.hhvideo.heihei.beans.login.LoginUserResponse;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.SplashResourceResponse;
import com.wmlive.hhvideo.heihei.beans.main.UpdateInfo;
import com.wmlive.hhvideo.heihei.beans.main.UpdateSystemBean;
import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateListBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.quickcreative.ChooseStyle4QuickActivity;
import com.wmlive.hhvideo.heihei.quickcreative.CreativeQuickUtils;
import com.wmlive.hhvideo.heihei.splash.presenter.SplashPresenter;
import com.wmlive.hhvideo.heihei.splash.view.SplashView;
import com.wmlive.hhvideo.service.DcWebSocketService;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.FileZipAndUnZip;
import com.wmlive.hhvideo.utils.FrameUtils;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.download.FileDownload;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Created by lsq on 7/5/2017.
 * 首页的presenter
 */

public class MainPresenter extends SplashPresenter<MainPresenter.IMainNewView> {


    public MainPresenter(MainPresenter.IMainNewView view) {
        super(view);
    }

    //检查升级
    public void checkSystemAppUpdate(String version, String channel) {
        executeRequest(HttpConstant.TYPE_CHECK_SYSTEM_UPLOAD_CODE,
                getHttpApi().checkSystemAppUpdate(InitCatchData.sysUpdateCheck(), version, "android", "hhvideo", channel))
                .subscribe(new DCNetObserver<UpdateSystemBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, UpdateSystemBean response) {
                        if (viewCallback != null && response != null) {
                            viewCallback.checkSystemAppUpdateSucceed(response.getInfo());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.checkSystemAppUpdateFailure(message);
                        }
                    }
                });
    }

    //上传日志
    public void uploadLog(File logFile) {
        if (logFile.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), logFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", logFile.getName(), requestFile);
            executeRequest(HttpConstant.TYPE_UPLOAD_LOG,
                    getHttpApi().uploadLog(InitCatchData.sysUploadLog(), body))
                    .subscribe(new DCNetObserver<BaseResponse>() {
                        @Override
                        public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                            KLog.i("=====日志上传成功");
                            KLog.i("log_update", "====onRequestDataReady===：" + message);
                            LogFileManager.getInstance().delLogFile();
                        }

                        @Override
                        public void onRequestDataError(int requestCode, int serverCode, String message) {
                            KLog.i("=====日志上传失败：" + message);
                            KLog.i("log_update", "====onRequestDataError===：" + message);
                        }
                    });
        }
    }

    /**
     * 多文件上传
     *
     * @param filePaths
     */
    public void uploadFile(String... filePaths) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        File tFile;
        int index = 0;
        for (String filePath : filePaths) {
            if (!TextUtils.isEmpty(filePath)) {
                tFile = new File(filePath);
                if (tFile.exists()) {
                    requestBodyMap.put("file" + (index++) + "\"; filename=\"" + tFile.getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), tFile));
                }
            }
        }
        if (!requestBodyMap.isEmpty()) {
            executeRequest(HttpConstant.TYPE_UPLOAD_LOG,
                    getHttpApi().fileUpload(InitCatchData.sysUploadLog(), requestBodyMap))
                    .subscribe(new DCNetObserver<BaseResponse>() {
                        @Override
                        public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                            KLog.i("=====文件上传成功");
                        }

                        @Override
                        public void onRequestDataError(int requestCode, int serverCode, String message) {
                            KLog.i("=====文件上传失败：" + message);
                        }
                    });
        }

    }

    public void getFrameLayoutList(final Context context) {
        executeRequest(HttpConstant.TYPE_FRAME_LAYOUT,
                getHttpApi().getFrameLayoutList(InitCatchData.getOpusFrameLayout()))
                .subscribe(new DCNetObserver<Frames>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, final Frames response) {
                        Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(@NonNull Integer integer) throws Exception {
                                        KLog.i("frame layout保存到本地");
                                        SPUtils.putString(DCApplication.getDCApp(), SPUtils.FRAME_LAYOUT_DATA, JSON.toJSONString(response));
//                                        KLog.i("frame layout保存到本地完成response==" + JSON.toJSONString(response));
                                        FrameUtils.ins().init();
                                        if (response != null && response.layouts != null) {
                                            List<FrameSortBean> layouts = response.layouts;
                                            ArrayList<DownloadBean> downloadList = new ArrayList<DownloadBean>();
                                            for (int i = 0, size = layouts.size(); i < size; i++) {
                                                FrameSortBean frame = layouts.get(i);
                                                List<FrameInfo> layout = frame.layout;
                                                for (FrameInfo frameInfo : layout) {
                                                    if (frameInfo != null && !TextUtils.isEmpty(frameInfo.sep_image)) {
                                                        DownloadBean downloadBean = new DownloadBean(frameInfo.id, frameInfo.sep_image,
                                                                AppCacheFileUtils.getAppFramesImagePath(), "", "");
                                                        downloadList.add(downloadBean);
                                                    }
                                                }

                                            }
                                            //下载服务
                                            FileDownload.start(context, downloadList, new ResultReceiver(new Handler()) {
                                                @Override
                                                protected void onReceiveResult(int resultCode, Bundle resultData) {
                                                    KLog.i("MainPresenter FileDownload onReceiveResult resultCode " + resultCode);
                                                    switch (resultCode) {
                                                        case FileDownload.RESULT_COMPLETE_ALL:
                                                            KLog.i("MainPresenter FileDownload", "下载画框图片完成");
                                                            break;
                                                    }
                                                }
                                            });

                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {

                                    }
                                });
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                    }
                });
    }

    public void silentLogin() {
        UserInfo userInfo = AccountUtil.getUserInfo();
        if (userInfo != null) {
            executeRequest(HttpConstant.TYPE_USER_USER_INFO, getHttpApi().getUserInfo(InitCatchData.userGetUserInfo(), userInfo.getId()))
                    .subscribe(new DCNetObserver<LoginUserResponse>() {
                        @Override
                        public void onRequestDataReady(int requestCode, String message, final LoginUserResponse response) {
                            if (response != null && response.getUser_info() != null) {
                                response.setToken(AccountUtil.getToken());
                                AccountUtil.resetAccount(response);
                                UserInfo userInfo = response.getUser_info();
                                TCAgent.onLogin(String.valueOf(userInfo.getId()), TDAccount.AccountType.REGISTERED, userInfo.getName());
                            } else {
                                AccountUtil.clearAccount();
                            }
                            DcWebSocketService.startSocket(DCApplication.getDCApp(), 1500);
                        }

                        @Override
                        public void onRequestDataError(int requestCode, int serverCode, String message) {
                            if (serverCode == 30001) {
                                AccountUtil.clearAccount();
                                DcWebSocketService.startSocket(DCApplication.getDCApp(), 1500);
                            }
                        }
                    });
        } else {
            DcWebSocketService.startSocket(DCApplication.getDCApp(), 1500);
        }
    }

    public void getLoadSplash() {
        KLog.i("=======开始获取splash资源");
        executeRequest(HttpConstant.TYPE_SPLASH_IMAGE, getHttpApi().getLoadSplash(InitCatchData.getLoadSplash()))
                .subscribe(new DCNetObserver<SplashResourceResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, SplashResourceResponse response) {
                        KLog.i("======获取splash数据成功");
                        if (!CollectionUtil.isEmpty(response.data)) {
                            GiftManager.get().checkSplashResource(response.data);
                        } else {
                            FileUtil.deleteAll(GiftManager.getSplashResourcePath(), false);
                            SPUtils.putString(DCApplication.getDCApp(), SPUtils.SPLASH_RESOURCE_DATA, "");
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        KLog.i("======获取splash数据出错：" + message);
                        SPUtils.putString(DCApplication.getDCApp(), SPUtils.SPLASH_RESOURCE_DATA, "");
                    }
                });
    }

    public interface IMainNewView extends SplashView {
        void checkSystemAppUpdateSucceed(UpdateInfo updateInfo);

        void checkSystemAppUpdateFailure(String updateInfo);

    }

    public void getCreativeList() {
        executeRequest(HttpConstant.TYPE_CREATIVELIST, getHttpApi().getCreativeList(InitCatchData.getCreativeList(), 0)).subscribe(new DCNetObserver<CreativeTemplateListBean>() {
            @Override
            public void onRequestDataReady(int requestCode, String message, CreativeTemplateListBean response) {
                KLog.d("getCreativeList", "onRequestDataReady: response==" + InitCatchData.getCreativeList() + response);
                String string = SPUtils.getString(DCApplication.getDCApp(), SPUtils.CREATIVE_ZIP_LIST, "");
                if (!TextUtils.isEmpty(string)) {
                    CreativeTemplateListBean creativeTemplateListBean = JsonUtils.parseObject(string, CreativeTemplateListBean.class);
                    compareList(creativeTemplateListBean.getTemplate_list(), response.getTemplate_list());
                }
                List<CreativeTemplateListBean.TemplateListBean> template_list = response.getTemplate_list();

                if (CollectionUtil.isEmpty(template_list) || template_list.size() == 0)
                    return;
                CreativeTemplateListBean.TemplateListBean templateListBean = template_list.get(0);
                for (int i = 0; i < template_list.size(); i++) {
                    if (template_list.get(i).getIs_default() == 1) {
                        templateListBean = template_list.get(i);
                        break;
                    }
                }
                CreativeTemplateListBean.BgListBean defaultBgBean = CreativeQuickUtils.getDefaultBgBean(response.getBg_list(), templateListBean.getDefault_bg());
                int bgDownloadId = FileDownloadUtils.generateId(defaultBgBean.getBg_resource(), AppCacheFileUtils.getAppCreativePath());
                DownloadBean bgdownloadBean = new DownloadBean(bgDownloadId, defaultBgBean.getBg_resource(),
                        AppCacheFileUtils.getAppCreativePath(), "", "", 0);
                int downloadId = FileDownloadUtils.generateId(templateListBean.getZip_path(), AppCacheFileUtils.getAppCreativePath());
                DownloadBean downloadBean = new DownloadBean(downloadId, templateListBean.getZip_path(),
                        AppCacheFileUtils.getAppCreativePath(), "", "", 0);
                ArrayList<DownloadBean> downloadList = new ArrayList<>();
                downloadList.add(downloadBean);
                downloadList.add(bgdownloadBean);
                FileDownload.start(context, downloadList, resultReceiver);
                KLog.d("CREATIVE_DEFALT_ZIP", "onRequestDataReady: templateListBean.getZip_path()==" + AppCacheFileUtils.getAppCreativePath() + templateListBean.getZip_path().substring(templateListBean.getZip_path().lastIndexOf("/") + 1, templateListBean.getZip_path().length()));
                SPUtils.putString(DCApplication.getDCApp(), SPUtils.CREATIVE_ZIP_LIST, JSON.toJSONString(response));
                SPUtils.putString(DCApplication.getDCApp(), SPUtils.CREATIVE_DEFALT_TEMPLATE_NAME,
                        templateListBean.getTemplate_name()
                        /*templateListBean.getZip_path().substring(templateListBean.getZip_path().lastIndexOf("/") + 1, templateListBean.getZip_path().length())*/);

                SPUtils.putString(DCApplication.getDCApp(), SPUtils.CREATIVE_DEFALT_TEMPLATEBEAN, JSON.toJSONString(templateListBean));

            }

            @Override
            public void onRequestDataError(int requestCode, int serverCode, String message) {
//                viewCallback.onRequestDataError(requestCode, message);
            }
        });
    }

    private void compareList(List<CreativeTemplateListBean.TemplateListBean> listOld, List<CreativeTemplateListBean.TemplateListBean> listNew) {
        for (CreativeTemplateListBean.TemplateListBean listBean : listOld) {
            String template_name = listBean.getTemplate_name();
            String zip_md5 = listBean.getZip_md5();
            for (CreativeTemplateListBean.TemplateListBean oldBean : listOld) {
                if (oldBean.getTemplate_name().equals(template_name)) {
                    if (!zip_md5.equals(oldBean.getZip_md5())) {//zip changed      need to delete old
                        FileUtil.deleteDir(AppCacheFileUtils.getAppCreativePath() + template_name);
                    }
                }
            }
        }
    }


    private ResultReceiver resultReceiver = new mainReciver(new Handler(Looper.getMainLooper()));

    public class mainReciver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        @SuppressLint("RestrictedApi")
        public mainReciver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String message = resultData.getString("message");
            DownloadBean downloadBean = resultData.getParcelable("downloadBean");
            KLog.i("", "downloadMaterial: " + downloadBean + " resultCode " + resultCode + message);
            switch (resultCode) {
                case FileDownload.RESULT_PREPARE:
                    break;
                case FileDownload.RESULT_DOWNLOADING:
                    break;
                case FileDownload.RESULT_ERROR:
                    break;
                case FileDownload.RESULT_COMPLETE:
                    String savePath = downloadBean.wholePathName;
                    FileZipAndUnZip.unZipFile(savePath, AppCacheFileUtils.getAppCreativePath());//重复解压
                    break;
            }

        }
    }
}
