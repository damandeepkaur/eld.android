package com.bsmwireless.data.storage.eldevents;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "events")
public final class ELDEventEntity {
    public enum SyncType {
        SYNC,
        SEND,
        NEW_UNSYNC,
        UPDATE_UNSYNC
    }

    @PrimaryKey
    @ColumnInfo(name = "inner_id")
    private Integer mInnerId;
    @ColumnInfo(name = "id", index = true)
    private Integer mId;
    @ColumnInfo(name = "sync")
    private Integer mSync;
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
    @ColumnInfo(name = "log_sheet")
    private Long mLogSheet;
    @ColumnInfo(name = "odometer")
    private Integer mOdometer;
    @ColumnInfo(name = "engine_hours")
    private Integer mEngineHours;
    @ColumnInfo(name = "lat")
    private Double mLat;
    @ColumnInfo(name = "lng")
    private Double mLng;
    @ColumnInfo(name = "lat_ln_flag")
    private String mLatLnFlag;
    @ColumnInfo(name = "distance")
    private Integer mDistance;
    @ColumnInfo(name = "comment")
    private String mComment;
    @ColumnInfo(name = "location")
    private String mLocation;
    @ColumnInfo(name = "check_sum")
    private String mCheckSum;
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
    @ColumnInfo(name = "driver_id", index = true)
    private Integer mDriverId;
    @ColumnInfo(name = "sequence")
    private Integer mSequence;
    @ColumnInfo(name = "malfunction")
    private Boolean mMalfunction;
    @ColumnInfo(name = "diagnostic")
    private Boolean mDiagnostic;
    @ColumnInfo(name = "mal_code")
    private String mMalCode;
    @ColumnInfo(name = "latlng_code")
    private String mLatLngCode;

    public Integer getInnerId() {
        return mInnerId;
    }

    public void setInnerId(Integer innerId) {
        mInnerId = innerId;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Integer getSync() {
        return mSync;
    }

    public void setSync(Integer sync) {
        mSync = sync;
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

    public Long getLogSheet() {
        return mLogSheet;
    }

    public void setLogSheet(Long logSheet) {
        mLogSheet = logSheet;
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

    public String getLatLnFlag() {
        return mLatLnFlag;
    }

    public void setLatLnFlag(String latLnFlag) {
        this.mLatLnFlag = latLnFlag;
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

    public Integer getSequence() {
        return mSequence;
    }

    public void setSequence(Integer sequence) {
        this.mSequence = sequence;
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

    public String getMalCode() {
        return mMalCode;
    }

    public void setMalCode(String malCode) {
        this.mMalCode = malCode;
    }

    public String getLatLngCode() {
        return mLatLngCode;
    }

    public void setLatLngCode(String latLngCode) {
        mLatLngCode = latLngCode;
    }
}
