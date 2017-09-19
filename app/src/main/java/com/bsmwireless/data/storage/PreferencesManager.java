package com.bsmwireless.data.storage;

import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String KEY_DRIVER_ACCOUNT_NAME = "driver_account_name";
    private static final String KEY_USER_ACCOUNT_NAME = "user_account_name";
    private static final String KEY_DRIVER_ID = "driver_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_SELECTED_VEHICLE_ID = "selected_vehicle_id";
    private static final String KEY_SELECTED_BOX_ID = "selected_box_id";
    private static final String KEY_REMEMBER_USER_ENABLED = "keep_user_enabled";
    private static final String KEY_SHOW_HOME_SCREEN_ENABLED = "show_home_screen_enabled";
    private static final String KEY_BOX_GPS_ENABLED = "keep_box_gps_enabled";
    private static final String KEY_FIXED_AMOUNT_ENABLED = "keep_fixed_amount_enabled";
    private static final String KEY_SELECTED_KM_UNITS = "selected_km_units";
    private static final String KEY_SHOW_SELECT_ASSET_SCREEN_ENABLED = "show_select_asset_screen_enabled";
    //TODO: validate from server
    public static final String KEY_TIME_ON_DUTY = "time_on_duty";
    public static final String KEY_TIME_DRIVING = "time_driving";
    public static final String KEY_TIME_SLEEPER_BERTH = "time_sleeper_berth";

    public static final String KEY_DUTY_TYPE = "duty_type";
    private static final String KEY_DUTY_DAY = "duty_day";

    public static final int NOT_FOUND_VALUE = -1;

    private SharedPreferences mPreferences;

    public PreferencesManager(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    public void addListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void setDriverAccountName(String accountName) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_DRIVER_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    public String getDriverAccountName() {
        return mPreferences.getString(KEY_DRIVER_ACCOUNT_NAME, null);
    }

    public void setUserAccountName(String accountName) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_USER_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    public String getUserAccountName() {
        return mPreferences.getString(KEY_USER_ACCOUNT_NAME, null);
    }

    public void setDriverId(int driverId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_DRIVER_ID, driverId);
        editor.apply();
    }

    public int getDriverId() {
        return mPreferences.getInt(KEY_DRIVER_ID, NOT_FOUND_VALUE);
    }

    public void setUserId(int userId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        return mPreferences.getInt(KEY_USER_ID, NOT_FOUND_VALUE);
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
        return mPreferences.getBoolean(KEY_SHOW_HOME_SCREEN_ENABLED, true);
    }

    public void clearValues() {
        boolean rememberMeEnabled = isRememberUserEnabled();
        mPreferences.edit().clear().apply();
        setRememberUserEnabled(rememberMeEnabled);
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

    public void setOnDutyTime(long time) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(KEY_TIME_ON_DUTY, time);
        editor.apply();
    }

    public long getOnDutyTime() {
        return mPreferences.getLong(KEY_TIME_ON_DUTY, 0);
    }

    public void setDrivingTime(long time) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(KEY_TIME_DRIVING, time);
        editor.apply();
    }

    public long getDrivingTime() {
        return mPreferences.getLong(KEY_TIME_DRIVING, 0);
    }

    public void setSleeperBerthTime(long time) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(KEY_TIME_SLEEPER_BERTH, time);
        editor.apply();
    }

    public long getSleeperBerthTime() {
        return mPreferences.getLong(KEY_TIME_SLEEPER_BERTH, 0);
    }

    //Should be used via DutyTypeManager only
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

    public void setKMOdometerUnits(boolean kmOdometerUnitsSelected) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(KEY_SELECTED_KM_UNITS, kmOdometerUnitsSelected);
        editor.apply();
    }

    /**
     * Retrieves whether the user selected km
     * or mi odometer units. Default value will be km.
     *
     * @return True if km odometer units selected, or false for mi.
     */
    public boolean isKMOdometerUnitsSelected() {
        return mPreferences.getBoolean(KEY_SELECTED_KM_UNITS, true);
    }

    public void setShowSelectAssetScreenEnabled(boolean showSelectAssetScreen) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(KEY_SHOW_SELECT_ASSET_SCREEN_ENABLED, showSelectAssetScreen);
        editor.apply();
    }

    public boolean isShowSelectAssetScreenEnabled() {
        return mPreferences.getBoolean(KEY_SHOW_SELECT_ASSET_SCREEN_ENABLED, false);
    }
}
