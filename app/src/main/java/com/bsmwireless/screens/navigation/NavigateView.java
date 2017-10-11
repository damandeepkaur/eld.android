package com.bsmwireless.screens.navigation;

import com.bsmwireless.screens.common.menu.BaseMenuView;

public interface NavigateView extends BaseMenuView {
    void goToLoginScreen();
    void goToSelectAssetScreen();
    void goToCarrierEditScreen();
    void showErrorMessage(String message);
    void setDriverName(String name);
    void setCoDriversNumber(int coDriverNum);
    void setBoxId(int boxId);
    void setAssetsNumber(int assetsNum);
    void setAutoOnDuty(long stoppedTime);
    void setAutoDriving();
    void setAutoDrivingWithoutConfirm();
    void showUnassignedDialog();
}
