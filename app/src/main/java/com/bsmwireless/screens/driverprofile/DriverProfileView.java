package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.User;
import com.bsmwireless.screens.common.menu.BaseMenuView;

public interface DriverProfileView extends BaseMenuView {
    void setUserInfo(UserEntity user);
    void showError(Throwable error);
    void hideControlButtons();
    void showControlButtons();
    void setResults(User user);
    void showChangePasswordError(String error);
    void showPasswordChanged();
}
