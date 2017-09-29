package com.bsmwireless.screens.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bsmwireless.screens.dashboard.DashboardFragment;
import com.bsmwireless.screens.logs.LogsFragment;
import com.bsmwireless.screens.multiday.MultidayFragment;

import app.bsmuniversal.com.R;

public final class NavigationAdapter extends FragmentStatePagerAdapter {
    private final static int MAX_PAGES = 3;

    private Context mContext;

    public NavigationAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        //TODO: return actual fragments
        switch (position) {
            case 0:
                return new DashboardFragment();
            case 1:
                return new LogsFragment();
            default:
                return new MultidayFragment();
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
                return mContext.getString(R.string.navigation_dashboard);
            case 1:
                return mContext.getString(R.string.navigation_logs);
            default:
                return mContext.getString(R.string.navigation_multi_day);
        }
    }
}
