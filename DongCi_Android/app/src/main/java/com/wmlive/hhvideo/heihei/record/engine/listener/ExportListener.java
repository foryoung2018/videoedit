package com.wmlive.hhvideo.heihei.record.engine.listener;

/**
 * 视频导出
 */
public interface ExportListener {

    void onExportStart();

    void onExporting(int progress, int max);

    void onExportEnd(int var1,String path);

}
