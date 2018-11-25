package com.wmlive.hhvideo.utils.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.wmlive.hhvideo.utils.KLog;

import java.util.concurrent.ExecutionException;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/31/2017.
 * Glide图片加载类
 */

public class GlideLoader {

    private static final String TAG = "GlideLoader";

    /**
     * 默认的配置
     *
     * @param placeHolder
     * @param errorDrawable
     * @return
     */
    private static RequestOptions defaultOptions(int placeHolder, int errorDrawable) {
        return new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeHolder)
                .error(errorDrawable);
    }

    /**
     * 加载Gif图片
     *
     * @param uri
     * @param imageView
     */
    public static void loadGif(String uri, ImageView imageView) {
        Glide.with(imageView.getContext()).asGif().load(uri).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param url
     * @param iv
     */
    public static void loadCircleImage(String url, final ImageView iv, int placeHolder) {
        loadImage(url, iv, placeHolder, defaultOptions(placeHolder, placeHolder).circleCrop());
    }

    public static void loadCircleImage(int drawableId, final ImageView iv, int placeHolder) {
        loadImage(drawableId, iv, new RequestOptions()
                .placeholder(placeHolder)
                .error(placeHolder)
                .circleCrop());
    }

    public static void loadCircleImage(String url, final ImageView iv, int placeHolder, LoadCallback loadCallback) {
        loadImage(url, iv, placeHolder, defaultOptions(placeHolder, placeHolder).circleCrop(), loadCallback);
    }

    /**
     * 加载圆角图片
     *
     * @param url
     * @param iv
     * @param radius
     */
    public static void loadCornerImage(String url, final ImageView iv, int radius) {
        Glide.with(iv.getContext())
                .load(url)
                .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(radius)))
                .into(iv);

    }

    public static void loadVideoThumb(String url, final ImageView iv, int radius) {
        Glide.with(iv.getContext())
                .load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.bg_message_video_default)
                        .transform(new GlideRoundTransform(iv.getContext(), radius)))
                .into(iv);
    }

    public static void loadVideoThumb(String url, final ImageView iv, int radius, int holder) {
        Glide.with(iv.getContext())
                .load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(holder)
                        .transform(new GlideRoundTransform(iv.getContext(), radius)))
                .into(iv);
    }

    /**
     * 加载圆角图片
     *
     * @param url         图片URL
     * @param iv          图片宿主
     * @param placeHolder 占位图
     * @param radius      圆角半径
     */
    public static void loadCornerImage(String url, final ImageView iv, int placeHolder, int radius) {
        Glide.with(iv.getContext())
                .load(url)
                .apply(new RequestOptions().centerCrop().placeholder(placeHolder).transform(new RoundedCorners(radius)))
                .into(iv);

    }


    /**
     * 加载图片
     *
     * @param url
     * @param iv
     */
    public static void loadImage(String url, final ImageView iv) {
        loadImage(url, iv, R.drawable.bg_gray_shape);
    }

    /**
     * 加载图片
     *
     * @param url
     * @param iv
     * @param placeHolder
     */
    public static void loadImage(String url, final ImageView iv, int placeHolder) {
        loadImage(url, iv, placeHolder, defaultOptions(placeHolder, placeHolder));
    }

    public static void loadFitCenterImage(String url, final ImageView iv, int placeHolder) {
        loadImage(url, iv, placeHolder, defaultOptions(placeHolder, placeHolder).fitCenter());
    }


    public static void loadCenterCropImage(String url, final ImageView iv) {
        loadImage(url, iv, R.drawable.bg_home_video_default,
                defaultOptions(R.drawable.bg_home_video_default, R.drawable.bg_home_video_default).centerCrop());
    }

    public static void loadCenterCropImage(String url, final ImageView iv, int placeHolder) {
        loadImage(url, iv, placeHolder, defaultOptions(placeHolder, placeHolder).centerCrop());
    }


    /**
     * 加载资源图片
     *
     * @param resId
     * @param iv
     * @param placeHolder
     */
    public static void loadImage(int resId, final ImageView iv, int placeHolder) {
        loadImage(resId, iv, defaultOptions(placeHolder, placeHolder));
    }

    /**
     * 加载图片
     *
     * @param url
     * @param iv
     * @param placeHolder
     */
    public static void loadImage(String url, final ImageView iv, int placeHolder, RequestOptions options) {
        loadImage(url, iv, placeHolder, options, null);
    }

    public static void loadImage(String url, final ImageView iv, int placeHolder, RequestOptions options, final LoadCallback callback) {
        if (TextUtils.isEmpty(url)) {
            iv.setImageResource(placeHolder);
            return;
        }
        Glide.with(iv.getContext())
                .load(url)
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        iv.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (resource != null) {
                            iv.setImageDrawable(resource);
                            if (callback != null && iv.getDrawable() != null) {
                                callback.onDrawableLoaded(resource);
                            }
                        }
                    }
                });
    }

    /**
     * 加载资源图片
     *
     * @param resId
     * @param iv
     * @param options
     */
    public static void loadImage(int resId, final ImageView iv, RequestOptions options) {
        Glide.with(iv.getContext())
                .load(resId)
                .apply(options)
                .into(iv);
    }

    /**
     * 以Url加载图片，并监听图片加载的结果
     */
    public static void loadImage(String url, final ImageView iv, final LoadCallback callback) {
        loadImage(url, iv, R.drawable.bg_gray_shape, callback);
    }

    /**
     * 以Url加载图片，并监听图片加载的结果
     */
    public static void loadImage(String url, final ImageView iv, int holder, final LoadCallback callback) {
        if (TextUtils.isEmpty(url)) {
            iv.setImageResource(holder);
            return;
        }

        Glide.with(iv.getContext())
                .load(url)
                .apply(defaultOptions(holder, holder))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        iv.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (resource != null) {
                            iv.setImageDrawable(resource);
                            if (callback != null && iv.getDrawable() != null) {
                                callback.onDrawableLoaded(resource);
                            }
                        }
                    }
                });
    }

    public static void loadGif(String gifUrl, String thumbnailUrl, ImageView imageView) {

//        Glide
//                .with(imageView.getContext())
//                .load(url)
//                .into(imageView);

        Glide.with(imageView.getContext())
                .load(gifUrl)
                .thumbnail(Glide.with(imageView.getContext()).load(thumbnailUrl))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        KLog.v(TAG, "onLoadFailed " + e.toString());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        KLog.v(TAG, "onResourceReady " + target);
                        return false;
                    }
                })
                .into(imageView);

    }


    public static Bitmap downloadImage(Context ctx, String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        try {
            KLog.i("=======Glide downloadImage:" + url);
            return Glide.with(ctx)
                    .asBitmap()
                    .load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                    .submit(width > 0 ? width : Target.SIZE_ORIGINAL, height > 0 ? height : Target.SIZE_ORIGINAL)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载一张图片，并返回Bitmap
     */
    public static Bitmap downloadImage(Context ctx, String url) {
        return downloadImage(ctx, url, 0, 0);
    }
}
