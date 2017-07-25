package com.bsmwireless.data.storage;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bsmwireless.common.App;

public class PreferencesManager {
    private static final String KEY_ACCOUNT_NAME = "account_name";
    private static final String KEY_SELECTED_VEHICLE_ID = "selected_vehicle_id";
    private static final String KEY_SELECTED_BOX_ID = "selected_box_id";
    private static final String KEY_REMEMBER_USER_ENABLED = "keep_user_enabled";

    private static final String PREF_BOX_GPS = "pref_box_gps";
    private static final String PREF_FIXED_AMOUNT = "pref_fixed_amount";
    private static final String PREF_ODOMETER_UNITS = "pref_odometer_units";

    public static final int NOT_FOUND_VALUE = -1;
    public static final String NOT_FOUND_PREF_VALUE = "";

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
        return mPreferences.getBoolean(KEY_REMEMBER_USER_ENABLED, false);
    }

    public void clearValues() {
        mPreferences.edit().clear().apply();
    }

    /**
     * Helper method to register a preferences listener.
     *
     * @param listener Listener to register.
     */
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getComponent().context());
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Helper method to un-register a preferences listener typically registered with
     * registerOnSharedPreferenceChangeListener method.
     *
     * @param listener Listener to un-register.
     */
    public void unRegisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getComponent().context());
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public String getPrefBoxGPS() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.getComponent().context());
        return sharedPref.getString(PREF_BOX_GPS, NOT_FOUND_PREF_VALUE);
    }

    public String getPrefFixedAmount() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.getComponent().context());
        return sharedPref.getString(PREF_FIXED_AMOUNT, NOT_FOUND_PREF_VALUE);
    }

    public String getPrefOdometerUnits() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.getComponent().context());
        return sharedPref.getString(PREF_ODOMETER_UNITS, NOT_FOUND_PREF_VALUE);
    }
}
