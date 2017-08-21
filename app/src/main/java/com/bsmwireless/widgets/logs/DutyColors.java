package com.bsmwireless.widgets.logs;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

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
