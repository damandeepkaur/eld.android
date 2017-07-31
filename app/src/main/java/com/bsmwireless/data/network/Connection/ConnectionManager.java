package com.bsmwireless.data.network.connection;

import com.bsmwireless.data.network.connection.device.Device;
import com.bsmwireless.data.network.connection.request.ECMStatusGenerator;
import com.bsmwireless.data.network.connection.request.SubscriptionGenerator;
import com.bsmwireless.data.network.connection.response.AckResponseProcessor;
import com.bsmwireless.data.network.connection.response.ECMDataProcessor;
import com.bsmwireless.data.network.connection.response.ResponseProcessor;
import com.bsmwireless.models.Vehicle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;



public class ConnectionManager implements ConnectionInterface {




    private Device mDevice;
    private Vehicle mVehicle;
    private byte sequenceID = 1;
    private static int RETRY_DELAY=1000;
    private static int READING_DELAY=1000;
    private static int READING_TIMEOUT=5000;

    public enum ConnectionStatus {Ready, Connecting, Subscribing, Paired, Disconnected }


    static ConnectionStatus DefaultInitialStatus = ConnectionStatus.Ready;
    // State mCurrentState;
    volatile ConnectionStatus mConnectionstatus ;


    private final BehaviorSubject<ConnectionStatus> connectionState = BehaviorSubject.create();

    public ConnectionManager(Device device) {
        mDevice= device;
    }

    public BehaviorSubject<ConnectionStatus> getConnectionStateObservable() {
        return connectionState;
    }


/*
    public Observable<ConnectionStatus> getConnectionStatusObservable(Vehicle vehicle){
        Observable<ConnectionStatus> stateObservable = Observable.create(new ObservableOnSubscribe<ConnectionStatus>(){

            @Override
            public void subscribe(@NonNull ObservableEmitter<ConnectionStatus> e) throws Exception {
                setVehicle(vehicle);
                if (mDevice == null || mVehicle == null) return;
                setConnectionstatus(ConnectionStatus.Ready);
                e.onNext(mConnectionstatus);
                while(mConnectionstatus == ConnectionStatus.Ready) {
                    mDevice.connect();
                    if (mDevice.isConnected()) {
                        setConnectionstatus(ConnectionStatus.Connecting);
                        e.onNext(mConnectionstatus);
                        initializeCommunication();
                        while (mConnectionstatus == ConnectionStatus.Paired) {
                            readStatusResponse();
                        }
                    }
                    Thread.sleep(RETRY_DELAY); // retry every 1 seconds
                }
            }
        });

        return stateObservable;
    }*/

    public void setConnectionstatus(ConnectionStatus Connectionstatus) {
        mConnectionstatus = Connectionstatus;
        connectionState.onNext(mConnectionstatus);

    }
   /* public void setCurrentState(State ConnectionState) {
        mCurrentState = ConnectionState;
        setConnectionstatus(mCurrentState.getConnectionStatus());
    }
*/

    @Override
    public void setDevice(Device device) {
        mDevice = device;
    }
    public Device getDevice() {
        return mDevice;
    }

    public void setVehicle(Vehicle vehicle)
    {
        mVehicle= vehicle;
    }

    public byte getSequenceID() {
        sequenceID++;
        if (sequenceID > 250)
            sequenceID = 1;

        return sequenceID;
    }

    @Override
    public void connect(Vehicle vehicle) {

        mVehicle =vehicle;
        // get the device/vehicle and move it to the ready state
        // mDevice = new WiFiDevice();
        if (mDevice == null || mVehicle == null) return;
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
        setConnectionstatus(ConnectionStatus.Disconnected);
        mDevice.disconnect();

    }

    @Override
    public boolean isConnected()
    {
        return mConnectionstatus == ConnectionStatus.Paired;
    }


