package com.bsmwireless.screens.login;

public interface LoginView {

    String getUsername();

    String getPassword();

    String getDomain();

    void showErrorMessage(CharSequence msg);

    void goToSelectAssetScreen();

    void goToNavigationScreen();

    void loadUserData(String name, String domain);

    void setLoginButtonEnabled(boolean enabled);
}
