package com.bsmwireless.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CheckList implements Parcelable {

    @SerializedName("categories")
    @Expose
    private List<ReportCategory> mCategories = null;
    @SerializedName("attachcategories")
    @Expose
    private List<AttachCategory> mAttachCategories = null;

    public final static Parcelable.Creator<CheckList> CREATOR = new Creator<CheckList>() {

        @SuppressWarnings({
            "unchecked"
        })
        public CheckList createFromParcel(Parcel in) {
            CheckList instance = new CheckList();
            in.readList(instance.mCategories, (ReportCategory.class.getClassLoader()));
            in.readList(instance.mAttachCategories, (AttachCategory.class.getClassLoader()));
            return instance;
        }

        public CheckList[] newArray(int size) {
            return (new CheckList[size]);
        }

    };

    public List<ReportCategory> getCategories() {
        return mCategories;
    }

    public void setCategories(List<ReportCategory> categories) {
        this.mCategories = categories;
    }

    public List<AttachCategory> getAttachCategories() {
        return mAttachCategories;
    }

    public void setAttachCategories(List<AttachCategory> attachCategories) {
        this.mAttachCategories = attachCategories;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CheckList{");
        sb.append("mCategories=").append(mCategories);
        sb.append(", mAttachCategories=").append(mAttachCategories);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mCategories).append(mAttachCategories).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CheckList)) {
            return false;
        }
        CheckList rhs = ((CheckList) other);
        return new EqualsBuilder().append(mCategories, rhs.mCategories).append(mAttachCategories, rhs.mAttachCategories).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mCategories);
        dest.writeList(mAttachCategories);
    }

    public int describeContents() {
        return  0;
    }
}
