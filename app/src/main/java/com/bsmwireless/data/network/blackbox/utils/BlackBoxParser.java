package com.bsmwireless.data.network.blackbox.utils;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.models.BlackBoxModel;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Created by osminin on 14.08.2017.
 */

public class BlackBoxParser {
    public static final int START_INDEX = 11;
    //  2 start bytes, 1 checksum, 2 length, 1 command, 1 packetId,  4 boxid, 2 year, 1 month, 1 day, 1 hour, 1 minute, 1 sec, 2 millisecond, 1 GPS, 1 update rate.
    private static final int PACKET_LENGTH = 22;
    private static final byte SUBSCRIPTION_REQUEST = (byte) 'C';
    private static final byte START_MESSAGE_INDICATOR = (byte) '@';
    private static final byte START_PACKET_INDICATOR = (byte) 0xFF;
    private static final byte DEVICE_TYPE = (byte) Constants.DEVICE_TYPE.charAt(0);  // Representing 'A' for the device type Android
    private static final int HEADER_LENGTH = 6;
    private static final int GPS_DATA = 1;
    private static final int NO_GPS_DATA = 0;
    private static final int CHECK_SUM_INDX = 8;

    public static final int PREAMBLE_LENGTH = 5;

    public static final int VIN_INDEX_START = 13;
    public static final int VIN_INDEX_END = 36;

    public static BlackBoxResponseModel parseSubscription(byte[] data) throws UnsupportedEncodingException {
        BlackBoxResponseModel responseModel = new BlackBoxResponseModel();
        if (parseHeader(data, responseModel)) {
            int index = START_INDEX;
            byte msg = data[index++];
            responseModel.setResponseType(BlackBoxResponseModel.ResponseType.valueOf((char) msg));
            responseModel.setSequenceId(data[index++] & 0x0FF);
            // on Ack - parse for the VIN , available from the subscription ack message
            // is length 31 excluding the header
            if (responseModel.getResponseType() == BlackBoxResponseModel.ResponseType.Ack && responseModel.getLength() == 31) {
                int sum = 0;
                for (int i = VIN_INDEX_START; i < VIN_INDEX_END; ++i) {
                    sum |= data[i];
                }
                String vinNumber = "";
                if (sum > 0) {
                    vinNumber = new String(data, index, VIN_INDEX_END - VIN_INDEX_START, "ASCII");
                }
                responseModel.setVinNumber(vinNumber);
                Timber.i("VIN number from the box " + responseModel.getVinNumber());
            }
            if (responseModel.getResponseType() == BlackBoxResponseModel.ResponseType.NAck) {
                responseModel.setErrReasonCode(BlackBoxResponseModel.NackReasonCode.fromValue(data[index]));
            }
        }
        return responseModel;
    }

