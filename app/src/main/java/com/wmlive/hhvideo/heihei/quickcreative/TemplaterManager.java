package com.wmlive.hhvideo.heihei.quickcreative;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvBgEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvTemplateEntity;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.download.FileDownload;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 模板管理，
 * 1.模板下载，
 * 2.模板解压
 */
public class TemplaterManager {


    /**
     * 下载模板
     */
    public static int downLoadTemplateAndBg(Context context,MvTemplateEntity mvTemplateEntity,
                                        MvBgEntity mvBgEntity, ResultReceiver resultReceiver){
        ArrayList<DownloadBean> downloadList = new ArrayList<>();
        if (FileUtil.isTemplateFileEmpty(mvTemplateEntity.template_name)) {
            String tempPath = mvTemplateEntity.getZip_path();
            int tempDownloadId = FileDownloadUtils.generateId(tempPath, AppCacheFileUtils.getAppCreativePath());
            DownloadBean tempDownload = new DownloadBean(tempDownloadId, tempPath, AppCacheFileUtils.getAppCreativePath(), "", "", DownloadBean.DOWNLOAD_ID_TEMPLATE);
            downloadList.add(tempDownload);
        } else {

        }
        if (FileUtil.isTemplateFileEmpty(mvBgEntity.bg_name)) {
            String bgPath = mvBgEntity.getBg_resource();
            int bgDownloadId = FileDownloadUtils.generateId(bgPath, AppCacheFileUtils.getAppCreativePath());
            DownloadBean bgDownload = new DownloadBean(bgDownloadId, bgPath, AppCacheFileUtils.getAppCreativePath(), "", "", DownloadBean.DOWNLOAD_ID_BG);
            downloadList.add(bgDownload);
        } else {

        }
        //下载任务开启
        int downSize = downloadList.size();
        if (downSize> 0) {
            FileDownload.start(context, downloadList, resultReceiver, true);
        }
        return downSize;
    }

    /**
     * 解压模板
     */
    public static void unZipTemplate(String savePath){
        CreativeQuickUtils.doUnzip(savePath, AppCacheFileUtils.getAppCreativePath());

    }


    /**
     * 读取模板信息
     * @param templateName 模板名称
     * @return 本地是否存在该模板
     */
    public static boolean read(String templateName){
        boolean result = FileUtil.isTemplateFileEmpty(templateName);
        return result;
    }


}
