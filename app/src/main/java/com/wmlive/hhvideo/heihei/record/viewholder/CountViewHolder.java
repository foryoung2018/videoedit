package com.wmlive.hhvideo.heihei.record.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;
import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * Created by lsq on 8/28/2017.
 * 倒计时选择列表item
 */

public class CountViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.count_tv)
    public CustomFontTextView countTv;
    @BindView(R.id.seconds_tv)
    public CustomFontTextView secondsTv;
    @BindView(R.id.indicator)
    public TextView indicator;
    public CountViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
