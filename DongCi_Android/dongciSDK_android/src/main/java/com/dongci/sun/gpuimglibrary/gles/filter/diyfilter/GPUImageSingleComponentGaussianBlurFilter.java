package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

public class GPUImageSingleComponentGaussianBlurFilter extends LFGPUImageGaussianBlurFilter {
    public GPUImageSingleComponentGaussianBlurFilter(double blurRadiusInPixels) {
        super(getVertexShader(blurRadiusInPixels), getFragmentShader(blurRadiusInPixels), getVertexShader(blurRadiusInPixels), getFragmentShader(blurRadiusInPixels));
        this.blurRadiusInPixels = blurRadiusInPixels;
    }

    protected static String getVertexShader(double blurRadiusInPixels) {
        blurRadiusInPixels = Math.round(blurRadiusInPixels);

        int calculatedSampleRadius = 0;
        if (blurRadiusInPixels >= 1) {
            // Calculate the number of pixels to sample from by setting a bottom limit for the contribution of the outermost pixel
            double minimumWeightToFindEdgeOfSamplingArea = 1.0/256.0;
            calculatedSampleRadius = (int)(Math.floor(Math.sqrt(-2.0 * Math.pow(blurRadiusInPixels, 2.0) * Math.log(minimumWeightToFindEdgeOfSamplingArea * Math.sqrt(2.0 * Math.PI * Math.pow(blurRadiusInPixels, 2.0))))));
            calculatedSampleRadius += calculatedSampleRadius % 2; // There's nothing to gain from handling odd radius sizes, due to the optimizations I use
        }
        return vertexShaderForOptimizedBlurOfRadius(calculatedSampleRadius, blurRadiusInPixels);
    }

    protected static String getFragmentShader(double blurRadiusInPixels) {
        blurRadiusInPixels = Math.round(blurRadiusInPixels);

        int calculatedSampleRadius = 0;
        if (blurRadiusInPixels >= 1) {
            // Calculate the number of pixels to sample from by setting a bottom limit for the contribution of the outermost pixel
            double minimumWeightToFindEdgeOfSamplingArea = 1.0/256.0;
            calculatedSampleRadius = (int)(Math.floor(Math.sqrt(-2.0 * Math.pow(blurRadiusInPixels, 2.0) * Math.log(minimumWeightToFindEdgeOfSamplingArea * Math.sqrt(2.0 * Math.PI * Math.pow(blurRadiusInPixels, 2.0))))));
            calculatedSampleRadius += calculatedSampleRadius % 2; // There's nothing to gain from handling odd radius sizes, due to the optimizations I use
        }
        return fragmentShaderForOptimizedBlurOfRadius(calculatedSampleRadius, blurRadiusInPixels);
    }

    protected static String vertexShaderForOptimizedBlurOfRadius(int blurRadius, double sigma) {
        if (blurRadius < 1) {
            return NO_FILTER_VERTEX_SHADER;
        }

        // First, generate the normal Gaussian weights for a given sigma
        double[] standardGaussianWeights = new double[blurRadius + 1];
        double sumOfWeights = 0.0;

        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; ++currentGaussianWeightIndex)
        {
            standardGaussianWeights[currentGaussianWeightIndex] = (1.0 / Math.sqrt(2.0 * Math.PI * Math.pow(sigma, 2.0))) * Math.exp(-Math.pow(currentGaussianWeightIndex, 2.0) / (2.0 * Math.pow(sigma, 2.0)));

            if (currentGaussianWeightIndex == 0) {
                sumOfWeights += standardGaussianWeights[currentGaussianWeightIndex];
            } else {
                sumOfWeights += 2.0 * standardGaussianWeights[currentGaussianWeightIndex];
            }
        }

