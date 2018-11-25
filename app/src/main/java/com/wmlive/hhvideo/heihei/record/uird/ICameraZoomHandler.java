package com.wmlive.hhvideo.heihei.record.uird;

import android.hardware.Camera;
import android.view.MotionEvent;

public interface ICameraZoomHandler {
    int ZOOM_STOPPED = 0;
    int ZOOM_START = 1;
    int ZOOM_STOPPING = 2;

    Camera getMainCamera();

    void setMainCamera(Camera var1);

    int getZoomState();

    void setZoomState(int var1);

    int getZoomValue();

    void initializeZoom();

    boolean onTouch(MotionEvent var1);
}