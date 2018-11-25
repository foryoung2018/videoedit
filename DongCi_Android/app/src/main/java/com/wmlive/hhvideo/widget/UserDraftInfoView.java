package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 11/30/2017.6:11 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class UserDraftInfoView extends BaseCustomView {
    @BindView(R.id.tvDraftCount)
    TextView tvDraftCount;

    public UserDraftInfoView(Context context) {
        super(context);
    }

    public UserDraftInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserDraftInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_draft_info;
    }

}
