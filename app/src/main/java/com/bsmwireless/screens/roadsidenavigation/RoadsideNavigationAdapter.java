package com.bsmwireless.screens.roadsidenavigation;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bsmwireless.screens.roadside.RoadsideFragment;
import com.bsmwireless.screens.roadsidehistory.RoadsideHistoryFragment;

import app.bsmuniversal.com.R;

public final class RoadsideNavigationAdapter extends FragmentStatePagerAdapter {
    private final static int MAX_PAGES = 2;

    private Context mContext;

    public RoadsideNavigationAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RoadsideFragment();
            default:
                return new RoadsideHistoryFragment();
        }
    }

    @Override
    public int getCount() {
        return MAX_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.roadside_title);
            default:
                return mContext.getString(R.string.roadside_history);
        }
    }
}
