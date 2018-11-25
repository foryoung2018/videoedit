package com.wmlive.hhvideo.heihei.message.viewholder;

import android.view.View;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * 自己发送的消息不支持
 * Created by admin on 2017/3/28.
 */

public class MeNoHolderViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.tv_im_detail_me_no_holder_content)
    public TextView mTVContent;

    public MeNoHolderViewHolder(View itemView) {
        super(itemView);
    }

    public void setmTVContent(String strContent) {
        mTVContent.setText(strContent);
    }
}
