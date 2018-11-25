package com.wmlive.hhvideo.common.base;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.WeakHandler;
import com.wmlive.hhvideo.widget.dialog.CustomProgressDialog;
import com.wmlive.hhvideo.widget.dialog.LoginDialog;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment基类，不含根布局
 * 当需要获取Fragment是否可见时（包括Fragment的嵌套）,子类请重写{@link #onVisibleChange(int, boolean)}方法
 */
public abstract class BaseFragment<P extends IBasePresenter> extends Fragment
        implements View.OnClickListener, BaseView {

    protected String requestTag = this.getClass().getSimpleName();
    private Set<IBasePresenter> presenterList = new HashSet<>();
    protected static final String LAZY_MODE = "lazy_mode";
    protected static final String SINGLE_MODE = "single_mode";
    protected boolean isLazyMode = true; //是否懒加载，在子类创建的时候传参数 {@param LAZY_MODE} 进行设置
    protected boolean isVisible = true;    //页面是否已经可见  注意：使用FragmentTabHost或者容器中只有一个fragment，setUserVisibleHint()方法不会被调用，所以需要传true给isVisible，可以通过设置SINGLE_MODE为true或者直接设置isVisible为true
    protected boolean isPrepared = false;    //懒加载是否已经准备好
    protected boolean isLoadFinish = false;    //页面是否已经加载完毕
    protected boolean isFirstLoadData = true;    //首次加载数据
    protected long lastClickTime;
    private int lastViewId;
    private boolean isHide = true;
    protected P presenter;   //页面presenter
    protected View rootView; //base根布局
    private Unbinder unbinder;

    private WeakHandler weakHandler;

    /**
     * 情况一、当Fragment嵌套在Fragment中时，调用父Fragment的hide方法进行切换会调用此方法
     *
     * @param hidden true表示不可见
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        KLog.i(requestTag + " onHiddenChanged: ");
        onVisibleChange(0, !hidden);
        needChangeChild(0, !hidden);
    }

    /**
     * 情况二、当父容器是ViewPager时，切换ViewPager会调用此方法
     *
     * @param isVisibleToUser true表示可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        KLog.i(requestTag + " setUserVisibleHint: ");
        if (isAdded()) {
            onVisibleChange(1, isVisibleToUser);
            needChangeChild(1, isVisibleToUser);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        KLog.i(requestTag + " onCreateView: ");
        if (getArguments() != null) {
            isLazyMode = getArguments().getBoolean(LAZY_MODE, false);
            isVisible = getArguments().getBoolean(SINGLE_MODE, false);
        }
        int layoutId = getBaseLayoutId();
        if (layoutId > 0) {
            rootView = inflater.inflate(layoutId, container, false);
        } else {
            rootView = super.onCreateView(inflater, container, savedInstanceState);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KLog.i(requestTag + " onViewCreated: ");
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void onStart() {
        super.onStart();
        KLog.i("=====onStart:" + requestTag);
        for (IBasePresenter presenter : presenterList) {
            if (null != presenter) {
                presenter.start();
            }
        }
    }

    /**
     * 情况三、当Fragment回到屏幕视图顶端时，判断Fragment的可见性
     */
    @Override
    public void onResume() {
        super.onResume();
        boolean visible = getUserVisibleHint();
//        return isAdded() && !isHidden() && getView() != null
//                && getView().getWindowToken() != null && getView().getVisibility() == View.VISIBLE;
        KLog.i("=====Activity on resume,isAdded:" + isAdded()
                + " ,!isHidden:" + (!isHidden())
                + " ,getView!=null:" + (getView() != null)
                + " ,getView().getWindowToken() != null:" + (getView() != null && getView().getWindowToken() != null)
                + " ,getView().getVisibility() == View.VISIBLE:" + (getView() != null && getView().getVisibility() == View.VISIBLE));
        KLog.i("========visible1:" + visible);
        visible = isAdded() && (!isHidden()) && getView() != null && (getView().getVisibility() == View.VISIBLE) && visible;
        KLog.i("========visible2:" + visible);
        visible = visible && isParentVisible();
        KLog.i("========visible3:" + visible);
        onVisibleChange(2, visible);
        KLog.i("=====onResume:" + requestTag);
        for (IBasePresenter presenter : presenterList) {
            if (null != presenter) {
                presenter.resume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        KLog.i("=====onPause:" + requestTag);
        onVisibleChange(2, false);
        for (IBasePresenter presenter : presenterList) {
            if (null != presenter) {
                presenter.pause();
            }
        }
        DeviceUtils.hiddenKeyBoard(getActivity().findViewById(android.R.id.content));
    }

    @Override
    public void onStop() {
        super.onStop();
        KLog.i("=====onStop:" + requestTag);
        for (IBasePresenter presenter : presenterList) {
            if (null != presenter) {
                presenter.stop();
            }
        }
    }

    @Override
    public void onDestroy() {
        KLog.i("=====onDestroy:" + requestTag);
        Iterator<IBasePresenter> iterator = presenterList.iterator();
        while (iterator.hasNext()) {
            iterator.next().destroy();
            iterator.remove();
        }
        if (null != unbinder) {
            unbinder.unbind();
            unbinder = null;
        }
        if (null != weakHandler) {
            weakHandler.removeCallbacksAndMessages(null);
            weakHandler = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissLoad();
        KLog.i("======onDestroyView:" + requestTag);
        ((BaseCompatActivity) getActivity()).removeWeakHandler();
        if (rootView != null) {
            ViewGroup parent = ((ViewGroup) rootView.getParent());
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
    }

    /**
     * 如果是嵌套在Fragment中，判断父Fragment是否可见
     * 注意：父Fragment需要使用getChildFragmentManager方法进行fragment的add操作，否则getParentFragment()将获取到null
     *
     * @return
     */
    private boolean isParentVisible() {
        Fragment parent = getParentFragment();//这里获取到父Fragment，父Fragment需要使用getChildFragmentManager方法进行fragment的add操作
        boolean parentVisible = true;
        if (parent != null && parent instanceof BaseFragment) {
            parentVisible = ((BaseFragment) parent).getVisible();
        }
        return parentVisible;
    }

    /**
     * 判断Fragment是否嵌套了子Fragment,如果嵌套了，则通知子Fragment是否可见
     *
     * @param type    被调用方法类型，0：onHiddenChanged     1：setUserVisibleHint    2：onResume或者onPause
     * @param visible true表示可见
     */
    private void needChangeChild(int type, boolean visible) {
        if (isAdded() && getChildFragmentManager() != null) {
            List<Fragment> childFragments = getChildFragmentManager().getFragments();
            if (childFragments != null && childFragments.size() > 0) {
                for (Fragment childFragment : childFragments) {
                    if (childFragment instanceof BaseFragment) {
                        ((BaseFragment) childFragment).onVisibleChange(type, visible
                                && childFragment.getUserVisibleHint()
                                && childFragment.isVisible());
                    }
                }
            }
        }
    }

    /**
     * Fragment可见性发生变化时会调用此方法
     *
     * @param type    被调用方法类型，0：onHiddenChanged     1：setUserVisibleHint    2：onResume或者onPause
     * @param visible true表示可见
     */
    public void onVisibleChange(int type, boolean visible) {
        KLog.i(requestTag + " onVisibleChange: " + (visible ? "可见" : "不可见"));
        isVisible = visible;
        lazyLoad();
    }

    protected boolean getVisible() {
        return isVisible;
    }

    /**
     * 是否懒加载
     */
    protected void lazyLoad() {
        if (isLazyMode && (!isVisible || !isPrepared)) {
            return;
        }
        if (isPrepared) {
            loadAll();
        }
    }

    protected void loadAll() {
        if (!isLoadFinish) {
            KLog.i(requestTag + "====loadAll");
            presenter = getPresenter();
            addPresenter(presenter);
            initBaseView();
            unbinder = ButterKnife.bind(this, rootView);
            initData();
            isLoadFinish = true;
        }
    }

    /**
     * 获取一个WeakHandler
     *
     * @return
     */
    protected WeakHandler getWeakHandler() {
        if (weakHandler == null) {
            weakHandler = new WeakHandler();
        }
        return weakHandler;
    }

    /**
     * 添加Presenter
     *
     * @param presenters
     */
    protected void addPresenter(IBasePresenter... presenters) {
        for (IBasePresenter presenter : presenters) {
            if (null != presenter) {
                presenter.bindContext(getContext());
                presenterList.add(presenter);
            }
        }
    }

    public boolean isLoadFinish() {
        return isLoadFinish;
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 初始化presenter，只针对泛型方式传入的Presenter
     *
     * @return
     */
    protected P getPresenter() {
        return null;
    }

    /**
     * 获取页面的布局文件
     *
     * @return
     */
    protected abstract int getBaseLayoutId();

    /**
     * 初始化基础布局，Base之间的继承才调用这个方法，其他情况不用
     */
    void initBaseView() {
    }

    /**
     * view的点击事件都在此方法中处理
     *
     * @param v
     */
    protected abstract void onSingleClick(View v);

    /**
     * 控制statusbar和toolbar的显示
     *
     * @param status 0：正常显示toolbar和statubar
     *               1：隐藏toolbar，显示statubar
     *               2：隐藏toolbar和statubar
     *               3：显示toolbar，内容沉浸到statubar（5.0以上有效）
     *               4：隐藏toolbar，内容沉浸到statubar（5.0以上有效）
     */
    protected void changeDecorView(int status) {
        ((BaseCompatActivity) getActivity()).changeDecorView(status);
    }

    /**
     * 特殊情况需要findView
     *
     * @param id
     * @return
     */
    protected View findViewById(int id) {
        return rootView.findViewById(id);
    }

    protected void showToast(String message) {
        if (getActivity() != null) {
            ((BaseCompatActivity) getActivity()).showToast(message);
        }
    }

    protected void showToast(int stringId) {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).showToast(stringId);
        }
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if ((lastViewId == v.getId())) {
            if (currentTime - lastClickTime > GlobalParams.Config.MINIMUM_CLICK_DELAY) {
                onSingleClick(v);
            } else {
                KLog.i(requestTag, "快速点击无效");
            }
        } else {
            onSingleClick(v);
        }
        lastViewId = v.getId();
        lastClickTime = currentTime;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ((BaseCompatActivity) getActivity()).onBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void startActivity(String intentName) {
        ((BaseCompatActivity) getActivity()).startActivity(intentName);
    }

    protected String getResString(int resId) {
        return getContext().getResources().getString(resId);
    }

    /**
     * 请求的返回结果错误
     *
     * @param requestCode
     * @param message
     */
    @Override
    public void onRequestDataError(int requestCode, String message) {
        showToast(message);
    }

    public LoginDialog showReLogin() {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            return ((BaseCompatActivity) getActivity()).showReLogin();
        }
        return null;
    }

    public void loading() {
        if (getActivity() != null) {
            ((BaseCompatActivity) getActivity()).loading();
        }
    }

    public CustomProgressDialog loading(boolean cancelable, DialogInterface.OnDismissListener listener) {
        if (getActivity() != null) {
            return ((BaseCompatActivity) getActivity()).loading(cancelable, listener);
        }
        return null;
    }

    public void dismissLoad() {
        if (getActivity() != null) {
            ((BaseCompatActivity) getActivity()).dismissLoad();
        }
    }

    public void wechatLogin() {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).wechatLogin();
        }
    }

    public void wechatShare(int type, ShareInfo shareInfo) {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).wechatShare(type, shareInfo);
        }
    }

    public void wxMinAppShare(int type, ShareInfo shareInfo, Bitmap bitmap){
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).wxMinAppShare(type, shareInfo,bitmap);
        }
    }

    public void weiboJump(String id) {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).weiboJump(id);
        }
    }

    public void weiboLogin() {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).weiboLogin();
        }
    }

    public void weiboShare(ShareInfo shareInfo) {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).weiboShare(shareInfo);
        }
    }

    public void qqLogin() {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).qqLogin();
        }
    }

    public void qqShare(ShareInfo shareInfo) {
        if (getActivity() != null && getActivity() instanceof BaseCompatActivity) {
            ((BaseCompatActivity) getActivity()).qqShare(shareInfo);
        }
    }

}
