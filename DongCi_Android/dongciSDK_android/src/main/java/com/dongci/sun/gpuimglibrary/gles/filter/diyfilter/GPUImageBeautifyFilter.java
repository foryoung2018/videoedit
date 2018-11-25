package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageBilateralFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFilterGroup;

public class GPUImageBeautifyFilter extends GPUImageFilterGroup {

    private GPUImageBilateralFilter mBilateralFilter;
    private GPUImageFilterGroup mBilateralFilterContainer;

    private GPUImageCannyEdgeDetectionFilter mCannyEdgeDetectionFilter;
    private GPUImageFilterGroup mCannyEdgeDetectionFilterContainer;

    private GPUImageCombinationFilter mCombinationFilter;
    private GPUImageHSBFilter mHSBFilter;
    public GPUImageBeautifyFilter() {
        super();
        mBilateralFilter = new GPUImageBilateralFilter();
        mBilateralFilterContainer = new GPUImageFilterGroup();
        mBilateralFilterContainer.addFilter(mBilateralFilter);

        mCannyEdgeDetectionFilter = new GPUImageCannyEdgeDetectionFilter();
        mCannyEdgeDetectionFilterContainer = new GPUImageFilterGroup();
        mCannyEdgeDetectionFilterContainer.addFilter(mCannyEdgeDetectionFilter);

        mCombinationFilter = new GPUImageCombinationFilter();
        addFilter(mCombinationFilter);

        mHSBFilter = new GPUImageHSBFilter();
        addFilter(mHSBFilter);
    }

    @Override
    public void onInit() {
        super.onInit();

        mBilateralFilterContainer.onInit();
        mCannyEdgeDetectionFilterContainer.onInit();

        mBilateralFilter.setDistanceNormalizationFactor(4.0f);

        mHSBFilter.adjustBrightness(1.1f);
        mHSBFilter.adjustSaturation(1.1f);
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        mBilateralFilterContainer.onInputSizeChanged(width, height);
        mCannyEdgeDetectionFilterContainer.onInputSizeChanged(width, height);
    }

    @Override
    public void onDisplaySizeChanged(final int width, final int height) {
        super.onDisplaySizeChanged(width, height);

        mBilateralFilterContainer.onDisplaySizeChanged(width, height);
        mCannyEdgeDetectionFilterContainer.onDisplaySizeChanged(width, height);
    }

    @Override
    public int onDraw(final int textureId) {
        mBilateralFilterContainer.onDraw(textureId, mGLCubeBuffer, mGLTextureBuffer);
        mCannyEdgeDetectionFilterContainer.onDraw(textureId, mGLCubeBuffer, mGLTextureBuffer);
        mCombinationFilter.setTexture(mBilateralFilterContainer.getTextureId());
        mCombinationFilter.setTexture3(mCannyEdgeDetectionFilterContainer.getTextureId());
        return super.onDraw(textureId);
    }
}
