package com.dongci.sun.gpuimglibrary.api.listener;

import com.dongci.sun.gpuimglibrary.common.CutEntity;

import java.util.List;

public interface DCCutListener {

    void onCutStart();

    boolean onCuting(int var1);

    void onCutFinish(int var1, List<CutEntity> list);

}
