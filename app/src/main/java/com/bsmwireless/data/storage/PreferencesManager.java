package com.bsmwireless.data.storage;

import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String KEY_ACCOUNT_NAME = "account_name";
    private static final String KEY_SELECTED_VEHICLE_ID = "selected_vehicle_id";
    private static final String KEY_SELECTED_BOX_ID = "selected_box_id";
    private static final String KEY_REMEMBER_USER_ENABLED = "keep_user_enabled";
    private static final String KEY_SHOW_HOME_SCREEN_ENABLED = "show_home_screen_enabled";
    private static final String KEY_BOX_GPS_ENABLED = "keep_box_gps_enabled";
    private static final String KEY_FIXED_AMOUNT_ENABLED = "keep_fixed_amount_enabled";
    //TODO: validate from server
    private static final String KEY_TIME_ON_DUTY = "time_on_duty";
    private static final String KEY_TIME_DRIVING = "time_driving";
    private static final String KEY_TIME_SLEEPER_BERTH = "time_sleeper_berth";

    public static final String KEY_DUTY_TYPE = "duty_type";
    private static final String KEY_DUTY_DAY = "duty_day";

    public static final int NOT_FOUND_VALUE = -1;

    private SharedPreferences mPreferences;

    public PreferencesManager(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    public String setAccountName(String accountName) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_ACCOUNT_NAME, accountName);
        editor.apply();

        return accountName;
    }

    public String getAccountName() {
        return mPreferences.getString(KEY_ACCOUNT_NAME, null);
    }

    public void setVehicleId(int vehicleId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_SELECTED_VEHICLE_ID, vehicleId);
        editor.apply();
    }

    public int getVehicleId() {
        return mPreferences.getInt(KEY_SELECTED_VEHICLE_ID, NOT_FOUND_VALUE);
    }

    public void setBoxId(int boxId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_SELECTED_BOX_ID, boxId);
        editor.apply();
    }

    public int getBoxId() {
        return mPreferences.getInt(KEY_SELECTED_BOX_ID, NOT_FOUND_VALUE);
    }

    public void setRememberUserEnabled(boolean rememberUserEnabled) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(KEY_REMEMBER_USER_ENABLED, rememberUserEnabled);
        editor.apply();
    }

    public boolean isRememberUserEnabled() {
        return mPreferences.getBoolean(KEY_REMEMBER_USER_ENABLED, true);
    }

    public void setShowHomeScreenEnabled(boolean showHomeScreen) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(KEY_SHOW_HOME_SCREEN_ENABLED, showHomeScreen);
        editor.apply();
    }

    public boolean isShowHomeScreenEnabled() {
        return mPreferences.getBoolean(KEY_SHOW_HOME_SCREEN_ENABLED, false);
    }

    public void clearValues() {
        mPreferences.edit().clear().apply();
    }

    public void setBoxGPSEnabled(boolean boxGPSEnabled) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(KEY_BOX_GPS_ENABLED, boxGPSEnabled);
        editor.apply();
    }

    public void setFixedAmountEnabled(boolean fixedAmountEnabled) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(KEY_FIXED_AMOUNT_ENABLED, fixedAmountEnabled);
        editor.apply();
    }

    public boolean isBoxGPSEnabled() {
        return mPreferences.getBoolean(KEY_BOX_GPS_ENABLED, false);
    }

    public boolean isFixedAmountEnabled() {
        return mPreferences.getBoolean(KEY_FIXED_AMOUNT_ENABLED, false);
    }

    public void setOnDutyTime(int time) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_TIME_ON_DUTY, time);
        editor.apply();
    }

    public int getOnDutyTime() {
        return mPreferences.getInt(KEY_TIME_ON_DUTY, 0);
    }

    public void setDrivingTime(int time) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_TIME_DRIVING, time);
        editor.apply();
    }

    public int getDrivingTime() {
        return mPreferences.getInt(KEY_TIME_DRIVING, 0);
    }

    public void setSleeperBerthTime(int time) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_TIME_SLEEPER_BERTH, time);
        editor.apply();
    }

    public int getSleeperBerthTime() {
        return mPreferences.getInt(KEY_TIME_SLEEPER_BERTH, 0);
    }

    //Should be used via DutyManager only
    public void setDutyType(int dutyType) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_DUTY_TYPE, dutyType);
        editor.apply();
    }

    public int getDutyType() {
        return mPreferences.getInt(KEY_DUTY_TYPE, 0);
    }

    public void setDutyDay(long day) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(KEY_DUTY_DAY, day);
        editor.apply();
    }

    public int getDutyDay() {
        return mPreferences.getInt(KEY_DUTY_DAY, 0);
    }

    public void addListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
