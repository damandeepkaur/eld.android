package com.bsmwireless.screens.driverprofile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.User;
import com.bsmwireless.screens.common.BaseMenuActivity;
import com.bsmwireless.screens.driverprofile.dagger.DaggerDriverProfileComponent;
import com.bsmwireless.screens.driverprofile.dagger.DriverProfileModule;
import com.bsmwireless.widgets.signview.SignatureLayout;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

public class DriverProfileActivity extends BaseMenuActivity implements DriverProfileView, SignatureLayout.OnSaveSignatureListener {

    public static final String EXTRA_USER = "user";

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

     // TODO: Password change not implemented on server side.
     /*@BindView(R.id.password)
     EditText mPasswordTextView;*/

    @BindView(R.id.cycle)
    EditText mCycleTextView;

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

        mSignatureLayout.setOnSaveListener(this);
    }

    @Override
    public void onBackPressed() {
        mPresenter.onSaveUserInfo(mAddressTextView.getText().toString());
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
                       .setHideableOnFocusLost(true)
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
}
