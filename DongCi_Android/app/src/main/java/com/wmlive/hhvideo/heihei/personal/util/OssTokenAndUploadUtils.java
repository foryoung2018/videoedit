package com.wmlive.hhvideo.heihei.personal.util;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.wmlive.hhvideo.common.network.DCRequest;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.common.ossutils.UpLoadInterface;
import com.wmlive.hhvideo.common.ossutils.UploadALiResultBean;
import com.wmlive.hhvideo.heihei.beans.oss.OSSTokenResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * 上传阿里云服务器
 * Created by admin on 2017/3/20.
 */

public class OssTokenAndUploadUtils {
    private static OssTokenAndUploadUtils mOssTokenAndUploadUtils;
    private UpLoadInterface mUpLoadInterface;//上传回调接口
    private Context mContext;//
    private String strUploadPath;//上传文件路径
    private boolean isShowWatingDialog = true;//是否显示等待框

    public OssTokenAndUploadUtils(Context mContext, UpLoadInterface mUpLoadInterface) {
        this.mUpLoadInterface = mUpLoadInterface;
        this.mContext = mContext;
    }

    public OssTokenAndUploadUtils(Context mContext, UpLoadInterface mUpLoadInterface, String strUploadPath) {
        this.mUpLoadInterface = mUpLoadInterface;
        this.mContext = mContext;
        this.strUploadPath = strUploadPath;
    }

    /**
     * 设置上传阿里云接口
     *
     * @param uploadALiInterface
     */
    public void setmUpLoadInterface(UpLoadInterface uploadALiInterface) {
        mUpLoadInterface = uploadALiInterface;
    }

    public void setStrUploadPath(String strUploadPath) {
        this.strUploadPath = strUploadPath;
    }

    public void setShowWatingDialog(boolean showWatingDialog) {
        isShowWatingDialog = showWatingDialog;
    }

    /**
     * 获取上传osstoken 信息
     *
     * @param format mp3,png,jepg,jpg,
     * @param module record’, ‘message’,avatar (录音模块：record，消息：message, 头像：avator)
     */
    public void getOssTokenUploadByNetwork(String format, String module) {
        DCRequest.getRetrofit()
                .getObservable(null, HttpConstant.TYPE_PERSONAL_POST_USER_HEAD, DCRequest.getHttpApi()
                        .getOssToken(InitCatchData.sysOssToken(), module, format), null)
                .subscribe(new DCNetObserver<OSSTokenResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, OSSTokenResponse response) {
                        if (null != response && response.getError_code() == 0) {
                            actionUploadALiBaBa(response);
                        } else {
                            if (mUpLoadInterface != null) {
                                UploadALiResultBean mBean = new UploadALiResultBean();
                                mBean.setUpload_type(UploadALiResultBean.TYPE_UPLOAD_OSSTOKEN);
                                mBean.setErrormsg(response.getError_msg());
                                mBean.setErrorcode(response.getError_code());
                                mBean.setmOssTokenResult(response);
                                mUpLoadInterface.onFailsUpload(mBean);
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        UploadALiResultBean mBean = new UploadALiResultBean();
                        mBean.setUpload_type(UploadALiResultBean.TYPE_UPLOAD_OSSTOKEN);
                        mBean.setErrormsg(message);
                        mUpLoadInterface.onFailsUpload(mBean);
                    }
                });
    }

    /**
     * 上传阿里云
     */
    private void actionUploadALiBaBa(final OSSTokenResponse ossTokenResult) {
        String endpoint = ossTokenResult.getFileInfo().getEndpoint();
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossTokenResult.getCredentials().getAccessKeyId(), ossTokenResult.getCredentials().getAccessKeySecret(), ossTokenResult.getCredentials().getSecurityToken());

        OSS oss = new OSSClient(mContext.getApplicationContext(), endpoint, credentialProvider);
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(ossTokenResult.getFileInfo().getBucketName(), ossTokenResult.getFileInfo().getPath(), strUploadPath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                if (null != mUpLoadInterface) {
                    mUpLoadInterface.onProgress(request, currentSize, totalSize);
                }
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (null != mUpLoadInterface) {
                    UploadALiResultBean mBean = new UploadALiResultBean();
                    mBean.setUpload_type(UploadALiResultBean.TYPE_UPLOAD_ALIBABA);
                    mBean.setmOSSRequest(request);
                    mBean.setmOSSResult(result);
                    mBean.setmOssTokenResult(ossTokenResult);
                    mUpLoadInterface.onSuccessUpload(mBean);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (null != mUpLoadInterface) {
                    UploadALiResultBean mBean = new UploadALiResultBean();
                    mBean.setUpload_type(UploadALiResultBean.TYPE_UPLOAD_ALIBABA);
                    mBean.setmOSSRequest(request);
                    mBean.setmClientException(clientExcepion);
                    mBean.setmServiceException(serviceException);
                    mUpLoadInterface.onFailsUpload(mBean);
                }
            }
        });
        // task.waitUntilFinished();
        // task.cancel(); // 可以取消任务
        //task.waitUntilFinished(); // 可以等待任务完成
    }

}
