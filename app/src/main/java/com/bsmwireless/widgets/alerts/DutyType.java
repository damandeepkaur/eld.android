package com.bsmwireless.widgets.alerts;

import app.bsmuniversal.com.R;

public enum DutyType implements Type {
    ON_DUTY(1, 4, R.string.event_type_on_duty, R.string.event_type_on_duty,R.color.onduty_light, R.drawable.ic_duty_status_on, 4),
    OFF_DUTY(1, 1, R.string.event_type_off_duty, R.string.event_type_off_duty,R.color.offduty_light, R.drawable.ic_duty_status_of, 1),
    SLEEPER_BERTH(1, 2, R.string.event_type_sleeping, R.string.event_type_sleeping,R.color.sleepingberth_light, R.drawable.ic_duty_status_sb, 2),
    DRIVING(1, 3, R.string.event_type_driving, R.string.event_type_driving,R.color.driving_light, R.drawable.ic_duty_status_dr, 3),
    YARD_MOVES(3, 2, R.string.event_type_yard_moves, R.string.event_type_yard_moves_title, R.color.onduty_light, R.drawable.ic_duty_status_ym, 4),
    PERSONAL_USE(3, 1, R.string.event_type_personal_use, R.string.event_type_personal_use_title, R.color.offduty_light, R.drawable.ic_duty_status_pu, 1),
    CLEAR(3, 0, R.string.event_type_clear, R.string.event_type_clear, R.color.offduty_light, R.drawable.ic_duty_status_of, 1),
    CLEAR_YM(3, 0, R.string.event_type_clear, R.string.event_type_yard_moves_end_title, R.color.onduty_light, R.drawable.ic_duty_status_of, 4),
    CLEAR_PU(3, 0, R.string.event_type_clear, R.string.event_type_personal_use_end_title, R.color.offduty_light, R.drawable.ic_duty_status_of, 1);

    private int mCode;
    private int mType;
    private int mName;
    private int mTitle;
    private int mColor;
    private int mIcon;
    private int mOriginalCode;

    DutyType(int type, int code, int name, int title, int color, int icon, int originalCode) {
        mType = type;
        mCode = code;
        mName = name;
        mTitle = title;
        mColor = color;
        mIcon = icon;
        mOriginalCode = originalCode;
    }

    public static DutyType getDutyTypeByCode(int type, int code) {
        for (DutyType t : DutyType.values()) {
            if (t.mCode == code && t.mType == type) {
                return t;
            }
        }
        return OFF_DUTY;
    }

    @Override
    public int getCode() {
        return mCode;
    }

    @Override
    public int getName() {
        return mName;
    }

    @Override
    public int getTitle() {
        return mTitle;
    }

    @Override
    public int getColor() {
        return mColor;
    }

    @Override
    public int getIcon() {
        return mIcon;
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public int getOriginalCode() {
        return mOriginalCode;
    }

}
