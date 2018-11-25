package com.wmlive.hhvideo.utils;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vhawk on 2017/5/23.
 */
public class RegexUtil {


//    public static boolean isValidMobile(String mobile) {
//        Pattern p = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");
//        Matcher m = p.matcher(mobile);
//        return m.matches();
//    }

    /**
     * 是否为有效手机号
     *
     * @param phoneNum
     * @return
     */
    public static boolean isValidMobile(String phoneNum) {
//        String pattern = "^(((13[0-9])|(14[5,7])|(15([0-3]|[5-9]))|(16[0-9])|(17[0-9])|(18[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";
        String pattern = "^1\\d{10}$";
        return phoneNum.matches(pattern);
    }


    /**
     * 是否是url
     *
     * @param s
     * @return
     */
    public static boolean isUrl(String s) {
        return Patterns.WEB_URL.matcher(s).matches();
//        String p = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
//        return s.matches(p);
    }

    /**
     * 是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainsChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile(".*[\u4e00-\u9fa5]+.*");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }
}
