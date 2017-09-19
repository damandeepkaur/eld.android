package com.bsmwireless.common.utils;

import android.support.annotation.NonNull;

import com.bsmwireless.models.BlackBoxModel;

public interface BlackBoxStateChecker {

    boolean isMoving(@NonNull BlackBoxModel blackBoxModel);
    boolean isStopped(@NonNull BlackBoxModel blackBoxModel);
    boolean isIgnitionOn(@NonNull BlackBoxModel blackBoxModel);
    boolean isIgnitionOff(@NonNull BlackBoxModel blackBoxModel);
    boolean isUpdate(@NonNull BlackBoxModel blackBoxModel);

}
