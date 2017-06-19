package com.bsmwireless.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Item implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("category")
    @Expose
    private Integer mCategory;
    @SerializedName("parent")
    @Expose
    private Integer mParent;
    @SerializedName("desc")
    @Expose
    private String mDesc;
    @SerializedName("defectlevel")
    @Expose
    private Integer mDefectLevel;
    @SerializedName("scannable")
    @Expose
    private Boolean mScannable;
    @SerializedName("location")
    @Expose
    private String mLocation;
    @SerializedName("stype")
    @Expose
    private Integer mStype;
    @SerializedName("subItems")
    @Expose
    private List<Item> mSubItems = null;

    public final static Parcelable.Creator<Item> CREATOR = new Creator<Item>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Item createFromParcel(Parcel in) {
            Item instance = new Item();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mCategory = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mParent = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDesc = ((String) in.readValue((String.class.getClassLoader())));
            instance.mDefectLevel = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mScannable = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mLocation = ((String) in.readValue((String.class.getClassLoader())));
            instance.mStype = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.mSubItems, (Item.class.getClassLoader()));
            return instance;
        }

        public Item[] newArray(int size) {
            return (new Item[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getCategory() {
        return mCategory;
    }

    public void setCategory(Integer category) {
        this.mCategory = category;
    }

    public Integer getParent() {
        return mParent;
    }

    public void setParent(Integer parent) {
        this.mParent = parent;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public Integer getDefectLevel() {
        return mDefectLevel;
    }

    public void setDefectLevel(Integer defectLevel) {
        this.mDefectLevel = defectLevel;
    }

    public Boolean getScannable() {
        return mScannable;
    }

    public void setScannable(Boolean scannable) {
        this.mScannable = scannable;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public Integer getStype() {
        return mStype;
    }

    public void setStype(Integer stype) {
        this.mStype = stype;
    }

    public List<Item> getSubItems() {
        return mSubItems;
    }

    public void setSubItems(List<Item> subItems) {
        this.mSubItems = subItems;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Item{");
        sb.append("mId=").append(mId);
        sb.append(", mCategory=").append(mCategory);
        sb.append(", mParent=").append(mParent);
        sb.append(", mDesc='").append(mDesc).append('\'');
        sb.append(", mDefectLevel=").append(mDefectLevel);
        sb.append(", mScannable=").append(mScannable);
        sb.append(", mLocation='").append(mLocation).append('\'');
        sb.append(", mStype=").append(mStype);
        sb.append(", mSubItems=").append(mSubItems);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId)
                .append(mCategory)
                .append(mParent)
                .append(mDesc)
                .append(mDefectLevel)
                .append(mScannable)
                .append(mLocation)
                .append(mStype)
                .append(mSubItems)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Item)) {
            return false;
        }
        Item rhs = ((Item) other);
        return new EqualsBuilder().append(mId, rhs.mId)
                .append(mCategory, rhs.mCategory)
                .append(mParent, rhs.mParent)
                .append(mDesc, rhs.mDesc)
                .append(mDefectLevel, rhs.mDefectLevel)
                .append(mScannable, rhs.mScannable)
                .append(mLocation, rhs.mLocation)
                .append(mStype, rhs.mStype)
                .append(mSubItems, rhs.mSubItems)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mCategory);
        dest.writeValue(mParent);
        dest.writeValue(mDesc);
        dest.writeValue(mDefectLevel);
        dest.writeValue(mScannable);
        dest.writeValue(mLocation);
        dest.writeValue(mStype);
        dest.writeList(mSubItems);
    }

    public int describeContents() {
        return  0;
    }
}
