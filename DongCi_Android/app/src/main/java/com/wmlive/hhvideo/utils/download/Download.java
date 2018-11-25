package com.wmlive.hhvideo.utils.download;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

/**
 * Created by lsq on 6/5/2017.
 * 下载服务
 * 支持url检查，修改时间检查，断点下载，暂不支持服务器md5校验
 */

public class Download extends IntentService {

    //    public static final String testUrl = "http://www.oschina.net/uploads/osc-android-v2.6.7-oschina-release.apk";
    public static final String testUrl = "http://dldir1.qq.com/weixin/android/weixin6330android920.apk";

    //下载指令
    public static final int CMD_START = 100;
    public static final int CMD_PAUSE = 110;
    public static final int CMD_NOTIFY = 120;
    public static final int CMD_NONE = 130;

    //下载状态
    public static final int RESULT_PREPARE = 200;//预备
    public static final int RESULT_START = 210;
    public static final int RESULT_PAUSE = 220;
    public static final int RESULT_COMPLETE = 230;
    public static final int RESULT_DOWNLOADING = 240;
    public static final int RESULT_ERROR = 250; //错误

    private volatile boolean isDownload = false;
    private ResultReceiver resultReceiver;

    public Download() {
        super("DownloadService");
    }

    public Download(String name) {
        super(name);
    }

    /**
     * 这个方法用作预加载视频用
     */
    public static void downloadPreloadVideo(Context context, String downloadUrl, String savePath, String saveName, String saveSuffix, long limitRange) {
        start(context, downloadUrl, savePath, saveName, saveSuffix, limitRange, 0, null);
    }

    /**
     * 开始下载
     *
     * @param context
     * @param downloadUrl
     * @param savePath
     * @param saveName
     * @param saveSuffix  文件的后缀名，不需要在前面加.,如果给空值，则尝试使用请求到的contentType类型
     * @param receiver
     */
    public static void start(Context context, String downloadUrl, String savePath, String saveName, String saveSuffix, @NonNull ResultReceiver receiver) {
        start(context, downloadUrl, savePath, saveName, saveSuffix, 0, 0, receiver);
    }

    /**
     * @param context
     * @param downloadUrl
     * @param savePath
     * @param saveName
     * @param saveSuffix
     * @param downloadId  下载ID
     * @param receiver
     */
    public static void start(Context context, String downloadUrl, String savePath, String saveName, String saveSuffix, int downloadId, @NonNull ResultReceiver receiver) {
        start(context, downloadUrl, savePath, saveName, saveSuffix, 0, downloadId, receiver);
    }

    /**
     * 开始下载
     *
     * @param context
     * @param downloadUrl
     * @param savePath
     * @param saveName
     * @param saveSuffix  文件的后缀名，不需要在前面加.,如果给空值，则尝试使用请求到的contentType类型
     * @param limitRange  限制下载大小
     * @param receiver
     */
    public static void start(Context context, String downloadUrl, String savePath, String saveName, String saveSuffix, long limitRange, int downloadId, @NonNull ResultReceiver receiver) {
        Intent intent = new Intent(context, Download.class);
        intent.putExtra("downloadUrl", downloadUrl);
        intent.putExtra("command", CMD_START);
        intent.putExtra("savePath", !TextUtils.isEmpty(savePath) ? savePath : context.getCacheDir().getAbsolutePath());
        intent.putExtra("limitRange", limitRange);
        intent.putExtra("downloadId", downloadId);
        intent.putExtra("saveName", saveName);
        intent.putExtra("saveSuffix", saveSuffix);
        intent.putExtra("receiver", receiver);
        context.startService(intent);
    }

