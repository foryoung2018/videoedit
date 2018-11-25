package com.example.curllibrary;

/**
 * Author：create by jht on 2018/9/19 16:04
 * Email：haitian.jiang@welines.cn
 */
public class Curl {
    /**
     * @param url
     * @param ua
     */
    static {
        System.loadLibrary("nativeslib");
    }
    public static native int trace(String url,String ua);
    public static native void tracereset();
    public static native String tracegetres(int code);
    public static native String getfinalip();
}
