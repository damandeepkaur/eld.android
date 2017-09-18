package com.bsmwireless.screens.settings;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.settings.dagger.DaggerSettingsComponent;
import com.bsmwireless.screens.settings.dagger.SettingsModule;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public final class SettingsActivity extends BaseMenuActivity implements SettingsView {

    private static final String TAG = SettingsActivity.class.getCanonicalName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.box_gps_switch)
    SwitchCompat mBoxGPSSwitch;

    @BindView(R.id.fixed_amount_switch)
    SwitchCompat mFixedAmountSwitch;

    @BindView(R.id.lbl_odometer_units)
    TextView mOdometerUnitsTextView;

    @BindView(R.id.radio_group_odometer_units)
    RadioGroup mOdometerUnitsRadioGroup;

    @BindView(R.id.kilometers_unit_button)
    AppCompatRadioButton mKilometersUnitsRadioButton;

    @BindView(R.id.miles_unit_button)
    AppCompatRadioButton mMilesUnitsRadioButton;

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

        mOdometerUnitsRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.kilometers_unit_button:
                    mPresenter.onUnitsSelected(true);
                    break;
                case R.id.miles_unit_button:
                    mPresenter.onUnitsSelected(false);
                    break;
                default:
                    break;
            }
        });

        mPresenter.onViewCreated();
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
    public void checkOdometerUnit(OdometerUnits odometerUnits) {
        switch (odometerUnits) {
            case ODOMETER_UNITS_KM:
                mKilometersUnitsRadioButton.setChecked(true);
                break;
            case ODOMETER_UNITS_MI:
                mMilesUnitsRadioButton.setChecked(true);
                break;
            default:
                mKilometersUnitsRadioButton.setChecked(true);
                break;
        }
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

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }
}
