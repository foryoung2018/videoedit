package com.wmlive.hhvideo.utils.location;

/**
 * Created by lsq on 10/23/2017.
 */

public interface AMapLocateCallback {
    void onLocateOk(LocationEntity entity);

    void onLocateFailed();
}
