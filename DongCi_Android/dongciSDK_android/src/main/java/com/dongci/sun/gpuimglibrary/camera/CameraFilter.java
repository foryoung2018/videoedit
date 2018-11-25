package com.dongci.sun.gpuimglibrary.camera;

import android.content.res.Resources;


/**
 * Description:
 * 摄像头切换的控制器
 */
public class CameraFilter extends OesFilter {

    public CameraFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        float[] coord;
        if (getFlag() == 1) {    //前置摄像头 顺时针旋转90,并上下颠倒
            coord = new float[]{
                    1.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,
            };
        } else {               //后置摄像头 顺时针旋转90度
            coord = new float[]{
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
            };
        }
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }
}