    /**
     * 暂停下载
     *
     * @param context
     */
    public static void pause(Context context) {
        Intent intent = new Intent(context, Download.class);
        intent.putExtra("command", CMD_PAUSE);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        DLog.i("--------onStartCommand:" + Thread.currentThread().getName());
        //以下在主线程
        if (intent != null) {
            int cmd = intent.getIntExtra("command", CMD_NONE);
            DLog.i("command:" + cmd);
            switch (cmd) {
                case CMD_PAUSE:
                    DLog.i("暂停下载");
                    if (!isDownload) {
                        stopSelf();
                    } else {
                        isDownload = false;
                    }
                    break;
                case CMD_NOTIFY:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //以下在非主线程
        DLog.i("--------onHandleIntent:" + Thread.currentThread().getName());
        if (intent != null) {
            int cmd = intent.getIntExtra("command", CMD_NONE);
            switch (cmd) {
                case CMD_START:
                    resultReceiver = intent.getParcelableExtra("receiver");
                    String downloadUrl = intent.getStringExtra("downloadUrl");
                    String savePath = intent.getStringExtra("savePath");
                    String saveName = intent.getStringExtra("saveName");
                    String saveSuffix = intent.getStringExtra("saveSuffix");
                    long limitRange = intent.getLongExtra("limitRange", 0L);
                    int downloadId = intent.getIntExtra("downloadId", 0);
                    if (DownloadUtil.isUrl(downloadUrl)) {
                        if (TextUtils.isEmpty(saveName)) {
                            saveName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length());
                            if (TextUtils.isEmpty(saveSuffix)) {
                                if (saveName.contains(".")) {//如果含有扩展名
                                    saveSuffix = saveName.substring(saveName.lastIndexOf(".") + 1, saveName.length());
                                    saveName = saveName.substring(0, saveName.lastIndexOf("."));
                                }
                            }
//                            if (TextUtils.isEmpty(saveName)) {
//                                saveName = String.valueOf(System.currentTimeMillis());
//                            }
                            saveName = String.valueOf(System.currentTimeMillis());
                        }
                        download(downloadUrl, savePath, saveName, saveSuffix, limitRange, downloadId, resultReceiver);
                    } else {
                        sendResult(RESULT_ERROR, "错误的url", new Bundle(), resultReceiver, 0, 0, savePath, downloadId);
                    }
                    break;
            }
        }
    }

    private void download(String downloadUrl, String savePath, String saveName, String saveSuffix, long limitRange,
                          int downloadId, ResultReceiver receiver) {
        File saveDir = new File(savePath);
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            boolean isOk = saveDir.mkdirs();
            DLog.i("isOk:" + isOk);
        }

        Bundle result = new Bundle();
        sendResult(RESULT_PREPARE, "准备下载", result, receiver, 0, 0, "", downloadId);
        if (saveDir.exists()) {
            FileInfo fileInfo = DownloadUtil.getRemoteFileInfo(downloadUrl, limitRange);//从远程服务器获取的文件大小和修改时间
            downloadUrl = fileInfo.getDownloadUrl();
            if (TextUtils.isEmpty(downloadUrl) || fileInfo.getFileSize() < 1) {
                sendResult(RESULT_ERROR, "获取远程文件信息失败", result, receiver, 0, fileInfo.getFileSize(), savePath, downloadId);
            } else {
                RandomAccessFile rafTempFile = null;
                FileChannel tempChannel;
                File tempFile;  //记录当前下载的位置和最后修改时间
                File saveFile;  //下载后的文件
                if (TextUtils.isEmpty(saveSuffix)) {
                    saveSuffix = "." + (!TextUtils.isEmpty(fileInfo.getFileSuffix()) ? fileInfo.getFileSuffix() : "download");
                } else {
                    saveSuffix = "." + saveSuffix;
                }
                try {
                    tempFile = new File(savePath + File.separator + (saveName + ".tmp"));
                    if (!tempFile.exists()) {
                        tempFile.createNewFile();
                    }
                    saveFile = new File(savePath + File.separator + saveName + saveSuffix);
                    DLog.i("======下载url:" + downloadUrl + "\n保存文件:" + saveFile.getAbsolutePath());
                    rafTempFile = new RandomAccessFile(tempFile, "rw");
                    tempChannel = rafTempFile.getChannel();
                    MappedByteBuffer tempRecordBuffer = tempChannel.map(READ_WRITE, 0, 16); //临时文件的读写大小为16B（修改时间8B+文件已下载长度8B）
                    if (saveFile.exists()) {
                        long modifyTime = tempRecordBuffer.getLong(0);  //上次修改时间
                        if (modifyTime == 0 || modifyTime == fileInfo.getModifyTime()) {//文件未修改，继续下载
                            if (saveFile.length() == fileInfo.getFileSize() && tempRecordBuffer.getLong(8) == fileInfo.getFileSize()) {
                                DLog.i("文件已下载完成，无需再下载");
                                fileInfo.setSavePath(saveFile.getAbsolutePath());
                                sendResult(RESULT_COMPLETE, "下载完成", result, receiver, fileInfo.getFileSize(), fileInfo.getFileSize(), saveFile.getAbsolutePath(), downloadId);
                            } else {//下载未开始或者下载未完成，准下载
                                execDownload(downloadUrl, fileInfo, result, receiver, saveFile, tempRecordBuffer, downloadId);
                            }
                        } else {
                            DLog.i("文件已修改，需要重新下载");
                            resetFile(saveFile, fileInfo.getModifyTime(), tempRecordBuffer);
                            execDownload(downloadUrl, fileInfo, result, receiver, saveFile, tempRecordBuffer, downloadId);
                        }
                    } else {
                        DLog.i("本地文件不存在，需要从头开始下载");
                        resetFile(saveFile, fileInfo.getModifyTime(), tempRecordBuffer);
                        execDownload(downloadUrl, fileInfo, result, receiver, saveFile, tempRecordBuffer, downloadId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    sendResult(RESULT_ERROR, e.toString(), result, receiver, 0, fileInfo.getFileSize(), savePath, downloadId);
                } finally {
                    close(rafTempFile);
                }
            }
        } else {
            sendResult(RESULT_ERROR, "创建下载文件夹失败，请确保文件夹未被其他程序占用", result, receiver, 0, 0, savePath, downloadId);
        }
    }

    private void execDownload(String downloadUrl, FileInfo fileInfo,
                              Bundle result, ResultReceiver receiver,
                              File saveFile, MappedByteBuffer tempRecordBuffer, int downloadId) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile rafSaveFile = null;
        long currentSize = tempRecordBuffer.getLong(8);//已下载大小
        try {
            if (currentSize == 0) {//刚创建临时文件或者临时文件可能被删了，之前下载的文件需要删除重新下载，以免错误
                resetFile(saveFile, fileInfo.getModifyTime(), tempRecordBuffer);
            }
            rafSaveFile = new RandomAccessFile(saveFile, "rw");
            FileChannel saveChannel = rafSaveFile.getChannel();
            URL url = new URL(downloadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.setRequestMethod("GET");

            if (fileInfo.isSupportRange()) {
                DLog.i("支持断点下载，从断点开始下载");
                connection.setRequestProperty("Range", "bytes=" + currentSize + "-" + fileInfo.getFileSize());
            } else {
                DLog.i("不支持断点下载，从头开始下载");
                resetFile(saveFile, fileInfo.getModifyTime(), tempRecordBuffer);
            }

            DLog.i("正式联网开始下载");
            connection.connect();
            isDownload = true;
            sendResult(RESULT_START, "下载开始", result, receiver, currentSize, fileInfo.getFileSize(), saveFile.getAbsolutePath(), downloadId);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                    || connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                inputStream = connection.getInputStream();
                byte buffer[] = new byte[20480]; //20k缓冲区
                int len;
                MappedByteBuffer saveBuffer;
                long count = 1;
                long start = currentSize;
                while ((len = inputStream.read(buffer)) != -1) {
                    if (!isDownload) {   //暂停下载
                        DLog.i("需要暂停下载");
                        sendResult(RESULT_PAUSE, "下载暂停", result, receiver, currentSize, fileInfo.getFileSize(), saveFile.getAbsolutePath(), downloadId);
                        break;
                    } else {
                        saveBuffer = saveChannel.map(READ_WRITE, currentSize, len);
                        saveBuffer.put(buffer, 0, len);
                        currentSize += len;
                        tempRecordBuffer.putLong(8, currentSize);
                        if (currentSize > ((count << 18) + start)) {//256k
                            sendResult(RESULT_DOWNLOADING, "正在下载", result, receiver, currentSize, fileInfo.getFileSize(), saveFile.getAbsolutePath(), downloadId);
                            count++;
                        }
                        DLog.i("====正在下载：" + currentSize);
                    }
                }
                if (isDownload) {
                    DLog.i("====下载完成");
                    isDownload = false;
                    fileInfo.setSavePath(saveFile.getAbsolutePath());
                    sendResult(RESULT_COMPLETE, "下载完成", result, receiver, currentSize, fileInfo.getFileSize(), saveFile.getAbsolutePath(), downloadId);
                }
            } else {
                DLog.i("下载失败，未知错误");
                isDownload = false;
                sendResult(RESULT_ERROR, "下载出错：未知错误", result, receiver, currentSize, fileInfo.getFileSize(), saveFile.getAbsolutePath(), downloadId);
            }
        } catch (IOException e) {
            e.printStackTrace();
            DLog.i("下载失败，未知错误");
            isDownload = false;
            sendResult(RESULT_ERROR, "下载出错：" + e.getMessage(), result, receiver, currentSize, fileInfo.getFileSize(), saveFile.getAbsolutePath(), downloadId);
        } finally {
            DLog.i("开始关闭流");
            close(inputStream, rafSaveFile);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void resetFile(File saveFile, long lastModifyTime, MappedByteBuffer tempRecordBuffer) throws IOException {
        if (saveFile != null) {
            if (saveFile.exists()) {
                saveFile.delete();
            }
            saveFile.createNewFile();
            tempRecordBuffer.putLong(0, lastModifyTime);
            tempRecordBuffer.putLong(8, 0);
        }
    }

    @SuppressLint("RestrictedApi")
    private void sendResult(int status, String message, Bundle result, ResultReceiver receiver, long currentSize, long fileSize, String savePath, int downloadId) {
        if (receiver != null && result != null) {
            result.putString("message", message);
            result.putLong("currentSize", currentSize);
            result.putInt("percent", (int) (currentSize * 100.0f / (fileSize > 0 ? fileSize : 1)));
            result.putString("savePath", savePath);
            result.putInt("downloadId", downloadId);
            receiver.send(status, result);
        }
    }

    private void close(Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (null != closeable) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        isDownload = false;
        resultReceiver = null;
        stopSelf();
        DLog.i("----stopSelf");
        super.onDestroy();
    }
}
