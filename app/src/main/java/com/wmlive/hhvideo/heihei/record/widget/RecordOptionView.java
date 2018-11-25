package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/25/2017.
 */

public class RecordOptionView extends BaseCustomView {
    @BindView(R.id.ivIcon)
    public ImageView ivIcon;
    @BindView(R.id.tvTitle)
    public TextView tvTitle;

    public RecordOptionView(Context context) {
        super(context);
    }

    public RecordOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    public void setEnable(boolean enable) {
        tvTitle.setTextColor(getResources().getColor(enable ? R.color.hh_color_a : R.color.hh_color_c));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_record_option_tab;
    }
}
