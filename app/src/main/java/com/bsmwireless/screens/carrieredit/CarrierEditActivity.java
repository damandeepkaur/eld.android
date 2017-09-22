package com.bsmwireless.screens.carrieredit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.bsmwireless.screens.common.BaseActivity;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by osminin on 21.09.2017.
 */

public final class CarrierEditActivity extends BaseActivity implements CarrierEditView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier_edit);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
