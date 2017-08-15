package com.bsmwireless.screens.editevent;

import android.app.TimePickerDialog;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

public interface EditEventView {
    void getExtrasFromIntent();
    void setStartTime(String time);
    void setStatus(DutyType type);
    void setComment(String comment);
    void setAddress(String address);
    void openTimePickerDialog(TimePickerDialog.OnTimeSetListener listener, int hours, int minutes);
    void addEvent(ELDEvent newELDEvent);
    void changeEvent(ELDEvent oldEvent, ELDEvent newELDEvent);
}
