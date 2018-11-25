package com.wmlive.hhvideo.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.networklib.util.EventHelper;
import com.wmlive.networklib.util.NetUtil;

public class NetWorkStatusReceiver extends BroadcastReceiver {

    boolean isFirst = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isFirst) {
            isFirst = false;
            return;
        }
        int result = NetUtil.getNetworkState(context);
        GlobalParams.StaticVariable.sCurrentNetwork = result;
//        if (result == 0) {
//            GlobalParams.StaticVariable.sAllowdMobile = false;
//        }
        TaskManager.get().getAllIp();
        EventHelper.post(GlobalParams.EventType.TYPE_NETWORK_CHANGE, result);
    }


}
