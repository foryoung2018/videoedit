package com.wmlive.hhvideo.heihei.beans.oss;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by lsq on 5/23/2018 - 11:30 AM
 * 类描述：
 */
public class UploadEntity extends BaseModel {
    public String file_path;//本地文件路径
    public String url;//上传url
    public String private_ip;//私网ip
    public String local_ip;//私网出口ip
    public String server_ip;//服务端ip
    public long file_len;//文件大小
    public String res;//结果success/fail
    public long duration;//耗时

    @Override
    public String toString() {
        return "UploadEntity{" +
                "file_path='" + file_path + '\'' +
                ", url='" + url + '\'' +
                ", private_ip='" + private_ip + '\'' +
                ", local_ip='" + local_ip + '\'' +
                ", server_ip='" + server_ip + '\'' +
                ", file_len=" + file_len +
                ", res='" + res + '\'' +
                ", duration=" + duration +
                '}';
    }
}