        // Next, normalize these weights to prevent the clipping of the Gaussian curve at the end of the discrete samples from reducing luminance
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; ++currentGaussianWeightIndex)
        {
            standardGaussianWeights[currentGaussianWeightIndex] = standardGaussianWeights[currentGaussianWeightIndex] / sumOfWeights;
        }

        // From these weights we calculate the offsets to read interpolated values from
        int numberOfOptimizedOffsets = Math.min(blurRadius / 2 + (blurRadius % 2), 7);
        double[] optimizedGaussianOffsets = new double[numberOfOptimizedOffsets];

        for (int currentOptimizedOffset = 0; currentOptimizedOffset < numberOfOptimizedOffsets; currentOptimizedOffset++)
        {
            double firstWeight = standardGaussianWeights[currentOptimizedOffset*2 + 1];
            double secondWeight = standardGaussianWeights[currentOptimizedOffset*2 + 2];

            double optimizedWeight = firstWeight + secondWeight;

            optimizedGaussianOffsets[currentOptimizedOffset] = (firstWeight * (currentOptimizedOffset*2 + 1) + secondWeight * (currentOptimizedOffset*2 + 2)) / optimizedWeight;
        }

        StringBuilder shaderStringBuilder = new StringBuilder();
        String shaderString =
                "attribute vec4 position;\n"+
                        "attribute vec4 inputTextureCoordinate;\n"+
                        "uniform float texelWidthOffset;\n"+
                        "uniform float texelHeightOffset;\n"+
                        "varying vec2 blurCoordinates[" + (1 + (numberOfOptimizedOffsets * 2)) +"];\n"+
                        "void main() {\n"+
                        "  gl_Position = position;\n"+
                        "  vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n";
        shaderStringBuilder.append(shaderString);

        // Inner offset loop
        shaderStringBuilder.append("blurCoordinates[0] = inputTextureCoordinate.xy;\n");
        for (int currentOptimizedOffset = 0; currentOptimizedOffset < numberOfOptimizedOffsets; ++currentOptimizedOffset) {
            shaderString = "blurCoordinates[" + ((currentOptimizedOffset * 2) + 1) + "] = inputTextureCoordinate.xy + singleStepOffset * " + optimizedGaussianOffsets[currentOptimizedOffset] + ";\n";
            shaderStringBuilder.append(shaderString);
            shaderString = "blurCoordinates[" + ((currentOptimizedOffset * 2) + 2) + "] = inputTextureCoordinate.xy - singleStepOffset * " + optimizedGaussianOffsets[currentOptimizedOffset] + ";\n";
            shaderStringBuilder.append(shaderString);
        }
        shaderStringBuilder.append("}\n");
        return shaderStringBuilder.toString();
    }

    protected static String fragmentShaderForOptimizedBlurOfRadius(int blurRadius, double sigma) {
        if (blurRadius < 1) {
            return NO_FILTER_FRAGMENT_SHADER;
        }

        // First, generate the normal Gaussian weights for a given sigma
        double[] standardGaussianWeights = new double[blurRadius + 1];
        double sumOfWeights = 0.0;
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; ++currentGaussianWeightIndex) {
            standardGaussianWeights[currentGaussianWeightIndex] = (1.0 / Math.sqrt(2.0 * Math.PI * Math.pow(sigma, 2.0))) * Math.exp(-Math.pow(currentGaussianWeightIndex, 2.0) / (2.0 * Math.pow(sigma, 2.0)));

            if (currentGaussianWeightIndex == 0) {
                sumOfWeights += standardGaussianWeights[currentGaussianWeightIndex];
            } else {
                sumOfWeights += 2.0 * standardGaussianWeights[currentGaussianWeightIndex];
            }
        }

        // Next, normalize these weights to prevent the clipping of the Gaussian curve at the end of the discrete samples from reducing luminance
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; ++currentGaussianWeightIndex)
        {
            standardGaussianWeights[currentGaussianWeightIndex] = standardGaussianWeights[currentGaussianWeightIndex] / sumOfWeights;
        }

        // From these weights we calculate the offsets to read interpolated values from
        int numberOfOptimizedOffsets = Math.min(blurRadius / 2 + (blurRadius % 2), 7);
        int trueNumberOfOptimizedOffsets = blurRadius / 2 + (blurRadius % 2);

        StringBuilder shaderStringBuilder = new StringBuilder();
        String shaderString =
                "uniform sampler2D inputImageTexture;\n"+
                        "uniform highp float texelWidthOffset;\n"+
                        "uniform highp float texelHeightOffset;\n"+
                        "varying highp vec2 blurCoordinates["+(1 + (numberOfOptimizedOffsets * 2))+"];\n"+
                        "void main() {\n"+
                        "  lowp float sum = 0.0;\n";
        shaderStringBuilder.append(shaderString);

        shaderString = "sum += texture2D(inputImageTexture, blurCoordinates[0]).r * " + standardGaussianWeights[0] + ";\n";
        shaderStringBuilder.append(shaderString);

        for (int currentBlurCoordinateIndex = 0; currentBlurCoordinateIndex < numberOfOptimizedOffsets; ++currentBlurCoordinateIndex) {
            double firstWeight = standardGaussianWeights[currentBlurCoordinateIndex * 2 + 1];
            double secondWeight = standardGaussianWeights[currentBlurCoordinateIndex * 2 + 2];
            double optimizedWeight = firstWeight + secondWeight;

            shaderString = "sum += texture2D(inputImageTexture, blurCoordinates["+((currentBlurCoordinateIndex * 2) + 1)+"]).r * "+optimizedWeight+";\n";
            shaderStringBuilder.append(shaderString);
            shaderString = "sum += texture2D(inputImageTexture, blurCoordinates["+((currentBlurCoordinateIndex * 2) + 2)+"]).r * "+optimizedWeight+";\n";
            shaderStringBuilder.append(shaderString);
        }

        if (trueNumberOfOptimizedOffsets > numberOfOptimizedOffsets) {
            shaderStringBuilder.append("highp vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n");

            for (int currentOverlowTextureRead = numberOfOptimizedOffsets; currentOverlowTextureRead < trueNumberOfOptimizedOffsets; ++currentOverlowTextureRead) {
                double firstWeight = standardGaussianWeights[currentOverlowTextureRead * 2 + 1];
                double secondWeight = standardGaussianWeights[currentOverlowTextureRead * 2 + 2];

                double optimizedWeight = firstWeight + secondWeight;
                double optimizedOffset = (firstWeight * (currentOverlowTextureRead * 2 + 1) + secondWeight * (currentOverlowTextureRead * 2 + 2)) / optimizedWeight;

                shaderString = "sum += texture2D(inputImageTexture, blurCoordinates[0] + singleStepOffset * "+optimizedOffset+").r * "+optimizedWeight+";\n";
                shaderStringBuilder.append(shaderString);
                shaderString = "sum += texture2D(inputImageTexture, blurCoordinates[0] - singleStepOffset * "+optimizedOffset+").r * "+optimizedWeight+";\n";
                shaderStringBuilder.append(shaderString);
            }
        }
        shaderStringBuilder.append("gl_FragColor = vec4(sum, sum, sum, 1.0);\n}\n");

        return shaderStringBuilder.toString();
    }
}
