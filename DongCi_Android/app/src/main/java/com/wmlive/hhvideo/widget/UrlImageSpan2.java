package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wmlive.hhvideo.utils.DeviceUtils;

import java.lang.reflect.Field;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/8/4.
 * <p>
 * 获取网络图片的ImageSpan
 * <p>
 * 用于铃铛 的 图文混排
 */

public class UrlImageSpan2 extends ImageSpan {

    private String url;
    private TextView tv;

    public UrlImageSpan2(Context context, String url, TextView tv) {
        super(context, R.drawable.icon_profile_gift_48_48);
        this.url = url;
        this.tv = tv;
    }

    @Override
    public Drawable getDrawable() {
        Glide.with(tv.getContext()).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                Resources resources = tv.getContext().getResources();
                BitmapDrawable b = new BitmapDrawable(resources, bitmap);

                int fontH = (int) (DeviceUtils.dip2px(tv.getContext(), 14) * 1.5);
                int height = fontH;
                int width = (int) ((float) b.getIntrinsicWidth() / (float) b
                        .getIntrinsicHeight()) * fontH;
                if (width == 0) {
                    width = b.getIntrinsicWidth();
                }
                b.setBounds(0, 0, width, height);

                Field mDrawable;
                Field mDrawableRef;
                try {
                    mDrawable = ImageSpan.class.getDeclaredField("mDrawable");
                    mDrawable.setAccessible(true);
                    mDrawable.set(UrlImageSpan2.this, b);

                    mDrawableRef = DynamicDrawableSpan.class.getDeclaredField("mDrawableRef");
                    mDrawableRef.setAccessible(true);
                    mDrawableRef.set(UrlImageSpan2.this, null);

//                    tv.setText(tv.getText());
                } catch (IllegalAccessException e) {

                } catch (NoSuchFieldException e) {

                }
            }
        });
        return super.getDrawable();
    }

    /**
     * 按宽度缩放图片
     *
     * @param bmp  需要缩放的图片源
     * @param newW 需要缩放成的图片宽度
     * @return 缩放后的图片
     */
    public static Bitmap zoom(@NonNull Bitmap bmp, int newW) {

        // 获得图片的宽高
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        // 计算缩放比例
        float scale = ((float) newW) / width;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);

        return newbm;
    }
}
