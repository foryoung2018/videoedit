package com.wmlive.hhvideo.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.record.CloneableEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.beans.splash.InitUrlResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wmlive.hhvideo.BuildConfig;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 7/27/2017.
 * 通用的，没法归类的工具类
 */

public class CommonUtils {

    /**
     * 生成随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public static int getRandom(int min, int max) {
        return (int) Math.round(Math.random() * (max - min) + min);
    }

    public static float getRandom(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }


    /**
     * dauble类型，整型就显示整数
     *
     * @param num
     * @return
     */
    public static String doubleTrans(double num) {
        if (num % 1.0 == 0) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }

    /**
     * 列表克隆，克隆前务必保证传入的List不为空!!!
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T extends CloneableEntity> List<T> cloneList(List<T> list) {
        if (list == null) {
            List<T> clonedList = new ArrayList<>();
            return clonedList;
        }
        int size = list.size();
        List<T> clonedList = new ArrayList<>(size);
        T entity;
        try {
            for (int i = 0; i < size; i++) {
                entity = (T) list.get(i).clone();
                if (entity != null) {
                    clonedList.add(entity);
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clonedList;
    }

    public static <T> String printList(List<T> list) {
        if (list != null) {
            StringBuilder stringBuilder = new StringBuilder(100);
            for (T t : list) {
                if (t != null) {
                    stringBuilder
                            .append(t.toString())
                            .append("\n");
                }
            }
            return stringBuilder.toString();
        }
        return "null";
    }

    public static long stringParseLong(String srcString) {
        long dest = -1L;
        if (!TextUtils.isEmpty(srcString) && !"null".equalsIgnoreCase(srcString)) {
            try {
                dest = Long.parseLong(srcString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                dest = 0L;
            }
        }
        return dest;
    }

    public static <K, V> String printMap(Map<K, V> map) {
        if (map != null) {
            StringBuilder stringBuilder = new StringBuilder(100);
            for (K k : map.keySet()) {
                stringBuilder
                        .append("key:").append(k)
                        .append("-->")
                        .append("value:").append(map.get(k))
                        .append("\n");
            }
            return stringBuilder.toString();
        }
        return "null";
    }

    public static String getVersion(boolean needVersion) {
        String version = BuildConfig.APP_VERSION;
        if (needVersion) {
            version += " (build:" + BuildConfig.VERSION_CODE
                    + ",type:" + BuildConfig.BUILD_TYPE
                    + ",channel:" + HeaderUtils.getChannel() + ")";
        }
        return version;
    }

    public static void showFlyHeart(RelativeLayout rootView, float rawDownX, float rawDownY,
                                    float targetRawX, float targetRawY) {
        if (rootView == null) {
            return;
        }

        final ImageView animLikeView = new ImageView(rootView.getContext());
        animLikeView.setImageResource(R.drawable.icon_homepage_bigheart);
        animLikeView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        float animLikeViewWidth = animLikeView.getMeasuredWidth();
        float animLikeViewHeight = animLikeView.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = (int) (rawDownX - animLikeViewWidth * 0.5);
        layoutParams.topMargin = (int) (rawDownY - animLikeViewHeight * 0.5 - GlobalParams.StaticVariable.sStatusBarHeight - DeviceUtils.dip2px(rootView.getContext(), 44));
        animLikeView.setLayoutParams(layoutParams);
        rootView.addView(animLikeView);

        Animator heartScaleAnim = AnimatorInflater.loadAnimator(rootView.getContext(), R.animator.heart_scale_anim);
        heartScaleAnim.setTarget(animLikeView);
        float distanceX = targetRawX + -rawDownX;
        float distanceY = targetRawY + -rawDownY;
        KLog.i("====click point:" + rawDownX + " x " + rawDownY
                + " \nivLike on screen:" + targetRawX + " x " + targetRawY
                + " \nmove distance:" + distanceX + " x " + distanceY);
        final ObjectAnimator transXAnim = ObjectAnimator
                .ofFloat(animLikeView, "translationX", 0, distanceX)
                .setDuration(160);
        final ObjectAnimator transYAnim = ObjectAnimator
                .ofFloat(animLikeView, "translationY", 0, distanceY)
                .setDuration(160);
        final ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(animLikeView, "scaleX", 0.8f, 0.4f)
                .setDuration(160);
        final ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(animLikeView, "scaleY", 0.8f, 0.4f)
                .setDuration(160);
        heartScaleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                transXAnim.start();
                transYAnim.start();
                scaleXAnim.start();
                scaleYAnim.start();
            }
        });
        transXAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootView.removeView(animLikeView);
            }
        });
        heartScaleAnim.start();
    }

    public static Point getViewPoint(View view, boolean center) {
        if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            KLog.i("=====view getLocationInWindow left:" + location[0] + " ,top:" + location[1] + " ，width:" + view.getWidth() + " ,height:" + view.getHeight());
            return new Point(location[0] + (center ? view.getWidth() / 2 : 0), location[1] + (center ? view.getHeight() / 2 : 0));
        }
        return new Point();
    }

    public static String getCountString(int count) {
        return getCountString(count, true);
    }

    public static String getCountString(int count, boolean showZero) {
        if (count >= 10000) {
            return String.format(Locale.getDefault(), "%.1f", (count * 1f / 10000)) + "W";
        } else if (count >= 1000) {
            return String.format(Locale.getDefault(), "%.1f", (count * 1f / 1000)) + "K";
        } else if (count >= 0) {
            if (showZero) {
                return String.valueOf(count);
            } else {
                return count == 0 ? "" : String.valueOf(count);
            }
        }
        return showZero ? "0" : "";
    }

    public static final String LABEL_DOT = "\\.";

    /**
     * 比较版本号
     *
     * @param version1
     * @param version2
     * @return 1：大于，0：等于，-1小于
     */
    public static int compareVersion(String version1, String version2) {
        if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version1)) {
            return 1;//一律按照高版本处理
        }
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split(LABEL_DOT);
        String[] version2Array = version2.split(LABEL_DOT);
        int index = 0;
        //获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        //循环判断每位的大小
        while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            //如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    public static Observable<String> parseLocalApi() {
        KLog.i("========从本地加载备份api");
        return Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        AssetManager assetManager = DCApplication.getDCApp().getAssets();
                        try {
                            InputStream is = assetManager.open(GlobalParams.Config.IS_DEBUG ? "apit.json" : "api.json");
                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                            StringBuilder stringBuffer = new StringBuilder();
                            String str;
                            while ((str = br.readLine()) != null) {
                                stringBuffer.append(str);
                            }
                            str = stringBuffer.toString();
                            KLog.i("=======本地加载的备份api");
                            KLog.json(str);
                            InitCatchData.saveInitData(JsonUtils.parseObject(str, InitUrlResponse.class));
                            return str;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
