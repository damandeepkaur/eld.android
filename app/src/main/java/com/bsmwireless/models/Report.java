package com.bsmwireless.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Report implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("lat")
    @Expose
    private Double mLat;
    @SerializedName("lng")
    @Expose
    private Double mLng;
    @SerializedName("address")
    @Expose
    private String mAddress;
    @SerializedName("starttime")
    @Expose
    private String mStartTime;
    @SerializedName("logtime")
    @Expose
    private String mLogTime;
    @SerializedName("odometer")
    @Expose
    private Integer mOdometer;
    @SerializedName("type")
    @Expose
    private String mType;
    @SerializedName("safe")
    @Expose
    private Boolean mSafe;
    @SerializedName("vehicle")
    @Expose
    private Vehicle mVehicle;
    @SerializedName("trailer")
    @Expose
    private ReportTrailer mTrailer;
    @SerializedName("checklist")
    @Expose
    private CheckList mCheckList;
    @SerializedName("defects")
    @Expose
    private List<Defect> mDefects = null;

    public final static Parcelable.Creator<Report> CREATOR = new Creator<Report>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Report createFromParcel(Parcel in) {
            Report instance = new Report();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mLat = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.mLng = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.mAddress = ((String) in.readValue((String.class.getClassLoader())));
            instance.mStartTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLogTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.mOdometer = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mType = ((String) in.readValue((String.class.getClassLoader())));
            instance.mSafe = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mVehicle = ((Vehicle) in.readValue((Vehicle.class.getClassLoader())));
            instance.mTrailer = ((ReportTrailer) in.readValue((ReportTrailer.class.getClassLoader())));
            instance.mCheckList = ((CheckList) in.readValue((CheckList.class.getClassLoader())));
            in.readList(instance.mDefects, (Defect.class.getClassLoader()));
            return instance;
        }

        public Report[] newArray(int size) {
            return (new Report[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double lat) {
        this.mLat = lat;
    }

    public Double getLng() {
        return mLng;
    }

    public void setLng(Double lng) {
        this.mLng = lng;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public String getLogTime() {
        return mLogTime;
    }

    public void setLogTime(String logTime) {
        this.mLogTime = logTime;
    }

    public Integer getOdometer() {
        return mOdometer;
    }

    public void setOdometer(Integer odometer) {
        this.mOdometer = odometer;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public Boolean getSafe() {
        return mSafe;
    }

    public void setSafe(Boolean safe) {
        this.mSafe = safe;
    }

    public Vehicle getVehicle() {
        return mVehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.mVehicle = vehicle;
    }

    public ReportTrailer getTrailer() {
        return mTrailer;
    }

    public void setTrailer(ReportTrailer trailer) {
        this.mTrailer = trailer;
    }

    public CheckList getCheckList() {
        return mCheckList;
    }

    public void setCheckList(CheckList checkList) {
        this.mCheckList = checkList;
    }

    public List<Defect> getDefects() {
        return mDefects;
    }

    public void setDefects(List<Defect> defects) {
        this.mDefects = defects;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Report{");
        sb.append("mId=").append(mId);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLng=").append(mLng);
        sb.append(", mAddress='").append(mAddress).append('\'');
        sb.append(", mStartTime='").append(mStartTime).append('\'');
        sb.append(", mLogTime='").append(mLogTime).append('\'');
        sb.append(", mOdometer=").append(mOdometer);
        sb.append(", mType='").append(mType).append('\'');
        sb.append(", mSafe=").append(mSafe);
        sb.append(", mVehicle=").append(mVehicle);
        sb.append(", mTrailer=").append(mTrailer);
        sb.append(", mCheckList=").append(mCheckList);
        sb.append(", mDefects=").append(mDefects);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId)
                .append(mLat)
                .append(mLng)
                .append(mAddress)
                .append(mStartTime)
                .append(mLogTime)
                .append(mOdometer)
                .append(mType)
                .append(mSafe)
                .append(mVehicle)
                .append(mTrailer)
                .append(mCheckList)
                .append(mDefects)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Report)) {
            return false;
        }
        Report rhs = ((Report) other);
        return new EqualsBuilder().append(mId, rhs.mId)
                .append(mLat, rhs.mLat)
                .append(mLng, rhs.mLng)
                .append(mAddress, rhs.mAddress)
                .append(mStartTime, rhs.mStartTime)
                .append(mLogTime, rhs.mLogTime)
                .append(mOdometer, rhs.mOdometer)
                .append(mType, rhs.mType)
                .append(mSafe, rhs.mSafe)
                .append(mVehicle, rhs.mVehicle)
                .append(mTrailer, rhs.mTrailer)
                .append(mCheckList, rhs.mCheckList)
                .append(mDefects, rhs.mDefects)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mLat);
        dest.writeValue(mLng);
        dest.writeValue(mAddress);
        dest.writeValue(mStartTime);
        dest.writeValue(mLogTime);
        dest.writeValue(mOdometer);
        dest.writeValue(mType);
        dest.writeValue(mSafe);
        dest.writeValue(mVehicle);
        dest.writeValue(mTrailer);
        dest.writeValue(mCheckList);
        dest.writeList(mDefects);
    }

    public int describeContents() {
        return  0;
    }
}
