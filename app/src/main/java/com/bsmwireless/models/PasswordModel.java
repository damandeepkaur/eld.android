package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PasswordModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("newpswd")
    @Expose
    private String newpswd;

    public final static Parcelable.Creator<PasswordModel> CREATOR = new Creator<PasswordModel>() {

        @SuppressWarnings({
            "unchecked"
        })
        public PasswordModel createFromParcel(Parcel in) {
            PasswordModel instance = new PasswordModel();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.username = ((String) in.readValue((String.class.getClassLoader())));
            instance.password = ((String) in.readValue((String.class.getClassLoader())));
            instance.newpswd = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PasswordModel[] newArray(int size) {
            return (new PasswordModel[size]);
        }

    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewpswd() {
        return newpswd;
    }

    public void setNewpswd(String newpswd) {
        this.newpswd = newpswd;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PasswordModel{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", newpswd='").append(newpswd).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(username)
                                    .append(password)
                                    .append(newpswd)
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
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(username, rhs.username)
                                  .append(password, rhs.password)
                                  .append(newpswd, rhs.newpswd)
                                  .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(username);
        dest.writeValue(password);
        dest.writeValue(newpswd);
    }

    public int describeContents() {
        return  0;
    }

}
