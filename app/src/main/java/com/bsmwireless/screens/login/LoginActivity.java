package com.bsmwireless.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;
import com.bsmwireless.screens.login.dagger.DaggerLoginComponent;
import com.bsmwireless.screens.login.dagger.LoginModule;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class LoginActivity extends BaseActivity implements LoginView {
    public static final String ARG_ACCOUNT_NAME = "name";
    public static final String ARG_DOMAIN_NAME = "domain";

    @BindView(R.id.username)
    EditText mUserName;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.domain)
    EditText mDomain;

    @BindView(R.id.execute_login)
    Button mLoginButton;

    @BindView(R.id.switchButton)
    Button mSwitchButton;

    @Inject
    LoginPresenter mPresenter;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerLoginComponent.builder().appComponent(App.getComponent()).loginModule(new LoginModule(this)).build().inject(this);

        setContentView(R.layout.activity_login);
        mUnbinder = ButterKnife.bind(this);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(ARG_ACCOUNT_NAME) && intent.hasExtra(ARG_DOMAIN_NAME)) {
            loadUserData(intent.getStringExtra(ARG_ACCOUNT_NAME), intent.getStringExtra(ARG_DOMAIN_NAME));
        } else {
            mPresenter.onLoadUserData();
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mUnbinder.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.execute_login)
    void executeLogin() {
        //TODO: check if remember me activated
        mPresenter.onLoginButtonClicked(true);
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
    public void showErrorMessage(String message) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.loginerror);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }

    @Override
    public void goToMainScreen() {
        startActivity(new Intent(this, SelectAssetActivity.class));
        finish();
    }

    @Override
    public void loadUserData(String name, String domain) {
        mUserName.setText(name);
        mDomain.setText(domain);
    }

    @Override
    public void setLoginButtonEnabled(boolean enabled) {
        mLoginButton.setEnabled(enabled);
    }
}
