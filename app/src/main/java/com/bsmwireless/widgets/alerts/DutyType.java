package com.bsmwireless.widgets.alerts;

import app.bsmuniversal.com.R;

public enum DutyType {
    OFF_DUTY(1, R.string.event_type_off_duty, R.color.offduty_light, R.drawable.ic_duty_status_of),
    SLEEPER_BERTH(2, R.string.event_type_sleeping, R.color.sleepingberth_light, R.drawable.ic_duty_status_sb),
    DRIVING(3, R.string.event_type_driving, R.color.driving_light, R.drawable.ic_duty_status_dr),
    ON_DUTY(4, R.string.event_type_on_duty, R.color.onduty_light, R.drawable.ic_duty_status_on),
    PERSONAL_USE(5, R.string.event_type_personal_use, R.color.driving_light, R.drawable.ic_duty_status_pu),
    YARD_MOVES(6, R.string.event_type_yard_moves, R.color.onduty_light, R.drawable.ic_duty_status_ym);

    private int mId;
    private int mName;
    private int mColor;
    private int mIcon;

    DutyType(int type, int name, int color, int icon) {
        mId = type;
        mName = name;
        mColor = color;
        mIcon = icon;
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

    public int getIcon() {
        return mIcon;
    }
}
