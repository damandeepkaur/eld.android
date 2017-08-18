package com.bsmwireless.widgets.alerts;

import com.bsmwireless.models.ELDEvent;

import app.bsmuniversal.com.R;

public enum DutyType {
    OFF_DUTY(1, R.string.event_type_off_duty, R.color.offduty_light, R.drawable.ic_duty_status_of),
    SLEEPER_BERTH(2, R.string.event_type_sleeping, R.color.sleepingberth_light, R.drawable.ic_duty_status_sb),
    DRIVING(3, R.string.event_type_driving, R.color.driving_light, R.drawable.ic_duty_status_dr),
    ON_DUTY(4, R.string.event_type_on_duty, R.color.onduty_light, R.drawable.ic_duty_status_on),
    PERSONAL_USE(1, R.string.event_type_personal_use, R.color.offduty_light, R.drawable.ic_duty_status_pu),
    YARD_MOVES(2, R.string.event_type_yard_moves, R.color.onduty_light, R.drawable.ic_duty_status_ym);

    private int mCode;
    private int mName;
    private int mColor;
    private int mIcon;

    DutyType(int code, int name, int color, int icon) {
        mCode = code;
        mName = name;
        mColor = color;
        mIcon = icon;
    }

    public static int getNameByCode(int id) {
        for (DutyType t : DutyType.values()) {
            if (t.mCode == id) {
                return t.mName;
            }
        }
        return R.string.event_type_off_duty;
    }

    public static int getColorByCode(int id) {
        for (DutyType t : DutyType.values()) {
            if (t.mCode == id) {
                return t.mColor;
            }
        }
        return R.color.offduty_light;
    }

    private static DutyType getTypeByCode(int id) {
        for (DutyType t : DutyType.values()) {
            if (t.mCode == id) {
                return t;
            }
        }
        return DutyType.OFF_DUTY;
    }

    public static DutyType getTypeByCode(int type, int code) {
        if (type == ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue()) {
            return DutyType.getTypeByCode(code);
        } else if (type == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
            if (code == DutyType.PERSONAL_USE.getValue()) {
                return DutyType.PERSONAL_USE;
            } else {
                return DutyType.YARD_MOVES;
            }
        }
        return OFF_DUTY;
    }

    public int getValue() {
        return mCode;
    }

    public int getName() {
        return mName;
    }

    public int getColor() {
        return mColor;
    }

    public int getIcon() {
        return mIcon;
    }
}
