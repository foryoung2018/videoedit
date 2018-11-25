package com.wmlive.hhvideo.utils.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.log.MaterialDownLoad;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.util.EventHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wenlu on 2017/9/20.
 */

public class FileDownload extends Service {

    private static final String TAG = FileDownload.class.getSimpleName();
    //下载指令
    public static final int DOWNLOAD_SERVER_START = 100;
    public static final int DOWNLOAD_SERVER_STOP = 110;

    //下载状态
    public static final int RESULT_PREPARE = 200;//预备
    public static final int RESULT_START = 210;
    public static final int RESULT_PAUSE = 220;
    public static final int RESULT_COMPLETE = 230;
    public static final int RESULT_DOWNLOADING = 240;
    public static final int RESULT_ERROR = 250; //错误
    public static final int RESULT_COMPLETE_ALL = 260;

    private volatile boolean isDownload = false;
    private static int mDownLoadCount;
    private int autoRetryTimes = 2;//默认的重试次数
    private ResultReceiver resultReceiver;
    public static Map<Integer, BaseDownloadTask> taskArray = new ArrayMap<Integer, BaseDownloadTask>();

    public static void start(Context context, DownloadBean downloadBean, @NonNull ResultReceiver receiver) {
        start(context, downloadBean, receiver, false);
    }

    public static void start(Context context, DownloadBean downloadBean, @NonNull ResultReceiver receiver, boolean reDownload) {
        start(context, downloadBean, receiver, reDownload, false);
    }

    /**
     * 单个下载
     *
     * @param context
     * @param downloadBean
     * @param receiver
     * @param reDownload
     * @param canProgress
     */
    public static void start(Context context, DownloadBean downloadBean, @NonNull ResultReceiver receiver, boolean reDownload, boolean canProgress) {
        Intent intent = new Intent(context, FileDownload.class);
        intent.putExtra("downloadBean", downloadBean);
        intent.putExtra("reDownload", reDownload);
        intent.putExtra("canProgress", canProgress);
        intent.putExtra("command", DOWNLOAD_SERVER_START);
        intent.putExtra("receiver", receiver);
        context.startService(intent);
    }

    public static void start(Context context, ArrayList<DownloadBean> downloadList, @NonNull ResultReceiver receiver) {
        start(context, downloadList, receiver, false);
    }

    /**
     * 批量下载
     *
     * @param context
     * @param downloadList
     * @param receiver
     * @param canProgress
     */
    public static void start(Context context, ArrayList<DownloadBean> downloadList, @NonNull ResultReceiver receiver, boolean canProgress) {
        Intent intent = new Intent(context, FileDownload.class);
        intent.putParcelableArrayListExtra("downloadList", downloadList);
        intent.putExtra("command", DOWNLOAD_SERVER_START);
        intent.putExtra("canProgress", canProgress);
        intent.putExtra("receiver", receiver);
        context.startService(intent);
    }

    /**
     * 暂停下载
     *
     * @param context
     */
    public static void pause(Context context) {
        Intent intent = new Intent(context, FileDownload.class);
        intent.putExtra("command", DOWNLOAD_SERVER_STOP);
        context.startService(intent);
    }

