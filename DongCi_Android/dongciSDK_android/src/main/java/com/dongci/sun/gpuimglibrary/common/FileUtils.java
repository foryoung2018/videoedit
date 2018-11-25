package com.dongci.sun.gpuimglibrary.common;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * create by ggq at 2018/5/30
 */
public class FileUtils {

    public static String getPath(Context context, String path, String fileName) {
        String p = getBaseFolder(context) + path;
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder(context) + fileName;
        }
        return p + fileName;
    }

    /**
     * 视频存放的地址
     *
     * @param context
     * @return
     */
    public static String getVideoPath(Context context) {
        String p = getBaseFolder(context) + "record/";
        return p;
    }

    public static String getBaseFolder(Context context) {
        String baseFolder = Environment.getExternalStorageDirectory() + "/Codec/";
        File f = new File(baseFolder);
        if (!f.exists()) {
            boolean b = f.mkdirs();
            if (!b) {
                baseFolder = context.getExternalFilesDir(null).getAbsolutePath() + "/";
            }
        }
        return baseFolder;
    }


    public static boolean copy(String fromFile, String toFile) {
        File from = new File(fromFile);
        if (!from.exists()) {
            return false;
        }

        boolean success = true;
        try {
            File dest = new File(toFile);
            if (!dest.exists()) {
                dest.createNewFile();
            }
            FileChannel src = new FileInputStream(from).getChannel();
            FileChannel dst = new FileOutputStream(dest).getChannel();
            dst.transferFrom(src, 0, src.size());
        } catch (IOException e) {
            success = false;
        }
        return success;
    }

    public static void createFile(String path) {
        if (path == null)
            return;
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
