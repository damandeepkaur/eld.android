package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class InspectionItem implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("category")
    @Expose
    private String mCategory;
    @SerializedName("level")
    @Expose
    private Integer mLevel;
    @SerializedName("type")
    @Expose
    private Integer mType;

    public final static Parcelable.Creator<InspectionItem> CREATOR = new Creator<InspectionItem>() {

        @SuppressWarnings({
            "unchecked"
        })
        public InspectionItem createFromParcel(Parcel in) {
            InspectionItem instance = new InspectionItem();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mCategory = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLevel = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mType = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public InspectionItem[] newArray(int size) {
            return (new InspectionItem[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public Integer getLevel() {
        return mLevel;
    }

    public void setLevel(Integer level) {
        this.mLevel = level;
    }

    public Integer getType() {
        return mType;
    }

    public void setType(Integer type) {
        this.mType = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InspectionItem{");
        sb.append("mId=").append(mId);
        sb.append(", mName='").append(mName).append('\'');
        sb.append(", mCategory='").append(mCategory).append('\'');
        sb.append(", mLevel=").append(mLevel);
        sb.append(", mType=").append(mType);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId).append(mName).append(mCategory).append(mLevel).append(mType).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof InspectionItem)) {
            return false;
        }
        InspectionItem rhs = ((InspectionItem) other);
        return new EqualsBuilder().append(mId, rhs.mId).append(mName, rhs.mName).append(mCategory, rhs.mCategory).append(mLevel, rhs.mLevel).append(mType, rhs.mType).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mName);
        dest.writeValue(mCategory);
        dest.writeValue(mLevel);
        dest.writeValue(mType);
    }

    public int describeContents() {
        return  0;
    }
}
