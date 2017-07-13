package com.bsmwireless.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

//TODO: add real model
public class BlackBoxModel {
    private int mOdometer;
    private double mLat;
    private double mLon;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BlackBoxModel that = (BlackBoxModel) o;

        return new EqualsBuilder()
                .append(mOdometer, that.mOdometer)
                .append(mLat, that.mLat)
                .append(mLon, that.mLon)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mOdometer)
                .append(mLat)
                .append(mLon)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BlackBoxModel{");
        sb.append("mOdometer=").append(mOdometer);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLon=").append(mLon);
        sb.append('}');
        return sb.toString();
    }
}
