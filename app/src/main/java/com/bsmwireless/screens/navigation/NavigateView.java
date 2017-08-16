package com.bsmwireless.screens.navigation;

import com.bsmwireless.widgets.snackbar.SnackBarLayout;

public interface NavigateView {
    void goToLoginScreen();
    void showErrorMessage(String message);
    void setDriverName(String name);
    void setCoDriversNumber(int coDriverNum);
    void setBoxId(int boxId);
    void setAssetsNumber(int assetsNum);

    SnackBarLayout getSnackBar();
    SnackBarLayout getErrorSnackBar();
}
