package com.bsmwireless.screens.navigation;

import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

public interface NavigateView extends BaseMenuView {
    void goToLoginScreen();
    void goToSelectAssetScreen();
    void showErrorMessage(String message);
    void setDriverName(String name);
    void setCoDriversNumber(int coDriverNum);
    void setBoxId(int boxId);
    void setAssetsNumber(int assetsNum);
    void setResetTime(long time);
    void onCoDriverViewStart(String coDriverName);
    void onCoDriverViewEnd();

    void setAutoOnDuty();
    void setAutoDriving();
    void setAutoDrivingWithoutConfirm();

    SnackBarLayout getSnackBar();
}
