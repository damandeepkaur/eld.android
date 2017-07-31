package com.bsmwireless.data.network.connection.request;


import com.bsmwireless.data.network.connection.ConnectionUtils;

/**
 *  Vehicle status Request Generator to get the box information
 */

public class VehicleStatusGenerator extends RequestGenerator {
    // 2 start bytes(-1) , 1 checksum , 2 length , 1 command
    int PACKET_LENGTH=6;
    public byte[] generateRequest(byte sequenceID, int boxID) {
        byte[] request = new byte[PACKET_LENGTH + HEADER_LENGTH];
        int index = 0;
        /*for(index =0 ; index < 5; index++) {
            ba[index] = START_MESSAGE_INDICATOR;
        }
        ba[index++] = DEVICE_TYPE;
        ba[index++] = ba[index++] = START_PACKET_INDICATOR;*/
        System.arraycopy(generateHeader(), 0, request, index, HEADER_LENGTH+2);
        index = HEADER_LENGTH+2;
        // checksum at 8
        request[CHECK_SUM_INDX] = 0;
        // total length from B6 to the end of the packet
        request[index++] = (byte)PACKET_LENGTH;
        request[index++] = (byte)'S';
        request[CHECK_SUM_INDX] = ConnectionUtils.checkSum(request, 9);
        return request;
    }

}
