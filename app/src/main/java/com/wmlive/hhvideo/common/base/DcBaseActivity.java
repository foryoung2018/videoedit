package com.wmlive.hhvideo.common.base;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.widget.AppToolbar;
import com.wmlive.hhvideo.widget.StatusView;

import java.util.List;

import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

/**
 * Created by vhawk on 2017/5/19.
 * Modify by lsq
 * DcBaseFragment，含根布局(包括Toolbar和Content)
 * 大部分页面都继承自这个类，如果有自己特定的布局，可以直接继承{@link BaseCompatActivity}
 */

public abstract class DcBaseActivity<P extends IBasePresenter> extends BaseCompatActivity<P> implements
        StatusView.OptionClickListener {

    protected RelativeLayout rlRootView;
    protected FrameLayout contentView;
    protected AppToolbar toolbar;  //不要在自己的布局文件中加入toolbar
    private StatusView statusView;
    private boolean isRelativeMode;  //是否是toolbar与contentView是叠加模式，true可实现toolbar渐变透明度效果
    private int pageStatus = StatusView.STATUS_NORMAL;
    private ImageView ivBack;

    @Override
    void initBaseView() {
        super.initBaseView();
        getDelegate().setContentView(R.layout.view_base_layout);
        rlRootView = findViewById(R.id.rlRootView);
        toolbar = findViewById(R.id.toolbar);
        contentView = findViewById(R.id.fl_page_container);
        setContentView(getLayoutResId());
        String t = setTitle();
        if (!TextUtils.isEmpty(t)) {
            initToolbar(t, true, 0);
        }
        changeDecorView(0);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    public void setBlackToolbar() {
        if (toolbar != null) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_bar));
            toolbar.setCenterTitleColor(getResources().getColor(R.color.white));
            if (ivBack != null) {
                ivBack.setImageResource(R.drawable.icon_back_white);
            }
        }
    }

    /**
     * 初始化Toolbar
     *
     * @param title    标题
     * @param showBack 是否显示返回箭头
     * @param resId    logo
     */
    final void initToolbar(String title, boolean showBack, int resId) {
        if (!TextUtils.isEmpty(title) || showBack) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle("");
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
                ivBack = (ImageView) toolbar.findViewById(R.id.ivBack);
                if (ivBack != null) {
                    ivBack.setVisibility(showBack ? View.VISIBLE : View.GONE);
                    ivBack.setOnClickListener(new MyClickListener() {
                        @Override
                        protected void onMyClick(View v) {
                            onBack();
                        }
                    });
                }
            }
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void changeDecorView(int status) {
        super.changeDecorView(status);
        if (status == 3) {
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            l.topMargin = DeviceUtils.getStatusBarHeight(this);
        }
    }

    public void showBack(boolean show) {
        if (ivBack != null) {
            ivBack.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
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
     * 设置Title，如果不设置或者返回null将不会显示toolbar
     */
    protected String setTitle() {
        return null;
    }

    /**
     * 设置title
     *
     * @param title    title字符串
     * @param showBack 是否显示返回箭头
     */
    protected void setTitle(String title, boolean showBack) {
        initToolbar(title, showBack, 0);
    }

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
    public View setToolbarLeftView(@LayoutRes int layoutId, View.OnClickListener listener) {
        View view = LayoutInflater.from(this).inflate(layoutId, null);
        if (toolbar != null && view != null) {
            return toolbar.addLeftView(view, listener);
        }
        return null;
    }

    public View setToolbarLeftView(View view, View.OnClickListener listener) {
        if (toolbar != null && view != null) {
            return toolbar.addLeftView(view, listener);
        }
        return null;
    }

    public View setToolbarRightView(@LayoutRes int layoutId, View.OnClickListener listener) {
        View view = LayoutInflater.from(this).inflate(layoutId, null);
        if (toolbar != null && view != null) {
            toolbar.setVisibility(View.VISIBLE);
            ivBack = (ImageView) toolbar.findViewById(R.id.ivBack);
            ivBack.setVisibility(View.GONE);
            return toolbar.addRightView(view, listener);
        }
        return null;
    }

    public View setToolbarRightView(View view, View.OnClickListener listener) {
        if (toolbar != null && view != null) {
            return toolbar.addRightView(view, listener);
        }
        return null;
    }

    public View setToolbarCenterView(View view, View.OnClickListener listener) {
        if (toolbar != null && view != null) {
            return toolbar.addCenterView(view, listener);
        }
        return null;
    }

    /*设置布局*/
    @Override
    public void setContentView(int layoutResID) {
        if (layoutResID != 0) {
            if (contentView != null) {
                contentView.removeAllViews();
                getLayoutInflater().inflate(layoutResID, contentView);
            } else {
                getDelegate().setContentView(layoutResID);
                toolbar = (AppToolbar) findViewById(R.id.toolbar);
            }
            unbinder = ButterKnife.bind(this);
        }
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
                statusView = StatusView.createStatusView(this).setOptionClickListener(this);
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
     * 判断某一个类是否存在任务栈里面
     *
     * @return
     */
    protected boolean isExistActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        ComponentName cmpName = intent.resolveActivity(getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break;  //跳出循环，优化效率
                }
            }
        }
        return flag;
    }


    /**
     * 获取根布局
     *
     * @return
     */
    public RelativeLayout getRootView() {
        return rlRootView;
    }

    /**
     * StatusView中的点击事件
     *
     * @param view
     */
    @Override
    public void onOptionClick(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();




    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
