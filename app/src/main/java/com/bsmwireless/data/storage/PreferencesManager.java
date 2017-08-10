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
    private static final String KEY_SELECTED_KM_UNITS = "selected_km_units";


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
}
