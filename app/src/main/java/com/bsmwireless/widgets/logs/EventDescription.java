package com.bsmwireless.widgets.logs;

import app.bsmuniversal.com.R;

public enum EventDescription {
    OFF_DUTY(1, 1, R.string.event_type_off_duty),
    SLEEPER_BERTH(1, 2, R.string.event_type_sleeping),
    DRIVING(1, 3, R.string.event_type_driving),
    ON_DUTY(1, 4, R.string.event_type_on_duty),
    PERSONAL_USE(3, 1, R.string.event_type_personal_use_title),
    YARD_MOVES(3, 2, R.string.event_type_yard_moves_title);

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
        return R.string.event_type_off_duty;
    }
}
