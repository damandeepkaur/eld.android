package com.bsmwireless.screens.common;

import android.support.v7.app.AppCompatActivity;

import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    protected Unbinder mActivityHolder;

    @Override
    protected void onDestroy() {
        if (mActivityHolder != null) {
            mActivityHolder.unbind();
        }

        super.onDestroy();
    }
}
