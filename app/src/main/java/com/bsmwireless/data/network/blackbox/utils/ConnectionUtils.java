package com.bsmwireless.data.network.blackbox.utils;

import android.os.Build;

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

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
