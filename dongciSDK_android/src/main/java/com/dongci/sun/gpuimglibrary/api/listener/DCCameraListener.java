package com.dongci.sun.gpuimglibrary.api.listener;

public interface DCCameraListener {

    void onCamera(int var1, String var2);

    void onPrepared(int var1, String var2);

    void onPermissionFailed(int var1, String var2);

    void onGetRecordStatus(int position);

    void onRecordBegin(int var1, String var2);

    void onRecordFailed(int var1, String var2);

    void onRecordEnd(int var1, String var2);
}
