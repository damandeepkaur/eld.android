package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.network.blackbox.utils.BlackBoxParser;
import com.bsmwireless.models.BlackBoxModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    public static final int TIMEOUT_RATIO = 10;

    private Socket mSocket;
    private byte mSequenceID = 1;
    private int mBoxId;
    private String mVinNumber;
    private Disposable mDisposable;
    private final AtomicReference<BlackBoxModel> mBlackBoxModel;
    private final AtomicReference<BehaviorSubject<BlackBoxModel>> mEmitter;
    private final ReentrantReadWriteLock.ReadLock readSocketLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;

    public BlackBoxImpl() {
        mBlackBoxModel = new AtomicReference<>(new BlackBoxModel());
        mEmitter = new AtomicReference<>(BehaviorSubject.create());

        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        readSocketLock = reentrantReadWriteLock.readLock();
        writeLock = reentrantReadWriteLock.writeLock();
    }

    @Override
    public void connect(int boxId) throws Exception {
        Timber.d("connect");
        if (!isConnected()) {
            mSocket = new Socket(WIFI_GATEWAY_IP, WIFI_REMOTE_PORT);
            mBoxId = boxId;
            mDisposable = Observable.interval(RETRY_CONNECT_DELAY, TimeUnit.MILLISECONDS)
                    .take(RETRY_COUNT)
                    .filter(this::initializeCommunication)
                    .firstElement()
                    .ignoreElement()
                    .andThen(startContinuousRead())
                    .timeout(UPDATE_RATE_MILLIS * TIMEOUT_RATIO,
                            TimeUnit.MILLISECONDS,
                            Observable.error(new BlackBoxConnectionException(UNKNOWN_ERROR)))
                    .subscribeOn(Schedulers.io())
                    .subscribe(model -> mEmitter.get().onNext(model),
                            throwable -> mEmitter.get().onError(throwable),
                            () -> mEmitter.get().onComplete());
        }
    }

    @Override
    public void disconnect() throws IOException {
        Timber.d("disconnect");
        mBlackBoxModel.set(new BlackBoxModel());
        if (isConnected()) {
            mDisposable.dispose();
            recreateEmitter();
            closeSocket();
        }
    }

    @Override
    public boolean isConnected() {
        readSocketLock.lock();
        boolean connected = mSocket != null && mSocket.isConnected();
        readSocketLock.unlock();
        return connected;
    }

    @Override
    public Observable<BlackBoxModel> getDataObservable() {

        if (mEmitter.get().hasComplete() || mEmitter.get().hasThrowable()) {
            recreateEmitter();
        }

        return mEmitter.get();
    }

    @Override
    public String getVinNumber() {
        return mVinNumber;
    }

    @Override
    public BlackBoxModel getBlackBoxState() {
        return mBlackBoxModel.get();
    }

    private void recreateEmitter() {
        BehaviorSubject<BlackBoxModel> old = mEmitter.get();
        old.onComplete();
        mEmitter.compareAndSet(old, BehaviorSubject.create());
    }

    private boolean initializeCommunication(long retryIndex) throws Exception {
        Timber.d("initializeCommunication");
        if (retryIndex == RETRY_COUNT - 1) {
            throw new BlackBoxConnectionException(UNKNOWN_ERROR);
        }
        writeRawData(BlackBoxParser.generateSubscriptionRequest(getSequenceID(), mBoxId, UPDATE_RATE_MILLIS));
        BlackBoxResponseModel response = readSubscriptionResponse();
        if (response.getResponseType() == BlackBoxResponseModel.ResponseType.ACK) {
            mVinNumber = response.getVinNumber();
            Timber.d("readSubscriptionResponse ok");
            return true;
        } else if (response.getResponseType() == BlackBoxResponseModel.ResponseType.NACK) {
            Timber.e("readSubscriptionResponse error");
            throw new BlackBoxConnectionException(response.getErrReasonCode());
        }
        return false;
    }

    private Observable<BlackBoxModel> startContinuousRead() {
        Timber.d("startContinuousRead");
        return Observable.interval(UPDATE_RATE_MILLIS / UPDATE_RATE_RATIO, TimeUnit.MILLISECONDS)
                .filter(unused -> isConnected())
                .map(unused -> getInputStream())
                .filter(stream -> stream.available() > START_INDEX)
                .map(this::readRawData)
                .map(bytes -> BlackBoxParser.parseVehicleStatus(bytes).getBoxData())
                .distinctUntilChanged()
                .doOnNext(model -> {
                    mBlackBoxModel.set(model);
                    Timber.v("Box Model processed:" + model.toString());
                });
    }

    public byte getSequenceID() {
        return (mSequenceID & 0xFF) > 250 ? 1 : ++mSequenceID;
    }

    private BlackBoxResponseModel readSubscriptionResponse() throws Exception {
        byte[] response;
        response = readRawData(getInputStream());
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
            System.arraycopy(buf, 0, response, START_INDEX, Math.abs(len - HEADER_LENGTH));
        }
        return response;
    }

    private void closeSocket() throws IOException {
        writeLock.lock();
        mSocket.close();
        mSocket = null;
        writeLock.unlock();
    }

    private InputStream getInputStream() throws Exception {
        readSocketLock.lock();
        if (mSocket == null) {
            throw new Exception("Socket is already closed or still not opened");
        }
        final InputStream inputStream = mSocket.getInputStream();
        readSocketLock.unlock();
        return inputStream;
    }

    private void writeRawData(byte[] request) throws IOException {
        readSocketLock.lock();
        if (mSocket == null) {
            return;
        }
        OutputStream output = mSocket.getOutputStream();
        readSocketLock.unlock();
        output.write(request);
        output.flush();
    }
}
