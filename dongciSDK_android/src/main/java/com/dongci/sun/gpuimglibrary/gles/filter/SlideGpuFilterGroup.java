package com.dongci.sun.gpuimglibrary.gles.filter;

import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.gles.EasyGlUtils;


/**
 * create by ggq at 2018/5/30
 * <p>
 * 负责滤镜切换的主要类
 */

public class SlideGpuFilterGroup {
    private GPUImageFilter curFilter;
    private int width, height;
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    public SlideGpuFilterGroup() {
        initFilter();
    }

    private void initFilter() {
        curFilter = new GPUImageFilter();
    }

//    public void setOnFilterChangeListener(OnFilterChangeListener listener){
//        this.mListener = listener;
//    }

    private GPUImageFilter getFilter(int index) {

        return new GPUImageFilter();
    }

    public void init() {
        curFilter.init();
    }

    public void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glGenFramebuffers(1, fFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, width, height);
        curFilter.onInputSizeChanged(width, height);
    }

    public int getOutputTexture() {
        return fTexture[0];
    }

    public void onDrawFrame(int textureId) {
        int texId = curFilter.onDraw(textureId);
        EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
//        curFilter.onDrawFrame(textureId);
        if (texId > 0) {
            curFilter.onDrawFrame(texId);
        } else {
            curFilter.onDrawFrame(textureId);
        }
        EasyGlUtils.unBindFrameBuffer();
    }

    public void destroy() {
        curFilter.destroy();
    }

    public void setFilter(GPUImageFilter filter) {
        if (curFilter!=null)
            curFilter.destroy();
        curFilter=filter;
        curFilter.init();
        curFilter.onInputSizeChanged(this.width, this.height);
        curFilter.onDisplaySizeChanged(this.width, this.height);
    }

}
