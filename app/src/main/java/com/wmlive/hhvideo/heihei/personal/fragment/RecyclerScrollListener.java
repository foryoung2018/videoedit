package com.wmlive.hhvideo.heihei.personal.fragment;

/**
 * Created by XueFei on 2017/6/19.
 * <p>
 * 个人页--他人页--账户 切换tab时滑动处理
 */

public interface RecyclerScrollListener {
    void Scrolled(int distance, int pagePosition);

    void adjustScroll(int scrollHeight, int headerHeight);
}
