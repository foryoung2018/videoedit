package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageTwoPassTextureSamplingFilter;

import static com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageDilationFilter.VERTEX_SHADER_1;
import static com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageDilationFilter.VERTEX_SHADER_2;
import static com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageDilationFilter.VERTEX_SHADER_3;
import static com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageDilationFilter.VERTEX_SHADER_4;

/**
 * Created by zhangxiao on 2018/7/30.
 */

public class GPUImageErosionFilter extends GPUImageTwoPassTextureSamplingFilter {
    public static final String kGPUImageErosionRadiusOneFragmentShaderString =
            "precision lowp float;\n" +
            "varying vec2 centerTextureCoordinate;\n" +
            "varying vec2 oneStepPositiveTextureCoordinate;\n" +
            "varying vec2 oneStepNegativeTextureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "void main() {\n" +
            "  float centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate).r;\n" +
            "  float oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate).r;\n" +
            "  float oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate).r;\n" +
            "  lowp float minValue = min(centerIntensity, oneStepPositiveIntensity);\n" +
            "  minValue = min(minValue, oneStepNegativeIntensity);\n" +
            "  gl_FragColor = vec4(vec3(minValue), 1.0);\n" +
            "}";

    public static final String kGPUImageErosionRadiusTwoFragmentShaderString =
            "precision lowp float;\n" +
            " \n" +
            "varying vec2 centerTextureCoordinate;\n" +
            "varying vec2 oneStepPositiveTextureCoordinate;\n" +
            "varying vec2 oneStepNegativeTextureCoordinate;\n" +
            "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
            "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "void main() {\n" +
            "  float centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate).r;\n" +
            "  float oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate).r;\n" +
            "  float oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate).r;\n" +
            "  float twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate).r;\n" +
            "  float twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate).r;\n" +
            "  \n" +
            "  lowp float minValue = min(centerIntensity, oneStepPositiveIntensity);\n" +
            "  minValue = min(minValue, oneStepNegativeIntensity);\n" +
            "  minValue = min(minValue, twoStepsPositiveIntensity);\n" +
            "  minValue = min(minValue, twoStepsNegativeIntensity);\n" +
            "  \n" +
            "  gl_FragColor = vec4(vec3(minValue), 1.0);\n" +
            "}";

    public static final String kGPUImageErosionRadiusThreeFragmentShaderString =
            "precision lowp float;\n" +
            " \n" +
            "varying vec2 centerTextureCoordinate;\n" +
            "varying vec2 oneStepPositiveTextureCoordinate;\n" +
            "varying vec2 oneStepNegativeTextureCoordinate;\n" +
            "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
            "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
            "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
            "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "void main() {\n" +
            "  float centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate).r;\n" +
            "  float oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate).r;\n" +
            "  float oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate).r;\n" +
            "  float twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate).r;\n" +
            "  float twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate).r;\n" +
            "  float threeStepsPositiveIntensity = texture2D(inputImageTexture, threeStepsPositiveTextureCoordinate).r;\n" +
            "  float threeStepsNegativeIntensity = texture2D(inputImageTexture, threeStepsNegativeTextureCoordinate).r;\n" +
            "  \n" +
            "  lowp float minValue = min(centerIntensity, oneStepPositiveIntensity);\n" +
            "  minValue = min(minValue, oneStepNegativeIntensity);\n" +
            "  minValue = min(minValue, twoStepsPositiveIntensity);\n" +
            "  minValue = min(minValue, twoStepsNegativeIntensity);\n" +
            "  minValue = min(minValue, threeStepsPositiveIntensity);\n" +
            "  minValue = min(minValue, threeStepsNegativeIntensity);\n" +
            "  \n" +
            "  gl_FragColor = vec4(vec3(minValue), 1.0);\n" +
            "}";

    public static final String kGPUImageErosionRadiusFourFragmentShaderString =
            "precision lowp float;\n" +
            "\n" +
            "varying vec2 centerTextureCoordinate;\n" +
            "varying vec2 oneStepPositiveTextureCoordinate;\n" +
            "varying vec2 oneStepNegativeTextureCoordinate;\n" +
            "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
            "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
            "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
            "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
            "varying vec2 fourStepsPositiveTextureCoordinate;\n" +
            "varying vec2 fourStepsNegativeTextureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "void main() {\n" +
            "  float centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate).r;\n" +
            "  float oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate).r;\n" +
            "  float oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate).r;\n" +
            "  float twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate).r;\n" +
            "  float twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate).r;\n" +
            "  float threeStepsPositiveIntensity = texture2D(inputImageTexture, threeStepsPositiveTextureCoordinate).r;\n" +
            "  float threeStepsNegativeIntensity = texture2D(inputImageTexture, threeStepsNegativeTextureCoordinate).r;\n" +
            "  float fourStepsPositiveIntensity = texture2D(inputImageTexture, fourStepsPositiveTextureCoordinate).r;\n" +
            "  float fourStepsNegativeIntensity = texture2D(inputImageTexture, fourStepsNegativeTextureCoordinate).r;\n" +
            "  \n" +
            "  lowp float minValue = min(centerIntensity, oneStepPositiveIntensity);\n" +
            "  minValue = min(minValue, oneStepNegativeIntensity);\n" +
            "  minValue = min(minValue, twoStepsPositiveIntensity);\n" +
            "  minValue = min(minValue, twoStepsNegativeIntensity);\n" +
            "  minValue = min(minValue, threeStepsPositiveIntensity);\n" +
            "  minValue = min(minValue, threeStepsNegativeIntensity);\n" +
            "  minValue = min(minValue, fourStepsPositiveIntensity);\n" +
            "  minValue = min(minValue, fourStepsNegativeIntensity);\n" +
            "  \n" +
            "  gl_FragColor = vec4(vec3(minValue), 1.0);\n" +
            "}";

    public GPUImageErosionFilter(int dilationRadius) {
        this(getVertexShader(dilationRadius), getFragmentShader(dilationRadius), getVertexShader(dilationRadius), getFragmentShader(dilationRadius));
    }

    public GPUImageErosionFilter(String firstVertexShader, String firstFragmentShader,
                                 String secondVertexShader, String secondFragmentShader) {
        super(firstVertexShader, firstFragmentShader,
                secondVertexShader, secondFragmentShader);
    }

    private static String getVertexShader(int dilationRadius) {
        switch (dilationRadius) {
            case 0:
            case 1:
                return VERTEX_SHADER_1;
            case 2:
                return VERTEX_SHADER_2;
            case 3:
                return VERTEX_SHADER_3;
            case 4:
                return VERTEX_SHADER_4;
            default:
                return VERTEX_SHADER_4;
        }
    }

    private static String getFragmentShader(int dilationRadius) {
        switch (dilationRadius) {
            case 0:
            case 1:
                return kGPUImageErosionRadiusOneFragmentShaderString;
            case 2:
                return kGPUImageErosionRadiusTwoFragmentShaderString;
            case 3:
                return kGPUImageErosionRadiusThreeFragmentShaderString;
            case 4:
                return kGPUImageErosionRadiusFourFragmentShaderString;
            default:
                return kGPUImageErosionRadiusFourFragmentShaderString;
        }
    }
}
