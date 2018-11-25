package com.dongci.sun.gpuimglibrary.gles;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class EGLUtils {

    private static final String TAG = EGLUtils.class.getSimpleName();

    public static final int NO_TEXTURE = -1;
    public static final int SIZEOF_FLOAT = 4;

    public static int loadTexture(final Context context, final String name) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        checkEGlError("glGenTextures");
        if (textureHandle[0] != 0) {

            // Read in the resource
            final Bitmap bitmap = getImageFromAssetsFile(context, name);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            checkEGlError("texImage2D");
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }

    public static int loadTexture(Bitmap img, int usedTexId) {
        return loadTexture(img, usedTexId, false);
    }

    public static int loadTexture(Bitmap img, int usedTexId, boolean recyled) {
        if (img == null)
            return NO_TEXTURE;
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            checkEGlError("glGenTextures");
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
            checkEGlError("texImage2D");
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img);
            checkEGlError("glTexSubImage2D");
            textures[0] = usedTexId;
        }
        if (recyled)
            img.recycle();
        return textures[0];
    }

    public static int loadTexture(Buffer data, int width, int height, int usedTexId) {
        return loadTexture(data, width, height, usedTexId, GLES20.GL_UNSIGNED_BYTE);
    }

    public static int loadTexture(Buffer data, int width, int height, int usedTexId, int type) {
        if (data == null)
            return NO_TEXTURE;
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            checkEGlError("glGenTextures");
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                    0, GLES20.GL_RGBA, type, data);
            checkEGlError("glTexImage2D");
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width,
                    height, GLES20.GL_RGBA, type, data);
            checkEGlError("glTexSubImage2D");
            textures[0] = usedTexId;
        }
        return textures[0];
    }


    private static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static int loadProgram(String strVSource, String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }

        iProgId = GLES20.glCreateProgram();
        checkEGlError("glCreateProgram");
        if (iProgId == 0) {
            Log.e(TAG, "Could not create program");
            return iProgId;
        }

        GLES20.glAttachShader(iProgId, iVShader);
        checkEGlError("glAttachShader");
        GLES20.glAttachShader(iProgId, iFShader);
        checkEGlError("glAttachShader");
        GLES20.glLinkProgram(iProgId);
        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(iProgId));
            GLES20.glDeleteProgram(iProgId);
            iProgId = 0;
        }
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }

    private static int loadShader(String strSource, int iType) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        checkEGlError("glCreateShader type=" + iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + iType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(iShader));
            GLES20.glDeleteShader(iShader);
            iShader = 0;
        }
        return iShader;
    }

    public static int getExternalOESTextureID() {
        int[] texture = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, texture, 0);
        //在绑定纹理之前，先激活纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);

        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

//        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return texture[0];
    }

    /**
     * GL_NEAREST和GL_LINEAR
     * <p>
     * 前者表示“使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色”，
     * <p>
     * 后者表示“使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色”。
     * <p>
     * 前者只经过简单比较，需要运算较少，可能速度较快，
     * <p>
     * 后者需要经过加权平均计算，其中涉及除法运算，可能速度较慢（但如果有专门的处理硬件，也可能两者速度相同）。
     * <p>
     * 从视觉效果上看，前者效果较差，在一些情况下锯齿现象明显，后者效果会较好（但如果纹理图象本身比较大，则两者在视觉效果上就会比较接近）。
     */


    public static void deleteTexture(int textureId) {
        if (textureId != EGLUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        }
    }

    public static String readShaderFromRawResource(Context context, int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return body.toString();
    }

    /**
     * Allocates a direct float buffer, and populates it with the float array data.
     */
    public static FloatBuffer createFloatBuffer(float[] coords) {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(coords);
        fb.position(0);
        return fb;
    }

    /**
     * Checks to see if a GLES error has been raised.
     */
    public static boolean checkEGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            return false;
        } else {
            return true;
        }
    }
}
