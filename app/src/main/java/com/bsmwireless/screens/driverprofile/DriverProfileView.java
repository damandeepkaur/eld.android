package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.User;

public interface DriverProfileView {
    void setUserInfo(UserEntity user);
    void setHomeTerminalInfo(HomeTerminalEntity homeTerminal);
    void setCarrierInfo(CarrierEntity carrier);
    void showError(Throwable error);
    void hideControlButtons();
    void showControlButtons();
    void setResults(User user);
    void showChangePasswordError(String error);
    void showPasswordChanged();
}
