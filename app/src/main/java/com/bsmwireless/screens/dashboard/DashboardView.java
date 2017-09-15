package com.bsmwireless.screens.dashboard;

import com.bsmwireless.widgets.alerts.DutyType;

import app.bsmuniversal.com.R;

public interface DashboardView {
    enum Error {
        INVALID_COMMENT_LENGTH(R.string.edit_event_comment_length_error),
        INVALID_COMMENT(R.string.edit_event_comment_error),
        VALID_COMMENT(R.string.edit_event_valid_comment);

        private int mStringId;

        Error(int stringId) {
            mStringId = stringId;
        }

        public int getStringId() {
            return mStringId;
        }
    }

    void setDutyType(DutyType dutyType);
    void showDutyTypeDialog();
    void showNotInVehicleDialog();
}
