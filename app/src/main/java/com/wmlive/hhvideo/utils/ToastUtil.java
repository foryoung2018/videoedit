package com.wmlive.hhvideo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import cn.wmlive.hhvideo.R;

/**
 * Created by vhawk on 2017/5/23.
 * Modify by lsq
 */

public class ToastUtil {
    private static Context mContext;
    private static String mOldMsg;
    protected static Toast mToast = null;
    private static long mOneTime = 0;
    private static long mTwoTime = 0;

    /**
     * 初始化Toast
     *
     * @param ctx 必须使用Application，否则会造成内存泄漏
     */
    public static void init(Context ctx) {
        mContext = ctx;
    }

    public static void showToast(int resId) {
        showToast(mContext.getString(resId));
    }

    public static void showToast(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            KLog.i("======不是主线程，不弹Toast");
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(mContext, s, Toast.LENGTH_SHORT);
            mToast.setView(LayoutInflater.from(mContext).inflate(R.layout.view_toast, null));
            mToast.setGravity(Gravity.CENTER, 0, 0);
            ((TextView) mToast.getView()).setText(s);
            show();
            mOneTime = System.currentTimeMillis();
        } else {
            mTwoTime = System.currentTimeMillis();
            if (s.equals(mOldMsg)) {
                if (mTwoTime - mOneTime > Toast.LENGTH_SHORT) {
                    if (mToast.getView() instanceof TextView) {
                        ((TextView) mToast.getView()).setText(s);
                    } else {
                        mToast.setText(s);
                    }
                    show();
                }
            } else {
                mOldMsg = s;
                if (mToast.getView() instanceof TextView) {
                    ((TextView) mToast.getView()).setText(s);
                } else {
                    mToast.setText(s);
                }
                show();
            }
        }
        mOneTime = mTwoTime;
    }

    private static void show(){
        try {
            mToast.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
