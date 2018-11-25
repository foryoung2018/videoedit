package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.MyClickListener;

import cn.wmlive.hhvideo.R;


/**
 * 页面状态
 * Created by lsq on 12/15/2016.
 */

public class StatusView extends FrameLayout {
    private ImageView mIvPagePic;
    private ProgressBar mPbLoading;
    private TextView mTvPageInfo;
    private Button mBtPageOperate;
    private OptionClickListener mOptionClickListener;

    public static final int STATUS_NORMAL = 0;  //页面正常
    public static final int STATUS_LOADING = 1; //页面加载
    public static final int STATUS_EMPTY = 2;    //页面为空
    public static final int STATUS_ERROR = 3;   //页面出错


    public static StatusView createStatusView(Context context) {
        return new StatusView(context);
    }

    public StatusView setOptionClickListener(OptionClickListener clickListener) {
        mOptionClickListener = clickListener;
        return this;
    }

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LinearLayout statusPage = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_status_page, null);
        mIvPagePic = (ImageView) statusPage.findViewById(R.id.iv_page_pic);
        mPbLoading = (ProgressBar) statusPage.findViewById(R.id.pb_loading);
        mTvPageInfo = (TextView) statusPage.findViewById(R.id.tv_page_info);
        mBtPageOperate = (Button) statusPage.findViewById(R.id.bt_page_operate);
        addView(statusPage);
    }


    /**
     * 显示其他非数据页面
     *
     * @param status  0:数据正常,1:正在加载,2:页面为空,3:页面出错
     * @param message
     */
    public void showStatusPage(int status, String message) {
        this.setVisibility(status != STATUS_NORMAL ? View.VISIBLE : View.GONE);
        if (status != STATUS_NORMAL) {
            mPbLoading.setVisibility(status == STATUS_LOADING ? View.VISIBLE : View.GONE);
            mTvPageInfo.setVisibility(status != STATUS_ERROR ? View.VISIBLE : View.GONE);
            mTvPageInfo.setText(message);
            mBtPageOperate.setVisibility(status == STATUS_ERROR ? View.VISIBLE : View.GONE);
            mIvPagePic.setVisibility(status != STATUS_LOADING ? View.VISIBLE : View.GONE);
            mBtPageOperate.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (mOptionClickListener != null) {
                        mOptionClickListener.onOptionClick(v);
                    }
                }
            });
        }
    }

    public interface OptionClickListener {
        void onOptionClick(View view);
    }
}
