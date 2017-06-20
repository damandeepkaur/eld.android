package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Response implements Parcelable {

    @SerializedName("responseCode")
    @Expose
    private Integer mResponseCode;
    @SerializedName("message")
    @Expose
    private String mMessage;

    public final static Parcelable.Creator<Response> CREATOR = new Creator<Response>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Response createFromParcel(Parcel in) {
            Response instance = new Response();
            instance.mResponseCode = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mMessage = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Response[] newArray(int size) {
            return (new Response[size]);
        }

    };

    public Integer getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.mResponseCode = responseCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Response{");
        sb.append("mResponseCode=").append(mResponseCode);
        sb.append(", mMessage='").append(mMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mResponseCode).append(mMessage).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Response)) {
            return false;
        }
        Response rhs = ((Response) other);
        return new EqualsBuilder().append(mResponseCode, rhs.mResponseCode).append(mMessage, rhs.mMessage).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mResponseCode);
        dest.writeValue(mMessage);
    }

    public int describeContents() {
        return  0;
    }
}
