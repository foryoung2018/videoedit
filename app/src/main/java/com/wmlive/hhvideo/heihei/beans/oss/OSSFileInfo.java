package com.wmlive.hhvideo.heihei.beans.oss;

/**
 * oss 返回的文件信息
 * Created by kangzhen on 2017/6/5.
 */

public class OSSFileInfo {
    private String fileName;
    private String path;//上传路径
    private String sign;
    private String bucketName;
    private String objectKey;
    private String endPoint;

    public String getEndpoint() {
        return endPoint;
    }

    public void setEndpoint(String endpoint) {
        this.endPoint = endpoint;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return "OSSFileInfo{" +
                "fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", sign='" + sign + '\'' +
                ", sign='" + endPoint + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", objectKey='" + objectKey + '\'' +
                '}';
    }
}
