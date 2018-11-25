package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2017/11/29.
 * 发布页顶部按钮
 */

public class PublishEditMenuView extends BaseCustomView {
    @BindView(R.id.ivVolume)
    ImageView ivVolume;
    @BindView(R.id.ivEffect)
    ImageView ivEffect;
    @BindView(R.id.ivEditing)
    ImageView ivEditing;
    private OnMenuClickListener clickListener;

    public PublishEditMenuView(Context context) {
        super(context);
    }

    public PublishEditMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PublishEditMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        ivVolume.setOnClickListener(this);
        ivEffect.setOnClickListener(this);
        ivEditing.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_publish_edit_menu;
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (clickListener != null) {
            switch (v.getId()) {
                case R.id.ivVolume:
                    clickListener.onVolumeClick();
                    break;
                case R.id.ivEffect:
                    clickListener.onEffectClick();
                    break;
                case R.id.ivEditing:
                    clickListener.onEditingClick();
                    break;
            }
        }
    }


    public void setMenuClickListener(OnMenuClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnMenuClickListener {
        void onVolumeClick();

        void onEffectClick();

        void onEditingClick();
    }
}
