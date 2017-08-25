package com.bsmwireless.screens.autologout;

public interface AutoLogoutView {

    void goToLoginScreen();

    void initAutoLogoutDialog();

    void showAutoLogoutDialog();

    void initAutoLogoutIfNoUserInteraction();
}
