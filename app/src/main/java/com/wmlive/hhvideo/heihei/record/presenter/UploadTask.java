package com.wmlive.hhvideo.heihei.record.presenter;

import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.example.crclibrary.Crc64;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.common.ossutils.OSSUtils;
import com.wmlive.hhvideo.heihei.beans.log.VideoUpload;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialResponseEntity;
import com.wmlive.hhvideo.heihei.beans.oss.OSSCredentials;
import com.wmlive.hhvideo.heihei.beans.oss.OSSFileInfo;
import com.wmlive.hhvideo.heihei.beans.oss.OSSTokenResponse;
import com.wmlive.hhvideo.heihei.beans.oss.UploadEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;
import com.wmlive.networklib.util.EventHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsq on 9/12/2017.
 * <p>
 * module类型  (message，avatar，opus,material)
 * format类型
 * message’: [‘png’, ‘mp3’, ‘jpg’, ‘mp4’]，
 * ’avatar’: [‘png’, ‘jpg’] ‘
 * opus’: [‘mp4’, ‘jpg’, ‘webp’, ‘jpeg’]
 */

public class
UploadTask extends BasePresenter<AbsUploadTaskView> {
    public int index;
    private String module;
    private String format;
    private String filePath;
    private String ossPath;
    private String ossSign;
    private String accessKeyId;
    private OSSAsyncTask uploadTask;
    private OSSTokenResponse tokenResponse;
    public long contentLength = 0;
    public long videoLength = 0;
    public long originalId = 0;
    public long materialId = 0;
    public long musicId;
    public boolean isSuccess;
    private boolean started = false;
    private short retryCount = 5;
    private String crc64;
    public String quality; // 上传素材质量 "low" "high"
    private UploadEntity uploadEntity;
    private long startTime;
    private VideoUpload uploadVideoLog;
    private long fileLength;

    public UploadTask(AbsUploadTaskView view, int index, String module, String format, String filePath) {
        super(view);
        this.index = index;
        this.module = module;
        this.format = format;
        this.filePath = filePath;
        uploadEntity = new UploadEntity();
        uploadEntity.file_path = filePath;
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                contentLength = file.length();
                uploadEntity.file_len = contentLength;
            }
        }
    }

    public void startUpload() {
        KLog.i("=====start upload index:" + index);
        KLog.i("=====start upload filePath:" + filePath);
        startTime = System.currentTimeMillis();
        uploadVideoLog = new VideoUpload();
        getOssToken();
    }

    public void getOssToken() {
        if (!TextUtils.isEmpty(module) && !TextUtils.isEmpty(format) && !TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
            KLog.i("=====executeRequest to get oss token");
            executeRequest("UploadTask", HttpConstant.TYPE_OSSTOKEN_TOKEN_CODE,
                    getHttpApi().getOssToken(InitCatchData.sysOssToken(), module, format))
                    .subscribe(new DCNetObserver<OSSTokenResponse>() {
                        @Override
                        public void onRequestDataReady(int requestCode, String message, OSSTokenResponse response) {
                            if (!TextUtils.isEmpty(filePath) && response.getCredentials() != null && response.getFileInfo() != null) {
                                KLog.i("======获取oss token成功，index:" + index);
                                ossPath = response.getFileInfo().getPath();
                                ossSign = response.getFileInfo().getSign();
                                accessKeyId = response.getCredentials().getAccessKeyId();
                                uploadEntity.url = ossPath;
                                uploadFile(response, format, filePath);
                            } else {
                                KLog.i("======get oss token fail,message:" + message);
                                onFail(index, "get oss token error");
                            }
                        }

                        @Override
                        public void onRequestDataError(int requestCode, int serverCode, String message) {
                            KLog.i("======get oss token fail,serverCode:" + serverCode + " ,message:" + message + "\n剩余尝试次数：" + retryCount);
                            if (retryCount > 0) {
                                retryCount--;
                                getOssToken();
                            } else {
                                isSuccess = false;
                                onFail(index, "network error,error code:" + serverCode + ",error message:" + message);
                            }
                        }
                    });
        } else {
            KLog.i("=====data error or file not exist");
            isSuccess = false;
            onFail(index, "data error or file not exist");
        }
    }

    //osstoken失效,重新获取
    public void reGetOssToken() {
        if (!TextUtils.isEmpty(accessKeyId) && !TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
            KLog.i("=====executeRequest to get oss token");
            executeRequest("UploadTask", HttpConstant.TYPE_OSSTOKEN_RETOKEN_CODE,
                    getHttpApi().reGetOssToken(InitCatchData.sysReOssToken(), AccountUtil.getToken(), accessKeyId))
                    .subscribe(new DCNetObserver<OSSTokenResponse>() {
                        @Override
                        public void onRequestDataReady(int requestCode, String message, OSSTokenResponse response) {
                            if (!TextUtils.isEmpty(filePath) && response.getCredentials() != null && response.getFileInfo() != null) {
                                KLog.i("======获取oss token成功，index:" + index);
                                ossPath = response.getFileInfo().getPath();
                                ossSign = response.getFileInfo().getSign();
                                uploadEntity.url = ossPath;
                                uploadFile(response, format, filePath);
                            } else {
                                KLog.i("======get oss token fail,message:" + message);
                                onFail(index, "get oss token error");
                            }
                        }

                        @Override
                        public void onRequestDataError(int requestCode, int serverCode, String message) {
                            KLog.i("======get oss token fail,serverCode:" + serverCode + " ,message:" + message + "\n剩余尝试次数：" + retryCount);
                            if (retryCount > 0) {
                                retryCount--;
                                reGetOssToken();
                            } else {
                                isSuccess = false;
                                onFail(index, "network error,error code:" + serverCode + ",error message:" + message);
                            }
                        }
                    });
        } else {
            KLog.i("=====data error or file not exist");
            isSuccess = false;
            onFail(index, "data error or file not exist");
        }
    }


    private void uploadFile(final OSSTokenResponse ossToken, final String format, final String filePath) {
        if (ossToken != null && ossToken.getFileInfo() != null && ossToken.getCredentials() != null) {
            tokenResponse = ossToken;
            String endpoint = ossToken.getFileInfo().getEndpoint();
            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                    ossToken.getCredentials().getAccessKeyId(),
                    ossToken.getCredentials().getAccessKeySecret(),
                    ossToken.getCredentials().getSecurityToken());

            ClientConfiguration configuration = new ClientConfiguration();
            configuration.setMaxErrorRetry(5);
            configuration.setSocketTimeout(30 * 1000);
            configuration.setConnectionTimeout(20 * 1000);
            OSS ossClient = new OSSClient(DCApplication.getDCApp(), endpoint, credentialProvider, configuration);
            OSSFileInfo fileInfo = ossToken.getFileInfo();
            File uploadFile = new File(filePath); // 需要分片上传的文件
            byte[] bytes =  getBytes(filePath);
            if(bytes==null){
                onFail(index, "file is null");
                return;
            }
            crc64 = Crc64.aoscrc64(0,bytes, bytes.length);
            fileLength = uploadFile.length();
            if (fileLength > 10 * 1024 * 1024) {
                resumableUpload(ossClient, fileInfo, filePath, format);
            } else {
                normalUpload(ossToken, ossClient, format);
            }
        } else {
            KLog.i("====get oss token fail");
            onFail(index, "get oss token fail");
        }
    }

    /**
     * 获得指定文件的byte数组
     */

    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 断点续传使用MultiPart上传来实现的
     *
     * @param ossClient
     * @param fileInfo
     * @param filePath
     */
    public void resumableUpload(OSS ossClient, OSSFileInfo fileInfo, String filePath, String format) {
        KLog.i("====断点续传方式上传文件开始index：" + index + ",filePath:" + filePath);
        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record";
        File recordDir = new File(recordDirectory);
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(OSSUtils.getOssContentTypeByFormat(format));

        ResumableUploadRequest request = new ResumableUploadRequest(fileInfo.getBucketName(), fileInfo.getPath(), filePath, metadata, recordDirectory);
        request.setCRC64(OSSRequest.CRC64Config.YES);
        request.setPartSize(1024 * 1024);
        //request.setDeleteUploadOnCancelling(false);
        started = false;
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
                if (currentSize == totalSize) {
                    started = !started;
                    return;
                }
                if (viewCallback != null) {
                    viewCallback.onUploading(index, currentSize, totalSize);
                }
            }
        });


        uploadTask = ossClient.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                KLog.i("====断点续传方式上传文件成功index：" + index + ",filePath:" + filePath);
                onUploadLog(true);
                if (index < PublishPresenter.TYPE_EXPORT_VIDEO) {
                    uploadMaterial();
                } else {
                    isSuccess = true;
                    onOk(index, null);
                }
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String message = "upload file fail clientException:"
                        + (clientExcepion != null ? clientExcepion.getMessage() : "null")
                        + ",serviceException:" + (serviceException != null ? serviceException.getMessage() : "null");
                KLog.i("====断点续传方式上传文件失败，index：" + index + " , message:" + message + "\n剩余尝试次数：" + retryCount);
                onUploadLog(false);
                if ("InvalidSecurityToken".equals(serviceException.getErrorCode())) {
                    reGetOssToken();
                }
                if (retryCount > 0) {
                    retryCount--;
                    uploadFile(tokenResponse, format, filePath);
                } else {
                    isSuccess = false;
                    onFail(index, message);
                }
            }
        });
    }

    /**
     * 普通上传方式
     *
     * @param ossToken
     * @param ossClient
     */
    public void normalUpload(OSSTokenResponse ossToken, OSS ossClient, String format) {
        KLog.i("====普通方式上传文件开始index：" + index + ",filePath:" + filePath);
        PutObjectRequest request = new PutObjectRequest(ossToken.getFileInfo().getBucketName(), ossToken.getFileInfo().getPath(), filePath);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(OSSUtils.getOssContentTypeByFormat(format));
        request.setMetadata(metadata);
        request.setCRC64(OSSRequest.CRC64Config.YES);
        started = false;
        request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                if (currentSize == totalSize) {
                    started = !started;
                    return;
                }
                if (viewCallback != null) {
                    viewCallback.onUploading(index, currentSize, totalSize);
                }
            }
        });

        KLog.i("======开始上传阿里云index:" + index +
                ",localPath:" + filePath +
                ",server path:" + (ossToken.getFileInfo().getPath() +
                ",server sign:" + ossToken.getFileInfo().getSign()));
        uploadTask = ossClient.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                KLog.i("====普通方式上传文件成功index：" + index + ",filePath:" + filePath);
                if(filePath.endsWith("mp4")){
                    onUploadLog(true);
                }
                if (index < PublishPresenter.TYPE_EXPORT_VIDEO) {
                    uploadMaterial();
                } else {
                    isSuccess = true;
                    onOk(index, null);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                String message = "upload file fail clientException:"
                        + (clientException != null ? clientException.getMessage() : "null")
                        + ",serviceException:" + (serviceException != null ? serviceException.getMessage() : "null");
                KLog.i("====普通方式上传文件失败，index：" + index + " , message:" + message + "\n剩余尝试次数：" + retryCount);
                if(filePath.endsWith("mp4")){
                    onUploadLog(true);
                }
                if ("InvalidSecurityToken".equals(serviceException.getErrorCode())) {
                    reGetOssToken();
                }
                if (retryCount > 0) {
                    retryCount--;
                    uploadFile(tokenResponse, format, filePath);
                } else {
                    isSuccess = false;
                    onFail(index, message);
                }
            }
        });
