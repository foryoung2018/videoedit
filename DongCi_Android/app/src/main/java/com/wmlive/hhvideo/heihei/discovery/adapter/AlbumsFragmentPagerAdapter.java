package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wmlive.hhvideo.common.base.BaseFragment;

import java.util.List;

/**
 * Author：create by jht on 2018/9/19 14:46
 * Email：haitian.jiang@welines.cn
 */
public class AlbumsFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<String> mTitleList;
    private List<BaseFragment> mFragmentList;

    public AlbumsFragmentPagerAdapter(FragmentManager fm, List<String> titleList, List<BaseFragment> fragmentList) {
        super(fm);
        this.mTitleList = titleList;
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
