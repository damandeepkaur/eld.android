package com.bsmwireless.data.network.Connection.Request;

import com.bsmwireless.data.network.Connection.ConnectionUtils;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by hsudhagar on 2017-07-23.
 */

public class SubscriptionRequestGenerator {


    public byte[] generateRequest(byte sequenceID, int boxID)
    {
        byte[] ba = new byte[13 + 4 + 9]; // 4 for boxID | 9 for timestamp
        ba[0] = ba[1] = ba[2] = ba[3] = ba[4] = (byte)'@'; //Five characters ‘@’ (0X40)
        ba[5] = 0x0a;
        ba[6] = ba[7] =(byte) 0xFF;

        ba[8] = 0; // checksum

        ba[9] = 7;  // total length from B6 to the end of the packet
        ba[10] = 0;

        ba[11] = (byte)'C';
        ba[12] = sequenceID++;  // PacketID -> sequence number
        if (sequenceID > 250)
            sequenceID = 1;
        int index = 13;
        byte[] bytes = BigInteger.valueOf(boxID).toByteArray();
        System.arraycopy(bytes, 0, ba, index, 4);
        index += 4;

        //in UTC
        GregorianCalendar cal= new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        byte[] yrBytes = BigInteger.valueOf(cal.get(Calendar.YEAR)).toByteArray();
        System.arraycopy(bytes, 0, ba, index, 2); // 2 bytes

        index += 2;
        ba[index++] = (byte)cal.get(Calendar.MONTH);
        ba[index++] = (byte)cal.get(Calendar.DAY_OF_MONTH);
        ba[index++] = (byte)cal.get(Calendar.HOUR_OF_DAY);
        ba[index++] = (byte)cal.get(Calendar.MINUTE);
        byte[] msBytes = BigInteger.valueOf(cal.get(Calendar.MILLISECOND)).toByteArray();
        System.arraycopy(bytes, 0, ba, index, 2); // 2 bytes

        index += 2;                            // 9 bytes for ts

        // 1- To recieve Gps data 0- no Gps data
        //Always received BOX GPS Data
        ba[index++] = (byte)1;
        //Vehicle Status Update Rate
        ba[index++] = (byte)10;
        // lastTS = SentinelMobile.Shared.Data.Utils.GetDateTimeUtcNow();
        ba[8] = ConnectionUtils.checkSum(ba, 9);
        return ba;
    }
}
