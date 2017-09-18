package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;

import java.io.IOException;

public final class BlackBoxConnectionException extends RuntimeException {

    final BlackBoxResponseModel.NackReasonCode mReasonCode;

    public BlackBoxConnectionException(BlackBoxResponseModel.NackReasonCode reasonCode) {
        mReasonCode = reasonCode;
    }

    public BlackBoxResponseModel.NackReasonCode getReasonCode() {
        return mReasonCode;
    }

    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        throw new IOException("Not defined");
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        throw new IOException("Not defined");
    }
}
