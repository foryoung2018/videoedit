package com.wmlive.hhvideo.common.manager;

import android.text.TextUtils;

import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.KLog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 日志文件管理
 * Created by kangzhen on 2017/8/9.
 */

public class LogFileManager {
    private static String str_log_file_name = "dc_log_file";//日志文件的前缀
    private boolean isLogFileUpdate = false;//日志是否在上传中(上传的文件禁止写入数据)

    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式


    private static LogFileManager logFileManager;


    //日志的级别
    public static final String V = "Verbose";
    public static final String D = "Debug";
    public static final String I = "Info";
    public static final String W = "Warn";
    public static final String E = "Error";
    public static final String A = "Assert";

    /**
     * 初始对象
     *
     * @return
     */
    public static synchronized LogFileManager getInstance() {
        if (logFileManager == null) {
            synchronized (LogFileManager.class) {
                if (logFileManager == null) {
                    logFileManager = new LogFileManager();
                }
            }
        }
        return logFileManager;
    }

    /**
     * 返回日志文件
     *
     * @return
     */
    public String getLogFileName() {
        return str_log_file_name;
    }

    public boolean isLogFileUpdate() {
        return isLogFileUpdate;
    }

    public void setLogFileUpdate(boolean logFileUpdate) {
        isLogFileUpdate = logFileUpdate;
    }


    /**
     * 保存日子信息到本地文件
     *
     * @param logContent
     */
    public synchronized void saveLogInfo(String logContent) {
        if (isLogFileUpdate) {
            return;
        }
        Observable.just(logContent)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        writeLogtoFile(null, s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 保存日子信息到本地文件
     *
     * @param logContent
     */
    public synchronized void saveLogInfo(final String logType, String logContent) {
        if (isLogFileUpdate) {
            return;
        }
        Observable.just(logContent)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        writeLogtoFile(logType, s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 保存日子信息到本地文件
     *
     * @param obj
     */
    public synchronized void saveLogInfo(String... obj) {
        if (isLogFileUpdate) {
            return;
        }
        Observable.fromArray(obj)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        writeLogtoFile(null, s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 保存日子信息到本地文件
     *
     * @param obj
     */
    public synchronized void saveLogInfo(final String logType, String... obj) {
        if (isLogFileUpdate) {
            return;
        }
        Observable.fromArray(obj)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        writeLogtoFile(logType, s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 获取日志文件格式
     *
     * @param logType    //日志的级别
     * @param logContent //日志内容
     * @return
     */
    private synchronized String getLogInfoFomat(String logType, String logContent) {
        StringBuffer stringBuffer = new StringBuffer();
        if (TextUtils.isEmpty(logType)) {
            logType = I;
        }
        stringBuffer.append("[ logtype : ").append(logType).append(" ] ,");
        stringBuffer.append("[ time : ").append(myLogSdf.format(new Date())).append(" ] ,");
        stringBuffer.append("[ logcontent : ").append(logContent).append(" ] ");
        return stringBuffer.toString();
    }

    /**
     * 删除已上传的日志文件
     */
    public synchronized void delLogFile() {
        try {
            KLog.i("log_update", "=====delLogFile=========start===");
            File file = new File(AppCacheFileUtils.getAppLogPath(), str_log_file_name);
            if (file != null && file.exists()) {
                file.delete();
            }
            KLog.i("log_update", "=====delLogFile=======end===");
            KLog.i("log_update", "=====delLogFile=======createNewFile===start=");
            //创建新的文件
            File fileNewDir = new File(AppCacheFileUtils.getAppLogPath());
            if (fileNewDir != null && !fileNewDir.exists()) {
                fileNewDir.mkdir();
            }
            File fileNewFile = new File(AppCacheFileUtils.getAppLogPath(), str_log_file_name);
            if (fileNewFile != null && !fileNewFile.exists()) {
                fileNewFile.createNewFile();
            }
            KLog.i("log_update", "=====delLogFile=======createNewFile===end=");

        } catch (Exception e) {
            KLog.i("log_update", "=====delLogFile======e:" + e.getMessage());
        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    private synchronized void writeLogtoFile(String logType, String text) {// 新建或打开日志文件
        File file = new File(AppCacheFileUtils.getAppLogPath(), str_log_file_name);
        try {
            if (file != null && !file.exists()) {
                file.createNewFile();
            }
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(getLogInfoFomat(logType, text));
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            KLog.i("log_update", "=====writeLogtoFile=========:" + e.getMessage());
        }
    }
}
