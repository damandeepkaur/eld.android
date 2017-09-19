package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class InspectionDefect implements Parcelable {

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

    public InspectionDefect() {}

    private InspectionDefect(Parcel in) {
        mClearedDriverId = in.readInt();
        mClearedDriverName = in.readString();
        mId = in.readInt();
        mInspectionItemId = in.readInt();
        mComments = in.readString();
        mTrailerId = in.readInt();
        mCleared = in.readByte() != 0;
        mImages = in.readString();
    }

    public final static Parcelable.Creator<InspectionDefect> CREATOR = new Creator<InspectionDefect>() {

        @SuppressWarnings({"unchecked"})
        @Override
        public InspectionDefect createFromParcel(Parcel in) {
            return new InspectionDefect(in);
        }

        @Override
        public InspectionDefect[] newArray(int size) {
            return (new InspectionDefect[size]);
        }
    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getInspectionItemId() {
        return mInspectionItemId;
    }

    public void setInspectionItemId(Integer inspectionItemId) {
        this.mInspectionItemId = inspectionItemId;
    }

    public String getComments() {
        return mComments;
    }

    public void setComments(String comments) {
        this.mComments = comments;
    }

    public Integer getTrailerId() {
        return mTrailerId;
    }

    public void setTrailerId(Integer trailerId) {
        this.mTrailerId = trailerId;
    }

    public boolean isCleared() {
        return mCleared;
    }

    public void setCleared(boolean cleared) {
        this.mCleared = cleared;
    }

    public String getImages() {
        return mImages;
    }

    public void setImages(String images) {
        this.mImages = images;
    }

    public Integer getClearedDriverId() {
        return mClearedDriverId;
    }

    public void setClearedDriverId(Integer clearedDriverId) {
        this.mClearedDriverId = clearedDriverId;
    }

    public String getClearedDriverName() {
        return mClearedDriverName;
    }

    public void setClearedDriverName(String clearedDriverName) {
        this.mClearedDriverName = clearedDriverName;
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
        dest.writeInt(mClearedDriverId);
        dest.writeString(mClearedDriverName);
        dest.writeInt(mId);
        dest.writeInt(mInspectionItemId);
        dest.writeString(mComments);
        dest.writeInt(mTrailerId);
        dest.writeByte((byte) (mCleared ? 1 : 0));
        dest.writeString(mImages);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
