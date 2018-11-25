package com.wmlive.hhvideo.heihei.mainhome.util;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.utils.ToastUtil;

public class PublishUtils {
    public static boolean showToast() {
        if (InitCatchData.getInitCatchData().getTips() != null && InitCatchData.getInitCatchData().getTips().recordCheck != null) {//服务器控制是否可录制的
            if (InitCatchData.getInitCatchData().getTips().recordCheck.showTip) {
                ToastUtil.showToast(InitCatchData.getInitCatchData().getTips().recordCheck.tips);
                return true;
            }
        }
        if (GlobalParams.StaticVariable.ispublishing) {//有作品正在发布中
            ToastUtil.showToast("有作品正在上传中\n请稍后再试");
            return true;
        }
        return false;
    }
}
