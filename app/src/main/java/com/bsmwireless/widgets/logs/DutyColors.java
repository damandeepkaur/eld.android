package com.bsmwireless.widgets.logs;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

public class DutyColors {
    private int[] mDutyColors;

    public DutyColors(Context context) {
        mDutyColors = new int[4];

        //initialize duty state colors
        mDutyColors[0] = ContextCompat.getColor(context, DutyType.OFF_DUTY.getColor());
        mDutyColors[1] = ContextCompat.getColor(context, DutyType.SLEEPER_BERTH.getColor());
        mDutyColors[2] = ContextCompat.getColor(context, DutyType.DRIVING.getColor());
        mDutyColors[3] = ContextCompat.getColor(context, DutyType.ON_DUTY.getColor());
    }

    public int getColor(int eventType, int eventCode) {
        if (eventType == ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue()) {
            return mDutyColors[eventCode - 1];
        } else {
            return mDutyColors[0];
        }
    }

    public int getColor(int eventCode) {
        return mDutyColors[eventCode - 1];
    }

    public int getColor(DutyType type) {
        return mDutyColors[type.getValue() - 1];
    }
}
