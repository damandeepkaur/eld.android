package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Defect implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("trailerid")
    @Expose
    private Integer mTrailerid;
    @SerializedName("comments")
    @Expose
    private String mComments;
    @SerializedName("inspectionItem")
    @Expose
    private InspectionItem mInspectionItem;
    @SerializedName("clearnote")
    @Expose
    private ClearNote mClearNote;
    @SerializedName("images")
    @Expose
    private Images mImages;

    public final static Parcelable.Creator<Defect> CREATOR = new Creator<Defect>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Defect createFromParcel(Parcel in) {
            Defect instance = new Defect();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mTrailerid = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mComments = ((String) in.readValue((String.class.getClassLoader())));
            instance.mInspectionItem = ((InspectionItem) in.readValue((InspectionItem.class.getClassLoader())));
            instance.mClearNote = ((ClearNote) in.readValue((ClearNote.class.getClassLoader())));
            instance.mImages = ((Images) in.readValue((Images.class.getClassLoader())));
            return instance;
        }

        public Defect[] newArray(int size) {
            return (new Defect[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getTrailerid() {
        return mTrailerid;
    }

    public void setTrailerid(Integer trailerid) {
        this.mTrailerid = trailerid;
    }

    public String getComments() {
        return mComments;
    }

    public void setComments(String comments) {
        this.mComments = comments;
    }

    public InspectionItem getInspectionItem() {
        return mInspectionItem;
    }

    public void setInspectionItem(InspectionItem inspectionItem) {
        this.mInspectionItem = inspectionItem;
    }

    public ClearNote getClearNote() {
        return mClearNote;
    }

    public void setClearNote(ClearNote clearNote) {
        this.mClearNote = clearNote;
    }

    public Images getImages() {
        return mImages;
    }

    public void setImages(Images images) {
        this.mImages = images;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Defect{");
        sb.append("mId=").append(mId);
        sb.append(", mTrailerid=").append(mTrailerid);
        sb.append(", mComments='").append(mComments).append('\'');
        sb.append(", mInspectionItem=").append(mInspectionItem);
        sb.append(", mClearNote=").append(mClearNote);
        sb.append(", mImages=").append(mImages);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId).append(mTrailerid).append(mComments).append(mInspectionItem).append(mClearNote).append(mImages).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Defect)) {
            return false;
        }
        Defect rhs = ((Defect) other);
        return new EqualsBuilder().append(mId, rhs.mId).append(mTrailerid, rhs.mTrailerid).append(mComments, rhs.mComments).append(mInspectionItem, rhs.mInspectionItem).append(mClearNote, rhs.mClearNote).append(mImages, rhs.mImages).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mTrailerid);
        dest.writeValue(mComments);
        dest.writeValue(mInspectionItem);
        dest.writeValue(mClearNote);
        dest.writeValue(mImages);
    }

    public int describeContents() {
        return  0;
    }
}
