package com.wmlive.hhvideo.common.ossutils;

import android.text.TextUtils;

/**
 * Created by kangzhen on 2017/6/21.
 */

public class OSSUtils {
    public static String getOssContentTypeByFormat(String format) {
        if (TextUtils.isEmpty(format)) {
            return "application/octet-stream";
        } else if ("mp4".equals(format.toLowerCase())) {
            return "video/mp4";
        } else if ("jpg".equals(format.toLowerCase())) {
            return "application/x-jpg";
        } else {
            return "application/octet-stream";
        }
    }
}
