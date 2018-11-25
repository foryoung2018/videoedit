package com.wmlive.hhvideo.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Administrator on 2018/6/13.
 */

public class ScreenUtil {
        private static int width = 0;
        private static int height = 0;

        /**
         * 获取屏幕分辨率
         *
         * @param context
         * @return
         */
        public static String getScreenPixel(Context context) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowMgr.getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels + "x" + dm.heightPixels;
        }

        /**
         * 获取屏幕宽度
         *
         * @return
         */
        public static int getWidth(Context context) {
            if (width == 0) {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                android.view.Display display = wm.getDefaultDisplay();
                width = display.getWidth();
            }

            return width;
        }

        /**
         * 设置屏幕宽度
         *
         * @return
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * 获取屏幕高度
         *
         * @return
         */
        public static int getHeight(Context context) {
            if (height == 0) {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                android.view.Display display = wm.getDefaultDisplay();
                height = display.getHeight();
            }

            return height;
        }

        /**
         * 设置屏幕宽度
         *
         * @return
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         * dip2px
         *
         * @param context
         * @param dpValue
         * @return
         */
        public static int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        /**
         * px2dip
         *
         * @param context
         * @return
         */
        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }

        /**
         * px2sp
         *
         * @param context
         * @return
         */
        public static int px2sp(Context context, float pxValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (pxValue / fontScale + 0.5f);
        }

        /**
         * sp2px
         *
         * @param spValue
         * @return
         */
        public static int sp2px(Context context, float spValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (spValue * fontScale + 0.5f);
        }

        /**
         * 获得状态栏的高度
         *
         * @param context
         * @return
         */
        public static int getStatusHeight(Context context) {

            int statusHeight = -1;
            try {
                Class clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height")
                        .get(object).toString());
                statusHeight = context.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return statusHeight;
        }

        // 隐藏软键盘
        public static void hideSoftInputBoard(Activity context) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
        }

        /**
         * 设置view margin
         *
         * @param v
         * @param l
         * @param t
         * @param r
         * @param b
         */
        public static void setMargins(View v, int l, int t, int r, int b) {
            if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                p.setMargins(l, t, r, b);
                v.requestLayout();
            }
        }


}
