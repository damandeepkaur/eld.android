package com.bsmwireless.screens.switchdriver;

import com.bsmwireless.data.network.RetrofitException;

import java.util.List;

import app.bsmuniversal.com.R;

public interface SwitchDriverView {
    enum Error {
        ERROR_INVALID_CREDENTIALS(R.string.switch_driver_invalid_credentials),
        ERROR_LOGIN_CO_DRIVER(R.string.switch_driver_error_login_codriver);

        private int mStringId;

        Error(int stringId) {
            mStringId = stringId;
        }

        public int getStringId() {
            return mStringId;
        }
    }

    void show();
    void show(SwitchDriverDialog.SwitchDriverStatus status);
    void setDriverInfo(SwitchDriverDialog.UserModel driver);
    void setCoDriversForSwitchDialog(List<SwitchDriverDialog.UserModel> coDrivers);
    void setCoDriversForLogOutDialog(List<SwitchDriverDialog.UserModel> coDrivers);
    void setCoDriversForDriverSeatDialog(List<SwitchDriverDialog.UserModel> coDrivers);
    void coDriverLoggedIn();
    void loginError();
    void coDriverLoggedOut();
    void logoutError();
    void showError(Error error);
    void showError(RetrofitException error);
    void showProgress();
    void hideProgress();
}
