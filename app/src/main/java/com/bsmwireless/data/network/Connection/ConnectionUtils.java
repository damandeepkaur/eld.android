package com.bsmwireless.data.network.Connection;

import com.bsmwireless.widgets.graphview.DutyType;

import java.math.BigDecimal;

/**
 * Created by hsudhagar on 2017-07-20.
 */

public class ConnectionUtils {

    public static byte[] intToByte(int value, int size)
    {
        byte[] byteValue = new byte[size];
        for(int i=0; i<byteValue.length-1;i++)
        {
            byteValue[i] = (byte)(value );
            value>>=8;
        }
        return byteValue;
    }

    public static int byteToUnsignedInt(byte b1, byte b2)
    {
        return (b2 & 0XFF) <<8 | (b1 &0XFF);


    }
    public static int byteToUnsignedInt(byte[] byteArr)
    {
        return (int) (byteArr[3] & 0XFF) <<24 | (byteArr[3] & 0XFF) <<16 | (byteArr[1] & 0XFF) <<8 | (byteArr[0] &0XFF);

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

       return null;
    }


}
