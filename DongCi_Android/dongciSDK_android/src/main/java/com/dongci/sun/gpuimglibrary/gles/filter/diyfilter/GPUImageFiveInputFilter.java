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

public class GPUImageFiveInputFilter extends GPUImageFourInputFilter {
    public static final String kGPUImageFiveInputTextureVertexShaderString =
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "attribute vec4 inputTextureCoordinate2;\n" +
            "attribute vec4 inputTextureCoordinate3;\n" +
            "attribute vec4 inputTextureCoordinate4;\n" +
            "attribute vec4 inputTextureCoordinate5;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 textureCoordinate2;\n" +
            "varying vec2 textureCoordinate3;\n" +
            "varying vec2 textureCoordinate4;\n" +
            "varying vec2 textureCoordinate5;\n" +
            "\n" +
            "void main() {\n" +
            "  gl_Position = position;\n" +
            "  textureCoordinate = inputTextureCoordinate.xy;\n" +
            "  textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "  textureCoordinate3 = inputTextureCoordinate3.xy;\n" +
            "  textureCoordinate4 = inputTextureCoordinate4.xy;\n" +
            "  textureCoordinate5 = inputTextureCoordinate5.xy;\n" +
            "}";

    private int mFilter5TextureCoordinateAttribute;
    private int mFilterInputTextureUniform5;
    private int mFilterSourceTexture5 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture5CoordinatesBuffer;
    private Bitmap mBitmap;

    public GPUImageFiveInputFilter(String fragmentShader) {
        super(kGPUImageFiveInputTextureVertexShaderString, fragmentShader);
    }

    public GPUImageFiveInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onInit() {
        super.onInit();

        mFilter5TextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate5");
        mFilterInputTextureUniform5 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture5"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(mFilter5TextureCoordinateAttribute);

        if (mBitmap != null&&!mBitmap.isRecycled()) {
            setBitmap5(mBitmap);
        }
    }

    public void setTexture5(int texture) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
        mFilterSourceTexture5 = texture;
    }

    public void setBitmap5(final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap;
        if (mBitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {
            public void run() {
                if (mFilterSourceTexture5 == OpenGlUtils.NO_TEXTURE) {
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
                    mFilterSourceTexture5 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
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
                mFilterSourceTexture5
        }, 0);
        mFilterSourceTexture5 = OpenGlUtils.NO_TEXTURE;
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES20.glEnableVertexAttribArray(mFilter5TextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture5);
        GLES20.glUniform1i(mFilterInputTextureUniform5, 6);

        mTexture5CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mFilter5TextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture5CoordinatesBuffer);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        super.setRotation(rotation, flipHorizontal, flipVertical);
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture5CoordinatesBuffer = bBuffer;
    }
}
