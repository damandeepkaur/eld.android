package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class InspectionReport implements Parcelable {

    @SerializedName("inspections")
    @Expose
    private List<Inspection> mInspectionList = null;
    @SerializedName("trailers")
    @Expose
    private List<Vehicle> mVehicleAttributeList = null;

    public InspectionReport() {}

    private InspectionReport(Parcel in) {
        in.readTypedList(mInspectionList, Inspection.CREATOR);
        in.readTypedList(mVehicleAttributeList, Vehicle.CREATOR);
    }

    public static final Creator<InspectionReport> CREATOR = new Creator<InspectionReport>() {

        @SuppressWarnings({"unchecked"})
        @Override
        public InspectionReport createFromParcel(Parcel in) {
            return new InspectionReport(in);
        }

        @Override
        public InspectionReport[] newArray(int size) {
            return new InspectionReport[0];
        }
    };

    public List<Inspection> getInspectionList() {
        return mInspectionList;
    }

    public void setInspectionList(List<Inspection> inspectionList) {
        this.mInspectionList = inspectionList;
    }

    public List<Vehicle> getVehicleAttributeList() {
        return mVehicleAttributeList;
    }

    public void setVehicleAttributeList(List<Vehicle> vehicleAttributeList) {
        this.mVehicleAttributeList = vehicleAttributeList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InspectionReport{");
        sb.append("mInspectionList=").append(mInspectionList);
        sb.append(", mVehicleAttributeList=").append(mVehicleAttributeList);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mInspectionList)
                .append(mVehicleAttributeList)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        // self check
        if (this == other) {
            return true;
        }
        // null check and type check (cast)
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        InspectionReport rhs = ((InspectionReport) other);
        // field comparison
        return new EqualsBuilder().append(mInspectionList, rhs.mInspectionList)
                .append(mVehicleAttributeList, rhs.mVehicleAttributeList)
                .isEquals();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mInspectionList);
        dest.writeTypedList(mVehicleAttributeList);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
