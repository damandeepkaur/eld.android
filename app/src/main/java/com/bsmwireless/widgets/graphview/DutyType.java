package com.bsmwireless.widgets.graphview;

import app.bsmuniversal.com.R;

public enum DutyType {

    OFF_DUTY(101, "OFF DUTY", R.color.offduty_light),
    SLEEPER_BERTH(102, "SLEEPER BERTH", R.color.sleepingberth_light),
    DRIVING(103, "DRIVING", R.color.driving_light),
    ON_DUTY(104, "ON DUTY", R.color.onduty_light);

    private int mType;
    private String mName;
    private int mColor;

    DutyType(int type, String name, int color) {
        mType = type;
        mName = name;
        mColor = color;
    }

    public int getTypeId() {
        return mType;
    }

    public static String getTypeNameById(int type) {
        for (DutyType t:
                DutyType.values()) {
            if (t.mType == type) {
                return t.mName;
            }
        }
        return "";
    }

    public static int getTypeColorById(int type) {
        for (DutyType t:
                DutyType.values()) {
            if (t.mType == type) {
                return t.mColor;
            }
        }
        return R.color.offduty_light;
    }

    public static DutyType getTypeById(int type) {
        for (DutyType t:
                DutyType.values()) {
            if (t.mType == type) {
                return t;
            }
        }
        return DutyType.OFF_DUTY;
    }
}
