package com.dongci.sun.gpuimglibrary.player.renderObject;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class DCRenderFilter {
    private static final String TAG = "DCRenderFilter";

    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    protected static final int FLOAT_SIZE_BYTES = 4;
    protected static final int VERTICES_DATA_STRIDE_BYTES = 3 * FLOAT_SIZE_BYTES;
    protected static final int COORDS_DATA_STRIDE_BYTES = 2 * FLOAT_SIZE_BYTES;
    public static final String vertexShader = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String fragmentShader = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform samplerExternalOES texture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(texture, textureCoordinate);\n" +
            "}";

    private float[] surfaceMatrix = new float[16];
    private int positionHandle;
    private int textureCoordinateHandle;
    private int uniformTextureHandle;

    private int textureID;
    private int program;

    //private  int  width;
    //private  int  height;
    public SurfaceTexture surface;

    private boolean isInited = false;


    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    private final float[] vertexData = {
            1f,-1f,0f,
            -1f,-1f,0f,
            1f,1f,0f,
            -1f,1f,0f
    };

//    private final float[] textureVertexData = {
//            1f,0f,
//            0f,0f,
//            1f,1f,
//            0f,1f
//    };

//    private final float[] textureVertexData = {
//            0f,1f,
//            1f,1f,
//            0f,0f,
//            1f,0f
//    };


    private final float[] textureVertexData = {
            1f,1f,
            0f,1f,
            1f,0f,
            0f,0f
    };


    public DCRenderFilter() {

    }

    public void   init(int width,int height) {
//        int[] textures = new int[1];
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glGenTextures(1, textures, 0);
//        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[0]);
//
//        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//
//        this.textureID = textures[0];
//        this.surface = new SurfaceTexture(this.textureID);

        //清除遗留的
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
        /**创建一个帧染缓冲区对象*/
        GLES20.glGenFramebuffers(1, fFrame, 0);
        /**根据纹理数量 返回的纹理索引*/
        GLES20.glGenTextures(1, fTexture, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


    }


    private boolean useProgram() {
        if (program == 0) {
            program = createProgram();
            if (program == 0) {
                return false;
            }

            positionHandle = GLES20.glGetAttribLocation(program, "position");
            checkGlError("glGetAttribLocation position");
            if (positionHandle == -1) {
                return false;
            }

            textureCoordinateHandle = GLES20.glGetAttribLocation(program, "inputTextureCoordinate");
            checkGlError("glGetAttribLocation inputTextureCoordinate");
            if (textureCoordinateHandle == -1) {
                return false;
            }


            uniformTextureHandle = GLES20.glGetUniformLocation(program, "texture");
            checkGlError("glGetUniformLocation uniformTextureHandle");
            if (uniformTextureHandle == -1) {
                return false;
            }
        }
        GLES20.glUseProgram(program);
        checkGlError("glUseProgram");
        return true;
    }

    private void bindFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        //GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                //GLES20.GL_TEXTURE_2D, fTexture[0], 0);
    }

    private void unBindFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int draw( int textureID,int width,int height) {

        if(!isInited) {
            init(width,height);
            isInited = true;
            this.textureID = textureID;
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, fTexture[0], 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }


        if(!useProgram()) {
            return  0;
        }

        bindFrameBuffer();

        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureID);

        FloatBuffer vertexBuffer1 = ByteBuffer.allocateDirect(
                vertexData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer1.put(vertexData).position(0);

        FloatBuffer coordBuffer1 = ByteBuffer.allocateDirect(
                textureVertexData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        coordBuffer1.put(textureVertexData).position(0);


        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
                VERTICES_DATA_STRIDE_BYTES, vertexBuffer1);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                COORDS_DATA_STRIDE_BYTES, coordBuffer1);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);

        unBindFrameBuffer();
        return  fTexture[0];
    }

    protected int createProgram() {
        return createProgram(vertexShader, fragmentShader);
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(pixelShader);

        return program;
    }

    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }


    static boolean checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            return false;
        }
        return true;
    }

    public void release() {
        if (this.textureID != 0) {
            int[] textures = {this.textureID};
            GLES20.glDeleteTextures(1, textures, 0);
            this.textureID = 0;
        }

        if (this.program != 0) {
            GLES20.glDeleteProgram(this.program);
            this.program = 0;
        }

        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }


}
