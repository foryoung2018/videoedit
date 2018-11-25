package com.wmlive.hhvideo.utils.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.List;

/**
 * --------------------------------------------------------------- :
 * Jianfei.G Create: 2016/10/12 18.32
 * --------------------------------------------------------------- Describe: 在线更新下载功能
 * --------------------------------------------------------------- Changes:
 * --------------------------------------------------------------- 2016/10/12 18
 * : Create by Jianfei.G
 * ---------------------------------------------------------------
 */
public class UpdateUtils {
    private static String TAG = UpdateUtils.class.getSimpleName();

    public static void download(Context ctx, String url, String title, String fileName) {
        if (!canDownloadState(ctx)) {
            showDownloadSetting(ctx);
            return;
        }
        APKUpdateManager.download(ctx, url, title, fileName);
    }

    private static boolean canDownloadState(Context ctx) {
        try {
            int state = ctx.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void showDownloadSetting(Context ctx) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(intent, ctx)) {
            ctx.startActivity(intent);
        }
    }

    private static boolean intentAvailable(Intent intent, Context ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}
