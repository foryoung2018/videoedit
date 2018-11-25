package com.wmlive.hhvideo.heihei.personal.widget.wheel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.wmlive.hhvideo.R;

public class BirthdayCell extends LinearLayout {

    private TextView tv_content;

    public BirthdayCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_content = (TextView) findViewById(R.id.text_item_content);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tv_content.setSelected(selected);
    }

}
