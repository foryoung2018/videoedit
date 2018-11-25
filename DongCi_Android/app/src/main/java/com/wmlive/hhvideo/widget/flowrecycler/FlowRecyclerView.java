package com.wmlive.hhvideo.widget.flowrecycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lsq on 1/8/2018.5:31 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class FlowRecyclerView extends RecyclerView {
    /**
     * 按下的X轴坐标
     */
    private float mDownX;
    private float mDownY;
    private FlowLayoutManager.Builder mManagerBuilder;

    public FlowRecyclerView(Context context) {
        super(context);
        init();
    }

    public FlowRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlowRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        createManageBuilder();
        setLayoutManager(mManagerBuilder.build());
        setChildrenDrawingOrderEnabled(true); //开启重新排序
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    /**
     * 创建布局构建器
     */
    private void createManageBuilder() {
        if (mManagerBuilder == null) {
            mManagerBuilder = new FlowLayoutManager.Builder();
            mManagerBuilder.setAlphaItem(true);
        }
    }

    /**
     * 设置是否为普通平面滚动
     *
     * @param isFlat true:平面滚动；false:叠加缩放滚动
     */
    public void setFlatFlow(boolean isFlat) {
        createManageBuilder();
        mManagerBuilder.setFlat(isFlat);
        setLayoutManager(mManagerBuilder.build());
    }

    /**
     * 设置Item灰度渐变
     *
     * @param greyItem true:Item灰度渐变；false:Item灰度不变
     */
    public void setGreyItem(boolean greyItem) {
        createManageBuilder();
        mManagerBuilder.setGreyItem(greyItem);
        setLayoutManager(mManagerBuilder.build());
    }

    /**
     * 设置Item灰度渐变
     *
     * @param alphaItem true:Item半透渐变；false:Item透明度不变
     */
    public void setAlphaItem(boolean alphaItem) {
        createManageBuilder();
        mManagerBuilder.setAlphaItem(alphaItem);
        setLayoutManager(mManagerBuilder.build());
    }

    /**
     * 设置Item的间隔比例
     *
     * @param intervalRatio Item间隔比例。
     *                      即：item的宽 x intervalRatio
     */
    public void setIntervalRatio(float intervalRatio) {
        createManageBuilder();
        mManagerBuilder.setIntervalRatio(intervalRatio);
        setLayoutManager(mManagerBuilder.build());
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof FlowLayoutManager)) {
            throw new IllegalArgumentException("The layout manager must be CoverFlowLayoutManger");
        }
        super.setLayoutManager(layout);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int center = getCoverFlowLayout().getCenterPosition()
                - getCoverFlowLayout().getFirstVisiblePosition(); //计算正在显示的所有Item的中间位置
        if (center < 0) center = 0;
        else if (center > childCount) center = childCount;
        int order;
        if (i == center) {
            order = childCount - 1;
        } else if (i > center) {
            order = center + childCount - 1 - i;
        } else {
            order = i;
        }
        return order;
    }

    /**
     * 获取LayoutManger，并强制转换为CoverFlowLayoutManger
     */
    public FlowLayoutManager getCoverFlowLayout() {
        return ((FlowLayoutManager) getLayoutManager());
    }

    /**
     * 获取被选中的Item位置
     */
    public int getSelectedPos() {
        return getCoverFlowLayout().getSelectedPos();
    }

    /**
     * 设置选中监听
     *
     * @param l 监听接口
     */
    public void setOnItemSelectedListener(FlowLayoutManager.OnSelected l) {
        getCoverFlowLayout().setOnSelectedListener(l);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true); //设置父类不拦截滑动事件
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mDownY - ev.getY()) > Math.abs(mDownX - ev.getX())) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if ((ev.getX() > mDownX && getCoverFlowLayout().getCenterPosition() == 0) ||
                        (ev.getX() < mDownX && getCoverFlowLayout().getCenterPosition() ==
                                getCoverFlowLayout().getItemCount() - 1)) {
                    //如果是滑动到了最前和最后，开放父类滑动事件拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    //滑动到中间，设置父类不拦截滑动事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public FlowLayoutManager getFlowLayoutManager() {
        if (!(getLayoutManager() instanceof FlowLayoutManager)) {
            throw new IllegalArgumentException("The layout manager must be CoverFlowLayoutManger");
        }
        return (FlowLayoutManager) getLayoutManager();
    }
}
