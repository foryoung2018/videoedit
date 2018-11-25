package com.wmlive.networklib.okhttp;


import android.content.Context;
import android.text.TextUtils;

import com.wmlive.networklib.util.NetLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieManager implements CookieJar {
    private static final String TAG = CookieManager.class.getSimpleName();
    private String webHost = "wmlives.com";

    private Map<String, String> cookieMap;

    public CookieManager(Context context) {
        cookieMap = new HashMap<>(2);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        NetLog.i(TAG, "====saveFromResponse:" + cookies);
        if (cookies.size() > 0) {
            for (Cookie item : cookies) {
                NetLog.i(TAG, "===saveFromResponse==cookie key:"
                        + (item != null ? item.name() : "null")
                        + ", value:" + (item != null ? item.value() : "null")
                        + ", domain:" + (item != null ? item.domain() : "null"));
                if (item != null && !TextUtils.isEmpty(item.domain())
                        && item.domain().contains(webHost)) {
                    cookieMap.put(item.name(), item.toString());
                }
            }
        }
    }

    public void setWebHost(String hostUrl) {
        webHost = hostUrl;
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return new ArrayList<>();
    }

    public void clearCookie() {
        if (cookieMap != null) {
            cookieMap.clear();
        }
    }
}
