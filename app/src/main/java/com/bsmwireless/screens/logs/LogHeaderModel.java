package com.bsmwireless.screens.logs;


import android.os.Parcel;
import android.os.Parcelable;

public final class LogHeaderModel implements Parcelable {
    public static final Creator<LogHeaderModel> CREATOR = new Creator<LogHeaderModel>() {
        @Override
        public LogHeaderModel createFromParcel(Parcel source) {
            return new LogHeaderModel(source);
        }

        @Override
        public LogHeaderModel[] newArray(int size) {
            return new LogHeaderModel[size];
        }
    };
    private Long mLogDay;
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
        this.mLogDay = (Long) in.readValue(Long.class.getClassLoader());
        this.mTimezone = in.readString();
        this.mDriverName = in.readString();
        this.mCoDriversName = in.readString();
        this.mVehicleName = in.readString();
        this.mVehicleLicense = in.readString();
        this.mStartOdometer = in.readString();
        this.mEndOdometer = in.readString();
        this.mDistanceDriven = in.readString();
        this.mCarrierName = in.readString();
        this.mHomeTerminalName = in.readString();
        this.mHomeTerminalAddress = in.readString();
        this.mTrailers = in.readString();
        this.mShippingId = in.readString();
        this.mAllExemptions = in.readString();
        this.mSelectedExemptions = in.readString();
    }

    public Long getLogDay() {
        return mLogDay;
    }

    public void setLogDay(Long logDay) {
        this.mLogDay = logDay;
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
        sb.append("mLogDay=").append(mLogDay);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mLogDay);
        dest.writeString(this.mTimezone);
        dest.writeString(this.mDriverName);
        dest.writeString(this.mCoDriversName);
        dest.writeString(this.mVehicleName);
        dest.writeString(this.mVehicleLicense);
        dest.writeString(this.mStartOdometer);
        dest.writeString(this.mEndOdometer);
        dest.writeString(this.mDistanceDriven);
        dest.writeString(this.mCarrierName);
        dest.writeString(this.mHomeTerminalName);
        dest.writeString(this.mHomeTerminalAddress);
        dest.writeString(this.mTrailers);
        dest.writeString(this.mShippingId);
        dest.writeString(this.mAllExemptions);
        dest.writeString(this.mSelectedExemptions);
    }
}

