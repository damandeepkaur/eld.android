package com.bsmwireless.data.network.utils;

import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.network.blackbox.utils.BlackBoxParser;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import app.bsmuniversal.com.RxSchedulerRule;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests for BlackBoxParser.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlackBoxParserTest {

    private static final String HEX_PREAMBLE = "4040404040";    // B0-B4: preamble "@@@@@" (@ = 0x40)
    private static final String HEX_ANDROID = "41";             // B5: device type 'A' = Android
    private static final String HEX_MESSAGE_START = "ffff";     // B6-B7: message start
    private static final String HEX_HEADER = HEX_PREAMBLE + HEX_ANDROID + HEX_MESSAGE_START;

    private static final String HEX_EMPTY_VIN = "000000000000000000000000000000000000000000000000"; // B13-B36 all 0x00

    private static final int MAX_VIN_LENGTH = 24;

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateSubscription() {
        byte sequenceId = (byte)0;
        int boxId = 312648;
        int updateRateMillis = 60000;

        byte[] response = BlackBoxParser.generateSubscriptionRequest(sequenceId, boxId, updateRateMillis);

        System.out.println(boxToString(response));
        System.out.println(boxToCompactHexString(response));
        System.out.println(boxToEscapedHexString(response));
    }

    @Test
    public void testParseSubscriptionVin() {
        // given
        String vin1 = "JH4KB16225C021129";
        String vin2 = "SAJDA41C252JM4368";

        String vinLength0 = "";
        String vinLength1 = "1";
        String vinLength23 = "12345678901234567890123";
        String vinLength24 = "123456789012345678901234";


        try {
            // when
            BlackBoxResponseModel response1 = BlackBoxParser.parseSubscription(generateSubscriptionAccepted(vin1));
            BlackBoxResponseModel response2 = BlackBoxParser.parseSubscription(generateSubscriptionAccepted(vin2));

            BlackBoxResponseModel responseLength0 = BlackBoxParser.parseSubscription(generateSubscriptionAccepted(vinLength0));
            BlackBoxResponseModel responseLength1 = BlackBoxParser.parseSubscription(generateSubscriptionAccepted(vinLength1));
            BlackBoxResponseModel responseLength23 = BlackBoxParser.parseSubscription(generateSubscriptionAccepted(vinLength23));
            BlackBoxResponseModel responseLength24 = BlackBoxParser.parseSubscription(generateSubscriptionAccepted(vinLength24));

            // then
            assertEquals(vin1, response1.getVinNumber());
            assertEquals(vin2, response2.getVinNumber());
            assertEquals(BlackBoxResponseModel.ResponseType.ACK, response1.getResponseType());
            assertEquals(BlackBoxResponseModel.ResponseType.ACK, response2.getResponseType());
            assertEquals(31, response1.getLength());
            assertEquals(31, response2.getLength());

            assertEquals(vinLength0, responseLength0.getVinNumber());
            assertEquals(vinLength1, responseLength1.getVinNumber());
            assertEquals(vinLength23, responseLength23.getVinNumber());
            assertEquals(vinLength24, responseLength24.getVinNumber());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    // TODO: parseSubscription() - exception


    // TODO: parseVehicleStatus()
    // TODO: generateSubscriptionRequest() (with VIN)

    @Test
    public void testParseSubscriptionAckNoVin() {
        // given

        // HOS ack example 1: 40:40:40:40:40:41:FF:FF:7C:1F:00:61:02:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00
        //                   |--------HEADER---------|-------------------------------------BODY---------------------------------------------|
        String dataAck1 = HEX_HEADER
                + "7c"                  // B8: checksum
                + "1f00"                // B9-B10: length
                + "61"                  // B11: command 'a'
                + "02"                  // B12: packet ID
                + "000000000000000000000000000000000000000000000000";   // B13-B36: VIN (all 0x00 if unavailable)

        byte[] dataAckBytes1 = new BigInteger(dataAck1,16).toByteArray();
        BlackBoxResponseModel responseAck1;

        // when

        try {
            responseAck1 = BlackBoxParser.parseSubscription(dataAckBytes1);


            // then
            assertEquals(BlackBoxResponseModel.ResponseType.ACK, responseAck1.getResponseType());
            assertThat(responseAck1.getResponseType(), not(equalTo(BlackBoxResponseModel.ResponseType.NACK)));
            assertEquals(31, responseAck1.getLength());
            assertTrue(BlackBoxResponseModel.ResponseType.ACK  == responseAck1.getResponseType());
            assertFalse(BlackBoxResponseModel.ResponseType.NACK == responseAck1.getResponseType());
            assertEquals("", responseAck1.getVinNumber());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    // TODO: generateImmediateStatusRequest()
    // TODO: parseHeader

    @Test
    public void testVinToBytes() {
        // given
        String vin = "SALVT2BG1CH654491"; // VIN Description: 2012 Land Rover Range Rover Evoque from https://vingenerator.org/
        String expectedHex = "53414c5654324247314348363534343931" + "00000000000000"; // VIN(17 bytes) + padding(7 bytes)

        // when
        byte[] result = vinToBytes(vin);

        // then
        assertArrayEquals(result, new BigInteger(expectedHex,16).toByteArray());
    }

    /**
     * Converts a VIN string into an ELD-box-protocol compatible byte array.
     *
     * @param vin a VIN string e.g. "SALVT2BG1CH654491"
     * @return a 24-byte array representing a VIN in the box protocol
     */
    private byte[] vinToBytes(String vin) {
        byte[] vinBytes = new byte[MAX_VIN_LENGTH];
        for(int i=0; i<vinBytes.length; i++) {
            vinBytes[i] = 0;
        }

        if(vin.length() == 0) return vinBytes;

        String vinString = "";


        for(int i=0; i<vin.length() && i<MAX_VIN_LENGTH; i++) { // note: truncates VIN that are too long
            char c = vin.charAt(i);
            vinString = vinString + charToByteHexStr(c);  // construct string for debugging... (not efficient, but this is for tests only)
        }

        System.out.println(vinString);

        byte[] readBytes = new BigInteger(vinString,16).toByteArray();
        for(int i=0; i<readBytes.length; i++) {
            vinBytes[i] = readBytes[i]; // write converted VIN chars into result
        }

        return vinBytes;
    }

    /**
     * Converts char into a 2-character hex string.
     *
     * @param c
     * @return
     */
    private String charToByteHexStr(char c) {
        return String.format("%02x", (int)c);
    }


    /**
     * Prints a raw string representation of a box command.
     *
     * @param boxCommand
     * @return
     */
    private String boxToString(byte[] boxCommand) {
        StringBuilder dataStr = new StringBuilder();

        for(int i=0; i<boxCommand.length; i++) {
            dataStr.append((char)boxCommand[i]);
        }

        return dataStr.toString();
    }

    /**
     * Produces a human-readable string for a box command.
     *
     * @param boxCommand
     * @return
     */
    private String boxToDebugHexString(byte[] boxCommand) {
        StringBuilder hexStr = new StringBuilder();

        for(int i=0; i<boxCommand.length; i++) {
            hexStr.append(i + "=" + String.format("%02x", boxCommand[i]) + ": ");
        }

        return hexStr.toString();
    }

    /**
     * Produces a compact hex string for a box command.
     *
     * Resulting string is a series of hex digits, with each byte being represented by
     * two hex digits.
     *
     * e.g. "53414c5654324247314348363534343931" (length = 17 bytes)
     *
     * @param boxCommand
     * @return
     */
    private String boxToCompactHexString(byte[] boxCommand) {
        StringBuilder hexStr = new StringBuilder();

        for(int i=0; i<boxCommand.length; i++) {
            hexStr.append(String.format("%02x", boxCommand[i]));
        }

        return hexStr.toString();
    }

    /**
     * Produces escaped hex string for a box command.
     *
     * @param boxCommand
     * @return
     */
    private String boxToEscapedHexString(byte[] boxCommand) {
        StringBuilder hexStr = new StringBuilder();

        for(int i=0; i<boxCommand.length; i++) {
            hexStr.append("\\x" + String.format("%02x", boxCommand[i]));
        }

        return hexStr.toString();
    }

    /**
     * Calculates checksum for black box.
     *
     * @param bytes
     * @param start
     * @return
     */
    private byte checksum(byte[] bytes, int start) {
        byte sum = 0;
        for (int i=start; i<bytes.length; i++) {
            sum ^= bytes[i];
        }
        return sum;
    }

    /**
     *
     * @param vin a VIN, will be truncated if length > MAX_VIN_LENGTH
     * @return
     */
    private byte[] generateSubscriptionAccepted(String vin) {

        String vinHex = boxToCompactHexString(vinToBytes(vin)); // vinToBytes() truncates to MAX_VIN_LENGTH digits

        String dataAck1 = HEX_HEADER
                + "00"          // B8: checksum (fake for now, calculated later)
                + "1f00"        // B9-B10: length
                + "61"          // B11: command 'a'
                + "02"          // B12: packet ID (any is ok)
                + vinHex;       // B13-B36: VIN (all 0x00 if unavailable)

        byte[] result = new BigInteger(dataAck1,16).toByteArray();

        byte checksum = checksum(result, 9);
        result[8] = checksum;

        return result;
    }


}