    private class StartConnectionTask extends Thread{
        public void run() {
            try {
                while(mConnectionstatus == ConnectionStatus.Ready) {
                    mDevice.connect();
                    if (mDevice.isConnected()) {
                     /*   mDevice.getReadResponseObservable()
                                .observeOn(Schedulers.computation())
                                .subscribeOn(Schedulers.io())
                                .subscribe(response->{
                                    processResponse(response);
                                }, error ->{
                                    Timber.i("Error in reading the response");
                                        }
                                        );*/
                        setConnectionstatus(ConnectionStatus.Connecting);
                        initializeCommunication();
                        while (mConnectionstatus == ConnectionStatus.Paired) {
                            readStatusResponse();
                        }
                    }
                    Thread.sleep(RETRY_DELAY); // retry every 1 seconds
                }
            }
            catch (Exception ex)
            {
                Timber.e(ex);
            }

        }
    }

    private void processResponse(byte[] response) {

        if (response!=null){
            byte msgByte = response[11];
            switch ((char)msgByte){
                case 'A'://TODO: to test with legacy, need to be removed
                case 'a':
                    processSubscriptionResponse(response);
                    break;
                case 'S':
                case 'D':
                case 'I':
                case 'i':
                case 'G':
                case 'g':
                case 'E':
                    processStatusResponse(response);
                    break;
            }

        }
    }

    private void initializeCommunication()
    {
        while(mConnectionstatus == ConnectionStatus.Connecting) {
            boolean sentRequest = false;
            setConnectionstatus(ConnectionStatus.Subscribing);
            if (mVehicle != null) {
                SubscriptionGenerator subscriptionRequest = new SubscriptionGenerator();
                //ECMStatusGenerator statusRequest = new ECMStatusGenerator();
                //VehicleStatusGenerator statusRequest = new VehicleStatusGenerator();
                sentRequest = sendRequest(subscriptionRequest.generateRequest(getSequenceID(),mVehicle.getBoxId()));
            }

            if (sentRequest) {
                readSubscriptionResponse();
            }
        }

    }

    private void newInitializeCommunication()
    {
        while(mConnectionstatus == ConnectionStatus.Connecting) {
            boolean sentRequest = false;
            setConnectionstatus(ConnectionStatus.Subscribing);
            if (mVehicle != null) {
                //SubscriptionGenerator subscriptionRequest = new SubscriptionGenerator();
                ECMStatusGenerator statusRequest = new ECMStatusGenerator();
                //VehicleStatusGenerator statusRequest = new VehicleStatusGenerator();
                sentRequest = sendRequest(statusRequest.generateRequest(getSequenceID(),mVehicle.getBoxId()));
            }

         /*   if (sentRequest) {
                readSubscriptionResponse();
            }*/
        }

    }
    public  boolean sendRequest(byte[] request)
    {
        try {

            if (mDevice.isConnected()) {

                OutputStream output = mDevice.getOutputStream();
                output.write(request);
                output.flush();
                return true;
            }
        }
        catch (IOException e)
        {

            Timber.e("Error in Send  Request:" , e);

        }
        if (mConnectionstatus != ConnectionStatus.Disconnected)
            setConnectionstatus(ConnectionStatus.Ready);
        return false;
    }

    public  void readStatusResponse()
    {
        try {
            byte[] response = readResponse();
            if (response != null) {
                processStatusResponse(response);
            }
            else
                setConnectionstatus(ConnectionStatus.Connecting);

        }catch(Exception e){
            Timber.i("Exception in reading status response:" , e);
            if (mConnectionstatus != ConnectionStatus.Disconnected)
                setConnectionstatus(ConnectionStatus.Ready);
        }
    }
    public  void readSubscriptionResponse() {
        boolean readSubscription = false;
        long elapsed = 0;
        byte[] response = null;
        try {

            Timber.i("Reading subscription response");
            response = readResponse();
            Timber.i("Processing subscription response");
            if (response != null ) {
                processSubscriptionResponse(response);
            }
            else
                setConnectionstatus(ConnectionStatus.Connecting);
        }catch(Exception e){
            Timber.i("Exception in reading subscription response:" , e);
            if (mConnectionstatus != ConnectionStatus.Disconnected)
                setConnectionstatus(ConnectionStatus.Ready);
        }

    }

/*
    public Observable<byte[]> getReadResponseObservable() {
        Observable<byte[]> result = Observable.create(new ObservableOnSubscribe<byte[]>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<byte[]> e) throws Exception {
                while(isConnectionAlive()) {
                    e.onNext(readResponse());
                }

                e.onError(new RuntimeException("no connection"));
            }
        });


        return result;
    }
*/



