package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by lsq on 11/21/2017.5:14 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MarqueTextView extends AppCompatTextView {

    public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueTextView(Context context) {
        super(context);
    }

    @Override

    public boolean isFocused() {
        return true;
    }
}