package com.wmlive.hhvideo.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.wmlive.hhvideo.DCApplication;

import java.util.Map;

/**
 * Created by lsq on 3/9/2017.
 * <p>
 * SharedPreferences操作类
 */

public class SPUtils {
    public static String sSPFileName = "app_preferences";
    private static String sEncryptKey = "WqNmLgDs13";

    public static String COOKIES = "COOKIES";

    public static final String KEY_UNIQUE_DEVICE_ID = "key_show_damanku";//设备ID
    public static final String KEY_NEW_UNIQUE_DEVICE_ID = "key_new_device_id";//设备ID

    public static final String KEY_SHOW_DAMANKU = "key_show_damanku";//是否显示弹幕，默认显示弹幕
    public static final String KEY_APP_LAUNCH_COUNT = "key_app_launch_count";
    public static final String FRAME_LAYOUT_DATA = "frame_layout_data";//画框信息
    public static final String SPLASH_RESOURCE_DATA = "splash_resource_data";//splash资源信息

    public static final String KEY_OLD_APP_VERSION = "key_old_app_version";  //升级前版本
    public static final String KEY_CURRENT_APP_VERSION = "key_current_app_version"; //升级后版本

    public static final String KEY_SHOW_GUIDE = "key_show_guide";
    public static final String KEY_LATEST_GET_DISCOVERY_MSG = "key_latest_get_discovery_msg";
    public static final String KEY_LATEST_DISCOVERY_NEWS_TIME = "key_latest_discovery_news_time";

    public static final String CREATIVE_ZIP_LIST = "creative_zip_list";

    public static final String CREATIVE_DEFALT_ZIP = "creative_defalt_zip";
    public static final String CREATIVE_DEFALT_TEMPLATE_NAME = "creative_defalt_template_name";
    public static final String CREATIVE_DEFALT_TEMPLATEBEAN = "creative_defalt_templatebean";

    //是否是第一次安装
    public static final String ISFIRSTINSTALL = "isfirstinstall";
    //版本号
    public static final String VERSIONCODE = "versioncode";


    private static SharedPreferences sSharedPreferences;

    /**
     * 务必在Application中初始化这个方法
     *
     * @param encryptKey 加密的key，为null时使用随机的key
     * @param fileName   保存的文件名
     */
    public static void init(String encryptKey, String fileName) {
        sEncryptKey = encryptKey;
        if (!TextUtils.isEmpty(fileName)) {
            sSPFileName = fileName;
        }
        sSharedPreferences = DCApplication.getDCApp().getSharedPreferences(sSPFileName, Context.MODE_PRIVATE);
    }

    private static void initPreferences() {
        if (null == sSharedPreferences) {
            sSharedPreferences = DCApplication.getDCApp().getSharedPreferences(sSPFileName, Context.MODE_PRIVATE);
        }
    }

    private static SecurePreferences getSecurePreferences(Context context) {
        return new SecurePreferences(context.getApplicationContext(), sEncryptKey, sSPFileName);
    }

    public static void putString(Context context, String key, String value) {
        initPreferences();
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defValue) {
        initPreferences();
        return sSharedPreferences.getString(key, defValue);
    }

    public static void putInt(Context context, String key, int value) {
        initPreferences();
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putInt(key, value).apply();
    }

    public static int getInt(Context context, String key, int defValue) {
        initPreferences();
        return sSharedPreferences.getInt(key, defValue);
    }

    public static void putFloat(Context context, String key, float value) {
        initPreferences();
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putFloat(key, value).apply();
    }

    public static float getFloat(Context context, String key, float defValue) {
        initPreferences();
        return sSharedPreferences.getFloat(key, defValue);
    }

    public static void putLong(Context context, String key, long value) {
        initPreferences();
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putLong(key, value).apply();
    }

    public static long getLong(Context context, String key, long defValue) {
        initPreferences();
        return sSharedPreferences.getLong(key, defValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        initPreferences();
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        initPreferences();
        return sSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 是否含有某个key
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        return getSecurePreferences(context).contains(key);
    }

    /**
     * 一次性存储多个值
     *
     * @param context
     * @param params
     */
    public static void putMultiParmas(Context context, Map<String, Object> params) {
        if (params != null) {
//            SecurePreferences.Editor editor = getSecurePreferences(context).edit();
            initPreferences();
            SharedPreferences.Editor editor = sSharedPreferences.edit();
            for (Map.Entry<String, Object> set : params.entrySet()) {
                if (!TextUtils.isEmpty(set.getKey())) {
                    if (set.getValue() instanceof String) {
                        editor.putString(set.getKey(), (String) set.getValue());
                    } else if (set.getValue() instanceof Integer) {
                        editor.putInt(set.getKey(), (Integer) set.getValue());
                    } else if (set.getValue() instanceof Boolean) {
                        editor.putBoolean(set.getKey(), (Boolean) set.getValue());
                    } else if (set.getValue() instanceof Float) {
                        editor.putFloat(set.getKey(), (Float) set.getValue());
                    } else if (set.getValue() instanceof Long) {
                        editor.putLong(set.getKey(), (Long) set.getValue());
                    }
                }
            }
            editor.apply();
        }
    }
}
