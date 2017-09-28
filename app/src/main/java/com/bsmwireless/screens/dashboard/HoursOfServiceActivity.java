package com.bsmwireless.screens.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;

import javax.inject.Inject;

import app.bsmuniversal.com.R;

public class HoursOfServiceActivity extends BaseMenuActivity {

    @Inject
    HoursOfServicePresenter mHoursOfServicePresenter;

    public static Intent createIntent(Context context) {
        return new Intent(context, HoursOfServiceActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hours_of_service);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new DashboardFragment(), null)
                .commit();

        App.getComponent().hoursOfServiceBuilder()
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mHoursOfServicePresenter;
    }
}
