package com.bsmwireless.data.network.connection;

import com.bsmwireless.common.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *  Utility function used in generating request and processing the response
 */

public class ConnectionUtils {

    final byte START_MESSAGE_INDICATOR = (byte)'@';
    final byte START_PACKET_INDICATOR = (byte) 0xFF;
    final byte DEVICE_TYPE = (byte) Constants.DEVICE_TYPE.charAt(0);  // Representing 'A' for the device type Android


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
        if (byteArr == null) return value;
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

    /*
     * Used to calculate the checksum of the bytes for the message sent in the protocol request.
     */
    public static  byte checkSum(byte[] bytes, int indx)
    {
        byte sum=0;
        for(int i=indx;i<bytes.length;i++)
        {
            sum ^= bytes[i];
        }
        return sum;
    }

    public static String formattedDateUTC(Date date){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }


}
