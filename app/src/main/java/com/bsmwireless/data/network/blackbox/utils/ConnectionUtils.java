package com.bsmwireless.data.network.blackbox.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility function used in generating request and processing the response
 */

public class ConnectionUtils {
    /**
     * Util function to convert integer to byte array
     *
     * @param value given integer
     * @param size  number of bytes
     * @return byte array with 'size' number of bytes
     */
    public static byte[] intToByte(int value, int size) {
        byte[] byteValue = new byte[size];
        for (int i = 0; i < size; i++) {
            byteValue[i] = (byte) (value);
            value >>= 8;
        }
        return byteValue;
    }

    /**
     * Utils function to convert from byte array to unsigned integer
     *
     * @param byteArr byte array to be converted to integer
     * @return integer value of the bytearray
     */
    public static int byteToUnsignedInt(byte[] byteArr) {
        int value = 0;
        if (byteArr == null) return value;
        for (int i = 0; i < byteArr.length - 1; i++) {
            value |= (byteArr[i] & 0XFF) << 8 * i;
        }
        return value;
    }

    public static int byteToUnsignedInt(byte b1, byte b2) {
        return (b2 & 0XFF) << 8 | (b1 & 0XFF);
    }


    /**
     * Used to calculate the checksum ,XOR of all the bytes from and including the index byte
     *
     * @param bytes, byte array to find the checksum
     * @param indx,  the position from which the checksum is calculated.     *
     * @return checksum byte
     */
    public static byte checkSum(byte[] bytes, int indx) {
        byte sum = 0;
        for (int i = indx; i < bytes.length; i++) {
            sum ^= bytes[i];
        }
        return sum;
    }

    /**
     * Util function to check if a bit is set or not, which represent a vehicle state in the protocol
     *
     * @param sensorArr bytearray of sensor bits
     * @param sensorbit bit position to read from the array
     * @return true for bit 1 and false for bit 0
     */
    public static boolean checkSensorState(byte[] sensorArr, int sensorbit) {
        byte[] sensorLSB = new byte[sensorArr.length];
        // Find the byte position in the array
        int bytePosition = sensorbit / 8;
        // Find the bit position in that byte
        int bitPosition = sensorbit % 8;
        byte srcByte = sensorArr[bytePosition];
        // Shift to the bit position and add 1
        int valInt = srcByte >> (bitPosition) & 0x001;
        return valInt == 1;
    }

    /**
     * Util function to convert Date to formatted string
     *
     * @param date Given a UTC date
     * @return return a date string in "yyyy-MM-dd HH:mm:ss" format
     */
    public static String formattedDateUTC(Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

}
