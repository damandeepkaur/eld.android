package com.bsmwireless.screens.help;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.bsmwireless.widgets.helpview.HelpView;

import java.util.ArrayList;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HelpActivity extends Activity {

    public static final String HELP_IMAGE_ID_TEG = "HELP_IMAGE_RESOURCE_ID";
    public static final String HELP_MODEL_EXTRA = "help_model";

    @BindView(R.id.help_view)
    HelpView mHelpView;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hideTitle status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_help);
        mUnbinder = ButterKnife.bind(this);

        //TODO: remove image loading when possible
        if (getIntent().hasExtra(HELP_IMAGE_ID_TEG)) {
            int helpImageId = getIntent().getIntExtra(HELP_IMAGE_ID_TEG, 0);
            mHelpView.setBackgroundResource(helpImageId);
        } else if (getIntent().hasExtra(HELP_MODEL_EXTRA)) {
            ArrayList<HelpView.HelpModel> list = getIntent().getParcelableArrayListExtra(HELP_MODEL_EXTRA);
            initHelpView(list);
        }

        mHelpView.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    private void initHelpView(ArrayList<HelpView.HelpModel> list) {
        for (HelpView.HelpModel model : list) {
            mHelpView.addView(model);
        }
    }
}
