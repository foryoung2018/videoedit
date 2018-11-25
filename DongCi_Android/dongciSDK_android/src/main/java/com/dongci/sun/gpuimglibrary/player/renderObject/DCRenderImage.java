package com.dongci.sun.gpuimglibrary.player.renderObject;

import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.dongci.sun.gpuimglibrary.player.DCAssetInfo;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEvent;

public class DCRenderImage extends DCRenderObject {
    private static final String fragmentShader =
            "precision mediump float;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform float transparency;\n" +
                    "uniform sampler2D texture;\n" +
                    "void main() {\n" +
                    "  vec4 texColor = texture2D(texture, textureCoordinate);\n" +
                    "  gl_FragColor = vec4(texColor.rgb, texColor.a * transparency);" +
                    "}\n";
    private static final float[] defaultUVMatrix = new float[]{
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f};

    public DCRenderImage(DCAssetInfo assetInfo) {
        super(assetInfo);
    }

    @Override
    protected void updateParameters(int textureId) {
        // set texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(uniformTextureHandle, 0);

        // set matrix
        GLES20.glUniformMatrix4fv(uvMatrixHandle, 1, false, defaultUVMatrix, 0);
    }

    @Override
    public int updateTexture(long presentationTime, DCTimeEvent timeEvent) {
        if (this.textureID == 0) {
            if (this.assetInfo.assetWrapper.getBitmap(0) == null) {
                return 0;
            }
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);

            this.textureID = textures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textureID);
            checkGlError("glBindTexture mTextureID");
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, this.assetInfo.assetWrapper.getBitmap(0), 0);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
        return this.textureID;
    }

    @Override
    protected int createProgram() {
        return createProgram(vertexShader, fragmentShader);
    }
}