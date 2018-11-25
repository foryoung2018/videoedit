package com.wmlive.hhvideo.common.base;


import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.widget.AppToolbar;
import com.wmlive.hhvideo.widget.StatusView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/27/2017.
 * DcBaseFragment，含根布局(包括Toolbar和Content)
 * 大部分页面都继承自这个类，如果有自己特定的布局，可以直接继承{@link BaseFragment}
 */
public abstract class DcBaseFragment<P extends IBasePresenter> extends BaseFragment<P> implements
        BaseView, StatusView.OptionClickListener {

    protected RelativeLayout rlRootView;  //内容布局
    protected FrameLayout contentView;  //内容布局
    protected AppToolbar toolbar;
    protected AppBarLayout appBarLayout;
    private StatusView statusView;  //页面状态view

    private boolean isRelativeMode;  //是否是toolbar与contentView是叠加模式，true可实现toolbar渐变透明度效果

    @Override
    protected int getBaseLayoutId() {
        return R.layout.view_base_layout;
    }

    @Override
    void initBaseView() {
        super.initBaseView();
        rlRootView = (RelativeLayout) findViewById(R.id.rlRootView);
        if (rootView != null) {
            toolbar = rootView.findViewById(R.id.toolbar);
            contentView = (FrameLayout) rootView.findViewById(R.id.fl_page_container);
        }
        if (contentView != null) {
            View view = null;
            int layoutId = getLayoutId();
            if (layoutId > 0) {
                view = LayoutInflater.from(getActivity()).inflate(layoutId, null, false);
            }
            if (view != null) {
                contentView.addView(view);
            }
        } else {
            setRelativeMode(true);
        }
        String t = setTitle();
        if (!TextUtils.isEmpty(t)) {
            initToolbar(t, false, 0);
        }
    }

    /**
     * 添加主布局文件
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化Toolbar，子类请勿调用此方法
     *
     * @param title
     * @param showBack
     */
    final void initToolbar(String title, boolean showBack, int resId) {
        if (!TextUtils.isEmpty(title) || showBack) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle("");
            setHasOptionsMenu(true);
            if (!isRelativeMode && contentView != null) {
                RelativeLayout.LayoutParams layoutParams =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
                contentView.setLayoutParams(layoutParams);
            }
            toolbar.setCenterTitle(title);
            if (resId != 0) {
                toolbar.setLogo(resId);
            } else {
                ImageView ivBack = (ImageView) toolbar.findViewById(R.id.ivBack);
                ivBack.setVisibility(showBack ? View.VISIBLE : View.GONE);
                ivBack.setOnClickListener(new MyClickListener() {
                    @Override
                    protected void onMyClick(View v) {
                        onBack();
                    }
                });
            }
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    protected void onBack() {
        ((BaseCompatActivity) getActivity()).onBack();
    }

    /**
     * 设置Title，如果不设置或者返回null将不会显示toolbar
     */
    protected String setTitle() {
        return null;
    }

    /**
     * 设置Title
     */
    protected void setTitle(String title, boolean showBack) {
        initToolbar(title, showBack, 0);
    }

    /**
     * 设置Title
     *
     * @param resId    title字符串id
     * @param showBack 是否显示返回箭头
     */
    protected void setTitle(int resId, boolean showBack) {
        initToolbar(getString(resId), showBack, 0);
    }

    /**
     * toolbar左侧添加布局
     *
     * @param layoutId
     * @param listener
     * @return
     */
    private View setToolbarView(@LayoutRes int layoutId, View.OnClickListener listener, boolean left) {
        View view = LayoutInflater.from(getActivity()).inflate(layoutId, null);
        if (toolbar != null && view != null) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle("");
            setHasOptionsMenu(true);
            return left ? toolbar.addLeftView(view, listener) : toolbar.addCenterView(view, listener);
        }
        return null;
    }

    public View setToolbarLeftView(@LayoutRes int layoutId, View.OnClickListener listener) {
        return setToolbarView(layoutId, listener, true);
    }

    public View setToolbarCenterView(@LayoutRes int layoutId, View.OnClickListener listener) {
        return setToolbarView(layoutId, listener, false);
    }

    /**
     * 显示其他非数据页面
     * 若不使用base布局，则需保证自己的布局中有Framelayout,切id是R.id.fl_page_container
     *
     * @param status  0:数据正常,1:正在加载,2:页面为空,3:页面出错
     * @param message
     */
    protected void showStatusPage(int status, String message) {
        if (contentView != null) {
            if (statusView == null) {
                statusView = StatusView.createStatusView(getActivity()).setOptionClickListener(this);
                contentView.addView(statusView);
            }
            statusView.showStatusPage(status, message);
            if (status == StatusView.STATUS_NORMAL) {
                contentView.removeView(statusView);
                statusView = null;
            }
        }
    }


    /**
     * 是否是toolbar与contentView是叠加模式
     * 注意：如果为true，请勿重写{@link #setTitle()}方法，
     * 此方法需要在{@link #setTitle(int, boolean)} 或者{@link #setTitle(String, boolean)}之前调用!!!
     *
     * @param relativeMode
     */
    public void setRelativeMode(boolean relativeMode) {
        isRelativeMode = relativeMode;
    }


    /**
     * 如果StatusView中有需要处理的点击事件
     *
     * @param view
     */
    @Override
    public void onOptionClick(View view) {

    }

    /**
     * 获取根布局
     *
     * @return
     */
    public RelativeLayout getRootView() {
        return rlRootView;
    }

}