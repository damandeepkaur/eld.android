package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SyncDescription implements Parcelable {
    @SerializedName("en")
    @Expose
    private String mEn;

    @SerializedName("fr")
    @Expose
    private String mFr;

    @SerializedName("es")
    @Expose
    private String mEs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SyncDescription that = (SyncDescription) o;

        return new EqualsBuilder()
                .append(mEn, that.mEn)
                .append(mFr, that.mFr)
                .append(mEs, that.mEs)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mEn)
                .append(mFr)
                .append(mEs)
                .toHashCode();
    }

    public String getEn() {
        return mEn;
    }

    public void setEn(String en) {
        mEn = en;
    }

    public String getFr() {
        return mFr;
    }

    public void setFr(String fr) {
        mFr = fr;
    }

    public String getEs() {
        return mEs;
    }

    public void setEs(String es) {
        mEs = es;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mEn);
        dest.writeString(this.mFr);
        dest.writeString(this.mEs);
    }

    public SyncDescription() {
    }

    protected SyncDescription(Parcel in) {
        this.mEn = in.readString();
        this.mFr = in.readString();
        this.mEs = in.readString();
    }

    public static final Parcelable.Creator<SyncDescription> CREATOR = new Parcelable.Creator<SyncDescription>() {
        @Override
        public SyncDescription createFromParcel(Parcel source) {
            return new SyncDescription(source);
        }

        @Override
        public SyncDescription[] newArray(int size) {
            return new SyncDescription[size];
        }
    };
}
