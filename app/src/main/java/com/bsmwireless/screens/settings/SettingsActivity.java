package com.bsmwireless.screens.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.settings.dagger.DaggerSettingsComponent;
import com.bsmwireless.screens.settings.dagger.SettingsModule;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsActivity extends BaseActivity implements SettingsView {

    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;

    @Inject
    SettingsPresenter mPresenter;

    private Unbinder mUnbinder;

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_driver_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_eld:
                break;
            case R.id.action_sign:
                break;
            case R.id.action_occupants:
                break;
            case android.R.id.home: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        mPresenter.onDestroy();
        super.onDestroy();
    }
}