    public static BlackBoxResponseModel parseVehicleStatus(byte[] data) {
        BlackBoxResponseModel responseModel = new BlackBoxResponseModel();
        if (parseHeader(data, responseModel)) {
            BlackBoxModel boxData = responseModel.getBoxData();
            int index = START_INDEX;
            byte msg = data[index++];
            BlackBoxResponseModel.ResponseType responseType = BlackBoxResponseModel.ResponseType.valueOf((char) msg);
            responseModel.setResponseType(responseType);
            boxData.setResponseType(responseType);
            //boxID 12 to 15
            long boxId = ByteBuffer.wrap(new byte[]{data[index++],
                    data[index++],
                    data[index++],
                    data[index++]})
                    .order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
            boxData.setBoxId(boxId);

            //sequence number 16 and 17
            boxData.setSequenceNum(ConnectionUtils.byteToUnsignedInt(new byte[]{data[index++], data[index++]}));

            //Event Time processing
            // only last two digits of the year are given, add it to 2000
            int year = 2000 + data[index++];  // year at 18
            int month = data[index++];// month at 19 , zero index
            int day = data[index++]; //Day at 20
            int hr = data[index++];//Day at 21
            int min = data[index++];//Day at 22
            int sec = data[index++];//Day at 23

            Calendar cal = new GregorianCalendar(year, month - 1, day, hr, min, sec);
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            boxData.setEvenTimeUTC(cal.getTime());

            //TODO: process sensor state
            // sensor state 24-26 3 byte
            int sensorState = ConnectionUtils.byteToUnsignedInt(new byte[]{data[index++], data[index++], data[index++]});
            // sensor change mask 27-29 3 byte
            int sensorMask = ConnectionUtils.byteToUnsignedInt(new byte[]{data[index++], data[index++], data[index++]});
            //latitude at 30-33
            byte[] latLonArr = new byte[4];
            System.arraycopy(data, index, latLonArr, 0, 4);
            int lat = ByteBuffer.wrap(latLonArr).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
            boxData.setLat(lat / 1000000.0);
            index += 4;
            //longitude at 34-37
            System.arraycopy(data, index, latLonArr, 0, 4);
            int lon = ByteBuffer.wrap(latLonArr).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
            boxData.setLon(lon / 1000000.0);
            index += 4;
            //Heading at 38-39
            boxData.setHeading(ByteBuffer.wrap(new byte[]{data[index++],
                    data[index++]}).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort());
            //Speed 40
            int speed = (data[index++] & 0XFF);
            boxData.setSpeed(speed);
            //Odometer 41 to 44
            byte[] odometer = new byte[4];
            System.arraycopy(data, index, odometer, 0, 4);
            boxData.setOdometer(ByteBuffer.wrap(odometer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt());
            index += 4;

            //Total Engine Run time in seconds 45 to 48
            byte[] TERTArr = new byte[4];
            System.arraycopy(data, index, TERTArr, 0, 4);
            boxData.setTERT(ByteBuffer.wrap(TERTArr).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt());
            index += 4;
            Timber.i("Box Model processed:" + boxData.toString());
            // TD Messages in queue
            boxData.setTDMsgQueue((data[index] & 0XFF));
        }
        return responseModel;
    }

    public static byte[] generateSubscriptionRequest(byte sequenceID, int boxID, int updateRateMillis) {
        byte[] request = new byte[PACKET_LENGTH + HEADER_LENGTH];
        int index = 0;

        System.arraycopy(generateHeader(), 0, request, index, HEADER_LENGTH + 2);
        index = HEADER_LENGTH + 2;
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
        byte[] bytes = ConnectionUtils.intToByte(boxID, 4);
        System.arraycopy(bytes, 0, request, index, 4);
        index += 4;
        //in UTC
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        int yr = cal.get(Calendar.YEAR);
        byte[] yrBytes = ConnectionUtils.intToByte(yr, 2);
        System.arraycopy(yrBytes, 0, request, index, 2); // 2 bytes


        index += 2;
        request[index++] = (byte) (cal.get(Calendar.MONTH) + 1);
        request[index++] = (byte) cal.get(Calendar.DAY_OF_MONTH);
        request[index++] = (byte) cal.get(Calendar.HOUR_OF_DAY);
        request[index++] = (byte) cal.get(Calendar.MINUTE);
        request[index++] = (byte) cal.get(Calendar.SECOND);
        byte[] msBytes = ConnectionUtils.intToByte(cal.get(Calendar.MILLISECOND), 2);
        System.arraycopy(msBytes, 0, request, index, 2); // 2 bytes


        index += 2;
        // 1- To recieve Gps data 0- no Gps data
        //Always received BOX GPS Data
        request[index++] = (byte) GPS_DATA;
        //Vehicle Status Update Rate
        request[index] = (byte) (updateRateMillis / 1000);
        request[CHECK_SUM_INDX] = ConnectionUtils.checkSum(request, 9);
        return request;
    }

    public static boolean parseHeader(byte[] data, BlackBoxResponseModel responseModel) {
        int index;
        // Header starts with five @
        for (index = 0; index < PREAMBLE_LENGTH; ++index) {
            if ((char) data[index] != START_MESSAGE_INDICATOR) {
                return false;
            }
        }
        //TelematicDevice type -Always android
        if (data[index++] != (byte) Constants.DEVICE_TYPE.charAt(0)) {
            return false;
        }
        // Two bytes of 0xFF indicating start of the packet
        boolean res1 = (data[index++] != (byte) 0xFF);
        boolean res2 = (data[index++] != (byte) 0xFF);
        if (res1 && res2) {
            return false;
        }
        responseModel.setCheckSum(data[index++] & 0xFF)
                .setLength(ConnectionUtils.byteToUnsignedInt(data[index++], data[index])); // length -> 2 bytes

        return true;
    }

    private static byte[] generateHeader() {
        byte[] header = new byte[HEADER_LENGTH + 2]; // including two bytes for start of the packet
        int index;
        for (index = 0; index < PREAMBLE_LENGTH; index++) {
            header[index] = START_MESSAGE_INDICATOR;
        }
        header[index++] = DEVICE_TYPE;
        header[index++] = header[index] = START_PACKET_INDICATOR;

        return header;
    }
}
