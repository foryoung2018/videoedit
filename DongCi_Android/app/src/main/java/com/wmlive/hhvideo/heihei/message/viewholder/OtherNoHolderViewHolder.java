package com.wmlive.hhvideo.heihei.message.viewholder;

import android.view.View;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * 接收到不支持的数据信息
 * Created by admin on 2017/3/28.
 */

public class OtherNoHolderViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.tv_im_detail_other_no_holder_content)
    public TextView mTvContent;

    public OtherNoHolderViewHolder(View itemView) {
        super(itemView);
    }

    public void setmTvContent(String mTvContent) {
        this.mTvContent.setText(mTvContent);
    }
}
