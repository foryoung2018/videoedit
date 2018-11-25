package com.dongci.sun.gpuimglibrary.common;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * Created by yangjiangang on 2018/6/6.
 * 压缩视频转码类
 */

public class SLVideoCompressor1 {
    private static final String TAG = SLVideoCompressor1.class.getSimpleName();

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    public interface onCompressCompleteListener {
        void onComplete(boolean compressed, String outPath);
    }

    public interface OnVideoProgressListener {
        void progress(int progress);
    }

    /**
     * 压缩视频
     *
     * @param inputPath 输入视频源
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void compressVideo(String inputPath, String outputPath, onCompressCompleteListener listener, OnVideoProgressListener mOnVideoProgressListener, ProgressDialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            new VideoCompressorTask(getFileOrFilesSize(inputPath, SIZETYPE_KB), outputPath, listener, mOnVideoProgressListener, dialog).execute(inputPath, outputPath);
        else
            listener.onComplete(true, inputPath);
    }

    /**
     * @param inputPath 输入视频源
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void compressVideo(String inputPath, String outputPath, onCompressCompleteListener listener, OnVideoProgressListener mOnVideoProgressListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            new VideoCompressorTask(getFileOrFilesSize(inputPath, SIZETYPE_KB), outputPath, listener, mOnVideoProgressListener, null).execute(inputPath, outputPath);
        else
            listener.onComplete(true, inputPath);
    }

    private static class VideoCompressorTask extends AsyncTask<String, Integer, Boolean> {
        private ProgressDialog mDialog;
        private String outputPath;
        private onCompressCompleteListener mOnCompleteListener;
        private OnVideoProgressListener mOnVideoProgressListener;
        private double mVideoSize = 0;

        VideoCompressorTask(double videoSize, String outputPath, onCompressCompleteListener listener, OnVideoProgressListener listener2, ProgressDialog dialog) {
            mOnCompleteListener = listener;
            mOnVideoProgressListener = listener2;
            mVideoSize = videoSize;
            mDialog = dialog;
            this.outputPath = outputPath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mDialog != null)
                mDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... path) {
            return SLMediaController.getInstance().convertVideo(path[0], path[1], new SLMediaController.OnProgressUpdateListener() {
                @Override
                public void onHandleVideoSize(double size) {
                    publishProgress((int) ((size / mVideoSize) * 100));
                }
            });
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (mDialog != null) {
                mDialog.setProgress(progress[0]);
            }
            if (mOnVideoProgressListener != null) {
                mOnVideoProgressListener.progress(progress[0]);
            }

        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
            if (mOnCompleteListener != null)
                mOnCompleteListener.onComplete(compressed, outputPath);
        }
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    private static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormtFileSize(blockSize, sizeType);
    }

    /**
     * 获取指定文件大小
     *
     * @param file 文件的路径
     * @return long
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }


    /**
     * 获取指定文件夹
     *
     * @param f 文件路径
     * @return long
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS 需要转换文件大小
     * @return String
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS    需要转换文件大小
     * @param sizeType 需要转换文件大小的类型
     * @return double
     */
    private static double FormtFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }
}
