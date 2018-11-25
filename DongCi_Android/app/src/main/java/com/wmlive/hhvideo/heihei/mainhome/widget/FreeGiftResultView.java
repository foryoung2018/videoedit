package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.gifts.GiftRebateEntity;
import com.wmlive.hhvideo.heihei.beans.gifts.RebateEntity;
import com.wmlive.hhvideo.utils.CollectionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 1/16/2018.5:11 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class FreeGiftResultView extends RelativeLayout {
    @BindView(R.id.tvClickCount)
    TextView tvClickCount;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvDiamondCount)
    TextView tvDiamondCount;
    @BindView(R.id.tvDecibelCount1)
    TextView tvDecibelCount1;

    public FreeGiftResultView(Context context) {
        super(context);
    }

    public FreeGiftResultView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FreeGiftResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setData(GiftRebateEntity giftRebateEntity) {
        if (giftRebateEntity != null) {
            int decibelCount = 0;
            int goldCount = 0;
            if (!CollectionUtil.isEmpty(giftRebateEntity.prize_message)) {
                for (RebateEntity rebateEntity : giftRebateEntity.prize_message) {
                    if (rebateEntity != null) {
                        decibelCount += rebateEntity.getDecibelRebateCount();
                        goldCount += rebateEntity.getGoldRebateCount();
                    }
                }
            }
            tvDiamondCount.setText("X" + goldCount);
            tvDecibelCount1.setText("X" + decibelCount);
            tvClickCount.setText(giftRebateEntity.title);
            tvDesc.setText(giftRebateEntity.description);
        }
    }
}
