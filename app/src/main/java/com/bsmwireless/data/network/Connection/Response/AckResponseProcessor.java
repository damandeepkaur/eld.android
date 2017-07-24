package com.bsmwireless.data.network.Connection.Response;

import timber.log.Timber;

/**
 * Created by hsudhagar on 2017-07-20.
 */

public class AckResponseProcessor extends ResponseProcessor {



    public ResponseProcessor parse(byte[] data)
    {
        try {

            if (!parseHeader(data)) return null;
            int index = 11;
            byte msg = data[11];
            this.setResponseType( ResponseType.valueOf((char) msg));
            index++;
            this.setSequenceId( data[12] & 0x0FF);
            index++;


            // on Ack - parse for the VIN , available from the subscription ack message
            // is length 31 excluding the header
            if (this.getResponseType()==ResponseType.Ack  && this.getLength() ==31) {
                this.setVinNumber(new String(data, index,24, "ASCII"));// from 13 to 36
                Timber.i("VIN number from the box" + this.getVinNumber());
            }

            if (this.getResponseType()== ResponseType.NAck)
            {
                this.setErrReasonCode(NackReasonCode.fromValue(data[13]));
            }
            return this;
        }catch(Exception e){

            Timber.e("Exception in handling ack handler" ,e);
        }
        return null;
    }

}
