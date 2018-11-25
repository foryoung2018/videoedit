package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.graphics.Bitmap;

import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageFilterGroup;

/**
 * Created by zhangxiao on 2018/7/31.
 */

public class FWHudsonFilter extends GPUImageFilterGroup {
    public static final String kFWHudsonShaderString =
            "precision lowp float;\n" +
            "\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform sampler2D inputImageTexture2; //blowout;\n" +
            "uniform sampler2D inputImageTexture3; //overlay;\n" +
            "uniform sampler2D inputImageTexture4; //map\n" +
            "\n" +
            "void main() {\n" +
            "  \n" +
            "  vec4 texel = texture2D(inputImageTexture, textureCoordinate);\n" +
            "  \n" +
            "  vec3 bbTexel = texture2D(inputImageTexture2, textureCoordinate).rgb;\n" +
            "  \n" +
            "  texel.r = texture2D(inputImageTexture3, vec2(bbTexel.r, texel.r)).r;\n" +
            "  texel.g = texture2D(inputImageTexture3, vec2(bbTexel.g, texel.g)).g;\n" +
            "  texel.b = texture2D(inputImageTexture3, vec2(bbTexel.b, texel.b)).b;\n" +
            "  \n" +
            "  vec4 mapped;\n" +
            "  mapped.r = texture2D(inputImageTexture4, vec2(texel.r, .16666)).r;\n" +
            "  mapped.g = texture2D(inputImageTexture4, vec2(texel.g, .5)).g;\n" +
            "  mapped.b = texture2D(inputImageTexture4, vec2(texel.b, .83333)).b;\n" +
            "  mapped.a = 1.0;\n" +
            "  gl_FragColor = mapped;\n" +
            "}";

    public FWHudsonFilter(Bitmap hudsonBackground, Bitmap overlayMap, Bitmap hudsonMap) {
        super();
        FWFilter5 filter = new FWFilter5();
        filter.setBitmap(hudsonBackground);
        filter.setBitmap3(overlayMap);
        filter.setBitmap4(hudsonMap);

        addFilter(filter);
    }

    static class FWFilter5 extends GPUImageFourInputFilter {

        public FWFilter5() {
            super(kFWHudsonShaderString);
        }
    }
}
