package com.bsmwireless.data.network.connection.response;

import com.bsmwireless.data.network.connection.ConnectionUtils;
import com.bsmwireless.models.BlackBoxModel;

import java.util.IllegalFormatException;

/**
 *  Abstracted the Response processor with definitions  and  common parser
 */

public abstract class ResponseProcessor {
    private ResponseType mResponseType;
    private int mCheckSum;
    private int mLength;
    private int mSequenceId;
    private String mVinNumber;
    private NackReasonCode mErrReasonCode;
    private BlackBoxModel mBoxData = new BlackBoxModel();
    private StatusType responseStatus;
    private String errMsg;

    public enum StatusType{ Success, Failed};
    public enum ResponseType{
        Ack('a'),
        NAck('n'),
        StatusUpdate('S'),
        IgnitionOn('I'),
        IgnitionOff('i'),
        Moving('G'),
        Stopped('g'),
        SensorChange('E');

        char msgType;

        ResponseType(char msgType)
        {
            this.msgType = msgType;
        }
        public char typeChar(){return msgType;}

        public static ResponseType valueOf(char type)
        {


            switch(type)
            {
                case 'A'://TODO: to test with legacy, need to be removed
                case 'a':
                    return Ack;
                case 'n':
                    return NAck;
                case 'S':
                case 'D': //TODO: to test with legacy, need to be removed
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
            }

            throw new IllegalArgumentException("Unknown Response Type " + type );
        }

    };


    public enum ResponseValue{MessageType, SequenceId, DriverStatus,  EventTime, Speed,  Heading, Odometer, Latitude, Longitude ,TERT };

    public enum NackReasonCode{
        CheckSum_Wrong((byte)0x01),
        BoxId_MisMatch((byte)0x02),
        TimeStamp_older((byte)0x03),
        Unknown_Error((byte)0x00);

        private byte mByteVal;

        NackReasonCode(byte val){
            this.mByteVal = val;
        }

        private byte getByteVal()
        {
            return mByteVal;
        }

        static NackReasonCode fromValue(byte byteVal)
        {
            for (NackReasonCode  reasonCode: values())
            {
                if (reasonCode.getByteVal() == byteVal)
                    return reasonCode;

            }

            return Unknown_Error;
        }
    };

    public ResponseType getResponseType() { return mResponseType; }

    public void setResponseType(ResponseType responseType) { this.mResponseType = responseType;  }

    public int getCheckSum() { return mCheckSum;   }

    public void setCheckSum(int checkSum) { this.mCheckSum =checkSum;  }

    public int getLength() { return mLength;  }

    public void setLength(int length) { this.mLength =length;  }

    public int getSequenceId() { return mSequenceId;  }

    public void setSequenceId(int sequenceId) { this.mSequenceId = sequenceId;  }

    public String getVinNumber() { return mVinNumber;   }

    public void setVinNumber(String vinNumber) {this.mVinNumber = vinNumber;   }

    public NackReasonCode getErrReasonCode() { return mErrReasonCode;   }

    public void setErrReasonCode(NackReasonCode errReasonCode) { this.mErrReasonCode = errReasonCode;    }

    public BlackBoxModel getBoxData() { return mBoxData;  }

    public void setBoxData(BlackBoxModel boxData) { this.mBoxData = boxData;  }
    /*
     *  validate header according to the protocol defintion
     */
    public boolean parseHeader(byte[] data) {
        int indx=0;
        // Header starts with five @
        for (indx=0;indx<5;indx++) {
            if ((char) data[indx] != '@') return false;
        }
        //if (data[indx++] != (byte) Constants.DEVICE_TYPE.charAt(0)) return false;
        byte receivedByte = data[5];
        if (data[indx++] != receivedByte) return false;

        if (data[indx++] != (byte)0xFF && data[indx++] != (byte)0xFF)  return false;

        this.mCheckSum = data[indx++] & 0xFF;

        this.mLength= ConnectionUtils.byteToUnsignedInt(data[indx++] , data[indx++]);// length -> 2 bytes

        return true;

    }


}
