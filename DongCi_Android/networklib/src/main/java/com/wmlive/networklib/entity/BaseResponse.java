package com.wmlive.networklib.entity;

import java.io.Serializable;

/**
 * Created by vhawk on 2017/5/22.
 * Modify by lsq
 */

public class BaseResponse implements Serializable {
    //接口请求码
    private int reqCode;
//      "error_code": 0,
//      "error_msg": "success",

    private int error_code;
    private String error_msg;

    private boolean has_more;
    private int offset;

    public int getError_code() {
        return error_code;
    }

    public BaseResponse setError_code(int error_code) {
        this.error_code = error_code;
        return this;
    }

    public String getError_msg() {
        return error_msg;
    }

    public BaseResponse setError_msg(String error_msg) {
        this.error_msg = error_msg;
        return this;
    }

    public int getReqCode() {
        return reqCode;
    }

    public void setReqCode(int reqCode) {
        this.reqCode = reqCode;
    }

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
