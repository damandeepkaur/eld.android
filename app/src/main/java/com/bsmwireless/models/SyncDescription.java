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
    private String mEnglish;

    @SerializedName("fr")
    @Expose
    private String mFrench;

    @SerializedName("es")
    @Expose
    private String mSpanish;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SyncDescription that = (SyncDescription) o;

        return new EqualsBuilder()
                .append(mEnglish, that.mEnglish)
                .append(mFrench, that.mFrench)
                .append(mSpanish, that.mSpanish)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mEnglish)
                .append(mFrench)
                .append(mSpanish)
                .toHashCode();
    }

    public String getEnglish() {
        return mEnglish;
    }

    public void setEnglish(String english) {
        mEnglish = english;
    }

    public String getFrench() {
        return mFrench;
    }

    public void setFrench(String french) {
        mFrench = french;
    }

    public String getSpanish() {
        return mSpanish;
    }

    public void setSpanish(String spanish) {
        mSpanish = spanish;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mEnglish);
        dest.writeString(this.mFrench);
        dest.writeString(this.mSpanish);
    }

    public SyncDescription() {
    }

    protected SyncDescription(Parcel in) {
        this.mEnglish = in.readString();
        this.mFrench = in.readString();
        this.mSpanish = in.readString();
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SyncDescription{");
        sb.append("mEnglish='").append(mEnglish).append('\'');
        sb.append(", mFrench='").append(mFrench).append('\'');
        sb.append(", mSpanish='").append(mSpanish).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
