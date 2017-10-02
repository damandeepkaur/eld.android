package com.bsmwireless.screens.hoursofservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

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

public final class HoursOfServiceActivity extends BaseMenuActivity implements ViewPager.OnPageChangeListener, HoursOfServiceView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.navigation_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.navigation_snackbar)
    SnackBarLayout mSnackBarLayout;

    @Inject
    HoursOfServicePresenter mPresenter;

    private Runnable mResetTimeTask = () -> mPresenter.onResetTime();
    private Handler mHandler = new Handler();

    public static Intent createIntent(Context context) {
        return new Intent(context, HoursOfServiceActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hours_of_service);
        mUnbinder = ButterKnife.bind(this);

        App.getComponent().hoursOfServiceBuilder()
                .view(this)
                .build()
                .inject(this);


        NavigationAdapter pagerAdapter = new NavigationAdapter(getApplicationContext(), getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.accent_transparent), ContextCompat.getColor(this, R.color.accent));

        mSnackBarLayout.setHideableOnTouch(false);

        mToolbar.setTitleTextAppearance(this, R.style.AppTheme_TextAppearance_Toolbar_Title);
        mToolbar.setSubtitleTextAppearance(this, R.style.AppTheme_TextAppearance_Toolbar_Subtitle);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_green_24dp);
        }

        mPresenter.loadTitle();
        mPresenter.onResetTime();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mViewPager.removeOnPageChangeListener(this);
        super.onDestroy();
        mHandler.removeCallbacks(mResetTimeTask);
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
    public void setTitle(long boxId, String driverName) {
        if (mToolbar == null) {
            return;
        }

        mToolbar.setTitle(driverName);

        mToolbar.setSubtitle(boxId > 0
                ? getString(R.string.box, boxId)
                : getString(R.string.hos_not_in_vehicle));
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
