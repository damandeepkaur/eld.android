package com.bsmwireless.screens.logs;

import java.util.List;

public class LogHeaderModel {
    private String mTimezone;
    private String mDriverName;
    private String mCoDriversName;
    private String mVehicleName;
    private String mVehicleLicense;
    private String mStartOdometer;
    private String mEndOdometer;
    private String mDistanceDriven;
    private String mCarrierName;
    private String mHomeTerminalName;
    private String mHomeTerminalAddress;
    private String mTrailers;
    private String mShippingId;
    private String mExemptions;

    public LogHeaderModel() {
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getDriverName() {
        return mDriverName;
    }

    public void setDriverName(String driverName) {
        mDriverName = driverName;
    }

    public String getCoDriversName() {
        return mCoDriversName;
    }

    public void setCoDriversName(String coDriversName) {
        mCoDriversName = coDriversName;
    }

    public String getVehicleName() {
        return mVehicleName;
    }

    public void setVehicleName(String vehicleName) {
        mVehicleName = vehicleName;
    }

    public String getVehicleLicense() {
        return mVehicleLicense;
    }

    public void setVehicleLicense(String vehicleLicense) {
        mVehicleLicense = vehicleLicense;
    }

    public String getStartOdometer() {
        return mStartOdometer;
    }

    public void setStartOdometer(String startOdometer) {
        mStartOdometer = startOdometer;
    }

    public String getEndOdometer() {
        return mEndOdometer;
    }

    public void setEndOdometer(String endOdometer) {
        mEndOdometer = endOdometer;
    }

    public String getDistanceDriven() {
        return mDistanceDriven;
    }

    public void setDistanceDriven(String distanceDriven) {
        mDistanceDriven = distanceDriven;
    }

    public String getCarrierName() {
        return mCarrierName;
    }

    public void setCarrierName(String carrierName) {
        mCarrierName = carrierName;
    }

    public String getHomeTerminalName() {
        return mHomeTerminalName;
    }

    public void setHomeTerminalName(String homeTerminalName) {
        mHomeTerminalName = homeTerminalName;
    }

    public String getHomeTerminalAddress() {
        return mHomeTerminalAddress;
    }

    public void setHomeTerminalAddress(String homeTerminalAddress) {
        mHomeTerminalAddress = homeTerminalAddress;
    }

    public String getTrailers() {
        return mTrailers;
    }

    public void setTrailers(String trailers) {
        mTrailers = trailers;
    }

    public String getShippingId() {
        return mShippingId;
    }

    public void setShippingId(String shippingId) {
        mShippingId = shippingId;
    }

    public String getExemptions() {
        return mExemptions;
    }

    public void setExemptions(String exemptions) {
        mExemptions = exemptions;
    }
}
