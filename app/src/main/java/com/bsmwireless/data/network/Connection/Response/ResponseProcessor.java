package com.bsmwireless.data.network.Connection.Response;

import com.bsmwireless.data.network.Connection.ConnectionUtils;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.widgets.graphview.DutyType;

import java.util.Date;

/**
 * Created by hsudhagar on 2017-07-20.
 */

public abstract class ResponseProcessor {



    private ResponseType mResponseType;
    private int mCheckSum;
    private int mLength;
    private int mSequenceId;
    private String mVinNumber;

    private NackReasonCode mErrReasonCode;

    private BlackBoxModel mBoxData = new BlackBoxModel();

   /* //Vehicle status Information
    public int odometer;
    public DutyType driverStatus;
    public Date evenTimeUTC;
    public long boxId;


    public int sequenceNum;

    public int TERT; // Total Engine Run time in seconds

    //TD Messages in Queue
    //Up to 250 pending messages (255 = queue size larger than 250). 
    public int TDMsgQueue;

    //Speed in kilometer per hour
    public int speed;

    public int heading;

    public double lat;
    public double lon;
*/
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
                    case 'A':
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
                return  null;

        }

    };


    public enum ResponseValue{MessageType, SequenceId, DriverStatus,  EventTime, Speed,  Heading, Odometer, Latitude, Longitude ,TERT };

    public enum NackReasonCode{
        CheckSum_Wrong((byte)0x01),
        BoxId_MisMatch((byte)0x02),
        TimeStamp_older((byte)0x03);
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

            return null;
        }
    };

    public ResponseType getResponseType() {    return mResponseType; }

    public void setResponseType(ResponseType responseType) { this.mResponseType = responseType;  }

    public int getCheckSum() {    return mCheckSum;   }

    public void setCheckSum(int checkSum) { this.mCheckSum =checkSum;  }

    public int getLength() {  return mLength;  }

    public void setLength(int length) {  this.mLength =length;  }

    public int getSequenceId() {     return mSequenceId;  }

    public void setSequenceId(int sequenceId) { this.mSequenceId = sequenceId;  }

    public String getVinNumber() { return mVinNumber;   }

    public void setVinNumber(String vinNumber) {this.mVinNumber = vinNumber;   }

    public NackReasonCode getErrReasonCode() { return mErrReasonCode;   }

    public void setErrReasonCode(NackReasonCode errReasonCode) {   this.mErrReasonCode = errReasonCode;    }

    public BlackBoxModel getBoxData() {  return mBoxData;  }

    public void setBoxData(BlackBoxModel boxData) {   this.mBoxData = boxData;  }

    public boolean parseHeader(byte[] data)
    {
        // validate header acccording to the protocol defintion

        // Header starts with five @
        for (int i=0;i<5;i++)
             if ((char)data[i] !='@') return false;

        //Device type - need to change for the new protocol
        if (data[5] != (byte)0x0A) return false;

        if (data[6] != (byte)0xFF && data[7] != (byte)0xFF)  return false;

        this.mCheckSum = data[8] & 0xFF;

        this.mLength= ConnectionUtils.byteToUnsignedInt(data[9] , data[10]);// length -> 2 bytes

        return true;

    }


}
