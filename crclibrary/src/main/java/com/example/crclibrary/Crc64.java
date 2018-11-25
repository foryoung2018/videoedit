package com.example.crclibrary;

/**
 * Author：create by jht on 2018/9/5 22:04
 * Email：haitian.jiang@welines.cn
 */
public class Crc64 {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native long aoscrc64combine(long crc1,long crc2,long len2);
    public static native String aoscrc64(long crc,byte[] buf,long len);
}
