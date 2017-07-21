package com.bsmwireless.screens.login;

public interface LoginView {

    String getUsername();

    String getPassword();

    String getDomain();

    void showErrorMessage(String msg);

    void goToMainScreen();

    void loadUserData(String name, String domain);

    void setLoginButtonEnabled(boolean enabled);

    boolean loadUserDataEnabled();
}
