package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SyncInspectionItem implements Parcelable {

    @SerializedName("defectLevel")
    @Expose
    private Integer mDefectLevel;

    @SerializedName("desc")
    @Expose
    private SyncDescription mDesc;

    @SerializedName("items")
    @Expose
    private List<SyncInspectionItem> mSubItems = null;

    @SerializedName("category")
    @Expose
    private Integer mCategory;

    @SerializedName("id")
    @Expose
    private Integer mId;

    @SerializedName("parent")
    @Expose
    private Integer mParent;

    @SerializedName("sType")
    @Expose
    private Integer mSType;

    public Integer getDefectLevel() {
        return mDefectLevel;
    }

    public void setDefectLevel(Integer defectLevel) {
        mDefectLevel = defectLevel;
    }

    public SyncDescription getDesc() {
        return mDesc;
    }

    public void setDesc(SyncDescription desc) {
        mDesc = desc;
    }

    public List<SyncInspectionItem> getSubItems() {
        return mSubItems;
    }

    public void setSubItems(List<SyncInspectionItem> subItems) {
        mSubItems = subItems;
    }

    public Integer getCategory() {
        return mCategory;
    }

    public void setCategory(Integer category) {
        mCategory = category;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Integer getParent() {
        return mParent;
    }

    public void setParent(Integer parent) {
        mParent = parent;
    }

    public Integer getSType() {
        return mSType;
    }

    public void setSType(Integer SType) {
        mSType = SType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SyncInspectionItem that = (SyncInspectionItem) o;

        return new EqualsBuilder()
                .append(mDefectLevel, that.mDefectLevel)
                .append(mDesc, that.mDesc)
                .append(mSubItems, that.mSubItems)
                .append(mCategory, that.mCategory)
                .append(mId, that.mId)
                .append(mParent, that.mParent)
                .append(mSType, that.mSType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mDefectLevel)
                .append(mDesc)
                .append(mSubItems)
                .append(mCategory)
                .append(mId)
                .append(mParent)
                .append(mSType)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SyncInspectionItem{");
        sb.append("mDefectLevel=").append(mDefectLevel);
        sb.append(", mDesc=").append(mDesc);
        sb.append(", mSubItems=").append(mSubItems);
        sb.append(", mCategory=").append(mCategory);
        sb.append(", mId=").append(mId);
        sb.append(", mParent=").append(mParent);
        sb.append(", mSType=").append(mSType);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mDefectLevel);
        dest.writeParcelable(this.mDesc, flags);
        dest.writeList(this.mSubItems);
        dest.writeValue(this.mCategory);
        dest.writeValue(this.mId);
        dest.writeValue(this.mParent);
        dest.writeValue(this.mSType);
    }

    public SyncInspectionItem() {
    }

    protected SyncInspectionItem(Parcel in) {
        this.mDefectLevel = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mDesc = in.readParcelable(SyncDescription.class.getClassLoader());
        this.mSubItems = new ArrayList<SyncInspectionItem>();
        in.readList(this.mSubItems, SyncInspectionItem.class.getClassLoader());
        this.mCategory = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mParent = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mSType = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<SyncInspectionItem> CREATOR = new Parcelable.Creator<SyncInspectionItem>() {
        @Override
        public SyncInspectionItem createFromParcel(Parcel source) {
            return new SyncInspectionItem(source);
        }

        @Override
        public SyncInspectionItem[] newArray(int size) {
            return new SyncInspectionItem[size];
        }
    };
}
