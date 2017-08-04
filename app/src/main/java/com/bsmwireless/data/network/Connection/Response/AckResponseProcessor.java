package com.bsmwireless.data.network.connection.response;

import timber.log.Timber;

/**
 *   Subscription Response Processor , processing Ack or Nack
 */

public class AckResponseProcessor extends ResponseProcessor {


    private final int START_INDEX = 11;
    /*
     * On successful parsing, return the value in the ResponseProcessor object
     * On exception or invalid response, return null
     */
    public ResponseProcessor parse(byte[] data) {
        try {

            if (!parseHeader(data)) return null;
            int index = START_INDEX;
            byte msg = data[index];
            this.setResponseType( ResponseType.valueOf((char) msg));
            index++;
            this.setSequenceId( data[index] & 0x0FF);
            index++;
            // on Ack - parse for the VIN , available from the subscription ack message
            // is length 31 excluding the header
            if (this.getResponseType()==ResponseType.Ack  && this.getLength() == 31) {
                this.setVinNumber(new String(data, index,24, "ASCII"));// from 13 to 36
                Timber.i("VIN number from the box" + this.getVinNumber());
            }

            if (this.getResponseType()== ResponseType.NAck) {
                this.setErrReasonCode(NackReasonCode.fromValue(data[index]));
            }
            return this;
        }catch(Exception e){

            Timber.e("Exception in handling ack handler" ,e);
        }
        return null;
    }

}
