package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2017/11/28.
 * 视频展示CoverView
 */

public class SmallShowVideoView extends AnomalyView implements View.OnClickListener {

    public static final int VIEW_TYPE_REPLACE = 0;
    public static final int VIEW_TYPE_JOIN = 1;
    public ImageView ivReplace;
    private TextView tvUserName;
    private int viewType;
    private OnSmallShowVideoClickListener smallShowVideoClickListener;

    public SmallShowVideoView(@NonNull Context context) {
        this(context, null);
    }

    public SmallShowVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallShowVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_small_show_video_view, this, false);
        ivReplace = view.findViewById(R.id.ivReplace);
        tvUserName = view.findViewById(R.id.tvUserName);
        ivReplace.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        addView(view);
    }

    @Override
    public void onClick(View v) {
        if (smallShowVideoClickListener != null) {
            int index = getTag() == null ? -1 : (Integer) getTag();
            switch (v.getId()) {
                case R.id.ivReplace:
                    if (VIEW_TYPE_REPLACE == viewType) {
                        smallShowVideoClickListener.onReplaceClick(index, SmallShowVideoView.this);
                    } else if (VIEW_TYPE_JOIN == viewType) {
                        smallShowVideoClickListener.onJoinClick(index, SmallShowVideoView.this);
                    }
                    break;
                case R.id.tvUserName:
                    smallShowVideoClickListener.onUserNameClick(index, SmallShowVideoView.this);
                    break;
            }
        }
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
        if (viewType == VIEW_TYPE_JOIN) {
            ivReplace.setImageResource(R.drawable.icon_video_create_white);
        } else if (viewType == VIEW_TYPE_REPLACE) {
            ivReplace.setImageResource(R.drawable.icon_video_replace);
        }
    }

    public void setUserName(String userName) {
        if (!TextUtils.isEmpty(userName)) {
            tvUserName.setVisibility(View.VISIBLE);
            tvUserName.setText(userName);
        } else {
            tvUserName.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void setOnSmallShowVideoClickListener(OnSmallShowVideoClickListener smallShowVideoClickListener) {
        this.smallShowVideoClickListener = smallShowVideoClickListener;
    }

    public interface OnSmallShowVideoClickListener {

        /**
         * 替换
         *
         * @param index
         * @param view
         */
        void onReplaceClick(int index, SmallShowVideoView view);

        /**
         * 加入创作
         *
         * @param index
         * @param view
         */
        void onJoinClick(int index, SmallShowVideoView view);

        /**
         * 点击用户名
         *
         * @param index
         * @param view
         */
        void onUserNameClick(int index, SmallShowVideoView view);
    }
}
