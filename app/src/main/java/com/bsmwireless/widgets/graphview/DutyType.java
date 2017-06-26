package com.bsmwireless.widgets.graphview;

import app.bsmuniversal.com.R;

public enum DutyType {

    OFF_DUTY(101, R.string.hos_txt_offduty, R.color.offduty_light),
    SLEEPER_BERTH(102, R.string.hos_txt_sleepingberth, R.color.sleepingberth_light),
    DRIVING(103, R.string.hos_txt_driving, R.color.driving_light),
    ON_DUTY(104, R.string.hos_txt_onduty, R.color.onduty_light);

    private int mType;
    private int mNameRes;
    private int mColor;

    DutyType(int type, int nameRes, int color) {
        mType = type;
        mNameRes = nameRes;
        mColor = color;
    }

    public int getTypeId() {
        return mType;
    }

    public static int getTypeNameResById(int type) {
        for (DutyType t:
                DutyType.values()) {
            if (t.mType == type) {
                return t.mNameRes;
            }
        }
        return R.string.hos_txt_offduty;
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
