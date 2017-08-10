package com.bsmwireless.screens.login;

import com.bsmwireless.data.network.RetrofitException;

public interface LoginView {

    enum Error {
        ERROR_UNEXPECTED,
        ERROR_USER,
        ERROR_PASSWORD,
        ERROR_DOMAIN
    }

    String getUsername();

    String getPassword();

    String getDomain();

    void showErrorMessage(Error error);

    void showErrorMessage(RetrofitException error);

    void goToSelectAssetScreen();

    void goToNavigationScreen();

    void loadUserData(String name, String domain);

    void setLoginButtonEnabled(boolean enabled);
}
