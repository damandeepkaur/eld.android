package com.bsmwireless.screens.hoursofservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.bsmwireless.common.App;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.navigation.NavigationAdapter;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HoursOfServiceActivity extends BaseMenuActivity implements ViewPager.OnPageChangeListener, HoursOfServiceView {

    @BindView(R.id.navigation_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.navigation_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.navigation_snackbar)
    SnackBarLayout mSnackBarLayout;

    @Inject
    HoursOfServicePresenter mPresenter;

    private NavigationAdapter mPagerAdapter;
    private Runnable mResetTimeTask = () -> mPresenter.onResetTime();
    private Handler mHandler = new Handler();


    public static Intent createIntent(Context context) {
        return new Intent(context, HoursOfServiceActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hours_of_service);
        ButterKnife.bind(this);

        App.getComponent().hoursOfServiceBuilder()
                .view(this)
                .build()
                .inject(this);


        mPagerAdapter = new NavigationAdapter(getApplicationContext(), getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.accent_transparent), ContextCompat.getColor(this, R.color.accent));

        mSnackBarLayout.setHideableOnTouch(false);

        mPresenter.onResetTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mResetTimeTask);
        mViewPager.removeOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mSnackBarLayout.reset().hideSnackbar();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public SnackBarLayout getSnackBar() {
        return mSnackBarLayout;
    }

    @Override
    public void setResetTime(long time) {
        mHandler.removeCallbacks(mResetTimeTask);
        if (time == 0) {
            mHandler.post(mResetTimeTask);
        } else {
            mHandler.postAtTime(mResetTimeTask, time);
        }
    }

    @Override
    public void showReassignDialog(ELDEvent event) {
        showReassignEventDialog(event);
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }
}
