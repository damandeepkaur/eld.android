package com.bsmwireless.screens.carrieredit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsView;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsView;

import javax.inject.Inject;

import app.bsmuniversal.com.R;

/**
 * Created by osminin on 22.09.2017.
 */

public final class CarrierEditAdapter extends FragmentStatePagerAdapter {
    private static final int PAGE_COUNT = 2;

    @Inject
    EditedEventsView mEditedEventsView;
    @Inject
    UnassignedEventsView mUnassignedEventsView;

    CarrierEditView mView;

    public CarrierEditAdapter(CarrierEditView view) {
        super(view.getSupportFragmentManager());
        mView = view;
        mView.getComponent().inject(this);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment currentFragment = null;
        switch (position) {
            case 0:
                currentFragment = (Fragment) mUnassignedEventsView;
                break;
            case 1:
                currentFragment = (Fragment) mEditedEventsView;
                break;
        }
        return currentFragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mView.getString(R.string.unassigned_events);
            case 1:
                return mView.getString(R.string.edited_events);
        }
        return null;
    }
}
