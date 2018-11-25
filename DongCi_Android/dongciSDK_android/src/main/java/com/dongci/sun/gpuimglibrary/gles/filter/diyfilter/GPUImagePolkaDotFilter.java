package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.opengl.GLES20;

/**
 * Created by zhangxiao on 2018/7/30.
 */

public class GPUImagePolkaDotFilter extends GPUImagePixellateFilter {
    public static final String kGPUImagePolkaDotFragmentShaderString =
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform highp float fractionalWidthOfPixel;\n" +
            "uniform highp float aspectRatio;\n" +
            "uniform highp float dotScaling;\n" +
            "\n" +
            "void main() {\n" +
            "  highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
            "  \n" +
            "  highp vec2 samplePos = textureCoordinate - mod(textureCoordinate, sampleDivisor) + 0.5 * sampleDivisor;\n" +
            "  highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "  highp vec2 adjustedSamplePos = vec2(samplePos.x, (samplePos.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "  highp float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);\n" +
            "  lowp float checkForPresenceWithinDot = step(distanceFromSamplePoint, (fractionalWidthOfPixel * 0.5) * dotScaling);\n" +
            "\n" +
            "  lowp vec4 inputColor = texture2D(inputImageTexture, samplePos);\n" +
            "  \n" +
            "  gl_FragColor = vec4(inputColor.rgb * checkForPresenceWithinDot, inputColor.a);\n" +
            "}";

    private int dotScalingUniform;

    public GPUImagePolkaDotFilter() {
        super(kGPUImagePolkaDotFragmentShaderString);
    }

    @Override
    public void onInit() {
        super.onInit();
        dotScalingUniform = GLES20.glGetUniformLocation(getProgram(), "dotScaling");
        setDotScaling(0.9f);
    }

    public void setDotScaling(float dotScaling) {
        setFloat(dotScalingUniform, dotScaling);
    }
}
