package com.dongci.sun.gpuimglibrary.gles;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Message;

import com.dongci.sun.gpuimglibrary.common.Constant;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;


public class CameraEGLRender {//fixme 字节序
    private static final String TAG = CameraEGLRender.class.getSimpleName();
    private int mGLProgId;
    private int mGLPositionIndex;
    private int mGLInputImageTextureIndex;
    private int mGLTextureCoordinateIndex;
    private int mGLTextureTransformIndex;

    private int mPictureWidth;
    private int mPictureHeight;

    private int[] mGLCubeId;
    private int[] mGLTextureCoordinateId;
    private FloatBuffer mGLCubeBuffer;
    private FloatBuffer mGLTextureBuffer;

    private int[] mGLFboId;
    private int[] mGLFboTexId;
    private ByteBuffer mGLFboBuffer;
    private IntBuffer mGLFboBufferTmp;

    private final Context mContext;
    private final EventHandler mEventHandler;
    private final int mOESTextureId;

    private volatile boolean mInitialized = false;
    private static final String VERTEX_SHADER =
            "attribute vec4 position;                                                         \n" +
                    "attribute vec4 inputTextureCoordinate;                                   \n" +
                    "varying vec2 textureCoordinate;                                          \n" +
                    "uniform mat4 textureTransform;                                           \n" +
                    "void main() {                                                            \n" +
                    "    textureCoordinate = (textureTransform * inputTextureCoordinate).xy;  \n" +
                    "    gl_Position = position;                                              \n" +
                    "}                                                                        \n";


    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require                                   \n" +
                    "precision mediump float;                                                 \n" +
                    "varying mediump vec2 textureCoordinate;                                  \n" +
                    "uniform samplerExternalOES inputImageTexture;                            \n" +
                    "void main() {                                                            \n" +
                    "    gl_FragColor = texture2D(inputImageTexture, textureCoordinate);      \n" +
                    "}                                                                        \n";


    public CameraEGLRender(Context context, EventHandler eventHandler, int OESTextureId) {
        mContext = context;
        mOESTextureId = OESTextureId;
        mEventHandler = eventHandler;
        init();
    }

