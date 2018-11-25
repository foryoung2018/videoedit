//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wmlive.hhvideo.heihei.record.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Process;
import android.os.Build.VERSION;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public final class CoreUtils {
    public static final int VIDEO_SHARE_INPUT_TEXT_MAX_LENGTH = 95;
    private static Context a;
    private static int b;
    public static final int UNCONNECTED = 0;
    public static final int MOBILECONNECTED = 1;
    public static final int WIFICONNECTED = 2;
    public static final int UNKNOWCONNECTED = 3;
    private static boolean c;
    private static String d = "";
    private static String e = "";
    private static int f = 0;
    private static float g = 1.0F;
    private static DisplayMetrics h;
    public static final String REFLESHACTION = "��¼��ע��action";
    private static String i = "";
    private static String j = "";

    public CoreUtils() {
    }

    public static float getPixelDensity() {
        return g;
    }

    public static void init(Context var0) {
        a = var0;
        c = (a.getApplicationInfo().flags & 2) == 2;
        a();
        b = b();
    }

    private static void a() {
        h = new DisplayMetrics();
        WindowManager var0 = (WindowManager)a.getSystemService("window");
        var0.getDefaultDisplay().getMetrics(h);
        g = h.density;
    }

    private static int b() {
        return a.getResources().getConfiguration().orientation;
    }

    public static boolean checkFileExit(String var0) {
        File var1 = new File(var0);
        return var1.exists();
    }

    public static boolean hasKitKat() {
        return VERSION.SDK_INT >= 19;
    }

    public static boolean hasFroyo() {
        return VERSION.SDK_INT >= 8;
    }

    public static boolean hasGingerbread() {
        return VERSION.SDK_INT >= 9;
    }

    public static boolean hasGingerbreadMR1() {
        return VERSION.SDK_INT >= 10;
    }

    public static boolean hasHoneycomb() {
        return VERSION.SDK_INT >= 11;
    }

    public static boolean hasHoneycombMR1() {
        return VERSION.SDK_INT >= 12;
    }

    public static boolean hasIceCreamSandwich() {
        return VERSION.SDK_INT >= 14;
    }

    public static boolean hasJellyBean() {
        return VERSION.SDK_INT >= 16;
    }

    public static boolean hasJELLY_BEAN_MR2() {
        return VERSION.SDK_INT >= 18;
    }

    public static boolean hasL() {
        return VERSION.SDK_INT >= 21;
    }

    public static boolean hasM() {
        return VERSION.SDK_INT >= 23;
    }

    public static int getTargetSdk(Context var0) {
        if (var0 == null) {
            return 0;
        } else {
            ApplicationInfo var1 = var0.getApplicationInfo();
            return var1 != null ? var1.targetSdkVersion : 0;
        }
    }

    public static boolean hasN() {
        return VERSION.SDK_INT >= 24;
    }

    public static String getDeviceInfo(Context var0) {
        if (TextUtils.isEmpty(d)) {
            try {
                String var1 = null;
                JSONObject var2 = new JSONObject();

                try {
                    TelephonyManager var3 = (TelephonyManager)var0.getSystemService("phone");
                    var1 = var3.getDeviceId();
                } catch (Exception var6) {
                    var6.printStackTrace();
                }

                WifiManager var8 = (WifiManager)var0.getSystemService("wifi");
                String var4 = var8.getConnectionInfo().getMacAddress();
                var2.put("mac", var4);
                if (TextUtils.isEmpty(var1)) {
                    var1 = var4;
                }

                if (TextUtils.isEmpty(var1)) {
                    var1 = Secure.getString(var0.getContentResolver(), "android_id");
                }

                var2.put("device_id", var1);
                MessageDigest var5 = MessageDigest.getInstance("MD5");
                var5.update(var2.toString().getBytes());
                d = a(var5.digest());
            } catch (Exception var7) {
                var7.printStackTrace();
                d = "";
            }
        }

        return d;
    }

    private static String a(byte[] var0) {
        StringBuilder var1 = new StringBuilder();

        for(int var2 = 0; var2 < var0.length; ++var2) {
            String var3 = Integer.toHexString(255 & var0[var2]);
            if (var3.length() == 1) {
                var1.append('0');
            }

            var1.append(var3);
        }

        return var1.toString();
    }

    public static String getVersionCode(Context var0) {
        if (TextUtils.isEmpty(e)) {
            try {
                PackageInfo var1 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0);
                e = String.valueOf(var1.versionCode);
            } catch (NameNotFoundException var3) {
                var3.printStackTrace();
                e = "";
            }
        }

        return e;
    }

    public static int getVersion_Code(Context var0) {
        if (f == 0) {
            PackageManager var1 = var0.getPackageManager();
            PackageInfo var2 = null;

            try {
                var2 = var1.getPackageInfo(var0.getPackageName(), 0);
                f = var2.versionCode;
            } catch (NameNotFoundException var4) {
                var4.printStackTrace();
                f = 0;
            }
        }

        return f;
    }

    public static InetAddress getLocalIpAddress(Context var0) {
        WifiManager var1 = (WifiManager)var0.getSystemService("wifi");
        WifiInfo var2 = var1.getConnectionInfo();
        int var3 = var2.getIpAddress();

        try {
            return InetAddress.getByName(String.format("%d.%d.%d.%d", var3 & 255, var3 >> 8 & 255, var3 >> 16 & 255, var3 >> 24 & 255));
        } catch (UnknownHostException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static boolean isAppInstalled(Context var0, String var1) {
        PackageManager var2 = var0.getPackageManager();
        List var3 = var2.getInstalledPackages(0);
        ArrayList var4 = new ArrayList();
        if (var3 != null) {
            for(int var5 = 0; var5 < var3.size(); ++var5) {
                String var6 = ((PackageInfo)var3.get(var5)).packageName;
                var4.add(var6);
            }
        }

        return var4.contains(var1);
    }

    public static boolean startApp(Context var0, String var1) {
        try {
            new Intent();
            PackageManager var3 = var0.getPackageManager();
            Intent var2 = var3.getLaunchIntentForPackage(var1);
            var2.setFlags(337641472);
            var0.startActivity(var2);
            return true;
        } catch (Exception var4) {
            return false;
        }
    }

    public static boolean installApp(Context var0, String var1) {
        try {
            Intent var2 = installAppIntent(var1);
            if (var2 != null && var0 != null) {
                var0.startActivity(var2);
            }

            return true;
        } catch (Exception var3) {
            var3.printStackTrace();
            return false;
        }
    }

    public static Intent installAppIntent(String var0) {
        try {
            Intent var1 = new Intent("android.intent.action.VIEW");
            var1.setFlags(268435456);
            var1.setAction("android.intent.action.VIEW");
            var1.setDataAndType(Uri.fromFile(new File(var0)), "application/vnd.android.package-archive");
            return var1;
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static int checkNetworkInfo(Context var0) {
        if (var0 == null) {
            return 3;
        } else {
            ConnectivityManager var1 = (ConnectivityManager)var0.getApplicationContext().getSystemService("connectivity");
            NetworkInfo var2 = var1.getActiveNetworkInfo();
            if (var2 != null && var2.isAvailable()) {
                State var3 = State.UNKNOWN;
                State var4 = State.UNKNOWN;
                NetworkInfo var5 = var1.getNetworkInfo(0);
                if (var5 != null) {
                    var3 = var5.getState();
                }

                var5 = var1.getNetworkInfo(1);
                if (var5 != null) {
                    var4 = var5.getState();
                }

                if (var4.equals(State.CONNECTED)) {
                    return 2;
                } else {
                    return var3.equals(State.CONNECTED) ? 1 : 3;
                }
            } else {
                return 0;
            }
        }
    }

    public static DisplayMetrics getMetrics() {
        int var0 = b();
        if (b != var0) {
            a();
            b = var0;
        }

        return h;
    }

    public static int dpToPixel(float var0) {
        return Math.round(g * var0);
    }

    public static int getSameAspectRatioHeight(Window var0, int var1) {
        Rect var2 = new Rect();
        var0.getDecorView().getWindowVisibleDisplayFrame(var2);
        return var1 * var2.width() / var2.height();
    }

    public static boolean getDebuggable() {
        return c;
    }

    public static String getVersionName(Context var0) {
        if (TextUtils.isEmpty(i)) {
            i = a(var0);
        }

        return i;
    }

    private static String a(Context var0) {
        try {
            PackageInfo var1 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0);
            return var1.versionName;
        } catch (NameNotFoundException var3) {
            var3.printStackTrace();
            return "";
        }
    }

    public static String getReleaseChannel(Context var0) {
        if (TextUtils.isEmpty(j)) {
            String var1 = getMetadata(var0, "UMENG_CHANNEL");
            if (!TextUtils.isEmpty(var1)) {
                j = var1;
                return j;
            } else {
                return "����";
            }
        } else {
            return j;
        }
    }

    public static String getMetadata(Context var0, String var1) {
        try {
            PackageManager var2 = var0.getPackageManager();
            if (var2 != null) {
                ApplicationInfo var3 = var2.getApplicationInfo(var0.getPackageName(), 128);
                if (var3 != null && var3.metaData != null) {
                    return var3.metaData.getString(var1);
                }
            }
        } catch (NameNotFoundException var4) {
            ;
        }

        return null;
    }

    public static String getMd5(String var0) {
        MessageDigest var1 = null;

        try {
            var1 = MessageDigest.getInstance("MD5");
            var1.reset();
            var1.update(var0.getBytes());
        } catch (NoSuchAlgorithmException var5) {
            var5.printStackTrace();
        }

        byte[] var2 = var1.digest();
        StringBuffer var3 = new StringBuffer();

        for(int var4 = 0; var4 < var2.length; ++var4) {
            if (Integer.toHexString(255 & var2[var4]).length() == 1) {
                var3.append("0").append(Integer.toHexString(255 & var2[var4]));
            } else {
                var3.append(Integer.toHexString(255 & var2[var4]));
            }
        }

        return var3.toString();
    }

    public static boolean assetRes2File(AssetManager var0, String var1, String var2) {
        FileOutputStream var3 = null;

        try {
            var3 = new FileOutputStream(var2);
            byte[] var4 = new byte[1024];
            if (var0 == null) {
                return false;
            }

            InputStream var6 = var0.open(var1);

            int var5;
            while((var5 = var6.read(var4)) != -1) {
                var3.write(var4, 0, var5);
            }

            var3.flush();
            var3.close();
            var6.close();
        } catch (FileNotFoundException var18) {
            var18.printStackTrace();
        } catch (IOException var19) {
            var19.printStackTrace();
        } finally {
            try {
                if (var3 != null) {
                    var3.close();
                }
            } catch (IOException var17) {
                var17.printStackTrace();
            }

        }

        return true;
    }

    public static long getAssetResourceLen(AssetManager var0, String var1) throws IOException {
        if (var0 == null) {
            return -1L;
        } else {
            InputStream var2 = var0.open(var1);
            long var3 = (long)var2.available();
            var2.close();
            return var3;
        }
    }

    public static int getStatusBarHeight(Context var0) {
        Class var1 = null;
        Object var2 = null;
        Field var3 = null;
        boolean var4 = false;
        int var5 = 0;

        try {
            var1 = Class.forName("com.android.internal.R$dimen");
            var2 = var1.newInstance();
            var3 = var1.getField("status_bar_height");
            int var8 = Integer.parseInt(var3.get(var2).toString());
            var5 = var0.getResources().getDimensionPixelSize(var8);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return var5;
    }

    public static boolean checkDeviceVirtualBar(Context var0) {
        boolean var1 = false;
        Resources var2 = var0.getResources();
        int var3 = var2.getIdentifier("config_showNavigationBar", "bool", "android");
        if (var3 > 0) {
            var1 = var2.getBoolean(var3);
        }

        try {
            Class var4 = Class.forName("android.os.SystemProperties");
            Method var5 = var4.getMethod("get", String.class);
            String var6 = (String)var5.invoke(var4, "qemu.hw.mainkeys");
            if ("1".equals(var6)) {
                var1 = false;
            } else if ("0".equals(var6)) {
                var1 = true;
            }
        } catch (Exception var7) {
            ;
        }

        return var1;
    }

    public static int getVirtualBarHeight(Context var0) {
        int var1 = 0;
        WindowManager var2 = (WindowManager)var0.getSystemService("window");
        Display var3 = var2.getDefaultDisplay();
        DisplayMetrics var4 = new DisplayMetrics();

        try {
            Class var5 = Class.forName("android.view.Display");
            Method var6 = var5.getMethod("getRealMetrics", DisplayMetrics.class);
            var6.invoke(var3, var4);
            var1 = var4.heightPixels - var2.getDefaultDisplay().getHeight();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return var1;
    }

    public static boolean checkValidExtVideoFile(String var0) {
        return var0.endsWith(".mp4") || var0.endsWith(".3gp") || var0.endsWith(".3gpp") || var0.endsWith(".3gpp2");
    }

    public static String getAssetText(AssetManager var0, String var1) {
        try {
            InputStreamReader var2 = new InputStreamReader(var0.open(var1));
            BufferedReader var3 = new BufferedReader(var2);
            String var4 = "";
            StringBuffer var5 = new StringBuffer();

            while((var4 = var3.readLine()) != null) {
                var5.append(var4);
            }

            return var5.toString();
        } catch (Exception var6) {
            var6.printStackTrace();
            return "";
        }
    }

    public static int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int)(var1 * var2 + 0.5F);
    }

    public static int px2dip(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int)(var1 / var2 + 0.5F);
    }

    public static long getFreeMemory(Context var0) {
        ActivityManager var3 = (ActivityManager)var0.getSystemService("activity");
        MemoryInfo var4 = new MemoryInfo();
        var3.getMemoryInfo(var4);
        long var1 = var4.availMem / 1024L;
        var3 = null;
        var4 = null;
        return var1;
    }

    public static boolean isMainProcess(Context var0) {
        String var1 = getCurrentProcessName(var0);
        if (TextUtils.isEmpty(var1)) {
            return true;
        } else {
            return var1.indexOf(":") == -1;
        }
    }

    public static String getCurrentProcessName(Context var0) {
        BufferedReader var1 = null;

        try {
            try {
                var1 = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + Process.myPid() + "/cmdline"), "iso-8859-1"));
            } catch (UnsupportedEncodingException var18) {
                return null;
            } catch (FileNotFoundException var19) {
                return null;
            }

            StringBuilder var3 = new StringBuilder();

            int var2;
            try {
                while((var2 = var1.read()) > 0) {
                    var3.append((char)var2);
                }
            } catch (IOException var20) {
                return null;
            }

            String var6 = var3.toString();
            return var6;
        } finally {
            if (var1 != null) {
                try {
                    var1.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }

        }
    }

    @SuppressLint({"InlinedApi"})
    public static Intent gotoAppInfo(String var0) {
        try {
            Uri var1 = Uri.parse("package:" + var0);
            return new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", var1);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    @SuppressLint({"InlinedApi"})
    public static void gotoAppInfo(Context var0, String var1) {
        try {
            Uri var2 = Uri.parse("package:" + var1);
            var0.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", var2));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void hideVirtualBar(Activity var0) {
        if (hasIceCreamSandwich()) {
            try {
                View var1 = var0.findViewById(16908290);
                if (var1 != null) {
                    var1.setSystemUiVisibility(5894);
                }
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

    }

    public static void openGallery(Context var0, int var1) {
        Intent var2 = new Intent("android.intent.action.GET_CONTENT");
        var2.setType("video/*");
        Intent var3 = Intent.createChooser(var2, (CharSequence)null);
        ((Activity)var0).startActivityForResult(var3, var1);
    }

    @TargetApi(19)
    public static String getAbsolutePath(Activity var0, Uri var1) {
        if (var0 != null && var1 != null) {
            if (VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(var0, var1)) {
                String var2;
                String[] var3;
                String var4;
                if (isExternalStorageDocument(var1)) {
                    var2 = DocumentsContract.getDocumentId(var1);
                    var3 = var2.split(":");
                    var4 = var3[0];
                    if ("primary".equalsIgnoreCase(var4)) {
                        return Environment.getExternalStorageDirectory() + "/" + var3[1];
                    }
                } else {
                    if (isDownloadsDocument(var1)) {
                        var2 = DocumentsContract.getDocumentId(var1);
                        Uri var8 = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(var2));
                        return getDataColumn(var0, var8, (String)null, (String[])null);
                    }

                    if (isMediaDocument(var1)) {
                        var2 = DocumentsContract.getDocumentId(var1);
                        var3 = var2.split(":");
                        var4 = var3[0];
                        Uri var5 = null;
                        if ("image".equals(var4)) {
                            var5 = Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(var4)) {
                            var5 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(var4)) {
                            var5 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        String var6 = "_id=?";
                        String[] var7 = new String[]{var3[1]};
                        return getDataColumn(var0, var5, var6, var7);
                    }
                }
            } else {
                if ("content".equalsIgnoreCase(var1.getScheme())) {
                    if (isGooglePhotosUri(var1)) {
                        return var1.getLastPathSegment();
                    }

                    return getDataColumn(var0, var1, (String)null, (String[])null);
                }

                if ("file".equalsIgnoreCase(var1.getScheme())) {
                    return var1.getPath();
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public static String getDataColumn(Context var0, Uri var1, String var2, String[] var3) {
        Cursor var4 = null;
        String var5 = "_data";
        String[] var6 = new String[]{var5};

        String var9;
        try {
            var4 = var0.getContentResolver().query(var1, var6, var2, var3, (String)null);
            if (var4 == null || !var4.moveToFirst()) {
                return null;
            }

            int var7 = var4.getColumnIndexOrThrow(var5);
            var9 = var4.getString(var7);
        } finally {
            if (var4 != null) {
                var4.close();
            }

        }

        return var9;
    }

    public static boolean isExternalStorageDocument(Uri var0) {
        return "com.android.externalstorage.documents".equals(var0.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri var0) {
        return "com.android.providers.downloads.documents".equals(var0.getAuthority());
    }

    public static boolean isMediaDocument(Uri var0) {
        return "com.android.providers.media.documents".equals(var0.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri var0) {
        return "com.google.android.apps.photos.content".equals(var0.getAuthority());
    }

    public static void onDestory() {
        a = null;
    }
}