//        uploadTask.waitUntilFinished();
//        uploadTask.cancel(); // 可以取消任务
//        uploadTask.waitUntilFinished(); // 可以等待任务完成
    }

    public void uploadMaterial() {
        if (tokenResponse != null && tokenResponse.getFileInfo() != null) {
            executeRequest(HttpConstant.TYPE_UPLOAD_MATERIAL,
                    getHttpApi().uploadMaterial(InitCatchData.getUploadMaterial(), tokenResponse.getFileInfo().getPath(),
                            tokenResponse.getFileInfo().getSign(), videoLength, originalId, musicId, crc64, quality))
                    .subscribe(new DCNetObserver<UploadMaterialResponseEntity>() {
                        @Override
                        public void onRequestDataReady(int requestCode, String message, UploadMaterialResponseEntity response) {
                            if (viewCallback != null) {
                                if (response.material != null) {
                                    isSuccess = true;
                                    KLog.i("=====onUpload material ok，index:" + index);
                                    materialId = response.material.id;
                                    onOk(index, response.material);
                                } else {
                                    KLog.i("=====upload material fail,parse material error，index:" + index);
                                    onFail(index, "upload material fail,parse material error");
                                }
                            }
                        }

                        @Override
                        public void onRequestDataError(int requestCode, int serverCode, String message) {
                            KLog.i("=====upload material fail,parse material error，index:" + index
                                    + " ,message:" + message + " ,serverCode:" + serverCode + "\n剩余尝试次数：" + retryCount);
                            if (retryCount > 0) {
                                retryCount--;
                                uploadMaterial();
                            } else {
                                isSuccess = false;
                                onFail(index, "upload material fail,error code:" + serverCode + ",message:" + message);
                            }
                        }
                    });
        } else {
            isSuccess = false;
            onFail(index, "upload material file is not exist");
        }
    }

    private void onFail(int index, String message) {
        uploadEntity.local_ip = GlobalParams.StaticVariable.sLocalPublicIp;
        uploadEntity.server_ip = GlobalParams.StaticVariable.sAliyunUploadIp;
        uploadEntity.res = "fail";
        uploadEntity.duration = System.currentTimeMillis() - startTime;
        if (null != viewCallback) {
            viewCallback.onUploadFail(index, message);
        }
    }

    private void onOk(int index, UploadMaterialEntity entity) {
        uploadEntity.local_ip = GlobalParams.StaticVariable.sLocalPublicIp;
        uploadEntity.server_ip = GlobalParams.StaticVariable.sAliyunUploadIp;
        uploadEntity.res = "success";
        uploadEntity.duration = System.currentTimeMillis() - startTime;
        if (null != viewCallback) {
            viewCallback.onUploadOk(index, entity);
        }
    }

    /**
     * 上传视频日志上报
     */
    /**
     * 上传视频日志上报
     */
    private void onUploadLog(boolean isSuccess) {
        double duration = (System.currentTimeMillis() - startTime);
        double fileSize = fileLength / 1024.0;//byte -》k
        uploadVideoLog.file_len = fileSize + "";
        uploadVideoLog.upoload_duration = duration/1000.0 + "";
        if (isSuccess) {
            uploadVideoLog.upload_speed = fileSize / duration*1000.0 + "";
            uploadVideoLog.res = "success";
        } else {
            uploadVideoLog.res = "fail";
        }
        EventHelper.post(GlobalParams.EventType.TYPE_UPLOAD, uploadVideoLog);
    }

    public String getOssPath() {
        return ossPath;
    }

    public String getOssSign() {
        return ossSign;
    }

    public String getCrc64() {
        return crc64;
    }

    @Override
    public void destroy() {
        if (uploadTask != null && !uploadTask.isCanceled()) {
            uploadTask.cancel();
        }
        started = false;
        super.destroy();
    }

    @Override
    public String toString() {
        return "UploadTask{" +
                "index=" + index +
                ", module='" + module + '\'' +
                ", format='" + format + '\'' +
                ", filePath='" + filePath + '\'' +
                ", uploadTask=" + (uploadTask == null ? "null" : uploadTask.toString()) +
                ", videoLength=" + videoLength +
                ", originalId=" + originalId +
                ", musicId=" + musicId +
                ", ossPath=" + ossPath +
                ", ossSign=" + ossSign +
                '}';
    }

    private void createOss() {
        OSSTokenResponse ossTokenResponse = new OSSTokenResponse();
        ossTokenResponse.setCredentials(new OSSCredentials());
        ossTokenResponse.setFileInfo(new OSSFileInfo());

        ossTokenResponse.getCredentials().setAccessKeyId("STS.JzYUfkUNjoigYyhbx3euygjbX");
        ossTokenResponse.getCredentials().setSecurityToken("CAISxgJ1q6Ft5B2yfSjIq7nsHtzfuJFL2KuMW1/ZhnhmafpViK/Jujz2IHlLdHJvB+AasfU/nWBX6fcalqBuSpMQHBScPJsoeQ+uSoLiMeT7oMWQweEufvTHcDHh+3eZsebWZ+LmNpy/Ht6md1HDkAJq3" +
                "LL+bk/Mdle5MJqP+/kFC9MMRVuAcCZhDtVbLRcYpq18D3bKMuu3ORPHm3fZCFES2jBxkmRi86+ysO/+yhPVlw/90fRH5dazcJGhZtt3PZw6Wcq+3+FqM/KYlyJZ9xEN+KBwg6dFvjDfo8raWQEIvE3XaruOqoQ3dF4pVpZnEv9AqdrzvN8A47" +
                "SOydqpkUcRYrsOCnmPFLrNmpWURLmbUf8ibqv+Nnj31dSCC4L4qQtMYwhAbVwaIIV/eiMvWEx1Gm6Fe/e9mVnSekK8UamZ3bG6VwWBX51wlBqAATN3E2VMs4zNY/MTud7fMptTkcNicgiQ7jijMAxPcTL3we+CTKeIXfhTd8ZDv5oix0b2xgM15V" +
                "3HfyZmoR1sEiZ8ewChN3DTpQgeWMjEqZ/EtcdsiXY2IqEwfs2sMN9nN+X7i7z++e49BDgZgqkBu8FpIuf/36WxBhyBDTKNKCNy");
        ossTokenResponse.getCredentials().setExpiration("2017-09-19T13:47:09Z");
        ossTokenResponse.getCredentials().setAccessKeySecret("489KvhWSZrsoRpDPQ6w49LcuJV58WRFxswns639afbg6");
        ossTokenResponse.getFileInfo().setBucketName("amazsic-bucket-01");
        ossTokenResponse.getFileInfo().setSign("c6eeb803fc736651e6efee355ca9ac11");
        ossTokenResponse.getFileInfo().setPath("data/dongci/opus/201709192010098XV5a9lmJgA");
        ossTokenResponse.getFileInfo().setFileName("201709192010098XV5a9lmJgA");
        ossTokenResponse.getFileInfo().setEndpoint("http://amazsic-bucket-01.oss-cn-beijing.aliyuncs.com/data/dongci/opus/2017091920100988W1PfYnHNj");
//        uploadFile(ossTokenResponse, format, filePath);
    }
}
