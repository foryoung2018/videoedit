package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 7/3/2017.
 * 首页顶部的tab
 */

public class MainTopTabView extends BaseCustomView {
    @BindView(R.id.ivBell)
    ImageView ivBell;
    @BindView(R.id.tv_discory)
    CustomFontTextView tv_discory;
    @BindView(R.id.tvRecommend)
    CustomFontTextView tvRecommend;
    @BindView(R.id.tvLatest)
    CustomFontTextView tvLatest;
    @BindView(R.id.viewRightDot)
    View viewRightDot;
    @BindView(R.id.tvMessageCount)
    TextView tvMessageCount;
    @BindView(R.id.rlBell)
    RelativeLayout rlBell;
    @BindView(R.id.rlRoot)
    RelativeLayout rlRoot;
    @BindView(R.id.llTitleTab)
    LinearLayout llTitleTab;
    private int currentPosition = -1;

    private int titleTabHeight;
    private int titleTabTopMargin = DeviceUtils.dip2px(DCApplication.getDCApp(), 20);
    private int ivBellTopPadding;
    private int viewHeight;
    private BadgeView badgeView;

    public MainTopTabView(Context context) {
        super(context);
    }

    public MainTopTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        ivBell.setOnClickListener(this);
        tv_discory.setOnClickListener(this);

        tvRecommend.setOnClickListener(this);
        tvLatest.setOnClickListener(this);

        ivBell.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToastUtil.showToast(CommonUtils.getVersion(true));
                return true;
            }
        });
        setClickDelay(1500);
        llTitleTab.post(new Runnable() {
            @Override
            public void run() {
                viewHeight = rlRoot.getHeight();
                titleTabHeight = llTitleTab.getHeight();
                ivBellTopPadding = ivBell.getPaddingTop();
//                titleTabTopMargin = ((RelativeLayout.LayoutParams) llTitleTab.getLayoutParams()).topMargin;
            }
        });
        badgeView = new BadgeView(getContext());
        badgeView.setTargetView(rlBell);
        badgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
        badgeView.setBadgeMargin(0, 4, 14, 0);
        badgeView.setBackground(10, Color.parseColor("#FF0000"));
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_top_tab;
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (null != onMainTabClickListener) {
            boolean isValid;
            switch (v.getId()) {
                case R.id.ivBell:
                    onMainTabClickListener.onDiscoveryClick();
                    break;
                case R.id.tv_discory:
                    isValid = onMainTabClickListener.onDiscoveryTab();
                    if (isValid) {
                        setSelect(1);
                    }
                    break;
                case R.id.tvRecommend:
                    isValid = onMainTabClickListener.onRecommendClick();
                    if (isValid) {
                        setSelect(0);
                    }
                    break;
                case R.id.tvLatest:
                    isValid = onMainTabClickListener.onLatestClick();
                    if (isValid) {
                        setSelect(2);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setSelect(int position) {
//        tvFollow.setTextAppearance(getContext(), position == 0 ? R.style.tv_dd_72 : R.style.tv_aa_72);
//        tvRecommend.setTextAppearance(getContext(), position == 1 ? R.style.tv_dd_72 : R.style.tv_aa_72);
//        tvLatest.setTextAppearance(getContext(), position == 2 ? R.style.tv_dd_72 : R.style.tv_aa_72);
//        tvFollow.getPaint().setFakeBoldText(position == 0);
//        tvRecommend.getPaint().setFakeBoldText(position == 1);
//        tvLatest.getPaint().setFakeBoldText(position == 2);

//        tvFollow.setTextColor(getContext().getResources().getColor(position == 0 ? R.color.hh_color_dd : R.color.hh_color_aa));
        tvRecommend.setTextColor(getContext().getResources().getColor(position == 0 ? R.color.hh_color_dd : R.color.hh_color_aa));
        tv_discory.setTextColor(getContext().getResources().getColor(position == 1 ? R.color.hh_color_dd : R.color.hh_color_aa));
        tvLatest.setTextColor(getContext().getResources().getColor(position == 2 ? R.color.hh_color_dd : R.color.hh_color_aa));
        currentPosition = position;
    }

    public void showLatest(boolean show) {
        tvLatest.setVisibility(show ? VISIBLE : GONE);
        viewRightDot.setVisibility(show ? VISIBLE : GONE);
    }

    public void showBellDot(boolean show) {
        tvMessageCount.setVisibility(show ? VISIBLE : GONE);
    }

    public void showMessageCount(long count) {
        badgeView.setVisibility(count > 0 ? VISIBLE : GONE);
        badgeView.setText(count > 99 ? "···" : String.valueOf(count));
    }

    private OnMainTabClickListener onMainTabClickListener;

    public void setTabClickListener(OnMainTabClickListener onMainTabClickListener) {
        this.onMainTabClickListener = onMainTabClickListener;
    }

    private float minScale = 0.6f;

    public void zoom(int currentScrollY, int allScrollY) {
        float scale;//保证scale的范围在1到0.6之间
        if (allScrollY < 120) {
            scale = 1 - allScrollY / 300f;
        } else {
            scale = minScale;
        }
        llTitleTab.setPivotX(0);
        llTitleTab.setPivotY(titleTabHeight / 2);
        llTitleTab.setScaleX(scale);
        llTitleTab.setScaleY(scale);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) llTitleTab.getLayoutParams();
        int top = (int) (titleTabTopMargin * scale - (1 - scale) * 160);
        KLog.i("=========titleTabTopMargin:" + titleTabTopMargin + " ,top:" + top);
        layoutParams.topMargin = top < 0 ? 0 : top;
        llTitleTab.setLayoutParams(layoutParams);
    }

}
