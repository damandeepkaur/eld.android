package com.bsmwireless.data.storage.vehicles;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "vehicles")
public final class VehicleEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "box_id")
    private Integer mBoxId;

    @ColumnInfo(name = "license")
    private String mLicense;

    @ColumnInfo(name = "province")
    private String mProvince;

    @ColumnInfo(name = "weight")
    private Integer mWeight;

    @ColumnInfo(name = "dot")
    private String mDot;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        mBoxId = boxId;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        mLicense = license;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public Integer getWeight() {
        return mWeight;
    }

    public void setWeight(Integer weight) {
        mWeight = weight;
    }

    public String getDot() {
        return mDot;
    }

    public void setDot(String dot) {
        mDot = dot;
    }

}
