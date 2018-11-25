package com.wmlive.hhvideo.heihei.personal.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.personal.fragment.UserHomeFragment;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/5/31.
 * <p>
 * 我的--他人-作品-holder
 */

public class PersonalProductHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.rl_bg)
    RelativeLayout rlBg;
    @BindView(R.id.iv_bg)
    SimpleDraweeView ivBg;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.iv_heart)
    ImageView ivHeart;

    private ViewGroup.LayoutParams layoutParams;

    public PersonalProductHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);

        int width3 = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0] / 3 - UserHomeFragment.SPACE_ITEM_DECRRATION * 2;
        int height = width3 * 16 / 9;
        layoutParams = rlBg.getLayoutParams();
        layoutParams.height = height;
    }
}
