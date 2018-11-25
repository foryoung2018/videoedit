package com.wmlive.hhvideo.common.ossutils;

import com.alibaba.sdk.android.oss.model.OSSRequest;

/**
 * 上传的回调接口
 * Created by admin on 2017/3/18.
 */

public interface UpLoadInterface {


    public void onSuccessUpload(UploadALiResultBean obj);// 返回 阿里云 上传信息

    public void onFailsUpload(UploadALiResultBean obj);

    public void onExceptionUpload(UploadALiResultBean e);

    public void onProgress(OSSRequest request, long currentSize, long totalSize);
}
