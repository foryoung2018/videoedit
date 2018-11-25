package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.wmlive.hhvideo.utils.KLog;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/4/19.
 */

public class CustomFontTextView extends AppCompatTextView {
    public CustomFontTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context ctx, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String customFont = a.getString(R.styleable.CustomFontTextView_cftv_fontFamily);
        if(!TextUtils.isEmpty(customFont)) {
            setCustomFont(ctx, customFont);
        }
        a.recycle();
    }

    public void setCustomFont(Context ctx, String font) {
        try {
            Typeface tf = Typeface.createFromAsset(ctx.getAssets(), font);
            setTypeface(tf);
        } catch (Exception e) {
            KLog.e("xxxx", "Can not find font " + font + " " + e.getMessage());
        }
    }

    public void setCustomFont(Context ctx, int fontRes) {
        try {
            Typeface tf = ResourcesCompat.getFont(ctx, fontRes);
            setTypeface(tf);
        } catch (Exception e) {
            KLog.e("xxxx", "Can not find font " + e.getMessage());
        }
    }


}
