package com.bsmwireless.screens.driverprofile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.User;
import com.bsmwireless.screens.common.BaseMenuActivity;
import com.bsmwireless.screens.driverprofile.dagger.DaggerDriverProfileComponent;
import com.bsmwireless.screens.driverprofile.dagger.DriverProfileModule;
import com.bsmwireless.widgets.signview.SignatureLayout;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import java.util.Calendar;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

import static com.bsmwireless.screens.driverprofile.DriverProfilePresenter.PASSWORD_NOT_MATCH_ERROR;

public class DriverProfileActivity extends BaseMenuActivity implements DriverProfileView, SignatureLayout.OnSaveSignatureListener {

    public static final String EXTRA_USER = "user";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.name)
    TextInputEditText mNameTextView;

    @BindView(R.id.emp_id)
    TextInputEditText mEmployeeIDTextView;

    @BindView(R.id.license)
    TextInputEditText mLicenseTextView;

    @BindView(R.id.current_password)
    TextInputEditText mCurrentPasswordTextView;

    @BindView(R.id.current_password_layout)
    TextInputLayout mCurrentPasswordLayout;

    @BindView(R.id.new_password)
    TextInputEditText mNewPasswordTextView;

    @BindView(R.id.new_password_layout)
    TextInputLayout mNewPasswordLayout;

    @BindView(R.id.confirm_password)
    TextInputEditText mConfirmPasswordTextView;

    @BindView(R.id.confirm_password_layout)
    TextInputLayout mConfirmPasswordLayout;

    @BindView(R.id.role)
    TextInputEditText mRole;

    @BindView(R.id.hos_cycle_spinner)
    AppCompatSpinner mHOSCycle;

    @BindView(R.id.eld_toggle)
    SwitchCompat mELDToggle;

    // TODO: not implemented on server
    /*@BindView(R.id.carrier_name)
    TextInputEditText mCarrierName;*/

    @BindView(R.id.terminal_name)
    TextInputEditText mTerminalName;

    @BindView(R.id.terminal_address)
    TextInputEditText mTerminalAddress;

    @BindView(R.id.home_terminal_time_zone)
    TextInputEditText mHomeTerminalTimeZone;

    @BindView(R.id.signature_view)
    SignatureLayout mSignatureLayout;

    @BindView(R.id.scroll_view)
    ScrollView mScrollView;

    @BindView(R.id.snackbar)
    SnackBarLayout mSnackBarLayout;

    @Inject
    DriverProfilePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerDriverProfileComponent.builder().appComponent(App.getComponent()).driverProfileModule(new DriverProfileModule(this)).build().inject(this);

        setContentView(R.layout.activity_driver_profile);
        mUnbinder = ButterKnife.bind(this);

        initToolbar();
        initSnackbar();

        mPresenter.onNeedUpdateUserInfo();

        mSnackBarLayout.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                mSnackBarLayout.hideSnackbar();
            }
        });

        mSignatureLayout.setOnSaveListener(this);
    }

    @Override
    public void onBackPressed() {
        mPresenter.onSaveUserInfo();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setUserInfo(UserEntity user) {
        mNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        mEmployeeIDTextView.setText(String.valueOf(user.getId()));
        mLicenseTextView.setText(user.getLicense());
        mELDToggle.setChecked(user.getExempt());
        mTerminalName.setText(user.getOrganization());
        mTerminalAddress.setText(user.getOrgAddr());
        mHomeTerminalTimeZone.setText(ViewUtils.getFullTimeZone(user.getTimezone(), Calendar.getInstance().getTimeInMillis()));
        mRole.setText(User.DriverType.DRIVER.name());

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
        mSnackBarLayout.hideSnackbar();
    }

    @Override
    public void showControlButtons() {
        mSnackBarLayout.showSnackbar();
    }

    @Override
    public void setResults(User user) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_USER, user);
        setResult(RESULT_OK, resultIntent);
    }

    @Override
    public void showChangePasswordError(String error) {
        setPasswordChangeError(error);
    }

    @Override
    public void showPasswordChanged() {
        Toast.makeText(this, getString(R.string.driver_profile_password_changed), Toast.LENGTH_SHORT).show();
        setPasswordChangeError(null);
    }

    @OnClick(R.id.change_password_button)
    void onChangePasswordClick() {
        mPresenter.onChangePasswordClick(mCurrentPasswordTextView.getText().toString(),
                                         mNewPasswordTextView.getText().toString(),
                                         mConfirmPasswordTextView.getText().toString());
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSnackbar() {
        mSnackBarLayout.setPositiveLabel(getString(R.string.ok), v -> mPresenter.onSaveSignatureClicked(mSignatureLayout.getImageData()))
                       .setNegativeLabel(getString(R.string.clear), v -> mSignatureLayout.clear())
                       .setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                            @Override
                            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                                switch (newState) {
                                    case STATE_HIDDEN: {
                                        mSignatureLayout.setEditable(false);
                                        break;
                                    }
                                    case STATE_EXPANDED: {
                                        mSignatureLayout.setEditable(true);
                                        break;
                                    }
                                    default: {
                                        break;
                                    }
                                }
                            }

                           @Override
                           public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                           }
                       });
    }

    private void setPasswordChangeError(String error) {
        mCurrentPasswordLayout.setError(error);
        mNewPasswordLayout.setError(error);
        mConfirmPasswordLayout.setError(error);
    }
}