    public boolean isConnectionAlive() {
        return true; // BAD!!!!!!
    }


    public byte[] readResponse() throws Exception {
        long elapsed = 0;
        int total = 0;
        while (total <= 0 && elapsed < READING_TIMEOUT) {
            Timber.i("Reading  response");
            if (mConnectionstatus != ConnectionStatus.Disconnected) {
                InputStream input = mDevice.getInputStream();
                byte[] response = new byte[1544];
                int available = input != null ? input.available() : 0;
                while (available > 0) {
                    byte[] buf = new byte[available];
                    int len = input.read(buf, 0, available);
                    System.arraycopy(buf, 0, response, total, len);
                    total += len;
                    available = input != null ? input.available() : 0;
                }

                if (total >0)
                    return response;
                try {
                    Thread.sleep(READING_DELAY); // wait to read again
                } catch (InterruptedException ex) {
                    Timber.e("Error in reading response:", ex);
                }
                elapsed += READING_DELAY;
            }

        }
        return null;
    }


    public void processStatusResponse(byte[] response)
    {
        //Handle the vehicle state responses from the box - old protocol
        ECMDataProcessor vehicleResponse =  new ECMDataProcessor();
        vehicleResponse.parse(response);
        if (vehicleResponse.getBoxData().getBoxId() != mVehicle.getBoxId()){
            setConnectionstatus(ConnectionStatus.Ready);
        }


    }
    public void processSubscriptionResponse(byte[] response)
    {
        //Handle the packet id comparision and the ack or nack
        AckResponseProcessor ackResponse= new AckResponseProcessor();
        ackResponse.parse(response);
        if (ackResponse!=null && ackResponse.getResponseType() == ResponseProcessor.ResponseType.Ack)
            setConnectionstatus(ConnectionStatus.Paired);
        else {
            setConnectionstatus(ConnectionStatus.Ready);
        }
    }


