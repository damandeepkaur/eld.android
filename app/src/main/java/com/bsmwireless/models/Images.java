package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Images implements Parcelable {

    @SerializedName("filename")
    @Expose
    private String mFileName;
    @SerializedName("image")
    @Expose
    private String mImage;

    public final static Parcelable.Creator<Images> CREATOR = new Creator<Images>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Images createFromParcel(Parcel in) {
            Images instance = new Images();
            instance.mFileName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mImage = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Images[] newArray(int size) {
            return (new Images[size]);
        }

    };

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Images{");
        sb.append("mFileName='").append(mFileName).append('\'');
        sb.append(", mImage='").append(mImage).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mFileName).append(mImage).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Images)) {
            return false;
        }
        Images rhs = ((Images) other);
        return new EqualsBuilder().append(mFileName, rhs.mFileName).append(mImage, rhs.mImage).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mFileName);
        dest.writeValue(mImage);
    }

    public int describeContents() {
        return  0;
    }
}
