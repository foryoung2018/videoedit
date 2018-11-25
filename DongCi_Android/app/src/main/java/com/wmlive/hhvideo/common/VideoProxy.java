package com.wmlive.hhvideo.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.VideoView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.ProxyCacheException;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class VideoProxy implements CacheListener {

    private File cacheDir;//缓存目录
    private HttpProxyCacheServer proxy;//视频本地代理
    private long maxSize;  //缓存文件夹的最大容量，单位：bit
    private String cacheUrl; //当前正在缓存的文件
    private HttpURLConnection cacheConnection;
    private static VideoProxy videoProxy = new VideoProxy();
    private VideoView videoView;

    private VideoProxy() {
        init(DCApplication.getDCApp(), AppCacheFileUtils.getAppVideoCachePathFile(DCApplication.getDCApp()),
                GlobalParams.Config.APP_FILE_CACHE_MXI_SIZE, GlobalParams.Config.IS_DEBUG);
    }

    public static VideoProxy get() {
        return videoProxy;
    }

    /**
     * 初始化缓存代理
     *
     * @param context
     * @param cacheDir 缓存目录
     * @param maxSize  缓存大小，单位：bit
     * @param isDebug  是否是调试模式
     */
    public void init(Context context, File cacheDir, long maxSize, boolean isDebug) {
        this.cacheDir = cacheDir;
        this.maxSize = maxSize;
        getProxy(context.getApplicationContext(), cacheDir);
    }

    /**
     * 缓存一个文件，注意：必须在非主线程中调用
     *
     * @param fileUrl
     */
    public void cacheFile(String fileUrl) {
        if (proxy != null) {
            getProxy().registerCacheListener(this, fileUrl);
            Log.d("缓存", "cacheFile: fileUrl==" + fileUrl);
            cacheUrl = fileUrl;
            try {
                URL url = new URL(getProxy().getProxyUrl(fileUrl));
                cacheConnection = (HttpURLConnection) url.openConnection();
                cacheConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
                if (cacheConnection != null) {
                    KLog.i("=======请求失败=缓存连接被断开");
                    cacheConnection.disconnect();
                    cacheConnection = null;
                }
            } finally {

            }
        }
    }

    /**
     * 停止当前正在缓存文件
     */
    public void stopCacheFile() {
        try {
            if (cacheUrl != null)
                getProxy().shutdownClient(cacheUrl);
        } catch (ProxyCacheException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取代理
     *
     * @return
     */
    public HttpProxyCacheServer getProxy() {
        return proxy;
    }

    /**
     * 获取代理，如果为空，尝试创建新的代理
     *
     * @param context
     * @return
     */
    public HttpProxyCacheServer getProxy(Context context) {
        return getProxy(context, cacheDir);
    }

    /**
     * 获取代理，如果为空，尝试创建新的代理，可设置新的缓存文件夹
     *
     * @param context
     * @param cache   缓存文件夹，如果为空，则使用App的外置存储cache目录
     * @return
     */
    public HttpProxyCacheServer getProxy(Context context, File cache) {
        if (cache == null || !cache.exists()) {//如果缓存文件为空，则返回默认的proxy
            return newProxy(context.getApplicationContext(), cache);
        }
        if (cacheDir != null) {
            if (!cacheDir.getAbsolutePath().equals(cache.getAbsolutePath())) {//与旧的缓存位置不一致
                if (null != proxy) {
                    proxy.shutdown();
                }
            } else {
                if (null == proxy) {
                    return newProxy(context.getApplicationContext(), cache);
                }
                return proxy;
            }
        }
        return newProxy(context.getApplicationContext(), cache);
    }

    /**
     * 创建视频缓存
     * 注意：如果没有读写手机存储区的权限，默认使用外置存储区的app cache目录
     *
     * @param context
     * @param cacheDir
     * @return
     */
    private HttpProxyCacheServer newProxy(Context context, File cacheDir) {
        if (cacheDir == null) {
            //如果cacheDir为空，默认使用app的缓存目录 /Android/data/包名/cache目录
            cacheDir = StorageUtils.getIndividualCacheDirectory(context);
        }
        KLog.i("======newProxy");
        HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(context);
        if (!cacheDir.exists()) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //6.0以上需要运行时权限,如果没有读写手机存储区的权限，默认使用外置存储区的app cache目录
                cacheDir = StorageUtils.getIndividualCacheDirectory(context);
            } else {
                cacheDir.mkdirs();
            }
        }
        builder.cacheDirectory(cacheDir);
        if (maxSize > 0) {  //如果文件夹大小限制不设置，则默认是500M
            builder.maxCacheSize(maxSize);
        }
        this.cacheDir = cacheDir;
        proxy = builder.build();
        return proxy;
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        Log.d("缓存1111", "onCacheAvailable: cacheFile.length()==" + cacheFile.length());
        Log.d("缓存1111", "onCacheAvailable: url==" + url + "  percentsAvailable==" + percentsAvailable);
        if (cacheUrl.equals(url)) {
            if (cacheFile.length() > 400 * 1024) {
                stopCacheFile();
            }
        }
//            if (percentsAvailable >= 20) {//大于800k后停止缓存
//                KLog.i("=======缓存停止");
//                VideoProxy.get().stopCacheFile();
//            }
    }
}
