package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.wmlive.hhvideo.common.base.BaseFragment;

import java.util.List;

/**
 * Created by lsq on 7/4/2017.
 */

public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<BaseFragment> fragmentList;

    public HomeViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        fragmentList = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
