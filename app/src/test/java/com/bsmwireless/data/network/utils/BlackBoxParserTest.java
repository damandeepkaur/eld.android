package com.bsmwireless.data.network.utils;

import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.network.blackbox.utils.BlackBoxParser;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

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
            assertThat(responseAck1, not(equalTo(BlackBoxResponseModel.ResponseType.NACK)));
            assertThat(BlackBoxResponseModel.ResponseType.ACK, not(equalTo(BlackBoxResponseModel.ResponseType.NACK)));
            assertEquals(31, responseAck1.getLength());
            assertTrue(BlackBoxResponseModel.ResponseType.ACK  == responseAck1.getResponseType());
            assertFalse(BlackBoxResponseModel.ResponseType.NACK == responseAck1.getResponseType());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

//    private byte[] buildAckBody(int packetId, String vin) {
//
//    }

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
        byte[] vinBytes = new byte[24];
        for(int i=0; i<vinBytes.length; i++) {
            vinBytes[i] = 0;
        }

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


    private String boxToString(byte[] boxCommand) {
        StringBuilder dataStr = new StringBuilder();

        for(int i=0; i<boxCommand.length; i++) {
            dataStr.append((char)boxCommand[i]);
        }

        return dataStr.toString();
    }

    private String boxToDebugHexString(byte[] boxCommand) {
        StringBuilder hexStr = new StringBuilder();

        for(int i=0; i<boxCommand.length; i++) {
            hexStr.append(i + "=" + String.format("%02x", boxCommand[i]) + ": ");
        }

        return hexStr.toString();
    }

    private String boxToCompactHexString(byte[] boxCommand) {
        StringBuilder hexStr = new StringBuilder();

        for(int i=0; i<boxCommand.length; i++) {
            hexStr.append(String.format("%02x", boxCommand[i]));
        }

        return hexStr.toString();
    }

    private String boxToEscapedHexString(byte[] boxCommand) {
        StringBuilder hexStr = new StringBuilder();

        for(int i=0; i<boxCommand.length; i++) {
            hexStr.append("\\x" + String.format("%02x", boxCommand[i]));
        }

        return hexStr.toString();
    }


}
