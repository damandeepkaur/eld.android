package com.bsmwireless.models;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class BlackBoxModel {
    private int mOdometer;
    private double mLat;
    private double mLon;
    private int mSpeed;
    private int mHeading;
    private int mTERT;
    private BlackBoxResponseModel.ResponseType mResponseType;
    private Date mEventTimeUTC;
    private int mSequenceNum;
    private long mBoxId;
    private int mTDMsgQueue;
    private int mSensorState;
    private int mEngineHours;

    public int getOdometer() {
        return mOdometer;
    }

    public void setOdometer(int odometer) {
        mOdometer = odometer;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public int getEngineHours() {
        return mEngineHours;
    }

    public void setEngineHours(int engineHours) {
        mEngineHours = engineHours;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public int getHeading() {
        return mHeading;
    }

    public void setHeading(int heading) {
        this.mHeading = heading;
    }

    public int getTERT() {
        return mTERT;
    }

    public void setTERT(int TERT) {
        this.mTERT = TERT;
    }

    public BlackBoxResponseModel.ResponseType getResponseType() {
        return mResponseType;
    }

    public void setResponseType(BlackBoxResponseModel.ResponseType responseType) {
        this.mResponseType = responseType;
    }

    public Date getEventTimeUTC() {
        return mEventTimeUTC;
    }

    public void setEventTimeUTC(Date eventTimeUTC) {
        this.mEventTimeUTC = eventTimeUTC;
    }

    public int getSequenceNum() {
        return mSequenceNum;
    }

    public void setSequenceNum(int sequenceNum) {
        this.mSequenceNum = sequenceNum;
    }

    public long getBoxId() {
        return mBoxId;
    }

    public void setBoxId(long boxId) {
        this.mBoxId = boxId;
    }

    public int getTDMsgQueue() {
        return mTDMsgQueue;
    }

    public void setTDMsgQueue(int TDMsgQueue) {
        this.mTDMsgQueue = TDMsgQueue;
    }

    public boolean getSensorState(BlackBoxSensorState state) {
        return (mSensorState & state.getMask()) != 0;
    }

    public void setSensorState(int sensorState) {
        mSensorState = sensorState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BlackBoxModel that = (BlackBoxModel) o;

        return new EqualsBuilder()
                .append(mOdometer, that.mOdometer)
                .append(mLat, that.mLat)
                .append(mLon, that.mLon)
                .append(mEngineHours, that.mEngineHours)
                .append(mSpeed, that.mSpeed)
                .append(mHeading, that.mLon)
                .append(mTERT, that.mTERT)
                .append(mResponseType, that.mResponseType)
                .append(mEventTimeUTC, that.mEventTimeUTC)
                .append(mSequenceNum, that.mSequenceNum)
                .append(mBoxId, that.mBoxId)
                .append(mTDMsgQueue, that.mTDMsgQueue)
                .append(mSensorState, that.mSensorState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mOdometer)
                .append(mLat)
                .append(mLon)
                .append(mEngineHours)
                .append(mLon)
                .append(mSpeed)
                .append(mHeading)
                .append(mTERT)
                .append(mResponseType)
                .append(mEventTimeUTC)
                .append(mSequenceNum)
                .append(mBoxId)
                .append(mTDMsgQueue)
                .append(mSensorState)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BlackBoxModel{");
        sb.append("mOdometer=").append(mOdometer);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLon=").append(mLon);
        sb.append(", mEngineHours=").append(mEngineHours);
        sb.append(", mSpeed=").append(mSpeed);
        sb.append(", mHeading=").append(mHeading);
        sb.append(", mTERT=").append(mTERT);
        sb.append(", mResponseType=").append(mResponseType == null ? "null" : mResponseType.name());
        sb.append(", mEventTimeUTC=").append(mEventTimeUTC == null ? "null" : DateUtils.getLocalDate("Etc/UTC", mEventTimeUTC.getTime()));
        sb.append(", mSequenceNum=").append(mSequenceNum);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mTDMsgQueue=").append(mTDMsgQueue);
        sb.append(", mSensorState=").append(mSensorState);
        sb.append('}');
        return sb.toString();
    }
}
