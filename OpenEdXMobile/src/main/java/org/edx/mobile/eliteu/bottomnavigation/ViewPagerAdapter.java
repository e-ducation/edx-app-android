package org.edx.mobile.eliteu.bottomnavigation;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<FragmentInfo> mFragmentInfos;

    public ViewPagerAdapter(FragmentManager fm, List<FragmentInfo> fragments) {
        super(fm);
        this.mFragmentInfos = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            Class fragment = mFragmentInfos.get(position).getFragment();
            return (Fragment) fragment.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragmentInfos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentInfos.get(position).getTitle();
    }

}
