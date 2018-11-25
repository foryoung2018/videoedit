package com.wmlive.hhvideo.common.base;

import android.content.Context;

/**
 * Created by lsq on 5/27/2017.
 */

public interface IBasePresenter {

    void bindContext(Context context);

    void start();

    void resume();

    void pause();

    void stop();

    void destroy();

}
