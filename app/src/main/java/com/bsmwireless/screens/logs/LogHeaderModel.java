package com.bsmwireless.screens.logs;


import android.os.Parcel;
import android.os.Parcelable;

import com.bsmwireless.models.LogSheetHeader;

public class LogHeaderModel implements Parcelable {
    private LogSheetHeader mLogSheetHeader;
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
    private String mAllExemptions;
    private String mSelectedExemptions;

    public LogHeaderModel() {
    }

    protected LogHeaderModel(Parcel in) {
        mLogSheetHeader = in.readParcelable(LogSheetHeader.class.getClassLoader());
        mTimezone = in.readString();
        mDriverName = in.readString();
        mCoDriversName = in.readString();
        mVehicleName = in.readString();
        mVehicleLicense = in.readString();
        mStartOdometer = in.readString();
        mEndOdometer = in.readString();
        mDistanceDriven = in.readString();
        mCarrierName = in.readString();
        mHomeTerminalName = in.readString();
        mHomeTerminalAddress = in.readString();
        mTrailers = in.readString();
        mShippingId = in.readString();
        mAllExemptions = in.readString();
        mSelectedExemptions = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mLogSheetHeader, flags);
        dest.writeString(mTimezone);
        dest.writeString(mDriverName);
        dest.writeString(mCoDriversName);
        dest.writeString(mVehicleName);
        dest.writeString(mVehicleLicense);
        dest.writeString(mStartOdometer);
        dest.writeString(mEndOdometer);
        dest.writeString(mDistanceDriven);
        dest.writeString(mCarrierName);
        dest.writeString(mHomeTerminalName);
        dest.writeString(mHomeTerminalAddress);
        dest.writeString(mTrailers);
        dest.writeString(mShippingId);
        dest.writeString(mAllExemptions);
        dest.writeString(mSelectedExemptions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LogHeaderModel> CREATOR = new Creator<LogHeaderModel>() {
        @Override
        public LogHeaderModel createFromParcel(Parcel in) {
            return new LogHeaderModel(in);
        }

        @Override
        public LogHeaderModel[] newArray(int size) {
            return new LogHeaderModel[size];
        }
    };

    public LogSheetHeader getLogSheetHeader() {
        return mLogSheetHeader;
    }

    public void setLogSheetHeader(LogSheetHeader logSheetHeader) {
        mLogSheetHeader = logSheetHeader;
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

    public String getAllExemptions() {
        return mAllExemptions;
    }

    public void setAllExemptions(String allExemptions) {
        mAllExemptions = allExemptions;
    }

    public String getSelectedExemptions() {
        return mSelectedExemptions;
    }

    public void setSelectedExemptions(String selectedExamptions) {
        mSelectedExemptions = selectedExamptions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LogHeaderModel{");
        sb.append("mLogSheetHeader=").append(mLogSheetHeader);
        sb.append(", mTimezone='").append(mTimezone).append('\'');
        sb.append(", mDriverName='").append(mDriverName).append('\'');
        sb.append(", mCoDriversName='").append(mCoDriversName).append('\'');
        sb.append(", mVehicleName='").append(mVehicleName).append('\'');
        sb.append(", mVehicleLicense='").append(mVehicleLicense).append('\'');
        sb.append(", mStartOdometer='").append(mStartOdometer).append('\'');
        sb.append(", mEndOdometer='").append(mEndOdometer).append('\'');
        sb.append(", mDistanceDriven='").append(mDistanceDriven).append('\'');
        sb.append(", mCarrierName='").append(mCarrierName).append('\'');
        sb.append(", mHomeTerminalName='").append(mHomeTerminalName).append('\'');
        sb.append(", mHomeTerminalAddress='").append(mHomeTerminalAddress).append('\'');
        sb.append(", mTrailers='").append(mTrailers).append('\'');
        sb.append(", mShippingId='").append(mShippingId).append('\'');
        sb.append(", mAllExemptions='").append(mAllExemptions).append('\'');
        sb.append(", mSelectedExemptions='").append(mSelectedExemptions).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

