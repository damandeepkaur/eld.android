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

import static io.reactivex.internal.operators.observable.ObservableBlockingSubscribe.subscribe;

/**
 * Created by osminin on 10.08.2017.
 */

public final class BlackBoxImpl implements BlackBox {
    private static final String WIFI_GATEWAY_IP = "192.168.1.1";
    private static final int WIFI_REMOTE_PORT = 2880;
    private static final int RETRY_CONNECT_DELAY = 3000;
    private static final int RETRY_COUNT = 5;
    public static final int BUFFER_SIZE = 512;
    public static final int UPDATE_RATE_MILLIS = 10000;

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
            mBoxId = boxId;
            mDisposable = Observable.interval(RETRY_CONNECT_DELAY, TimeUnit.MILLISECONDS)
                    .take(RETRY_COUNT)
                    .filter(this::initializeCommunication)
                    .take(1)
                    .switchMap(unused -> startContinuousRead())
                    .doOnError(throwable -> {
                        Timber.e(throwable);
                        mEmitter.onError(throwable);
                    })
                    .doOnNext(model -> mEmitter.onNext(model))
                    .doOnComplete(() -> mEmitter.onComplete())
                    .subscribe();
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
        if (retryIndex ==  RETRY_COUNT - 1) {
            throw new BlackBoxConnectionException();
        }
        writeRawData(BlackBoxParser.generateSubscriptionRequest(getSequenceID(), mBoxId, UPDATE_RATE_MILLIS));
        return readSubscriptionResponse();
    }

    private Observable<BlackBoxModel> startContinuousRead() {
        Timber.d("startContinuousRead");
        return Observable.interval(UPDATE_RATE_MILLIS / 2, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .filter(unused -> isConnected())
                .map(unused -> mSocket.getInputStream())
                .filter(stream -> stream.available() > 0)
                .map(this::readRawData)
                .map(bytes -> BlackBoxParser.parseVehicleStatus(bytes).getBoxData())
                .doOnError(throwable -> Timber.e(throwable));
    }

    public byte getSequenceID() {
        return (mSequenceID & 0xFF) > 250 ? 1 : ++mSequenceID;
    }

    private boolean readSubscriptionResponse() throws Exception {
        byte[] response;
        response = readRawData(mSocket.getInputStream());
        if (response != null) {
            BlackBoxResponseModel responseModel = BlackBoxParser.parseSubscription(response);
            if (responseModel.getResponseType() == BlackBoxResponseModel.ResponseType.Ack) {
                Timber.d("readSubscriptionResponse ok");
                return true;
            } else {
                Timber.e("readSubscriptionResponse error");
                return false;
            }
        }
        return false;
    }

    private byte[] readRawData(InputStream input) throws Exception {
        int total = 0;
        byte[] response = new byte[BUFFER_SIZE];
        int available = input.available();
        while (available > 0) {
            byte[] buf = new byte[available];
            int len = input.read(buf, 0, available);
            System.arraycopy(buf, 0, response, total, len);
            total += len;
            available = input.available();
        }
        return response;
    }

    private void writeRawData(byte[] request) throws IOException {
        OutputStream output = mSocket.getOutputStream();
        output.write(request);
        output.flush();
    }
}
