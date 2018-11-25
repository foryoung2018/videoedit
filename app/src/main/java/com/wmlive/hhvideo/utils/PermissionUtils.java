package com.wmlive.hhvideo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by lsq on 6/13/2017.
 * 运行时权限工具
 */

public class PermissionUtils {
    /**
     * 以下是危险权限，需要运行时申请
     * group:android.permission-group.CONTACTS
     * permission:android.permission.WRITE_CONTACTS
     * permission:android.permission.GET_ACCOUNTS
     * permission:android.permission.READ_CONTACTS
     * <p>
     * group:android.permission-group.PHONE
     * permission:android.permission.READ_CALL_LOG
     * permission:android.permission.READ_PHONE_STATE
     * permission:android.permission.CALL_PHONE
     * permission:android.permission.WRITE_CALL_LOG
     * permission:android.permission.USE_SIP
     * permission:android.permission.PROCESS_OUTGOING_CALLS
     * permission:com.android.voicemail.permission.ADD_VOICEMAIL
     * <p>
     * group:android.permission-group.CALENDAR
     * permission:android.permission.READ_CALENDAR
     * permission:android.permission.WRITE_CALENDAR
     * <p>
     * group:android.permission-group.CAMERA
     * permission:android.permission.CAMERA
     * <p>
     * group:android.permission-group.SENSORS
     * permission:android.permission.BODY_SENSORS
     * <p>
     * group:android.permission-group.LOCATION
     * permission:android.permission.ACCESS_FINE_LOCATION
     * permission:android.permission.ACCESS_COARSE_LOCATION
     * <p>
     * group:android.permission-group.STORAGE
     * permission:android.permission.READ_EXTERNAL_STORAGE
     * permission:android.permission.WRITE_EXTERNAL_STORAGE
     * <p>
     * group:android.permission-group.MICROPHONE
     * permission:android.permission.RECORD_AUDIO
     * <p>
     * group:android.permission-group.SMS
     * permission:android.permission.READ_SMS
     * permission:android.permission.RECEIVE_WAP_PUSH
     * permission:android.permission.RECEIVE_MMS
     * permission:android.permission.RECEIVE_SMS
     * permission:android.permission.SEND_SMS
     * permission:android.permission.READ_CELL_BROADCASTS
     */


    /**
     * 是否有权限，每次申请一组权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(final @NonNull Context context, final @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求权限
     *
     * @param activity
     * @param permission
     * @param requestCode
     */
    public static void requestPermission(final @NonNull Activity activity, final @NonNull String permission, final @IntRange(from = 0) int requestCode) {
        if (!hasPermission(activity, permission)) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    /**
     * 是否有读写SD卡权限
     *
     * @param context
     * @return
     */
    public static boolean hasSDCardRWPermission(final @NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return true;
    }
}
