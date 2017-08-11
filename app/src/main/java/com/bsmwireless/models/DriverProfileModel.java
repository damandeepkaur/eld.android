package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bsmwireless.data.storage.users.UserEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DriverProfileModel implements Parcelable {

    @SerializedName("email")
    @Expose
    private String mEmail;
    @SerializedName("license")
    @Expose
    private String mLicense;
    @SerializedName("lastmodified")
    @Expose
    private Long mLastModified;
    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;
    @SerializedName("firstName")
    @Expose
    private String mFirstName;
    @SerializedName("midName")
    @Expose
    private String mMidName;
    @SerializedName("lastName")
    @Expose
    private String mLastName;

    public final static Parcelable.Creator<DriverProfileModel> CREATOR = new Creator<DriverProfileModel>() {

        @SuppressWarnings({
            "unchecked"
        })
        public DriverProfileModel createFromParcel(Parcel in) {
            return new DriverProfileModel(in);
        }

        public DriverProfileModel[] newArray(int size) {
            return (new DriverProfileModel[size]);
        }

    };

    public DriverProfileModel() {}

    public DriverProfileModel(Parcel in) {
        mEmail = in.readString();
        mLicense = in.readString();
        mLastModified = in.readLong();
        mDriverId = in.readInt();
        mFirstName = in.readString();
        mMidName = in.readString();
        mLastName =  in.readString();
    }

    public DriverProfileModel(UserEntity userEntity) {
        mEmail = userEntity.getEmail();
        mLicense = userEntity.getLicense();
        mLastModified = userEntity.getSyncTime();
        mDriverId = userEntity.getId();
        mFirstName = userEntity.getFirstName();
        mMidName = userEntity.getMidName();
        mLastName = userEntity.getLastName();
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        this.mLicense = license;
    }

    public Long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(Long lastModified) {
        this.mLastModified = lastModified;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getMidName() {
        return mMidName;
    }

    public void setMidName(String midName) {
        this.mMidName = midName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DriverProfileModel{");
        sb.append("mEmail='").append(mEmail).append('\'');
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mLastModified=").append(mLastModified);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mFirstName='").append(mFirstName).append('\'');
        sb.append(", mMidName='").append(mMidName).append('\'');
        sb.append(", mLastName='").append(mLastName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(mEmail)
                .append(mLicense)
                .append(mLastModified)
                .append(mDriverId)
                .append(mFirstName)
                .append(mMidName)
                .append(mLastName)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DriverProfileModel)) {
            return false;
        }
        DriverProfileModel rhs = ((DriverProfileModel) other);
        return new EqualsBuilder().append(mEmail, rhs.mEmail)
                                  .append(mLicense, rhs.mLicense)
                                  .append(mLastModified, rhs.mLastModified)
                                  .append(mDriverId, rhs.mDriverId)
                                  .append(mFirstName, rhs.mFirstName)
                                  .append(mMidName, rhs.mMidName)
                                  .append(mLastName, rhs.mLastName)
                                  .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEmail);
        dest.writeString(mLicense);
        dest.writeLong(mLastModified);
        dest.writeInt(mDriverId);
        dest.writeString(mFirstName);
        dest.writeString(mMidName);
        dest.writeString(mLastName);
    }

    public int describeContents() {
        return  0;
    }

}
