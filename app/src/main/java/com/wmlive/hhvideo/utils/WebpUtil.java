package com.wmlive.hhvideo.utils;

import android.graphics.Bitmap;

import com.google.webp.libwebp;
import com.google.webp.my.WebpUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 作者: andy
 * 时间: 16-4-8
 * 描述:
 * 修订: 暂无
 */
public class WebpUtil {

    static {
        System.loadLibrary("webp");
    }

    /**
     * 字节数组转换为bitmap对象
     *
     * @param encoded
     * @return
     */
    public static Bitmap webpToBitmap(byte[] encoded) {
        int[] width = new int[]{0};
        int[] height = new int[]{0};
        byte[] decoded = libwebp.WebPDecodeARGB(encoded, encoded.length, width,
                height);

        int[] pixels = new int[decoded.length / 4];
        ByteBuffer.wrap(decoded).asIntBuffer().get(pixels);

        return Bitmap.createBitmap(pixels, width[0], height[0],
                Bitmap.Config.ARGB_8888);
    }

    /**
     * 流转换为字节数组
     *
     * @param in
     * @return
     */
    public static byte[] streamToBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
                out.flush();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }


    /**
     * bitmpa BGRA
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBGRA(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        byte[] bs = new byte[w * h * 4];
        int x = 0;
        int index = 0;

        for (int i = 0; i < w; i++)
            // 水平
            for (int j = 0; j < h; j++)// 垂直
            {
                x = bitmap.getPixel(i, j);
                index = (j * w + i) * 4;
                bs[index] = (byte) (x & 0xff);
                bs[index + 1] = (byte) (x >> 8 & 0xff);
                bs[index + 2] = (byte) (x >> 16 & 0xff);
                bs[index + 3] = (byte) (x >> 24 & 0xff);
            }
        return bs;
    }

    public static boolean isWebp(byte[] data) {
        return data != null && data.length > 12 && data[0] == 'R'
                && data[1] == 'I' && data[2] == 'F' && data[3] == 'F'
                && data[8] == 'W' && data[9] == 'E' && data[10] == 'B'
                && data[11] == 'P';
    }

    /**
     * 获取webp版本
     * 用来测试JNI的调用
     *
     * @return
     */
    public static int getWebpVersion() {
        return libwebp.WebPGetDecoderVersion();
    }


    /**
     * 编码webp
     *
     * @param bytes
     * @param width
     * @param height
     * @param stride
     * @param q
     */
    public static byte[] getWebpByte(byte[] bytes, int width, int height, int stride, int q) {
        byte[] result = null;
        try {
            result = libwebp.WebPEncodeBGRA(bytes, width, height, stride, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean saveWebpMuxByByes(byte[][] webpBytes, int time, int repeat, String filePath) {
        if (webpBytes == null || webpBytes.length == 0) {
            return false;
        }
        //第一个参数是时间，第二个参数是重复播放次数
        byte[] bytes1 = new WebpUtils().addImage(time, repeat, webpBytes);
        FileOutputStream fileOutputStream = null;
        boolean isOk = false;
        try {
            fileOutputStream = new FileOutputStream(new File(filePath));
            fileOutputStream.write(bytes1, 0, bytes1.length);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            isOk = false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                    isOk = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isOk;
    }

}
