package com.bsmwireless.screens.driverprofile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.driverprofile.dagger.DaggerDriverProfileComponent;
import com.bsmwireless.screens.driverprofile.dagger.DriverProfileModule;
import com.bsmwireless.widgets.signview.SignatureLayout;


import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DriverProfileActivity extends BaseActivity implements DriverProfileView, SignatureLayout.OnSaveSignatureListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.name)
    EditText mNameTextView;

    @BindView(R.id.emp_id)
    EditText mEmployeeIDTextView;

    @BindView(R.id.company)
    EditText mCompanyTextView;

    @BindView(R.id.license)
    EditText mLicenseTextView;

    @BindView(R.id.home_addr)
    EditText mAddressTextView;

    @BindView(R.id.time_zone)
    EditText mTimeZoneTextView;

    @BindView(R.id.password)
    EditText mPasswordTextView;

    @BindView(R.id.cycle)
    EditText mCycleTextView;

    @BindView(R.id.signature_view)
    SignatureLayout mSignatureLayout;

    @BindView(R.id.control_buttons)
    LinearLayout mControlButtons;

    @Inject
    DriverProfilePresenter mPresenter;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerDriverProfileComponent.builder().appComponent(App.getComponent()).driverProfileModule(new DriverProfileModule(this)).build().inject(this);

        setContentView(R.layout.activity_driver_profile);
        mUnbinder = ButterKnife.bind(this);

        initToolbar();

        mPresenter.onNeedUpdateUserInfo();

        mControlButtons.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideControlButtons();
            }
        });

        mSignatureLayout.setOnSaveListener(this);
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
    protected void onStop() {
        super.onStop();
        mPresenter.onSaveUserInfo(mAddressTextView.getText().toString());
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mControlButtons.clearAnimation();
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void setUserInfo(UserEntity user) {
        mNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        mEmployeeIDTextView.setText(String.valueOf(user.getId()));
        mCompanyTextView.setText(user.getOrganization());
        mLicenseTextView.setText(user.getLicense());
        mAddressTextView.setText(user.getAddress());
        mTimeZoneTextView.setText(user.getTimezone());
        mCycleTextView.setText(String.valueOf(user.getCycleCountry()));
        mSignatureLayout.setImageData(user.getSignature());
    }

    @Override
    public void showError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangeClicked() {
        showControlButtons();
    }

    @Override
    public void hideControlButtons() {
        if (mControlButtons.getVisibility() == VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mControlButtons.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mControlButtons.startAnimation(animation);
            mSignatureLayout.setEditable(false);
        }
    }

    @Override
    public void showControlButtons() {
        if (mControlButtons.getVisibility() != VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mControlButtons.setVisibility(VISIBLE);
                    mControlButtons.requestFocus();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mControlButtons.startAnimation(animation);
            mSignatureLayout.setEditable(true);
        }
    }

    @OnClick(R.id.clear_button)
    void onClearSignatureClicked() {
        mSignatureLayout.clear();
    }

    @OnClick(R.id.ok_button)
    void onSaveSignatureClicked() {
        mPresenter.onSaveSignatureClicked(mSignatureLayout.getImageData());
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
