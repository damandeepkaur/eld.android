package com.bsmwireless.data.network.connection.response;

import com.bsmwireless.data.network.connection.ConnectionUtils;
import com.bsmwireless.models.BlackBoxModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Legacy vehicle status handler response from the box
 */

public class ECMDataProcessor extends ResponseProcessor {


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
            long boxId = ConnectionUtils.byteToUnsignedInt(new byte[]{ data[index],  data[index+1],  data[index+2],data[index+3]});

            boxData.setBoxId(boxId);
            index +=4;
            return this;
        } catch (Exception e) {

            Timber.e("Exception in handling ack handler", e);
        }
        return null;
    }

    public static void main(String[] args)

    {
        byte[] boxid =new byte[]{73,-59,4,1};
        byte test =(byte)129;
        String testStr=String.format("%8s", Integer.toBinaryString(test & 0xFF));
        String test1=String.format("%8s",Integer.toBinaryString((test & 0xFF) + 0x100).substring(1));
        String boxidStr1 = String.format("%8s", Integer.toBinaryString(boxid[0] & 0xFF));
        String boxidStr2 = String.format("%8s", Integer.toBinaryString(boxid[1] & 0xFF));
        String boxidStr3 = String.format("%8s", Integer.toBinaryString(boxid[2] & 0xFF));
        String boxidStr4 = String.format("%8s", Integer.toBinaryString(boxid[3] & 0xFF));

        String testStr1=String.format("%8s", Integer.toBinaryString((~test) +1 & 0xFF));



        byte[] boxidByte = ConnectionUtils.intToByte(312649,4);


        byte[] byteValue = new byte[4];
        int value=312649;
        for(int i=0; i<3;i++)
        {
            byteValue[i] =(byte) ((byte)(value) & 0xFF);
            value>>=8;
        }


        String temp="test";
      /*  int boxInt1 = ConnectionUtils.byteToUnsignedInt(boxid);


        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        int yr = cal.get(Calendar.YEAR);
        byte[] yrBytes = ConnectionUtils.intToByte(cal.get(Calendar.YEAR),2);


        int year =2017;
        byte[] yr1 = ConnectionUtils.intToByte(2017,2);

        Timber.i("resutls");*/

    }

}