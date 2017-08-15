package com.bsmwireless.data.network.blackbox.models;

import com.bsmwireless.models.BlackBoxModel;

/**
 * Created by osminin on 14.08.2017.
 */

public class BlackBoxResponseModel {

    private ResponseType mResponseType = ResponseType.None;
    private int mCheckSum;
    private int mLength;
    private int mSequenceId;
    private String mVinNumber;
    private NackReasonCode mErrReasonCode;
    private BlackBoxModel mBoxData = new BlackBoxModel();

    public ResponseType getResponseType() {
        return mResponseType;
    }

    public BlackBoxResponseModel setResponseType(ResponseType responseType) {
        mResponseType = responseType;
        return this;
    }

    public int getCheckSum() {
        return mCheckSum;
    }

    public BlackBoxResponseModel setCheckSum(int checkSum) {
        mCheckSum = checkSum;
        return this;
    }

    public int getLength() {
        return mLength;
    }

    public BlackBoxResponseModel setLength(int length) {
        mLength = length;
        return this;
    }

    public int getSequenceId() {
        return mSequenceId;
    }

    public BlackBoxResponseModel setSequenceId(int sequenceId) {
        mSequenceId = sequenceId;
        return this;
    }

    public String getVinNumber() {
        return mVinNumber;
    }

    public BlackBoxResponseModel setVinNumber(String vinNumber) {
        mVinNumber = vinNumber;
        return this;
    }

    public NackReasonCode getErrReasonCode() {
        return mErrReasonCode;
    }

    public BlackBoxResponseModel setErrReasonCode(NackReasonCode errReasonCode) {
        mErrReasonCode = errReasonCode;
        return this;
    }

    public BlackBoxModel getBoxData() {
        return mBoxData;
    }

    public BlackBoxResponseModel setBoxData(BlackBoxModel boxData) {
        mBoxData = boxData;
        return this;
    }

    public enum ResponseType {
        None(Character.MIN_VALUE),
        Ack('a'),
        NAck('n'),
        StatusUpdate('S'),
        IgnitionOn('I'),
        IgnitionOff('i'),
        Moving('G'),
        Stopped('g'),
        SensorChange('E');

        final char msgType;

        ResponseType(char msgType) {
            this.msgType = msgType;
        }

        public static ResponseType valueOf(char type) {
            switch (type) {
                case 'a':
                    return Ack;
                case 'n':
                    return NAck;
                case 'S':
                    return StatusUpdate;
                case 'I':
                    return IgnitionOn;
                case 'i':
                    return IgnitionOff;
                case 'G':
                    return Moving;
                case 'g':
                    return Stopped;
                case 'E':
                    return SensorChange;
                default:
                    return None;
            }
        }

    }

    public enum NackReasonCode {
        CheckSum_Wrong((byte) 0x01),
        BoxId_MisMatch((byte) 0x02),
        TimeStamp_older((byte) 0x03),
        Unknown_Error((byte) 0x00);

        final byte mByteVal;

        NackReasonCode(byte val) {
            this.mByteVal = val;
        }

        private byte getByteVal() {
            return mByteVal;
        }

        public static NackReasonCode fromValue(byte byteVal) {
            for (NackReasonCode reasonCode : values()) {
                if (reasonCode.getByteVal() == byteVal)
                    return reasonCode;

            }

            return Unknown_Error;
        }
    }
}
