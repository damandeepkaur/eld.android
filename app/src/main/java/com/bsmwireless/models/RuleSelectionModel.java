package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class RuleSelectionModel implements Parcelable {

    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;
    @SerializedName("dutyCycle")
    @Expose
    private String mDutyCycle;
    @SerializedName("ruleException")
    @Expose
    private String mRuleException;
    @SerializedName("applyTime")
    @Expose
    private Long mApplyTime;
    public final static Parcelable.Creator<RuleSelectionModel> CREATOR = new Creator<RuleSelectionModel>() {

        @SuppressWarnings({
            "unchecked"
        })
        public RuleSelectionModel createFromParcel(Parcel in) {
            return new RuleSelectionModel(in);
        }

        public RuleSelectionModel[] newArray(int size) {
            return (new RuleSelectionModel[size]);
        }

    };

    public RuleSelectionModel() {}

    public RuleSelectionModel(Parcel in) {
        mDriverId = in.readInt();
        mDutyCycle = in.readString();
        mRuleException = in.readString();
        mApplyTime = in.readLong();
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public RuleSelectionModel setDriverId(Integer driverId) {
        this.mDriverId = driverId;
        return this;
    }

    public String getDutyCycle() {
        return mDutyCycle;
    }

    public RuleSelectionModel setDutyCycle(String dutyCycle) {
        this.mDutyCycle = dutyCycle;
        return this;
    }

    public String getRuleException() {
        return mRuleException;
    }

    public RuleSelectionModel setRuleException(String ruleException) {
        this.mRuleException = ruleException;
        return this;
    }

    public Long getApplyTime() {
        return mApplyTime;
    }

    public RuleSelectionModel setApplyTime(Long applyTime) {
        this.mApplyTime = applyTime;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleSelectionModel{");
        sb.append("mDriverId=").append(mDriverId);
        sb.append(", mDutyCycle='").append(mDutyCycle).append('\'');
        sb.append(", mRuleException='").append(mRuleException).append('\'');
        sb.append(", mApplyTime=").append(mApplyTime);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDriverId)
                                    .append(mDutyCycle)
                                    .append(mRuleException)
                                    .append(mApplyTime)
                                    .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RuleSelectionModel)) {
            return false;
        }
        RuleSelectionModel rhs = ((RuleSelectionModel) other);
        return new EqualsBuilder().append(mDriverId, rhs.mDriverId)
                                  .append(mDutyCycle, rhs.mDutyCycle)
                                  .append(mRuleException, rhs.mRuleException)
                                  .append(mApplyTime, rhs.mApplyTime)
                                  .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDriverId);
        dest.writeString(mDutyCycle);
        dest.writeString(mRuleException);
        dest.writeLong(mApplyTime);
    }

    public int describeContents() {
        return  0;
    }

}
