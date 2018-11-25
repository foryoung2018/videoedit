package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.dongci.sun.gpuimglibrary.gles.OpenGlUtils;
import com.dongci.sun.gpuimglibrary.common.Rotation;
import com.dongci.sun.gpuimglibrary.gles.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zhangxiao on 2018/7/31.
 */

public class GPUImageFourInputFilter extends GPUImageThreeInputFilter {
    public static final String kGPUImageFourInputTextureVertexShaderString =
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "attribute vec4 inputTextureCoordinate2;\n" +
            "attribute vec4 inputTextureCoordinate3;\n" +
            "attribute vec4 inputTextureCoordinate4;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 textureCoordinate2;\n" +
            "varying vec2 textureCoordinate3;\n" +
            "varying vec2 textureCoordinate4;\n" +
            "\n" +
            "void main() {\n" +
            "  gl_Position = position;\n" +
            "  textureCoordinate = inputTextureCoordinate.xy;\n" +
            "  textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "  textureCoordinate3 = inputTextureCoordinate3.xy;\n" +
            "  textureCoordinate4 = inputTextureCoordinate4.xy;\n" +
            "}";

    private int mFilter4TextureCoordinateAttribute;
    private int mFilterInputTextureUniform4;
    private int mFilterSourceTexture4 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture4CoordinatesBuffer;
    private Bitmap mBitmap;

    public GPUImageFourInputFilter(String fragmentShader) {
        super(kGPUImageFourInputTextureVertexShaderString, fragmentShader);
    }

    public GPUImageFourInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onInit() {
        super.onInit();

        mFilter4TextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate4");
        mFilterInputTextureUniform4 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture4"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(mFilter4TextureCoordinateAttribute);

        if (mBitmap != null&&!mBitmap.isRecycled()) {
            setBitmap4(mBitmap);
        }
    }

    public void setTexture4(int texture) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        mFilterSourceTexture4 = texture;
    }

    public void setBitmap4(final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap;
        if (mBitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {
            public void run() {
                if (mFilterSourceTexture4 == OpenGlUtils.NO_TEXTURE) {
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
                    mFilterSourceTexture4 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
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
                mFilterSourceTexture4
        }, 0);
        mFilterSourceTexture4 = OpenGlUtils.NO_TEXTURE;
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES20.glEnableVertexAttribArray(mFilter4TextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture4);
        GLES20.glUniform1i(mFilterInputTextureUniform4, 5);

        mTexture4CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mFilter4TextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture4CoordinatesBuffer);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        super.setRotation(rotation, flipHorizontal, flipVertical);
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture4CoordinatesBuffer = bBuffer;
    }
}
