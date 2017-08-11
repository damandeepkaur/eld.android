package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ResponseMessage implements Parcelable {

    @SerializedName("message")
    @Expose
    private String mMessage;

    public final static Parcelable.Creator<ResponseMessage> CREATOR = new Creator<ResponseMessage>() {

        @SuppressWarnings({
            "unchecked"
        })
        public ResponseMessage createFromParcel(Parcel in) {
            ResponseMessage instance = new ResponseMessage();
            instance.mMessage = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ResponseMessage[] newArray(int size) {
            return (new ResponseMessage[size]);
        }

    };

    public ResponseMessage() {}

    public ResponseMessage(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResponseMessage{");
        sb.append(", mMessage='").append(mMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mMessage).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ResponseMessage)) {
            return false;
        }
        ResponseMessage rhs = ((ResponseMessage) other);
        return new EqualsBuilder().append(mMessage, rhs.mMessage).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mMessage);
    }

    public int describeContents() {
        return  0;
    }
}
