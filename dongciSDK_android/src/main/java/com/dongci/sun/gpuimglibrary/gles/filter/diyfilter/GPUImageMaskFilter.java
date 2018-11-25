package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageTwoInputFilter;

public class GPUImageMaskFilter extends GPUImageTwoInputFilter {
    public static String kGPUImageMaskShaderString = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " uniform highp vec4 strokeColor;\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " void main()\n" +
            " {\n" +
            "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "   if (textureColor2.r != 0.0) {" +
            "     gl_FragColor = vec4(strokeColor.rgb, textureColor2.a);" +
            "   } else {" +
            "     gl_FragColor = vec4(textureColor.xyz, textureColor2.a);\n" +
            "   }" +
            " }";

    private int mStrokeLocation;
    private int mStrokeColor;

    public GPUImageMaskFilter() {
        super(kGPUImageMaskShaderString);
        mStrokeColor = 0xffffffff;
    }

    @Override
    public void onInit() {
        super.onInit();
        mStrokeLocation = GLES20.glGetUniformLocation(getProgram(), "strokeColor");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setStrokeColor(mStrokeColor);
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES20.glDepthMask(false);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void onDrawArraysAfter() {
        super.onDrawArraysAfter();
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDepthMask(true);
    }

    public void setStrokeColor(int color) {
        mStrokeColor = color;
        float[] strokeColor = {
                ((color & 0x00ff0000) >> 16) / 255.0f,
                ((color & 0x0000ff00) >> 8) / 255.0f,
                (color & 0x000000ff) / 255.0f,
                ((color & 0xff000000) >> 24) / 255.0f
        };
        setFloatVec4(mStrokeLocation, strokeColor);
    }
}
