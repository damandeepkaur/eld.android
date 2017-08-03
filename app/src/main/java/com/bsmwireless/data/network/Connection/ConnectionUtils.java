package com.bsmwireless.data.network.connection;

import com.bsmwireless.common.Constants;
import com.bsmwireless.widgets.alerts.DutyType;

/**
 *  Utility function to generate request and process the response
 */

public class ConnectionUtils {

    byte START_MESSAGE_INDICATOR = (byte)'@';
    byte START_PACKET_INDICATOR = (byte) 0xFF;
    byte DEVICE_TYPE = (byte) Constants.DEVICE_TYPE.charAt(0);  // Representing 'A' for the device type Android


    public static byte[] intToByte(int value, int size)
    {
        byte[] byteValue = new byte[size];
        for(int i=0; i<size;i++)
        {
            byteValue[i] = (byte)(value );
            value>>=8;
        }
        return byteValue;
    }

    public static int byteToUnsignedInt(byte[] byteArr)
    {
        int value=0;

        for(int i=0; i<byteArr.length-1;i++)
        {
            value |=( byteArr[i]& 0XFF) <<8*i;
        }
        return value;
    }

    public static int byteToUnsignedInt(byte b1, byte b2)
    {
        return (b2 & 0XFF) <<8 | (b1 &0XFF);
    }

    public static final byte checkSum(byte[] bytes, int indx)
    {
        byte sum=0;
        for(int i=indx;i<bytes.length;i++)
        {
            sum ^=bytes[i];
        }
        return sum;
    }

    public static DutyType getDutyType(int ecmStatus)
    {
        switch (ecmStatus)
        {
            case 1:
                return DutyType.OFF_DUTY;
            case 2:
                return DutyType.ON_DUTY;
            case 3:
                return DutyType.DRIVING;

        }

        throw new IllegalArgumentException();
    }



}
