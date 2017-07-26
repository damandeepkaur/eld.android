package com.bsmwireless.screens.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.common.BaseMenuActivity;
import com.bsmwireless.screens.settings.dagger.DaggerSettingsComponent;
import com.bsmwireless.screens.settings.dagger.SettingsModule;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsActivity extends BaseMenuActivity implements SettingsView {

    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.box_gps_switch)
    SwitchCompat boxGPSSwitch;

    @BindView(R.id.fixed_amount_switch)
    SwitchCompat fixedAmountSwitch;

    @Inject
    SettingsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);

        DaggerSettingsComponent.builder().appComponent(App.getComponent()).settingsModule(new SettingsModule(this)).build().inject(this);

        setContentView(R.layout.activity_settings);
        mUnbinder = ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_green_24dp);
            actionBar.setTitle(R.string.settings_title);
        }

        mPresenter.setBoxGPSSwitch();
        mPresenter.setFixedAmountSwitch();
    }

    @Override
    public void setBoxGPSSwitchEnabled(boolean isEnabled) {
        boxGPSSwitch.setChecked(isEnabled);
    }

    @Override
    public void setFixedAmountSwitchEnabled(boolean isEnabled) {
        fixedAmountSwitch.setChecked(isEnabled);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onBoxGPSSwitchChecked(boxGPSSwitch.isChecked());
        mPresenter.onFixedAmountSwitchChecked(fixedAmountSwitch.isChecked());
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }
}
