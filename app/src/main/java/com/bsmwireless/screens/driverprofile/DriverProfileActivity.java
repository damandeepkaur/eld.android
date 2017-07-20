package com.bsmwireless.screens.driverprofile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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
import butterknife.Unbinder;

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

        mSignatureLayout.setOnSaveListener(this);

        mCompanyTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !mCompanyTextView.getText().toString().isEmpty()) {
                mPresenter.onSaveCompanyClicked(mCompanyTextView.getText().toString());
            }
        });

        mAddressTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !mAddressTextView.getText().toString().isEmpty()) {
                mPresenter.onSaveHomeAddressClicked(mAddressTextView.getText().toString());
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void setUserInfo(UserEntity user) {
        mNameTextView.setText(user.getAccountName());
        mEmployeeIDTextView.setText(String.valueOf(user.getId()));
        mCompanyTextView.setText(user.getOrganization());
        mLicenseTextView.setText(user.getLicense());
        mAddressTextView.setText(user.getAddress());
        mTimeZoneTextView.setText(user.getTimezone());
        mPasswordTextView.setText(getString(R.string.driver_profile_password));
        mCycleTextView.setText(String.valueOf(user.getCycleCountry()));
        mSignatureLayout.setImageData(user.getSignature());
    }

    @Override
    public void updateUser() {
        Toast.makeText(this, getString(R.string.driver_profile_user_updated), Toast.LENGTH_SHORT).show();
        mPresenter.onNeedUpdateUserInfo();
    }

    @Override
    public void showError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveSignatureClicked(String data) {
        mPresenter.onSaveSignatureClicked(data);
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
