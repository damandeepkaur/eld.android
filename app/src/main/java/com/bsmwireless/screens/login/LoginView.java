package com.bsmwireless.screens.login;

import com.bsmwireless.data.network.RetrofitException;

public interface LoginView {

    String getUsername();

    String getPassword();

    String getDomain();

    void showErrorMessage(int id);

    void showErrorMessage(RetrofitException error);

    void goToSelectAssetScreen();

    void goToNavigationScreen();

    void loadUserData(String name, String domain);

    void setLoginButtonEnabled(boolean enabled);
}
