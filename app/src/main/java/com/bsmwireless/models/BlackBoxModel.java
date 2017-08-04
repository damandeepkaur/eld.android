package com.bsmwireless.models;

import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.data.network.connection.response.ResponseProcessor.ResponseType;
import com.bsmwireless.widgets.alerts.DutyType;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

//TODO: add real model
public class BlackBoxModel {
    private int mOdometer;
    private double mLat;
    private double mLon;
    private int mSpeed;
    private int mHeading;
    public int mTERT;
    public ResponseType mResponseType;
    public Date mEvenTimeUTC;
    public int mSequenceNum;
    public long mBoxId;
    public int mTDMsgQueue;

    private int mEngineHours;

    public int getOdometer() {
        return mOdometer;
    }

    public void setOdometer(int odometer) {
        mOdometer = odometer;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public int getEngineHours() {
        return mEngineHours;
    }

    public void setEngineHours(int engineHours) {
        mEngineHours = engineHours;
    }

    public int getSpeed() { return mSpeed;   }

    public void setSpeed(int speed) { mSpeed = speed;    }

    public int getHeading() {return mHeading;  }

    public void setHeading(int heading) {  this.mHeading =heading;  }

    public int getTERT() {return mTERT;   }

    public void setTERT(int TERT) { this.mTERT = TERT;   }

    public ResponseType getResponseType() {return mResponseType;   }

    public void setResponseType(ResponseType responseType) { this.mResponseType = responseType;  }

    public Date getEvenTimeUTC() {return mEvenTimeUTC;   }

    public void setEvenTimeUTC(Date evenTimeUTC) {this.mEvenTimeUTC = evenTimeUTC;  }

    public int getSequenceNum() {return mSequenceNum;  }

    public void setSequenceNum(int sequenceNum) {  this.mSequenceNum = sequenceNum;  }

    public long getBoxId() { return mBoxId;    }

    public void setBoxId(long boxId) {this.mBoxId = boxId;  }

    public int getTDMsgQueue() { return mTDMsgQueue;   }

    public void setTDMsgQueue(int TDMsgQueue) {this.mTDMsgQueue = TDMsgQueue;   }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BlackBoxModel that = (BlackBoxModel) o;

        return new EqualsBuilder()
                .append(mOdometer, that.mOdometer)
                .append(mLat, that.mLat)
                .append(mLon, that.mLon)
                .append(mEngineHours, that.mEngineHours)
                .append(mSpeed, that.mSpeed)
                .append(mHeading, that.mLon)
                .append(mTERT, that.mTERT)
                .append(mResponseType, that.mResponseType)
                .append(mEvenTimeUTC, that.mEvenTimeUTC)
                .append(mSequenceNum, that.mSequenceNum)
                .append(mBoxId, that.mBoxId)
                .append(mTDMsgQueue, that.mTDMsgQueue)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mOdometer)
                .append(mLat)
                .append(mLon)
                .append(mEngineHours)
                .append(mLon)
                .append(mSpeed)
                .append(mHeading)
                .append(mTERT)
                .append(mResponseType)
                .append(mEvenTimeUTC)
                .append(mSequenceNum)
                .append(mBoxId)
                .append(mTDMsgQueue)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BlackBoxModel{");
        sb.append("mOdometer=").append(mOdometer);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLon=").append(mLon);
        sb.append(", mEngineHours=").append(mEngineHours);
        sb.append(", mSpeed=").append(mSpeed);
        sb.append(", mHeading=").append(mHeading);
        sb.append(", mTERT=").append(mTERT);
        sb.append(", mResponseType=").append(mResponseType.name());
        sb.append(", mEvenTimeUTC=").append(ViewUtils.getLocalDate("Etc/UTC", mEvenTimeUTC.getTime()));
        sb.append(", mSequenceNum=").append(mSequenceNum);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mTDMsgQueue=").append(mTDMsgQueue);
        sb.append('}');
        return sb.toString();
    }
}
