package com.bsmwireless.data.network.connection.request;

import com.bsmwireless.common.Constants;



public abstract class RequestGenerator {
    int HEADER_LENGTH=6;
    int GPS_DATA=1;
    int NO_GPS_DATA=0;
    int CHECK_SUM_INDX=8;


    final byte SUBSCRIPTION_REQUEST=(byte)'C';
    final byte START_MESSAGE_INDICATOR =(byte)'@';
    final byte START_PACKET_INDICATOR= (byte) 0xFF;
    final byte DEVICE_TYPE = (byte) Constants.DEVICE_TYPE.charAt(0);  // Representing 'A' for the device type Android

    public byte[] generateHeader(){
        byte[] header = new byte[HEADER_LENGTH+2]; // including two bytes for start of the packet
        int index;
        for(index =0 ; index < 5; index++) {
            header[index] = START_MESSAGE_INDICATOR;
        }
        header[index++] = DEVICE_TYPE;
        header[index++] = header[index] = START_PACKET_INDICATOR;

        return header;
    }

}
