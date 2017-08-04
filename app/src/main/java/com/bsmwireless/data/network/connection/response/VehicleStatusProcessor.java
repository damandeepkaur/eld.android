package com.bsmwireless.data.network.connection.response;

import com.bsmwireless.data.network.connection.ConnectionUtils;
import com.bsmwireless.models.BlackBoxModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import timber.log.Timber;

/**
 *  Processor for vehicle status response , to process the box data
 */

public class VehicleStatusProcessor extends ResponseProcessor {
    private final int START_INDEX = 11;

    /*
     *  Parse the box data value and return the responseprocessor object
     *  on Error or invalid response, return null
     */
    public ResponseProcessor parse(byte[] data) {
        try {

            if (!parseHeader(data)) return null;
            BlackBoxModel boxData= this.getBoxData();
            int index = START_INDEX;
            byte msg = data[index];
            ResponseType responseType=ResponseType.valueOf((char) msg);
            this.setResponseType(responseType);
            boxData.setResponseType(responseType);
            index++;
            //boxID 12 to 15
            long boxId = ConnectionUtils.byteToUnsignedInt(new byte[]{ data[index],  data[index+1],  data[index+2], data[index+3]});
            index +=4;

            boxData.setBoxId(boxId);

            //sequence number 16 and 17
            boxData.setSequenceNum(ConnectionUtils.byteToUnsignedInt(new byte[]{data[index], data[index+1]}));
            index += 2;

            //Event Time processing
            // only last two digits of the year are given, add it to 2000
            int year = 2000 + data[index];  // year at 18
            index++;
            int month = data[index];// month at 19 , zero index
            index++;
            int day = data[index]; //Day at 20
            index++;
            int hr = data[index];//Day at 21
            index++;
            int min = data[index];//Day at 22
            index++;
            int sec = data[index];//Day at 23
            index++;

            Calendar cal = new GregorianCalendar(year,month-1,day,hr,min,sec);
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            boxData.setEvenTimeUTC(cal.getTime());

            //TODO: process sensor state
            // sensor state 24-26 3 byte
            int sensorState = ConnectionUtils.byteToUnsignedInt(new byte[]{data[index], data[index+1], data[index+2]});
            // sensor change mask 27-29 3 byte
            index +=3;
            int sensorMask = ConnectionUtils.byteToUnsignedInt(new byte[]{data[index], data[index+1], data[index+2]});
            index +=3;
            //latitude at 30-33
            byte[] latArr = new byte[4];
            System.arraycopy(data,index,latArr,0,4);
            double lat =ConnectionUtils.byteToUnsignedInt(latArr);
            boxData.setLat(lat);
            index = index+4;
            //longitude at 34-37
            byte[] lonArr = new byte[4];
            System.arraycopy(data,index,lonArr,0,4);
            double lon =ConnectionUtils.byteToUnsignedInt(lonArr);
            boxData.setLon(lon);
            index = index + 4;
            //Heading at 38-39
            boxData.setHeading(ConnectionUtils.byteToUnsignedInt(new byte[]{data[index], data[index+1]}));
            index += 2;
            int speed =(data[index] & 0XFF);
            boxData.setSpeed(speed);
            index++;
            //Odometer 41 to 44
            byte[] odometer = new byte[4];
            System.arraycopy(data,index,odometer,0,4);
            boxData.setOdometer(ConnectionUtils.byteToUnsignedInt(odometer));
            index = index+4;

            //Total Engine Run time in seconds 45 to 48
            byte[] TERTArr = new byte[4];
            System.arraycopy(data,index,TERTArr,0,4);
            boxData.setTERT(ConnectionUtils.byteToUnsignedInt(TERTArr));
            index = index+4;
            Timber.i("Box Model processed:" +  boxData.toString());
            // TD Messages in queue
            boxData.setTDMsgQueue ( (data[index] & 0XFF));
            return this;

        } catch (Exception e) {

            Timber.e("Exception in handling status ", e);
        }
        return null;
    }
}