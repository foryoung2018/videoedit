package com.wmlive.hhvideo.heihei.record.widget;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.wmlive.hhvideo.R;


public class ScaleXViewMode implements ItemViewMode {

    private float mScaleRatio = 0.001f;

    public ScaleXViewMode() {
    }

    public ScaleXViewMode(float scaleRatio) {
        mScaleRatio = scaleRatio;
    }

    @Override
    public void applyToView(View v, RecyclerView parent) {
//        boolean isCenter = (boolean) v.getTag(R.string.tag_is_center);
//        RelativeLayout rv = (RelativeLayout) v;
//        TextView tv = (TextView) rv.getChildAt(0);
//        if (isCenter) {
//            tv.setTextColor(Color.parseColor("#FFFFFF"));
//        } else {
//            tv.setTextColor(Color.parseColor("#999999"));
//        }
    }
}
