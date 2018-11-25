package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageTwoInputFilter;
import com.dongci.sun.gpuimglibrary.gles.OpenGlUtils;
import com.dongci.sun.gpuimglibrary.common.Rotation;
import com.dongci.sun.gpuimglibrary.gles.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zhangxiao on 2018/7/30.
 */

public class GPUImageThreeInputFilter extends GPUImageTwoInputFilter {
    public static final String kGPUImageThreeInputTextureVertexShaderString =
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "attribute vec4 inputTextureCoordinate2;\n" +
            "attribute vec4 inputTextureCoordinate3;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 textureCoordinate2;\n" +
            "varying vec2 textureCoordinate3;\n" +
            "\n" +
            "void main() {\n" +
            "  gl_Position = position;\n" +
            "  textureCoordinate = inputTextureCoordinate.xy;\n" +
            "  textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "  textureCoordinate3 = inputTextureCoordinate3.xy;\n" +
            "}";

    private int mFilterThreeTextureCoordinateAttribute;
    private int mFilterInputTextureUniform3;
    private int mFilterSourceTexture3 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture3CoordinatesBuffer;
    private Bitmap mBitmap;

    public GPUImageThreeInputFilter(String fragmentShader) {
        super(kGPUImageThreeInputTextureVertexShaderString, fragmentShader);
    }

    public GPUImageThreeInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onInit() {
        super.onInit();

        mFilterThreeTextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate3");
        mFilterInputTextureUniform3 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture3"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(mFilterThreeTextureCoordinateAttribute);

        if (mBitmap != null&&!mBitmap.isRecycled()) {
            setBitmap3(mBitmap);
        }
    }

    public void setTexture3(int texture) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        mFilterSourceTexture3 = texture;
    }

    public void setBitmap3(final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap;
        if (mBitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {
            public void run() {
                if (mFilterSourceTexture3 == OpenGlUtils.NO_TEXTURE) {
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
                    mFilterSourceTexture3 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
                }
            }
        });
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void recycleBitmap() {
        super.recycleBitmap();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{
                mFilterSourceTexture3
        }, 0);
        mFilterSourceTexture3 = OpenGlUtils.NO_TEXTURE;
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES20.glEnableVertexAttribArray(mFilterThreeTextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture3);
        GLES20.glUniform1i(mFilterInputTextureUniform3, 4);

        mTexture3CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mFilterThreeTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture3CoordinatesBuffer);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        super.setRotation(rotation, flipHorizontal, flipVertical);
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture3CoordinatesBuffer = bBuffer;
    }
}
