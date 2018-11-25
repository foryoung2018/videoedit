package com.wmlive.hhvideo.heihei.mainhome.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yss on 2017/5/22.
 */

public class ScreenShot {
    // 获取指定Activity的截屏
    public static Bitmap takeScreenShot(Activity activity, Context context) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        int b1getHeight = b1.getHeight();
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width,
                b1.getHeight() - statusBarHeight - getNavigationBarHeight(context));
//        Bitmap b = Bitmap.createBitmap(b1, 0, 0, width,
//                b1.getHeight());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return b;
    }

    // 获取指定Activity的截屏
    public static Bitmap takeScreenShotForFirstPage(Activity activity, Context context) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
//        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width,
//                b1.getHeight()-statusBarHeight-getNavigationBarHeight(context));
        Bitmap b = Bitmap.createBitmap(b1, 0, 0, width,
                b1.getHeight());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return b;
    }


    //listview截图方法
    public static Bitmap shotListView(ListView listview) {

        ListAdapter adapter = listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();

        for (int i = 0; i < itemscount; i++) {

            View childView = adapter.getView(i, null, listview);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }

        Bitmap bigbitmap =
                Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);

        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();

            bmp.recycle();
            bmp = null;
        }

        return bigbitmap;
    }

    //获取当前view的
    public static Bitmap getViewBp(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(),
                    View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                    v.getHeight(), View.MeasureSpec.EXACTLY));
            v.layout((int) v.getX(), (int) v.getY(),
                    (int) v.getX() + v.getMeasuredWidth(),
                    (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }

        Bitmap b = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return b;
    }

    //webview相关代码开始---------------------------------------------------------------------------------------------------------------
    public static Bitmap getViewBitmapWithoutBottom(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
            v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getMeasuredWidth(), (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }
        Bitmap bp = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight() - v.getPaddingBottom());
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return bp;
    }

    public static Bitmap getViewBitmap(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
            v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getMeasuredWidth(), (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return b;
    }

    /**
     * 获取 WebView 视图截图
     *
     * @param context
     * @param view
     * @return
     */
    public static Bitmap getWebViewBitmap(Context context, WebView view) {
        if (null == view) return null;
        view.scrollTo(0, 0);
        view.buildDrawingCache(true);
        view.setDrawingCacheEnabled(true);
        view.setVerticalScrollBarEnabled(false);
        Bitmap b = getViewBitmapWithoutBottom(view);
        // 可见高度
        int vh = view.getHeight();
        // 容器内容实际高度
        int th = (int) (view.getContentHeight() * view.getScale());
        Bitmap temp = null;
        if (th > vh) {
            int w = getScreenWidth(context);
            int absVh = vh - view.getPaddingTop() - view.getPaddingBottom();
            do {
                int restHeight = th - vh;
                if (restHeight <= absVh) {
                    view.scrollBy(0, restHeight);
                    vh += restHeight;
                    temp = getViewBitmap(view);
                } else {
                    view.scrollBy(0, absVh);
                    vh += absVh;
                    temp = getViewBitmapWithoutBottom(view);
                }
                b = mergeBitmap(vh, w, temp, 0, view.getScrollY(), b, 0, 0);
            } while (vh < th);
        }
        // 回滚到顶部
        view.scrollTo(0, 0);
        view.setVerticalScrollBarEnabled(true);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return b;
    }

    /**
     * 拼接图片
     *
     * @param newImageH
     * @param newImageW
     * @param background
     * @param backX
     * @param backY
     * @param foreground
     * @param foreX
     * @param foreY
     * @return
     */
    public static Bitmap mergeBitmap(int newImageH, int newImageW, Bitmap background, float backX, float backY, Bitmap foreground, float foreX, float foreY) {
        if (null == background || null == foreground) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(newImageW, newImageH, Bitmap.Config.RGB_565);
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(background, backX, backY, null);
        cv.drawBitmap(foreground, foreX, foreY, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return bitmap;
    }

    /**
     * get the width of screen
     */
    public static int getScreenWidth(Context ctx) {
        int w = 0;
        if (Build.VERSION.SDK_INT > 13) {
            Point p = new Point();
            ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(p);
            w = p.x;
        } else {
            w = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        }
        return w;
    }

    /**
     * 保存图片
     *
     * @param context
     * @param bitmap
     * @param file
     * @param quality
     * @return
     */
    public static boolean save(Context context, Bitmap bitmap, File file, int quality) {
        if (bitmap == null) return false;
        // 获得后缀格式
        String abs = file.getAbsolutePath();
        String suffix = abs.substring(abs.lastIndexOf(".") + 1).toLowerCase();
        Bitmap.CompressFormat format;
        if ("jpg".equals(suffix) || "jpeg".equals(suffix)) {
            format = Bitmap.CompressFormat.JPEG;
        } else {
            format = Bitmap.CompressFormat.PNG;
            quality = 100;
        }
        if (file.exists() && !file.delete()) return false;
        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(format, quality, stream);
            stream.flush();
            stream.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //webview相关代码结束---------------------------------------------------------------------------------------------------------

    // 保存到sdcard
    public static void savePic(Bitmap b, String strFileName) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //截取activity
    public static String shootActivity(Activity a, Context context) {
        //Environment.getExternalStorageDirectory()
        String strFileName = "/sdcard/" + String.valueOf(System.currentTimeMillis()) + ".png";
        ScreenShot.savePic(ScreenShot.takeScreenShot(a, context), strFileName);
        return strFileName;
    }

    //截取指定Activity
    public static String shootForFirstPage(Activity a, Context context) {
        //Environment.getExternalStorageDirectory()
        String strFileName = "/sdcard/" + String.valueOf(System.currentTimeMillis()) + ".png";
        ScreenShot.savePic(ScreenShot.takeScreenShotForFirstPage(a, context), strFileName);
        return strFileName;
    }

    // 截取WebView
    public static String shootWebView(WebView a, Context context) {
        //Environment.getExternalStorageDirectory()
        String strFileName = "/sdcard/" + String.valueOf(System.currentTimeMillis()) + ".png";
        ScreenShot.savePic(ScreenShot.getWebViewBitmap(context, a), strFileName);
        return strFileName;
    }

    //截取view
    public static String shootView(View a) {
        //Environment.getExternalStorageDirectory()
        String strFileName = "/sdcard/" + String.valueOf(System.currentTimeMillis()) + ".png";
        ScreenShot.savePic(ScreenShot.getViewBp(a), strFileName);
        return strFileName;
    }


    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            Class systemPropertiesClass = null;
            Method m = null;
            try {
                systemPropertiesClass = Class.forName("android.os.SystemProperties");
                m = systemPropertiesClass.getMethod("get", String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check override flag
            String sNavBarOverride = null;
            try {
                sNavBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
}
