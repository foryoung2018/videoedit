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

public class GPUImageSixInputFilter extends GPUImageFiveInputFilter {
    public static final String kGPUImageSixInputTextureVertexShaderString =
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "attribute vec4 inputTextureCoordinate2;\n" +
            "attribute vec4 inputTextureCoordinate3;\n" +
            "attribute vec4 inputTextureCoordinate4;\n" +
            "attribute vec4 inputTextureCoordinate5;\n" +
            "attribute vec4 inputTextureCoordinate6;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 textureCoordinate2;\n" +
            "varying vec2 textureCoordinate3;\n" +
            "varying vec2 textureCoordinate4;\n" +
            "varying vec2 textureCoordinate5;\n" +
            "varying vec2 textureCoordinate6;\n" +
            "\n" +
            "void main() {\n" +
            "  gl_Position = position;\n" +
            "  textureCoordinate = inputTextureCoordinate.xy;\n" +
            "  textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "  textureCoordinate3 = inputTextureCoordinate3.xy;\n" +
            "  textureCoordinate4 = inputTextureCoordinate4.xy;\n" +
            "  textureCoordinate5 = inputTextureCoordinate5.xy;\n" +
            "  textureCoordinate6 = inputTextureCoordinate6.xy;\n" +
            "}";

    private int mFilter6TextureCoordinateAttribute;
    private int mFilterInputTextureUniform6;
    private int mFilterSourceTexture6 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture6CoordinatesBuffer;
    private Bitmap mBitmap;

    public GPUImageSixInputFilter(String fragmentShader) {
        super(kGPUImageSixInputTextureVertexShaderString, fragmentShader);
    }

    public GPUImageSixInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onInit() {
        super.onInit();

        mFilter6TextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate6");
        mFilterInputTextureUniform6 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture6"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(mFilter6TextureCoordinateAttribute);

        if (mBitmap != null&&!mBitmap.isRecycled()) {
            setBitmap6(mBitmap);
        }
    }

    public void setTexture6(int texture) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
        mFilterSourceTexture6 = texture;
    }

    public void setBitmap6(final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap;
        if (mBitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {
            public void run() {
                if (mFilterSourceTexture6 == OpenGlUtils.NO_TEXTURE) {
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
                    mFilterSourceTexture6 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
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
                mFilterSourceTexture6
        }, 0);
        mFilterSourceTexture6 = OpenGlUtils.NO_TEXTURE;
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES20.glEnableVertexAttribArray(mFilter6TextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture6);
        GLES20.glUniform1i(mFilterInputTextureUniform6, 7);

        mTexture6CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mFilter6TextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture6CoordinatesBuffer);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        super.setRotation(rotation, flipHorizontal, flipVertical);
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture6CoordinatesBuffer = bBuffer;
    }
}
