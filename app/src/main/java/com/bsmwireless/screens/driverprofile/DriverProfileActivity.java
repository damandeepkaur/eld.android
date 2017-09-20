package com.bsmwireless.screens.driverprofile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.User;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.driverprofile.dagger.DaggerDriverProfileComponent;
import com.bsmwireless.screens.driverprofile.dagger.DriverProfileModule;
import com.bsmwireless.widgets.signview.SignatureLayout;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.getFullTimeZone;

public final class DriverProfileActivity extends BaseMenuActivity implements DriverProfileView, SignatureLayout.OnSaveSignatureListener, AdapterView.OnItemSelectedListener {

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

    @BindView(R.id.change_password_button)
    AppCompatButton mButtonChangePass;

    @BindView(R.id.role)
    TextInputEditText mRole;

    @BindView(R.id.hos_cycle_spinner)
    AppCompatSpinner mHOSCycle;

    @BindView(R.id.eld_toggle)
    SwitchCompat mELDToggle;

    @BindView(R.id.carrier_name)
    TextInputEditText mCarrierName;

    @BindView(R.id.terminal_name_spinner)
    AppCompatSpinner mTerminalNames;

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

        boolean isAppOnline = NetworkUtils.isOnlineMode();
        mCurrentPasswordTextView.setFocusable(false);
        mNewPasswordTextView.setFocusable(false);
        mConfirmPasswordTextView.setFocusable(false);

        mNewPasswordTextView.setEnabled(isAppOnline);
        mConfirmPasswordTextView.setEnabled(isAppOnline);
        mCurrentPasswordTextView.setEnabled(isAppOnline);

        mButtonChangePass.setTextColor(ContextCompat.getColor(this, R.color.default_hint));
        mButtonChangePass.setEnabled(isAppOnline);

        initToolbar();

        mPresenter.onNeedUpdateUserInfo();

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
        mSnackBarLayout.reset().hideSnackbar();
        super.onDestroy();
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setUserInfo(UserEntity user) {
        mNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        mEmployeeIDTextView.setText(String.valueOf(user.getId()));
        mLicenseTextView.setText(user.getLicense());
        mELDToggle.setChecked(user.getExempt());
        // TODO: set real role
        mRole.setText(User.DriverType.DRIVER.name());

        mSignatureLayout.setImageData(user.getSignature());
    }

    @Override
    public void setHomeTerminalsSpinner(List<String> homeTerminalNames, int selectedTerminal) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, homeTerminalNames);

        mTerminalNames.setAdapter(adapter);
        mTerminalNames.setSelection(selectedTerminal);
        mTerminalNames.setOnItemSelectedListener(this);
    }

    @Override
    public void setHomeTerminalInfo(HomeTerminalEntity homeTerminal) {
        mTerminalAddress.setText(homeTerminal.getAddress());
        mHomeTerminalTimeZone.setText(getFullTimeZone(homeTerminal.getTimezone(), Calendar.getInstance().getTimeInMillis()));
    }

    @Override
    public void setCarrierInfo(CarrierEntity carrier) {
        mCarrierName.setText(carrier.getName());
    }

    @Override
    public void showError(RetrofitException error) {
        showNotificationSnackBar(NetworkUtils.getErrorMessage(error, this).toString());
    }

    @Override
    public void showError(Error error) {
        showNotificationSnackBar(getString(error.getStringId()));
    }

    @Override
    public void onChangeClicked() {
        showChangeSignSnackBar();
    }

    @Override
    public void setResults(User user) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_USER, user);
        setResult(RESULT_OK, resultIntent);
    }

    @Override
    public void showSignatureChanged() {
        Timber.e(getString(R.string.driver_profile_signature_changed));
        showNotificationSnackBar(getString(R.string.driver_profile_signature_changed));
    }

    @Override
    public void showPasswordChanged() {
        showNotificationSnackBar(getString(R.string.driver_profile_password_changed));
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

    private void showChangeSignSnackBar() {
        mSnackBarLayout.setOnReadyListener(snackBar -> {
            snackBar.reset()
                    .setPositiveLabel(getString(R.string.ok), v -> mPresenter.onSaveSignatureClicked(mSignatureLayout.getImageData()))
                    .setNegativeLabel(getString(R.string.clear), v -> mSignatureLayout.clear())
                    .setHideableOnFocusLost(true)
                    .setOnCloseListener(new SnackBarLayout.OnCloseListener() {
                        @Override
                        public void onClose(SnackBarLayout snackBar) {
                            mSignatureLayout.setEditable(false);
                        }

                        @Override
                        public void onOpen(SnackBarLayout snackBar) {
                            mSignatureLayout.setEditable(true);
                        }
                    });
        })
                .showSnackbar();
    }

    private void showNotificationSnackBar(String message) {
        mSnackBarLayout.setOnReadyListener(snackBar -> snackBar.reset().setMessage(message).setHideableOnTimeout(SnackBarLayout.DURATION_LONG))
                .showSnackbar();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.onChooseHomeTerminal(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
