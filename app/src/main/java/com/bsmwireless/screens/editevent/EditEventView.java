package com.bsmwireless.screens.editevent;

import android.app.TimePickerDialog;

import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;

import app.bsmuniversal.com.R;

public interface EditEventView extends BaseMenuView {
    enum Error {
        ERROR_INVALID_TIME(R.string.edit_event_error_invalid_time),
        SERVER_ERROR(R.string.edit_event_server_error),
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
    void getExtrasFromIntent();
    void setStartTime(String time);
    void setStatus(DutyType type);
    void setComment(String comment);
    void setAddress(String address);
    void openTimePickerDialog(TimePickerDialog.OnTimeSetListener listener, int hours, int minutes);
    void changeEvent(ArrayList<ELDEvent> newELDEvent);
    void showError(Error error);
    void showError(RetrofitException error);
}
