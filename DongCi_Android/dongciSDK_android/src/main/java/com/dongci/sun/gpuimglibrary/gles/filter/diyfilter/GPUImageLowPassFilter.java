package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageDissolveBlendFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFilterGroup;

public class GPUImageLowPassFilter extends GPUImageFilterGroup {
    private GPUImageDissolveBlendFilter mDissolveBlendFilter;
    private GPUImageFilterGroup mBufferFilter;

    public GPUImageLowPassFilter() {
        super();

        mDissolveBlendFilter = new GPUImageDissolveBlendFilter(0.5f);

        mBufferFilter = new GPUImageFilterGroup();
        mBufferFilter.addFilter(new GPUImageFilter());

        addFilter(mDissolveBlendFilter);
    }

    @Override
    public void onInit() {
        super.onInit();

        mBufferFilter.onInit();
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        mBufferFilter.onInputSizeChanged(width, height);
    }

    @Override
    public void onDisplaySizeChanged(final int width, final int height) {
        super.onDisplaySizeChanged(width, height);
        mBufferFilter.onDisplaySizeChanged(width, height);

        mDissolveBlendFilter.setTexture(mBufferFilter.getTextureId());
    }

    @Override
    public int onDraw(final int textureId) {
        int ret = super.onDraw(textureId);
        mBufferFilter.onDraw(ret, mGLCubeBuffer, mGLTextureBuffer);
        return ret;
    }
}
