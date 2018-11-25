package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/28/2017.
 * 录制页面顶部的menu,包括剪切，美颜，闪光灯
 */

public class RecordMenuView extends BaseCustomView {

    @BindView(R.id.ivCut)
    ImageView ivCut;
    @BindView(R.id.ivBeauty)
    ImageView ivBeauty;
    @BindView(R.id.ivFlash)
    ImageView ivFlash;
    @BindView(R.id.ivToggle)
    ImageView ivToggle;
    @BindView(R.id.ivSort)
    ImageView ivSort;

    private OnMenuClickListener clickListener;

    public RecordMenuView(Context context) {
        super(context);
    }

    public RecordMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        ivCut.setOnClickListener(this);
        ivBeauty.setOnClickListener(this);
        ivFlash.setOnClickListener(this);
        ivToggle.setOnClickListener(this);
        ivSort.setOnClickListener(this);
        setCutEnable(true);
        setBeautyEnable(true, true);
        setFlashEnable(false, false);
        setToggleEnable(true);
        setSortEnable(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_record_menu;
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (clickListener != null) {
            switch (v.getId()) {
                case R.id.ivBeauty:
                    clickListener.onBeautyClick();
                    break;
                case R.id.ivFlash:
                    clickListener.onFlashClick();
                    break;
                case R.id.ivToggle:
                    clickListener.onToggleClick();
                    break;
            }
        }
    }

    public void setCutEnable(boolean enable) {
        ivCut.setImageResource(enable ? R.drawable.icon_video_topbar_music_nor : R.drawable.icon_video_topbar_music_dis);
        ivCut.setClickable(enable);
    }

    public void setBeautyEnable(boolean clickable, boolean enable) {
        ivBeauty.setImageResource(clickable ? (enable ? R.drawable.icon_video_topbar_beauty : R.drawable.icon_video_topbar_unbeauty) : R.drawable.icon_video_topbar_unbeauty_dis);
        ivBeauty.setClickable(clickable);
    }

    public void setFlashEnable(boolean enable, boolean isFront) {
        ivFlash.setClickable(enable);
        ivFlash.setImageResource(enable ? (isFront ? R.drawable.icon_video_topbar_flash_on : R.drawable.icon_video_topbar_flash_off) : R.drawable.icon_video_topbar_flash_dis);
    }

    public void setToggleEnable(boolean enable) {
        ivToggle.setImageResource(enable ? R.drawable.icon_video_topbar_turn : R.drawable.icon_video_topbar_turn_dis);
        ivToggle.setClickable(enable);
    }

    public void  setSortEnable(boolean enable) {
        ivSort.setImageResource(enable ? R.drawable.icon_film_move : R.drawable.icon_film_move_dis);
        ivSort.setClickable(enable);
    }

    public void setMenuClickListener(OnMenuClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnMenuClickListener {

        void onBeautyClick();

        void onFlashClick();

        void onToggleClick();
    }
}
