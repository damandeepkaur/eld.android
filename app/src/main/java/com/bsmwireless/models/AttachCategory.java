package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AttachCategory implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("category")
    @Expose
    private String mCategory;
    @SerializedName("level")
    @Expose
    private Integer mLevel;
    @SerializedName("type")
    @Expose
    private Integer mType;

    public final static Parcelable.Creator<AttachCategory> CREATOR = new Creator<AttachCategory>() {

        @SuppressWarnings({
            "unchecked"
        })
        public AttachCategory createFromParcel(Parcel in) {
            AttachCategory instance = new AttachCategory();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mCategory = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLevel = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mType = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public AttachCategory[] newArray(int size) {
            return (new AttachCategory[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
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
        final StringBuilder sb = new StringBuilder("AttachCategory{");
        sb.append("mId=").append(mId);
        sb.append(", mCategory='").append(mCategory).append('\'');
        sb.append(", mLevel=").append(mLevel);
        sb.append(", mType=").append(mType);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId).append(mCategory).append(mLevel).append(mType).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AttachCategory)) {
            return false;
        }
        AttachCategory rhs = ((AttachCategory) other);
        return new EqualsBuilder().append(mId, rhs.mId).append(mCategory, rhs.mCategory).append(mLevel, rhs.mLevel).append(mType, rhs.mType).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mCategory);
        dest.writeValue(mLevel);
        dest.writeValue(mType);
    }

    public int describeContents() {
        return  0;
    }
}
