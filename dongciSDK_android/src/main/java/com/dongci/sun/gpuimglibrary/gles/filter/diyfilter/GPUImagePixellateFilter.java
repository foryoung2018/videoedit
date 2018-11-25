package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;

/**
 * Created by zhangxiao on 2018/7/30.
 */

public class GPUImagePixellateFilter extends GPUImageFilter {
    public static final String kGPUImagePixellationFragmentShaderString =
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float fractionalWidthOfPixel;\n" +
            "uniform highp float aspectRatio;\n" +
            "\n" +
            "void main() {\n" +
            "  highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
            "  \n" +
            "  highp vec2 samplePos = textureCoordinate - mod(textureCoordinate, sampleDivisor) + 0.5 * sampleDivisor;\n" +
            "  gl_FragColor = texture2D(inputImageTexture, samplePos );\n" +
            "}";

    private int fractionalWidthOfAPixelUniform;
    private int aspectRatioUniform;
    private float fractionalWidthOfAPixel;

    public GPUImagePixellateFilter() {
        super(NO_FILTER_VERTEX_SHADER, kGPUImagePixellationFragmentShaderString);
    }

    public GPUImagePixellateFilter(String fragmentShader) {
        super(NO_FILTER_VERTEX_SHADER, fragmentShader);
    }

    @Override
    public void onInit() {
        super.onInit();
        fractionalWidthOfAPixelUniform = GLES20.glGetUniformLocation(getProgram(), "fractionalWidthOfPixel");
        aspectRatioUniform = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");

        setFractionalWidthOfAPixel(0.05f);
//        setAspectRatio();
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        adjustAspectRatio();
    }

    private void adjustAspectRatio() {
        setAspectRatio((float)getIntputHeight() / (float)getIntputWidth());
    }

    public void setFractionalWidthOfAPixel(float fractionalWidthOfAPixel) {
        float singlePixelSpacing;
        if (getOutputWidth() != 0.0)
        {
            singlePixelSpacing = 1.0f / getOutputWidth();
        }
        else
        {
            singlePixelSpacing = 1.0f / 2048.0f;
        }

        if (fractionalWidthOfAPixel < singlePixelSpacing)
        {
            this.fractionalWidthOfAPixel = singlePixelSpacing;
        }
        else
        {
            this.fractionalWidthOfAPixel = fractionalWidthOfAPixel;
        }
        setFloat(fractionalWidthOfAPixelUniform, fractionalWidthOfAPixel);
    }

    public void setAspectRatio(float aspectRatio) {
        setFloat(aspectRatioUniform, aspectRatio);
    }
}
