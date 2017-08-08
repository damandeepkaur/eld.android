package com.bsmwireless.data.network.connection;



import android.support.annotation.Nullable;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.connection.device.TelematicDevice;
import com.bsmwireless.data.network.connection.request.SubscriptionGenerator;
import com.bsmwireless.data.network.connection.response.AckResponseProcessor;
import com.bsmwireless.data.network.connection.response.ResponseProcessor;
import com.bsmwireless.data.network.connection.response.VehicleStatusProcessor;
import com.bsmwireless.models.Vehicle;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;



public class ConnectionManager implements ConnectionInterface {
    private TelematicDevice mTelematicDevice;
    private Vehicle mVehicle;
    private ConnectionManagerService mConnectionService;
    private byte mSequenceID = 1;
    final int RETRY_CONNECT_DELAY =5000;
    final int READING_DELAY=1000;
    final int READING_TIMEOUT=5000;
    final int STATUS_TIMEOUT = Constants.STATUS_UPDATE_RATE_SECS*1000*2; // Try two times
    // Response are of max size of 50 bytes. Even if the messages are sent together , below limit seems to be safer.
    final int BUFFER_SIZE=512;
    private boolean vehicleConnected = false;

    public enum ConnectionStatus {Ready,  Subscribing, Paired, Disconnected }

    volatile ConnectionStatus mConnectionstatus ;



    private final BehaviorSubject<ConnectionStatus> connectionState = BehaviorSubject.create();

    public ConnectionManager(TelematicDevice telematicDevice) {
        mTelematicDevice = telematicDevice;
    }

    public BehaviorSubject<ConnectionStatus> getConnectionStateObservable() {
        return connectionState;
    }

    public synchronized void setConnectionstatus(ConnectionStatus connectionstatus) {
        mConnectionstatus = connectionstatus;
        connectionState.onNext(mConnectionstatus);
    }
    public synchronized ConnectionStatus getConnectionstatus() {
        return mConnectionstatus;
    }

    @Override
    public void setDevice(TelematicDevice telematicDevice) {
        mTelematicDevice = telematicDevice;
    }


    public byte getmSequenceID() {
        mSequenceID++;
        if ((mSequenceID & 0xFF )> 250)
            mSequenceID = 1;

        return mSequenceID;
    }

    @Override
    public void connect(Vehicle vehicle) {

        mVehicle =vehicle;
        // get the device/vehicle and move it to the ready state
        if (mTelematicDevice == null || mVehicle == null) return;
        vehicleConnected = true;
        startConnectionService();
        setConnectionstatus(ConnectionStatus.Ready);
        // start a separate thread for the communication process
        try {
            new StartConnectionTask().start();
        }
        catch (Exception ex)
        {
            Timber.e(ex);
        }
    }

    @Override
    public void disconnect() {
        vehicleConnected = false;
        setConnectionstatus(ConnectionStatus.Disconnected);
        mTelematicDevice.disconnect();
        stopConnectionService();
    }

    @Override
    public boolean isConnected()
    {
        return mConnectionstatus == ConnectionStatus.Paired;
    }


    /*
     * Status will be disconnected only on logout.
     * No need to reconnect after reaching the disconnected state.
     * No need to change status, if current status is equal to the previous status. This may trigger the same status to the listener
     */
    private void resetStatus(ConnectionStatus connectionStatus) {
        if (getConnectionstatus() != connectionStatus && getConnectionstatus() != ConnectionStatus.Disconnected) {
          setConnectionstatus(connectionStatus);
        }
    }
    private class StartConnectionTask extends Thread{
        public void run() {
            while(vehicleConnected) {
                if (getConnectionstatus() == ConnectionStatus.Ready) {
                    try {
                        mTelematicDevice.connect();
                        if (mTelematicDevice.isConnected()) {
                            resetStatus(ConnectionStatus.Subscribing);
                            initializeCommunication();

                        }
                        Thread.sleep(RETRY_CONNECT_DELAY); // retry after a wait period
                    } catch (Exception ex) {
                        Timber.e(ex);
                    }
                }
            }
        }
    }


