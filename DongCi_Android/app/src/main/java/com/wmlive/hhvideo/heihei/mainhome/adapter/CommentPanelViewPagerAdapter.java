package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wmlive.hhvideo.common.base.DcBaseFragment;

import java.util.List;

/**
 * Created by lsq on 5/2/2018 - 3:55 PM
 * 类描述：
 */
public class CommentPanelViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<DcBaseFragment> fragmentList;

    public CommentPanelViewPagerAdapter(FragmentManager fm, List<DcBaseFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
}
