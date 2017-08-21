package com.bsmwireless.widgets.logs;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import static com.bsmwireless.widgets.alerts.DutyType.PERSONAL_USE;
import static com.bsmwireless.widgets.alerts.DutyType.YARD_MOVES;

public class DutyColors {
    private int[] mDutyColors;

    public DutyColors(Context context) {
        mDutyColors = new int[DutyType.values().length];

        //initialize duty state colors
        for (int i = 0; i < DutyType.values().length; i++) {
            mDutyColors[i] = ContextCompat.getColor(context, DutyType.values()[i].getColor());
        }
    }

    public int getColor(int eventType, int eventCode) {
        int color = mDutyColors[0];
        if (eventType == ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue()) {
            color = mDutyColors[eventCode - 1];
        } else if (eventType == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
            if (eventCode == ELDEvent.DriverIndicationCode.PERSONAL_USE_ON.getValue()) {
                color = mDutyColors[PERSONAL_USE.ordinal()];
            } else if (eventCode == ELDEvent.DriverIndicationCode.YARD_MOVES_ON.getValue()) {
                color = mDutyColors[YARD_MOVES.ordinal()];
            }
        }
        return color;
    }

    public int getColor(int eventCode) {
        return mDutyColors[eventCode - 1];
    }

    public int getColor(DutyType type) {
        return mDutyColors[type.getValue() - 1];
    }
}
