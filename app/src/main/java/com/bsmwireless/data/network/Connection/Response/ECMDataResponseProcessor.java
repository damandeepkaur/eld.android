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

public class ECMDataResponseProcessor extends ResponseProcessor {


    public ResponseProcessor parse(byte[] data) {
        try {
            if (!parseHeader(data)) return null;
            BlackBoxModel boxData= this.getBoxData();
            int index = 11;
            byte cmd = data[11];
            Timber.i("Message type received from box:" +(char) cmd);
            this.setResponseType( ResponseType.valueOf((char) cmd));
            index++;
            boxData.setSequenceNum(data[12] & 0x0FF);
            index++;
            boxData.setDriverStatus(ConnectionUtils.getDutyType((int)data[13]));   //1- OFF_DUTY, 2- ON_DUTY , 3- DRIVING
            index++;
            byte[] odometer = new byte[4];

            System.arraycopy(data,index,odometer,0,4);
            boxData.setOdometer(ConnectionUtils.byteToUnsignedInt(odometer));
            index += 4;
            if ((int) data[index] ==1) //GPS valid  timestamp- parse for date and time
            {
                int hr = data[index+1];
                int min = data[index+2];
                int sec = data[index+3];
                int day = data[index + 4];
                int month = data[index + 5];
                int year = 2000 + data[index + 6];

                Calendar cal = new GregorianCalendar(year,month,day,hr,min,sec);
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                boxData.setEvenTimeUTC(cal.getTime());
                String s = cal.getTime().toString();

            }
            index += 7;

            //boxID
            long boxId = data[index];
            index++;
            boxId |= (int)((data[index] & 0XFF) << 8);
            index++;
            boxId |= (int)((data[index] & 0XFF) << 16);
            index++;
            boxData.setBoxId(boxId);

            return this;
        } catch (Exception e) {

            Timber.e("Exception in handling ack handler", e);
        }
        return null;
    }

}