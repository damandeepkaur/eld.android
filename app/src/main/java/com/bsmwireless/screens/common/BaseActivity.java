package com.bsmwireless.screens.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bsmwireless.common.utils.UiVisibilityHelper;

import butterknife.Unbinder;

import static com.bsmwireless.common.utils.UiVisibilityHelper.NAVIGATION_STICKY;

public abstract class BaseActivity extends AppCompatActivity {
    protected Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiVisibilityHelper.updateUiVisibility(this, NAVIGATION_STICKY);
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        super.onDestroy();
    }
}
