package com.wmlive.hhvideo.heihei.personal.adapter;

/**
 * Created by XueFei on 2017/5/27.
 * <p>
 * 用来给viewpager提供fragment的adapter
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;

import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.heihei.personal.fragment.RecyclerScrollListener;

import java.util.ArrayList;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<DcBaseFragment> fragmentsList;

    private SparseArrayCompat<RecyclerScrollListener> mScrollTabHolders;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<DcBaseFragment> fragments) {
        super(fm);
        mScrollTabHolders = new SparseArrayCompat<>();
        this.fragmentsList = fragments;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        mScrollTabHolders.put(position, (RecyclerScrollListener) object);
        return object;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (fragmentsList != null) {
            size = fragmentsList.size();
        }
        return size;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public SparseArrayCompat<RecyclerScrollListener> getScrollTabHolders() {
        return mScrollTabHolders;
    }

}
