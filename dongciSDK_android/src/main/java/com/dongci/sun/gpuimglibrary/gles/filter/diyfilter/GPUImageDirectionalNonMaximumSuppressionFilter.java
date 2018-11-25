package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;

public class GPUImageDirectionalNonMaximumSuppressionFilter extends GPUImageFilter {
    public static final String kGPUImageDirectionalNonmaximumSuppressionFragmentShaderString =
            "precision mediump float;\n" +
            "\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float texelWidth; \n" +
            "uniform highp float texelHeight; \n" +
            "uniform mediump float upperThreshold; \n" +
            "uniform mediump float lowerThreshold; \n" +
            "\n" +
            "void main() {\n" +
            "  vec3 currentGradientAndDirection = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "  vec2 gradientDirection = ((currentGradientAndDirection.gb * 2.0) - 1.0) * vec2(texelWidth, texelHeight);\n" +
            "  \n" +
            "  float firstSampledGradientMagnitude = texture2D(inputImageTexture, textureCoordinate + gradientDirection).r;\n" +
            "  float secondSampledGradientMagnitude = texture2D(inputImageTexture, textureCoordinate - gradientDirection).r;\n" +
            "  \n" +
            "  float multiplier = step(firstSampledGradientMagnitude, currentGradientAndDirection.r);\n" +
            "  multiplier = multiplier * step(secondSampledGradientMagnitude, currentGradientAndDirection.r);\n" +
            "  \n" +
            "  float thresholdCompliance = smoothstep(lowerThreshold, upperThreshold, currentGradientAndDirection.r);\n" +
            "  multiplier = multiplier * thresholdCompliance;\n" +
            "  \n" +
            "  gl_FragColor = vec4(multiplier, multiplier, multiplier, 1.0);\n" +
            "}";

    protected int mTexelWidthUniform;
    protected int mTexelHeightUniform;
    protected int mUpperThresholdUniform;
    protected int mLowerThresholdUniform;

    public GPUImageDirectionalNonMaximumSuppressionFilter() {
        super(NO_FILTER_VERTEX_SHADER, kGPUImageDirectionalNonmaximumSuppressionFragmentShaderString);
    }

    @Override
    public void onInit() {
        super.onInit();
        mTexelWidthUniform = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        mTexelHeightUniform = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
        mUpperThresholdUniform = GLES20.glGetUniformLocation(getProgram(), "upperThreshold");
        mLowerThresholdUniform = GLES20.glGetUniformLocation(getProgram(), "lowerThreshold");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();

        setUpperThreshold(0.5f);
        setlowerThreshold(0.1f);
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);

        setTexelWidth(width);
        setTexelHeight(height);
    }

    public void setTexelWidth(float texelWidth) {
        setFloat(mTexelWidthUniform, texelWidth);
    }

    public void setTexelHeight(float texelHeight) {
        setFloat(mTexelHeightUniform, texelHeight);
    }

    public void setUpperThreshold(float upperThreshold) {
        setFloat(mUpperThresholdUniform, upperThreshold);
    }

    public void setlowerThreshold(float lowerThreshold) {
        setFloat(mLowerThresholdUniform, lowerThreshold);
    }
}
