package com.wmlive.hhvideo.heihei.quickcreative;

import android.text.TextUtils;
import android.util.Log;

import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateListBean;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.FileZipAndUnZip;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.util.List;

public class CreativeQuickUtils {
    private static final String TAG = "CreativeQuickUtils";


    /**
     * 获取模板默认的bg素材zip
     *
     * @param bg_list
     * @param bgname
     * @return
     */
    public static CreativeTemplateListBean.BgListBean getDefaultBgBean(List<CreativeTemplateListBean.BgListBean> bg_list, String bgname) {
        int defindex = 0;
        for (int j = 0; j < bg_list.size(); j++) {
            CreativeTemplateListBean.BgListBean bgListBean = bg_list.get(j);
            if (bgListBean.getBg_name().equals(bgname)) {
                return bgListBean;
            }
            if (bgListBean.getIs_default() == 1) {
                defindex = j;
            }
        }
        return bg_list.get(defindex);
    }

    /**
     * 解压zip文件
     */
    public static void doUnzip(String zipFilePath, String name) {
//        String substring = zipFilePath.substring(zipFilePath.lastIndexOf("/") + 1, zipFilePath.indexOf(".zip"));
//        KLog.d(TAG, "doUnzip: substring==" + substring);
//        File file = new File(AppCacheFileUtils.getAppCreativePath() + substring + File.separator + name);
//        if (file.list() == null || file.list().length == 0) {
//            FileZipAndUnZip.unZipFile(zipFilePath, AppCacheFileUtils.getAppCreativePath() + substring);
//            KLog.d("", "generatePreview:   执行解压");
//        }
        File file = new File(AppCacheFileUtils.getAppCreativePath() + name);
        if (file.list() == null || file.list().length == 0) {
            FileZipAndUnZip.unZipFile(zipFilePath, AppCacheFileUtils.getAppCreativePath());
            KLog.d("", "generatePreview:   执行解压");
        }
    }

    public static boolean isFileEmpty(String name) {
        File file = new File(AppCacheFileUtils.getAppCreativePath() + name);
        return file.list() == null || file.list().length == 0;
    }

//    public static String getZipFileName(String s) {
//        if (TextUtils.isEmpty(s)) {
//            return "";
//        } else {
//            return s.substring(s.lastIndexOf("/") + 1, s.indexOf(".zip"));
//        }
//    }
//
//    public static String getFilePath(String downLoadPath){
//        if (TextUtils.isEmpty(downLoadPath)){
//            return "";
//        }else {
//            return AppCacheFileUtils.getAppCreativePath()+getZipFileName(downLoadPath)+File.separator;
//        }
//
//    }
}
