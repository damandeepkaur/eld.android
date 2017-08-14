package com.bsmwireless.screens.navigation;

import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

public interface NavigateView extends BaseMenuView {
    void goToLoginScreen();
    void showErrorMessage(String message);
    void setDriverName(String name);
    void setCoDriversNumber(int coDriverNum);
    void setBoxId(int boxId);
    void setAssetsNumber(int assetsNum);

    SnackBarLayout getSnackBar();
}