  /*  public class ReadyState implements State{

        ConnectionStatus  connectionType= ConnectionStatus.Ready;

        @Override
        public ConnectionStatus getConnectionStatus() {
            return connectionType;
        }

        @Override
        public void process(ConnectionManager connectionMgr) {

            boolean result =connectionMgr.getDevice().connect();
            if (result)
                this.onSuccess(connectionMgr);
            this.onFailure(connectionMgr);
        }

        @Override
        public void onSuccess(ConnectionManager connectionMgr) {
            connectionMgr.setCurrentState(new SubscribingState());
        }

        @Override
        public void onFailure(ConnectionManager connectionMgr) {


        }
    }
    public class ConnectingState implements State{
        int MaxCount=4;
        int retryCount=0;
        ConnectionStatus  connectionType= ConnectionStatus.Connecting;

        public ConnectingState(ConnectionManager connectionMgr)
        {
           process(connectionMgr);
        }
        @Override
        public ConnectionStatus getConnectionStatus() {
            return connectionType;
        }

        @Override
        public void process(ConnectionManager connectionMgr) {


            boolean result =connectionMgr.getDevice().connect();
            if (result) //On successful connection
                this.onSuccess(connectionMgr);
            this.onFailure(connectionMgr);
        }

        @Override
        public void onSuccess(ConnectionManager connectionMgr) {
            connectionMgr.setCurrentState(new SubscribingState(connectionMgr));
        }

        @Override
        public void onFailure(ConnectionManager connectionMgr) {

            process(connectionMgr); // retry sending
            if (++retryCount > MaxCount) // after maximum tries change to disconnect
            {
                connectionMgr.setCurrentState(new DisconnectedState(connectionMgr));
            }


        }
    }

    public class SubscribingState implements State{

        ConnectionStatus  connectionType= ConnectionStatus.Subscribing;

        public SubscribingState(ConnectionManager connectionMgr)
        {
            process(connectionMgr);
        }

        @Override
        public ConnectionStatus getConnectionStatus() {
            Timber.i("Subscribing state");
            return connectionType;
        }

        @Override
        public void process(ConnectionManager connectionMgr) {


            if (mVehicle!=null) {
                VehicleStatusGenerator statusRequest = new VehicleStatusGenerator();
                boolean sentResult = sendRequest(statusRequest.generateRequest(sequenceID, mVehicle.getBoxId()));
                if (!sentResult)
                    this.onFailure(connectionMgr);

                boolean readResult = readSubscriptionResponse();
                if (!readResult)
                    this.onFailure(connectionMgr);

                this.onSuccess(connectionMgr);
            }
        }


        public  boolean readSubscriptionResponse() {
            boolean readSubscription = false;
            // Try maximum of 4 times
            int numTries = 0;
            while (!readSubscription) {

                Timber.i("Reading subscription response");
                byte[] response = readResponse();
                if (response == null || numTries > 4) {
                    readSubscription =false;
                    break;
                } else if (response != null && getResponseLength(response) > 0) {
                    readSubscription = true;
                    processSubscriptionResponse(response);
                }
                numTries++;
            }

            return readSubscription;
        }
        @Override
        public void onSuccess(ConnectionManager connectionMgr) {
            connectionMgr.setCurrentState(new PairedState(connectionMgr));
        }

        @Override
        public void onFailure(ConnectionManager connectionMgr) {
            connectionMgr.setCurrentState(new ConnectingState(connectionMgr));
        }
    }
    public class PairedState implements State{

        ConnectionStatus  connectionType= ConnectionStatus.Paired;

        public PairedState(ConnectionManager connectionMgr)
        {
            process(connectionMgr);
        }
        @Override
        public ConnectionStatus getConnectionStatus() {
            return connectionType;
        }

        @Override
        public void process(ConnectionManager connectionMgr) {

            ReadThread readThread = new ReadThread();
            readThread.start();
        }

        @Override
        public void onSuccess(ConnectionManager connectionMgr) {

        }

        @Override
        public void onFailure(ConnectionManager connectionMgr) {


        }

        private class ReadThread extends Thread{
            public void run(){

                try {
                    while (mDevice.isConnected())
                        if (mDevice.isConnected() && mDevice.getInputStream()!=null) {
                            InputStream input = mDevice.getInputStream();
                            byte[] response = new byte[1544];
                            int total = 0;
                            int available = input!=null?input.available():0;
                            while (input.available() >0) {

                                byte[] buf = new byte[input.available()];
                                int len = input.read(buf, 0, input.available());
                                System.arraycopy(buf, 0, response, total, len);
                                total += len;

                            }
                            if(response!=null && response.length >0)
                                processStatusResponse(response);


                        }
                        else
                        { if (mDevice.isConnected())
                            setConnectionstatus(ConnectionStatus.Ready);}

                }
                catch (Exception ex)
                {
                    Timber.e(ex);
                }

            }
        }
    }
    public class DisconnectedState implements State{

        ConnectionStatus  connectionType= ConnectionStatus.Disconnected;

        public DisconnectedState(ConnectionManager connectionMgr)
        {
            process(connectionMgr);
        }
        @Override
        public ConnectionStatus getConnectionStatus() {
            return connectionType;
        }

        @Override
        public void process(ConnectionManager connectionMgr) {

            connectionMgr.getDevice().disconnect();

        }

        @Override
        public void onSuccess(ConnectionManager connectionMgr) {

        }

        @Override
        public void onFailure(ConnectionManager connectionMgr) {


        }
    }
    public abstract class  ConnectionState implements State{

        public void process(ConnectionManager connectionMgr) {}

        public void onSuccess(ConnectionManager connectionMgr) {  }


        public void onFailure() { }
    }
    public interface State{
        ConnectionStatus getConnectionStatus();
        void process(ConnectionManager connectionMgr);
        void onSuccess(ConnectionManager connectionMgr);
        void onFailure(ConnectionManager connectionMgr);

    }*/

}
