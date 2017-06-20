
package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Rule implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("datetime")
    @Expose
    private Long mDateTime;
    @SerializedName("country")
    @Expose
    private String mCountry;
    @SerializedName("exception")
    @Expose
    private Integer mException;

    public final static Parcelable.Creator<Rule> CREATOR = new Creator<Rule>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Rule createFromParcel(Parcel in) {
            Rule instance = new Rule();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDateTime = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mCountry = ((String) in.readValue((String.class.getClassLoader())));
            instance.mException = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Rule[] newArray(int size) {
            return (new Rule[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Long getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Long dateTime) {
        this.mDateTime = dateTime;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    public Integer getException() {
        return mException;
    }

    public void setException(Integer exception) {
        this.mException = exception;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rule{");
        sb.append("mId=").append(mId);
        sb.append(", mDateTime=").append(mDateTime);
        sb.append(", mCountry='").append(mCountry).append('\'');
        sb.append(", mException=").append(mException);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId)
                .append(mDateTime)
                .append(mCountry)
                .append(mException)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Rule)) {
            return false;
        }
        Rule rhs = ((Rule) other);
        return new EqualsBuilder().append(mId, rhs.mId)
                .append(mDateTime, rhs.mDateTime)
                .append(mCountry, rhs.mCountry)
                .append(mException, rhs.mException)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mDateTime);
        dest.writeValue(mCountry);
        dest.writeValue(mException);
    }

    public int describeContents() {
        return  0;
    }
}
