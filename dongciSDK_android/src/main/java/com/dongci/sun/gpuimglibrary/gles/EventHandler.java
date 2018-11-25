package com.dongci.sun.gpuimglibrary.gles;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

@SuppressWarnings("unchecked")
public class EventHandler<T> extends Handler {

    private static final String TAG = EventHandler.class.getSimpleName();

    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;//
    private WeakReference<T> mListenObj;

    private volatile boolean mIsHandleError = false;
    private volatile boolean mIsHandleInfo = false;


    public EventHandler(T listenObj) {
        this.mListenObj = new WeakReference<>(listenObj);

    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        if (onErrorListener != null) {
            this.mOnErrorListener = onErrorListener;
            mIsHandleError = true;
        } else {
            this.mOnErrorListener = null;
            mIsHandleError = false;
        }
    }

    public void setOnInfoListener(OnInfoListener onInfoListener) {

        if (onInfoListener != null) {
            this.mOnInfoListener = onInfoListener;
            mIsHandleInfo = true;
        } else {
            this.mOnInfoListener = null;
            mIsHandleInfo = false;
        }
    }

    public boolean isHandleError() {
        return mIsHandleError;
    }

    public boolean isHandleInfo() {
        return mIsHandleInfo;
    }

    @Override
    public void handleMessage(Message msg) {
        T listenObj = this.mListenObj.get();
        if (listenObj == null)
            return;


        if (msg.what > 0) {
            //infos:
            if (mIsHandleInfo) {
                mOnInfoListener.onInfo(listenObj, msg.what, msg.arg1, msg.arg2);
            }
        } else {
            //errors
            if (mIsHandleError) {
                mOnErrorListener.onError(listenObj, msg.what, msg.arg1, msg.arg2);
            }

        }
    }

    public interface OnErrorListener<T> {
        void onError(T listenObj, int what, int arg1, int arg2);
    }

    public interface OnInfoListener<T> {
        void onInfo(T listenObj, int what, int arg1, int arg2);
    }
}