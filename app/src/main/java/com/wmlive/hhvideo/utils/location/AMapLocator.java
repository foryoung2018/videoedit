package com.wmlive.hhvideo.utils.location;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.wmlive.hhvideo.utils.KLog;

/**
 * Created by lsq on 10/23/2017.
 */

public class AMapLocator {
    private static final String TAG = AMapLocator.class.getSimpleName();
    private AMapLocationClient locationClient;
    private AMapLocateCallback locateCallback;
    private Context context;
    private final int MAX_REPEAT_COUNT = 6;
    private int repeatCount = MAX_REPEAT_COUNT;
    private boolean isOnceLocation = true;  //是否单次定位

    public AMapLocator(Context context) {
        this.context = context;
    }

    /**
     * 设置定位监听回调
     *
     * @param callback
     * @return
     */
    public AMapLocator setLocateCallback(AMapLocateCallback callback) {
        locateCallback = callback;
        return this;
    }

    /**
     * 开始定位
     */
    public AMapLocator startLocate() {
        stopLocate();
        repeatCount = MAX_REPEAT_COUNT;
        locationClient = new AMapLocationClient(context.getApplicationContext());
        locationClient.setLocationOption(getDefaultOption());
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();
        KLog.i(TAG, "----开始定位");
        return this;
    }

    /*
    停止定位,一定记得在页面关闭的时候调用这个方法
     */
    public synchronized void stopLocate() {
        if (locationClient != null) {
            long start = System.currentTimeMillis();
            KLog.i(TAG, "----开始stopLocation");
            locationClient.stopLocation();//这个过程耗时好几秒
            KLog.i(TAG, "----结束stopLocation，耗时：" + (System.currentTimeMillis() - start));
            destroyLocation();
        }
    }

    private void destroyLocation() {
        if (null != locationClient) {
            if (locationListener != null) {
                locationClient.unRegisterLocationListener(locationListener);
            }
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    /**
     * 定位监听
     */
    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(com.amap.api.location.AMapLocation aMapLocation) {
            if (null != aMapLocation && aMapLocation.getErrorCode() == 0) {
                if (locateCallback != null) {
                    LocationEntity entity = new LocationEntity();
                    entity.province = aMapLocation.getProvince();
                    entity.city = aMapLocation.getCity();
                    entity.cityCode = aMapLocation.getCityCode();
                    entity.adCode = aMapLocation.getAdCode();//区编码
                    entity.district = aMapLocation.getDistrict();//区的名称
                    entity.address = aMapLocation.getAddress();
                    entity.longitude = aMapLocation.getLongitude();
                    entity.latitude = aMapLocation.getLatitude();
                    locateCallback.onLocateOk(entity);
                    KLog.i(TAG, "-----定位成功entity:" + entity.toString());
                }
                if (isOnceLocation) {
                    stopLocate();
                }
            } else {
                KLog.i(TAG, "-----定位失败");
                if (isOnceLocation) {
                    if (repeatCount > 0) {
                        repeatCount--;
                        KLog.i(TAG, "-----定位失败，还剩" + repeatCount + "次尝试机会");
                        return;
                    }
                    stopLocate();
                }
                if (locateCallback != null) {
                    locateCallback.onLocateFailed();
                }
            }
        }
    };

    public AMapLocator setOnceLocation(boolean onceLocation) {
        isOnceLocation = onceLocation;
        return this;
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(10000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
//        mOption.setOnceLocation(isOnceLocation);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        return mOption;
    }
}
