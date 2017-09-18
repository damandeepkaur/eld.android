package com.bsmwireless.data.storage.logsheets;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;

import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.models.HomeTerminal;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "log_sheet_header")
public final class LogSheetEntity {
    @PrimaryKey
    @ColumnInfo(name = "log_day")
    private Long mLogDay;
    @ColumnInfo(name = "driver_id")
    private Integer mDriverId;
    @ColumnInfo(name = "vehicle_id")
    private Integer mVehicleId;
    @ColumnInfo(name = "box_id")
    private Integer mBoxId;
    @ColumnInfo(name = "start_of_day")
    private Long mStartOfDay;
    @ColumnInfo(name = "shipping_id")
    private String mShippingId;
    @ColumnInfo(name = "trailer_ids")
    private String mTrailerIds;
    @ColumnInfo(name = "co_driver_ids")
    private String mCoDriverIds;
    @ColumnInfo(name = "comment")
    private String mComment;
    @ColumnInfo(name = "duty_cycle")
    private String mDutyCycle;
    @Embedded
    private HomeTerminalEntity mHomeTerminal;
    @ColumnInfo(name = "additions")
    private String mAdditions;
    @ColumnInfo(name = "signed")
    private Boolean mSigned;

    public Long getLogDay() {
        return mLogDay;
    }

    public void setLogDay(Long logDay) {
        mLogDay = logDay;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        mDriverId = driverId;
    }

    public Integer getVehicleId() {
        return mVehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        mVehicleId = vehicleId;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        mBoxId = boxId;
    }

    public Long getStartOfDay() {
        return mStartOfDay;
    }

    public void setStartOfDay(Long startOfDay) {
        mStartOfDay = startOfDay;
    }

    public String getShippingId() {
        return mShippingId;
    }

    public void setShippingId(String shippingId) {
        mShippingId = shippingId;
    }

    public String getTrailerIds() {
        return mTrailerIds;
    }

    public void setTrailerIds(String trailerIds) {
        mTrailerIds = trailerIds;
    }

    public String getCoDriverIds() {
        return mCoDriverIds;
    }

    public void setCoDriverIds(String coDriverIds) {
        mCoDriverIds = coDriverIds;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getDutyCycle() {
        return mDutyCycle;
    }

    public void setDutyCycle(String dutyCycle) {
        mDutyCycle = dutyCycle;
    }

    public HomeTerminalEntity getHomeTerminal() {
        return mHomeTerminal;
    }

    public void setHomeTerminal(HomeTerminalEntity homeTerminal) {
        mHomeTerminal = homeTerminal;
    }

    public String getAdditions() {
        return mAdditions;
    }

    public void setAdditions(String additions) {
        mAdditions = additions;
    }

    public Boolean getSigned() {
        return mSigned;
    }

    public void setSigned(Boolean signed) {
        mSigned = signed;
    }
}
