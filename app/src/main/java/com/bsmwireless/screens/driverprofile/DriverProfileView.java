package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.User;

public interface DriverProfileView {
    void setUserInfo(UserEntity user);
    void showError(Throwable error);
    void hideControlButtons();
    void showControlButtons();
    void setResults(User user);
}
