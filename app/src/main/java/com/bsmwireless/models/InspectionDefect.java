package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class InspectionDefect implements Parcelable {

    @SerializedName("clearedDriverId")
    @Expose
    private Integer mClearedDriverId;
    @SerializedName("clearedDriverName")
    @Expose
    private String mClearedDriverName;
    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("inspectionItemId")
    @Expose
    private Integer mInspectionItemId;
    @SerializedName("comments")
    @Expose
    private String mComments;
    @SerializedName("trailerId")
    @Expose
    private Integer mTrailerId;
    @SerializedName("cleared")
    @Expose
    private boolean mCleared;
    @SerializedName("images")
    @Expose
    private String mImages;


    public final static Parcelable.Creator<InspectionDefect> CREATOR = new Creator<InspectionDefect>() {

        @SuppressWarnings({"unchecked"})
        @Override
        public InspectionDefect createFromParcel(Parcel in) {
            InspectionDefect instance = new InspectionDefect();
            instance.mClearedDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mClearedDriverName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mInspectionItemId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mComments = ((String) in.readValue((String.class.getClassLoader())));
            instance.mTrailerId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mCleared = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mImages = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        @Override
        public InspectionDefect[] newArray(int size) {
            return (new InspectionDefect[size]);
        }
    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer mId) {
        this.mId = mId;
    }

    public Integer getInspectionItemId() {
        return mInspectionItemId;
    }

    public void setInspectionItemId(Integer mInspectionItemId) {
        this.mInspectionItemId = mInspectionItemId;
    }

    public String getComments() {
        return mComments;
    }

    public void setComments(String mComments) {
        this.mComments = mComments;
    }

    public Integer getTrailerId() {
        return mTrailerId;
    }

    public void setTrailerId(Integer mTrailerId) {
        this.mTrailerId = mTrailerId;
    }

    public boolean isCleared() {
        return mCleared;
    }

    public void setCleared(boolean mCleared) {
        this.mCleared = mCleared;
    }

    public String getImages() {
        return mImages;
    }

    public void setImages(String mImages) {
        this.mImages = mImages;
    }

    public Integer getClearedDriverId() {
        return mClearedDriverId;
    }

    public void setClearedDriverId(Integer mClearedDriverId) {
        this.mClearedDriverId = mClearedDriverId;
    }

    public String getClearedDriverName() {
        return mClearedDriverName;
    }

    public void setClearedDriverName(String mClearedDriverName) {
        this.mClearedDriverName = mClearedDriverName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InsceptionDefect{");
        sb.append("mClearedDriverId=").append(mClearedDriverId);
        sb.append(", mClearedDriverName=").append(mClearedDriverName);
        sb.append(", mId=").append(mId);
        sb.append(", mInspectionItemId='").append(mInspectionItemId);
        sb.append(", mComments=").append(mComments);
        sb.append(", mTrailerId=").append(mTrailerId);
        sb.append(", mCleared='").append(mCleared);
        sb.append(", mImages=").append(mImages);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mClearedDriverId)
                .append(mClearedDriverName)
                .append(mId)
                .append(mInspectionItemId)
                .append(mComments)
                .append(mTrailerId)
                .append(mCleared)
                .append(mImages)
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
        InspectionDefect rhs = ((InspectionDefect) other);
        // field comparison
        return new EqualsBuilder().append(mClearedDriverId, rhs.mClearedDriverId)
                .append(mClearedDriverName, rhs.mClearedDriverName)
                .append(mId, rhs.mId)
                .append(mInspectionItemId, rhs.mInspectionItemId)
                .append(mComments, rhs.mComments)
                .append(mTrailerId, rhs.mTrailerId)
                .append(mCleared, rhs.mCleared)
                .append(mImages, rhs.mImages)
                .isEquals();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mClearedDriverId);
        dest.writeValue(mClearedDriverName);
        dest.writeValue(mId);
        dest.writeValue(mInspectionItemId);
        dest.writeValue(mComments);
        dest.writeValue(mTrailerId);
        dest.writeValue(mCleared);
        dest.writeValue(mImages);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
