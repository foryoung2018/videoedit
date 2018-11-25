package com.dongci.sun.gpuimglibrary.player.renderObject;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.player.DCAssetInfo;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEvent;


public class DCRenderVideo extends DCRenderObject {
    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

//    private static final String fragmentShader =
//            "#extension GL_OES_EGL_image_external : require\n" +
//                    "precision mediump float;\n" +
//                    "varying vec2 textureCoordinate;\n" +
//                    "uniform float transparency;\n" +
//                    "uniform samplerExternalOES texture;\n" +
//                    "void main() {\n" +
//                    "  vec4 texColor = texture2D(texture, textureCoordinate);\n" +
//                    "  gl_FragColor = vec4(texColor.rgb, texColor.a * transparency);" +
//                    "}\n";


    private static final String fragmentShader =
            "precision mediump float;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform float transparency;\n" +
                    "uniform sampler2D texture;\n" +
                    "void main() {\n" +
                    "  vec4 texColor = texture2D(texture, textureCoordinate);\n" +
                    "  gl_FragColor = vec4(texColor.rgb, texColor.a * transparency);" +
                    "}\n";
    private float[] surfaceMatrix = new float[16];
    public SurfaceTexture surface;

    public SurfaceTexture filterSurface;

    public int filterTextureID;

    public DCRenderFilter filter;

    public DCRenderVideo(DCAssetInfo assetInfo) {
        super(assetInfo);
        init();

    }

    private void init() {
        int[] textures = new int[1];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        this.textureID = textures[0];
        this.surface = new SurfaceTexture(this.textureID);


        int[] textures1 = new int[1];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textures1, 0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures1[0]);

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        this.filterTextureID = textures1[0];
        this.filterSurface = new SurfaceTexture(this.filterTextureID);


        filter = new DCRenderFilter();


    }


    @Override
    protected int drawFrames(int width,int height) {
        int textureID =   filter.draw(filterTextureID,width,height);
        return  textureID;
    }

    @Override
    protected void updateParameters(int textureId) {
        // set texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(uniformTextureHandle, 0);

        // set matrix
        if (this.surface != null) {
            this.surface.getTransformMatrix(surfaceMatrix);
            GLES20.glUniformMatrix4fv(uvMatrixHandle, 1, false, surfaceMatrix, 0);
        }
    }

    @Override
    public int updateTexture(long presentationTime, DCTimeEvent timeEvent) {
        return this.textureID;
    }

    @Override
    protected int createProgram() {
        return createProgram(vertexShader, fragmentShader);
    }

    @Override
    public void release() {
        super.release();
        if(filter != null) {
            filter.release();
        }
    }
}
