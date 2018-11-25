package com.wmlive.hhvideo.common.ossutils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;
import com.wmlive.hhvideo.heihei.beans.oss.OSSTokenResponse;

/**
 * 上传啊里云接口对象
 * Created by admin on 2017/3/20.
 */

public class UploadALiResultBean {
    public static final int TYPE_UPLOAD_OSSTOKEN = 0;//获取
    public static final int TYPE_UPLOAD_ALIBABA = 1;//上传阿里云
    public static final int TYPE_UPLOAD_BUSSINESS = 2;//业务逻辑

    private int upload_type = -1;//上传类型  0： 请求osstoken   1:上传啊里云   3：业务逻辑
    private OSSTokenResponse mOssTokenResult; //osstoken 结果信息
    private OSSRequest mOSSRequest;//啊里云的 上传信息
    private OSSResult mOSSResult;//阿里云的 返回信息
    private long currentSize;//当前上传
    private long totalSize;//上传的总大小
    private ClientException mClientException;//阿里云的客户端错误
    private ServiceException mServiceException;//阿里云的服务错误
    private Throwable error;//http 错误
    /**
     * 返回状态
     */
    private int errorcode;

    /**
     * 提示信息
     */
    private String errormsg;


    public int getUpload_type() {
        return upload_type;
    }

    public void setUpload_type(int upload_type) {
        this.upload_type = upload_type;
    }

    public OSSTokenResponse getmOssTokenResult() {
        return mOssTokenResult;
    }

    public void setmOssTokenResult(OSSTokenResponse mOssTokenResult) {
        this.mOssTokenResult = mOssTokenResult;
    }

    public OSSRequest getmOSSRequest() {
        return mOSSRequest;
    }

    public void setmOSSRequest(OSSRequest mOSSRequest) {
        this.mOSSRequest = mOSSRequest;
    }

    public OSSResult getmOSSResult() {
        return mOSSResult;
    }

    public void setmOSSResult(OSSResult mOSSResult) {
        this.mOSSResult = mOSSResult;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public ClientException getmClientException() {
        return mClientException;
    }

    public void setmClientException(ClientException mClientException) {
        this.mClientException = mClientException;
    }

    public ServiceException getmServiceException() {
        return mServiceException;
    }

    public void setmServiceException(ServiceException mServiceException) {
        this.mServiceException = mServiceException;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }


    public static int getTypeUploadOsstoken() {
        return TYPE_UPLOAD_OSSTOKEN;
    }


}
