package com.wmlive.hhvideo.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by lsq on 5/29/2018 - 7:03 PM
 * 类描述：
 */
public class DcDeviceHelper {

    private static final String TAG = DcDeviceHelper.class.getSimpleName();

    private static final String DEVICE_FILE = ".dcdevice.dat";
    private static final String DEVICE_DIR_NAME = ".wmlive";

    private static final String DEVICE_BACKUP_PATH1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DEVICE_DIR_NAME;
    private static final String DEVICE_BACKUP_PATH2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + DEVICE_DIR_NAME;

    public static String getDeviceId(Context context) {
        String deviceId = null;
        File appDir = context.getExternalFilesDir(null);
        String appDirPath;
        String appDevicePath;
        String bkDevicePath1;
        String bkDevicePath2;
        if (appDir == null) {
            appDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "Android" + File.separator
                    + "data" + File.separator
                    + context.getPackageName();
        } else {
            appDirPath = appDir.getAbsolutePath();
        }
        appDevicePath = insureDir(appDirPath);
        if (!TextUtils.isEmpty(appDevicePath)) {
            deviceId = readDeviceId(appDevicePath);
        }
        if (hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            bkDevicePath1 = insureDir(DEVICE_BACKUP_PATH1);
            bkDevicePath2 = insureDir(DEVICE_BACKUP_PATH2);
            if (!TextUtils.isEmpty(deviceId)) {
                writeDeviceId(bkDevicePath1, deviceId);
                writeDeviceId(bkDevicePath2, deviceId);
                return deviceId;
            } else {
                if (!TextUtils.isEmpty(bkDevicePath1)) {
                    deviceId = readDeviceId(bkDevicePath1);
                }
                if (!TextUtils.isEmpty(deviceId)) {
                    writeDeviceId(appDevicePath, deviceId);
                    writeDeviceId(bkDevicePath2, deviceId);
                    return deviceId;
                } else {
                    if (!TextUtils.isEmpty(bkDevicePath2)) {
                        deviceId = readDeviceId(bkDevicePath2);
                    }
                    if (!TextUtils.isEmpty(deviceId)) {
                        writeDeviceId(appDevicePath, deviceId);
                        writeDeviceId(bkDevicePath1, deviceId);
                        return deviceId;
                    } else {
//                        deviceId = createRandomUUID();
//                        writeDeviceId(appDevicePath, deviceId);
//                        writeDeviceId(bkDevicePath1, deviceId);
//                        writeDeviceId(bkDevicePath2, deviceId);
                        return deviceId;
                    }
                }
            }
        } else {
//            if (TextUtils.isEmpty(deviceId)) {
//                deviceId = createRandomUUID();
//                writeDeviceId(appDevicePath, deviceId);
//            }
        }
        return deviceId;
    }

    public static void directWriteId(Context context, String deviceId) {
        File appDir = context.getExternalFilesDir(null);
        String appDirPath;
        if (appDir == null) {
            appDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "Android" + File.separator
                    + "data" + File.separator
                    + context.getPackageName();
        } else {
            appDirPath = appDir.getAbsolutePath();
        }
        writeDeviceId(insureDir(appDirPath), deviceId);
        if (hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            writeDeviceId(insureDir(DEVICE_BACKUP_PATH1), deviceId);
            writeDeviceId(insureDir(DEVICE_BACKUP_PATH2), deviceId);
        }
    }

    private static String readDeviceId(String devicePath) {
        File deviceFile = new File(devicePath);
        String result = null;
        if (deviceFile.exists() && deviceFile.isFile()) {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder(30);
            try {
                br = new BufferedReader(new FileReader(deviceFile));
                while ((result = br.readLine()) != null) {
                    sb.append(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            result = sb.toString();
        } else {
            try {
                deviceFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        KLog.e(TAG, "=====从目录：" + devicePath + " 获取到的id:" + result);
        return result;
    }

    private static void writeDeviceId(String deviceFilePath, String deviceId) {
        if (TextUtils.isEmpty(deviceFilePath)) {
            return;
        }
        File deviceFile = new File(deviceFilePath);
        if (!deviceFile.exists() || deviceFile.isDirectory()) {
            try {
                deviceFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (deviceFile.exists()) {
            FileWriter fw = null;
            BufferedWriter bw = null;
            try {
                fw = new FileWriter(deviceFile, false);
                bw = new BufferedWriter(fw);
                bw.write(deviceId);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String createRandomUUID() {
        return UUID.randomUUID().toString();
    }

    private static String insureDir(String path) {
        File appDir = new File(path);
        String filePath = null;
        if (!appDir.exists() || !appDir.isDirectory()) {
            appDir.mkdirs();
        }
        if (appDir.exists()) {
            filePath = path + File.separator + DEVICE_FILE;
        }
        return filePath;
    }

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
