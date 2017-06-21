package com.bsmwireless.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Category implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("desc")
    @Expose
    private String mDesc;
    @SerializedName("scannable")
    @Expose
    private Boolean mScannable;
    @SerializedName("location")
    @Expose
    private String mLocation;
    @SerializedName("barcode")
    @Expose
    private String mBarcode;
    @SerializedName("stype")
    @Expose
    private Integer mStype;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public final static Parcelable.Creator<Category> CREATOR = new Creator<Category>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Category createFromParcel(Parcel in) {
            Category instance = new Category();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDesc = ((String) in.readValue((String.class.getClassLoader())));
            instance.mScannable = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mLocation = ((String) in.readValue((String.class.getClassLoader())));
            instance.mBarcode = ((String) in.readValue((String.class.getClassLoader())));
            instance.mStype = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.items, (Item.class.getClassLoader()));
            return instance;
        }

        public Category[] newArray(int size) {
            return (new Category[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
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

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String barcode) {
        this.mBarcode = barcode;
    }

    public Integer getStype() {
        return mStype;
    }

    public void setStype(Integer stype) {
        this.mStype = stype;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Category{");
        sb.append("mId=").append(mId);
        sb.append(", mDesc='").append(mDesc).append('\'');
        sb.append(", mScannable=").append(mScannable);
        sb.append(", mLocation='").append(mLocation).append('\'');
        sb.append(", mBarcode='").append(mBarcode).append('\'');
        sb.append(", mStype=").append(mStype);
        sb.append(", items=").append(items);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId)
                .append(mDesc)
                .append(mScannable)
                .append(mLocation)
                .append(mBarcode)
                .append(mStype)
                .append(items)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Category)) {
            return false;
        }
        Category rhs = ((Category) other);
        return new EqualsBuilder().append(mId, rhs.mId)
                .append(mDesc, rhs.mDesc)
                .append(mScannable, rhs.mScannable)
                .append(mLocation, rhs.mLocation)
                .append(mBarcode, rhs.mBarcode)
                .append(mStype, rhs.mStype)
                .append(items, rhs.items)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mDesc);
        dest.writeValue(mScannable);
        dest.writeValue(mLocation);
        dest.writeValue(mBarcode);
        dest.writeValue(mStype);
        dest.writeList(items);
    }

    public int describeContents() {
        return  0;
    }
}
