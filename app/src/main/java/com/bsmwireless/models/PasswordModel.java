package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class PasswordModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("username")
    @Expose
    private String mUsername;
    @SerializedName("password")
    @Expose
    private String mPassword;
    @SerializedName("newpswd")
    @Expose
    private String mNewPassword;

    public final static Parcelable.Creator<PasswordModel> CREATOR = new Creator<PasswordModel>() {

        @SuppressWarnings({
            "unchecked"
        })
        public PasswordModel createFromParcel(Parcel in) {
            PasswordModel instance = new PasswordModel();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mUsername = ((String) in.readValue((String.class.getClassLoader())));
            instance.mPassword = ((String) in.readValue((String.class.getClassLoader())));
            instance.mNewPassword = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PasswordModel[] newArray(int size) {
            return (new PasswordModel[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getNewPassword() {
        return mNewPassword;
    }

    public void setNewPassword(String newPassword) {
        this.mNewPassword = newPassword;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PasswordModel{");
        sb.append("id=").append(mId);
        sb.append(", username='").append(mUsername).append('\'');
        sb.append(", password='").append(mPassword).append('\'');
        sb.append(", newpswd='").append(mNewPassword).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId)
                                    .append(mUsername)
                                    .append(mPassword)
                                    .append(mNewPassword)
                                    .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PasswordModel)) {
            return false;
        }
        PasswordModel rhs = ((PasswordModel) other);
        return new EqualsBuilder().append(mId, rhs.mId)
                                  .append(mUsername, rhs.mUsername)
                                  .append(mPassword, rhs.mPassword)
                                  .append(mNewPassword, rhs.mNewPassword)
                                  .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mUsername);
        dest.writeValue(mPassword);
        dest.writeValue(mNewPassword);
    }

    public int describeContents() {
        return  0;
    }

}
