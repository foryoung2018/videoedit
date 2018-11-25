package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;

/**
 * Created by zhangxiao on 2018/7/30.
 */

public class GPUImageZoomBlurFilter extends GPUImageFilter {
    public static final String kGPUImageZoomBlurFragmentShaderString =
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform highp vec2 blurCenter;\n" +
            "uniform highp float blurSize;\n" +
            "\n" +
            "void main() {\n" +
            "  // TODO: Do a more intelligent scaling based on resolution here\n" +
            "  highp vec2 samplingOffset = 1.0/100.0 * (blurCenter - textureCoordinate) * blurSize;\n" +
            "  \n" +
            "  lowp vec4 fragmentColor = texture2D(inputImageTexture, textureCoordinate) * 0.18;\n" +
            "  fragmentColor += texture2D(inputImageTexture, textureCoordinate + samplingOffset) * 0.15;\n" +
            "  fragmentColor += texture2D(inputImageTexture, textureCoordinate + (2.0 * samplingOffset)) *  0.12;\n" +
            "  fragmentColor += texture2D(inputImageTexture, textureCoordinate + (3.0 * samplingOffset)) * 0.09;\n" +
            "  fragmentColor += texture2D(inputImageTexture, textureCoordinate + (4.0 * samplingOffset)) * 0.05;\n" +
            "  fragmentColor += texture2D(inputImageTexture, textureCoordinate - samplingOffset) * 0.15;\n" +
            "  fragmentColor += texture2D(inputImageTexture, textureCoordinate - (2.0 * samplingOffset)) *  0.12;\n" +
            "  fragmentColor += texture2D(inputImageTexture, textureCoordinate - (3.0 * samplingOffset)) * 0.09;\n" +
            "  fragmentColor += texture2D(inputImageTexture, textureCoordinate - (4.0 * samplingOffset)) * 0.05;\n" +
            "  \n" +
            "  gl_FragColor = fragmentColor;\n" +
            "}";

    private int blurSizeUniform;
    private int blurCenterUniform;

    public GPUImageZoomBlurFilter() {
        super(NO_FILTER_VERTEX_SHADER, kGPUImageZoomBlurFragmentShaderString);
    }

    @Override
    public void onInit() {
        super.onInit();
        blurSizeUniform = GLES20.glGetUniformLocation(getProgram(), "blurSize");
        blurCenterUniform = GLES20.glGetUniformLocation(getProgram(), "blurCenter");
        setBlurSize(1.0f);
        setBlurCenter(0.5f, 0.5f);
    }

    public void setBlurSize(float blurSize) {
        setFloat(blurSizeUniform, blurSize);
    }

    public void setBlurCenter(float x, float y) {
        setFloatVec2(blurCenterUniform, new float[] {x, y});
    }
}
