package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageTwoInputFilter;

public class GPUImageMaskFilter extends GPUImageTwoInputFilter {
    public static String kGPUImageMaskShaderString = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " void main()\n" +
            " {\n" +
            "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "   gl_FragColor = vec4(textureColor.xyz, textureColor2.a);\n" +
            " }";

    public GPUImageMaskFilter() {
        super(kGPUImageMaskShaderString);
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
}
