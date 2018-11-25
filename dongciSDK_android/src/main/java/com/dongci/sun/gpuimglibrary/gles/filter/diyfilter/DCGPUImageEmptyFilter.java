package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;

/**
 * Created by zhangxiao on 2018/7/30.
 */

public class DCGPUImageEmptyFilter extends GPUImageFilter {
    public static final String kLFGPUImageEmptyFragmentShaderString =
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "void main(){\n" +
            "  lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "  gl_FragColor = vec4((textureColor.rgb), textureColor.w);\n" +
            "}";

    public DCGPUImageEmptyFilter() {
        super(NO_FILTER_VERTEX_SHADER, kLFGPUImageEmptyFragmentShaderString);
    }
}