    public static void pause(int key, int id) {
        DLog.i("暂停下载 MyFileDownloadListener id :" + id);
        if (taskArray.containsKey(key)) {
            FileDownloader.getImpl().pause(id);
            taskArray.remove(key);
            mDownLoadCount--;
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        DLog.i("--------onStartCommand:" + Thread.currentThread().getName());
        if (intent != null) {
            int cmd = intent.getIntExtra("command", DOWNLOAD_SERVER_START);
            DLog.i("command:" + cmd);
            switch (cmd) {
                case DOWNLOAD_SERVER_START:
                    resultReceiver = intent.getParcelableExtra("receiver");
                    ArrayList<DownloadBean> downloadList = intent.getParcelableArrayListExtra("downloadList");
                    DownloadBean downloadBean = intent.getParcelableExtra("downloadBean");
                    boolean reDownload = intent.getBooleanExtra("reDownload", false);
                    boolean canProgress = intent.getBooleanExtra("canProgress", false);
                    if (null != downloadBean) {
                        BaseDownloadTask downloadTask = FileDownloader.getImpl()
                                .create(downloadBean.downloadUrl)
                                .setPath(downloadBean.wholePathName, false)
                                .setListener(new MyFileDownloadListener(resultReceiver))
                                .setAutoRetryTimes(autoRetryTimes)
                                .setForceReDownload(reDownload);

                        downloadBean.realDownloadId = downloadTask.getId();

                        downloadTask.setTag(downloadBean);
                        if (!canProgress) {
                            downloadTask.setCallbackProgressIgnored();
                        }
                        taskArray.put(downloadBean.downloadId, downloadTask);
                        downloadTask.start();
                        return super.onStartCommand(intent, flags, startId);
                    } else if (null != downloadList && downloadList.size() > 0) {
                        isDownload = true;
                        MyFileDownloadListener myFileDownloadListener = new MyFileDownloadListener(resultReceiver);
                        FileDownloadQueueSet queueSet = new FileDownloadQueueSet(myFileDownloadListener);
                        List<BaseDownloadTask> tasks = new ArrayList<>();
                        int count = downloadList.size();
                        mDownLoadCount = count;
                        KLog.e(TAG, "---FileDownload------------download TotalCount:" + mDownLoadCount);
                        for (int i = 0; i < count; i++) {
                            DownloadBean bean = downloadList.get(i);
                            KLog.e(TAG, "---FileDownload------------downloadUrl:" + bean.downloadUrl + "<>wholePathName:" + bean.wholePathName + "<>downloadId:" + bean.downloadId);
                            BaseDownloadTask downloadTask = FileDownloader.getImpl().create(bean.downloadUrl)
                                    .setPath(bean.wholePathName, false).setForceReDownload(false);

                            bean.realDownloadId = downloadTask.getId();

                            downloadTask.setTag(bean);
                            taskArray.put(bean.downloadId, downloadTask);
                            tasks.add(downloadTask);
                        }
                        if (!canProgress) {
                            //// 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
                            queueSet.disableCallbackProgressTimes();
                        }
                        // 所有任务在下载失败的时候都自动重试一次
                        queueSet.setAutoRetryTimes(autoRetryTimes);
//                    queueSet.downloadSequentially(tasks); //串行下载
                        queueSet.downloadTogether(tasks); // 并行下载
                        queueSet.start();
                    }
                    break;
                case DOWNLOAD_SERVER_STOP:
                    DLog.i("暂停下载");
                    stopSelf();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    //==========================文件下载回调接口=================================
    class MyFileDownloadListener extends FileDownloadListener {

        ResultReceiver receiver;
        private long iDownLoadFinishCount;

        public MyFileDownloadListener(ResultReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            //等待，已经进入下载队列
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            KLog.e(TAG, "---MyFileDownloadListener--pending--------downloadId " + downloadBean.downloadId);
            Bundle result = new Bundle();
            result.putLong("currentSize", soFarBytes);
            float percent = (soFarBytes * 100.0f / (totalBytes > 0 ? totalBytes : 1));
            percent = (float) (Math.round(percent * 10)) / 10;
            result.putFloat("percent", percent);
            sendResult(RESULT_PREPARE, "进入下载队列", result, receiver, downloadBean);
        }

        @Override
        protected boolean isInvalid() {
            return super.isInvalid();
        }

        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            //结束了pending，并且开始当前任务的Runnable
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            KLog.e(TAG, "---MyFileDownloadListener--started--------downloadId " + downloadBean.downloadId);
            Bundle result = new Bundle();
            sendResult(RESULT_START, "下载下载开始", result, receiver, downloadBean);
            onDownLoadStart();
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            //已经连接上
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            KLog.e(TAG, "---MyFileDownloadListener--connected--------downloadId " + downloadBean.downloadId);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) throws Throwable {
            super.blockComplete(task);
            //在完成前同步调用该方法，此时已经下载完成
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            KLog.e(TAG, "---MyFileDownloadListener--blockComplete--------downloadId " + downloadBean.downloadId);
//            Bundle result = new Bundle();
//            sendResult(RESULT_COMPLETE, "进入下载队列", result, receiver, downloadBean);
        }

        @Override
        protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
            super.retry(task, ex, retryingTimes, soFarBytes);
            //重试之前把将要重试是第几次回调回来
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            KLog.e(TAG, "---MyFileDownloadListener--retry--------downloadId " + downloadBean.downloadId);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            //下载进度回调
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            KLog.e(TAG, "---MyFileDownloadListener--progress--------downloadId " + downloadBean.downloadId + " taskId " + task.getId());
            Bundle result = new Bundle();
            result.putLong("currentSize", soFarBytes);
            float percent = (soFarBytes * 100.0f / (totalBytes > 0 ? totalBytes : 1));
            percent = (float) (Math.round(percent * 10)) / 10;
            result.putFloat("percent", percent);
            sendResult(RESULT_DOWNLOADING, "进入下载队列", result, receiver, downloadBean);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            //完成整个下载过程
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            //素材下载情况日志上传
            if(!TextUtils.isEmpty(downloadBean.wholePathName)&&downloadBean.wholePathName.endsWith("mp4"))
            onDownLoadSuccess(HttpConstant.SUCCESS,downloadBean);
            ++iDownLoadFinishCount;
            KLog.e(TAG, "---MyFileDownloadListener--completed--------downloadId " + downloadBean.downloadId + " wholePathName " + downloadBean.wholePathName + " finishCount:" + iDownLoadFinishCount);
            Bundle result = new Bundle();
            sendResult(RESULT_COMPLETE, "下载完成", result, receiver, downloadBean);
            if (iDownLoadFinishCount >= mDownLoadCount) {
                sendResult(RESULT_COMPLETE_ALL, "所有下载完成", result, receiver, downloadBean);
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            //暂停下载
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            KLog.e(TAG, "---MyFileDownloadListener--paused--------downloadId " + downloadBean.downloadId);
            Bundle result = new Bundle();
            result.putLong("currentSize", soFarBytes);
            float percent = (soFarBytes * 100.0f / (totalBytes > 0 ? totalBytes : 1));
            percent = (float) (Math.round(percent * 10)) / 10;
            result.putFloat("percent", percent);
            sendResult(RESULT_PAUSE, "下载暂停", result, receiver, downloadBean);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            //下载错误
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            //素材下载情况日志上传
           if(!TextUtils.isEmpty(downloadBean.wholePathName)&&downloadBean.wholePathName.endsWith("mp4"))
            onDownLoadSuccess(HttpConstant.Fail,downloadBean);
            KLog.e(TAG, "---MyFileDownloadListener--error--------downloadId " + downloadBean.downloadId);
            ++iDownLoadFinishCount;
            Bundle result = new Bundle();
            result.putString("error", e.getMessage());
            sendResult(RESULT_ERROR, "下载出错", result, receiver, downloadBean);
            if (iDownLoadFinishCount >= mDownLoadCount) {
                sendResult(RESULT_COMPLETE_ALL, "所有下载完成", result, receiver, downloadBean);
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            //在下载队列中(正在等待/正在下载)已经存在相同下载连接与相同存储路径的任务
            DownloadBean downloadBean = (DownloadBean) task.getTag();
            KLog.e(TAG, "---MyFileDownloadListener--warn--------downloadId " + downloadBean.downloadId);
        }
    }

    private void sendResult(int status, String message, Bundle result, ResultReceiver receiver, DownloadBean downloadBean) {
        if (receiver != null && result != null) {
            result.putString("message", message);
            if (downloadBean != null) {
                result.putInt("downloadId", downloadBean.downloadId);
                result.putInt("index", downloadBean.index);
            }
            result.putParcelable("downloadBean", downloadBean);
            receiver.send(status, result);
        }
    }

    long downloadStartTime;
    /**
     *  开始下载
     */
    private void onDownLoadStart(){
        downloadStartTime = System.currentTimeMillis();
    }


    /**
     * 下载成功
     */
    private void onDownLoadSuccess(String code,DownloadBean downloadBean){
        if(downloadBean==null)
            return;
        double duration = (System.currentTimeMillis()-downloadStartTime);
        File f = new File(downloadBean.wholePathName);
        if(!f.exists()){
            return;
        }
        double fileSize = f.length()/1024.0;//byte -》k
        double speed = fileSize/duration*1000.0;//下载速度

        MaterialDownLoad materialDownLoad = new MaterialDownLoad();
        materialDownLoad.setUrl(downloadBean.downloadUrl);
        materialDownLoad.setDownload_len(fileSize+"");//Kb 确定单位
        materialDownLoad.setDownload_duration(duration/1000.0+"");
        materialDownLoad.setDownload_speed(speed+"");//Kb/s 确定单位
        materialDownLoad.setFile_len(fileSize+"");//目标大小
        materialDownLoad.setMaterial_id(downloadBean.downloadId+"");//? 有问题
        materialDownLoad.setRes(code);//success fail cancel

        EventHelper.post(GlobalParams.EventType.TYPE_CREATE_DOWNLOAD, materialDownLoad);
    }

    @Override
    public void onDestroy() {
        isDownload = false;
        try {
            FileDownloader.getImpl().pauseAll();
            FileDownloader.getImpl().clearAllTaskData();
        } catch (Exception e) {

        }
        resultReceiver = null;
        DLog.i("----stopSelf");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
