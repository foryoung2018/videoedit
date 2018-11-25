package com.wmlive.hhvideo.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.HeaderUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.WeakHandler;
import com.wmlive.networklib.util.EventHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by lsq on 10/18/2017.
 * IMChat和系统Im消息的WebSocketService
 */

public class DcWebSocketService extends DcBaseService implements Handler.Callback {

    private static final String TAG_SOCKET = "tag_websock_service";
    private static final String KEY_COMMAND = "command";
    private static final int CMD_START = 100;
    private static final int CMD_STOP = 200;
    private static final int CMD_RECONNECT = 300;
    private static final int CMD_PARSE_MESSAGE = 400;

    public static final int CODE_NORMAL_CLOSE = 4001;//直接关闭
    public static final int CODE_SERVER_CLOSE = 1000;//服务端关闭
    public static final int CODE_SERVER_DOWN = 1005;//服务端关闭，可能是token失效
    public static final int CODE_TOKEN_CLOSE = 3001;//token失效
    private static final int MAX_RETRY_COUNT = 16;
    private int retryCount = 0;
    private long nextRetryTime = 0;
    private boolean isClosing = false;

    private WebSocket webSocket;
    private WeakHandler weakHandler;
    private HandlerThread handlerThread;

    public static void startSocket(Context context) {
        startSocket(context, 0);
    }

