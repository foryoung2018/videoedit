package com.dongci.sun.gpuimglibrary.api.listener;

public interface DCExportListener {

    void onExportStart();

    boolean onExporting(int var1, int var2);

    void onExportEnd(int var1, String path);


}
