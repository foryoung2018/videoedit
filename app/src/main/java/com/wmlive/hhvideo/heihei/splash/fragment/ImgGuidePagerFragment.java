package com.wmlive.hhvideo.heihei.splash.fragment;


import android.view.View;
import android.widget.ImageView;
import com.wmlive.hhvideo.common.base.BaseFragment;
import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 图片引导页
 */
public class ImgGuidePagerFragment extends BaseFragment {
    @BindView(R.id.guide_iv)
    ImageView guideIv;

    @Override
    protected void initData() {
        super.initData();
        if (getArguments() == null) {
            return;
        }
        int imgRes = getArguments().getInt("res");
        guideIv.setImageResource(imgRes);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int getBaseLayoutId() {
        return R.layout.fragment_img_guide_pager;
    }

    @Override
    protected void onSingleClick(View v) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
