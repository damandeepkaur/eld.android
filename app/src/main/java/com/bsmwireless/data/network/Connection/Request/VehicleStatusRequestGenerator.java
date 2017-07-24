package com.bsmwireless.data.network.Connection.Request;

import com.bsmwireless.data.network.Connection.ConnectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by hsudhagar on 2017-07-23.
 */

public class VehicleStatusRequestGenerator {

    public byte[] generateRequest(byte sequenceID, int boxID) {
        byte[] ba = new byte[13 + 4 + 9]; // 4 for boxID | 9 for timestamp
        ba[0] = ba[1] = ba[2] = ba[3] = ba[4] = (byte)'@';
        ba[5] = 0x0a;
        ba[6] = ba[7] =(byte) 0xff;

        ba[8] = 0; // checksum

        ba[9] = 7;  // total length from B6 to the end of the packet
        ba[10] = 0;

        ba[11] = (byte)'D';

        ba[12] = sequenceID++;  // PacketID -> sequence number
        if (sequenceID > 250)
            sequenceID = 1;
        int index = 13;
        byte[] bytes = ConnectionUtils.intToByte(boxID,4);
        System.arraycopy(bytes, 0, ba, index, 4);
        index += 4;

       // in UTC
        GregorianCalendar cal= new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        byte[] yrBytes = ConnectionUtils.intToByte(cal.get(Calendar.YEAR),2);
        System.arraycopy(bytes, 0, ba, index, 2); // 2 bytes

        index += 2;
        ba[index++] = (byte)cal.get(Calendar.MONTH);
        ba[index++] = (byte)cal.get(Calendar.DAY_OF_MONTH);
        ba[index++] = (byte)cal.get(Calendar.HOUR_OF_DAY);
        ba[index++] = (byte)cal.get(Calendar.MINUTE);
        byte[] msBytes = ConnectionUtils.intToByte(cal.get(Calendar.MILLISECOND), 2);
        System.arraycopy(bytes, 0, ba, index, 2); // 2 bytes
        index += 2;                            // 9 bytes for ts

        ba[8] =ConnectionUtils.checkSum(ba, 9);


        return ba;
    }

}
