package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.MyClickListener;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/1/15.
 */

public class VideoCommentTabView extends BaseCustomView {
    public static final short TYPE_COMMENT = 10;
    public static final short TYPE_DECIBEL = 20;

    @BindView(R.id.tvCommentCount)
    TextView tvCommentCount;
    @BindView(R.id.llCommentCount)
    LinearLayout llCommentCount;
    @BindView(R.id.tvDecibelCount)
    TextView tvDecibelCount;
    @BindView(R.id.llDecibelCount)
    LinearLayout llDecibelCount;
    @BindView(R.id.viewDecibelCountLine)
    View viewDecibelCountLine;
    @BindView(R.id.viewCommentCountLine)
    View viewCommentCountLine;

    private IVideoDetailClickListener clickListener;

    public VideoCommentTabView(Context context) {
        super(context);
    }

    public VideoCommentTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoCommentTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    public void initData() {
        llCommentCount.setOnClickListener(myClickListener);
        llDecibelCount.setOnClickListener(myClickListener);
        selectItem(TYPE_COMMENT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_comment_tab_view;
    }

    public void selectItem(short type) {
        tvCommentCount.setTextColor(getResources().getColor(type == TYPE_COMMENT ? R.color.hh_color_c : R.color.hh_color_b));
        viewCommentCountLine.setBackgroundColor(getResources().getColor(type == TYPE_COMMENT ? R.color.hh_color_f : R.color.hh_color_a));
        tvDecibelCount.setTextColor(getResources().getColor(type == TYPE_DECIBEL ? R.color.hh_color_c : R.color.hh_color_b));
        viewDecibelCountLine.setBackgroundColor(getResources().getColor(type == TYPE_DECIBEL ? R.color.hh_color_f : R.color.hh_color_a));
    }

    public void setCommentCount(String count) {
        if (!TextUtils.isEmpty(count)) {
            tvCommentCount.setText("评论  " + count);
        } else {
            tvCommentCount.setText("评论");
        }
    }

    public void setDecibelCount(String count) {
        if (!TextUtils.isEmpty(count)) {
            tvDecibelCount.setText("分贝  " + count);
        } else {
            tvDecibelCount.setText("分贝");
        }
    }

    private MyClickListener myClickListener = new MyClickListener() {
        @Override
        protected void onMyClick(View v) {
            if (clickListener != null) {
                switch (v.getId()) {
                    case R.id.llCommentCount:
                        selectItem(TYPE_COMMENT);
                        if (clickListener != null) {
                            clickListener.onCommentListClick(VideoCommentTabView.this.getId());
                        }
                        break;
                    case R.id.llDecibelCount:
                        selectItem(TYPE_DECIBEL);
                        if (clickListener != null) {
                            clickListener.onDecibelListClick(VideoCommentTabView.this.getId());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    public void setVideoDetailClickListener(IVideoDetailClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface IVideoDetailClickListener {
        void onCommentListClick(int id);

        void onDecibelListClick(int id);
    }

}
