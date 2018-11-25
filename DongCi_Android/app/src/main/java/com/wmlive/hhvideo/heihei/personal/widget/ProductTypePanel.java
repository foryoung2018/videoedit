package com.wmlive.hhvideo.heihei.personal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.widget.BaseCustomView;
import com.wmlive.hhvideo.widget.CustomFontTextView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 10/12/2017.
 */

public class ProductTypePanel extends BaseCustomView {
    @BindView(R.id.tvProductCount)
    CustomFontTextView tvProductCount;
    @BindView(R.id.llProduct)
    LinearLayout llProduct;
    @BindView(R.id.tvTogetherCount)
    CustomFontTextView tvTogetherCount;
    @BindView(R.id.llTogether)
    LinearLayout llTogether;
    @BindView(R.id.tvLikeCount)
    CustomFontTextView tvLikeCount;

    @BindView(R.id.tvProductCountLabel)
    TextView tvProductCountLabel;
    @BindView(R.id.tvTogetherCountLabel)
    TextView tvTogetherCountLabel;
    @BindView(R.id.tvLikeCountLabel)
    TextView tvLikeCountLabel;

    @BindView(R.id.llLike)
    LinearLayout llLike;
    @BindView(R.id.ivProduct)
    ImageView ivProduct;
    @BindView(R.id.ivTogether)
    ImageView ivTogether;
    @BindView(R.id.ivLike)
    ImageView ivLike;
    @BindView(R.id.viewProductLine)
    View viewProductLine;
    @BindView(R.id.viewTogetherLine)
    View viewTogetherLine;
    @BindView(R.id.viewLikeLine)
    View viewLikeLine;
    private OnTypeClickListener onTypeClickListener;
    public static final int TYPE_PRODUCT = 100;
    public static final int TYPE_TOGETHER = 200;
    public static final int TYPE_LIKE = 300;

    public ProductTypePanel(Context context) {
        super(context);
    }

    public ProductTypePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        llProduct.setOnClickListener(this);
        llTogether.setOnClickListener(this);
        llLike.setOnClickListener(this);
    }

    public void initData(int productCount, int togetherCount, int likeCount) {
        tvProductCount.setText(String.valueOf(productCount));
        tvTogetherCount.setText(String.valueOf(togetherCount));
        tvLikeCount.setText(CommonUtils.getCountString(likeCount));
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (onTypeClickListener != null) {
            int index = TYPE_PRODUCT;
            switch (v.getId()) {
                case R.id.llProduct:
                    index = TYPE_PRODUCT;
                    break;
                case R.id.llTogether:
                    index = TYPE_TOGETHER;
                    break;
                case R.id.llLike:
                    index = TYPE_LIKE;
                    break;
                default:
                    break;
            }
            selectItem(index);
            onTypeClickListener.onTypeClick(index);
        }
    }

    public void selectItem(int index) {
        selectItem(index, false);
    }

    public void selectItem(int index, boolean click) {
        llProduct.setSelected(index == TYPE_PRODUCT);
        llTogether.setSelected(index == TYPE_TOGETHER);
        llLike.setSelected(index == TYPE_LIKE);

        ivProduct.setImageResource(index == TYPE_PRODUCT ? R.drawable.icon_home_view : R.drawable.icon_profile_shot_nor);
        ivTogether.setImageResource(index == TYPE_TOGETHER ? R.drawable.icon_profile_create : R.drawable.icon_profile_create_nor);
        ivLike.setImageResource(index == TYPE_LIKE ? R.drawable.icon_like_small : R.drawable.icon_profile_like_nor);

        viewProductLine.setVisibility(index == TYPE_PRODUCT ? VISIBLE : INVISIBLE);
        viewTogetherLine.setVisibility(index == TYPE_TOGETHER ? VISIBLE : INVISIBLE);
        viewLikeLine.setVisibility(index == TYPE_LIKE ? VISIBLE : INVISIBLE);

        tvProductCount.setTextColor(getContext().getResources().getColor(index == TYPE_PRODUCT ? R.color.hh_color_dd : R.color.hh_color_cc));
        tvProductCountLabel.setTextColor(getContext().getResources().getColor(index == TYPE_PRODUCT ? R.color.hh_color_dd : R.color.hh_color_cc));
        tvTogetherCount.setTextColor(getContext().getResources().getColor(index == TYPE_TOGETHER ? R.color.hh_color_dd : R.color.hh_color_cc));
        tvTogetherCountLabel.setTextColor(getContext().getResources().getColor(index == TYPE_TOGETHER ? R.color.hh_color_dd : R.color.hh_color_cc));
        tvLikeCount.setTextColor(getContext().getResources().getColor(index == TYPE_LIKE ? R.color.hh_color_dd : R.color.hh_color_cc));
        tvLikeCountLabel.setTextColor(getContext().getResources().getColor(index == TYPE_LIKE ? R.color.hh_color_dd : R.color.hh_color_cc));
        if (click && onTypeClickListener != null) {
            onTypeClickListener.onTypeClick(index);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_product_type;
    }

    public void setOnTypeClickListener(OnTypeClickListener onTypeClickListener) {
        this.onTypeClickListener = onTypeClickListener;
    }

    public interface OnTypeClickListener {
        void onTypeClick(int index);
    }
}
