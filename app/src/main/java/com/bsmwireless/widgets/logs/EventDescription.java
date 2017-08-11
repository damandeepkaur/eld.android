package com.bsmwireless.widgets.logs;

import app.bsmuniversal.com.R;

public enum EventDescription {
    OFF_DUTY(1, 1, R.string.event_type_off_duty),
    SLEEPER_BERTH(1, 2, R.string.event_type_sleeping),
    DRIVING(1, 3, R.string.event_type_driving),
    ON_DUTY(1, 4, R.string.event_type_on_duty),
    INT_LOCATION1(2, -1, R.string.event_type_int_location),
    CERT(4, -1, R.string.event_type_cert),
    LOGIN(5, 1, R.string.event_type_login),
    LOGOUT(5, 2, R.string.event_type_logout),
    SHUT_DOWN1(6, 1, R.string.event_type_shut_down),
    SHUT_DOWN2(6, 2, R.string.event_type_shut_down),
    POWER_UP3(6, 3, R.string.event_type_power_up),
    POWER_UP4(6, 4, R.string.event_type_power_up);

    private int mType;
    private int mCode;
    private int mTitleResId;

    EventDescription(int type, int code, int titleResId) {
        mType = type;
        mCode = code;
        mTitleResId = titleResId;
    }

    public static int getTitle(int type, int code) {
        for (EventDescription event : EventDescription.values()) {
            if (event.mType == type && event.mCode == code) {
                return event.mTitleResId;
            }
        }
        return R.string.event_type_unknown_type;
    }
}
