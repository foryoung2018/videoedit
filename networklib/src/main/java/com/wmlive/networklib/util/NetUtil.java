package com.wmlive.networklib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wmlive.networklib.entity.UrlBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lsq on 5/9/2017.
 */

public class NetUtil {

    public static Pattern sIpv4Pattern = Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");

    /**
     * 获取本地IP
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                Enumeration<InetAddress> enIp = ni.getInetAddresses();
                InetAddress address;
                while (enIp.hasMoreElements()) {
                    address = enIp.nextElement();
                    if (!address.isLoopbackAddress()
                            && (address instanceof Inet4Address)) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0";
    }

    /**
     * 获取本地公网ip
     *
     * @return
     */
    public static void getLocalPublicIp(final String address, final CallBackListener listener) {
        URL url;
        InputStream inStream;
        String line = null;
        try {
            url = new URL(address);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) conn;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                inStream.close();
                String json = EncodeUtil.decodeUnicode(sb.toString());
                NetLog.e("=======ping url strJson:" + json);
                if (!TextUtils.isEmpty(json) && !json.startsWith("{") && json.contains("{") && json.contains("}")) {
                    json = json.substring(json.indexOf("{"), json.indexOf("}") + 1);
                    UrlBean.DataBean dataBean;
                    try {
                        dataBean = JSON.parseObject(json, UrlBean.DataBean.class);
                    } catch (Exception e) {
                        dataBean = new UrlBean.DataBean();
                    }
                    listener.onSucess(dataBean);
                } else {
                    UrlBean urlBean;
                    try {
                        urlBean = JSON.parseObject(json, UrlBean.class);
                    } catch (Exception e) {
                        urlBean = new UrlBean();
                    }
                    listener.onSucess(urlBean.getData());
                }

            } else {
                listener.onFail("请求失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail(e.getMessage());
        }
    }

    /**
     * 获取指定url的ip
     *
     * @param url
     * @return
     */
    public static String ping(String url) {
        NetLog.e("=======ping url:" + url);
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        InputStream inputStream = null;
        String result = "";
        try {
            //[-aAbBdDfhLnOqrRUvV] [-c count] [-i interval] [-I interface]
            //            [-m mark] [-M pmtudisc_option] [-l preload] [-p pattern] [-Q tos]
            //            [-s packetsize] [-S sndbuf] [-t ttl] [-T timestamp_option]
            //            [-w deadline] [-W timeout] [hop1 ...] destination
            Process exec = Runtime.getRuntime().exec("/system/bin/ping -c 4 -w 1000 " + url);
            inputStream = exec.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            result = matchIpv4(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        NetLog.e("=======ping url result:" + result);
        return result;
    }

    public static boolean isAvailableIp(String ip) {
        return !TextUtils.isEmpty(ip) && !"".equals(ip) && !"null".equalsIgnoreCase(ip);
    }

    public static String matchIpv4(String content) {
        Matcher matcher = sIpv4Pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    /**
     * 获取当前网络连接类型
     *
     * @return -1.None  0.MOBILE   1.TYPE_WIFI   7.BLUETOOTH   9.ETHERNET
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.getType();
        }
        return -1;
    }


    /**
     * 是否连接到了网络
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        return getNetworkType(context) > -1;
    }

    /**
     * 是否是wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        return getNetworkType(context) == 1;
    }

    /**
     * 获取当前网络类型，
     *
     * @param context
     * @return 0：wifi    1:移动网络   2::无网络
     */
    public static int getNetworkState(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //获取WIFI连接的信息
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //获取移动数据连接的信息
        NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        int result = 0;
        if ((wifiNetworkInfo == null || !wifiNetworkInfo.isConnected())) {
            if (dataNetworkInfo != null && dataNetworkInfo.isConnected()) {
//                ToastUtil.showToast("当前正在使用流量");
                result = 1;
            } else {
//                ToastUtil.showToast("当前无网络");
                result = 2;
            }
        }
        return result;
    }

    public interface CallBackListener {
        void onSucess(UrlBean.DataBean bean);

        void onFail(String errorMsg);
    }

}
