package com.wmlive.hhvideo.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.wmlive.hhvideo.BuildConfig;

/**
 * Header 信息公共管理类
 * Created by kangzhen on 2017/7/10.
 */

public class HeaderUtils {
    private static volatile String sLocation = "0.0,0.0";//经纬度

    private static volatile String sCity = "未知城市";//城市

    /**
     * app名称
     *
     * @return
     */
    public static String getAppName() {
        return "hhvideo";
    }

    /**
     * 版本号
     *
     * @return
     */
    public static String getAppVersion() {
        return checkValue(BuildConfig.VERSION_NAME);
    }

    /**
     * 操作系统版本号
     *
     * @return
     */
    public static String getOsVersion() {
        return checkValue(Build.VERSION.RELEASE);
    }

    /**
     * 系统平台
     *
     * @return
     */
    public static String getOsPlatform() {
        return "android";
    }

    /**
     * 设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return checkValue(Build.MODEL);
    }

    /**
     * 获取
     *
     * @return
     */
    public static String getDeviceIdMsg() {
        return checkValue(GlobalParams.StaticVariable.sUniqueDeviceId);
    }

    /**
     * 设备分辨率
     *
     * @return
     */
    public static String getDeviceResolution() {
        try {
            int wh[] = DeviceUtils.getScreenWH(DCApplication.getDCApp());
            return wh[0] + "x" + wh[1];
        } catch (Exception e) {

        }
        return "0x0";
    }

    /**
     * 获取当前网络类型
     *
     * @return
     */
    public static String getDeviceAc() {
        return checkValue(DeviceUtils.getDeviceAc());
    }

    /**
     * api请求版本
     *
     * @return
     */
    public static String getApiVersion() {
        return checkValue(GlobalParams.StaticVariable.sApiServiceVersion);
    }

    /**
     * app 的build版本（version_code）
     *
     * @return
     */
    public static String getBuildNumber() {
        return checkValue(String.valueOf(BuildConfig.VERSION_CODE));
    }

    /**
     * 渠道
     *
     * @return
     */
    public static String getChannel() {
        return checkValue(GlobalParams.StaticVariable.CHANNEL_CODE.toLowerCase());
    }

    /**
     * 获取经纬度
     *
     * @return
     */
    public static String getLocationInfo() {
        return checkValue(sLocation);
    }

    public static void updateLatlon(double lat, double lon, String city) {
        sLocation = lat + "," + lon;
        sCity = city;
    }

    public static String getCity() {
        return sCity;
    }

    private static String checkValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return "null";
        }
        String newValue = value.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                try {
                    return URLEncoder.encode(newValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return "null";
                }
            }
        }
        return newValue;
    }
}
