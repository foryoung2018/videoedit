package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.wmlive.hhvideo.widget.VideoCommentTabView;
import com.wmlive.hhvideo.widget.VideoPlayItemView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 1/10/2018.2:53 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class VideoDetailHeader extends LinearLayout {

    @BindView(R.id.videoPlayItemView)
    public VideoPlayItemView videoPlayItemView;
    @BindView(R.id.llHeaderTabView)
    public VideoCommentTabView llHeaderTabView;

    public VideoDetailHeader(Context context) {
        super(context);
    }

    public VideoDetailHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoDetailHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        llHeaderTabView.selectItem(VideoCommentTabView.TYPE_DECIBEL);
    }

    public void setData(boolean showList) {
        llHeaderTabView.setVisibility(showList ? VISIBLE : GONE);
    }

}
