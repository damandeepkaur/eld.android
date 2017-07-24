package com.bsmwireless.data.network.Connection.Response;

import com.bsmwireless.data.network.Connection.ConnectionUtils;
import com.bsmwireless.models.BlackBoxModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Created by hsudhagar on 2017-07-21.
 */

public class VehicleStatusResponseProcessor extends ResponseProcessor {


    public ResponseProcessor parse(byte[] data) {
        try {

            if (!parseHeader(data)) return null;
            BlackBoxModel boxData= this.getBoxData();
            int index = 11;
            byte msg = data[11];
            this.setResponseType(ResponseType.valueOf((char) msg));
            index++;
            //boxID 12 to 15
            long boxId = data[index];
            index++;
            boxId |= (int)((data[index] & 0XFF) << 8);
            index++;
            boxId |= (int)((data[index] & 0XFF) << 16);
            index++;
            Timber.i("boxid received from box:" +  boxId);
            boxData.setBoxId(boxId);
            index++;
            //sequence number 16 and 17
            boxData.setSequenceNum(ConnectionUtils.byteToUnsignedInt(data[index], data[index+1]));
            index += 2;

            //Event Time
            int year = 2000 + data[index];  // year at 18
            index++;
            int month = data[index];// month at 19
            index++;
            int day = data[index]; //Day at 20
            index++;
            int hr = data[index];//Day at 21
            index++;
            int min = data[index];//Day at 22
            index++;
            int sec = data[index];//Day at 23
            index++;

            Calendar cal = new GregorianCalendar(year,month,day,hr,min,sec);
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            boxData.setEvenTimeUTC(cal.getTime());
            String s = cal.getTime().toString();

            //TODO: process sensor state

            index=30;
            //latitude at 30-33
            byte[] latArr = new byte[4];
            System.arraycopy(data,index,latArr,0,4);
            double lat =ConnectionUtils.byteToUnsignedInt(latArr);
            boxData.setLat(lat);
            Timber.i("Latitude received from box:" +  lat);
            index = index+4;
            //longitude at 34-37
            byte[] lonArr = new byte[4];
            System.arraycopy(data,index,lonArr,0,4);
            double lon =ConnectionUtils.byteToUnsignedInt(lonArr);
            boxData.setLon(lon);
            Timber.i("Longitude received from box:" +  lon);
            index = index+4;
            //Heading at 38-39
            boxData.setHeading(ConnectionUtils.byteToUnsignedInt(data[index], data[index+1]));
            index += 2;
            index+=2;
            int speed = (int)(data[index] & 0XFF);
            index++;
            //Odometer 41 to 44
            byte[] odometer = new byte[4];
            System.arraycopy(data,index,odometer,0,4);
            boxData.setOdometer(ConnectionUtils.byteToUnsignedInt(odometer));
            Timber.i("Odometer received from box:" +  boxData.getOdometer());
            index = index+4;

            //Total Engine Run time in seconds
            byte[] TERTArr = new byte[4];
            System.arraycopy(data,index,TERTArr,0,4);
            boxData.setTERT(ConnectionUtils.byteToUnsignedInt(TERTArr));
            Timber.i("TERT received from box:" +  boxData.getTERT());
            index = index+4;


            // TD Messages in queue
            boxData.setTDMsgQueue ( (int)(data[index] & 0XFF));
            return this;
        } catch (Exception e) {
;
            Timber.e("Exception in handling ack handler", e);
        }
        return null;
    }

}