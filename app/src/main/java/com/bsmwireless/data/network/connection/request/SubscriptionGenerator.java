package com.bsmwireless.data.network.connection.request;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.connection.ConnectionUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *  Subscription request generator , sent during the beginning of the communication
 */

public class SubscriptionGenerator extends RequestGenerator {


    //  2 start bytes, 1 checksum, 2 length, 1 command, 1 packetId,  4 boxid, 2 year, 1 month, 1 day, 1 hour, 1 minute, 1 sec, 2 millisecond, 1 GPS, 1 update rate.
    private final int PACKET_LENGTH=22;
    public byte[] generateRequest(byte sequenceID, int boxID) {
        byte[] request = new byte[PACKET_LENGTH + HEADER_LENGTH];
        int index = 0;
       /* request[index++] = request[index++] = request[index++] = request[index++] = request[index++] = START_MESSAGE_INDICATOR; //Five characters ‘@’ (0X40)
        request[index++] = DEVICE_TYPE; // Representing 'A' for the device type Android
        request[index++] = request[index++] = START_PACKET_INDICATOR;*/
        System.arraycopy(generateHeader(), 0, request, index, HEADER_LENGTH+2);
        index = HEADER_LENGTH+2;
        // checksum
        request[CHECK_SUM_INDX] = 0;
        index++;
        //Packet length
        byte[] lengthBytes = ConnectionUtils.intToByte(PACKET_LENGTH, 2);
        System.arraycopy(lengthBytes, 0, request, index, 2); // 2 bytes
        index += 2;
        //Command
        request[index++] = SUBSCRIPTION_REQUEST;
        //PacketID -> sequence number
        request[index++] = sequenceID;
        //BoxId
        byte[] bytes = ConnectionUtils.intToByte(boxID,4);
        System.arraycopy(bytes, 0, request, index, 4);
        index += 4;
        //in UTC
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        int yr = cal.get(Calendar.YEAR);
        byte[] yrBytes = ConnectionUtils.intToByte(yr,2);
        System.arraycopy(yrBytes, 0, request, index, 2); // 2 bytes


        index += 2;
        request[index++] = (byte)(cal.get(Calendar.MONTH)+1);
        request[index++] = (byte)cal.get(Calendar.DAY_OF_MONTH);
        request[index++] = (byte)cal.get(Calendar.HOUR_OF_DAY);
        request[index++] = (byte)cal.get(Calendar.MINUTE);
        request[index++] = (byte)cal.get(Calendar.SECOND);
        byte[] msBytes = ConnectionUtils.intToByte(cal.get(Calendar.MILLISECOND), 2);
        System.arraycopy(msBytes, 0, request, index, 2); // 2 bytes


        index += 2;
        // 1- To recieve Gps data 0- no Gps data
        //Always received BOX GPS Data
        request[index++] = (byte)GPS_DATA;
        //Vehicle Status Update Rate
        request[index] = (byte) Constants.STATUS_UPDATE_RATE_SECS;
        request[CHECK_SUM_INDX] = ConnectionUtils.checkSum(request, 9);
        return request;
    }


}
