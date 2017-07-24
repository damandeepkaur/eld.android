package com.bsmwireless.data.network.Connection;

import com.bsmwireless.data.network.Connection.Request.VehicleStatusRequestGenerator;
import com.bsmwireless.data.network.Connection.Response.AckResponseProcessor;
import com.bsmwireless.data.network.Connection.Response.ECMDataResponseProcessor;
import com.bsmwireless.data.network.Connection.Response.ResponseProcessor;
import com.bsmwireless.models.Vehicle;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Created by hsudhagar on 2017-07-17.
 */

public class ConnectionManager implements ConnectionInterface {



    private Device device;
    private Vehicle mVehicle;
    byte sequenceID = 1;


    public enum ConnectionState{Ready, Connecting, Subscribing,WaitingOnSubscribing, Paired, Disconnected }



    ConnectionState mConnectionstatus = ConnectionState.Ready;


    private final BehaviorSubject<ConnectionState> connectionState= BehaviorSubject.create();

    public ConnectionManager() {


    }

    public BehaviorSubject<ConnectionState> getConnectionStateObservable() {
        return connectionState;
    }



    public void setConnectionstatus(ConnectionState Connectionstatus) {
        mConnectionstatus = Connectionstatus;
        connectionState.onNext(mConnectionstatus);
    }

    @Override
    public void connect(Vehicle vehicle) {

        mVehicle= vehicle;
        device = new Device();
        setConnectionstatus(ConnectionState.Ready);

        try {

            // Start a new  connection
            new StartConnectionTask().start();


        }
        catch (Exception ex)
        {
            Timber.e(ex);
        }


    }



    @Override
    public void disconnect() {
        device.disconnect();

        setConnectionstatus(ConnectionState.Disconnected);
    }
    @Override
    public boolean isConnected()
    {
        if (mConnectionstatus == ConnectionState.Paired)
            return true;
        return false;
    }

    public  boolean sendRequest(byte[] request)
    {
        try {

                if (device.isConnected()) {

                    OutputStream output = device.getOutputStream();
                    output.write(request);
                    output.flush();
                    return true;
                }
                else
                    setConnectionstatus(ConnectionState.Ready);

        }
        catch (Exception e)
        {

            Timber.e("Error in Send  Request:" , e);

        }

        return false;
    }

    public  void readStatusResponse()
    {

        byte[] response = readResponse();
        if (response!=null && getResponseLength(response) > 0) {
            processStatusResponse(response);
        }
    }
    public  void readSubscriptionResponse()
    {
        boolean readSubscription = false;
        // Try maximum of 4 times
        int numTries =0;
        while(!readSubscription) {

            Timber.i("Reading subscription response");
            byte[] response = readResponse();
            if (response == null || numTries>4) {

                setConnectionstatus(ConnectionState.Connecting);
                break;
            }
            else if (response!=null && getResponseLength(response) > 0){
                readSubscription = true;
                processSubscriptionResponse(response);
            }
            numTries++;
        }


    }

    public int getResponseLength(byte[] response)
    {
        int res = ConnectionUtils.byteToUnsignedInt(response[9], response[10]);
        return res;
    }
    public byte[] readResponse()
    {

        try {

            if (device.isConnected() && device.getInputStream()!=null) {
                InputStream input = device.getInputStream();
                byte[] response = new byte[1544];
                int total = 0;
                int available = input!=null?input.available():0;
                while (input.available() >0) {

                    byte[] buf = new byte[input.available()];
                    int len = input.read(buf, 0, input.available());
                    System.arraycopy(buf, 0, response, total, len);
                    total += len;

                }
                return response;


            }
            else
            { if (device.isConnected())
                setConnectionstatus(ConnectionState.Ready);}
        }
        catch (Exception e)
        {
            Timber.e("Error in reading response:" , e);
        }
        return null;
    }


    public void processStatusResponse(byte[] response)
    {
        //Handle the vehicle state responses from the box - old protocol

        ECMDataResponseProcessor vehicleResponse =  new ECMDataResponseProcessor();
        vehicleResponse.parse(response);
        if (vehicleResponse.getBoxData().getBoxId() != mVehicle.getBoxId()){
            disconnect();
        }


    }
    public void processSubscriptionResponse(byte[] response)
    {
        //Handle the packet id comparision and the ack or nack
        AckResponseProcessor ackResponse= new AckResponseProcessor();
        ackResponse.parse(response);
        if (ackResponse.getResponseType() == ResponseProcessor.ResponseType.Ack)
            setConnectionstatus(ConnectionState.Paired);
        else {
            disconnect();
        }
    }


    private class StartConnectionTask extends Thread{
        public void run(){

            try {
                while(mConnectionstatus == ConnectionState.Ready) {
                    device.connect();
                    if (device.isConnected()) {
                        setConnectionstatus(ConnectionState.Connecting);
                        initializeCommunication();
                        while (mConnectionstatus == ConnectionState.Paired) {
                            readStatusResponse();
                        }
                    }
                }

            }
            catch (Exception ex)
            {
                Timber.e(ex);
            }

        }
    }

    private void initializeCommunication()
    {
        while(mConnectionstatus == ConnectionState.Connecting) {
            boolean sentRequest = false;
            setConnectionstatus(ConnectionState.Subscribing);
            if (mVehicle != null) {
                VehicleStatusRequestGenerator statusRequest = new VehicleStatusRequestGenerator();
                sentRequest = sendRequest(statusRequest.generateRequest(sequenceID,mVehicle.getBoxId()));
            }

            if (sentRequest) {
                setConnectionstatus(ConnectionState.WaitingOnSubscribing);
                readSubscriptionResponse();
            }
        }

    }

}
