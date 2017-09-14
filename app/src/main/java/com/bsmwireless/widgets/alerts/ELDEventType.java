package com.bsmwireless.widgets.alerts;


import app.bsmuniversal.com.R;

public enum ELDEventType {

    OFF_DUTY(1, 1, R.string.event_type_off_duty_description),
    SLEEPER_BERTH(1, 2, R.string.event_type_sleeper_berth_description),
    DRIVING(1, 3, R.string.event_type_driving_description),
    ON_DUTY_NOT_DRIVING(1, 4, R.string.event_type_on_duty_not_driving_description),
    LOG_CONVENTIONAL(2, 1, R.string.event_type_intermediate_log_conventional_description),
    LOG_REDUCED(2, 2, R.string.event_type_intermediate_log_reduced_description),
    AUTHORIZED_PERSONAL_USE(3, 1, R.string.event_type_driver_authorized_personal_use_description),
    YARD_MOVES(3, 2, R.string.event_type_driver_yard_moves_description),
    CLEARED(3, 0, R.string.event_type_driver_cleared_description),
    FIRST_CERTIFICATION(4, 1, R.string.event_type_driver_certification_daily_record_description),
    N_CERTIFICATION(4, 2, R.string.event_type_driver_n_certification_description),
    DRIVER_LOGIN(5, 1, R.string.event_type_authenticated_driver_login_description),
    DRIVER_LOGOUT(5, 2, R.string.event_type_authenticated_driver_logout_description),
    ENGINE_POWER_UP_CONVENTIONAL(6, 1, R.string.event_type_engine_power_up_conventional_description),
    ENGINE_POWER_UP_REDUCED(6, 2, R.string.event_type_engine_power_up_reduced_description),
    ENGINE_SHUT_DOWN_CONVENTIONAL(6, 3, R.string.event_type_engine_shut_down_conventional_description),
    ENGINE_SHUT_DOWN_REDUCED(6, 4, R.string.event_type_engine_shut_down_reduced_description),
    ENGINE_MALFUNCTION_LOGGED(7, 1, R.string.event_type_engine_eld_malfunction_logged_description),
    ENGINE_MALFUNCTION_CLEARED(7, 2, R.string.event_type_engine_eld_malfunction_cleared_description),
    DIAGNOSTIC_LOGGED(7, 3, R.string.event_type_diagnostic_logged_description),
    DIAGNOSTIC_CLEARED(7, 4, R.string.event_type_diagnostic_cleared_description);

    private int mType;
    private int mCode;
    private int mDescription;

    ELDEventType(int type, int code, int description) {
        mType = type;
        mCode = code;
        mDescription = description;
    }

    public static ELDEventType getTypeByCode(int type, int code) {
        for (ELDEventType t : ELDEventType.values()) {
            if (t.mCode == code && t.mType == type) {
                return t;
            }
        }
        return ELDEventType.OFF_DUTY;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public int getDescription() {
        return mDescription;
    }

    public void setDescription(int description) {
        this.mDescription = description;
    }
}




