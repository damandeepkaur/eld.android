package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.data.storage.users.UserEntity;

public interface DriverProfileView {
    void setUserInfo(UserEntity user);
    void updateUser();
    void showError(Throwable error);
}
