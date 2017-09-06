package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;

public final class BlackBoxConnectionException extends RuntimeException {

    final BlackBoxResponseModel.NackReasonCode mReasonCode;

    public BlackBoxConnectionException(BlackBoxResponseModel.NackReasonCode reasonCode) {
        mReasonCode = reasonCode;
    }

    public BlackBoxResponseModel.NackReasonCode getReasonCode() {
        return mReasonCode;
    }
}
