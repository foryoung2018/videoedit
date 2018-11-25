package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.KLog;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 7/3/2017.
 * 首页底部的tab
 */

public class MainBottomTabView extends BaseCustomView {

    @BindView(R.id.ivHome)
    ImageView ivHome;
    @BindView(R.id.flHome)
    FrameLayout flHome;
    @BindView(R.id.ivDiscovery)
    ImageView ivDiscovery;
    @BindView(R.id.rlDiscover)
    RelativeLayout rlDiscover;
    @BindView(R.id.rlDiscover2)
    RelativeLayout rlDiscover2;
    @BindView(R.id.ivPublish)
    ImageView ivPublish;
    @BindView(R.id.ivBell)
    ImageView ivBell;
    @BindView(R.id.tvMessageCount)
    TextView tvMessageCount;
    @BindView(R.id.rlBell)
    RelativeLayout rlBell;
    @BindView(R.id.ivMe)
    ImageView ivMe;
    @BindView(R.id.flMe)
    FrameLayout flMe;
    @BindView(R.id.viewDiscoveryDot)
    View viewDiscoveryDot;
    private int index;
    private BadgeView badgeView;

    public MainBottomTabView(Context context) {
        super(context);
    }

    public MainBottomTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        flHome.setOnClickListener(this);
        rlDiscover.setOnClickListener(this);
        ivPublish.setOnClickListener(this);
        rlBell.setOnClickListener(this);
        flMe.setOnClickListener(this);
        badgeView = new BadgeView(context);
        badgeView.setTargetView(rlDiscover2);
        badgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
        badgeView.setBadgeMargin(0, 4, 4, 0);
        badgeView.setBackground(10, Color.parseColor("#FF0000"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_bottom_tab;
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (null != onMainTabClickListener) {
            boolean isValid;
            switch (v.getId()) {
                case R.id.flHome:
                    isValid = onMainTabClickListener.onHomeClick();
                    if (isValid) {
                        setSelect(0);
                    }
                    break;
                case R.id.rlDiscover:
                    isValid = onMainTabClickListener.onBellClick();
                    if (isValid) {
                        viewDiscoveryDot.setVisibility(GONE);
                        setSelect(1);
                    }
                    break;
                case R.id.ivPublish:
                    isValid = onMainTabClickListener.onPublishClick();
                    break;
                case R.id.rlBell:
                    isValid = onMainTabClickListener.onBellClick();
                    if (isValid) {
                        setSelect(3);
                    }
                    break;
                case R.id.flMe:
                    isValid = onMainTabClickListener.onMineClick();
                    if (isValid) {
                        setSelect(4);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setSelect(int position) {
        index = position;
        ivHome.setSelected(index == 0);
        ivDiscovery.setSelected(index == 1);
        ivBell.setSelected(index == 3);
        ivMe.setSelected(index == 4);
    }

    public void setMessageCount(long count) {
        KLog.i("======setMessageCount:" + count);
        badgeView.setVisibility(count > 0 ? VISIBLE : GONE);
        if (count > 99) {
            badgeView.setText("···");
        } else {
            badgeView.setText(String.valueOf(count));
        }
    }

    public void showDiscoveryDot(boolean show) {
        viewDiscoveryDot.setVisibility(show ? VISIBLE : GONE);
    }

    private OnMainTabClickListener onMainTabClickListener;

    public void setTabClickListener(OnMainTabClickListener onMainTabClickListener) {
        this.onMainTabClickListener = onMainTabClickListener;
    }
}
