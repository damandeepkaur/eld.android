package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class SyncInspectionCategory implements Parcelable {

    @SerializedName("groupId")
    @Expose
    private Integer mGroupId;

    @SerializedName("barCode")
    @Expose
    private String mBarcode;

    @SerializedName("stype")
    @Expose
    private String mSType;

    @SerializedName("mItems")
    @Expose
    private List<SyncInspectionItem> mItems = null;

    @SerializedName("desc")
    @Expose
    private SyncDescription mDesc;

    @SerializedName("id")
    @Expose
    private Integer mId;

    @SerializedName("scannable")
    @Expose
    private Boolean mScannable;

    @SerializedName("location")
    @Expose
    private String mLocation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SyncInspectionCategory that = (SyncInspectionCategory) o;

        return new EqualsBuilder()
                .append(mGroupId, that.mGroupId)
                .append(mBarcode, that.mBarcode)
                .append(mSType, that.mSType)
                .append(mItems, that.mItems)
                .append(mDesc, that.mDesc)
                .append(mId, that.mId)
                .append(mScannable, that.mScannable)
                .append(mLocation, that.mLocation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mGroupId)
                .append(mBarcode)
                .append(mSType)
                .append(mItems)
                .append(mDesc)
                .append(mId)
                .append(mScannable)
                .append(mLocation)
                .toHashCode();
    }

    public Integer getGroupId() {
        return mGroupId;
    }

    public void setGroupId(Integer groupId) {
        this.mGroupId = groupId;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }

    public String getSType() {
        return mSType;
    }

    public void setSType(String SType) {
        mSType = SType;
    }

    public List<SyncInspectionItem> getItems() {
        return mItems;
    }

    public void setItems(List<SyncInspectionItem> items) {
        this.mItems = items;
    }

    public SyncDescription getDesc() {
        return mDesc;
    }

    public void setDesc(SyncDescription desc) {
        mDesc = desc;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Boolean getScannable() {
        return mScannable;
    }

    public void setScannable(Boolean scannable) {
        mScannable = scannable;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SyncInspectionCategory{");
        sb.append("mGroupId=").append(mGroupId);
        sb.append(", mBarcode='").append(mBarcode).append('\'');
        sb.append(", mSType=").append(mSType);
        sb.append(", mItems=").append(mItems);
        sb.append(", mDesc=").append(mDesc);
        sb.append(", mId=").append(mId);
        sb.append(", mScannable=").append(mScannable);
        sb.append(", mLocation='").append(mLocation).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mGroupId);
        dest.writeString(this.mBarcode);
        dest.writeString(this.mSType);
        dest.writeList(this.mItems);
        dest.writeParcelable(this.mDesc, flags);
        dest.writeValue(this.mId);
        dest.writeValue(this.mScannable);
        dest.writeString(this.mLocation);
    }

    public SyncInspectionCategory() {
    }

    protected SyncInspectionCategory(Parcel in) {
        this.mGroupId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mBarcode = in.readString();
        this.mSType = in.readString();
        this.mItems = new ArrayList<SyncInspectionItem>();
        in.readList(this.mItems, SyncInspectionItem.class.getClassLoader());
        this.mDesc = in.readParcelable(SyncDescription.class.getClassLoader());
        this.mId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mScannable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mLocation = in.readString();
    }

    public static final Parcelable.Creator<SyncInspectionCategory> CREATOR = new Parcelable.Creator<SyncInspectionCategory>() {
        @Override
        public SyncInspectionCategory createFromParcel(Parcel source) {
            return new SyncInspectionCategory(source);
        }

        @Override
        public SyncInspectionCategory[] newArray(int size) {
            return new SyncInspectionCategory[size];
        }
    };
}
