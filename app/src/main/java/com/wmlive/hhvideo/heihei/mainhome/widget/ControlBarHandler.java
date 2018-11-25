package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by lsq on 5/8/2018 - 2:31 PM
 * 类描述：
 */
public class ControlBarHandler extends Handler {
    public static final int MESSAGE_UPDATE_PROGRESS = 10;
    private WeakReference<VideoDetailItemView1> view;

    ControlBarHandler(VideoDetailItemView1 view) {
        this.view = new WeakReference<>(view);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MESSAGE_UPDATE_PROGRESS:
                if (view.get() != null) {
                    VideoDetailItemView1 thiz = view.get();
                    if (thiz.hasControlBarVisiable()) {
                        int pos = thiz.setCurrentPosition();
                        if (!thiz.isDragging) {
                            msg = obtainMessage(MESSAGE_UPDATE_PROGRESS);
//                                sendMessageDelayed(msg, 1000 - (pos % 1000));
                            sendMessageDelayed(msg, 100);
                        }
                    }
                }
                break;
        }
    }
}