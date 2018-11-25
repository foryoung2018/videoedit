package com.wmlive.hhvideo.heihei.beans.oss;


import com.wmlive.networklib.entity.BaseResponse;

/**
 * ossToken　d的返回信息
 * Created by kangzhen on 2017/6/5.
 */

public class OSSTokenResponse extends BaseResponse {
    private OSSCredentials Credentials;//oss授权信息
    private OSSFileInfo FileInfo;//文件信息

    public OSSCredentials getCredentials() {
        return Credentials;
    }

    public void setCredentials(OSSCredentials credentials) {
        Credentials = credentials;
    }

    public OSSFileInfo getFileInfo() {
        return FileInfo;
    }

    public void setFileInfo(OSSFileInfo fileInfo) {
        FileInfo = fileInfo;
    }

    @Override
    public String toString() {
        return "OSSTokenResponse{" +
                "Credentials=" + (Credentials == null ? "null" : Credentials.toString()) +
                ", FileInfo=" + (FileInfo == null ? "null" : FileInfo.toString()) +
                '}';
    }
}
