package com.wmlive.hhvideo.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.common.manager.gift.GiftPresenter;
import com.wmlive.hhvideo.common.manager.message.BaseTask;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.FileZipAndUnZip;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 1/5/2018.3:56 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftService extends DcBaseService implements GiftPresenter.IGiftView {
    private static final String TAG = GiftService.class.getSimpleName();
    private GiftPresenter giftPresenter;

    private static final String KEY_COMMAND = "key_cmd";
    private static final String KEY_GIFT_ID = "key_gift_id";
    private static final String KEY_GIFT_LIST = "key_gift_list";

    public static final short CMD_NONE = 0;
    public static final short CMD_START_SERVICE = 10;
    public static final short CMD_STOP_SERVICE = 20;
    public static final short CMD_GET_GIFT_LIST = 30;
    public static final short CMD_CHECK_GIFT_LIST = 40;
    private volatile boolean isWorking = false;
    private List<BaseDownloadTask> downloadTaskList;
    private volatile int completeCount = 0;

    public static void sendCommand(short cmd) {
        sendCommand(cmd, null);
    }

    public static void sendCommand(short cmd, ArrayList<GiftEntity> list) {
        Intent intent = new Intent(DCApplication.getDCApp(), GiftService.class);
        Bundle bundle = new Bundle();
        bundle.putShort(KEY_COMMAND, cmd);
        bundle.putLong(KEY_GIFT_ID, 0);
        if (!CollectionUtil.isEmpty(list)) {
            bundle.putParcelableArrayList(KEY_GIFT_LIST, list);
        }
        intent.putExtras(bundle);
        if (cmd == CMD_STOP_SERVICE) {
            DCApplication.getDCApp().stopService(intent);
        } else {
            DCApplication.getDCApp().startService(intent);
        }
    }

    public static void checkLocalGift(ArrayList<GiftEntity> list) {
        sendCommand(CMD_CHECK_GIFT_LIST, list);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.i("====onCreate，礼物Service已启动");
        isWorking = true;
        getGiftPresenter().getAllGiftResource();
    }

    private GiftPresenter getGiftPresenter() {
        if (giftPresenter == null) {
            giftPresenter = new GiftPresenter(this);
        }
        return giftPresenter;
    }


    @Override
    public void onCustomCommand(Intent intent, int flags, int startId) {
        short cmd = CMD_NONE;
        if (intent != null) {
            cmd = intent.getShortExtra(KEY_COMMAND, CMD_NONE);
        }
        switch (cmd) {
            case CMD_GET_GIFT_LIST:
                firstGetGiftList(intent);
                break;
            case CMD_CHECK_GIFT_LIST:
                KLog.i("======检查礼物是否已下载");
                onGiftListOk(intent.getParcelableArrayListExtra(KEY_GIFT_LIST), false, 0);
                break;
            case CMD_STOP_SERVICE:
                KLog.i("===收到指令：停止礼物Service");
                stopSelf();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPingCommand(Intent intent, int flags, int startId) {
        if (!isAlive) {
            firstGetGiftList(intent);
        }
    }

    private void firstGetGiftList(Intent intent) {
        KLog.i("===收到指令：拉取礼物列表");
        if (isWorking) {
            KLog.i("===正在拉取礼物列表，忽略本次请求");
            return;
        }
        long giftId = intent.getLongExtra(KEY_GIFT_ID, 0L);
        if (AccountUtil.isLogin()) {
            isWorking = true;
            getGiftPresenter().getGiftList(giftId);
        } else {
            KLog.i("===用户未登录，不能拉取礼物列表");
        }
    }


    /**
     * 检查本地文件与服务器md5是否保持一致
     */
    private void checkLocalFile(List<GiftEntity> giftEntities) {
        completeCount = 0;
        KLog.i("=====checkLocalFile:" + giftEntities);
        if (!CollectionUtil.isEmpty(giftEntities)) {
            File oneGiftDir;
            File zipFile;
            String giftDirPath;
            String zipFilePath;
            String md5Str;
            boolean md5Correct;
            List<GiftEntity> needDownloadList = new ArrayList<>(giftEntities.size());
            for (GiftEntity giftEntity : giftEntities) {
                if (giftEntity != null && !TextUtils.isEmpty(giftEntity.attr_file)) {
                    giftDirPath = GiftManager.getRootGiftDirPath(giftEntity.id);
                    oneGiftDir = new File(giftDirPath);
                    if (oneGiftDir.exists() && oneGiftDir.isDirectory()) {
                        KLog.i("=====礼物：" + giftEntity.id + " 本地文件夹存在，检查zip文件是否存在");
                        zipFilePath = GiftManager.getZipFilePath(giftEntity.id);
                        zipFile = new File(zipFilePath);
                        if (zipFile.exists() && zipFile.isFile()) {
                            KLog.i("=====礼物：" + giftEntity.id + " zip文件存在，开始匹配zip文件的Md5值");
                            try {
                                md5Str = BinaryUtil.calculateMd5Str(zipFilePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                                md5Str = null;
                                KLog.i("=====礼物:" + giftEntity.id + " 计算md5值出错：" + e.getMessage());
                            }
                            KLog.i("attr_md5:" + giftEntity.attr_md5 + " ,本地文件的md5:" + md5Str);
                            md5Correct = (!TextUtils.isEmpty(md5Str) && md5Str.equalsIgnoreCase(giftEntity.attr_md5));
                            if (md5Correct) {
                                KLog.i("=====礼物：" + giftEntity.id + " md5值正确,检查是否已解压");
                                if (!FileZipAndUnZip.zipFileMatch(zipFilePath, giftDirPath)) {
                                    KLog.i("=====礼物" + giftEntity.id + " zip文件与解压文件不匹配");
                                    FileUtil.deleteAll(oneGiftDir, false);
                                    unzipFile(giftEntity.id, zipFilePath, giftDirPath);
                                } else {
                                    KLog.i("=====zip文件与解压文件匹配");
                                }
                            } else {
                                KLog.i("=====礼物：" + giftEntity.id + " md5值" + (TextUtils.isEmpty(md5Str) ? "为空" : "与服务器md5不一致") + " ，需要重新下载资源");
                                FileUtil.deleteAll(oneGiftDir, false);
                                needDownloadList.add(giftEntity);
                            }
                        } else {
                            KLog.i("=====礼物：" + giftEntity.id + " zip文件不存在，需要下载");
                            needDownloadList.add(giftEntity);
                        }
                    } else {
                        KLog.i("=====礼物：" + giftEntity.id + " 本地不存在，需要下载");
                        needDownloadList.add(giftEntity);
                    }
                } else {
                    KLog.i("=====礼物为空或者下载地址为空");
                }
            }

            if (!CollectionUtil.isEmpty(needDownloadList)) {
                KLog.i("=====过滤后，需要下载的礼物数量是：" + needDownloadList.size());
                startDownload(needDownloadList);
            } else {
                KLog.i("====过滤后，没有需要下载的礼物");
                isWorking = false;
            }
        } else {
            KLog.i("=====获取到的礼物列表为空，啥都不干");
            isWorking = false;
        }
    }

    private void startDownload(List<GiftEntity> giftEntities) {
        if (!CollectionUtil.isEmpty(giftEntities)) {
            final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);
            downloadTaskList = new ArrayList<>();
            GiftEntity giftEntity;
            String dir;
            for (int i = 0, size = giftEntities.size(); i < size; i++) {
                giftEntity = giftEntities.get(i);
                if (giftEntity != null && !TextUtils.isEmpty(giftEntity.attr_file)) {
                    dir = GiftManager.getRootGiftDirPath(giftEntity.id);
                    boolean createDirOk = FileUtil.createDirectory(dir);
                    KLog.i("=====礼物id:" + giftEntity.id + " 的存储目录是:" + dir + " ,创建文件夹：" + createDirOk);
                    downloadTaskList.add(FileDownloader.getImpl()
                            .create(giftEntity.attr_file)
                            .setPath(GiftManager.getZipFilePath(giftEntity.id))
                            .setTag(String.valueOf(giftEntity.id)));
                } else {
                    KLog.e(giftEntity == null ? "礼物giftEntity为空" : "礼物的下载地址为：" + giftEntity.attr_file);
                }
            }
            KLog.i("=====狂飙吧，下载器!!!");
//                queueSet.disableCallbackProgressTimes();
            queueSet.setAutoRetryTimes(2)
                    .downloadTogether(downloadTaskList)
                    .start();
        } else {
            isWorking = false;
        }

    }

    private FileDownloadListener downloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            KLog.e(TAG, "pending url:" + task.getUrl() + "\n tag" + task.getTag() + " ，soFarBytes: " + soFarBytes + " ,totalBytes:" + totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            KLog.e(TAG, "progress url:" + task.getUrl() + "\ntag" + task.getTag() + " ，soFarBytes: " + soFarBytes + " ,totalBytes:" + totalBytes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            KLog.e(TAG, "completed tag:" + task.getTag() + " ，url:" + task.getUrl());
            TaskManager.get().executeTask(new BaseTask() {
                @Override
                public void run() {
                    KLog.e(TAG, "completed,current thread is:" + Thread.currentThread().getName());
                    unzipFile(String.valueOf(task.getTag()), task.getPath(), GiftManager.getRootGiftDirPath((String) task.getTag()));
                    checkTaskCount();
                }
            });
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            KLog.e(TAG, "paused url:" + task.getUrl() + "\ntag" + task.getTag() + " ，soFarBytes: " + soFarBytes + " ,totalBytes:" + totalBytes);
            isWorking = false;
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            KLog.e(TAG, "error tag:" + task.getTag() + " ，url:" + task.getUrl() + ",error message:" + e.getMessage());
            checkTaskCount();
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            KLog.e(TAG, "warn  tag:" + task.getTag() + " ，url:" + task.getUrl());
        }
    };

    private void checkTaskCount() {
        completeCount++;
        KLog.i("====已完成任务数：" + completeCount + " ,剩余任务数:" + (downloadTaskList.size() - completeCount));
        isWorking = !(completeCount == downloadTaskList.size());
        if (!isWorking) {
            completeCount = 0;
            KLog.i("=====下载任务和解压任务全部完成");
        }
    }

    private void unzipFile(String giftId, String zipFilePath, String outDirPath) {
        boolean unzipOk = FileZipAndUnZip.unZipFile(zipFilePath, outDirPath);
        KLog.i("=====礼物" + giftId + " zip文件重新解压:" + (unzipOk ? "成功" : "失败"));
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        KLog.i("=====onRequestDataError:" + message);
    }

    @Override
    public void onGiftListOk(List<GiftEntity> giftEntities, boolean isInit, long giftId) {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
//                        loadOnlineGift(giftEntities);
                        checkLocalFile(giftEntities);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.i("=========accept:" + throwable.getMessage());
                    }
                });
    }

    private void loadOnlineGift(List<GiftEntity> giftEntities) {
        if (!CollectionUtil.isEmpty(giftEntities)) {
            List<GiftEntity> onlineGift = new ArrayList<>(6);
            for (GiftEntity giftEntity : giftEntities) {
                if (giftEntity != null && giftEntity.gift_status == 0) {
                    onlineGift.add(giftEntity);
                }
            }
            GiftManager.get().setGiftList(onlineGift);
        } else {
            GiftManager.get().setGiftList(new ArrayList<>(1));
        }
    }

    @Override
    public void onGiftListFail(String message) {
        KLog.i("=====onGiftListFail:" + message);
        isWorking = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (giftPresenter != null) {
            giftPresenter.destroy();
        }
        if (downloadListener != null) {
            FileDownloader.getImpl().pause(downloadListener);
            downloadListener = null;
        }
        KLog.i("=====onDestroy,礼物Service已停止");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
