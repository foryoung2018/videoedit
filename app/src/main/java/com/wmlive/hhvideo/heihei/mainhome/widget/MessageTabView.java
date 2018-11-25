package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.BadgeView;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 2/8/2018.10:51 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MessageTabView extends BaseCustomView {

    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.rlIcon)
    RelativeLayout rlIcon;
    @BindView(R.id.viewBottomLine)
    View viewBottomLine;
    private BadgeView badgeView;

    public MessageTabView(Context context) {
        super(context);
    }

    public MessageTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        badgeView = new BadgeView(getContext());
        badgeView.setTargetView(rlIcon);
        badgeView.setBadgeGravity(Gravity.TOP | Gravity.END);
        badgeView.setBadgeMargin(0, 2, 8, 0);
        badgeView.setBackground(10, Color.parseColor("#FF0000"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_message_tab;
    }

    public void setData(int stringId, int iconId) {
        tvName.setText(getResources().getString(stringId));
//        ivIcon.setImageResource(iconId);
        ivIcon.setVisibility(View.GONE);
    }

    public void setSelected(boolean selected, int iconId, int colorId) {
        ivIcon.setImageResource(iconId);
        tvName.setTextColor(getResources().getColor(colorId));
        viewBottomLine.setVisibility(selected ? VISIBLE : INVISIBLE);
    }

    public void setMessageCount(long count) {
        badgeView.setVisibility(count > 0 ? VISIBLE : GONE);
        if (count > 99) {
            badgeView.setText("···");
        } else {
            badgeView.setText(String.valueOf(count));
        }
    }

}
