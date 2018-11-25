package com.wmlive.hhvideo.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @author fengbb (http://www.6clue.com/)
 * @version 1.0
 * @created 2014-02-25 17:15:42
 */
@SuppressLint({"NewApi", "DefaultLocale"})
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StringUtils {

    public final static int BUFFER_SIZE = 4096;

    public final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    @SuppressLint("SimpleDateFormat")
    public final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    @SuppressLint("SimpleDateFormat")
    public final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    @SuppressLint("SimpleDateFormat")
    public final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yy-MM-dd HH:mm");// yy/MM/dd HH:mm
        }
    };

    @SuppressLint("SimpleDateFormat")
    public final static ThreadLocal<SimpleDateFormat> dateFormater4 = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");// yyyy.MM.dd
        }
    };

    @SuppressLint("SimpleDateFormat")
    public final static ThreadLocal<SimpleDateFormat> dateFormater5 = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd HH:mm");// MM/dd HH:mm
        }
    };
    @SuppressLint("SimpleDateFormat")
    public final static ThreadLocal<SimpleDateFormat> dateFormater6 = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy.MM.dd HH:mm");// yyyy.MM.dd HH:mm
        }
    };
    @SuppressLint("SimpleDateFormat")
    public final static ThreadLocal<SimpleDateFormat> dateFormater7 = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmm");// yyyyMMddHHmm
        }
    };

    @SuppressLint("SimpleDateFormat")
    public final static ThreadLocal<SimpleDateFormat> dateFormater8 = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");// dd-MMM-yyyy
            // HH:mm:ss:SSS
        }
    };

    public static Spannable createTimeSpannable(final CharSequence msg) {
        Spannable span = new SpannableString((msg != null ? msg : ""));
        span.setSpan(new AbsoluteSizeSpan(10, true), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static Spannable createSpannable(final CharSequence msg, final int color) {
        Spannable span = new SpannableString((msg != null ? msg : ""));
        span.setSpan(new ForegroundColorSpan(color), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }


    private static final String TAG = "StringUtils";

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    //===============================录音所需================================================

    /**
     * 将文件转成base64字符串(用于发送语音文件)
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    /**
     * 将base64编码后的字符串转成文件
     *
     * @param base64Code
     * @throws Exception
     */
    public static String decoderBase64File(String base64Code) {
        String savePath = "";
        String saveDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yun/Sounds/";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        savePath = saveDir + getRandomFileName();
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        try {
            FileOutputStream out = new FileOutputStream(savePath);
            out.write(buffer);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savePath;
    }

    public static String getRandomFileName() {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        rel = rel + new Random().nextInt(1000);
        return rel + ".aac";
    }
    //=================================录音所需结束====================================================

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     *
     * @return
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    /**
     * 返回long类型 BigDecimal mData = new BigDecimal("1401269772.1934").setScale(0,
     * BigDecimal.ROUND_FLOOR);
     *
     * @return
     */
    public static long getDToL(String time) {
        if (time != null && time.contains(".")) {
            BigDecimal mData = new BigDecimal(time).setScale(0, BigDecimal.ROUND_FLOOR);
            return Long.parseLong(mData.toString());
        }

        return Long.parseLong(time);
    }

    /**
     * 将时间转出为14位，传入的时间最少到秒(10位) 某个字符串小数点后不足4位的补足四位,并去掉".",最后转化为Long类型
     * 如1234.5---->12345000 如1234567891--->12345678910000
     *
     * @param data
     * @return
     */
    public static Long removePointbig10000ToLong(String data) {
        if (TextUtils.isEmpty(data)) {
            return 0l;
        }
        if (data.contains("E") || data.contains("e")) {
            BigDecimal decimal = new BigDecimal(data);
            data = decimal.toPlainString();
        }
        int last = data.lastIndexOf(".");
        if (last != -1) {

            int count = data.length() - 1 - last;
            if (count == 0) {
                data = data + "0000";
            } else if (count == 1) {
                data = data + "000";
            } else if (count == 2) {
                data = data + "00";
            } else if (count == 3) {
                data = data + "0";
            }
        } else {
            if (data.length() == 10) {
                data = data + "0000";
            } else if (data.length() == 13) {
                data = data + "0";
            }
        }
        return Long.parseLong(removePoint(data));

    }

    /**
     * 去掉"."
     *
     * @param data
     * @return
     */
    public static String removePoint(String data) {
        if (data != null && data.contains(".")) {
            return data.replace(".", "");
        }
        return data;

    }

    /**
     * 去除手机号+86
     *
     * @return
     */
    public static String removePrefix86(String phoneNum) {
        try {
            Pattern pattern = Pattern.compile("^((\\+{0,1}86){0,1})");
            Matcher matcher = pattern.matcher(phoneNum);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, "");
            }
            matcher.appendTail(sb);

            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * format by pattern
     *
     * @param obj
     * @return
     */
    public static String format(Object obj) {
        try {
            if (obj instanceof String || obj instanceof Integer || obj instanceof Float)
                return dateFormater3.get().format(Double.parseDouble(String.valueOf(obj)) * 1000);
            else if (obj instanceof Date)
                return dateFormater3.get().format(obj);
            return dateFormater3.get().format(0);
        } catch (NumberFormatException e) {
        }
        return "null";
    }

    /**
     * format by pattern
     *
     * @param obj
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String format2(String obj) {
        try {
            // LogUtil.d(TAG,"obj:"+obj);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Date curSysDate = sdf.parse(sdf.format(System.currentTimeMillis()));
            Date curDate = sdf.parse(sdf.format(Double.parseDouble(String.valueOf(obj)) * 1000));
            // LogUtil.d(TAG,"curDate.before(curSysDate)"+curDate.before(curSysDate));
            if (curDate.before(curSysDate)) {
                return dateFormater3.get().format(Double.parseDouble(String.valueOf(obj)) * 1000);
            } else {
                return dateFormater5.get().format(Double.parseDouble(String.valueOf(obj)) * 1000);
            }

        } catch (NumberFormatException e) {
        } catch (ParseException e) {
        }
        return "null";
    }

    /**
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
     *
     * @method: 判断手机号码是否正确 @2014-3-26 14:25:16
     * @author fengbb
     */
    public static boolean isValidMobiNumber(String paramString) {
        // String regex = "^1\\d{10}$";
        // String regex = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        String regex = "^((1[^(0-2),\\D]))\\d{9}$";
        if (paramString.matches(regex)) {
            return true;
        }
        return false;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equals(input) || "NULL".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    public static boolean isBlackChar(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * @param content
     * @return
     */
    public static String getSafeString(String content) {
        return null == content ? "" : content;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    public static double toDouble(String str, Double defValue) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    public static String toString(String str, String defValue) {
        if (str != null && !str.equals("") && !str.equals("null")) {
            return str;
        }

        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * @param des   目标字符串
     * @param index 检索字符串
     * @return 数组
     * @method: 获取字符串中字符串的起始和终结位置 @2013-12-13 下午6:44:52
     * @author mike
     */
    public static int[] getStringIndex(String des, String index) {
        int[] result = new int[2];

        int start = des.indexOf(index);
        int end = start + index.length();
        result[0] = start;
        result[1] = end;
        return result;
    }

    /**
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str the String to capitalize, may be null
     * @return the capitalized String, {@code null} if null String input
     * @since 2.0
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return new StringBuilder(strLen).append(Character.toTitleCase(firstChar)).append(str.substring(1)).toString();
    }

    /**
     * <p>
     * Checks if a CharSequence is whitespace, empty ("") or null.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 3.0 Changed signature from isBlank(String) to
     * isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line);
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /**
     * 将InputStream转换成某种字符编码的String
     *
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in, String encoding) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), encoding);
    }

    /**
     * 范例：http://service.demai.com/spx/6/532abb1a7f8b9a35b68b4567.spx
     * 从url中获取音频的长度
     *
     * @param url
     * @return
     */
    public static Long getAudioLenFromUrl(String url) {
        long len = 0;
        if (TextUtils.isEmpty(url)) {
            len = 0;
        } else {

            int lastSlashIndex = url.lastIndexOf("/");
            if (lastSlashIndex > 0) {
                String newUrl = url.substring(0, lastSlashIndex);
                lastSlashIndex = newUrl.lastIndexOf("/");
                String strLen = newUrl.substring(lastSlashIndex + 1);
                boolean isNum = strLen.matches("[0-9]+");
                if (isNum) {
                    len = Long.valueOf(strLen);
                }
            }
        }
        return len;
    }

    /***
     * true 不为空 false 空
     *
     * @param result
     * @return
     */
    public static boolean StrTxt(String result) {
        if (result != null && !result.equals("") && !result.equals("null") && !result.equals("no request")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 实现文本复制功能 Build.VERSION.SDK_INT add by fengbb
     * SDK11起android.text.ClipboardManager被废弃，使用它的子类android.content.
     * ClipboardManager替代，
     * 同样被废弃还有setText/getText/hasText方法，使用setPrimaryClip/getPrimaryClip
     * /hasPrimaryClip替代
     *
     * @param content
     */
    @SuppressWarnings("deprecation")
    public static void copy(String content, Context context) {
        if (Build.VERSION.SDK_INT < 11) {
            // 得到剪贴板管理器
            // LogUtil.d(UIUtils.TAG, "----小于11");
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content.trim());
        } else {
            // LogUtil.d(UIUtils.TAG, "----大于11");
            // 得到剪贴板管理器
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setPrimaryClip(ClipData.newPlainText(null, content.trim()));
        }
    }

    /**
     * 实现粘贴功能 add by fengbb
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String paste(Context context) {
        if (Build.VERSION.SDK_INT < 11) {
            // 得到剪贴板管理器
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            return cmb.getText().toString().trim();
        } else {
            // 得到剪贴板管理器
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cmb.hasPrimaryClip()) {
                return (String) cmb.getPrimaryClip().getItemAt(0).getText();
            }
        }
        return "";
    }

    /***
     * 判断sdk版本 大于11 true 小于11 false
     *
     * @return
     */
    public static boolean isSDK_INT() {
        if (Build.VERSION.SDK_INT < 11) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 返回当前的日期：dayOfMonth,month,year,dayOfYear
     *
     * @return
     */
    public static int[] getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;// 几月
        int year = cal.get(Calendar.YEAR);// 年份
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);// 一个月的第几天
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);// 一年中的第几天
        return new int[]{dayOfMonth, month, year, dayOfYear};
    }

    /**
     * 根据月，日计算当前日期是一年中第多少天
     *
     * @param month
     * @param day
     * @return
     */
    public static int getDayOfYear(int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(getCurrentDate()[2], month - 1, day);// month计算时要减去1
        int dayOfMonth = cal.get(Calendar.DAY_OF_YEAR);// 一年中的第几天
        return dayOfMonth;
    }

    /**
     * 通过与当前日期比较，显示
     *
     * @return
     */
    public static String getCustomBirthdayInfo(int month, int day, int dayOfYear) {
        StringBuilder info = new StringBuilder();
        info.append(month + "月" + day + "日");
        int todayInYear = getCurrentDate()[3];
        if (dayOfYear >= todayInYear) {
            int offset = dayOfYear - todayInYear;
            if (offset == 0) {
                info.append("(今天)");
            } else if (offset == 1) {
                info.append("(明天)");
            } else if (offset == 2) {
                info.append("(后天)");
            } else {
                info.append("(" + offset + "天后)");
            }

        }
        return info.toString();
    }

    /***
     * 从字符串中提取数字
     *
     * @return
     */
    public static String getStringNum(String string) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.replaceAll("").trim();
    }

    public static int getStringLength(String string) {
        if (isEmpty(string)) {
            return 0;
        }
        return string.length();
    }

    /***
     * true 不为空 false 空
     *
     * @param result
     * @return
     */
    public static String StrTxtRp(Integer result) {
        return result + "";
    }

    /***
     * 首页 判断 赞、评论、分享、收藏
     *
     * @return
     */
    public static boolean getHINum2(Integer count) {
        if (count > 0) {
            return true;
        }

        return false;
    }

    /*
     * 取出[] 实际上大放[30]所得税[hs]健康卡[89]叫姐姐[43]";
     *
     * @return
     */
    public static List<String> getPatternLists(String str) {
        List<String> tlist = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\[(.+?)\\]");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String string = matcher.group();
            tlist.add(string.substring(1, string.length() - 1));
        }

        return tlist;
    }

    /***
     * 去掉.0
     *
     * @param str
     * @return
     */
    public static Integer RepToInt(String str) {
        // LogUtil.d("str:"+str);
        try {
            if (str == null || str.equals("")) {
                return null;
            }
            if (str.contains(".0")) {
                return Integer.parseInt(str.replaceAll("\\.0", ""));
            } else {
                return Integer.parseInt(str);
            }
        } catch (Exception e) {
        }

        return null;
    }

    /***
     * 截掉.后面的数据
     *
     * @param str
     * @return
     */
    public static Integer catToInt(String str) {
        // LogUtil.d("str:"+str);
        try {
            if (str.contains(".")) {
                return Integer.parseInt(str.substring(0, str.indexOf(".")));
            } else {
                return Integer.parseInt(str);
            }
        } catch (Exception e) {
        }

        return null;
    }

    /***
     * 去掉.0
     *
     * @param str
     * @return
     */
    public static Long RepToLong(String str) {
        // LogUtil.d("str:"+str);
        try {
            if (str == null || str.equals("")) {
                return null;
            }
            if (str.contains(".0")) {
                return Long.parseLong(str.replaceAll("\\.0", ""));
            } else {
                return Long.parseLong(str);
            }
        } catch (Exception e) {
        }

        return null;
    }

    /***
     * 去掉.后面的数字
     *
     * @param str
     * @return
     */
    public static Long RepToLong2(String str) {
        try {
            if (str == null || str.equals("")) {
                return 0l;
            }
            if (str.contains(".")) {
                return Long.parseLong(str.substring(0, str.indexOf(".")));
            } else {
                return Long.parseLong(str);
            }
        } catch (Exception e) {
            return 0l;
        }

    }

    /**
     * 给字符串从右边开始，每3个加个逗号 例如： 12,345 ； 123,456 ； 123,456,789
     *
     * @param str
     * @return
     */
    public static String AddDouhao(String str) {
        if (str.length() < 4) {
            return str;
        }
        String[] split = str.split("");
        String[] split1 = new String[split.length - 1];
        for (int i = 1; i < split.length; i++) {
            split1[i - 1] = split[i];
        }
        int start = 0;
        String[] myArr;
        if (split1.length % 3 == 0) {
            start = 3;
            myArr = new String[split1.length / 3];
        } else {
            start = split1.length % 3;
            myArr = new String[split1.length / 3 + 1];
        }
        int j = 0;

        for (int i = start; i < (split1.length + 1); i += 3) {
            if (i == 1) {
                myArr[j] = split1[0] + ",";
            } else if (i == 2) {
                myArr[j] = split1[0] + split1[1] + ",";
            } else {

                myArr[j] = split1[i - 3] + split1[i - 2] + split1[i - 1] + ",";
            }
            j++;
        }
        StringBuffer sbBuffer = new StringBuffer();
        for (int i = 0; i < myArr.length; i++) {
            sbBuffer.append(myArr[i]);
        }

        return sbBuffer.toString().substring(0, sbBuffer.toString().length() - 1);

    }

    /**
     * 程序是否在前台运行 true 运行 false 不运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context mContext) {
        ActivityManager activityManager = (ActivityManager) mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        // String packageName =
        // mContext.getApplicationContext().getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (/*
                 * appProcess.processName.equals(SERVICEPARAMS.PROCESSNAME) &&
				 */(appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcess.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE)) {
                return true;
            }
        }

        return false;
    }

    /***
     * 适配4.4一下版本图片获取和以上的图片获取方式 isfile = true
     * uri:file:///storage/emulated/0/DCIM/
     * Screenshots/Screenshot_2014-08-22-17-42-09.png isfile = false
     * uri:/storage/emulated/0/DCIM/Screenshots/Screenshot_2014-08
     * -22-17-42-09.png
     *
     * @param uri
     * @return
     */
    protected final static boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    public static String getImgPath(Uri uri, Context context, boolean isfile) {
        CursorLoader loader = null;
        String img_path = "";
        String[] proj = {MediaStore.Images.Media.DATA};

        // 判断是否选择的是文件
        if (uri.toString().startsWith("file")) {
            return uri.toString();
        }

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + " = ?";
            loader = new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
        } else {
            loader = new CursorLoader(context, uri, proj, null, null, null);
        }
        Cursor actualimagecursor = loader.loadInBackground();
        int actual_image_column_index = actualimagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
        if (actualimagecursor.moveToFirst()) {
            img_path = actualimagecursor.getString(actual_image_column_index);
        }
        cursorClose(actualimagecursor);

        if (isfile) {
            return "file:///" + img_path;
        }
        return img_path;
    }

    /***
     * 关闭数据库查询对象
     *
     * @param c
     */
    public static void cursorClose(Cursor c) {
        if (c != null) {
            c.close();
        }
    }

    /***
     * 显示软键盘
     */
    public static void showSoftKeyBoard(Activity _mActivity) {
        try {
            InputMethodManager imm = (InputMethodManager) _mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 显示软键盘
     */
    public static void hideSoftKeyBoard(Activity _mActivity, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) _mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 反射实体里面所有属性和值
     *
     * @param obj
     * @return
     */
    public static <T> String toString(T obj) {
        StringBuffer sb = new StringBuffer();
        try {
            // 初始化列名
            Class<? extends Object> clasz = obj.getClass();
            Field[] fields = clasz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                String key = f.getName();
                String value = f.get(obj) + "";
                sb.append(key + "=" + value + ",");
            }
        } catch (SecurityException e) {
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        }
        return sb.toString();
    }

    /**
     * 获取字符串的长度，中文占一个字符,英文数字占半个字符
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static double length(String value) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < value.length(); i++) {
            // 获取一个字符
            String temp = value.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        // 进位取整
        return Math.ceil(valueLength);
    }

    /***
     * 提取字符串里面所有中文
     *
     * @return
     */
    public static String getZW(String str) {
        String regex = "([\u4e00-\u9fa5]+)";
        // String str = "1[32[更新至]45ddd]6";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if (matcher.find()) {
            // System.out.println(matcher.group(0));

            return matcher.group(0);
        }

        return "";
    }

    // android 4.0
    public static String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        String jsonStr = in;

        return jsonStr;
    }

    /**
     * 对List集合元素去重
     *
     * @param oldList
     * @return
     */
    public static <T> ArrayList<T> quChong(ArrayList<T> oldList) {
        if (oldList != null && oldList.size() > 0) {
            for (int i = 0; i < oldList.size() - 1; i++) {
                for (int j = i + 1; j < oldList.size(); j++) {
                    if (oldList.get(i).equals(oldList.get(j))) {
                        oldList.remove(j);
                        j--;
                    }
                }
            }
        }
        return oldList;

    }

    /**
     * 用"|"切割字符串
     *
     * @param string
     * @return
     */
    public static List<String> splitStr(String string) {
        String[] split = string.split("\\|");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            list.add(split[i]);
        }
        return list;

    }

    /**
     * 将一个数组转化成List集合
     *
     * @param array
     * @return
     */
    public static <T> ArrayList<T> toList(T[] array) {
        ArrayList<T> list = new ArrayList<T>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;

    }

    /**
     * 判断某个元素是否在指定List的集合中
     *
     * @param list
     * @param element
     * @return
     */
    public static <T> boolean isInList(List<T> list, T element) {
        return list.contains(element);
    }

    /****
     * sqlite的特殊字符转义及通配符 / -> // ' -> '' [ -> /[ ] -> /] % -> /% & -> /& _ -> /_
     * ( -> /( ) -> /)
     *
     * @param keyWord
     * @return
     */
    public static String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&", "/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }

    public static String sqliteEscapeReverse(String keyWord) {
        keyWord = keyWord.replace("//", "/");
        keyWord = keyWord.replace("''", "'");
        keyWord = keyWord.replace("/[", "[");
        keyWord = keyWord.replace("/]", "]");
        keyWord = keyWord.replace("/%", "%");
        keyWord = keyWord.replace("/&", "&");
        keyWord = keyWord.replace("/_", "_");
        keyWord = keyWord.replace("/(", "(");
        keyWord = keyWord.replace("/)", ")");
        return keyWord;
    }

    /***
     * 用于计算新浪微博、腾讯微博、身份认证、名片认证等
     *
     * @param flag
     * @param position
     * @return
     */
    public static boolean isSetDescFlag(int flag, int position) {
        int set = flag >> (position - 1);
        if (set % 2 == 1) {
            return true;
        }
        return false;
    }

    /**
     * 取SD卡路径
     **/
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    /**
     * 获取
     *
     * @param url
     * @return
     */
    public static int[] getImgSizeFromUri(String url) {
        int width = 0;
        int height = 0;
        if (url != null) {
            String[] arr = url.split("/");
            /*
             * String widthStr = list.get(list.size()-3); String heightStr =
			 * list.get(list.size()-2);
			 */
            try {
                String widthStr = arr[arr.length - 3];
                String heightStr = arr[arr.length - 2];
                width = Integer.parseInt(widthStr);
                height = Integer.parseInt(heightStr);
            } catch (Exception e) {
            }
        }
        return new int[]{width, height};

    }

	/*
     * 把十六进制Unicode编码字符串转换为中文字符串
	 */

    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);

        char ch;

        while (matcher.find()) {

            ch = (char) Integer.parseInt(matcher.group(2), 16);

            str = str.replace(matcher.group(1), ch + "");

        }

        return str;

    }

    public static String formatUnicodeCode(String str) {
        if (str == null || str.isEmpty() || str.length() % 5 != 0) {
            return str;
        }

        for (int i = 0; i < str.length(); i += 5) {
            if (str.charAt(i) != 'u') {
                return str;
            }
        }

        str = str.replaceAll("u", "\\\\u");

        return StringUtils.unicodeToString(str);

    }

    /**
     * 分转元
     *
     * @param coinint
     * @return
     */
    public static String fen2yuan(int coinint) {
        float coin = coinint / 100f;
        DecimalFormat fnum = new DecimalFormat("##0.00");
        String dd = fnum.format(coin);
        if (dd.endsWith(".00")) {
            dd = dd.replace(".00", "");
        }
        return dd;
    }

    /**
     * 从手机号里解析出区号
     *
     * @param phone
     * @return
     */
    public static String parsePhoneCodeFromStr(String phone) {
        String code = "";
        if (!StringUtils.isEmpty(phone)) {
            try {
                String[] strs = phone.split("\\s+");
                if (strs != null && strs.length >= 2) {
                    code = strs[0];
                }
            } catch (Exception e) {

            }

        }
        return code;
    }

    /**
     * 从手机号里解析出真正的手机号
     *
     * @param phone
     * @return
     */
    public static String parseRealPhoneFromStr(String phone) {
        String realPhone = phone;
        if (!StringUtils.isEmpty(phone)) {
            try {
                String[] strs = phone.split("\\s+");
                if (strs != null && strs.length >= 2) {
                    realPhone = strs[1];
                }
            } catch (Exception e) {

            }

        }
        return realPhone;
    }

    public static String trim(String str) {
        if (str == null)
            return "";
        return str.trim();
    }

    public static final String[] zodiacArr = {"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"};

    public static final String[] constellationArr = {"水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"};

    public static final int[] constellationEdgeDay = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};

    /**
     * 根据日期获取生肖
     *
     * @return
     */
    public static String getZodica(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return zodiacArr[cal.get(Calendar.YEAR) % 12];
    }

    /**
     * 根据日期获取星座
     *
     * @return
     */
    public static String getConstellation(Date date) {
        if (date == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        // default to return 魔羯
        return constellationArr[11];
    }

    /**
     * 年月日转date
     *
     * @param ymdStr
     * @return
     */
    public static Date parseYMD(String ymdStr) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(ymdStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String parseYMD(long times) {
        Date startDate = new Date(times * 1000);
        DateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd");
        String timeResult = startFormat.format(startDate);
        return timeResult;
    }

    /**
     * 中文算两个字节
     *
     * @param s
     * @return
     */
    public static int getLengthOfByteCode(String s) {
        float length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            char ch = s.charAt(i);
            if (isEmojiCharacter(ch)) {
                length += 1.5f;// 表情占3个字节
            } else if (ascii >= 0 && ascii <= 255) {
                length++;
            } else {
                length += 2;// 中文两个字节
            }

        }
        return (int) length;
    }

    /**
     * 是否是emoji表情
     *
     * @param codePoint
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }
}
