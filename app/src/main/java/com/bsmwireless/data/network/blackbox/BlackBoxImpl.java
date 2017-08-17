package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.network.blackbox.utils.BlackBoxParser;
import com.bsmwireless.models.BlackBoxModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

import static com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel.NackReasonCode.UNKNOWN_ERROR;
import static com.bsmwireless.data.network.blackbox.utils.BlackBoxParser.HEADER_LENGTH;
import static com.bsmwireless.data.network.blackbox.utils.BlackBoxParser.START_INDEX;

public final class BlackBoxImpl implements BlackBox {
    private static final String WIFI_GATEWAY_IP = "192.168.1.1";
    private static final int WIFI_REMOTE_PORT = 2880;
    private static final int RETRY_CONNECT_DELAY = 3000;
    private static final int RETRY_COUNT = 5;
    public static final int BUFFER_SIZE = 64;
    public static final int UPDATE_RATE_MILLIS = 10000;
    public static final int UPDATE_RATE_RATIO = 10;
    public static final int TIMEOUT_RATIO = 5;

    private Socket mSocket;
    private byte mSequenceID = 1;
    private BehaviorSubject<BlackBoxModel> mEmitter;
    private int mBoxId;
    private Disposable mDisposable;

    @Override
    public void connect(int boxId) throws Exception {
        Timber.d("connect");
        if (!isConnected()) {
            mSocket = new Socket(WIFI_GATEWAY_IP, WIFI_REMOTE_PORT);
            mEmitter = BehaviorSubject.create();
            mBoxId = /*boxId;*/ 209926;
            mDisposable = Observable.interval(RETRY_CONNECT_DELAY, TimeUnit.MILLISECONDS)
                    .take(RETRY_COUNT)
                    .filter(this::initializeCommunication)
                    .take(1)
                    .switchMap(unused -> startContinuousRead())
                    .timeout(UPDATE_RATE_MILLIS * TIMEOUT_RATIO,
                            TimeUnit.MILLISECONDS,
                            Observable.error(new BlackBoxConnectionException(UNKNOWN_ERROR)))
                    .doOnError(throwable -> {
                        Timber.e(throwable);
                        disconnect();
                    })
                    .subscribe(model -> mEmitter.onNext(model),
                            throwable -> mEmitter.onError(throwable),
                            () -> mEmitter.onComplete());
        }
    }

    @Override
    public void disconnect() throws IOException {
        Timber.d("disconnect");
        if (isConnected()) {
            mDisposable.dispose();
            mEmitter = null;
            mSocket.close();
            mSocket = null;
        }
    }

    @Override
    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    @Override
    public Observable<BlackBoxModel> getDataObservable() {
        return mEmitter;
    }

    private boolean initializeCommunication(long retryIndex) throws Exception {
        Timber.d("initializeCommunication");
        if (retryIndex == RETRY_COUNT - 1) {
            throw new BlackBoxConnectionException(UNKNOWN_ERROR);
        }
        writeRawData(BlackBoxParser.generateSubscriptionRequest(getSequenceID(), mBoxId, UPDATE_RATE_MILLIS));
        BlackBoxResponseModel response = readSubscriptionResponse();
        if (response.getResponseType() == BlackBoxResponseModel.ResponseType.ACK) {
            Timber.d("readSubscriptionResponse ok");
            return true;
        } else if (response.getResponseType() == BlackBoxResponseModel.ResponseType.NACK){
            Timber.e("readSubscriptionResponse error");
            throw new BlackBoxConnectionException(response.getErrReasonCode());
        }
        return false;
    }

    private Observable<BlackBoxModel> startContinuousRead() {
        Timber.d("startContinuousRead");
        return Observable.interval(UPDATE_RATE_MILLIS / UPDATE_RATE_RATIO, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .filter(unused -> isConnected())
                .map(unused -> mSocket.getInputStream())
                .filter(stream -> stream.available() > START_INDEX)
                .map(this::readRawData)
                .map(bytes -> BlackBoxParser.parseVehicleStatus(bytes).getBoxData())
                .distinctUntilChanged()
                .doOnNext(model -> {
                    Timber.v("Box Model processed:" + model.toString());
                });
    }

    public byte getSequenceID() {
        return (mSequenceID & 0xFF) > 250 ? 1 : ++mSequenceID;
    }

    private BlackBoxResponseModel readSubscriptionResponse() throws Exception {
        byte[] response;
        response = readRawData(mSocket.getInputStream());
        BlackBoxResponseModel responseModel = null;
        if (response != null) {
            responseModel = BlackBoxParser.parseSubscription(response);
        }
        return responseModel;
    }

    private byte[] readRawData(InputStream input) throws Exception {
        byte[] response = new byte[BUFFER_SIZE];
        byte[] buf = new byte[START_INDEX];
        int len = input.read(buf, 0, START_INDEX);
        System.arraycopy(buf, 0, response, 0, len);
        BlackBoxResponseModel model = new BlackBoxResponseModel();
        if (BlackBoxParser.parseHeader(response, model)) {
            int packetLength = model.getLength();
            buf = new byte[packetLength - START_INDEX + HEADER_LENGTH];
            len = input.read(buf, 0, packetLength - START_INDEX + HEADER_LENGTH);
            System.arraycopy(buf, 0, response, START_INDEX, len - HEADER_LENGTH);
        }
        return response;
    }

    private void writeRawData(byte[] request) throws IOException {
        OutputStream output = mSocket.getOutputStream();
        output.write(request);
        output.flush();
    }
}