    private void init() {
        GLES20.glDisable(GL10.GL_DITHER);//取消抖动算法 http://blog.csdn.net/grimraider/article/details/7449278
        initVbo();
        boolean ret = loadSamplerShader();
        if (!ret) {
            if (mEventHandler.isHandleError()) {
                Message msg = Message.obtain();
                msg.what = Constant.CAMERA_EGL_RENDER_INITIALIZE_FAILED;
                mEventHandler.sendMessage(msg);
            }
            return;
        }
        if (mEventHandler.isHandleInfo()) {
            Message msg = Message.obtain();
            msg.what = Constant.CAMERA_EGL_RENDER_INITIALIZE_SUCCEED;
            mEventHandler.sendMessage(msg);
        }
        mInitialized = true;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public void onPictureSizeChanged(int width, int height) {
        if (!mInitialized) {
            return;
        }

        GLES20.glViewport(0, 0, width, height);
        initFboTexture(width, height);
        mPictureWidth = width;
        mPictureHeight = height;
    }


    private boolean loadSamplerShader() {
        mGLProgId = EGLUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (mGLProgId == 0) {
            return false;
        }

        mGLPositionIndex = GLES20.glGetAttribLocation(mGLProgId, "position");
        mGLTextureCoordinateIndex = GLES20.glGetAttribLocation(mGLProgId, "inputTextureCoordinate");
        mGLTextureTransformIndex = GLES20.glGetUniformLocation(mGLProgId, "textureTransform");
        mGLInputImageTextureIndex = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture");
        return true;
    }

    private void initVbo() {
        //前后摄像头切换的时候转换渲染
        final float VEX_CUBE[] = {
                -1.0f, -1.0f, // Bottom left.
                1.0f, -1.0f, // Bottom right.
                -1.0f, 1.0f, // Top left.
                1.0f, 1.0f, // Top right.
        };

        final float TEX_COORD[] = {
                0.0f, 0.0f, // Bottom left.
                1.0f, 0.0f, // Bottom right.
                0.0f, 1.0f, // Top left.
                1.0f, 1.0f // Top right.
        };

        mGLCubeBuffer = ByteBuffer.allocateDirect(VEX_CUBE.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(VEX_CUBE).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TEX_COORD.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(TEX_COORD).position(0);

        mGLCubeId = new int[1];
        mGLTextureCoordinateId = new int[1];

        GLES20.glGenBuffers(1, mGLCubeId, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGLCubeId[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mGLCubeBuffer.capacity() * 4, mGLCubeBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glGenBuffers(1, mGLTextureCoordinateId, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGLTextureCoordinateId[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mGLTextureBuffer.capacity() * 4, mGLTextureBuffer, GLES20.GL_STATIC_DRAW);
    }

    private void destroyVbo() {
        if (mGLCubeId != null) {
            GLES20.glDeleteBuffers(1, mGLCubeId, 0);
            mGLCubeId = null;
        }
        if (mGLTextureCoordinateId != null) {
            GLES20.glDeleteBuffers(1, mGLTextureCoordinateId, 0);
            mGLTextureCoordinateId = null;
        }
    }


    private void initFboTexture(int width, int height) {
        if (mGLFboId != null) {
            if ((mPictureWidth == width && mPictureHeight == height)) {
                return;
            } else {
                destroyFboTexture();
            }
        }
        mGLFboId = new int[1];
        mGLFboTexId = new int[1];
        mGLFboBuffer = ByteBuffer.allocateDirect(width * height * 4);
        mGLFboBufferTmp = IntBuffer.allocate(width * height);
        GLES20.glGenFramebuffers(1, mGLFboId, 0);
        GLES20.glGenTextures(1, mGLFboTexId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLFboTexId[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mGLFboId[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mGLFboTexId[0], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    private void destroyFboTexture() {
        if (mGLFboTexId != null) {
            GLES20.glDeleteTextures(1, mGLFboTexId, 0);
            mGLFboTexId = null;
        }
        if (mGLFboId != null) {
            GLES20.glDeleteFramebuffers(1, mGLFboId, 0);
            mGLFboId = null;
        }
    }


    public void drawFrame(float[] mtx) {
        if (!mInitialized) {
            return;
        }

        GLES20.glUseProgram(mGLProgId);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGLCubeId[0]);
        GLES20.glEnableVertexAttribArray(mGLPositionIndex);
        GLES20.glVertexAttribPointer(mGLPositionIndex, 2, GLES20.GL_FLOAT, false, 4 * 2, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGLTextureCoordinateId[0]);
        GLES20.glEnableVertexAttribArray(mGLTextureCoordinateIndex);
        GLES20.glVertexAttribPointer(mGLTextureCoordinateIndex, 2, GLES20.GL_FLOAT, false, 4 * 2, 0);

        GLES20.glUniformMatrix4fv(mGLTextureTransformIndex, 1, false, mtx, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        GLES20.glUniform1i(mGLInputImageTextureIndex, 0);


        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mGLFboId[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glReadPixels(0, 0, mPictureWidth, mPictureHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mGLFboBufferTmp);
        mGLFboBuffer.asIntBuffer().put(mGLFboBufferTmp.array());//字节序问题，不在这里转换， return _ConvertToNV12(env, data, width, height, FOURCC_ABGR, dst_width, dst_height);
        //fixme 可能存在性能问题。
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);//第一次 draw 是往 framebuffer 中 写数据， 第二次是往 surface 上画

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        GLES20.glDisableVertexAttribArray(mGLPositionIndex);
        GLES20.glDisableVertexAttribArray(mGLTextureCoordinateIndex);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glUseProgram(0);

    }


    public ByteBuffer getGLFboBuffer() {
        return mGLFboBuffer;
    }


    public void release() {
        destroyFboTexture();
        destroyVbo();
        if (mGLProgId != 0) {
            GLES20.glDeleteProgram(mGLProgId);
        }
    }

}

