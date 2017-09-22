package com.bsmwireless.screens.switchdriver;

import com.bsmwireless.data.network.RetrofitException;

import java.util.List;

import app.bsmuniversal.com.R;

public interface SwitchDriverView {
    enum Error {
        ERROR_INVALID_CREDENTIALS(R.string.switch_driver_invalid_credentials),
        ERROR_LOGIN_CO_DRIVER(R.string.switch_driver_error_login_codriver),
        ERROR_LOGOUT_CO_DRIVER(R.string.switch_driver_error_logout_codriver),
        ERROR_REASSIGN_EVENT(R.string.switch_driver_error_reassign_event);

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
    void setUsersForReassignDialog(List<SwitchDriverDialog.UserModel> users);
    void coDriverLoggedIn();
    void coDriverLoggedOut();
    void eventReassigned();
    void showError(Error error);
    void showError(RetrofitException error);
    void showProgress();
    void hideProgress();
    void createSwitchOnlyDialog();
    void createSwitchDriverDialog();
    void createAddCoDriverDialog();
    void createLogOutCoDriverDialog();
    void createDriverSeatDialog();
    void createLoadingDialog();
    void createReassignDialog();
}
