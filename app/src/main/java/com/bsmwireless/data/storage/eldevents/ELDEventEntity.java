package com.bsmwireless.data.storage.eldevents;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "events")
public class ELDEventEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;
    @ColumnInfo(name = "is_sync")
    private boolean mIsSync = true;
    @ColumnInfo(name = "event_type")
    private Integer mEventType;
    @ColumnInfo(name = "event_code")
    private Integer mEventCode;
    @ColumnInfo(name = "status")
    private Integer mStatus;
    @ColumnInfo(name = "origin")
    private Integer mOrigin;
    @ColumnInfo(name = "event_time")
    private Long mEventTime;
    @ColumnInfo(name = "odometer")
    private Integer mOdometer;
    @ColumnInfo(name = "engine_hours")
    private Integer mEngineHours;
    @ColumnInfo(name = "lat")
    private Double mLat;
    @ColumnInfo(name = "lng")
    private Double mLng;
    @ColumnInfo(name = "distance")
    private Integer mDistance;
    @ColumnInfo(name = "comment")
    private String mComment;
    @ColumnInfo(name = "location")
    private String mLocation;
    @ColumnInfo(name = "check_sum")
    private String mCheckSum;
    @ColumnInfo(name = "shipping_id")
    private String mShippingId;
    @ColumnInfo(name = "co_driver_id")
    private Integer mCoDriverId;
    @ColumnInfo(name = "box_id")
    private Integer mBoxId;
    @ColumnInfo(name = "vehicle_id")
    private Integer mVehicleId;
    @ColumnInfo(name = "tz_offset")
    private Double mTzOffset;
    @ColumnInfo(name = "timezone")
    private String mTimezone;
    @ColumnInfo(name = "mobile_time")
    private Long mMobileTime;
    @ColumnInfo(name = "driver_id")
    private Integer mDriverId;
    @ColumnInfo(name = "malfunction")
    private Boolean mMalfunction;
    @ColumnInfo(name = "diagnostic")
    private Boolean mDiagnostic;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public boolean isIsSync() {
        return mIsSync;
    }

    public void setIsSync(boolean sync) {
        mIsSync = sync;
    }

    public Integer getEventType() {
        return mEventType;
    }

    public void setEventType(Integer eventType) {
        mEventType = eventType;
    }

    public Integer getEventCode() {
        return mEventCode;
    }

    public void setEventCode(Integer eventCode) {
        mEventCode = eventCode;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(Integer status) {
        mStatus = status;
    }

    public Integer getOrigin() {
        return mOrigin;
    }

    public void setOrigin(Integer origin) {
        mOrigin = origin;
    }

    public Long getEventTime() {
        return mEventTime;
    }

    public void setEventTime(Long eventTime) {
        mEventTime = eventTime;
    }

    public Integer getOdometer() {
        return mOdometer;
    }

    public void setOdometer(Integer odometer) {
        mOdometer = odometer;
    }

    public Integer getEngineHours() {
        return mEngineHours;
    }

    public void setEngineHours(Integer engineHours) {
        mEngineHours = engineHours;
    }

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double lat) {
        mLat = lat;
    }

    public Double getLng() {
        return mLng;
    }

    public void setLng(Double lng) {
        mLng = lng;
    }

    public Integer getDistance() {
        return mDistance;
    }

    public void setDistance(Integer distance) {
        mDistance = distance;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getCheckSum() {
        return mCheckSum;
    }

    public void setCheckSum(String checkSum) {
        mCheckSum = checkSum;
    }

    public String getShippingId() {
        return mShippingId;
    }

    public void setShippingId(String shippingId) {
        mShippingId = shippingId;
    }

    public Integer getCoDriverId() {
        return mCoDriverId;
    }

    public void setCoDriverId(Integer coDriverId) {
        mCoDriverId = coDriverId;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        mBoxId = boxId;
    }

    public Integer getVehicleId() {
        return mVehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        mVehicleId = vehicleId;
    }

    public Double getTzOffset() {
        return mTzOffset;
    }

    public void setTzOffset(Double tzOffset) {
        mTzOffset = tzOffset;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public Long getMobileTime() {
        return mMobileTime;
    }

    public void setMobileTime(Long mobileTime) {
        mMobileTime = mobileTime;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        mDriverId = driverId;
    }

    public Boolean getMalfunction() {
        return mMalfunction;
    }

    public void setMalfunction(Boolean malfunction) {
        mMalfunction = malfunction;
    }

    public Boolean getDiagnostic() {
        return mDiagnostic;
    }

    public void setDiagnostic(Boolean diagnostic) {
        mDiagnostic = diagnostic;
    }
}
