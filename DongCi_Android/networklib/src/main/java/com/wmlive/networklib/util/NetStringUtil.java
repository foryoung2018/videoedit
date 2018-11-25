package com.wmlive.networklib.util;

import android.content.Context;

import com.wmlive.networklib.R;


public class NetStringUtil {


    //最后一次请求的时间戳
    public static final String LATEST_REQUEST_TIMESTAMP = "latest_request_timestamp";

    //默认最近的时间戳  2017/1/16 11:30:00 AM
    public static final int ERR_CODE_2000 = 2000;
    public static final int ERR_CODE_NO_CONN = -1;
    public static final int ERR_CODE_201 = 201;
    public static final int ERR_CODE_404 = 404;
    public static final int ERR_CODE_401 = 401;
    public static final int ERR_CODE_500 = 500;
    public static final int ERR_CODE_502 = 502;
    public static final int ERR_INVALID_TOKEN = 30001;  //token失效
    public static final int ERR_SYSTEM_TIME_INCORRECT = 60001;  //本地系统时间错误

    public static String getErrString(Context context, int code) {
        String result;
        switch (code) {
            case ERR_CODE_404:
                result = context.getString(R.string.err404);
                break;
            case ERR_CODE_500:
                result = context.getString(R.string.err500);
                break;
            case ERR_CODE_502:
                result = context.getString(R.string.err502);
                break;
            case ERR_CODE_2000:
                result = context.getString(R.string.hintRequestParmasNull);
                break;
            case ERR_CODE_201:
                result = context.getString(R.string.hintRequestOkWithoutContent);
                break;
            case ERR_CODE_401:
                result = context.getString(R.string.hintVerifyFailed);
                break;
            case ERR_CODE_NO_CONN:
                result = context.getString(R.string.errorNoConnection);
                break;
            case ERR_INVALID_TOKEN:
                result = context.getString(R.string.errorInvalidToken);
                break;
            case ERR_SYSTEM_TIME_INCORRECT:
                result = context.getString(R.string.errSystemTimeIncorrect);
                break;
            default:
                result = context.getString(R.string.errDefault);
                break;
        }
        return result;
    }

    public static String getErrString(Context context, Throwable t) {
        String result;
        if (t instanceof java.net.SocketTimeoutException) {
            result = context.getString(R.string.hintServerTimeout);
        } else if (t instanceof IllegalArgumentException) {
            result = context.getString(R.string.errIllegalArgumentException);
        } else {
            result = context.getString(R.string.errDefault);
        }
        return result;
    }
}
