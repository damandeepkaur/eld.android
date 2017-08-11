package com.bsmwireless.screens.settings;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseMenuActivity;
import com.bsmwireless.screens.settings.dagger.DaggerSettingsComponent;
import com.bsmwireless.screens.settings.dagger.SettingsModule;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseMenuActivity implements SettingsView {

    private static final String TAG = SettingsActivity.class.getCanonicalName();

    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.box_gps_switch)
    SwitchCompat mBoxGPSSwitch;

    @BindView(R.id.fixed_amount_switch)
    SwitchCompat mFixedAmountSwitch;

    @BindView(R.id.lbl_odometer_units)
    TextView mOdometerUnitsTextView;

    @BindView(R.id.lbl_odometer_units_value)
    TextView mOdometerUnitsValueTextView;

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

        mPresenter.onViewCreated();
    }

    @OnClick(R.id.lbl_odometer_units)
    void executePopupMenu() {
        this.showPopupMenu();
    }

    @Override
    public void setBoxGPSSwitchEnabled(boolean isEnabled) {
        mBoxGPSSwitch.setChecked(isEnabled);
    }

    @Override
    public void setFixedAmountSwitchEnabled(boolean isEnabled) {
        mFixedAmountSwitch.setChecked(isEnabled);
    }

    @Override
    public void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, mOdometerUnitsTextView);
        popupMenu.inflate(R.menu.menu_odometer_units);

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_odometer_units_km:
                    mPresenter.onKMOdometerUnitsSelected(true);
                    return true;
                case R.id.menu_odometer_units_mi:
                    mPresenter.onKMOdometerUnitsSelected(false);
                    return true;
            }
            return false;
        });

        popupMenu.show();
    }

    @Override
    public void showOdometerUnits(OdometerUnits odometerUnits) {
        int id;

        switch (odometerUnits) {
            case ODOMETER_UNITS_KM:
                id = R.string.logs_km_set;
                break;

            case ODOMETER_UNITS_MI:
                id = R.string.logs_mi_set;
                break;

            default:
                id = R.string.logs_km_set;
                break;
        }

        mOdometerUnitsValueTextView.setText(getString(id));
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onBoxGPSSwitchChecked(mBoxGPSSwitch.isChecked());
        mPresenter.onFixedAmountSwitchChecked(mFixedAmountSwitch.isChecked());
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }
}
