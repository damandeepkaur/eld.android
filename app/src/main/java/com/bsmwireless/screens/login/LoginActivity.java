package com.bsmwireless.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.login.dagger.DaggerLoginComponent;
import com.bsmwireless.screens.login.dagger.LoginModule;
import com.bsmwireless.screens.navigation.NavigationActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


public final class LoginActivity extends BaseActivity implements LoginView {
    private static final int ERROR_DIALOG_REQUEST_CODE = 1001;

    public static final String ARG_ACCOUNT_NAME = "name";
    public static final String ARG_DOMAIN_NAME = "domain";

    @BindView(R.id.username)
    EditText mUserName;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.domain)
    EditText mDomain;

    @BindView(R.id.execute_login)
    AppCompatButton mLoginButton;

    @BindView(R.id.switchButton)
    SwitchCompat mSwitchButton;

    @BindView(R.id.login_snackbar)
    SnackBarLayout mSnackBarLayout;

    @BindView(R.id.login_progress_bar)
    ProgressBar mLoginProgressBar;

    @Inject
    LoginPresenter mPresenter;

    private boolean mRetryProviderInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerLoginComponent.builder().appComponent(App.getComponent()).loginModule(new LoginModule(this)).build().inject(this);
        installSecurityProvider();

        setContentView(R.layout.activity_login);
        mUnbinder = ButterKnife.bind(this);

        mSnackBarLayout
                .setHideableOnFocusLost(true)
                .setHideableOnTimeout(SnackBarLayout.DURATION_LONG)
                .setPositiveLabel(getString(R.string.try_again), v -> executeLogin());

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GooglePlayServicesUtil.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            mRetryProviderInstall = true;
        }
    }

    /**
     * On resume, check to see if we flagged that we need to reinstall the
     * provider.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRetryProviderInstall) {
            // We can now safely retry installation.
            installSecurityProvider();
        }
        mRetryProviderInstall = false;
    }

    private void installSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.showErrorDialogFragment(e.getConnectionStatusCode(), this, null, ERROR_DIALOG_REQUEST_CODE, dialogInterface -> {
            });
        } catch (GooglePlayServicesNotAvailableException e) {
            Timber.e(e);
        }
    }

    private void initView() {
        hideProgressBar();
        mPresenter.onViewCreated();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @OnClick(R.id.execute_login)
    void executeLogin() {
        mSnackBarLayout.hideSnackbar();
        mPresenter.onLoginButtonClicked(mSwitchButton.isChecked());
    }

    @Override
    public String getUsername() {
        return mUserName.getText().toString();
    }

    @Override
    public String getPassword() {
        return mPassword.getText().toString();
    }

    @Override
    public String getDomain() {
        return mDomain.getText().toString();
    }

    @Override
    public void showErrorMessage(Error error) {
        int id;

        switch (error) {
            case ERROR_USER:
                id = R.string.error_username;
                break;

            case ERROR_DOMAIN:
                id = R.string.error_domain;
                break;

            case ERROR_PASSWORD:
                id = R.string.error_password;
                break;

            default:
                id = R.string.error_unexpected;
                break;
        }

        mSnackBarLayout
                .setMessage(getString(id))
                .showSnackbar();
    }

    @Override
    public void showErrorMessage(RetrofitException error) {
        mSnackBarLayout
                .setMessage(NetworkUtils.getErrorMessage(error, this))
                .showSnackbar();
    }

    @Override
    public void goToSelectAssetScreen() {
        SchedulerUtils.cancel();
        SchedulerUtils.schedule();

        startActivity(new Intent(this, SelectAssetActivity.class));
        finish();
    }

    @Override
    public void goToNavigationScreen() {
        startActivity(NavigationActivity.createIntent(this));
        finish();
    }

    @Override
    public void loadUserData(String name, String domain, boolean enabled) {
        mSwitchButton.setChecked(enabled);

        mUserName.setText(name);
        mDomain.setText(domain);
    }

    @Override
    public void setLoginButtonEnabled(boolean enabled) {
        mLoginButton.setEnabled(enabled);
    }

    @Override
    public void showProgressBar() {
        mLoginProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mLoginProgressBar.setVisibility(View.INVISIBLE);
    }
}
