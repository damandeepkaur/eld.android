package com.bsmwireless.widgets.alerts;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.R;

public enum DutyType {
    OFF_DUTY(1, R.string.event_type_off_duty, R.color.offduty_light),
    SLEEPER_BERTH(2, R.string.event_type_sleeping, R.color.sleepingberth_light),
    DRIVING(3, R.string.event_type_driving, R.color.driving_light),
    ON_DUTY(4, R.string.event_type_on_duty, R.color.onduty_light);

    private int mId;
    private int mName;
    private int mColor;

    DutyType(int type, int name, int color) {
        mId = type;
        mName = name;
        mColor = color;
    }

    public static int getNameById(int id) {
        for (DutyType t : DutyType.values()) {
            if (t.mId == id) {
                return t.mName;
            }
        }
        return R.string.event_type_off_duty;
    }

    public static int getColorById(int id) {
        for (DutyType t : DutyType.values()) {
            if (t.mId == id) {
                return t.mColor;
            }
        }
        return R.color.offduty_light;
    }

    public static DutyType getTypeById(int id) {
        for (DutyType t : DutyType.values()) {
            if (t.mId == id) {
                return t;
            }
        }
        return DutyType.OFF_DUTY;
    }

    public int getId() {
        return mId;
    }

    public int getName() {
        return mName;
    }

    public int getColor() {
        return mColor;
    }
}
