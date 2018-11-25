package com.wmlive.hhvideo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class DeviceUtils {


    /**
     * 获取手机型号
     *
     * @return the user_Agent
     */
    public static String getDevice() {
        return Build.MODEL;
    }


    /**
     * 获取手机品牌
     *
     * @return the vENDOR
     */
    public static String getVendor() {
        return Build.BRAND;
    }


    /**
     * 获取Android SDK版本
     *
     * @return the SDK version
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }


    /**
     * 获取系统版本
     *
     * @return the OS version
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getImei(Context context) {
        String imeiRes = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imeiRes = telephonyManager.getDeviceId();
            if (imeiRes == null || imeiRes.trim().length() == 0 || imeiRes.matches("0+")) {
                imeiRes = (new StringBuilder("EMU")).append((new Random(System.currentTimeMillis())).nextLong())
                        .toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeiRes;
    }

    /**
     * 获取CPU核心数
     *
     * @return
     */
    public static int getCPUCores() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }


    //获取CPU可运行最大频率
    public static int getCPUMaxHz() {
        final String MaxPath = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(MaxPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public static int getCPULevel() {
        int cpuMaxHz = getCPUMaxHz();
        if (cpuMaxHz < 1300000) {
            return 3;
        } else if (cpuMaxHz < 1700000) {
            return 2;
        } else {
            return 1;
        }
    }

    public static boolean isMtkCpu() {
        String model = Build.HARDWARE;
        if (!TextUtils.isEmpty(model)) {
            return model.contains("mt") || model.contains("MT");
        }
        return false;
    }

    /**
     * dp convert to px
     */
    public static int dip2px(Context context, float dpValue) {
        if (context == null) {
            context = DCApplication.getDCApp();
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px convert to dp
     */
    public static int px2dip(Context context, float pxValue) {
        if (context == null) {
            context = DCApplication.getDCApp();
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        if (context == null) {
            context = DCApplication.getDCApp();
        }
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        if (context == null) {
            context = DCApplication.getDCApp();
        }
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hiddenKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    public static void showKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 获取屏幕的宽高
     *
     * @param ctx
     * @return
     */
    public static int[] getScreenWH(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return new int[]{point.x, point.y};
    }

    /**
     * 获取状态栏高度
     *
     * @param c
     * @return
     */
    public static int getStatusBarHeight(Context c) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = c.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = c.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 修改状态栏颜色
     *
     * @param activity
     * @param colorResId
     */
    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));
                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {

        }
    }

    //是否有虚拟导航按键
    public static boolean hasNavigationBar(Context ctx) {
        //判断是否有物理返回键、菜单键
        boolean hasMenuKey = ViewConfiguration.get(ctx)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    }

    //获取NavigationBar高度
    public static int getNavigationBarHeight(Context ctx) {
        if (hasNavigationBar(ctx)) {
            Resources resources = ctx.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height",
                    "dimen", "android");
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * <Get the version code for this app >
     *
     * @return
     */
    public static int getVersionCode(Context c) {
        try {
            String packageName = c.getPackageName();
            return c.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * <Get the version name for this app >
     *
     * @return
     */
    public static String getVersionName(Context c) {
        String packageName = c.getPackageName();
        try {
            return c.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUniqueDeviceId() {
        String deviceId = SPUtils.getString(DCApplication.getDCApp(), SPUtils.KEY_UNIQUE_DEVICE_ID, null);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        deviceId = UUID.randomUUID().toString();
        SPUtils.putString(DCApplication.getDCApp(), SPUtils.KEY_UNIQUE_DEVICE_ID, deviceId);
        return deviceId;
    }

    /**
     * 获取设备唯一标示
     * (根据多个设备信息拼装)
     *
     * @return
     */
    public static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) DCApplication.getDCApp().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;
        try {
            tmDevice = tm.getDeviceId();//设备id
        } catch (Exception e) {
            tmDevice = "";
        }

        try {
            tmSerial = tm.getSimSerialNumber();//序列号
        } catch (Exception e) {
            tmSerial = "";
        }
        try {
            androidId = android.provider.Settings.Secure.getString(DCApplication.getDCApp().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            androidId = "";
        }
        String deviceUUId = "";
        if (!TextUtils.isEmpty(tmDevice)) {
            deviceUUId = getSysUUID(tmDevice);
        } else if (!TextUtils.isEmpty(tmSerial)) {
            deviceUUId = getSysUUID(tmSerial);
        } else if (!TextUtils.isEmpty(androidId)) {
            deviceUUId = getSysUUID(androidId);
        } else {
            deviceUUId = getUUID(DCApplication.getDCApp());
        }
        return deviceUUId;
    }

    /**
     * 获取系统的UUId
     *
     * @param strContent
     * @return
     */
    private static String getSysUUID(String strContent) {
        String strUUId = "";
        try {
            UUID deviceUuid = new UUID(strContent.hashCode(), 0l);
            strUUId = deviceUuid.toString();
        } catch (Exception e) {
            strUUId = "";
        }
        return strUUId;
    }

    /**
     * 得到全局唯一UUID
     */
    private static String getUUID(Context context) {
        String uuid = "";
        uuid = SPUtils.getString(context, "sysDefaultUUID", "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            SPUtils.putString(context, "sysDefaultUUID", uuid);
        }
        return uuid;
    }

    /**
     * 获取Application的meta data.
     *
     * @param context
     * @param name
     * @return
     */
    public static String getMetadata(Context context, String name) {
        String meta_data = "";
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            meta_data = appInfo.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return meta_data;
    }


    /**
     * 设备网络
     *
     * @return
     */
    public static String getDeviceAc() {
        ConnectivityManager manager = (ConnectivityManager) DCApplication.getDCApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                return "WIFI";
            } else if (type.equalsIgnoreCase("MOBILE")) {
                NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mobileInfo != null) {
                    switch (mobileInfo.getType()) {
                        case ConnectivityManager.TYPE_MOBILE:// 手机网络
                            switch (mobileInfo.getSubtype()) {
                                case TelephonyManager.NETWORK_TYPE_UMTS:
                                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                                case TelephonyManager.NETWORK_TYPE_HSDPA:
                                case TelephonyManager.NETWORK_TYPE_HSUPA:
                                case TelephonyManager.NETWORK_TYPE_HSPA:
                                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                case TelephonyManager.NETWORK_TYPE_EHRPD:
                                case TelephonyManager.NETWORK_TYPE_HSPAP:
                                    return "3G";
                                case TelephonyManager.NETWORK_TYPE_CDMA:
                                case TelephonyManager.NETWORK_TYPE_GPRS:
                                case TelephonyManager.NETWORK_TYPE_EDGE:
                                case TelephonyManager.NETWORK_TYPE_1xRTT:
                                case TelephonyManager.NETWORK_TYPE_IDEN:
                                    return "2G";
                                case TelephonyManager.NETWORK_TYPE_LTE:
                                    return "4G";
                                default:
                                    return "unknown";
                            }
                    }
                }
            }
        }

        return "";
    }

    /**
     * 获取屏幕的分辨率
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
     * 通知权限 是否开启
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取屏幕横屏、竖屏状态
     *
     * @param activity
     * @return
     */
    public static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public static float getScreenWHRatio() {
        int[] screenWH = getScreenWH(DCApplication.getDCApp());
        return screenWH[1] == 0 ? 0 : screenWH[0] * 1.0f / screenWH[1];
    }

    public static float getScreenHWRatio() {
        int[] screenWH = getScreenWH(DCApplication.getDCApp());
        return screenWH[0] == 0 ? 0 : screenWH[1] * 1.0f / screenWH[0];
    }

}
