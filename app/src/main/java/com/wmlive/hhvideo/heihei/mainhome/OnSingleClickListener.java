package com.wmlive.hhvideo.heihei.mainhome;

import android.view.View;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.utils.KLog;

/**
 * Created by hsing on 2017/12/18.
 */

public abstract class OnSingleClickListener implements View.OnClickListener {
    private long lastClickTime;
    private int lastViewId;
    private long clickDelayTime = GlobalParams.Config.MINIMUM_CLICK_DELAY;

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if ((lastViewId == v.getId())) {
            if (currentTime - lastClickTime > clickDelayTime) {
                onSingleClick(v);
            } else {
                KLog.i(this.getClass().getSimpleName(), "====快速点击无效");
                onDoubleClick(v);
            }
        } else {
            onSingleClick(v);
        }
        lastViewId = v.getId();
        lastClickTime = currentTime;
    }

    protected abstract void onSingleClick(View v); //单击

    protected void onDoubleClick(View v) {//双击
    }
}
