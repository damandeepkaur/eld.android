package com.bsmwireless.data.network.blackbox.models;

import com.bsmwireless.models.BlackBoxModel;

public class BlackBoxResponseModel {

    private ResponseType mResponseType = ResponseType.NONE;
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
        NONE(Character.MIN_VALUE),
        ACK('a'),
        NACK('n'),
        STATUS_UPDATE('S'),
        IGNITION_ON('I'),
        IGNITION_OFF('i'),
        MOVING('G'),
        STOPPED('g'),
        SENSOR_CHANGE('E');

        final char msgType;

        ResponseType(char msgType) {
            this.msgType = msgType;
        }

        public static ResponseType valueOf(char type) {
            switch (type) {
                case 'a':
                    return ACK;
                case 'n':
                    return NACK;
                case 'S':
                    return STATUS_UPDATE;
                case 'I':
                    return IGNITION_ON;
                case 'i':
                    return IGNITION_OFF;
                case 'G':
                    return MOVING;
                case 'g':
                    return STOPPED;
                case 'E':
                    return SENSOR_CHANGE;
                default:
                    return NONE;
            }
        }

    }

    public enum NackReasonCode {
        CHECK_SUM_WRONG((byte) 0x01),
        BOX_ID_MISMATCH((byte) 0x02),
        TIME_STAMP_OLDER((byte) 0x03),
        UNKNOWN_ERROR((byte) 0x00);

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
            return UNKNOWN_ERROR;
        }
    }
}
