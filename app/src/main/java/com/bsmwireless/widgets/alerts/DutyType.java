package com.bsmwireless.widgets.alerts;

import app.bsmuniversal.com.R;

public enum DutyType {
    ON_DUTY(1, 4, R.string.event_type_on_duty, R.color.onduty_light, R.drawable.ic_duty_status_on),
    OFF_DUTY(1, 1, R.string.event_type_off_duty, R.color.offduty_light, R.drawable.ic_duty_status_of),
    SLEEPER_BERTH(1, 2, R.string.event_type_sleeping, R.color.sleepingberth_light, R.drawable.ic_duty_status_sb),
    DRIVING(1, 3, R.string.event_type_driving, R.color.driving_light, R.drawable.ic_duty_status_dr),
    YARD_MOVES(3, 2, R.string.event_type_yard_moves, R.color.onduty_light, R.drawable.ic_duty_status_ym),
    PERSONAL_USE(3, 1, R.string.event_type_personal_use, R.color.offduty_light, R.drawable.ic_duty_status_pu),
    CLEAR(3, 0, R.string.event_type_clear, R.color.offduty_light, R.drawable.ic_duty_status_of);

    private int mCode;
    private int mType;
    private int mName;
    private int mColor;
    private int mIcon;

    DutyType(int type, int code, int name, int color, int icon) {
        mType = type;
        mCode = code;
        mName = name;
        mColor = color;
        mIcon = icon;
    }

    public static DutyType getTypeByCode(int type, int code) {
        for (DutyType t : DutyType.values()) {
            if (t.mCode == code && t.mType == type) {
                return t;
            }
        }
        return DutyType.OFF_DUTY;
    }

    public int getCode() {
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

    public int getType() {
        return mType;
    }
}
