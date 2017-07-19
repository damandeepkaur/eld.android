package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.data.storage.users.UserEntity;

public interface DriverProfileView {
    void setUserInfo(UserEntity user);
    void userUpdated();
    void userUpdateError(Throwable error);
}
