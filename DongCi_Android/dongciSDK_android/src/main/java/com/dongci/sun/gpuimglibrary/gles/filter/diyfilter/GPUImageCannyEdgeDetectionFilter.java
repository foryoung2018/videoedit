package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageDirectionalSobelEdgeDetectionFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFilterGroup;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageGrayscaleFilter;
import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageWeakPixelInclusionFilter;

public class GPUImageCannyEdgeDetectionFilter extends GPUImageFilterGroup {

    GPUImageGrayscaleFilter mGrayscaleFilter;
    GPUImageSingleComponentGaussianBlurFilter mSingleComponentGaussianBlurFilter;
    GPUImageDirectionalSobelEdgeDetectionFilter mDirectionalSobelEdgeDetectionFilter;
    GPUImageDirectionalNonMaximumSuppressionFilter mDirectionalNonMaximumSuppressionFilter;
    GPUImageWeakPixelInclusionFilter mWeakPixelInclusionFilter;

    public GPUImageCannyEdgeDetectionFilter() {
        super();

        mGrayscaleFilter = new GPUImageGrayscaleFilter();
        mSingleComponentGaussianBlurFilter = new GPUImageSingleComponentGaussianBlurFilter(2);
        mDirectionalSobelEdgeDetectionFilter = new GPUImageDirectionalSobelEdgeDetectionFilter();
        mDirectionalNonMaximumSuppressionFilter = new GPUImageDirectionalNonMaximumSuppressionFilter();
        mWeakPixelInclusionFilter = new GPUImageWeakPixelInclusionFilter();

        addFilter(mGrayscaleFilter);
        addFilter(mSingleComponentGaussianBlurFilter);
        addFilter(mDirectionalSobelEdgeDetectionFilter);
        addFilter(mDirectionalNonMaximumSuppressionFilter);
        addFilter(mWeakPixelInclusionFilter);
    }

    @Override
    public void onInit() {
        super.onInit();
        mDirectionalNonMaximumSuppressionFilter.setUpperThreshold(0.4f);
        mDirectionalNonMaximumSuppressionFilter.setlowerThreshold(0.1f);
    }
}
