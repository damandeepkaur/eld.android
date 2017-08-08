package com.bsmwireless.screens.autologout;

public interface AutoLogoutView {

    void goToLoginScreen();

    void showAutoLogoutDialog();

    void initAutoLogoutIfNoUserInteraction();
}
