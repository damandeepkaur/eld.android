package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.User;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import java.util.List;

import app.bsmuniversal.com.R;

public interface DriverProfileView extends BaseMenuView {
    enum Error {
        ERROR_CHANGE_PASSWORD(R.string.driver_profile_password_not_changed),
        ERROR_SAVE_SIGNATURE(R.string.driver_profile_signature_changing_error),
        ERROR_SIGNATURE_LENGTH(R.string.driver_profile_signature_error),
        ERROR_INVALID_USER(R.string.driver_profile_user_error),
        ERROR_TERMINAL_UPDATE(R.string.driver_profile_home_terminal_updating_error),
        ERROR_HOS_CYCLE_UPDATE(R.string.driver_profile_hos_cycle_updating_error),
        VALID_PASSWORD(R.string.driver_profile_valid_password),
        PASSWORD_NOT_MATCH(R.string.driver_profile_password_not_match),
        PASSWORD_FIELD_EMPTY(R.string.driver_profile_password_field_empty);

        private int mStringId;

        Error(int stringId) {
            mStringId = stringId;
        }

        public int getStringId() {
            return mStringId;
        }
    }

    void setUserInfo(UserEntity user);
    void setHomeTerminalsSpinner(List<String> homeTerminalNames, int selectedTerminal);
    void setHomeTerminalInfo(HomeTerminalEntity homeTerminal);
    void setCarrierInfo(CarrierEntity carrier);
    void setCycleInfo(List<String> cycles, int selectedCycle);
    void showPasswordChanged();
    void setResults(User user);
    void showSignatureChanged();

    void showError(RetrofitException error);
    void showError(Error error);
}