    private void initializeCommunication()
    {
        try {
            while (getConnectionstatus() == ConnectionStatus.Subscribing) {
                boolean sentRequest;
                SubscriptionGenerator subscriptionRequest = new SubscriptionGenerator();
                sentRequest = sendRequest(subscriptionRequest.generateRequest(getmSequenceID(), mVehicle.getBoxId()));
                if (sentRequest) {
                    readSubscriptionResponse();
                }

                while (getConnectionstatus() == ConnectionStatus.Paired) {
                    readStatusResponse();
                }
            }
        }catch(Exception e){
            Timber.e ("Exception in initializeCommunication : "  ,e   );
        }
        // On any exception or unknown status retry
        resetStatus(ConnectionStatus.Ready);
    }

    /*
     * Write the byte array to the output stream
     * returns true, on successful write operation returns.
     * returns false, on exception
     */
    private boolean sendRequest(byte[] request)
    {
        try {
            OutputStream output = mTelematicDevice.getOutputStream();
            output.write(request);
            output.flush();
            return true;
        }
        catch (IOException e)
        {
            Timber.e("Error in Send  Request:" , e);
            resetStatus(ConnectionStatus.Ready);

        }

        return false;
    }

    /*
     * Read the response from the inputstream
     * process the bytes for the status response
     * on timeout sent the subscription request by setting the status to subscribing
     */
    private void readStatusResponse()
    {
        try {
                byte[] response = readResponse(STATUS_TIMEOUT);

                if (response != null) {
                    processStatusResponse(response);
                } else { // response null , timed out
                    resetStatus(ConnectionStatus.Subscribing);
                }

        }catch(Exception e){ // exception from reading , restart the communication
            resetStatus(ConnectionStatus.Ready);
        }
    }
    /*
    * Read the response from the inputstream
    * process the bytes for the subscription response
    * on timeout sent the subscription request by setting the status to subscribing
    */
    private  void readSubscriptionResponse() {
        byte[] response;
        try {
            response = readResponse( READING_TIMEOUT);
            if (response != null ) {
                processSubscriptionResponse(response);
            }
            else { // response null , timed out
                resetStatus(ConnectionStatus.Subscribing);
            }
        }catch(Exception e){ // on error restart the connection
            Timber.e("Exception in reading subscription response:" , e);
            resetStatus(ConnectionStatus.Ready);
        }

    }

    /*
     * read bytes from the input stream
     * if nothing available to read, wait for some time and read again
     * repeat this process until successful reading or timeout
     * on timeout return null response
     */
    @Nullable
    private byte[] readResponse(long timeout) throws Exception {
        long elapsed = 0;
        int total = 0;
        while (total <= 0 && elapsed < timeout) {
            InputStream input = mTelematicDevice.getInputStream();
            byte[] response = new byte[BUFFER_SIZE];
            int available = input.available();
            while (available > 0) {
                byte[] buf = new byte[available];
                int len = input.read(buf, 0, available);
                System.arraycopy(buf, 0, response, total, len);
                total += len;
                available = input.available();
            }

            if (total >0)
                return response;
            Thread.sleep(READING_DELAY); // wait to read again

            elapsed += READING_DELAY;
        }
        return null;
    }


    private void processStatusResponse(byte[] response)
    {
        //Handle the vehicle state responses from the box
        VehicleStatusProcessor vehicleResponse =  new VehicleStatusProcessor();
        vehicleResponse.parse(response);
        if (vehicleResponse.getBoxData().getBoxId() != mVehicle.getBoxId()){
            resetStatus(ConnectionStatus.Ready);
        }


    }
    private void processSubscriptionResponse(byte[] response)
    {
        //Handle the packet id comparision and the ack or nack
        AckResponseProcessor ackResponse= new AckResponseProcessor();
        ackResponse.parse(response);
        if (ackResponse.getResponseType() == ResponseProcessor.ResponseType.Ack)
            resetStatus(ConnectionStatus.Paired);
        else {
            resetStatus(ConnectionStatus.Ready);
        }
    }

    /*
     * start a service
     */
    private void startConnectionService()
    {
        if (mConnectionService == null)
            mConnectionService =new ConnectionManagerService();
        mConnectionService.startConnectionService();
    }
   /*
   * start a service
   */
    private void stopConnectionService()
    {
        if (mConnectionService != null)
            mConnectionService.stopConnectionService();
    }


}
