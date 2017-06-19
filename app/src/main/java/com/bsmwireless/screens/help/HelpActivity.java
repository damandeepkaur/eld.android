package com.bsmwireless.screens.help;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import app.bsmuniversal.com.R;

public class HelpActivity extends Activity {

    public static final String HELP_IMAGE_ID_TEG = "HELP_IMAGE_RESOURCE_ID";

    public FrameLayout mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mLayout = (FrameLayout) findViewById(R.id.help_layout);
        int helpImageId = getIntent().getIntExtra(HELP_IMAGE_ID_TEG, 0);
        mLayout.setBackgroundResource(helpImageId);
        mLayout.setOnClickListener(v -> finish());
    }
}