    public static void startSocket(Context context, long delay) {
        Observable.just(1)
                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Intent intent = new Intent(context, DcWebSocketService.class);
                        intent.putExtra(KEY_COMMAND, CMD_START);
                        context.startService(intent);
                        KLog.e("startSocket");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.e("======DcWebSocketService startSocket出错：" + throwable.getMessage());
                    }
                });
    }

    public static void stopSocket(Context context) {
        Intent intent = new Intent(context, DcWebSocketService.class);
        intent.putExtra(KEY_COMMAND, CMD_STOP);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.e("onCreate");
        handlerThread = new HandlerThread("DcWebSocketService");
        handlerThread.start();
        weakHandler = new WeakHandler(handlerThread.getLooper(), this);
    }

    @Override
    public void onCustomCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getIntExtra(KEY_COMMAND, 0)) {
                case CMD_START:
                    firstStartConnect();
                    break;
                case CMD_STOP:
                    removeAllHandlerMessage();
                    stopSocket("user shutdown service");
                    break;
                default:

                    break;
            }
        }
    }

    @Override
    public void onPingCommand(Intent intent, int flags, int startId) {
        if (!isAlive) {
            firstStartConnect();
        }
    }

    private void firstStartConnect() {
        retryCount = 0;
        nextRetryTime = 0;
        removeAllHandlerMessage();
        startConnect("first startSocket socket");
    }

    private void startConnect(String reason) {
        KLog.e(TAG_SOCKET, "==startConnect");
        if (webSocket != null) {
            stopSocket(reason);
            if (weakHandler != null) {
                weakHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startConnect();
                    }
                }, 1000);
            } else {
                startConnect();
            }
        } else {
            startConnect();
        }
    }

    private void startConnect() {
        if (TextUtils.isEmpty(HeaderUtils.getDeviceIdMsg())) {
            reconnectSocket("device id is null,wait for...", 3000);
            return;
        }
        String serverUrl = InitCatchData.getWebSocketServer();
        if (!TextUtils.isEmpty(serverUrl)) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .pingInterval(5, TimeUnit.SECONDS)
                    .build();
            String token = AccountUtil.getToken();
            if (!TextUtils.isEmpty(token)) {
                serverUrl = serverUrl + "?token=" + token;
            }
            KLog.d("======start connect websocket url is:" + serverUrl);
            Request request = new Request.Builder().url(serverUrl)
                    .addHeader("app_name", HeaderUtils.getAppName())
                    .addHeader("app_version", HeaderUtils.getAppVersion())
                    .addHeader("os_version", HeaderUtils.getOsVersion())
                    .addHeader("os_platform", HeaderUtils.getOsPlatform())
                    .addHeader("device_model", HeaderUtils.getDeviceModel())
                    .addHeader("device_id", HeaderUtils.getDeviceIdMsg())
                    .addHeader("device_resolution", HeaderUtils.getDeviceResolution())
                    .addHeader("device_ac", HeaderUtils.getDeviceAc())
                    .addHeader("api_version", HeaderUtils.getApiVersion())
                    .addHeader("build_number", HeaderUtils.getBuildNumber())
                    .addHeader("lat_lon", HeaderUtils.getLocationInfo())
                    .addHeader("channel", HeaderUtils.getChannel())
                    .build();
            client.newWebSocket(request, messageListener);
            client.dispatcher().executorService().shutdown();
        } else {
            KLog.e(TAG_SOCKET, "serverUrl is null");
            stopSelf();
        }
    }

    private void stopSocket(String reason) {
        if (webSocket != null) {
            boolean isOk = webSocket.close(CODE_NORMAL_CLOSE, reason);
            webSocket = null;
            KLog.e(TAG_SOCKET, "close Socket " + (isOk ? "success" : "fail"));
        }
    }

    private void reconnectSocket(String reason, long delayTime) {
        Message msgToken = Message.obtain();
        msgToken.what = CMD_RECONNECT;
        msgToken.obj = reason;
        KLog.e(TAG_SOCKET, "next reconnect time： " + delayTime);
        if (weakHandler != null) {
            weakHandler.sendMessageDelayed(msgToken, delayTime);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        String message = null;
        if (msg.obj instanceof String) {
            message = (String) msg.obj;
        }
        switch (msg.what) {
            case CMD_RECONNECT:
                startConnect(message);
                break;
            case CMD_PARSE_MESSAGE:
                if (!isClosing) {
                    MessageManager.get().parseMessage(message);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private WebSocketListener messageListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket socket, Response response) {
            super.onOpen(socket, response);
            KLog.e(TAG_SOCKET, "on open socket");
            webSocket = socket;
            isClosing = false;
            retryCount = 0;
            nextRetryTime = 0;
            removeAllHandlerMessage();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            KLog.e(TAG_SOCKET, "receive message:\n" + text);
            KLog.json(TAG_SOCKET, text);
            Message message = Message.obtain();
            message.what = CMD_PARSE_MESSAGE;
            message.obj = text;
            if (weakHandler != null) {
                weakHandler.sendMessage(message);
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            isClosing = true;
            KLog.e(TAG_SOCKET, "on closing socket,code:" + code + " ,reason:" + reason);
            switch (code) {
                case CODE_TOKEN_CLOSE:
                    AccountUtil.clearAccount();
                    EventHelper.post(30001, true);
                    KLog.e("=====onClosing=token已失效");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            KLog.e(TAG_SOCKET, "on close socket,code:" + code + " ,reason:" + reason);
            switch (code) {
                case CODE_NORMAL_CLOSE:
                    //手动关闭了Socket.do nothing
                    break;
                case CODE_SERVER_CLOSE:
                    reconnectSocket("server close socket", 3000);
                    break;
                case CODE_TOKEN_CLOSE:
                    AccountUtil.clearAccount();
                    KLog.e("=====onClosed token已失效");
                    break;
                default:
                    // 其他错误，重连
                    reconnectSocket("server close socket", 3000);
                    break;
            }
            isClosing = false;
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            String resp = null;
            try {
                if (response != null && response.body() != null) {
                    resp = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            KLog.e(TAG_SOCKET, "on fail socket：" + t.getMessage() + " ,response:" + resp);
            if (retryCount > MAX_RETRY_COUNT) {
                stopSelf();
            } else {
                retryCount++;
                nextRetryTime = 3000 * retryCount;
                reconnectSocket("on fail socket", nextRetryTime);
            }
            isClosing = false;
        }
    };

    @Override
    public void onDestroy() {
        stopSocket("service shutdown");
        removeAllHandlerMessage();
        if (handlerThread != null) {
            handlerThread.quit();
            handlerThread = null;
        }
        weakHandler = null;
        messageListener = null;
        super.onDestroy();
        KLog.e(TAG_SOCKET, "onDestroy");
    }

    private void removeAllHandlerMessage() {
        if (weakHandler != null) {
            weakHandler.removeCallbacksAndMessages(null);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
