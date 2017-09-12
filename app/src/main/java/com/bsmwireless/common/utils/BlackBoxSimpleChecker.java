package com.bsmwireless.common.utils;

import android.support.annotation.NonNull;

import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.models.BlackBoxModel;

public class BlackBoxSimpleChecker implements BlackBoxStateChecker {
    @Override
    public boolean isMoving(@NonNull BlackBoxModel blackBoxModel) {
        return BlackBoxResponseModel.ResponseType.MOVING == blackBoxModel.getResponseType();
    }

    @Override
    public boolean isStopped(@NonNull BlackBoxModel blackBoxModel) {
        return BlackBoxResponseModel.ResponseType.STOPPED == blackBoxModel.getResponseType();
    }

    @Override
    public boolean isIgnitionOn(@NonNull BlackBoxModel blackBoxModel) {
        return BlackBoxResponseModel.ResponseType.IGNITION_ON == blackBoxModel.getResponseType();
    }

    @Override
    public boolean isIgnitionOff(@NonNull BlackBoxModel blackBoxModel) {
        return BlackBoxResponseModel.ResponseType.IGNITION_OFF == blackBoxModel.getResponseType();
    }

    @Override
    public boolean isUpdate(@NonNull BlackBoxModel blackBoxModel) {
        return BlackBoxResponseModel.ResponseType.STATUS_UPDATE == blackBoxModel.getResponseType();
    }
}
