package com.wmlive.hhvideo.heihei.record.utils;

import android.text.TextUtils;

import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;

/**
 * 创建文件目录工具类
 */
public class FileDirUtils {

    private static boolean prepareDir(int shortVideoIndex) {

        if (TextUtils.isEmpty(RecordManager.get().getProductEntity().baseDir)) {
            String productPath = RecordFileUtil.createTimestampDir(RecordFileUtil.getTempDir(), "");
            if (TextUtils.isEmpty(productPath)) {
                KLog.i("====创建productDir文件夹失败");
                return false;
            }
            RecordManager.get().getProductEntity().baseDir = productPath;
        }

        if (!RecordFileUtil.createDir(new File(RecordManager.get().getProductEntity().baseDir))) {
            KLog.i("====创建productDir文件夹失败");
            return false;
        }

        if (TextUtils.isEmpty(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir)) {
            String shortVideoDir = RecordFileUtil.createTimestampDir(RecordManager.get().getProductEntity().baseDir, RecordManager.PREFIX_VIDEO_DIR);
            if (TextUtils.isEmpty(shortVideoDir)) {
                KLog.i("====创建shortVideoDir文件夹失败");
                return false;
            }
            RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir = shortVideoDir;
        }

        if (!RecordFileUtil.createDir(new File(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir))) {
            KLog.i("====创建shortVideoDir文件夹失败");
            return false;
        }
        return true;
    }

}
