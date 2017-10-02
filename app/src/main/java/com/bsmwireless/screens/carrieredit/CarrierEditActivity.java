package com.bsmwireless.screens.carrieredit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.carrieredit.dagger.CarrierEditComponent;
import com.bsmwireless.screens.carrieredit.dagger.CarrierEditModule;
import com.bsmwireless.screens.carrieredit.dagger.DaggerCarrierEditComponent;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public final class CarrierEditActivity extends BaseMenuActivity implements CarrierEditView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.carrier_edit_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.carrier_edit_tab_layout)
    TabLayout mTabLayout;

    @Inject
    CarrierEditPresenter mPresenter;

    private CarrierEditComponent mComponent;
    private CarrierEditAdapter mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier_edit);
        ButterKnife.bind(this);
        mComponent = DaggerCarrierEditComponent.builder()
                .appComponent(App.getComponent())
                .carrierEditModule(new CarrierEditModule())
                .build();
        mComponent.inject(this);
        mPresenter.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPagerAdapter = new CarrierEditAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public CarrierEditComponent getComponent() {
        return mComponent;
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return (BaseMenuPresenter) mPresenter;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
