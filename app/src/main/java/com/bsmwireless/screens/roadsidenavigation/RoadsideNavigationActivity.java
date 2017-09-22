package com.bsmwireless.screens.roadsidenavigation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.roadsidenavigation.dagger.DaggerRoadsideNavigationComponent;
import com.bsmwireless.screens.roadsidenavigation.dagger.RoadsideNavigationModule;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public final class RoadsideNavigationActivity extends BaseMenuActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.navigation_view_pager)
    ViewPager mViewPager;

    @Inject
    RoadsideNavigationPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerRoadsideNavigationComponent.builder().appComponent(App.getComponent())
                .roadsideNavigationModule(new RoadsideNavigationModule(this)).build().inject(this);

        setContentView(R.layout.activity_roadside_navigation);
        mUnbinder = ButterKnife.bind(this);

        init();
    }

    private void init() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.roadside_title);
        }

        mViewPager.setAdapter(new RoadsideNavigationAdapter(getApplicationContext(), getSupportFragmentManager()));

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.accent_transparent), ContextCompat.getColor(this, R.color.accent));
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }
}
