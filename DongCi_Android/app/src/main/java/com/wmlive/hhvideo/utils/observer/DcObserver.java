package com.wmlive.hhvideo.utils.observer;

import com.wmlive.hhvideo.utils.KLog;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lsq on 12/18/2017.4:48 PM
 *
 * @author lsq
 * @describe 如果不需要处理订阅结果，可以使用这个类
 */

public class DcObserver<T> implements Observer<T> {


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        KLog.i("=======DcObserver onNext" );
    }

    @Override
    public void onError(Throwable e) {
        KLog.i("=======DcObserver onError:" + e.getMessage());
    }

    @Override
    public void onComplete() {
        KLog.i("=======DcObserver onComplete" );
    }
}
