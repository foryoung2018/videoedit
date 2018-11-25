package com.wmlive.hhvideo.heihei.record.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.record.model.PublishModel;
import com.wmlive.hhvideo.heihei.record.model.PublishMvModel;
import com.wmlive.hhvideo.utils.KLog;

public class PublishMvBGService extends Service {
    protected static final String CHANNEL_NAME = "wlhhvideo";
    protected static int notificateId = 101;

    PublishMvModel publishModel = null;

    public PublishMvBGService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        regist();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregist();
        publishModel = null;
        GlobalParams.StaticVariable.ispublishing = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.i("onStartCommand");
        if (intent != null) {
            boolean ifsave = intent.getBooleanExtra("ifsave", false);
            if (publishModel == null) {
                publishModel = new PublishMvModel();
                publishModel.publish(ifsave);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                    try {
                        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        if (nm != null) {
                            NotificationChannel channel = new NotificationChannel(String.valueOf(notificateId),
                                    CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
                            nm.createNotificationChannel(channel);
                            startForeground(notificateId, new Notification.Builder(this, channel.getId()).build());
                        }
                        KLog.e(nm + "=======startForeground notificateId:" + notificateId);
                        notificateId += 10;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 网络已经连上
     */
    public void netConnect() {
        if (publishModel != null)
            publishModel.onNetConnect();
    }

    /**
     * w
     */
    public void netDisconnected() {
        if (publishModel != null)
            publishModel.onNetDisconnect();
    }

    private void regist() {
        IntentFilter intentFilter = new IntentFilter(); // 2. 设置接收广播的类型
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // 3. 动态注册：调用Context的registerReceiver（）方法
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); // 3. 动态注册：调用Context的registerReceiver（）方法
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    private void unregist() {
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        public String CONNECT = "CONNECT_RECEIVER";

        private String getConnectionType(int type) {
            String connType = "";
            if (type == ConnectivityManager.TYPE_MOBILE) {
                connType = "3G网络数据";
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                connType = "WIFI网络";
            }
            return connType;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e(CONNECT, "wifiState:" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        break;
                }
            }
            // 监听wifi的连接状态即是否连上了一个有效无线路由
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    // 获取联网状态的NetWorkInfo对象
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    //获取的State对象则代表着连接成功与否等状态
                    NetworkInfo.State state = networkInfo.getState();
                    //判断网络是否已经连接
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;
                    Log.e(CONNECT, "isConnected:" + isConnected);
                    if (isConnected) {

                    } else {

                    }
                }
            }
            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                //获取联网状态的NetworkInfo对象
                NetworkInfo info = intent
                        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI
                                || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.i(CONNECT, getConnectionType(info.getType()) + "连上");
                            netConnect();
                        }
                    } else {
                        netDisconnected();
                        Log.i(CONNECT, getConnectionType(info.getType()) + "断开");
                    }
                }
            }

        }
    };
}
